package com.example.todoapi.filter;

import com.example.todoapi.service.CustomUserDetailsService;
import com.example.todoapi.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component //Similar to @Bean - Difference: Bean = You construct object yourself, Component = Spring constructs object for you
public class JwtAuthFilter extends OncePerRequestFilter { //OncePerRequestFilter = Filter runs once per request

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override //Override doFilterInternal from OncePerRequestFilter
    protected void doFilterInternal( //Authenticate req based on JWT token in auth header and bind req to validated user
            HttpServletRequest request, //Incoming request
            HttpServletResponse response, //Outgoing response
            FilterChain filterChain) throws ServletException, IOException { //FilterChain = The chain of filters that the request goes through, needed to pass the request to the next filter

        final String authHeader = request.getHeader("Authorization"); //Get auth header from request

        if (authHeader == null || !authHeader.startsWith("Bearer ")) { //If auth header null or doesn't start with "Bearer ", pass request to next filter and return
            filterChain.doFilter(request, response); //Passes request to next filter, doesn't throw exception because it's not our job here, it's AuthorizationFilter's job (the req judge)
            return; //Hand req and res to next filter
        }

        final String token = authHeader.substring(7); //If ok - take out token from auth header (removes "Bearer ")

        if (!jwtService.isTokenValid(token)) { //If token is invalid (expired, tampered with, etc), pass request to next filter and return
            filterChain.doFilter(request, response);
            return; //Stops further execution
        }

        final String username = jwtService.extractUsername(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) { //If username successfully extracted and no existing authentication (Don't want to overwrite existing authentication)
            UserDetails userDetails = userDetailsService.loadUserByUsername(username); //Gets user from DB in Spring security format (Spring sec can read, but not see)

            UsernamePasswordAuthenticationToken authToken = //Creates authentication object to store in SecurityContext so that Spring security can use it (Spring can read and see)
                    new UsernamePasswordAuthenticationToken(
                            userDetails, //Details of user
                            null, //Normally password or other credentials, but JWT signature already proved user genuine //null = Already verified, no credentials needed
                            userDetails.getAuthorities() //Gets user's roles (already in userDetails, but needed for @PreAuthorize checks and to trigger a 3-arg constructor of UsernamePasswordAuthenticationToken)
                    );

            authToken.setDetails( //Adds metadata to authentication object
                    new WebAuthenticationDetailsSource().buildDetails(request) //Creates WebAuthenticationDetails object from request (Extracts details from Http req)
            );

            SecurityContextHolder.getContext().setAuthentication(authToken); //...Holder = static class that manages access to security context, .getContext() = Gets current security context, .setAuthentication() = Places authToken(authentication object) in security context
            //"This request is authenticated as this user" - Now we know what req belongs to what user --> Can bind todos to each user
            //This authToken contains user info, req body tells what user is trying to do --> Combine to make connection between todo and user
        }

        filterChain.doFilter(request, response); //Finally, pass request to next filter
        //AuthorizationFilter reads SecurityContext --> Allows user through
    }
}
