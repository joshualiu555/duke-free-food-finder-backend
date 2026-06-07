package com.joshualiu.dukefreefoodfinderbackend.food;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findByExpiresAtAfter(LocalDateTime now);
}
