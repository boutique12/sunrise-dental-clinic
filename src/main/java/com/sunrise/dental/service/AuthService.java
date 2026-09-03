package com.sunrise.dental.service;

import com.sunrise.dental.model.User;

import java.util.Optional;

public interface AuthService {

    Optional<User> authenticate(String username, String password);
}
