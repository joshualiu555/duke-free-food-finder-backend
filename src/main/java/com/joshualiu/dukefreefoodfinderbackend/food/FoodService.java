package com.joshualiu.dukefreefoodfinderbackend.food;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FoodService {
    private final FoodRepository repository;

    public FoodService(FoodRepository fr) {
        this.repository = fr;
    }

    public List<Food> getAllPosts() {
        return repository.findAll();
    }

    public Food getPostById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new FoodNotFoundException("Food not found with id: " + id));
    }

    public Food createFood(Food Food) {
        return repository.save(Food);
    }

    public Food updateFood(Long id, Food updated) {
        Food existing = getPostById(id);
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setLatitude(updated.getLatitude());
        existing.setLongitude(updated.getLongitude());
        existing.setLocationDetails(updated.getLocationDetails());
        existing.setActive(updated.isActive());
        return repository.save(existing);
    }

    public void deleteFood(Long id) {
        repository.deleteById(id);
    }
}
