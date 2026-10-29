package edu.corhuila.bysellens.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.security.*;
import java.security.interfaces.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.*;
import org.springframework.context.annotation.*;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SalesPostApiV1SalesAuthorizationTest {
    static KeyPair keyPair;
    static AnnotationConfigWebApplicationContext context;
    static MockMvc mvc;
    static final AtomicInteger handlerInvocations = new AtomicInteger();

    @Configuration @EnableWebMvc @Import(SalesSecurity.class)
    static class Harness {
        @Bean RSAPublicKey publicKey() { return (RSAPublicKey) keyPair.getPublic(); }
        @Bean SalesHandler salesHandler() { return new SalesHandler(); }
    }

    @RestController
    static class SalesHandler {
        @RequestMapping(value = "/api/v1/sales", method = {RequestMethod.GET, RequestMethod.POST})
        Map<String, Object> sales() {
            handlerInvocations.incrementAndGet();
            return Map.of("prototype_only", true, "sale_persisted", false);
        }
    }

    @BeforeAll static void startSecurityModule() throws Exception {
        var generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        keyPair = generator.generateKeyPair();
        context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(new MockServletContext());
        context.register(Harness.class);
        context.refresh();
        mvc = MockMvcBuilders.webAppContextSetup(context)
            .addFilter(context.getBean(FilterChainProxy.class)).build();
    }
    @AfterAll static void closeModule() { context.close(); }
    @BeforeEach void resetCount() { handlerInvocations.set(0); }

    static String token(String role, String issuer, String audience, Instant issuedAt,
                        KeyPair signer) throws Exception {
        var claims = new JWTClaimsSet.Builder().subject("550e8400-e29b-41d4-a716-446655440000")
            .issuer(issuer).audience(audience).issueTime(Date.from(issuedAt))
            .expirationTime(Date.from(issuedAt.plusSeconds(1800))).claim("role", role).build();
        var jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claims);
        jwt.sign(new RSASSASigner((RSAPrivateKey) signer.getPrivate()));
        return jwt.serialize();
    }
    static String validToken(String role) throws Exception {
        return token(role, "bysellens-auth", "bysellens-api", Instant.now(), keyPair);
    }

    @Test void postApiV1SalesWithValidNonAdminJwtReturns403BeforeHandler() throws Exception {
        // A test-only non-ADMIN identity; not an additional product role.
        mvc.perform(post("/api/v1/sales").header("Authorization", "Bearer " + validToken("TEST_ONLY"))
            .header("X-User-Role", "ADMIN").header("X-User-Id", "forged-admin")
            .contentType("application/json").content("{}"))
            .andExpect(status().isForbidden());
        assertEquals(0, handlerInvocations.get());
    }
    @Test void getApiV1SalesWithNonAdminJwtReturns403AndNoHistory() throws Exception {
        mvc.perform(get("/api/v1/sales").header("Authorization", "Bearer " + validToken("TEST_ONLY")))
            .andExpect(status().isForbidden());
        assertEquals(0, handlerInvocations.get());
    }
    @Test void postApiV1SalesWithoutTokenReturns401() throws Exception {
        mvc.perform(post("/api/v1/sales").header("X-User-Role", "ADMIN")).andExpect(status().isUnauthorized());
        assertEquals(0, handlerInvocations.get());
    }
    @Test void postApiV1SalesWithValidAdminReachesHandler() throws Exception {
        mvc.perform(post("/api/v1/sales").header("Authorization", "Bearer " + validToken("ADMIN")))
            .andExpect(status().isOk());
        assertEquals(1, handlerInvocations.get());
    }
    @Test void wrongIssuerAudienceAndExpiredTokenReturn401() throws Exception {
        for (String invalid : List.of(
                token("ADMIN", "wrong-issuer", "bysellens-api", Instant.now(), keyPair),
                token("ADMIN", "bysellens-auth", "wrong-audience", Instant.now(), keyPair),
                token("ADMIN", "bysellens-auth", "bysellens-api", Instant.now().minusSeconds(3600), keyPair))) {
            mvc.perform(post("/api/v1/sales").header("Authorization", "Bearer " + invalid))
                .andExpect(status().isUnauthorized());
        }
        assertEquals(0, handlerInvocations.get());
    }
    @Test void tokenSignedByUntrustedKeyReturns401() throws Exception {
        var generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        var invalid = token("ADMIN", "bysellens-auth", "bysellens-api", Instant.now(), generator.generateKeyPair());
        mvc.perform(post("/api/v1/sales").header("Authorization", "Bearer " + invalid))
            .andExpect(status().isUnauthorized());
        assertEquals(0, handlerInvocations.get());
    }
}
