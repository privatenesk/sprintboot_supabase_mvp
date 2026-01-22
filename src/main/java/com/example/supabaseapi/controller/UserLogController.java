package com.example.supabaseapi.controller;

import com.example.supabaseapi.model.UserLog;
import com.example.supabaseapi.repository.UserLogRepository;
import com.example.supabaseapi.security.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class UserLogController {

    @Autowired
    private UserLogRepository userLogRepository;

    @GetMapping
    public ResponseEntity<List<UserLog>> getUserLogs(UserContext userContext) {
        List<UserLog> logs = userLogRepository.findByUserId(userContext.userId());
        return ResponseEntity.ok(logs);
    }
}
