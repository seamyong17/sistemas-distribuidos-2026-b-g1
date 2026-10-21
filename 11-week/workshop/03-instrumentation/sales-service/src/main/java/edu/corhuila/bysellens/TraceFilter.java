package edu.corhuila.bysellens;

import java.io.IOException;
import java.util.Map;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TraceFilter extends OncePerRequestFilter {
    private final SalesMetrics metrics;
    public TraceFilter(SalesMetrics metrics) { this.metrics = metrics; }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().equals("/api/v1/sales");
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        TraceContext ctx = TraceContext.incoming(request.getHeader("traceparent"));
        request.setAttribute("traceContext", ctx);
        response.setHeader("traceparent", ctx.header());
        long start = System.nanoTime();
        StructuredLogger.write(ctx, "INFO", "sale.request.received", "Sale prototype request received", 0,
                Map.of("route", "/api/v1/sales", "method", request.getMethod()));
        try {
            chain.doFilter(request, response);
        } catch (IOException | ServletException | RuntimeException e) {
            response.setStatus(500);
            throw e;
        } finally {
            metrics.observe(response.getStatus(), (System.nanoTime() - start) / 1_000_000_000.0);
            StructuredLogger.write(ctx, response.getStatus() >= 500 ? "ERROR" : "INFO",
                    "sale.request.completed", "Sale prototype request completed",
                    (System.nanoTime() - start) / 1_000_000.0,
                    Map.of("route", "/api/v1/sales", "method", request.getMethod(), "http_status", response.getStatus()));
        }
    }
}
