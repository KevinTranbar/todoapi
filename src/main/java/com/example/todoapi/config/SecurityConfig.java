package com.example.todoapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean //Ex: @Service creates bean out of class, @bean creates bean out of output of method //Use when want Spring to handle class you didn't write yourself but want Spring managed
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); //Spring handles this now
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { //Throws exception because some configurations throw exception
        //Filters Spring security applies to all incoming req
        //HttpSecurity http = Builder object that builds filter chain
        //SecurityFilterChain = The finished, configured filter chain that comes out from http.build(), each "." is a config
        http
                .csrf(csrf -> csrf.disable()) //CSRF = Essentially cookie attack, Spring Sec fixes with special token: !Not needed because we aren't using cookie-auth(JWT auth), disable
                .sessionManagement(session -> //By default, Spring Sec creates and manges HttpSessions for each auth user; we want to use JWT auth aka stateless auth, HttpSess is state on server
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) //Says, "Never create or use sessions" / No session created for any req, no cookie sent, stateless
                )
                .authorizeHttpRequests(auth -> auth //Defines authorization rules, **auth** is a builder for adding rules, like http
                        .requestMatchers("/api/auth/**").permitAll() //Auth endpoints are public, all starting with /api/auth/ is allowed (for login / register)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() //"For any URL, if Http method=Options, allow without auth", OPTIONS = Http method for CORS preflight requests, preflight = permission check before real req, preflight sent for some requests (non-simple req)
                        .anyRequest().authenticated() //Everything else must be authenticated aka must have valid JWT
                );

        return http.build(); //After config, finalize setup and return SecurityFilterChain object
    }
}
