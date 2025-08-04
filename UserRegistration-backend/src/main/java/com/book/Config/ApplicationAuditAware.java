package com.book.Config;


import com.book.user.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
//Actually this class is used to know the current user who is logged in or aunthenticated recently
//the @EntityListeners(AuditListener class is responsible to add the createdDate and the last modifiedDate only
//now this class will manage the field created by and modifiedby by getting the curently authenticated user from the securityContextHolder.getContext().authentcated()
public class ApplicationAuditAware implements AuditorAware<Integer>{
    //here Iam used Integer because in my every entity class I have used the id as integer
    //we are traking the id and managing the created by and modified by fields


    @Override
    public Optional<Integer> getCurrentAuditor() {
        //this method is used to return the id  of the current authenticated user or logged in user to manage those fields
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            //if this is the case then the user is not authenticated Yet we will return empty id
            return Optional.empty();
        }

        System.out.println("PRINCIPLE :  "+authentication.getPrincipal());
        User user = (User)authentication.getPrincipal();//it will return the principle now we have type caste it to the user
        return Optional.ofNullable(user.getId());//it will return the currently authenticated user if the userId is null then it will directly return null as the output
        //now we have to say this class to the spring boot application to manage the createdby and stuff....
    }
}
