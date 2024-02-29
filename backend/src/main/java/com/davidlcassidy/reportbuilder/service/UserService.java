package com.davidlcassidy.reportbuilder.service;

import com.davidlcassidy.reportbuilder.model.User;
import com.davidlcassidy.reportbuilder.payload.UserDTO;
import com.davidlcassidy.reportbuilder.payload.UserUpdateRequest;
import com.davidlcassidy.reportbuilder.repository.InterviewRepository;
import com.davidlcassidy.reportbuilder.repository.UserRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    InterviewService interviewService;

    @Autowired
    InterviewRepository interviewRepository;

    Random random = new Random();

    public User validateUserAuthenticationToken(HttpServletRequest request) throws Exception {
        String authenticationToken = request.getHeader("AuthenticationToken");
        String hashedAuthenticationToken = generateHashValue(authenticationToken);

        Optional<User> user = userRepository.findByHashedAuthenticationTokenAndAuthenticationTokenExpirationAfter(hashedAuthenticationToken, ZonedDateTime.now());

        if (user.isEmpty()) {
            throw new Exception("Authentication token has expired or is invalid.");
        }

        return user.get();
    }

    public User validateUserAuthenticationTokenAndInterview(HttpServletRequest request, Long interviewId) throws Exception {
        User user = validateUserAuthenticationToken(request);
        if(interviewRepository.findByUserIdAndId(user.getId(), interviewId) == null) {
            throw new Exception();
        }
        return user;
    }

    public User updateUserInformation(User user, UserUpdateRequest requestBody) throws NoSuchAlgorithmException {
        if (!requestBody.getUsername().isBlank()) {
            user.setUsername(requestBody.getUsername());
        }
        if (!requestBody.getPassword().isBlank()) {
            String hashedPassport;
            hashedPassport = generateHashValue(requestBody.getPassword());
            user.setHashedPassword(hashedPassport);
        }
        if (!requestBody.getFirstName().isBlank()) {
            user.setFirstName(requestBody.getFirstName());
        }
        if (!requestBody.getLastName().isBlank()) {
            user.setLastName(requestBody.getLastName());
        }
        if (!requestBody.getPosition().isBlank()) {
            user.setPosition(requestBody.getPosition());
        }
        userRepository.save(user);
        return user;
    }

    public void deleteUserAndAssociatedData(String userId) {
        interviewService.deleteInterviewsByUserId(Long.valueOf(userId));
        userRepository.deleteById(userId);
    }

    public String generateAuthenticationToken() throws NoSuchAlgorithmException {
        int tokenLength = 50;
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        StringBuilder sb = new StringBuilder(tokenLength);
        for (int i = 0; i < tokenLength; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    public String generateHashValue(String unhashedValue) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(unhashedValue.getBytes());
        StringBuilder hashedValue = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String hex = Integer.toHexString(0xff & hashByte);
            if (hex.length() == 1) {
                hashedValue.append('0');
            }
            hashedValue.append(hex);
        }
        return hashedValue.toString();
    }

}