package com.project.template.config.utils.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.time.expiration}")
    private String timeExpiration;

    @Value("${jwt.user}")
    private String userGenerator;

    //Generate Token
    public String createToken(Authentication authentication){
        try{
            Algorithm algorithm = Algorithm.HMAC256(this.secretKey);

            String username = authentication.getPrincipal().toString();
            String authorities = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            String jwtToken = JWT.create()
                    .withIssuer(this.userGenerator)
                    .withSubject(username)
                    .withClaim("authorities",authorities)
                    .withIssuedAt(new Date(System.currentTimeMillis()))
                    .withExpiresAt(new Date(System.currentTimeMillis() + Long.parseLong(timeExpiration)))
                    .withJWTId(UUID.randomUUID().toString())
                    .withNotBefore(new Date(System.currentTimeMillis()))
                    .sign(algorithm);

            return jwtToken;

        }catch (JWTCreationException exception){
            throw new JWTCreationException("Error to create Token: ", exception);
        }
    }

    //Validate Token
    public DecodedJWT validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.secretKey);
            JWTVerifier jwtVerifier = JWT.require(algorithm)
                    .withIssuer(this.userGenerator)
                    .build();

            DecodedJWT decodedJWT = jwtVerifier.verify(token);
            return decodedJWT;

        }catch (JWTVerificationException exception){
            throw new JWTVerificationException("Token invalid, not Authorized");
        }
    }

    //Extract Username
    public String extractUsername(DecodedJWT decodedJWT){
        return decodedJWT.getSubject().toString();
    }

    //Get Any Claim
    public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName){
        return decodedJWT.getClaim(claimName);
    }
}
