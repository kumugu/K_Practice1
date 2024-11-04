package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api")
public class PostController {

    @Autowired
    private PostrService postrService;

    @PostMapping("/posts")
    public Post createPost(@RequestBody Post post) {
        return postrService.createPost(post.getTitle(), post.getContent());
    }

    @GetMapping("/posts")
    public List<Post> getAllPosts() {
        return postrService.getAllPosts();
    }
}
