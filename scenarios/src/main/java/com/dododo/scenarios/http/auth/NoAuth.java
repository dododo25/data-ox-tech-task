package com.dododo.scenarios.http.auth;

import java.net.HttpURLConnection;

public class NoAuth implements Auth {

    @Override
    public void accept(HttpURLConnection connection) {
        // no action required
    }
}
