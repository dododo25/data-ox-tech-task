package com.dododo.scenarios;

import com.dododo.scenarios.http.HttpClient;
import com.dododo.scenarios.http.HttpRequest;
import com.dododo.scenarios.http.HttpResponse;
import com.dododo.scenarios.http.auth.BearerTokenAuth;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class Scenario3 extends Thread {

    private final HttpClient clientClient;

    private final HttpClient adminClient;

    private final ObjectMapper mapper;

    public Scenario3(HttpClient clientClient, HttpClient adminClient) {
        this.clientClient = clientClient;
        this.adminClient = adminClient;

        this.mapper = new ObjectMapper();
    }

    @Override
    public void run() {
        Semaphore semaphore = new Semaphore(0);

        AtomicLong clientId = new AtomicLong();
        AtomicReference<String> adminToken = new AtomicReference<>();

        Thread disableBySignalThread = new Thread(() -> {
            try {
                semaphore.acquire();

                System.out.printf("trying to disable a client with id %d%n", clientId.get());

                HttpResponse response = adminClient.send(HttpRequest.post(String.format("/client/%s/disable", clientId.get()))
                        .defaultHeaders()
                        .auth(new BearerTokenAuth(adminToken.get())));

                if (response.getStatusCode() == 200) {
                    System.out.printf("client with id %d was successfully disabled%n", clientId.get());
                }
            } catch (IOException | InterruptedException e) {
                System.err.println(e.getMessage());
            }
        });
        disableBySignalThread.start();

        System.out.println("### Scenario 3 ###");
        System.out.println("#  Description   #");
        System.out.println("The user sends multiple different orders, and at the same time this client becomes disabled (through API).");
        System.out.println("Only those orders that were sent before this change should be added.");
        System.out.println("#     Steps      #");

        try {
            System.out.println("remove old orders");

            HttpResponse response = adminClient.send(HttpRequest.post("/login")
                    .defaultHeaders()
                    .body("{\"name\": \"admin\", \"password\": \"adminadmin\"}"));

            if (response.getStatusCode() != 200) {
                System.out.println("failed, terminating");
                return;
            }

            adminToken.set(response.getHeaders().get("Auth-Token"));

            response = adminClient.send(HttpRequest.get("/order/list")
                    .defaultHeaders()
                    .auth(new BearerTokenAuth(adminToken.get())));

            if (response.getStatusCode() != 200) {
                System.out.println("failed, terminating");
                return;
            }

            ArrayNode array = (ArrayNode) mapper.readTree(response.getBody().toString());

            for (int i = 0; i < array.size(); i++) {
                adminClient.send(HttpRequest.delete(String.format("/order/%d/delete", array.get(i).get("id").asLong()))
                        .defaultHeaders()
                        .auth(new BearerTokenAuth(adminToken.get())));
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
            clientId.set(object.get("id").asLong());

            String clientToken = response.getHeaders().get("Auth-Token");

            System.out.printf("success, id=%d, access token=%s%n", clientId.get(), adminToken);
            System.out.println("trying to create multiple (3) orders");

            response = adminClient.send(HttpRequest.post(String.format("/client/%s/enable", clientId.get()))
                    .defaultHeaders()
                    .auth(new BearerTokenAuth(adminToken.get())));

            if (response.getStatusCode() != 200) {
                System.out.println("failed, terminating");
                semaphore.release();
                return;
            }

            for (int i = 0; i < 3; i++) {
                response = clientClient.send(HttpRequest.post("/order/add")
                        .defaultHeaders()
                        .auth(new BearerTokenAuth(clientToken))
                        .body("{\"price\": 100}"));

                if (response.getStatusCode() == 201) {
                    System.out.println("order created");
                }

                if (i == 0) {
                    semaphore.release();
                }

                Thread.sleep(15000);
            }

            response = clientClient.send(HttpRequest.get(String.format("/client/%d/order/list", clientId.get()))
                    .defaultHeaders()
                    .auth(new BearerTokenAuth(clientToken)));

            array = (ArrayNode) mapper.readTree(response.getBody().toString());

            System.out.printf("done, %s item(s) was created%n", array.size());
        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }
}
