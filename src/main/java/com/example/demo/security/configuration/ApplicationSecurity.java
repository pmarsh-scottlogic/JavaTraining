package com.example.demo.security.configuration;

import com.example.demo.security.filter.JwtTokenFilter;
import com.example.demo.security.repo.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@EnableWebSecurity
@AllArgsConstructor
public class ApplicationSecurity extends WebSecurityConfigurerAdapter { //todo convert away from deprecated system

    @Autowired
    private final UserRepo userRepo;

    @Autowired
    private final JwtTokenFilter jwtTokenFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(
                username -> userRepo.findByUsername(username)
                        .orElseThrow(
                                () -> new UsernameNotFoundException("User " + username + " not found.")));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // disables protection against Cross Site Request Forgery. If we don't do this, however, we get 403 responses
        // to all http requests :(
        http.csrf().disable();

        // disables protection against the browser rendering a page in a or <iframe>.
        // we must disable it so that the h2-console can be rendered properly
        http.headers().frameOptions().disable();

        // Spring Security will never create an HttpSession, and it will never use it to obtain the SecurityContext
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // authorises any request via /auth/login to enter without authentication
        http.authorizeRequests()
                .antMatchers("/auth/login", "/h2-console/**", "/public/**").permitAll()
                .anyRequest().authenticated();

        // This exception handling code ensures that the server will return HTTP status 401 (Unauthorized)
        // if any error occurs during authentication process
        // todo: this is not what I want. It is causing nonsense urls to return 401 unauthorised errors. Which is not useful
        http.exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, ex) -> response.sendError(
                                HttpServletResponse.SC_UNAUTHORIZED,
                                ex.getMessage()
                        )
                );

        // We add our custom filter before the UsernameAndPasswordAuthenticationFilter in Spring Security filters chain.
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        // expose the PasswordEncoder to spring for use in UserService, so we can encode password before persisting them.
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        // overriding this method allows the AuthenticationManager (configured above in configure(AuthenticationManagerBuilder auth))
        // to be exposed as a Bean for access by Spring
        // AuthenticationManager has one method only: authenticate()
        return super.authenticationManagerBean();
    }

}