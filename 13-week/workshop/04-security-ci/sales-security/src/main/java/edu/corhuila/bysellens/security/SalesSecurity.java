package edu.corhuila.bysellens.security;

import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SalesSecurity {
    @Bean
    SecurityFilterChain salesFilterChain(HttpSecurity http) throws Exception {
        var roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("role");
        roles.setAuthorityPrefix("ROLE_");
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(roles);
        return http
            // This isolated API accepts Authorization headers only; no cookie/session login.
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a
                .requestMatchers(HttpMethod.GET, "/api/v1/health").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/sales").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/sales").hasRole("ADMIN")
                .anyRequest().denyAll())
            .oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(converter)))
            .build();
    }

    @Bean
    JwtDecoder salesJwtDecoder(RSAPublicKey publicKey) {
        var decoder = NimbusJwtDecoder.withPublicKey(publicKey)
            .signatureAlgorithm(SignatureAlgorithm.RS256).build();
        OAuth2TokenValidator<Jwt> profile = jwt -> {
            var now = Instant.now();
            boolean valid = jwt.getAudience().contains("bysellens-api")
                && jwt.getSubject() != null && !jwt.getSubject().isBlank()
                && jwt.getIssuedAt() != null && jwt.getExpiresAt() != null
                && !jwt.getIssuedAt().isAfter(now)
                && jwt.getExpiresAt().isAfter(now)
                && jwt.getExpiresAt().getEpochSecond() - jwt.getIssuedAt().getEpochSecond() == 1800;
            return valid ? OAuth2TokenValidatorResult.success()
                : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token"));
        };
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
            JwtValidators.createDefaultWithIssuer("bysellens-auth"), profile));
        return decoder;
    }
}
