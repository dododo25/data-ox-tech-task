package com.dododo.scenarios;

import com.dododo.scenarios.http.HttpClient;
import com.dododo.scenarios.http.HttpRequest;
import com.dododo.scenarios.http.HttpResponse;
import com.dododo.scenarios.http.auth.BearerTokenAuth;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class Scenario1 extends Thread {

    private final HttpClient clientClient;

    private final HttpClient adminClient;

    private final ObjectMapper mapper;

    public Scenario1(HttpClient clientClient, HttpClient adminClient) {
        this.clientClient = clientClient;
        this.adminClient = adminClient;

        this.mapper = new ObjectMapper();
    }

    @Override
    public void run() {
        try {
            System.out.println("### Scenario 1 ###");
            System.out.println("#  Description   #");
            System.out.println("The user sends multiple orders with the price of 1, all by mistake 1.");
            System.out.println("There should be only one order be processed, the rest must return an error.");
            System.out.println("#     Steps      #");

            System.out.println("remove old orders");

            HttpResponse response = adminClient.send(HttpRequest.post("/login")
                    .defaultHeaders()
                    .body("{\"name\": \"admin\", \"password\": \"adminadmin\"}"));

            if (response.getStatusCode() != 200) {
                System.out.println("failed, terminating");
                return;
            }

            String adminToken = response.getHeaders().get("Auth-Token");

            response = adminClient.send(HttpRequest.get("/order/list")
                    .defaultHeaders()
                    .auth(new BearerTokenAuth(adminToken)));

            if (response.getStatusCode() != 200) {
                System.out.println("failed, terminating");
                return;
            }

            ArrayNode array = (ArrayNode) mapper.readTree(response.getBody().toString());

            for (int i = 0; i < array.size(); i++) {
                adminClient.send(HttpRequest.delete(String.format("/order/%d/delete", array.get(i).get("id").asLong()))
                        .defaultHeaders()
                        .auth(new BearerTokenAuth(adminToken)));
            }

            System.out.println("success");
            System.out.println("trying to register as Alice");

            response = clientClient.send(HttpRequest.post("/register")
                    .defaultHeaders()
                    .body("{" +
                            "\"name\": \"Alice\"," +
                            "\"email\": \"alice@domain.com\"," +
                            "\"address\": \"test address1\"," +
                            "\"password\": \"AliceRulez\"" +
                            "}"));

            if (response.getStatusCode() != 201) {
                System.out.println("failed, trying to log in Alice");

                response = clientClient.send(HttpRequest.post("/login")
                        .defaultHeaders()
                        .body("{\"email\": \"alice@domain.com\", \"password\": \"AliceRulez\"}"));

                if (response.getStatusCode() != 200) {
                    System.out.println("failed, terminating");
                    return;
                }
            }

            ObjectNode object = (ObjectNode) mapper.readTree(response.getBody().toString());
            Long id = object.get("id").asLong();

            String clientToken = response.getHeaders().get("Auth-Token");

            System.out.printf("success, id=%d, access token=%s%n", id, clientToken);
            System.out.println("trying to create multiple (10) orders of the same price");

            response = adminClient.send(HttpRequest.post(String.format("/client/%s/enable", id))
                    .defaultHeaders()
                    .auth(new BearerTokenAuth(adminToken)));

            if (response.getStatusCode() != 200) {
                System.out.println("failed, terminating");
                return;
            }

            for (int i = 0; i < 10; i++) {
                clientClient.send(HttpRequest.post("/order/add")
                        .defaultHeaders()
                        .auth(new BearerTokenAuth(clientToken))
                        .body("{\"price\": 100}"));
            }

            response = clientClient.send(HttpRequest.get(String.format("/client/%d/order/list", id))
                    .defaultHeaders()
                    .auth(new BearerTokenAuth(clientToken)));

            array = (ArrayNode) mapper.readTree(response.getBody().toString());

            System.out.println(array.get(0).toPrettyString());
            System.out.printf("done, %s item(s) was created%n", array.size());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
