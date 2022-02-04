package gdinternshipspringapp.controller;

import gdinternshipspringapp.model.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import gdinternshipspringapp.service.UserService;

import java.util.List;

@RestController
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsers() {
        return service.findAll();
    }

    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserDto(@PathVariable Long id) {
        return service.findUserById(id);
    }

    @PutMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserDto user) {
        return service.updateUser(id, user);
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody UserDto user) {
        return service.createUser(user);
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void DeleteUser(@PathVariable Long id) {
        service.deleteUserById(id);
    }

}
