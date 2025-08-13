package com.dododo.scenarios.http.auth;

import java.net.HttpURLConnection;

public interface Auth {

    void accept(HttpURLConnection connection);

}
