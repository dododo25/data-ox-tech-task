package com.dododo.scenarios.http;

import com.dododo.scenarios.http.auth.Auth;
import com.dododo.scenarios.http.auth.NoAuth;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpRequest {

    final String path;

    final String method;

    final Map<String, String> headers;

    Auth auth;

    Object body;

    private HttpRequest(String path, String method) {
        this.path = path;
        this.method = method;

        this.headers = new HashMap<>();
        this.auth = new NoAuth();
    }

    public HttpRequest defaultHeaders() {
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "*/*");

        return this;
    }

    public HttpRequest body(Object body) {
        this.body = body;
        return this;
    }

    public HttpRequest auth(Auth auth) {
        this.auth = Objects.requireNonNull(auth);
        return this;
    }

    public static HttpRequest get(String path) {
        return new HttpRequest(path, "GET");
    }

    public static HttpRequest post(String path) {
        return new HttpRequest(path, "POST");
    }

    public static HttpRequest delete(String path) {
        return new HttpRequest(path, "DELETE");
    }
}
