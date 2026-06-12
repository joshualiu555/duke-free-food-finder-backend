package com.joshualiu.dukefreefoodfinderbackend.forum;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    private final ForumService service;

    public ForumController(ForumService service) {
        this.service = service;
    }

    @GetMapping
    public List<Forum> getAll() {
        return service.getAllForums();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Forum> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getForumById(id));
    }

    @PostMapping
    public ResponseEntity<Forum> create(@RequestBody Forum Forum) {
        return ResponseEntity.ok(service.createForum(Forum));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Forum> update(@PathVariable Long id, @RequestBody Forum Forum) {
        return ResponseEntity.ok(service.updateForum(id, Forum));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteForum(id);
        return ResponseEntity.noContent().build();
    }
}
