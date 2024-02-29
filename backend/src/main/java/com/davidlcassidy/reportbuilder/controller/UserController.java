package com.davidlcassidy.reportbuilder.controller;

import com.davidlcassidy.reportbuilder.model.User;
import com.davidlcassidy.reportbuilder.payload.UserDTO;
import com.davidlcassidy.reportbuilder.payload.UserLoginRequest;
import com.davidlcassidy.reportbuilder.payload.UserRegistrationRequest;
import com.davidlcassidy.reportbuilder.payload.UserUpdateRequest;
import com.davidlcassidy.reportbuilder.repository.UserRepository;
import com.davidlcassidy.reportbuilder.service.InterviewService;
import com.davidlcassidy.reportbuilder.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
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

    @Autowired
    InterviewService interviewService;

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody UserLoginRequest requestBody) {

        ZonedDateTime currentDateTime = ZonedDateTime.now();

        String hashedPassword;
        String authenticationToken;
        String hashedAuthenticationToken;
        try {
            hashedPassword = userService.generateHashValue(requestBody.getPassword());
            authenticationToken = userService.generateAuthenticationToken();
            hashedAuthenticationToken = userService.generateHashValue(authenticationToken);
        } catch (NoSuchAlgorithmException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        User user = userRepository.findByUsernameAndHashedPassword(requestBody.getUsername(), hashedPassword);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        user.setHashedAuthenticationToken(hashedAuthenticationToken);
        user.setAuthenticationTokenExpiration(currentDateTime.plusDays(1));

        userRepository.save(user);
        UserDTO response = new UserDTO(user, authenticationToken);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/registration")
    public ResponseEntity<UserDTO> registerNewUser(@RequestBody UserRegistrationRequest requestBody) {
        if (userRepository.findByUsername(requestBody.getUsername()) != null ||
                requestBody.getUsername().isBlank() || requestBody.getPassword().isBlank()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ZonedDateTime currentDateTime = ZonedDateTime.now();

        String hashedPassword;
        String authenticationToken;
        String hashedAuthenticationToken;
        try {
            hashedPassword = userService.generateHashValue(requestBody.getPassword());
            authenticationToken = userService.generateAuthenticationToken();
            hashedAuthenticationToken = userService.generateHashValue(authenticationToken);
        } catch (NoSuchAlgorithmException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        User newUser = User.builder()
                .username(requestBody.getUsername())
                .hashedPassword(hashedPassword)
                .hashedAuthenticationToken(hashedAuthenticationToken)
                .authenticationTokenExpiration(currentDateTime.plusDays(1))
                .firstName(requestBody.getFirstName())
                .lastName(requestBody.getLastName())
                .position(requestBody.getPosition())
                .build();

        User user = userRepository.save(newUser);
        UserDTO response = new UserDTO(user, authenticationToken);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/update")
    public ResponseEntity<UserDTO> updateUserDetails(
            HttpServletRequest request,
            @RequestBody UserUpdateRequest requestBody) {
        User user;
        try {
            user = userService.validateUserAuthenticationToken(request);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            user = userService.updateUserInformation(user, requestBody);
        } catch (NoSuchAlgorithmException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        UserDTO response = new UserDTO(user, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping("")
    public ResponseEntity<Void> deleteUserAndAssociatedData(HttpServletRequest request) {
        User user;
        try {
            user = userService.validateUserAuthenticationToken(request);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        interviewService.deleteInterviewsByUserId(user.getId());
        userRepository.deleteById(String.valueOf(user.getId()));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
