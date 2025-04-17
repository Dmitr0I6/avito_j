    package com.example.bulletinboard.config;

    import lombok.extern.slf4j.Slf4j;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.core.convert.converter.Converter;
    import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
    import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.authority.SimpleGrantedAuthority;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.oauth2.jwt.JwtDecoder;
    import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
    import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.web.client.RestTemplate;

    import java.util.*;
    import java.util.stream.Collectors;
    import java.util.stream.Stream;
    @Slf4j
    @Configuration
    @EnableWebSecurity
    public class SecurityConfig {

        @Value("${keycloak.resource}")
        private String resourceClientId;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(authorize -> authorize
                            .requestMatchers(
                                    "/v3/api-docs/**",
                                    "/swagger-ui/**",
                                    "/swagger-ui.html",
                                    "/api/user/login",
                                    "/api/user/register",
                                    "/api/advertisement/page")
                                    .permitAll()
                            .requestMatchers("/v1/admin/**").hasRole("ADMIN")
                            .anyRequest().authenticated()
                    )
                    .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {jwt.jwtAuthenticationConverter(jwtAuthenticationConverter());
                    }))
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

            return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            // Используем bcrypt с силой 12 (рекомендуется)
            return new BCryptPasswordEncoder(12);
        }

        @Bean
        public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
            DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
            expressionHandler.setDefaultRolePrefix(""); // empty string for no prefix
            // Or set your custom prefix:
            // expressionHandler.setDefaultRolePrefix("CUSTOM_");
            return expressionHandler;
        }
        @Bean
        public JwtDecoder jwtDecoder() {
            return NimbusJwtDecoder.withJwkSetUri("http://localhost:8080/realms/bulletin-board/protocol/openid-connect/certs")
                    .build();
        }

        interface AuthoritiesConverter extends Converter<Map<String, Object>, Collection<GrantedAuthority>> {
        }

        @Bean
        AuthoritiesConverter resourceAccessRolesAuthoritiesConverter() {
            return claims -> {
                try {
                    // 1. Извлекаем client roles (resource_access)
                    Map<String, Object> resourceAccess = claims.get("resource_access") instanceof Map ?
                            (Map<String, Object>) claims.get("resource_access") : Collections.emptyMap();

                    Map<String, Object> resource = resourceAccess.get(resourceClientId) instanceof Map ?
                            (Map<String, Object>) resourceAccess.get(resourceClientId) : Collections.emptyMap();

                    if (resource.get("roles") instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<String> clientRoles = (List<String>) resource.get("roles");

                        // 2. Преобразуем в GrantedAuthority
                        return clientRoles.stream()
                                .filter(role -> role != null && !role.isBlank())
                                .map(role -> "ROLE_" + role.toUpperCase()) // добавляем префикс ROLE_
                                .distinct()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());
                    }
                } catch (Exception e) {
                    log.warn("Failed to extract client roles from JWT", e);
                }
                return Collections.emptyList(); // возвращаем пустой список в случае ошибки
            };
        }
//        @Bean
//        public AuthoritiesConverter resourceAccessRolesAuthoritiesConverter() {
//            return claims -> {
//                var resourceAccess = (Map<String, Object>) claims.get("resource_access");
//                var resourceAccessObject = (Map<String, Object>) resourceAccess.get(resourceClientId);
//                var resourceRoles = (List<String>) resourceAccessObject.get("roles");
//
//                return resourceRoles.stream()
//                        .map(SimpleGrantedAuthority::new)
//                        .map(GrantedAuthority.class::cast)
//                        .toList();
//            };
//        }

//        @Bean
//        public JwtAuthenticationConverter authenticationConverter(
//                Converter<Map<String, Object>, Collection<GrantedAuthority>> authoritiesConverter) {
//            var authenticationConverter = new JwtAuthenticationConverter();
//            authenticationConverter.setJwtGrantedAuthoritiesConverter(jwt ->
//                    authoritiesConverter.convert(jwt.getClaims()));
//            return authenticationConverter;
//        }

        @Bean
        public JwtAuthenticationConverter jwtAuthenticationConverter() {
            JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
            converter.setJwtGrantedAuthoritiesConverter(jwt -> {
                Map<String, Object> claims = jwt.getClaims();
                List<String> allRoles = new ArrayList<>();

                // 1. Извлекаем client roles (resource_access)
                try {
                    Map<String, Object> resourceAccess = claims.get("resource_access") instanceof Map ?
                            (Map<String, Object>) claims.get("resource_access") : Collections.emptyMap();

                    Map<String, Object> resource = resourceAccess.get(resourceClientId) instanceof Map ?
                            (Map<String, Object>) resourceAccess.get(resourceClientId) : Collections.emptyMap();

                    if (resource.get("roles") instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<String> clientRoles = (List<String>) resource.get("roles");
                        allRoles.addAll(clientRoles);
                    }
                } catch (Exception e) {
                    log.warn("Failed to extract client roles from JWT", e);
                }

                // 2. Извлекаем realm roles (realm_access)
                try {
                    Map<String, Object> realmAccess = claims.get("realm_access") instanceof Map ?
                            (Map<String, Object>) claims.get("realm_access") : Collections.emptyMap();

                    if (realmAccess.get("roles") instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<String> realmRoles = (List<String>) realmAccess.get("roles");
                        allRoles.addAll(realmRoles);
                    }
                } catch (Exception e) {
                    log.warn("Failed to extract realm roles from JWT", e);
                }

                // 3. Преобразуем в GrantedAuthority
                return allRoles.stream()
                        .filter(role -> role != null && !role.isBlank())
                        .map(role -> "ROLE_" + role.toUpperCase())
                        .distinct()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
            });
            return converter;
        }


        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }