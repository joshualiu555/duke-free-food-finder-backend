package com.joshualiu.dukefreefoodfinderbackend.user;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class UserTest {

    @Test
    void onCreate_setsCreatedAt() {
        User user = new User("joshua.liu@duke.edu");
        assertThat(user.getCreatedAt()).isNull();

        user.onCreate();

        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    void constructor_setsEmail() {
        User user = new User("joshua.liu@duke.edu");
        assertThat(user.getEmail()).isEqualTo("joshua.liu@duke.edu");
    }
}
