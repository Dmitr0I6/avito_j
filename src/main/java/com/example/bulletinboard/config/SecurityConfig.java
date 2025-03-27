//package com.example.bulletinboard.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
//import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
//import org.springframework.security.web.SecurityFilterChain;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.Map;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Value("${keycloak.resource}")
//    private String resourceClientId;
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
//                        .requestMatchers("/v1/advertisements/**").authenticated()
//                        .anyRequest().authenticated()
//                )
//                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {
//                }))
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//        return http.build();
//    }
//
//    @Bean
//    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
//        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
//        expressionHandler.setDefaultRolePrefix(""); // empty string for no prefix
//        // Or set your custom prefix:
//        // expressionHandler.setDefaultRolePrefix("CUSTOM_");
//        return expressionHandler;
//    }
//
//    interface AuthoritiesConverter extends Converter<Map<String, Object>, Collection<GrantedAuthority>> {
//    }
//
//    @Bean
//    public AuthoritiesConverter resourceAccessRolesAuthoritiesConverter() {
//        return claims -> {
//            var resourceAccess = (Map<String, Object>) claims.get("resource_access");
//            var resourceAccessObject = (Map<String, Object>) resourceAccess.get(resourceClientId);
//            var resourceRoles = (List<String>) resourceAccessObject.get("roles");
//
//            return resourceRoles.stream()
//                    .map(SimpleGrantedAuthority::new)
//                    .map(GrantedAuthority.class::cast)
//                    .toList();
//        };
//    }
//
//    @Bean
//    public JwtAuthenticationConverter authenticationConverter(
//            Converter<Map<String, Object>, Collection<GrantedAuthority>> authoritiesConverter) {
//        var authenticationConverter = new JwtAuthenticationConverter();
//        authenticationConverter.setJwtGrantedAuthoritiesConverter(jwt ->
//                authoritiesConverter.convert(jwt.getClaims()));
//        return authenticationConverter;
//    }
//}