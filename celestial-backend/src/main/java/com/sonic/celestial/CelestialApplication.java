package com.sonic.celestial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CelestialApplication {
    public static void main(String[] args) {
        SpringApplication.run(CelestialApplication.class, args);
    }
}
