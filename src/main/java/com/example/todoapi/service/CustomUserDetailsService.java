package com.example.todoapi.service;

import com.example.todoapi.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService { //Basically a wrapper around UserRepository or a dto for Spring security //It returns a user from the DB in a format that Spring security understands

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override //Overrides UserDetailsService method loadUserByUsername for own custom implementation
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { //UserDetails = Spring security's representation of a user
        com.example.todoapi.model.User user = userRepository.findByUsername(username) //2 User variables --> Full path for specific User variable
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return User.builder() //Builds User object in Spring security format (UserDetails object)
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("USER") //Assign ROLE_USER to user (automatically prefixed with ROLE_), hardcoded for now
                .build();
    }
}
//How it differs from repo user: No id, no timestamp, includes roles

//SecurityContext = Context that Spring security sees and uses
//SecurityContext stores Authentication objects which contain UserDetails --> Reason why we need UserDetailsService
