package com.joshualiu.dukefreefoodfinderbackend.user;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User getUserById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    public User getUserByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    public User createUser(String email) {
        if (!email.endsWith("@duke.edu")) {
            throw new IllegalArgumentException("Only @duke.edu emails are allowed");
        }
        return repository.findByEmail(email)
                .orElseGet(() -> repository.save(new User(email)));
    }
}