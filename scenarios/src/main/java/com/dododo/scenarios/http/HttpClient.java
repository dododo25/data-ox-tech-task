package com.dododo.scenarios.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpClient {

    private final String baseUrl;

    public HttpClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public HttpResponse send(HttpRequest request) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(baseUrl + request.path).openConnection();

        conn.setRequestMethod(request.method);

        for (Map.Entry<String, String> entry : request.headers.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }

        request.auth.accept(conn);

        if (request.body != null) {
            conn.setDoOutput(true);

            OutputStream out = conn.getOutputStream();

            out.write(request.body.toString().getBytes());
            out.flush();
        }

        try {
            InputStream inputStream = conn.getInputStream();

            StringBuilder result = new StringBuilder();

            while (true) {
                byte b = (byte) inputStream.read();

                if (b == -1) {
                    break;
                }

                result.append((char) b);
            }

            Map<String, String> headers = conn.getHeaderFields()
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getKey() != null)
                    .peek(entry -> {
                        if (entry.getValue().isEmpty()) {
                            entry.getValue().add(null);
                        }
                    })
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().get(0)));

            return new HttpResponse(conn.getResponseCode(), headers, result.toString());
        } catch (IOException e) {
            return new HttpResponse(404, Collections.emptyMap(), null);
        }
    }
}
