package com.example.supabaseapi.controller;

import com.example.supabaseapi.model.UserLog;
import com.example.supabaseapi.repository.UserLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/logs")
public class UserLogController {

    @Autowired
    private UserLogRepository userLogRepository;

    @GetMapping
    public ResponseEntity<List<UserLog>> getUserLogs(@AuthenticationPrincipal Jwt jwt) {
        String sub = jwt.getClaimAsString("sub");
        if (sub == null) {
            return ResponseEntity.badRequest().build();
        }

        UUID userId = UUID.fromString(sub);
        List<UserLog> logs = userLogRepository.findByUserId(userId);

        return ResponseEntity.ok(logs);
    }
}
