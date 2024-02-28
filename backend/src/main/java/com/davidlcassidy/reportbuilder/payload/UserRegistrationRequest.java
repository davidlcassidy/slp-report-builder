package com.davidlcassidy.reportbuilder.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationRequest {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
}
