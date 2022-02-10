package gdinternshipspringapp.utils;

import gdinternshipspringapp.model.dto.*;
import gdinternshipspringapp.model.entity.*;
import gdinternshipspringapp.utils.impl.UserConverterImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserConverterTest {

    private UserConverter converter;

    @Mock
    private ModelMapper mapper;

    private User user;


    @BeforeEach
    void setUp() {
        converter = Mockito.spy(new UserConverterImpl(mapper));
        user = createUser();
    }

    @Test
    public void givenUser_whenConvert_thenUserReturned() {
        //given
        when(mapper.map(any(UserDto.class), eq(User.class))).thenReturn(user);

        //when
        User convertedUser = converter.toUser(new UserDto());

        //then
        Post post = convertedUser.getPosts().iterator().next();
        Comment comment = convertedUser.getComments().iterator().next();
        Topic topic = convertedUser.getTopics().iterator().next();
        Tag tag = topic.getTags().iterator().next();
        Pic pic = post.getPics().iterator().next();
        Forum forum = topic.getForum();
        Section section = forum.getSection();

        assertThat(post.getUser()).isNotNull();
        assertThat(post.getTopic()).isNotNull();
        assertThat(post.getComments().size()).isEqualTo(1);
        assertThat(topic.getUser()).isNotNull();
        assertThat(topic.getPosts().size()).isEqualTo(1);
        assertThat(comment.getUser()).isNotNull();
        assertThat(comment.getPost()).isNotNull();
        assertThat(pic.getPost()).isNotNull();
        assertThat(tag.getTopics().size()).isEqualTo(1);
        assertThat(forum.getTopics().size()).isEqualTo(1);
        assertThat(section.getForums().size()).isEqualTo(1);

        verify(mapper, times(1)).map(any(UserDto.class), eq(User.class));
        verifyNoMoreInteractions(mapper);
    }

    private User createUser() {
        User user = new User();
        user.setName("Alex");
        user.setLogin("al");
        user.setPassword("asdf");
        user.setEmail("alex@gmail.com");
        user.setRole(Role.USER);
        user.setPosts(new HashSet<>(Collections.singleton(createPost())));
        user.setTopics(new HashSet<>(Collections.singleton(createTopic())));
        user.setComments(new HashSet<>(Collections.singleton(createComment())));
        return user;
    }

    private Topic createTopic() {
        Topic topic = new Topic();
        topic.setName("Identify stone");
        topic.setForum(createForum());
        topic.setTags(new HashSet<>(Collections.singleton(createTag())));
        return topic;
    }

    private Forum createForum() {
        Forum forum = new Forum();
        forum.setName("Sharpening of cutting tool");
        forum.setSection(createSection());
        return forum;
    }

    private Section createSection() {
        Section section = new Section();
        section.setName("Cold steel and projectile weapons");
        return section;
    }

    private Tag createTag() {
        Tag tag = new Tag();
        tag.setName("Washita");
        return tag;
    }

    private Post createPost() {
        Post post = new Post();
        post.setMessage("Help me to identify sharpening stone from ebay");
        post.setDate(LocalDate.now());
        post.setPics(new HashSet<>(Collections.singleton(createPic())));
        return post;
    }

    private Pic createPic() {
        Pic pic = new Pic();
        pic.setCaption("Washita oil stone");
        pic.setImageLink("http://zatochiklinok.ru/wp-content/gallery/washita/0023-lw.JPG");
        return pic;
    }

    private Comment createComment() {
        Comment comment = new Comment();
        comment.setName("First comment");
        return comment;
    }
}