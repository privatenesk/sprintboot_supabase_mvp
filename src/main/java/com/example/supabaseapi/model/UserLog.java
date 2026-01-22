package com.example.supabaseapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    private String title;

    private String description;

    @Column(name = "device_ip")
    private String deviceIp;

    @Column(name = "request_url")
    private String requestUrl;

    @Column(name = "request_param")
    private String requestParam;

    private String state;
}
