package com.joshualiu.dukefreefoodfinderbackend.forum;

import com.joshualiu.dukefreefoodfinderbackend.food.Food;
import com.joshualiu.dukefreefoodfinderbackend.user.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class ForumTest {

    @Test
    void onCreate_setsCreatedAt() {
        User user = new User("joshua.liu@duke.edu");
        Food food = new Food("Pizza", "Leftover", 0.0, 0.0, "Social Sciences", LocalDateTime.now().plusHours(2), user);
        Forum forum = new Forum("What type of pizza?", user, food);
        assertThat(forum.getCreatedAt()).isNull();

        forum.onCreate();

        assertThat(forum.getCreatedAt()).isNotNull();
    }
}