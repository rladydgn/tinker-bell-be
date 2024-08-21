package com.example.tinkerbell.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info=@Info(
                title = "tinkerbell API docs",
                version = "0.1"
        )
)
@Configuration
public class SwaggerConfig {
        private final static String JWT = "JWT";

        @Bean
        public OpenAPI openAPI() {
                SecurityRequirement securityRequirement = new SecurityRequirement().addList(JWT);
                Components components = new Components()
                        .addSecuritySchemes(JWT, new SecurityScheme()
                                .name(JWT)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("Bearer")
                                .bearerFormat(JWT));

                return new OpenAPI()
                        .addSecurityItem(securityRequirement)
                        .components(components);
        }
}
