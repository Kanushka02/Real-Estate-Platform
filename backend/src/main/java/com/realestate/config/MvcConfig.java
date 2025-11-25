package com.realestate.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Maps the URL path /uploads/** to the uploads folder in the backend directory
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:D:/CV Project/realestate/backend/uploads/");
    }
}