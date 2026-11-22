package com.artillexstudios.axapi.utils.http;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/** Small HTTP helper implemented with APIs available on Java 8. */
public final class Requests {
    private static final Gson GSON = new Gson();

    private Requests() {
    }

    public static final class Response {
        private final int statusCode;
        private final String body;
        private final Map<String, List<String>> headers;

        private Response(int statusCode, String body, Map<String, List<String>> headers) {
            this.statusCode = statusCode;
            this.body = body;
            this.headers = headers == null ? Collections.<String, List<String>>emptyMap() : headers;
        }

        public int statusCode() {
            return this.statusCode;
        }

        public String body() {
            return this.body;
        }

        public Map<String, List<String>> headers() {
            return this.headers;
        }
    }

    private static Response send(RequestType type, String url, Map<String, String> headers, String body) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod(type.name());
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    connection.setRequestProperty(header.getKey(), header.getValue());
                }
            }

            if (type == RequestType.POST) {
                if (body == null) {
                    throw new IllegalArgumentException("The body of a POST request can't be null!");
                }
                connection.setDoOutput(true);
                byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                connection.setFixedLengthStreamingMode(bytes.length);
                try (OutputStream output = connection.getOutputStream()) {
                    output.write(bytes);
                }
            }

            int statusCode = connection.getResponseCode();
            InputStream input = statusCode >= 400 ? connection.getErrorStream() : connection.getInputStream();
            String responseBody = read(input);
            return new Response(statusCode, responseBody, connection.getHeaderFields());
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String read(InputStream input) throws IOException {
        if (input == null) {
            return "";
        }
        try (InputStream stream = input; ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int count;
            while ((count = stream.read(buffer)) != -1) {
                output.write(buffer, 0, count);
            }
            return new String(output.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    private static String transform(Supplier<JsonObject> bodySupplier) {
        if (bodySupplier == null) {
            throw new IllegalArgumentException("The body supplier of a POST request can't be null!");
        }
        JsonObject body = bodySupplier.get();
        if (body == null) {
            throw new IllegalArgumentException("The body of a POST request can't be null!");
        }
        return GSON.toJson(body);
    }

    public static Response sendTyped(RequestType type, String url, Map<String, String> headers, Supplier<JsonObject> body) {
        return send(type, url, headers, type == RequestType.POST ? transform(body) : null);
    }

    public static CompletableFuture<Response> sendTypedAsync(final RequestType type, final String url, final Map<String, String> headers, final Supplier<String> body) {
        return CompletableFuture.supplyAsync(new Supplier<Response>() {
            @Override
            public Response get() {
                String requestBody = null;
                if (type == RequestType.POST) {
                    if (body == null) {
                        throw new IllegalArgumentException("The body supplier of a POST request can't be null!");
                    }
                    requestBody = body.get();
                }
                return send(type, url, headers, requestBody);
            }
        });
    }

    public static Response post(String url, Map<String, String> headers, Supplier<JsonObject> body) {
        return sendTyped(RequestType.POST, url, headers, body);
    }

    public static Response delete(String url, Map<String, String> headers) {
        return sendTyped(RequestType.DELETE, url, headers, null);
    }

    public static Response get(String url, Map<String, String> headers) {
        return sendTyped(RequestType.GET, url, headers, null);
    }

    public static CompletableFuture<Response> postAsync(String url, Map<String, String> headers, Supplier<JsonObject> body) {
        return sendTypedAsync(RequestType.POST, url, headers, new Supplier<String>() {
            @Override
            public String get() {
                return transform(body);
            }
        });
    }

    public static CompletableFuture<Response> postAsyncString(String url, Map<String, String> headers, Supplier<String> body) {
        return sendTypedAsync(RequestType.POST, url, headers, body);
    }

    public static CompletableFuture<Response> deleteAsync(String url, Map<String, String> headers) {
        return sendTypedAsync(RequestType.DELETE, url, headers, null);
    }

    public static CompletableFuture<Response> getAsync(String url, Map<String, String> headers) {
        return sendTypedAsync(RequestType.GET, url, headers, null);
    }
}
