package edu.corhuila.bysellens;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import tools.jackson.databind.ObjectMapper;

public final class StructuredLogger {
    private static final ObjectMapper JSON = new ObjectMapper();
    private StructuredLogger() { }
    public static String write(TraceContext ctx, String level, String event,
                               String message, double durationMs, Map<String, Object> fields) {
        var log = new LinkedHashMap<String, Object>();
        log.put("timestamp", Instant.now().toString());
        log.put("level", level); log.put("service", "sales-service");
        log.put("version", "0.0.1"); log.put("environment", "workshop");
        log.put("trace_id", ctx.traceId()); log.put("span_id", ctx.spanId());
        log.put("correlation_id", ctx.correlationId()); log.put("event", event);
        log.put("message", message); log.put("duration_ms", durationMs);
        var allowed = java.util.Set.of("route", "method", "http_status", "error_code",
                "downstream_service", "product_id", "quantity", "item_count", "rule");
        fields.forEach((key, value) -> { if (allowed.contains(key)) log.put(key, value); });
        String line = JSON.writeValueAsString(log);
        System.out.println(line);
        return line;
    }
}
