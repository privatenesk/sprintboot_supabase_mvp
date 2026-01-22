package com.example.supabaseapi;

import com.example.supabaseapi.repository.UserLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class IntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserLogRepository userLogRepository;

    @Test
    void testGetLogs() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(get("/api/logs")
                .with(jwt().jwt(jwt -> jwt.claim("sub", userId.toString()))))
                .andExpect(status().isOk());

        // Verify that the repository was called to fetch logs
        verify(userLogRepository).findByUserId(userId);

        // Verify that the logging interceptor saved a log
        verify(userLogRepository).save(any());
    }

    @Test
    void testUnauthorized() throws Exception {
        mockMvc.perform(get("/api/logs"))
                .andExpect(status().isUnauthorized());
    }
}
