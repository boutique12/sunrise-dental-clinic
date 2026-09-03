package com.sunrise.dental.service.impl;

import com.sunrise.dental.dao.UserDao;
import com.sunrise.dental.model.User;
import com.sunrise.dental.service.AuthService;
import com.sunrise.dental.util.PasswordUtil;

import java.util.Optional;

public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;

    public AuthServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Optional<User> authenticate(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return Optional.empty();
        }

        return userDao.findByUsername(username.trim())
                .filter(User::isActive)
                .filter(user -> PasswordUtil.verifyPassword(password, user.getPasswordHash()))
                .map(this::sanitize);
    }

    private User sanitize(User user) {
        user.setPasswordHash(null);
        return user;
    }
}
