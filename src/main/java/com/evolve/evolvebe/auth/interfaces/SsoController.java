package com.evolve.evolvebe.auth.interfaces;

import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("sso")
@RequestMapping("/api/sso")
public class SsoController {

    @GetMapping("/me")
    public Map<String, Object> me(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return Map.of(
                "subject", jwt.getSubject(),
                "username", jwt.getClaimAsString("preferred_username"),
                "issuer", jwt.getIssuer().toString()
        );
    }
}