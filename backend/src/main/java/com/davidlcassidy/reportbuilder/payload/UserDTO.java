package com.davidlcassidy.reportbuilder.payload;

import com.davidlcassidy.reportbuilder.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String username;
    private String password;

    private String authenticationToken;
    private ZonedDateTime authenticationTokenExpiration;

    private String firstName;
    private String lastName;
    private String position;

    public UserDTO(User user, String authenticationToken) {
        this.username = user.getUsername();
        this.authenticationToken = authenticationToken;
        this.authenticationTokenExpiration = user.getAuthenticationTokenExpiration();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.position = user.getPosition();
    }
}
