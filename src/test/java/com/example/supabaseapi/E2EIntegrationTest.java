package com.example.supabaseapi;

import com.example.supabaseapi.model.UserLog;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("e2e")
@EnabledIfEnvironmentVariable(named = "TEST_E2E", matches = "true")
class E2EIntegrationTest {

    private final RestTemplate restTemplate = new RestTemplateBuilder().rootUri("http://localhost:8080").build();

    @Test
    void testGetLogsUnauthorized() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/logs", String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    // Note: To test authorized requests, we would need a valid JWT signed by the Supabase instance.
    // In an E2E test, we could use the Supabase Admin API (GoTrue) to sign up a user and get a token.
    // For now, checking the 401 proves the server is up and reachable.
}
