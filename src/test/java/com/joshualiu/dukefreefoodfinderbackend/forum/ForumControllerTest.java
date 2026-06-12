package com.joshualiu.dukefreefoodfinderbackend.forum;

import com.joshualiu.dukefreefoodfinderbackend.auth.JwtAuthFilter;
import com.joshualiu.dukefreefoodfinderbackend.auth.JwtService;
import com.joshualiu.dukefreefoodfinderbackend.auth.SecurityConfig;
import com.joshualiu.dukefreefoodfinderbackend.food.Food;
import com.joshualiu.dukefreefoodfinderbackend.user.User;
import jakarta.servlet.FilterChain;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ForumController.class)
@Import(SecurityConfig.class)
class ForumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ForumService service;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User user;
    private Forum forum1;
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

        mockAuth = new UsernamePasswordAuthenticationToken(
                user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        Food food = new Food("Pizza", "Leftover", 0.0, 0.0, "Social Sciences",
                LocalDateTime.now().plusHours(2), user);
        food.setId(10L);

        forum1 = new Forum("Any left?", user, food);
        forum1.setId(1L);

        Forum forum2 = new Forum("On my way!", user, food);
        forum2.setId(2L);
    }

    @Test
    void getAll_returnsOkWithBothForums() throws Exception {
        when(service.getAllForums()).thenReturn(List.of(forum1));

        mockMvc.perform(get("/api/forum")
                        .with(authentication(mockAuth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getById_existingId_returnsOk() throws Exception {
        when(service.getForumById(1L)).thenReturn(forum1);

        mockMvc.perform(get("/api/forum/1")
                        .with(authentication(mockAuth)))
                .andExpect(status().isOk());
    }

    @Test
    void getById_nonExistingId_returns404() throws Exception {
        when(service.getForumById(99L))
                .thenThrow(new ForumNotFoundException("Forum not found with id: 99"));

        mockMvc.perform(get("/api/forum/99")
                        .with(authentication(mockAuth)))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_validForum_returnsOk() throws Exception {
        when(service.createForum(any(Forum.class))).thenReturn(forum1);

        mockMvc.perform(post("/api/forum")
                        .with(authentication(mockAuth))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forum1)))
                .andExpect(status().isOk());
    }

    @Test
    void update_existingId_returnsOk() throws Exception {
        when(service.updateForum(eq(1L), any(Forum.class))).thenReturn(forum1);

        mockMvc.perform(put("/api/forum/1")
                        .with(authentication(mockAuth))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forum1)))
                .andExpect(status().isOk());
    }

    @Test
    void delete_existingId_returns204() throws Exception {
        doNothing().when(service).deleteForum(1L);

        mockMvc.perform(delete("/api/forum/1")
                        .with(authentication(mockAuth))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
