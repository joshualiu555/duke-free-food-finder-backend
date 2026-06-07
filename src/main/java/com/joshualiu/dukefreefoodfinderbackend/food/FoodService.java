package com.joshualiu.dukefreefoodfinderbackend.food;

import com.joshualiu.dukefreefoodfinderbackend.user.User;
import com.joshualiu.dukefreefoodfinderbackend.user.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class FoodService {

    private final FoodRepository repository;
    private final UserService userService;

    public FoodService(FoodRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    public List<Food> getAllPosts() {
        return repository.findByExpiresAtAfter(LocalDateTime.now());
    }

    public Food getPostById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new FoodNotFoundException("Food not found with id: " + id));
    }

    public Food createFood(Food food) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        food.setUser(user);
        return repository.save(food);
    }

    public Food updateFood(Long id, Food updated) {
        Food existing = getPostById(id);
        validateOwner(existing);
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setLatitude(updated.getLatitude());
        existing.setLongitude(updated.getLongitude());
        existing.setLocationDetails(updated.getLocationDetails());
        existing.setActive(updated.isActive());
        existing.setExpiresAt(updated.getExpiresAt());
        return repository.save(existing);
    }

    public void deleteFood(Long id) {
        Food existing = getPostById(id);
        validateOwner(existing);
        repository.deleteById(id);
    }

    private void validateOwner(Food food) {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        if (!food.getUser().getEmail().equals(email)) {
            throw new RuntimeException("You are not authorized to modify this post");
        }
    }
}
