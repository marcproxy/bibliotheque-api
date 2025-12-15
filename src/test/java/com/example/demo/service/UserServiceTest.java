package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.UserStatus;
import com.example.demo.entity.PasswordHistory;
import com.example.demo.repository.IUserRepository;
import com.example.demo.repository.IPasswordHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du Service User")
class UserServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IEmailService emailService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private IPasswordHistoryRepository passwordHistoryRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("hashedPassword");
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setStatus(UserStatus.INACTIF);
        user.setSecurityQuestion("Quelle est votre ville de naissance ?");
        user.setSecurityAnswer("hashedAnswer");
        user.setPasswordLastUpdated(LocalDateTime.now());
    }

    @Test
    @DisplayName("Devrait enregistrer un nouvel utilisateur")
    void testRegisterUser() throws Exception {
        // Arrange
        User newUser = new User();
        newUser.setEmail("new@example.com");
        newUser.setPassword("Password123!");
        newUser.setFirstname("Jane");
        newUser.setLastname("Smith");
        newUser.setSecurityQuestion("Quelle est votre ville de naissance ?");
        newUser.setSecurityAnswer("Paris");

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("hashedPassword");
        when(passwordEncoder.encode("paris")).thenReturn("hashedAnswer");
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        doNothing().when(emailService).sendActivationEmail(any(User.class));

        // Act
        userService.registerUser(newUser);

        // Assert
        verify(userRepository, times(1)).existsByEmail("new@example.com");
        verify(passwordEncoder, times(2)).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendActivationEmail(any(User.class));
    }

    @Test
    @DisplayName("Devrait lever une exception si email existe déjà")
    void testRegisterUser_EmailExists() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            userService.registerUser(user);
        });

        assertTrue(exception.getMessage().contains("existe déjà"));
        verify(userRepository, times(1)).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Devrait activer un utilisateur")
    void testActivateUser() throws Exception {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(emailService).sendActivationConfirmationEmail(any(User.class));

        // Act
        userService.activateUser("test@example.com");

        // Assert
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendActivationConfirmationEmail(any(User.class));
    }

    @Test
    @DisplayName("Devrait authentifier un utilisateur valide")
    void testAuthenticateUser_Success() throws Exception {
        // Arrange
        user.setStatus(UserStatus.ACTIF);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123!", "hashedPassword")).thenReturn(true);

        // Act
        boolean result = userService.authenticateUser("test@example.com", "Password123!");

        // Assert
        assertTrue(result);
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordEncoder, times(1)).matches("Password123!", "hashedPassword");
    }

    @Test
    @DisplayName("Devrait rejeter l'authentification avec mauvais mot de passe")
    void testAuthenticateUser_WrongPassword() {
        // Arrange
        user.setStatus(UserStatus.ACTIF);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPassword", "hashedPassword")).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            userService.authenticateUser("test@example.com", "WrongPassword");
        });

        assertTrue(exception.getMessage().contains("incorrect"));
    }

    @Test
    @DisplayName("Devrait rejeter l'authentification si compte inactif")
    void testAuthenticateUser_InactiveAccount() {
        // Arrange
        user.setStatus(UserStatus.INACTIF);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123!", "hashedPassword")).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            userService.authenticateUser("test@example.com", "Password123!");
        });

        assertTrue(exception.getMessage().contains("pas encore activé"));
    }

    @Test
    @DisplayName("Devrait mettre à jour le profil utilisateur")
    void testUpdateProfile() throws Exception {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.updateProfile("test@example.com", "Jane", "Smith");

        // Assert
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Devrait mettre à jour le mot de passe")
    void testUpdatePassword() throws Exception {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("OldPassword", "hashedPassword")).thenReturn(true);
        when(passwordEncoder.encode("NewPassword")).thenReturn("newHashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.updatePassword("test@example.com", "OldPassword", "NewPassword");

        // Assert
        verify(passwordEncoder, times(1)).matches("OldPassword", "hashedPassword");
        verify(passwordEncoder, times(1)).encode("NewPassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Devrait vérifier si le mot de passe est expiré")
    void testIsPasswordExpired() throws Exception {
        // Arrange
        user.setPasswordLastUpdated(LocalDateTime.now().minusWeeks(13)); // 13 semaines = expiré
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        boolean result = userService.isPasswordExpired("test@example.com");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Mot de passe ne devrait pas être expiré si mis à jour récemment")
    void testIsPasswordNotExpired() throws Exception {
        // Arrange
        user.setPasswordLastUpdated(LocalDateTime.now().minusWeeks(5)); // 5 semaines = non expiré
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        boolean result = userService.isPasswordExpired("test@example.com");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Devrait récupérer la question de sécurité")
    void testGetSecurityQuestion() throws Exception {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        String result = userService.getSecurityQuestion("test@example.com");

        // Assert
        assertEquals("Quelle est votre ville de naissance ?", result);
    }

    @Test
    @DisplayName("Devrait vérifier la réponse de sécurité correcte")
    void testVerifySecurityAnswer_Correct() throws Exception {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("paris", "hashedAnswer")).thenReturn(true);

        // Act
        boolean result = userService.verifySecurityAnswer("test@example.com", "Paris");

        // Assert
        assertTrue(result);
    }
}