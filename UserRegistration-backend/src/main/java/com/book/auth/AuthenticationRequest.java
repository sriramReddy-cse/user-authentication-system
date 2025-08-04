package com.book.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationRequest {

    @NotBlank(message = "Email should not be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password should not be blank")
    @NotEmpty(message = "password should not be empty")
    @Size(min = 8,message = "Password should contain atleast 8 digits")
    private String password;

}
