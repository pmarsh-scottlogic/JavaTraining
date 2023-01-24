package com.example.demo.security.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.impl.JWTParser;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.security.userInfo.AppUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtTokenUtil {
    private static final long EXPIRE_DURATION = 60 * 60 * 1000; // 1 hour

    private static final String SECRET_KEY = "sgfjsdlfgjsdlfhjksdf"; // todo: move this into a configuration file later
    private static final Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY.getBytes()); // secret key to verify stuff later

    public String generateAccessToken(AppUser user) {

        return JWT.create() // We create the JSON Web Token with the builder pattern
                .withSubject(makeTokenSubject(user)) // any unique token that identifies a user
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_DURATION)) // set expiry time (as millis since epoch)
                .withIssuer("Matcher_Backend") // company name or author of token, or in this case the url of the application. Anything that identifies the issuer of the token
                .sign(algorithm);
    }

    private String makeTokenSubject(AppUser user) {
        return String.format("%s,%s", user.getId(), user.getUsername());
    }

    public boolean validateAccessToken(String token) {
        try {
            // surely there's easier syntax for this?
            // anyway, verification is a builder object that takes claims and builds a JWTVerifier.
            // In this case we check claims later.
            // The JWTVerifier class holds the verify method to assert that a given Token has not only a proper JWT format, but also it's signature matches.
            // Recall that the signature is made by encoding the header and payload then hashing them along with a secret.
            JWTVerifier.BaseVerification verification = (JWTVerifier.BaseVerification) JWT.require(algorithm);
            DecodedJWT jwt = verification
                    .build()
                    .verify(token);
            return true;
        } catch (AlgorithmMismatchException ex) {
            log.error("Algorithm mismatch: ", ex.getMessage());
        } catch (SignatureVerificationException ex) {
            log.error("Signature verification failed", ex.getMessage());
        } catch (TokenExpiredException ex) {
            log.error("JWT has expired", ex);
        } catch (MissingClaimException ex) {
            log.error("Claim to be verified is missing", ex);
        } catch (IncorrectClaimException ex) {
            log.error("Incorrect claim");
        } catch (Exception ex) {
            log.error("There is a problem with the JWT");
        }
        return false;
    }

    public static String getSubject(String token) {
        // We receive the token in encoded format. We need to decode it then get subject field from the JSON

        DecodedJWT decoded = JWT.decode(token);
        return decoded.getSubject();
    }

}
