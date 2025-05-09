package com.junior;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
public class ModuleApiApplication {

    public static void main(String[] args) {
//        System.setProperty("spring.config.name", "application-api,application-domain,application-common");
        SpringApplication.run(ModuleApiApplication.class, args);
    }
}
