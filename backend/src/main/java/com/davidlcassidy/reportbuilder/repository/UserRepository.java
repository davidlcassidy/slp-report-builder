package com.davidlcassidy.reportbuilder.repository;

import com.davidlcassidy.reportbuilder.model.InterviewAnswer;
import com.davidlcassidy.reportbuilder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
    User findByUsernameAndHashedPassword(String username, String password);
    Optional<User> findByAuthenticationTokenAndAuthenticationTokenExpirationAfter(String token, ZonedDateTime now);
}