package com.joshualiu.dukefreefoodfinderbackend.forum;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ForumRepository extends JpaRepository<Forum, Long> {
}
