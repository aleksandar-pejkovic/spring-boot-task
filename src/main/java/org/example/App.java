package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
class App {

    protected App() {
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        log.info("Rest task application is up and running");
    }
}
