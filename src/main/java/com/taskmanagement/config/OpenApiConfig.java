package com.taskmanagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Online Task Management System API")
                        .version("1.0.0")
                        .description("RESTful API documentation for the Online Task Management System built with Spring Boot, JPA, and SQL. " +
                                "Supports complete task CRUD, status transitions, dynamic filtering, user assignments, categories, and tracking statistics.")
                        .contact(new Contact()
                                .name("Development Team")
                                .email("dev@taskmanagement.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")));
    }
}
