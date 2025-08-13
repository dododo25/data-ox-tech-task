package com.dododo.scenarios.http.auth;

import java.io.IOException;
import java.net.HttpURLConnection;

public class NoAuth implements Auth {

    @Override
    public void accept(HttpURLConnection connection) throws IOException {
        // no action required
    }
}
