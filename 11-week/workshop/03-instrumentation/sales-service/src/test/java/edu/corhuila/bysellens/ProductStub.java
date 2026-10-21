package edu.corhuila.bysellens;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/** Local demonstration fixture, not the Product service implementation. */
public class ProductStub {
    public static void main(String[] args) throws Exception {
        var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 18082), 0);
        server.createContext("/api/v1/products/550e8400-e29b-41d4-a716-446655440000/stock", exchange -> {
            byte[] body = "{\"quantity\":15}".getBytes(StandardCharsets.UTF_8);
            System.out.println("PRODUCT_STUB traceparent=" + exchange.getRequestHeaders().getFirst("traceparent"));
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            try (var output = exchange.getResponseBody()) { output.write(body); }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop(0)));
        server.start();
        System.out.println("Product test double listening on 127.0.0.1:18082; quantity=15");
    }
}
