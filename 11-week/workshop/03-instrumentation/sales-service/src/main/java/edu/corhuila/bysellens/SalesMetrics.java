package edu.corhuila.bysellens;

import org.springframework.stereotype.Component;

@Component
public class SalesMetrics {
    private final double[] bounds = {0.05, 0.1, 0.25, 0.5, 1, 3, 5, 10};
    private final long[] buckets = new long[bounds.length];
    private final long[] outcomes = new long[3];
    private long count;
    private double sum;
    public synchronized void observe(int status, double seconds) {
        outcomes[status >= 500 ? 2 : status >= 400 ? 1 : 0]++;
        count++; sum += seconds;
        for (int i = 0; i < bounds.length; i++) if (seconds <= bounds[i]) buckets[i]++;
    }
    public synchronized String prometheus() {
        String labels = "route=\"/api/v1/sales\"";
        StringBuilder out = new StringBuilder("# HELP sales_requests_total Completed prototype requests by outcome.\n");
        out.append("# TYPE sales_requests_total counter\n");
        String[] names = {"success", "rejected", "error"};
        for (int i = 0; i < names.length; i++) out.append("sales_requests_total{").append(labels)
                .append(",outcome=\"").append(names[i]).append("\"} ").append(outcomes[i]).append('\n');
        out.append("# HELP sales_request_duration_seconds Prototype request latency in seconds.\n")
                .append("# TYPE sales_request_duration_seconds histogram\n");
        for (int i = 0; i < bounds.length; i++) out.append("sales_request_duration_seconds_bucket{")
                .append(labels).append(",le=\"").append(bounds[i]).append("\"} ").append(buckets[i]).append('\n');
        out.append("sales_request_duration_seconds_bucket{").append(labels).append(",le=\"+Inf\"} ")
                .append(count).append('\n');
        out.append("sales_request_duration_seconds_count{").append(labels).append("} ").append(count).append('\n');
        out.append("sales_request_duration_seconds_sum{").append(labels).append("} ").append(sum).append('\n');
        return out.toString();
    }
}
