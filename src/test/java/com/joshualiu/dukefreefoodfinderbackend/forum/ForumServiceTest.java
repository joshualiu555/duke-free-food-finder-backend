package com.joshualiu.dukefreefoodfinderbackend.forum;

import com.joshualiu.dukefreefoodfinderbackend.food.Food;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ForumServiceTest {

    @Mock
    private ForumRepository repository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ForumService service;

    private User user;
    private Food food;
    private Forum forum1;
    private Forum forum2;

    @BeforeEach
    void setUp() {
        user = new User("joshua.liu@duke.edu");
        user.setId(1L);

        food = new Food("Pizza", "Leftover", 0.0, 0.0, "Social Sciences",
                LocalDateTime.now().plusHours(2), user);
        food.setId(10L);

        forum1 = new Forum("What type of pizza?", user, food);
        forum1.setId(1L);

        forum2 = new Forum("Any left?", user, food);
        forum2.setId(2L);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("joshua.liu@duke.edu");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getAll_returnsBothForums() {
        when(repository.findAll()).thenReturn(List.of(forum1, forum2));

        List<Forum> result = service.getAllForums();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getContent()).isEqualTo("What type of pizza?");
        assertThat(result.get(1).getContent()).isEqualTo("Any left?");
    }

    @Test
    void getById_existingId() {
        when(repository.findById(1L)).thenReturn(Optional.of(forum1));

        Forum result = service.getForumById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getContent()).isEqualTo("What type of pizza?");
    }

    @Test
    void getById_nonExistingId() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getForumById(99L))
                .isInstanceOf(ForumNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createForum_savesAndReturnsForum() {
        when(userService.getUserByEmail("joshua.liu@duke.edu")).thenReturn(user);
        when(repository.save(forum1)).thenReturn(forum1);

        Forum result = service.createForum(forum1);

        assertThat(result.getContent()).isEqualTo("What type of pizza?");
        verify(repository, times(1)).save(forum1);
    }

    @Test
    void updateForum() {
        Forum updated = new Forum("Just pepperoni", user, food);

        when(repository.findById(1L)).thenReturn(Optional.of(forum1));
        when(repository.save(any(Forum.class))).thenReturn(forum1);

        Forum result = service.updateForum(1L, updated);

        assertThat(result.getContent()).isEqualTo("Just pepperoni");
    }

    @Test
    void deleteForum() {
        when(repository.findById(1L)).thenReturn(Optional.of(forum1));
        doNothing().when(repository).deleteById(1L);

        service.deleteForum(1L);

        verify(repository, times(1)).deleteById(1L);
    }
}
