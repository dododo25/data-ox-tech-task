package com.dododo.scenarios.http.auth;

import java.io.IOException;
import java.net.HttpURLConnection;

public interface Auth {

    void accept(HttpURLConnection connection) throws IOException;

}
