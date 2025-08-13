package com.dododo.scenarios.http;

import java.util.List;
import java.util.Map;

public class HttpResponse {

    private final int statusCode;

    private final Map<String, String> headers;

    private final Object body;

    HttpResponse(int statusCode, Map<String, String> headers, Object body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Object getBody() {
        return body;
    }
}
