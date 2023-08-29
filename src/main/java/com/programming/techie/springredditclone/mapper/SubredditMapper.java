package com.programming.techie.springredditclone.mapper;

import com.programming.techie.springredditclone.dto.SubredditDto;
import com.programming.techie.springredditclone.model.Post;
import com.programming.techie.springredditclone.model.Subreddit;
import com.programming.techie.springredditclone.model.User;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubredditMapper {

    @Mapping(target = "numberOfPosts",expression = "java(mapPosts(subreddit.getPosts()))")
    @Mapping(target = "username",expression = "java(subreddit.getUser().getUsername())")
    SubredditDto mapSubredditDto(Subreddit subreddit);

    default Integer mapPosts(List<Post> numberOfPosts) {
        return numberOfPosts.size();
    }

    @InheritInverseConfiguration
    @Mapping(target = "posts",ignore = true)
    Subreddit mapDtoToSubreddit(SubredditDto subredditDto, User user);
}
