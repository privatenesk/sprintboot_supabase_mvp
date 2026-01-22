package com.example.supabaseapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("e2e")
@EnabledIfEnvironmentVariable(named = "TEST_E2E", matches = "true")
class E2EIntegrationTest {

    private final RestTemplate restTemplate = new RestTemplateBuilder()
            .rootUri("http://localhost:8080")
            .errorHandler(new DefaultResponseErrorHandler() {
                @Override
                public boolean hasError(ClientHttpResponse response) throws IOException {
                    // Do not throw exceptions on 4xx and 5xx
                    return false;
                }
            })
            .build();

    private final RestTemplate authRestTemplate = new RestTemplateBuilder()
            .errorHandler(new DefaultResponseErrorHandler() {
                 @Override
                 public boolean hasError(ClientHttpResponse response) throws IOException {
                     return false;
                 }
            })
            .build();

    @Test
    void testGetLogsUnauthorized() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/logs", String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testGetLogsAuthorized() throws Exception {
        String supabaseUrl = System.getenv("SUPABASE_URL");
        String supabaseKey = System.getenv("SUPABASE_KEY");

        assertNotNull(supabaseUrl, "SUPABASE_URL must be set");
        assertNotNull(supabaseKey, "SUPABASE_KEY must be set");

        // 1. Sign up/Sign in a user to get a JWT
        String token = getJwtToken(supabaseUrl, supabaseKey);
        assertNotNull(token, "Failed to obtain JWT token");

        // 2. Call the API with the token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange("/api/logs", HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        // We expect an empty list or some JSON array
        assertTrue(response.getBody().contains("[]") || response.getBody().contains("["), "Response body should be a JSON array");
    }

    private String getJwtToken(String supabaseUrl, String supabaseKey) throws Exception {
        String authUrl = supabaseUrl + "/auth/v1/signup";
        String email = "test" + UUID.randomUUID() + "@example.com";
        String password = "test-password-123";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", supabaseKey);

        Map<String, String> body = Map.of("email", email, "password", password);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = authRestTemplate.postForEntity(authUrl, request, String.class);

        if (response.getStatusCode().isError()) {
            System.err.println("Auth Error: " + response.getBody());
            // If signup fails (maybe auto-confirm is off?), try sign in?
            // Local supabase usually has auto-confirm on by default.
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        if (root.has("access_token")) {
            return root.get("access_token").asText();
        }

        // Sometimes signup returns the user but not the session if confirmation is required.
        // But for local dev with defaults, it usually returns the session.
        return null;
    }
}
