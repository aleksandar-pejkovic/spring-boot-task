package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
class App {

    protected App() {
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
