package gdinternshipspringapp.unitTest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gdinternshipspringapp.controller.UserController;
import gdinternshipspringapp.exception.ExceptionHandlerAdvice;
import gdinternshipspringapp.exception.ServiceException;
import gdinternshipspringapp.model.dto.UserDto;
import gdinternshipspringapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MimeTypeUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static gdinternshipspringapp.exception.errorCode.UserServiceErrorCode.USER_NOT_EXIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService service;

    @InjectMocks
    private UserController controller;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ExceptionHandlerAdvice())
                .build();
    }

    @Test
    public void givenUsers_whenGet_thenReturnUsersAndStatus200() throws Exception {
        //given
        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setName("Alex");
        user1.setEmail("alex@gmail.com");
        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setName("John");
        user2.setEmail("john@gmail.com");

        List<UserDto> allUsers = Arrays.asList(user1, user2);
        when(service.findAll()).thenReturn(allUsers);

        //when
        mockMvc.perform(get("/users"))

                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(user1.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(user1.getName())))
                .andExpect(jsonPath("$[0].email", is(user1.getEmail())))
                .andExpect(jsonPath("$[1].id", is(user2.getId().intValue())))
                .andExpect(jsonPath("$[1].name", is(user2.getName())))
                .andExpect(jsonPath("$[1].email", is(user2.getEmail())));
        verify(service, VerificationModeFactory.times(1)).findAll();
        verifyNoMoreInteractions(service);
    }

    @Test
    public void givenUser_whenCreate_thenStatus201AndUserReturned() throws Exception {
        //given
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("alex@gmail.com");
        ObjectMapper mapper = new ObjectMapper();
        when(service.createUser(any(UserDto.class))).thenReturn(user);

        //when
        String expectedJsonResult = mapper.writeValueAsString(user);
        MvcResult result = mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expectedJsonResult))

                //then
                .andExpect(status().isCreated())
                .andReturn();
        String actualJsonResponse = result.getResponse().getContentAsString();
        assertThat(actualJsonResponse).isEqualToIgnoringWhitespace(expectedJsonResult);
        verify(service, times(1)).createUser(any((UserDto.class)));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void givenUsers_whenInvalidAcceptHeader_thenNotAcceptableReturned() throws Exception {
        // given
        String invalidAcceptMimeType = MimeTypeUtils.APPLICATION_XML_VALUE;
        UserDto user1 = new UserDto();

        List<UserDto> allUsers = Collections.singletonList(user1);
        when(service.findAll()).thenReturn(allUsers);

        // when
        mockMvc.perform(get("/users").accept(invalidAcceptMimeType))
                // then
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void givenId_whenIsNotExist_thenBadRequestReturned() throws Exception {
        // given
        when(service.findUserById(anyLong())).thenThrow(new ServiceException(USER_NOT_EXIST));

        // when
        mockMvc.perform(get("/users/2"))

                // then
                .andExpect(status().isNotFound());
    }

    @Test
    public void giveUser_whenUpdate_thenStatus200AndUpdatedReturned() throws Exception {
        //given
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("Alex");
        user.setEmail("alex@gmail.com");
        when(service.updateUser(anyLong(), any(UserDto.class))).thenReturn(user);
        ObjectMapper mapper = new ObjectMapper();

        //when
        mockMvc.perform(put("/users/1")
                        .content(mapper.writeValueAsString(new UserDto()))
                        .contentType(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
        verify(service, times(1)).updateUser(anyLong(), any(UserDto.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    public void whenDelete_thenStatus200() throws Exception {
        //given
        doNothing().when(service).deleteUserById(anyLong());

        //when
        mockMvc.perform(delete("/users/1"))

                //then
                .andExpect(status().isOk());
        verify(service, times(1)).deleteUserById(anyLong());
        verifyNoMoreInteractions(service);
    }
}
