package com.book.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth") // Base path for this controller
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication and account management")
@Slf4j
public class AuthenticationController {

      private final AuthenticationService authenticationService;

      @PostMapping("/register")
      @ResponseStatus(HttpStatus.ACCEPTED)
      @Operation(
              summary = "Register a new user",
              description = "Registers a new user and sends an account activation email."
      )
      @ApiResponses({
              @ApiResponse(responseCode = "202", description = "Registration successful"),
              @ApiResponse(responseCode = "400", description = "Invalid registration details"),
              @ApiResponse(responseCode = "500", description = "Server error")
      })
      public ResponseEntity<?> register(
              @RequestBody @Valid RegistrationRequest request
      ) throws MessagingException {
            log.info(request.toString());
            authenticationService.register(request);
            return ResponseEntity.accepted().build();
      }

      @PostMapping("/authenticate")
      @Operation(
              summary = "Authenticate a user",
              description = "Authenticates a user and provides a JWT token."
      )
      @ApiResponses({
              @ApiResponse(responseCode = "200", description = "Authentication successful"),
              @ApiResponse(responseCode = "401", description = "Invalid credentials"),
              @ApiResponse(responseCode = "500", description = "Server error")
      })
      public ResponseEntity<AuthenticationRespose> authenticate(
              @RequestBody @Valid AuthenticationRequest request
      ) {
            return ResponseEntity.ok(authenticationService.authenticate(request));
      }

      @GetMapping("/activate-account")
      @Operation(
              summary = "Activate a user account",
              description = "Activates a user account using the provided token."
      )
      @ApiResponses({
              @ApiResponse(responseCode = "200", description = "Account activated"),
              @ApiResponse(responseCode = "400", description = "Invalid or expired token"),
              @ApiResponse(responseCode = "500", description = "Server error")
      })
      public void activateAccount(@RequestParam String token) throws MessagingException {
            authenticationService.activateAccount(token);
      }
}
