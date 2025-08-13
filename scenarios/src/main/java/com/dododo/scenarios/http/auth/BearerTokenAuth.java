package com.dododo.scenarios.http.auth;

import java.net.HttpURLConnection;

public class BearerTokenAuth implements Auth {

    private final String token;

    public BearerTokenAuth(String token) {
        this.token = token;
    }

    @Override
    public void accept(HttpURLConnection connection) {
        connection.setRequestProperty("Authorization", "Bearer " + token);
    }
}
