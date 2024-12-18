package com.pawstime.pawstime.global.config.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins("http://43.200.46.13:8080")  // 프론트엔드 주소
        .allowedMethods("GET", "POST", "PUT", "DELETE")
        .allowedHeaders("*");

    // Swagger 관련 경로 허용
    registry.addMapping("/swagger-ui/**")
        .allowedOrigins("http://43.200.46.13:8080")
        .allowedMethods("GET", "POST", "PUT", "DELETE")
        .allowedHeaders("*");

    registry.addMapping("/v3/api-docs/**")
        .allowedOrigins("http://43.200.46.13:8080")
        .allowedMethods("GET", "POST", "PUT", "DELETE")
        .allowedHeaders("*");
  }
}


