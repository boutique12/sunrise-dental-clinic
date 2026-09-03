package com.sunrise.dental.dao;

import com.sunrise.dental.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    Optional<User> findByUsername(String username);

    Optional<User> findById(Long userId);

    List<User> findByRole(String role);

    List<User> findAllActive();
}
