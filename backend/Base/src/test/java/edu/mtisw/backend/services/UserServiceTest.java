package edu.mtisw.backend.services;

import edu.mtisw.backend.entities.UserEntity;
import edu.mtisw.backend.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new UserEntity(1L, "12345678-9", "John", "Doe", "john@example.com", LocalDate.of(96, 5, 15));
    }

    @Test
    void whenGetUsers_thenReturnUserList() {
        // Given
        List<UserEntity> userList = new ArrayList<>();
        userList.add(user);
        when(userRepository.findAll()).thenReturn(userList);

        // When
        List<UserEntity> result = userService.getUsers();

        // Then
        assertThat(result).isEqualTo(userList);
    }

    @Test
    void whenSaveUser_thenReturnSavedUser() {
        // Given
        when(userRepository.save(user)).thenReturn(user);

        // When
        UserEntity result = userService.saveUser(user);

        // Then
        assertThat(result).isEqualTo(user);
    }

    @Test
    void whenGetUserByRut_thenReturnUser() {
        // Given
        when(userRepository.findByRut("12345678-9")).thenReturn(user);

        // When
        UserEntity result = userService.getUserByRut("12345678-9");

        // Then
        assertThat(result).isEqualTo(user);
    }

    @Test
    void whenGetUserById_thenReturnUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        UserEntity result = userService.getUserById(1L);

        // Then
        assertThat(result).isEqualTo(user);
    }

    @Test
    void whenDeleteUser_thenReturnTrue() throws Exception {
        // Given
        doNothing().when(userRepository).deleteById(1L);

        // When
        boolean result = userService.deleteUser(1L);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void whenDeleteUserThrowsException_thenCatchBlockExecutes() {
        // Simula un caso donde se lanza una excepción
        doThrow(new RuntimeException("Error al eliminar")).when(userRepository).deleteById(1L);

        Exception exception = assertThrows(Exception.class, () -> userService.deleteUser(1L));

        assertEquals("Error al eliminar", exception.getMessage());
        verify(userRepository, times(1)).deleteById(1L);
    }

}
