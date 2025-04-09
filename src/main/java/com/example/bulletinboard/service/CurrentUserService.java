package com.example.bulletinboard.service;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrentUserService {
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
            return jwtAuth.getToken().getClaimAsString("preferred_username"); // Или другой claim
        }

        throw new IllegalStateException("Unsupported authentication type");
    }

    public AccessToken getCurrentUserToken() {
        KeycloakAuthenticationToken authentication =
                (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        return ((KeycloakPrincipal<?>) authentication.getPrincipal())
                .getKeycloakSecurityContext()
                .getToken();
    }

    public boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
            List<String> roles = jwtAuth.getToken().getClaimAsStringList("roles");
            return roles != null && roles.contains(roleName);
        } else if (authentication instanceof KeycloakAuthenticationToken) {
            AccessToken token = getCurrentUserToken();
            return token.getRealmAccess().getRoles().contains(roleName) ||
                    token.getResourceAccess().values().stream()
                            .anyMatch(access -> access.getRoles().contains(roleName));
        }

        return false;
    }


    public boolean isAdminOrModerator() {
        return hasRole("ADMIN") || hasRole("MODERATOR");
    }
}
