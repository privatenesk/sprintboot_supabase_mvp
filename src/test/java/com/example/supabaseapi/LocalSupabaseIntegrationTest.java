package com.example.supabaseapi;

import com.example.supabaseapi.repository.UserLogRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("local-supabase")
@AutoConfigureMockMvc
@Tag("external")
// Only run if specifically enabled, as it requires a running Supabase instance
@EnabledIfEnvironmentVariable(named = "TEST_LOCAL_SUPABASE", matches = "true")
class LocalSupabaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserLogRepository userLogRepository;

    @Test
    void testGetLogsFromRealSupabase() throws Exception {
        UUID userId = UUID.randomUUID();

        // We simulate a JWT. In a real scenario against a real Auth server,
        // we might need a real valid token signed by the private key corresponding to the JWK Set.
        // However, Spring Security validates the signature.
        // If we are pointing to a real JWK Set (localhost:54321), the JWT created by `jwt()` here
        // is a MOCK token created by Spring Security Test. It is NOT signed by the real server.
        // But `jwt()` post-processor *bypasses* the actual JWT decoding/validation filter
        // and directly populates the SecurityContext.
        // So this test verifies that *if* we have a valid user, the DB connection works.

        mockMvc.perform(get("/api/logs")
                .with(jwt().jwt(jwt -> jwt.claim("sub", userId.toString()))))
                .andExpect(status().isOk());

        // Verification of DB persistence would go here if we wanted to check the side effect
        // but since we don't control the initial state of the external DB easily,
        // we just check if the query executes without error.
    }
}
