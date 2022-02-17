package gdinternshipspringapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import gdinternshipspringapp.model.dto.UserDto;
import gdinternshipspringapp.model.entity.*;
import gdinternshipspringapp.service.UserService;
import gdinternshipspringapp.converter.UserConverter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;

@SpringBootApplication
public class GdInternshipSpringAppApplication implements CommandLineRunner {

	private final UserService service;

	private final UserConverter converter;

	public GdInternshipSpringAppApplication(UserService service, UserConverter converter) {
		this.service = service;
		this.converter = converter;
	}

	public static void main(String[] args) {
		SpringApplication.run(GdInternshipSpringAppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Post post = createPost();
		Pic pic = createPic();
		post.setPics(new HashSet<>(Collections.singleton(pic)));
		pic.setPost(post);
		Comment comment = createComment();
		comment.setPost(post);
		post.setComments(new HashSet<>(Collections.singleton(comment)));

		Topic topic = createTopic();
		Tag tag = createTag();
		tag.setTopics(new HashSet<>(Collections.singleton(topic)));
		topic.setTags(new HashSet<>(Collections.singleton(tag)));
		post.setTopic(topic);
		topic.setPosts(new HashSet<>(Collections.singleton(post)));

		Section section = createSection();
		Forum forum = createForum();
		section.setForums(new HashSet<>(Collections.singleton(forum)));
		forum.setSection(section);
		forum.setTopics(new HashSet<>(Collections.singleton(topic)));
		topic.setForum(forum);

		User user = createNewUser();
		user.setPosts(new HashSet<>(Collections.singleton(post)));
		post.setUser(user);
		topic.setUser(user);
		user.setTopics(new HashSet<>(Collections.singleton(topic)));
		user.setComments(new HashSet<>(Collections.singleton(comment)));
		comment.setUser(user);
		UserDto userDto = converter.toUserDto(user);
		service.createUser(userDto);
	}

	private User createNewUser() {
		User user = new User();
		user.setName("Alex");
		user.setLogin("al");
		user.setPassword("asdf");
		user.setEmail("alex@gmail.com");
		user.setRole(Role.USER);
		return user;
	}

	private Section createSection() {
		Section section = new Section();
		section.setName("Cold steel and projectile weapons");
		return section;
	}

	private Forum createForum(){
		Forum forum  = new Forum();
		forum.setName("Sharpening of cutting tool");
		return forum;
	}

	private Topic createTopic() {
		Topic topic = new Topic();
		topic.setName("Identify stone");
		return topic;
	}

	private Tag createTag() {
		Tag tag = new Tag();
		tag.setName("Washita");
		return tag;
	}

	private Pic createPic() {
		Pic pic = new Pic();
		pic.setCaption("Washita oil stone");
		pic.setImageLink("http://zatochiklinok.ru/wp-content/gallery/washita/0023-lw.JPG");
		return pic;
	}

	private Post createPost(){
		Post post = new Post();
		post.setMessage("Help me to identify sharpening stone from ebay");
		post.setDate(LocalDate.now());
		return post;
	}

	private Comment createComment() {
		Comment comment = new Comment();
		comment.setName("First comment");
		return comment;
	}
}
