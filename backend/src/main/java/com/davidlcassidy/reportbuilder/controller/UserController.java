package com.davidlcassidy.reportbuilder.controller;

import com.davidlcassidy.reportbuilder.model.User;
import com.davidlcassidy.reportbuilder.payload.UserLoginRequest;
import com.davidlcassidy.reportbuilder.payload.UserRegistrationRequest;
import com.davidlcassidy.reportbuilder.payload.UserUpdateRequest;
import com.davidlcassidy.reportbuilder.repository.UserRepository;
import com.davidlcassidy.reportbuilder.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;

@RestController
@Tag(name = "User Controller", description = "")
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody UserLoginRequest requestBody) {

        ZonedDateTime currentDateTime = ZonedDateTime.now();

        String passwordHash;
        try {
            passwordHash = userService.generatePasswordHash(requestBody.getPassword());
        } catch (NoSuchAlgorithmException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        User user = userRepository.findByUsernameAndHashedPassword(requestBody.getUsername(), passwordHash);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        user.setAuthenticationToken(userService.generateAuthenticationToken());
        user.setAuthenticationTokenExpiration(currentDateTime.plusDays(1));

        userRepository.save(user);

        return new ResponseEntity<>(user, HttpStatus.OK);

    }

    @PostMapping("/registration")
    public ResponseEntity<User> registerNewUser(@RequestBody UserRegistrationRequest requestBody) {
        if (userRepository.findByUsername(requestBody.getUsername()) != null ||
                requestBody.getUsername().isBlank() || requestBody.getPassword().isBlank()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ZonedDateTime currentDateTime = ZonedDateTime.now();

        String passwordHash;
        try {
            passwordHash = userService.generatePasswordHash(requestBody.getPassword());
        } catch (NoSuchAlgorithmException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        User newUser = User.builder()
                .username(requestBody.getUsername())
                .hashedPassword(passwordHash)
                .authenticationToken(userService.generateAuthenticationToken())
                .authenticationTokenExpiration(currentDateTime.plusDays(1))
                .firstName(requestBody.getFirstName())
                .lastName(requestBody.getLastName())
                .build();

        User user = userRepository.save(newUser);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<User> updateUserDetails(
            HttpServletRequest request,
            @RequestBody UserUpdateRequest requestBody) {
        User user;
        try {
            user = userService.validateUserAuthenticationToken(request);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String passwordHash;
        try {
            passwordHash = userService.generatePasswordHash(requestBody.getPassword());
        } catch (NoSuchAlgorithmException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        user.setHashedPassword(passwordHash);
        user.setFirstName(requestBody.getFirstName());
        user.setLastName(requestBody.getLastName());
        User updatedUser = userRepository.save(user);

        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

}
