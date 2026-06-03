package br.com.fiap.jornadaterra.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🌾 Jornada da Terra API")
                        .description("API REST para gerenciamento gamificado de fazendas com monitoramento satelital")
                        .version("1.0.0"));
    }
}