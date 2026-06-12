package com.joshualiu.dukefreefoodfinderbackend.food;

import com.joshualiu.dukefreefoodfinderbackend.user.User;
import com.joshualiu.dukefreefoodfinderbackend.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FoodServiceTest {

    @Mock
    private FoodRepository repository;

    @Mock
    private UserService userService;

    @InjectMocks
    private FoodService service;

    private User user;
    private Food food1;
    private Food food2;

    @BeforeEach
    void setUp() {
        user = new User("joshua.liu@duke.edu");
        user.setId(1L);

        food1 = new Food("Pizza", "Leftover", 0.0, 0.0, "Social Sciences",
                LocalDateTime.now().plusHours(2), user);
        food1.setId(1L);

        food2 = new Food("Tacos", "Leftover", 100.0, 100.0, "Crowell Quad",
                LocalDateTime.now().plusHours(2), user);
        food2.setId(2L);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("joshua.liu@duke.edu");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getAll_returnBothFoods() {
        when(repository.findByExpiresAtAfter(any(LocalDateTime.class)))
                .thenReturn(List.of(food1, food2));

        List<Food> result = service.getAllFoods();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Pizza");
        assertThat(result.get(1).getTitle()).isEqualTo("Tacos");
    }

    @Test
    void getById_existingId() {
        when(repository.findById(1L)).thenReturn(Optional.of(food1));

        Food result = service.getFoodById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Pizza");
    }

    @Test
    void getById_nonExistingId() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getFoodById(99L))
                .isInstanceOf(FoodNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createFood_savesAndReturnsFood() {
        when(userService.getUserByEmail("joshua.liu@duke.edu")).thenReturn(user);
        when(repository.save(food1)).thenReturn(food1);

        Food result = service.createFood(food1);

        assertThat(result.getTitle()).isEqualTo("Pizza");
        verify(repository, times(1)).save(food1);
    }

    @Test
    void updateFood() {
        Food updated = new Food("Tacos", "Leftover", 50.0, 50.0, "Few Quad",
                LocalDateTime.now().plusHours(3), user);

        when(repository.findById(1L)).thenReturn(Optional.of(food1));
        when(repository.save(any(Food.class))).thenReturn(food1);

        Food result = service.updateFood(1L, updated);

        assertThat(result.getLocationDetails()).isEqualTo("Few Quad");
    }

    @Test
    void deleteFood() {
        when(repository.findById(1L)).thenReturn(Optional.of(food1));
        doNothing().when(repository).deleteById(1L);

        service.deleteFood(1L);

        verify(repository, times(1)).deleteById(1L);
    }
}
