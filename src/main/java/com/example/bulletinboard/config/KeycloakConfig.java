package com.example.bulletinboard.config;

import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.keycloak.admin.client.Keycloak;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Bean
    public Keycloak  keycloakAdminClient(){
        return KeycloakBuilder.builder()
                .serverUrl("http://localhost:8080") // URL Keycloak
                .realm("master")                   // Админский realm
                .clientId("admin-cli")             // Клиент для админских операций
                .username("admin")                 // Логин администратора
                .password("admin")        // Пароль администратора
                .build();
    }
}
