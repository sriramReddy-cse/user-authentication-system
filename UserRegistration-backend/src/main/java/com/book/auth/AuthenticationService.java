package com.book.auth;

import com.book.email.EmailService;
import com.book.email.EmailTemplateName;
import com.book.role.Role;
import com.book.role.RoleRepo;
import com.book.security.JwtService;
import com.book.user.Token;
import com.book.user.TokenRepo;
import com.book.user.User;
import com.book.user.UserRepo;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.book.email.EmailTemplateName.ACTIVATE_ACCOUNT;

@Service
@RequiredArgsConstructor
public class AuthenticationService{

    @Value("${application.mailing.frontend.activation-url}")
    private  String activationUrl;
    private final RoleRepo roleRepo;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepo tokenRepo;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final JwtService jwtService;


    public void register(RegistrationRequest request) throws MessagingException {

          //first we will extract the role of the user from the role database
          var userRole = roleRepo.findByName("USER")
                            .orElseThrow(() -> new IllegalStateException("ROle USER is not initialized.."));

          var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))//we have to encode the password and save into the table so that while authentication it will decode the password and generate the jwt token to the user it is secured crptographically
                  .createdDate(LocalDateTime.now())
                .roles(List.of(userRole))
                .accountLocked(false)
                .enabled(false)
                .build();
        userRepo.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
          //sendEmail now
        emailService.sendMail(
                   user.getEmail(),
                   user.fullname(),
                   ACTIVATE_ACCOUNT,
                   activationUrl,
                   newToken,
                "Account activation"
          );
    }

    private String generateAndSaveActivationToken(User user) {
        String generateToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generateToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))//the token will be expired after 15 minutes
                .user(user)
                .build();
        tokenRepo.save(token);
        return generateToken;
    }

    private String generateActivationCode(int lentgh) {
         String characters =  "0123456789";//these are the characters we have (in digits) now we have to generate the token from this
         StringBuilder code = new StringBuilder();
         SecureRandom secureRandom = new SecureRandom();//it is same as the random value but  the generated value  is secured cryptographically
         for(int i=0;i<lentgh;i++){
              int randomIndex = secureRandom.nextInt(characters.length());
              code.append(characters.charAt(randomIndex));
        }
         return code.toString();//we will convert into the String from StringBuilder
    }

    public AuthenticationRespose authenticate(AuthenticationRequest request) {

        var auth = authenticationManager.authenticate(
                 new UsernamePasswordAuthenticationToken(
                         request.getEmail(),
                         request.getPassword()
                 )
        );

        var claims = new HashMap<String,Object>();
        var user = (User)(auth.getPrincipal());
        claims.put("fullname",user.fullname());
        var jwtToken = jwtService.generateToken(claims,user);
        return AuthenticationRespose.builder()
                .token(jwtToken).build();
    }


    public void activateAccount(String token) throws MessagingException {
         Token savedToken = tokenRepo.findByToken(token).orElseThrow(()->new RuntimeException("Token not found"));
          if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())){
              //The token is expired Now resend the validation mail again
              sendValidationEmail(savedToken.getUser());
              throw new MessagingException("Token is expired");
          }
          //if the token is not expired validate it with the user from the database
          User newUser = userRepo.findById(savedToken.getUser().getId()).orElseThrow(
                  () -> new RuntimeException("User not found"));
          //now activate the user in the sense enable the user
         newUser.setEnabled(true);
         userRepo.save(newUser);
         savedToken.setValidatesAt(LocalDateTime.now());
         tokenRepo.save(savedToken);
         System.out.println("User is enabled successfully..");
    }
}
