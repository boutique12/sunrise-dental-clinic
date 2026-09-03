package com.sunrise.dental.service.impl;

import com.sunrise.dental.dao.UserDao;
import com.sunrise.dental.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserDao userDao;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userDao);
    }

    @Test
    void authenticateReturnsUserWhenCredentialsValid() {
        String plainPassword = "secure-pass";
        User stored = new User();
        stored.setUsername("reception01");
        stored.setPasswordHash(com.sunrise.dental.util.PasswordUtil.hashPassword(plainPassword));
        stored.setActive(true);
        stored.setRole("RECEPTIONIST");

        when(userDao.findByUsername("reception01")).thenReturn(Optional.of(stored));

        Optional<User> result = authService.authenticate("reception01", plainPassword);

        assertTrue(result.isPresent());
        assertNull(result.get().getPasswordHash());
    }

    @Test
    void authenticateRejectsInactiveUser() {
        String plainPassword = "secure-pass";
        User stored = new User();
        stored.setUsername("reception01");
        stored.setPasswordHash(com.sunrise.dental.util.PasswordUtil.hashPassword(plainPassword));
        stored.setActive(false);

        when(userDao.findByUsername("reception01")).thenReturn(Optional.of(stored));

        assertFalse(authService.authenticate("reception01", plainPassword).isPresent());
    }

    @Test
    void authenticateRejectsBlankCredentials() {
        assertFalse(authService.authenticate("", "password").isPresent());
        assertFalse(authService.authenticate("user", "").isPresent());
    }
}
