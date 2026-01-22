package com.example.supabaseapi.security;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class UserContextTest {

    @Test
    void testUserContext() {
        UUID userId = UUID.randomUUID();
        UserContext context = new UserContext(userId);
        assertEquals(userId, context.userId());

        UserContext context2 = new UserContext(userId);
        assertEquals(context, context2);
        assertEquals(context.hashCode(), context2.hashCode());
        assertNotNull(context.toString());
    }
}
