package com.joshualiu.dukefreefoodfinderbackend.user;

import com.joshualiu.dukefreefoodfinderbackend.auth.JwtAuthFilter;
import com.joshualiu.dukefreefoodfinderbackend.auth.JwtService;
import com.joshualiu.dukefreefoodfinderbackend.auth.SecurityConfig;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService service;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    private User user;
    private UsernamePasswordAuthenticationToken mockAuth;

    @BeforeEach
    void setUp() throws Exception {
        doAnswer(invocation -> {
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(jwtAuthFilter).doFilter(any(), any(), any());

        user = new User("joshua.liu@duke.edu");
        user.setId(1L);

        // FIX: Pass the email string as the principal, not the User object
        mockAuth = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void getById_ownProfile_returnsOk() throws Exception {
        when(service.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1")
                        .with(authentication(mockAuth)))
                .andExpect(status().isOk());
    }

    @Test
    void getById_otherProfile_returns403() throws Exception {
        when(service.getUserById(1L)).thenReturn(user);

        User sarah = new User("sarah@duke.edu");
        sarah.setId(2L);

        // FIX: Pass Sarah's email string as the principal
        UsernamePasswordAuthenticationToken sarahAuth = new UsernamePasswordAuthenticationToken(
                sarah.getEmail(), null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(get("/api/users/1")
                        .with(authentication(sarahAuth)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getById_notLoggedIn_returns403() throws Exception {
        // No authentication token provided
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isForbidden());
    }
}