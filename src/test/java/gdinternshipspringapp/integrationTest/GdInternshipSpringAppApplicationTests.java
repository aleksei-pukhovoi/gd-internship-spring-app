package gdinternshipspringapp.integrationTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gdinternshipspringapp.GdInternshipSpringAppApplication;
import gdinternshipspringapp.controller.UserController;
import gdinternshipspringapp.model.dto.UserDto;
import gdinternshipspringapp.model.entity.*;
import gdinternshipspringapp.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GdInternshipSpringAppApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GdInternshipSpringAppApplicationTests {

    private UserDto userDto;

    private UserDto savedDto;

    private User user;

    @LocalServerPort
    private int port;

    @Autowired
    private UserController controller;

    @Autowired
    private UserService service;

    private ObjectMapper mapper;

    private TestRestTemplate restTemplate;

    private HttpHeaders requestHeaders;

    @Value("${grant.type}")
    private String grantType;

    @Value("${client.id}")
    private String clientId;

    @Value("${url}")
    private String keycloakUrl;

    @BeforeEach
    void setUp() throws IOException {
        restTemplate = new TestRestTemplate();
        requestHeaders = new HttpHeaders();
        requestHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        userDto = mapper.readValue(new File("src/test/resources/user-dto.json"), UserDto.class);
    }

    @AfterEach
    public void clean() {
        List<Long> ids = service.findAll().stream().map(UserDto::getId).collect(Collectors.toList());
        ids.stream()
                .skip(1) //one entity is added to the database when application runs
                .forEach(id -> service.deleteUserById(id));
    }

    @Test
    public void contextLoads() {
        assertThat(controller).isNotNull();
        assertThat(service).isNotNull();
    }

    @Test
    public void givenUsers_whenGet_thenReturnUserListAndStatus200() throws JsonProcessingException {
        //given
        requestHeaders.add(
                "Authorization",
                "Bearer " + generateToken("user1", "A123a!"));
        savedDto = service.createUser(userDto);
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestHeaders);
        ParameterizedTypeReference<List<UserDto>> parameterizedTypeReference =
                new ParameterizedTypeReference<>() {
                };
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(port)
                .path("/users")
                .build();

        //when
        ResponseEntity<List<UserDto>> response = restTemplate.exchange(
                uriComponents.toUri(),
                HttpMethod.GET,
                requestEntity,
                parameterizedTypeReference
        );

        // then
        List<UserDto> actualUserList = response.getBody();
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(response.getHeaders()
                .getFirst("Content-Type")).isEqualToIgnoringCase("application/json");

        //one more new entity adds to database when app runs
        assertThat(actualUserList.size()).isEqualTo(2);
    }

    @Test
    public void givenId_whenGet_thenReturnUserAndStatus200() throws JsonProcessingException {
        //given
        requestHeaders.add(
                "Authorization",
                "Bearer " + generateToken("user1", "A123a!"));
        savedDto = service.createUser(userDto);
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestHeaders);
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(port)
                .pathSegment("users")
                .path("{id}")
                .buildAndExpand(savedDto.getId());

        //when
        ResponseEntity<UserDto> response = restTemplate.exchange(
                uriComponents.toUri(),
                HttpMethod.GET,
                requestEntity,
                UserDto.class
        );

        // then
        UserDto actualUser = response.getBody();
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(response.getHeaders()
                .getFirst("Content-Type")).isEqualToIgnoringCase("application/json");
        assertThat(actualUser.getName()).isEqualTo("Ivan");
        assertThat(actualUser.getEmail()).isEqualTo("ivan@gmail.com");
        assertThat(actualUser.getRole()).isEqualByComparingTo(Role.USER);
        assertThat(actualUser.getPosts().size()).isEqualTo(1);
        assertThat(actualUser.getComments().size()).isEqualTo(1);
        assertThat(actualUser.getTopics().size()).isEqualTo(1);
    }

    @Test
    public void givenUser_whenCreate_thenReturnUserAndStatus201() {
        //given
        HttpEntity<Object> requestEntity = new HttpEntity<>(userDto, requestHeaders);
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(port)
                .path("/")
                .build();

        //when
        ResponseEntity<UserDto> response = restTemplate.exchange(
                uriComponents.toUri(),
                HttpMethod.POST,
                requestEntity,
                UserDto.class
        );

        // then
        UserDto actualUser = response.getBody();
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.CREATED);
        assertThat(response.getHeaders()
                .getFirst("Content-Type")).isEqualToIgnoringCase("application/json");
        assertThat(actualUser.getName()).isEqualTo("Ivan");
        assertThat(actualUser.getEmail()).isEqualTo("ivan@gmail.com");
        assertThat(actualUser.getRole()).isEqualByComparingTo(Role.USER);
        assertThat(actualUser.getPosts().size()).isEqualTo(1);
        assertThat(actualUser.getComments().size()).isEqualTo(1);
        assertThat(actualUser.getTopics().size()).isEqualTo(1);
    }

    private String generateToken(String username, String password) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", grantType);
        map.add("client_id", clientId);
        map.add("username", username);
        map.add("password", password);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                keycloakUrl,
                HttpMethod.POST,
                entity,
                String.class
        );
        return Objects.requireNonNull(response.getBody())
                .contains("access_token") ? mapper.readTree(response.getBody()).get("access_token").asText() : "";
    }
}
