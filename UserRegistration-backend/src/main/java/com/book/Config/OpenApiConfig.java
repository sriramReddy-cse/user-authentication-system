package com.book.Config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "srk",
                        email = "sreeramr279@gmail.com",
                        url = "https://srk.com"
                ),
                description = "Open api documentation for spring security",
                title = "Openapi Specification - srk",
                version = "1.0",
                license = @License(
                        name = "srk license",
                        url = "https://srk-license.com"
                ),
                termsOfService = "terms of service"
        ),
        servers ={
        @Server(
                description = "Local ENV",
                url = "http://localhost:8088/api/v1/"
        ),
        @Server(
                description = "Prod ENV",
                url = "https://srk.com/srkStuff"
        )
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "Jwt Auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {


}
