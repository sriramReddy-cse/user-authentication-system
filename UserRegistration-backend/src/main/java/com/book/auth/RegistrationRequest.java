package com.book.auth;


import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class RegistrationRequest {

    @NotBlank(message = "Firstname should not be blank")
    @NotEmpty(message = "firstname should not be empty")
    private String firstname;

    @NotBlank(message = "Lastname should not be blank")
    @NotEmpty(message = "lastname should not be empty")
    private String lastname;

    @NotBlank(message = "Password should not be blank")
    @NotEmpty(message = "password should not be empty")
    @Size(min = 8,message = "Password should contain atleast 8 digits")
    private String password;

    @NotBlank(message = "Email should not be blank")
    @Email(message = "Invalid email format")
    private String email;

}
