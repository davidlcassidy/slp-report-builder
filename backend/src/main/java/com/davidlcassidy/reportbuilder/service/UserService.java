package com.davidlcassidy.reportbuilder.service;

import com.davidlcassidy.reportbuilder.model.User;
import com.davidlcassidy.reportbuilder.repository.InterviewRepository;
import com.davidlcassidy.reportbuilder.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
    InterviewRepository interviewRepository;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public String generateAuthenticationToken() {
        int tokenLength = 30;
        StringBuilder sb = new StringBuilder(tokenLength);
        Random random = new Random();
        for (int i = 0; i < tokenLength; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    public String generatePasswordHash(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String hex = Integer.toHexString(0xff & hashByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public User validateUserAuthenticationToken(HttpServletRequest request) throws Exception {
        String authenticationToken = request.getHeader("AuthenticationToken");

        Optional<User> user = userRepository.findByAuthenticationTokenAndAuthenticationTokenExpirationAfter(authenticationToken, ZonedDateTime.now());

        if (user.isEmpty()) {
            throw new Exception("Authentication token has expired or is invalid.");
        }

        return user.get();
    }

    public User validateUserAuthenticationTokenAndInterview(HttpServletRequest request, Long interviewId) throws Exception {
        User user = validateUserAuthenticationToken(request);
        if(interviewRepository.findByUserIdAndId(user.getId(), Long.valueOf(interviewId)) == null) {
            throw new Exception();
        }
        return user;
    }

}