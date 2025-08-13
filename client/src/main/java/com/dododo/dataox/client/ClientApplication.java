package com.dododo.dataox.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {
        "com.dododo.dataox.core.repository"
})
@EntityScan(basePackages = {
        "com.dododo.dataox.core.model"
})
@ComponentScan(basePackages = {
        "com.dododo.dataox.core",
        "com.dododo.dataox.client"
})
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }
}
