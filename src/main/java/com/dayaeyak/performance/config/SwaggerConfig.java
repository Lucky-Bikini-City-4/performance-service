package com.dayaeyak.performance.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Dayaeyak Performance API Document")
                .version("v0.0.1")
                .description("다예약 통합 예약 시스템의 공연 서비스 API 명세서");
        return new OpenAPI()
                .info(info);
    }
}
