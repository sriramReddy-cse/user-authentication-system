package com.book.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.Collections;

@Configuration //This is a class which is read by the springboot during the execution and then create the beans inside this into the ioc container
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor //basically it is used to  create the final and the private fields
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtFilter jwtAuthFilter;


    @Bean
     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         return http
                  .cors(Customizer.withDefaults())
                  .csrf(AbstractHttpConfigurer::disable)
                  .authorizeHttpRequests(
                          req ->
                                  req.requestMatchers(
                                          "/auth/**",
                                          "/v2/api-docs",
                                          "/api/v1/v3/api-docs",
                                          "/v3/api-docs/**",
                                          "/swagger-resources",
                                          "swagger-resources/**",
                                          "/configuration-ui/**",
                                          "configuration/security/",
                                          "/swagger-ui/**",
                                          "/swagger-ui.html",
                                          "/webjars/**",
                                          "/swagger-ui.html/"
                                  ).permitAll()
                                          .anyRequest()
                                          .authenticated()
                  ).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                  .authenticationProvider(authenticationProvider)
                  .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class).build();
     }


}
