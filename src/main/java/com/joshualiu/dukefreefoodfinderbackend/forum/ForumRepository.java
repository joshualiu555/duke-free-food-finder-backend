package com.joshualiu.dukefreefoodfinderbackend.forum;

import com.joshualiu.dukefreefoodfinderbackend.food.Food;
import com.joshualiu.dukefreefoodfinderbackend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ForumRepository extends JpaRepository<Forum, Long> {
    List<Forum> findByFood(Food food);
    List<Forum> findByUser(User user);
    List<Forum> findByUserAndFoodExpiresAtAfter(User user, LocalDateTime now);
}
