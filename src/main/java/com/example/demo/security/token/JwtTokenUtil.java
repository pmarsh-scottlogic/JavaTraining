package com.example.demo.security.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.security.userInfo.AppUser;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenUtil {
    private static final long EXPIRE_DURATION = 60 * 60 * 1000; // 1 hour

    private String SECRET_KEY = "sgfjsdlfgjsdlfhjksdf"; // todo: move this into a configuration file later

    public String generateAccessToken(AppUser user) {
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes()); // secret key to verify stuff later
        return JWT.create() // We create the JSON Web Token with the builder pattern
                .withSubject(user.getUsername()) // any unique token that identifies a user
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000)) // set expiry time (as millis since epoch)
                .withIssuer("Matcher_Backend") // company name or author of token, or in this case the url of the application. Anything that identifies the issuer of the token
                .sign(algorithm);
    }

}
