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
        }
        return false;
    }

    public String getSubject(String token) {
        JWTParser parser = new JWTParser();
        return parser.parsePayload(token).getSubject();
    }

}
