package com.joshualiu.dukefreefoodfinderbackend.food;

import com.joshualiu.dukefreefoodfinderbackend.storage.S3Service;
import com.joshualiu.dukefreefoodfinderbackend.user.User;
import com.joshualiu.dukefreefoodfinderbackend.user.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class FoodService {

    private final FoodRepository repository;
    private final UserService userService;
    private final S3Service s3Service;

    public FoodService(FoodRepository repository, UserService userService, S3Service s3Service) {
        this.repository = repository;
        this.userService = userService;
        this.s3Service = s3Service;
    }

    public List<Food> getAllFoods() {
        return repository.findByExpiresAtAfter(LocalDateTime.now());
    }

    public Food getFoodById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new FoodNotFoundException("Food not found with id: " + id));
    }

    public Food createFood(Food food) {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user = userService.getUserByEmail(email);
        food.setUser(user);
        return repository.save(food);
    }

    public Food updateFood(Long id, Food updated) {
        Food existing = getFoodById(id);
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
        Food existing = getFoodById(id);
        validateOwner(existing);
        repository.deleteById(id);
    }

    private void validateOwner(Food food) {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        if (!food.getUser().getEmail().equals(email)) {
            throw new RuntimeException("You are not authorized to modify this Food");
        }
    }

    public Food uploadImage(Long id, MultipartFile file) throws IOException {
        Food existing = getFoodById(id);
        String imageUrl = s3Service.uploadFile(file);
        existing.setImageUrl(imageUrl);
        return repository.save(existing);
    }
}
