package com.example.supabaseapi.interceptor;

import com.example.supabaseapi.model.UserLog;
import com.example.supabaseapi.repository.UserLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LoggingInterceptorTest {

    @Mock
    private UserLogRepository userLogRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private LoggingInterceptor loggingInterceptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void postHandle_shouldSaveLog_whenJwtIsValid() throws Exception {
        UUID userId = UUID.randomUUID();
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("sub")).thenReturn(userId.toString());
        Authentication auth = new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken(jwt);

        when(securityContext.getAuthentication()).thenReturn(auth);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/api/test"));
        when(request.getParameterMap()).thenReturn(Collections.emptyMap());
        when(response.getStatus()).thenReturn(200);

        loggingInterceptor.postHandle(request, response, new Object(), null);

        verify(userLogRepository).save(any(UserLog.class));
    }

    @Test
    void postHandle_shouldNotSaveLog_whenAuthenticationIsNull() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(null);

        loggingInterceptor.postHandle(request, response, new Object(), null);

        verify(userLogRepository, never()).save(any(UserLog.class));
    }

    @Test
    void postHandle_shouldNotSaveLog_whenAuthenticationIsNotJwt() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken("user", "password");
        when(securityContext.getAuthentication()).thenReturn(auth);

        loggingInterceptor.postHandle(request, response, new Object(), null);

        verify(userLogRepository, never()).save(any(UserLog.class));
    }

    @Test
    void postHandle_shouldNotSaveLog_whenSubClaimIsMissing() throws Exception {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("sub")).thenReturn(null);
        Authentication auth = new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken(jwt);

        when(securityContext.getAuthentication()).thenReturn(auth);

        loggingInterceptor.postHandle(request, response, new Object(), null);

        verify(userLogRepository, never()).save(any(UserLog.class));
    }
}
