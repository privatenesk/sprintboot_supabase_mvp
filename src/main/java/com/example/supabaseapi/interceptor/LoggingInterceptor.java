package com.example.supabaseapi.interceptor;

import com.example.supabaseapi.model.UserLog;
import com.example.supabaseapi.repository.UserLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Autowired
    private UserLogRepository userLogRepository;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String sub = jwt.getClaimAsString("sub");

            if (sub != null) {
                UserLog log = UserLog.builder()
                        .createdAt(ZonedDateTime.now())
                        .userId(UUID.fromString(sub))
                        .title(request.getMethod() + " " + request.getRequestURI())
                        .description("API Request")
                        .deviceIp(request.getRemoteAddr())
                        .requestUrl(request.getRequestURL().toString())
                        .requestParam(request.getParameterMap().entrySet().stream()
                                .map(e -> e.getKey() + "=" + String.join(",", e.getValue()))
                                .collect(Collectors.joining("&")))
                        .state(String.valueOf(response.getStatus()))
                        .build();

                userLogRepository.save(log);
            }
        }
    }
}
