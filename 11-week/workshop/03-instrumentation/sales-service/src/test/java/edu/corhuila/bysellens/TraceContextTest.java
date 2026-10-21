package edu.corhuila.bysellens;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import tools.jackson.databind.ObjectMapper;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class TraceContextTest {
    @Test void filterPreservesTraceAndReturnsNewSpan() throws Exception {
        var request = new MockHttpServletRequest("POST", "/api/v1/sales");
        request.addHeader("traceparent", "00-91c786ab12de45ff8076aabd34cda109-a100000000000001-01");
        var response = new MockHttpServletResponse();
        new TraceFilter(new SalesMetrics()).doFilter(request, response, (req, res) -> res.getWriter().write("prototype"));
        var ctx = (TraceContext) request.getAttribute("traceContext");
        assertEquals("91c786ab12de45ff8076aabd34cda109", ctx.traceId());
        assertNotEquals("a100000000000001", ctx.spanId());
        assertEquals(ctx.header(), response.getHeader("traceparent"));
    }
    @Test void invalidOrAbsentContextGetsValidNonzeroIds() {
        for (String header : new String[]{null, "bad", "00-00000000000000000000000000000000-a100000000000001-01",
                "00-91c786ab12de45ff8076aabd34cda109-0000000000000000-01"}) {
            var ctx = TraceContext.incoming(header);
            assertTrue(ctx.header().matches("00-[0-9a-f]{32}-[0-9a-f]{16}-01"));
            assertFalse(ctx.traceId().matches("0+"));
        }
    }
    @Test void loggerWritesContractAndDropsSecrets() {
        String line = StructuredLogger.write(TraceContext.incoming(null), "INFO", "sale.request.received",
                "Sale prototype request received", 0, Map.of("password", "never-print-this", "item_count", 1));
        var log = new ObjectMapper().readTree(line);
        for (String field : new String[]{"timestamp", "level", "service", "version", "environment", "trace_id",
                "span_id", "correlation_id", "event", "message", "duration_ms"}) assertTrue(log.has(field));
        assertFalse(line.contains("never-print-this"));
    }
}
