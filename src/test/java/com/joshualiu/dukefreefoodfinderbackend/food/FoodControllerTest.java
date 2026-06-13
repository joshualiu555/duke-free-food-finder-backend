package com.joshualiu.dukefreefoodfinderbackend.food;

import com.joshualiu.dukefreefoodfinderbackend.auth.JwtAuthFilter;
import com.joshualiu.dukefreefoodfinderbackend.auth.JwtService;
import com.joshualiu.dukefreefoodfinderbackend.auth.SecurityConfig;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(FoodController.class)
@Import(SecurityConfig.class)
class FoodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FoodService service;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User user;
    private Food food1;
    private Food food2;
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

        food1 = new Food("Pizza", "Leftover", 0.0, 0.0, "Main quad",
                LocalDateTime.now().plusHours(2), user);
        food1.setId(1L);

        food2 = new Food("Tacos", "Leftover", 100.0, 100.0, "Student union",
                LocalDateTime.now().plusHours(2), user);
        food2.setId(2L);
    }

    @Test
    void getAll_returnsOkWithBothFoods() throws Exception {
        when(service.getAllFoods()).thenReturn(List.of(food1, food2));

        mockMvc.perform(get("/api/food")
                        .with(authentication(mockAuth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getById_existingId_returnsOk() throws Exception {
        when(service.getFoodById(1L)).thenReturn(food1);

        mockMvc.perform(get("/api/food/1")
                        .with(authentication(mockAuth)))
                .andExpect(status().isOk());
    }

    @Test
    void getById_nonExistingId_returns404() throws Exception {
        when(service.getFoodById(99L)).thenThrow(new FoodNotFoundException("Food not found with id: 99"));

        mockMvc.perform(get("/api/food/99")
                        .with(authentication(mockAuth)))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_validFood_returnsOk() throws Exception {
        when(service.createFood(any(Food.class))).thenReturn(food1);

        mockMvc.perform(post("/api/food")
                        .with(authentication(mockAuth))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(food1)))
                .andExpect(status().isOk());
    }

    @Test
    void update_existingId_returnsOk() throws Exception {
        when(service.updateFood(eq(1L), any(Food.class))).thenReturn(food2);

        mockMvc.perform(put("/api/food/1")
                        .with(authentication(mockAuth))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(food2)))
                .andExpect(status().isOk());
    }

    @Test
    void delete_existingId_returns204() throws Exception {
        doNothing().when(service).deleteFood(1L);

        mockMvc.perform(delete("/api/food/1")
                        .with(authentication(mockAuth))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}