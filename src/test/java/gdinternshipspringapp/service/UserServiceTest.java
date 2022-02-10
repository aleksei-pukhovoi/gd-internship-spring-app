package gdinternshipspringapp.service;

import gdinternshipspringapp.model.dto.UserDto;
import gdinternshipspringapp.model.entity.Post;
import gdinternshipspringapp.model.entity.Topic;
import gdinternshipspringapp.model.entity.User;
import gdinternshipspringapp.repository.*;
import gdinternshipspringapp.service.impl.UserServiceImpl;
import gdinternshipspringapp.utils.UserConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserServiceImpl service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PicRepository picRepository;

    @Mock
    private ForumRepository forumRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserConverter converter;

    private UserDto userDto;

    private User user;

    @BeforeEach
    public void setUp() {
        service = Mockito.spy(new UserServiceImpl(userRepository,topicRepository, tagRepository,
                sectionRepository, postRepository, picRepository, forumRepository, commentRepository,converter));
        userDto = new UserDto();
        user = new User();
    }

    @Test
    public void givenUsers_whenFindAll_thenUsersReturned() {
        //given
        when(userRepository.findByOrderByNameAsc()).thenReturn(Collections.singletonList(user));
        when(converter.toUserDtos(anyList())).thenReturn(Collections.singletonList(userDto));

        //when
        int listSize = service.findAll().size();

        //then
        assertThat(listSize).isEqualTo(1);
        verify(userRepository, times(1)).findByOrderByNameAsc();
        verify(converter, times(1)).toUserDtos(anyList());
        verifyNoMoreInteractions(userRepository, converter);
    }

    @Test
    public void givenId_whenFind_thenUserReturned() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(converter.toUserDto(any(User.class))).thenReturn(userDto);

        //when
        service.findUserById(1L);

        //then
        verify(userRepository, times(1)).findById(anyLong());
        verify(converter, times(1)).toUserDto(any(User.class));
        verifyNoMoreInteractions(userRepository, converter);
    }

    @Test
    public void givenUser_whenSave_thenUserReturned() {
        //given
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(converter.toUserDto(any(User.class))).thenReturn(userDto);
        when(converter.toUser(any(UserDto.class))).thenReturn(user);
        when(postRepository.saveAll(any())).thenReturn(Collections.singletonList(new Post()));
        when(topicRepository.saveAll(any())).thenReturn(Collections.singletonList(new Topic()));

        //when
        service.createUser(new UserDto());

        //then
        verify(userRepository, times(1)).save(any(User.class));
        verify(postRepository, times(1)).saveAll(any());
        verify(topicRepository, times(1)).saveAll(any());
        verify(converter, times(1)).toUserDto(any(User.class));
        verify(converter, times(1)).toUser(any(UserDto.class));
        verifyNoMoreInteractions(userRepository, converter, postRepository, topicRepository);
        verifyNoInteractions(picRepository, commentRepository, sectionRepository, forumRepository, tagRepository);
    }

    @Test
    public void givenId_whenUpdate_thenUserReturned() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(converter.toUserDto(any(User.class))).thenReturn(userDto);
        when(converter.toUser(any(UserDto.class))).thenReturn(user);

        //when
        service.updateUser(1L, new UserDto());

        //then
        verify(userRepository, times(1)).findById(anyLong());
        verify(converter, times(1)).toUserDto(any(User.class));
        verify(converter, times(1)).toUser(any(UserDto.class));
        verifyNoMoreInteractions(userRepository, converter);
    }
}