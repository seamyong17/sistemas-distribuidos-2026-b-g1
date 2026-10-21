package edu.corhuila.bysellens;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

@RestController
public class SalesProbe {
    public record Item(UUID productId, int quantity, BigDecimal unitPrice) { }
    public record Input(UUID customerId, String paymentMethod, List<Item> items) { }
    private final URI product;
    private final Duration timeout;
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(1)).build();
    private final SalesMetrics metrics;
    public SalesProbe(@Value("${product.base-url:http://127.0.0.1:18082}") String base,
                      @Value("${product.timeout-ms:3000}") long timeoutMs, SalesMetrics metrics) {
        product = URI.create(base);
        if (!"http".equals(product.getScheme()) || !Set.of("localhost", "127.0.0.1").contains(product.getHost())
                || product.getUserInfo() != null || product.getQuery() != null || product.getFragment() != null)
            throw new IllegalArgumentException("The prototype Product destination must be loopback HTTP");
        timeout = Duration.ofMillis(timeoutMs);
        this.metrics = metrics;
    }
    @GetMapping(value = "/metrics", produces = "text/plain; version=0.0.4")
    public String metrics() { return metrics.prometheus(); }

    @PostMapping("/api/v1/sales")
    public ResponseEntity<?> probe(@RequestBody Input input, HttpServletRequest request) {
        TraceContext ctx = (TraceContext) request.getAttribute("traceContext");
        if (input.customerId() == null || input.items() == null || input.items().size() != 1
                || input.paymentMethod() == null
                || !Set.of("CASH", "NEQUI", "BANK_TRANSFER", "OTHER").contains(input.paymentMethod()))
            return ResponseEntity.badRequest().body(Map.of("prototype_only", true, "error", "INVALID_PROBE_INPUT"));
        Item item = input.items().getFirst();
        if (item == null || item.productId() == null || item.quantity() <= 0 || item.unitPrice() == null
                || item.unitPrice().signum() <= 0)
            return ResponseEntity.badRequest().body(Map.of("prototype_only", true, "error", "INVALID_PROBE_ITEM"));
        TraceContext child = ctx.child();
        long start = System.nanoTime();
        try {
            var outgoing = HttpRequest.newBuilder(product.resolve("/api/v1/products/" + item.productId() + "/stock"))
                    .timeout(timeout).header("traceparent", child.header()).GET().build();
            var response = client.send(outgoing, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return unavailable(child, start, "STOCK_LOOKUP_FAILED");
            var stock = new ObjectMapper().readTree(response.body());
            if (!stock.path("quantity").isIntegralNumber() || !stock.path("quantity").canConvertToInt()
                    || stock.path("quantity").asInt() < 0)
                return unavailable(child, start, "INVALID_STOCK_RESPONSE");
            if (item.quantity() > stock.path("quantity").asInt()) {
                StructuredLogger.write(child, "WARN", "sale.stock.rejected", "Requested quantity exceeds available stock",
                        elapsed(start), Map.of("rule", "INSUFFICIENT_STOCK", "product_id", item.productId().toString(),
                                "quantity", item.quantity(), "downstream_service", "product-service"));
                return ResponseEntity.unprocessableContent().body(Map.of("prototype_only", true, "error", "INSUFFICIENT_STOCK"));
            }
            StructuredLogger.write(child, "INFO", "sale.stock.validated", "Prototype stock validation completed",
                    elapsed(start), Map.of("downstream_service", "product-service", "product_id", item.productId().toString()));
            return ResponseEntity.ok(Map.of("prototype_only", true, "stock_validation", "passed", "sale_persisted", false));
        } catch (HttpTimeoutException e) {
            return unavailable(child, start, "STOCK_LOOKUP_TIMEOUT");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return unavailable(child, start, "STOCK_LOOKUP_INTERRUPTED");
        } catch (java.io.IOException | tools.jackson.core.JacksonException e) {
            return unavailable(child, start, "STOCK_LOOKUP_FAILED");
        }
    }
    private static double elapsed(long start) { return (System.nanoTime() - start) / 1_000_000.0; }
    private ResponseEntity<?> unavailable(TraceContext ctx, long start, String code) {
        StructuredLogger.write(ctx, "ERROR", code.equals("STOCK_LOOKUP_TIMEOUT") ? "sale.stock.timeout" : "sale.stock.failed",
                "Required prototype stock lookup unavailable", elapsed(start),
                Map.of("downstream_service", "product-service", "error_code", code));
        return ResponseEntity.status(503).body(Map.of("prototype_only", true, "error", code));
    }
}
