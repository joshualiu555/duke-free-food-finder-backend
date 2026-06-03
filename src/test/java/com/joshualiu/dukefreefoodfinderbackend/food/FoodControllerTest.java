package com.joshualiu.dukefreefoodfinderbackend.food;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

@WebMvcTest(FoodController.class)
class FoodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FoodService service;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Food food1;
    private Food food2;

    @BeforeEach
    void setUp() {
        food1 = new Food("Pizza", "Leftover", 0.0, 0.0, "Main quad");
        food1.setId(1L);

        food2 = new Food("Tacos", "Leftover", 100.0, 100.0, "Student union");
        food2.setId(2L);
    }

    @Test
    void getAll_returnsOkWithBothFoods() throws Exception {
        when(service.getAllPosts()).thenReturn(List.of(food1, food2));

        mockMvc.perform(get("/api/food-posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getById_existingId_returnsOk() throws Exception {
        when(service.getPostById(1L)).thenReturn(food1);

        mockMvc.perform(get("/api/food-posts/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getById_nonExistingId_returns404() throws Exception {
        when(service.getPostById(99L)).thenThrow(new FoodNotFoundException("Food not found with id: 99"));

        mockMvc.perform(get("/api/food-posts/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_validFood_returnsOk() throws Exception {
        when(service.createFood(any(Food.class))).thenReturn(food1);

        mockMvc.perform(post("/api/food-posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(food1)))
                .andExpect(status().isOk());
    }

    @Test
    void update_existingId_returnsOk() throws Exception {
        when(service.updateFood(eq(1L), any(Food.class))).thenReturn(food2);

        mockMvc.perform(put("/api/food-posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(food2)))
                .andExpect(status().isOk());
    }

    @Test
    void delete_existingId_returns204() throws Exception {
        doNothing().when(service).deleteFood(1L);

        mockMvc.perform(delete("/api/food-posts/1"))
                .andExpect(status().isNoContent());
    }
}
