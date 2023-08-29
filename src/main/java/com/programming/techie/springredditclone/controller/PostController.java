package com.programming.techie.springredditclone.controller;

import com.programming.techie.springredditclone.dto.PostRequest;
import com.programming.techie.springredditclone.dto.PostResponse;
import com.programming.techie.springredditclone.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
public class PostController {
    private final PostService postservice;

    @PostMapping
    public ResponseEntity<Void> createPost(@RequestBody PostRequest postRequest) {

        postservice.save(postRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        return status(HttpStatus.OK).body(postservice.getPost(id));
    }

    @PutMapping("/update")
    public ResponseEntity<PostResponse> updatePost(@RequestBody PostRequest postRequest) {

        System.out.println(postRequest);

        return status(HttpStatus.OK).body(postservice.update(postRequest));
    }

    @GetMapping("/")
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return status(HttpStatus.OK).body(postservice.getAllPosts());
    }

    @GetMapping("/by-subreddit/{id}")
    public ResponseEntity<List<PostResponse>> getPostsBySubreddit(@PathVariable Long id) {
        return status(HttpStatus.OK).body(postservice.getPostsBySubreddit(id));
    }

    @GetMapping("/by-user/{name}")
    public ResponseEntity<List<PostResponse>> getPostsByUserName(@PathVariable String name) {

        return status(HttpStatus.OK).body(postservice.getPostsByUserName(name));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePostById(@PathVariable Long id) {
        postservice.deletePostById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
