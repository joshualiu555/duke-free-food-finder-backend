package com.joshualiu.dukefreefoodfinderbackend.food;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/food-posts")
public class FoodController {

    private final FoodService service;

    public FoodController(FoodService fs) {
        this.service = fs;
    }

    @GetMapping
    public List<Food> getAll() {
        return service.getAllPosts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Food> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getPostById(id));
    }

    @PostMapping
    public ResponseEntity<Food> create(@RequestBody Food food) {
        return ResponseEntity.ok(service.createFood(food));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Food> update(@PathVariable Long id, @RequestBody Food food) {
        return ResponseEntity.ok(service.updateFood(id, food));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteFood(id);
        return ResponseEntity.noContent().build();
    }
}
