package com.joshualiu.dukefreefoodfinderbackend.food;

import com.joshualiu.dukefreefoodfinderbackend.user.User;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

class FoodTest {

    @Test
    void onCreate_setsCreatedAt() {
        User user = new User("joshua.liu@duke.edu");
        Food food = new Food("Pizza", "Leftover", 0.0, 0.0, "Social Sciences",
                LocalDateTime.now().plusHours(2), user);
        assertThat(food.getCreatedAt()).isNull();

        food.onCreate();

        assertThat(food.getCreatedAt()).isNotNull();
    }
}
