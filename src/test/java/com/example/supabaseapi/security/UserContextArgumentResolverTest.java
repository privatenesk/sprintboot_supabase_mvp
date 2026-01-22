package com.example.supabaseapi.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserContextArgumentResolverTest {

    private UserContextArgumentResolver resolver;
    private MethodParameter parameter;
    private ModelAndViewContainer mavContainer;
    private NativeWebRequest webRequest;
    private WebDataBinderFactory binderFactory;
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        resolver = new UserContextArgumentResolver();
        parameter = mock(MethodParameter.class);
        mavContainer = mock(ModelAndViewContainer.class);
        webRequest = mock(NativeWebRequest.class);
        binderFactory = mock(WebDataBinderFactory.class);
        securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void supportsParameter_shouldReturnTrueForUserContext() {
        doReturn(UserContext.class).when(parameter).getParameterType();
        assertTrue(resolver.supportsParameter(parameter));
    }

    @Test
    void supportsParameter_shouldReturnFalseForOtherTypes() {
        doReturn(String.class).when(parameter).getParameterType();
        assertFalse(resolver.supportsParameter(parameter));
    }

    @Test
    void resolveArgument_shouldReturnUserContext_whenJwtIsValid() throws Exception {
        UUID userId = UUID.randomUUID();
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("sub")).thenReturn(userId.toString());
        Authentication auth = new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken(jwt);

        when(securityContext.getAuthentication()).thenReturn(auth);

        Object result = resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        assertInstanceOf(UserContext.class, result);
        assertEquals(userId, ((UserContext) result).userId());
    }

    @Test
    void resolveArgument_shouldThrowUnauthorized_whenNoAuthentication() {
        when(securityContext.getAuthentication()).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    void resolveArgument_shouldThrowUnauthorized_whenNotJwtAuthentication() {
        Authentication auth = new UsernamePasswordAuthenticationToken("user", "password");
        when(securityContext.getAuthentication()).thenReturn(auth);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    void resolveArgument_shouldThrowBadRequest_whenSubClaimIsMissing() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("sub")).thenReturn(null);
        Authentication auth = new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken(jwt);

        when(securityContext.getAuthentication()).thenReturn(auth);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Missing 'sub' claim in JWT", exception.getReason());
    }

    @Test
    void resolveArgument_shouldThrowBadRequest_whenSubClaimIsInvalid() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("sub")).thenReturn("invalid-uuid");
        Authentication auth = new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken(jwt);

        when(securityContext.getAuthentication()).thenReturn(auth);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Invalid 'sub' claim format", exception.getReason());
    }
}
