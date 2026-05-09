package com.hotel.management.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class DebugRuntimeLogger {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Path LOG_PATH = Path.of("c:/Users/Duong Vinh/Downloads/webquanlikhachsan-JAVA--master/debug-83b084.log");
    private static final String SESSION_ID = "83b084";
    private static final String ENDPOINT = "http://127.0.0.1:7539/ingest/370dd323-8a19-41e8-8be1-f700e26d9fe0";

    private DebugRuntimeLogger() {
    }

    public static void log(String runId,
                           String hypothesisId,
                           String location,
                           String message,
                           Map<String, Object> data) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sessionId", SESSION_ID);
        payload.put("id", "log_" + Instant.now().toEpochMilli() + "_" + UUID.randomUUID());
        payload.put("timestamp", System.currentTimeMillis());
        payload.put("runId", runId);
        payload.put("hypothesisId", hypothesisId);
        payload.put("location", location);
        payload.put("message", message);
        payload.put("data", data == null ? Map.of() : data);
        String line;
        try {
            line = OBJECT_MAPPER.writeValueAsString(payload) + System.lineSeparator();
        } catch (IOException serializationError) {
            System.err.println("DebugRuntimeLogger serialize failed: " + serializationError.getMessage());
            return;
        }

        List<Path> candidatePaths = new ArrayList<>();
        candidatePaths.add(LOG_PATH);
        candidatePaths.add(Path.of("debug-83b084.log"));
        candidatePaths.add(Path.of(System.getProperty("user.dir"), "debug-83b084.log"));

        IOException lastError = null;
        for (Path candidate : candidatePaths) {
            try {
                Files.writeString(
                    candidate,
                    line,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.APPEND
                );
                sendToEndpoint(line);
                return;
            } catch (IOException ioError) {
                lastError = ioError;
            }
        }

        if (lastError != null) {
            System.err.println("DebugRuntimeLogger write failed: " + lastError.getMessage());
        }

        sendToEndpoint(line);
    }

    private static void sendToEndpoint(String line) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) URI.create(ENDPOINT).toURL().openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("X-Debug-Session-Id", SESSION_ID);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(line.getBytes(StandardCharsets.UTF_8));
            }
            connection.getResponseCode();
        } catch (Exception endpointError) {
            System.err.println("DebugRuntimeLogger endpoint failed: " + endpointError.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
