package com.example.demo.security.filter;

import com.example.demo.security.token.JwtTokenUtil;
import com.example.demo.security.userInfo.AppUser;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@AllArgsConstructor @Component
public class JwtTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // this method is called when the filter is reached in the filter chain

        // If the request has no AuthorizationBearer (i.e. no jwt token) then we continue down the chain,
        // where it will be intercepted elsewhere and give a 401 unauthorized response,
        // or it is a login request, and a new token will be sent to the client.
        if (!hasAuthorizationBearer(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getAccessToken(request);

        if (!jwtTokenUtil.validateAccessToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        setAuthenticationContext(token, request);
        filterChain.doFilter(request, response);
    }

    private boolean hasAuthorizationBearer(HttpServletRequest request) {
        // An http request carrying a JWT will put it in the Authorization section of the request header.
        String header = request.getHeader("Authorization");
        return !ObjectUtils.isEmpty(header) && header.startsWith("Bearer");
    }

    private String getAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        // The authorization header looks like "Bearer [token_string]", so we must isolate the token_string
        return header.split(" ")[1].trim();
    }

    private void setAuthenticationContext(String token, HttpServletRequest request) {
        // This method is what ultimately says that "yup, the given request is valid". I don't know how it works though.
        UserDetails userDetails = getUserDetails(token);

        UsernamePasswordAuthenticationToken
                authentication = new UsernamePasswordAuthenticationToken(userDetails, null, null);

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private UserDetails getUserDetails(String token) {
        String[] jwtSubject = jwtTokenUtil.getSubject(token).split(",");
        UUID id = UUID.fromString(jwtSubject[0]);
        String username = jwtSubject[1];

        AppUser userDetails = new AppUser();

        userDetails.setId(id);
        userDetails.setUsername(username);

        return userDetails;
    }
}
