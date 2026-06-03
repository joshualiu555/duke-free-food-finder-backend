package com.joshualiu.dukefreefoodfinderbackend.food;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class FoodServiceTest {

    @Mock
    private FoodRepository repository;

    @InjectMocks
    private FoodService service;

    private Food food1;
    private Food food2;

    @BeforeEach
    void setUp() {
        food1 = new Food("Pizza", "Leftover", 0.0, 0.0, "Social Sciences");
        food1.setId(1L);

        food2 = new Food("Tacos", "Leftover", 100.0, 100.0, "Crowell Quad");
        food2.setId(2L);
    }

    @Test
    void getAll_returnBothFoods() {
        when(repository.findAll()).thenReturn(List.of(food1, food2));

        List<Food> result = service.getAllPosts();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Pizza");
        assertThat(result.get(1).getTitle()).isEqualTo("Tacos");
    }

    @Test
    void getById_existingId() {
        when(repository.findById(1L)).thenReturn(Optional.of(food1));

        Food result = service.getPostById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Pizza");
    }

    @Test
    void getById_nonExistingId() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getPostById(99L))
                .isInstanceOf(FoodNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createFood_savesAndReturnsFood() {
        when(repository.save(food1)).thenReturn(food1);

        Food result = service.createFood(food1);

        assertThat(result.getTitle()).isEqualTo("Pizza");
        verify(repository, times(1)).save(food1);
    }

    @Test
    void updateFood() {
        Food updated = new Food("Tacos", "Leftover", 50.0, 50.0, "Few Quad");

        when(repository.findById(1L)).thenReturn(Optional.of(food1));
        when(repository.save(any(Food.class))).thenReturn(food1);

        Food result = service.updateFood(1L, updated);

        assertThat(result.getLocationDetails()).isEqualTo("Few Quad");
    }

    @Test
    void deleteFood() {
        doNothing().when(repository).deleteById(1L);

        service.deleteFood(1L);

        verify(repository, times(1)).deleteById(1L);
    }
}
