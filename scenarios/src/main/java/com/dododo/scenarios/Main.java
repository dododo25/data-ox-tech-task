package com.dododo.scenarios;

import com.dododo.scenarios.http.HttpClient;

import java.util.Objects;

public class Main {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("java -jar scenarios.jar --scenario [number]");
            return;
        }

        if (!Objects.equals(args[0], "--scenario")) {
            System.out.println("java -jar scenarios.jar --scenario [number]");
            return;
        }

        if (!args[1].matches("\\d+")) {
            System.out.println("java -jar scenarios.jar --scenario [number]");
            return;
        }

        HttpClient clientClient = new HttpClient("http://localhost:8081");
        HttpClient adminClient = new HttpClient("http://localhost:8082");

        Thread scenarioThread;

        switch (args[1]) {
            case "1":
                scenarioThread = new Scenario1(clientClient, adminClient);
                break;
            case "2":
                scenarioThread = new Scenario2(clientClient, adminClient);
                break;
            case "3":
                scenarioThread = new Scenario3(clientClient, adminClient);
                break;
            default:
                System.out.println("java -jar scenarios.jar --scenario [number]");
                return;
        }

        scenarioThread.start();
    }
}
