package com.codequest.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "CodeQuest API",
                version = "1.0.0",
                description = "REST API for CodeQuest game application",
                contact = @Contact(
                        name = "CodeQuest Team",
                        url = "https://codequest.example.com"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080/api/v1",
                        description = "Local Development"
                ),
                @Server(
                        url = "https://api.codequest.example.com/api/v1",
                        description = "Production"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Firebase ID Token in Bearer format"
)
public class OpenApiConfig {
}

