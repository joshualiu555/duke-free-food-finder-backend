package com.joshualiu.dukefreefoodfinderbackend.forum;

import com.joshualiu.dukefreefoodfinderbackend.food.Food;
import com.joshualiu.dukefreefoodfinderbackend.user.User;
import com.joshualiu.dukefreefoodfinderbackend.user.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class ForumService {

    private final ForumRepository repository;
    private final UserService userService;

    public ForumService(ForumRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    public List<Forum> getAllForums() {
        return repository.findAll();
    }

    public Forum getForumById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ForumNotFoundException("Forum not found with id: " + id));
    }

    public List<Forum> getAllForumsByFood(Long foodId) {
        Food food = new Food();
        food.setId(foodId);
        return repository.findByFood(food);
    }

    public List<Forum> getAllForumsByUser(Long userId) {
        User user = new User();
        user.setId(userId);
        return repository.findByUser(user);
    }

    public Forum createForum(Forum forum) {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        User user = userService.getUserByEmail(email);
        forum.setUser(user);
        return repository.save(forum);
    }

    public Forum updateForum(Long id, Forum updated) {
        Forum existing = getForumById(id);
        validateOwner(existing);
        existing.setContent(updated.getContent());
        return repository.save(existing);
    }

    public void deleteForum(Long id) {
        Forum existing = getForumById(id);
        validateOwner(existing);
        repository.deleteById(id);
    }

    private void validateOwner(Forum Forum) {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        if (!Forum.getUser().getEmail().equals(email)) {
            throw new RuntimeException("You are not authorized to modify this forum");
        }
    }
}
