package br.com.customer;

import br.com.customer.dto.request.AuthenticationRequest;
import br.com.customer.dto.request.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Boots the app against a throwaway Postgres so Flyway migrations and the
 * Postgres array columns behave exactly like in production.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public abstract class IntegrationTestSupport {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    /**
     * Signing keys are generated per run and handed over as inline PEM, so no key
     * material is ever written to disk or committed.
     */
    private static final KeyPair SIGNING_KEYS = generateSigningKeys();

    @DynamicPropertySource
    static void testProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.flyway.url", POSTGRES::getJdbcUrl);
        registry.add("spring.flyway.user", POSTGRES::getUsername);
        registry.add("spring.flyway.password", POSTGRES::getPassword);
        registry.add("jwt.public-key", () -> pem("PUBLIC KEY", SIGNING_KEYS.getPublic().getEncoded()));
        registry.add("jwt.private-key", () -> pem("PRIVATE KEY", SIGNING_KEYS.getPrivate().getEncoded()));
    }

    private static KeyPair generateSigningKeys(){
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("RSA is required to sign test tokens", exception);
        }
    }

    private static String pem(String label, byte[] encoded){
        return "-----BEGIN " + label + "-----\n"
                + Base64.getMimeEncoder(64, "\n".getBytes(StandardCharsets.UTF_8)).encodeToString(encoded)
                + "\n-----END " + label + "-----";
    }

    protected static final String PASSWORD = "123456";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    /** Registers a fresh user and returns a real signed JWT for it. */
    protected String authenticate(String username) throws Exception {
        RegisterRequest register = new RegisterRequest(
                username,
                username,
                username + "@trainup.dev",
                PASSWORD,
                LocalDate.of(1996, 4, 12)
        );

        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(register)))
                .andExpect(status().isCreated());

        String body = mockMvc.perform(post("/api/v1/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new AuthenticationRequest(username, PASSWORD))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(body).get("token").asText();
    }

    protected String bearer(String token){
        return "Bearer " + token;
    }
}
