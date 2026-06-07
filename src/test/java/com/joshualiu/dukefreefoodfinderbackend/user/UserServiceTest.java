package com.joshualiu.dukefreefoodfinderbackend.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("joshua.liu@duke.edu");
        user.setId(1L);
    }

    @Test
    void getUserById_existingId_returnsUser() {
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        User result = service.getUserById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("joshua.liu@duke.edu");
    }

    @Test
    void getUserById_nonExistingId_throwsException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getUserById(99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getUserByEmail_existingEmail_returnsUser() {
        when(repository.findByEmail("joshua.liu@duke.edu")).thenReturn(Optional.of(user));

        User result = service.getUserByEmail("joshua.liu@duke.edu");

        assertThat(result.getEmail()).isEqualTo("joshua.liu@duke.edu");
    }

    @Test
    void getUserByEmail_nonExistingEmail_throwsException() {
        when(repository.findByEmail("unknown@duke.edu")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getUserByEmail("unknown@duke.edu"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("unknown@duke.edu");
    }

    @Test
    void createUser_validDukeEmail_createsAndReturnsUser() {
        when(repository.findByEmail("joshua.liu@duke.edu")).thenReturn(Optional.empty());
        when(repository.save(any(User.class))).thenReturn(user);

        User result = service.createUser("joshua.liu@duke.edu");

        assertThat(result.getEmail()).isEqualTo("joshua.liu@duke.edu");
        verify(repository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_existingEmail_returnsExistingUser() {
        when(repository.findByEmail("joshua.liu@duke.edu")).thenReturn(Optional.of(user));

        User result = service.createUser("joshua.liu@duke.edu");

        assertThat(result.getEmail()).isEqualTo("joshua.liu@duke.edu");
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void createUser_nonDukeEmail_throwsException() {
        assertThatThrownBy(() -> service.createUser("joshua.liu@gmail.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("duke.edu");
    }
}
