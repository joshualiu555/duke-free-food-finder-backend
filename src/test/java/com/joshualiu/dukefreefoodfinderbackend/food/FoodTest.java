package com.joshualiu.dukefreefoodfinderbackend.food;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class FoodTest {

    @Test
    void onCreate_setsCreatedAt() {
        Food food = new Food("Pizza", "Leftover", 0.0, 0.0, "Social Sciences");
        assertThat(food.getCreatedAt()).isNull();

        food.onCreate();

        assertThat(food.getCreatedAt()).isNotNull();
    }
}
