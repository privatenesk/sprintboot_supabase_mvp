package com.example.supabaseapi;

import com.example.supabaseapi.repository.UserLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SupabaseApiApplicationTests {

    @MockBean
    private UserLogRepository userLogRepository;

	@Test
	void contextLoads() {
	}

}
