package com.booking.service;

import com.booking.entity.User;
import com.booking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setRole(User.Role.USER);
    }

    @Test
    void testCreateUser() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password123");
        newUser.setName("New User");
        newUser.setEmail("new@example.com");
        newUser.setRole(User.Role.USER);

        User created = userService.createUser(newUser);

        assertNotNull(created);
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_DuplicateUsername_ShouldFail() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setPassword("password123");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(newUser);
        });

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testPasswordEncryption() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("plainPassword");
        user.setName("Test");
        user.setEmail("test@test.com");
        user.setRole(User.Role.USER);

        userService.createUser(user);

        verify(passwordEncoder, times(1)).encode("plainPassword");
    }

    @Test
    void testGetUserByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        Optional<User> found = userService.getUserByUsername("testuser");

        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testValidatePassword() {
        when(passwordEncoder.matches("plainPassword", "encodedPassword")).thenReturn(true);

        boolean isValid = userService.validatePassword("plainPassword", "encodedPassword");

        assertTrue(isValid);
        verify(passwordEncoder, times(1)).matches("plainPassword", "encodedPassword");
    }
}
