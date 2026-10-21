package edu.corhuila.bysellens;

import com.sun.net.httpserver.HttpServer;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.*;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.server.servlet.context.ServletWebServerApplicationContext;
import static org.junit.jupiter.api.Assertions.*;

class PropagationTest {
    static HttpServer product;
    static ExecutorService executor;
    static ServletWebServerApplicationContext app;
    static final AtomicReference<String> mode = new AtomicReference<>("ok");
    static final AtomicReference<String> sent = new AtomicReference<>();
    static final AtomicReference<String> auth = new AtomicReference<>();
    static String origin;
    static final HttpClient CLIENT = HttpClient.newHttpClient();
    static final String TRACE = "00-91c786ab12de45ff8076aabd34cda109-a100000000000001-01";
    static final String BODY = """
        {"customerId":"750e8400-e29b-41d4-a716-446655440000","paymentMethod":"CASH",
         "items":[{"productId":"550e8400-e29b-41d4-a716-446655440000","quantity":2,"unitPrice":35000}]}
        """;
    @BeforeAll static void start() throws Exception {
        product = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        executor = Executors.newVirtualThreadPerTaskExecutor();
        product.setExecutor(executor);
        product.createContext("/api/v1/products/550e8400-e29b-41d4-a716-446655440000/stock", exchange -> {
            sent.set(exchange.getRequestHeaders().getFirst("traceparent"));
            auth.set(exchange.getRequestHeaders().getFirst("Authorization"));
            String selected = mode.get();
            if (selected.equals("slow")) try { Thread.sleep(900); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            String body = selected.equals("invalid") ? "{\"quantity\":\"unknown\"}"
                    : "{\"quantity\":" + (selected.equals("insufficient") ? 1 : 15) + "}";
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            try { exchange.sendResponseHeaders(selected.equals("failure") ? 503 : 200, bytes.length);
                exchange.getResponseBody().write(bytes);
            } finally { exchange.close(); }
        });
        product.start();
        app = (ServletWebServerApplicationContext) new SpringApplicationBuilder(SalesApplication.class)
                .run("--server.port=0", "--product.timeout-ms=500", "--product.base-url=http://127.0.0.1:" + product.getAddress().getPort());
        origin = "http://127.0.0.1:" + app.getWebServer().getPort();
    }
    @AfterAll static void stop() {
        if (app != null) app.close();
        if (product != null) product.stop(0);
        if (executor != null) executor.shutdownNow();
    }
    @BeforeEach void reset() { mode.set("ok"); sent.set(null); auth.set(null); }
    static HttpResponse<String> post(String trace, String body) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create(origin + "/api/v1/sales"))
                .timeout(java.time.Duration.ofSeconds(5)).header("Content-Type", "application/json")
                .header("Authorization", "Bearer test-secret-not-for-downstream");
        if (trace != null) builder.header("traceparent", trace);
        return CLIENT.send(builder.POST(HttpRequest.BodyPublishers.ofString(body)).build(), HttpResponse.BodyHandlers.ofString());
    }
    static String scrape() throws Exception {
        return CLIENT.send(HttpRequest.newBuilder(URI.create(origin + "/metrics")).GET().build(),
                HttpResponse.BodyHandlers.ofString()).body();
    }
    @Test void receivedTraceIsSentDownstreamWithAChildSpan() throws Exception {
        var response = post(TRACE, BODY);
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"sale_persisted\":false"));
        String returned = response.headers().firstValue("traceparent").orElseThrow();
        assertEquals(TRACE.split("-")[1], sent.get().split("-")[1]);
        assertEquals(TRACE.split("-")[1], returned.split("-")[1]);
        assertNotEquals(returned.split("-")[2], sent.get().split("-")[2]);
        assertNotEquals(TRACE.split("-")[2], returned.split("-")[2]);
        assertNull(auth.get());
        System.out.println("PROPAGATION incoming=" + TRACE + " returned=" + returned + " downstream=" + sent.get());
    }
    @Test void missingContextIsGeneratedAndPropagated() throws Exception {
        var response = post(null, BODY);
        assertEquals(200, response.statusCode());
        assertEquals(response.headers().firstValue("traceparent").orElseThrow().split("-")[1], sent.get().split("-")[1]);
    }
    @Test void insufficientStockRejectsTheProbe() throws Exception {
        mode.set("insufficient");
        var response = post(TRACE, BODY);
        assertEquals(422, response.statusCode());
        assertTrue(response.body().contains("INSUFFICIENT_STOCK"));
    }
    @Test void timeoutIsReportedWithoutCreatingASale() throws Exception {
        mode.set("slow");
        var response = post(TRACE, BODY);
        assertEquals(503, response.statusCode());
        assertTrue(response.body().contains("STOCK_LOOKUP_TIMEOUT"));
    }
    @Test void malformedStockAndDownstreamFailureAreUnavailable() throws Exception {
        mode.set("invalid"); assertEquals(503, post(TRACE, BODY).statusCode());
        mode.set("failure"); assertEquals(503, post(TRACE, BODY).statusCode());
    }
    @Test void invalidItemMakesNoDownstreamCall() throws Exception {
        assertEquals(400, post(TRACE, BODY.replace("\"quantity\":2", "\"quantity\":0")).statusCode());
        assertNull(sent.get());
    }
    @Test void metricsCountRequestsAndExcludeScrapes() throws Exception {
        String before = scrape(); assertEquals(before, scrape());
        post(TRACE, BODY);
        String after = scrape();
        assertTrue(after.contains("# TYPE sales_requests_total counter"));
        assertTrue(after.contains("# TYPE sales_request_duration_seconds histogram"));
        String prefix = "sales_request_duration_seconds_count{route=\"/api/v1/sales\"} ";
        double previous = Double.parseDouble(before.lines().filter(l -> l.startsWith(prefix)).findFirst().orElseThrow().substring(prefix.length()));
        double current = Double.parseDouble(after.lines().filter(l -> l.startsWith(prefix)).findFirst().orElseThrow().substring(prefix.length()));
        assertEquals(previous + 1, current);
        String infinity = "sales_request_duration_seconds_bucket{route=\"/api/v1/sales\",le=\"+Inf\"} ";
        assertEquals(current, Double.parseDouble(after.lines().filter(l -> l.startsWith(infinity)).findFirst().orElseThrow().substring(infinity.length())));
        System.out.println("METRICS\n" + after);
    }
}
