package com.opap.tournamentapp.util;

import com.opap.tournamentapp.model.User;
import io.jsonwebtoken.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String jwtSecret = "yourSecret"; // Replace with a real secret key
    private final long jwtExpirationInMillis = 3600000; // Token validity in milliseconds


    public String generateToken(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String email;

        if (principal instanceof UserDetails) {
            email = ((UserDetails) authentication.getPrincipal()).getUsername(); // Here getUsername gives you the email
        } else if (principal instanceof String) {
            email = (String) principal;
        } else {
            throw new IllegalArgumentException("Principal is not of a recognized type");
        }

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationInMillis))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }



    //This returns the email of the user for authentication
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            // log the error - invalid JWT signature
        } catch (MalformedJwtException ex) {
            // log the error - invalid JWT
        } catch (ExpiredJwtException ex) {
            // log the error - JWT expired
        } catch (UnsupportedJwtException ex) {
            // log the error - unsupported JWT
        } catch (IllegalArgumentException ex) {
            // log the error - JWT claims string is empty
        }
        return false;
    }

    // Add methods to validate the token and extract the username from it.
}
