package edu.corhuila.bysellens;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.UUID;

public record TraceContext(String traceId, String spanId, String flags, String correlationId) {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static String id(int bytes) {
        byte[] value = new byte[bytes];
        do { RANDOM.nextBytes(value); } while (HexFormat.of().formatHex(value).matches("0+"));
        return HexFormat.of().formatHex(value);
    }
    public static TraceContext incoming(String header) {
        boolean valid = header != null && header.matches("00-[0-9a-f]{32}-[0-9a-f]{16}-[0-9a-f]{2}");
        String[] parts = valid ? header.split("-") : new String[0];
        valid = valid && !parts[1].matches("0+") && !parts[2].matches("0+");
        return new TraceContext(valid ? parts[1] : id(16), id(8),
                valid ? parts[3] : "01", UUID.randomUUID().toString());
    }
    public TraceContext child() { return new TraceContext(traceId, id(8), flags, correlationId); }
    public String header() { return "00-" + traceId + "-" + spanId + "-" + flags; }
}
