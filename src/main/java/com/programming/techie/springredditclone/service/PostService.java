package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.PostRequest;
import com.programming.techie.springredditclone.dto.PostResponse;
import com.programming.techie.springredditclone.exceptions.PostNotFoundEXception;
import com.programming.techie.springredditclone.exceptions.SubredditNotFoundException;
import com.programming.techie.springredditclone.exceptions.SubredditNotMappedException;
import com.programming.techie.springredditclone.exceptions.UnAuthorizedException;
import com.programming.techie.springredditclone.mapper.PostMapper;
import com.programming.techie.springredditclone.model.Post;
import com.programming.techie.springredditclone.model.Subreddit;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.repository.PostRepository;
import com.programming.techie.springredditclone.repository.SubredditRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PostService {
    private final SubredditRepository subredditRepository;
    private final AuthService authService;
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final UserRepository userRepository;


    public Post save(PostRequest postRequest) {

        Subreddit subreddit=subredditRepository.findByName(postRequest.getSubredditName())
                .orElseThrow(
                        ()->new SubredditNotFoundException(postRequest.getSubredditName())
                );
        return postRepository.save(postMapper.map(postRequest,subreddit,authService.getCurrentUser()));
    }


    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        Post post=postRepository.findById(id)
                .orElseThrow(()-> new   PostNotFoundEXception(id.toString()));
        return postMapper.mapToDto(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(postMapper::mapToDto)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsBySubreddit(Long subredditid) {

        Subreddit subreddit=subredditRepository.findById(subredditid)
                .orElseThrow(()->new SubredditNotFoundException(subredditid.toString()));

        List<Post> posts=postRepository.findAllBySubreddit(subreddit);
        return posts.stream().map(postMapper::mapToDto).collect(toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUserName(String username) {
        User user=userRepository.findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException(username));
        return postRepository.findByUser(user)
                .stream()
                .map(postMapper::mapToDto)
                .collect(toList());
    }

    public PostResponse update(PostRequest postRequest) {

        Long id=postRequest.getPostId();

        Post post=postRepository.findById(id)
                .orElseThrow(()-> new   PostNotFoundEXception(id.toString()));

        Subreddit subreddit=subredditRepository.findByName(postRequest.getSubredditName())
                .orElseThrow(
                        ()->new SubredditNotFoundException(postRequest.getSubredditName())
                );
        if(!authService.getCurrentUser().getUsername().equals(subreddit.getUser().getUsername())) {
            throw new SubredditNotMappedException(subreddit.getName()+" Sub Reddit is Not Associated With This "+authService.getCurrentUser().getUsername());
        }


        postRepository.save(postMapper.map(postRequest,subreddit,authService.getCurrentUser()));

        Post updatedPost=postRepository.findById(id)
                .orElseThrow(()-> new   PostNotFoundEXception(id.toString()));
        return postMapper.mapToDto(updatedPost);
    }

    public void deletePostById(Long id) {

        Post post=postRepository.findById(id).orElseThrow(()->new PostNotFoundEXception("Unable To Find The post So Cannot Delete It"));
        postRepository.deleteById(id);
    }
}
