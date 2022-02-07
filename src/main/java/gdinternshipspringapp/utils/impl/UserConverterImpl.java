package gdinternshipspringapp.utils.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import gdinternshipspringapp.model.dto.*;
import gdinternshipspringapp.model.entity.*;
import gdinternshipspringapp.utils.UserConverter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserConverterImpl implements UserConverter {

    private final ModelMapper modelMapper;

    public UserConverterImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDto toUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public List<UserDto> toUserDtos(List<User> users) {
        return users.stream()
                .map(this::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public User toUser(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        Set<Comment> comments = user.getComments();
        comments.forEach(comment -> comment.setUser(user));
        Set<Post> posts = user.getPosts();
        posts.forEach(post -> {
            post.setUser(user);
            post.getComments().addAll(comments);
            post.getPics().forEach(pic -> pic.setPost(post));
        });
        Set<Topic> topics = user.getTopics();
        topics.forEach(topic -> {
            topic.getTags().forEach(tag -> tag.getTopics().add(topic));
            topic.setUser(user);
            topic.getPosts().addAll(posts);
            posts.forEach(post -> {
                post.setTopic(topic);
                comments.forEach(comment -> comment.setPost(post));
            });
            Forum forum = topic.getForum();
            forum.getTopics().add(topic);
            Section section = forum.getSection();
            section.getForums().add(forum);
        });
        return user;
    }

    @Override
    public List<User> toUsers(List<UserDto> userDtos) {
        return userDtos.stream()
                .map(this::toUser)
                .collect(Collectors.toList());
    }
}
