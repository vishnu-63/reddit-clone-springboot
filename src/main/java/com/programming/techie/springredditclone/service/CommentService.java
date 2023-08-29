package com.programming.techie.springredditclone.service;

import com.programming.techie.springredditclone.dto.CommentsDto;
import com.programming.techie.springredditclone.exceptions.PostNotFoundEXception;
import com.programming.techie.springredditclone.mapper.CommentMapper;
import com.programming.techie.springredditclone.model.Comment;
import com.programming.techie.springredditclone.model.NotificationEmail;
import com.programming.techie.springredditclone.model.Post;
import com.programming.techie.springredditclone.model.User;
import com.programming.techie.springredditclone.repository.CommentRepository;
import com.programming.techie.springredditclone.repository.PostRepository;
import com.programming.techie.springredditclone.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Service
@AllArgsConstructor
public class CommentService {
    private static final String POST_URL = "";
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final MailService mailService;
    private final MailContentBuilder mailContentBuilder;
    public void save(CommentsDto commentsDto) {
        Post post= postRepository.findById(commentsDto.getPostId())
                .orElseThrow(()->new PostNotFoundEXception(commentsDto.getPostId().toString()));
        Comment comment=commentMapper.map(commentsDto,post,authService.getCurrentUser());
        commentRepository.save(comment);
        String message=mailContentBuilder.build(post.getUser().getUsername()+" Commented On Your Post"+ POST_URL);
        sendCommentNotification(message,post.getUser());

    }

    private void sendCommentNotification(String message, User user) {
        mailService.sendMail(new NotificationEmail(user.getUsername()+" Commented On Your Post",user.getEmail(),message));
    }


    public List<CommentsDto> getAllCommentsForPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()->new PostNotFoundEXception(postId+" Not Found"));
        return commentRepository.findByPost(post)
                .stream()
                .map(commentMapper::mapToDto)
                .collect(toList());

    }

    public List<CommentsDto> getAllCommentsForUser(String userName) {
        User user=userRepository.findByUsername(userName)
                .orElseThrow(()->new UsernameNotFoundException(userName));
        return commentRepository.findAllByUser(user)
                .stream()
                .map(commentMapper::mapToDto)
                .collect(toList());
    }
}
