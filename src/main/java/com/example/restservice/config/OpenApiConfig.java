package com.example.restservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI baseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Demo API")
                        .version("1.0.0")
                        .description("Swagger UI cho Spring Boot demo")
                        .termsOfService("https://example.com/terms")  // Thêm terms nếu cần
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .name("Support")
                                .email("support@example.com"))  // Thêm contact
                        .license(new io.swagger.v3.oas.models.info.License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(Arrays.asList(
                        new Server().url("http://localhost:8080").description("Development"),
                        new Server().url("https://api.example.com").description("Production")
                ));  // Thêm servers cho các môi trường
    }

    @Bean
        public CommandLineRunner commandLineRunner(
                @Value("${springdoc.swagger-ui.path:/swagger-ui.html}") String swaggerUiPath,
                @Value("${server.port:8080}") int port  // Lấy port từ properties (mặc định 8081)
        ) {
            return args -> {
                String baseUrl = "http://localhost:" + port;
                System.out.println("Swagger UI available at: " + baseUrl + swaggerUiPath);
                // Có thể thêm log khác, ví dụ: System.out.println("OpenAPI JSON at: " + baseUrl + "/v3/api-docs");
            };
        }
}