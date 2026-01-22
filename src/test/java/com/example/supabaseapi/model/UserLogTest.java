package com.example.supabaseapi.model;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserLogTest {

    @Test
    void testUserLogBuilderAndGetters() {
        UUID userId = UUID.randomUUID();
        ZonedDateTime now = ZonedDateTime.now();
        String title = "Test Title";
        String description = "Test Description";
        String ip = "127.0.0.1";
        String url = "http://localhost/test";
        String params = "param=value";
        String state = "200";

        UserLog log = UserLog.builder()
                .id(1L)
                .createdAt(now)
                .userId(userId)
                .title(title)
                .description(description)
                .deviceIp(ip)
                .requestUrl(url)
                .requestParam(params)
                .state(state)
                .build();

        assertEquals(1L, log.getId());
        assertEquals(now, log.getCreatedAt());
        assertEquals(userId, log.getUserId());
        assertEquals(title, log.getTitle());
        assertEquals(description, log.getDescription());
        assertEquals(ip, log.getDeviceIp());
        assertEquals(url, log.getRequestUrl());
        assertEquals(params, log.getRequestParam());
        assertEquals(state, log.getState());
    }

    @Test
    void testUserLogEqualsAndHashCode() {
        UUID userId = UUID.randomUUID();
        ZonedDateTime now = ZonedDateTime.now();

        UserLog log1 = UserLog.builder()
                .id(1L)
                .createdAt(now)
                .userId(userId)
                .build();

        UserLog log2 = UserLog.builder()
                .id(1L)
                .createdAt(now)
                .userId(userId)
                .build();

        UserLog log3 = UserLog.builder()
                .id(2L)
                .createdAt(now)
                .userId(userId)
                .build();

        assertEquals(log1, log2);
        assertEquals(log1.hashCode(), log2.hashCode());
        assertNotEquals(log1, log3);
    }

    @Test
    void testNoArgsConstructor() {
        UserLog log = new UserLog();
        assertNull(log.getId());
    }

    @Test
    void testAllArgsConstructor() {
        UUID userId = UUID.randomUUID();
        ZonedDateTime now = ZonedDateTime.now();
        UserLog log = new UserLog(1L, now, userId, "title", "desc", "ip", "url", "params", "state");
        assertEquals(1L, log.getId());
    }

    @Test
    void testSetters() {
         UserLog log = new UserLog();
         log.setId(1L);
         assertEquals(1L, log.getId());
    }

    @Test
    void testToString() {
        UserLog log = UserLog.builder().id(1L).build();
        assertNotNull(log.toString());
    }
}
