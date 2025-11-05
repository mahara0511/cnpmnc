package com.example.restservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartupLogger {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${server.domain:localhost}")
    private String serverDomain;

    // ANSI màu cho terminal
    private static final String RESET = "\u001B[0m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";

    @Bean
    public CommandLineRunner logApiUrls() {
        return args -> {
            String backendUrl = "http://" + serverDomain + ":" + serverPort;
            String swaggerUrl = backendUrl + "/swagger-ui/index.html";

            System.out.println(CYAN + "\n╔══════════════════════════════════════════════════════╗" + RESET);
            System.out.println(YELLOW + "║  Application Started Successfully!" + RESET);
            System.out.println(CYAN + "╠══════════════════════════════════════════════════════╣" + RESET);
            System.out.println(GREEN + "║ Backend: " + backendUrl + RESET);
            System.out.println(GREEN + "║ Swagger UI: " + swaggerUrl + RESET);
            System.out.println(CYAN + "╚══════════════════════════════════════════════════════╝\n" + RESET);
        };
    }
}
