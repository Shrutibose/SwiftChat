package com.swiftchat.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final  Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); //secret key

    // Generate token
    public String generateToken(String username){
        return  Jwts.builder() //starts building
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) //1 DAY
                .signWith(key) //digital signature
                .compact(); //converts everything to string

    }
    //extracts usernames
    public String extractUsername(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token) //verify token
                .getBody().getSubject(); //extracts usernames
    }
    //validate tokens
    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e){
            return false;
        }
    }
}

/* user login
jwt generated token
Backend controller sends this token back to the user
 */

/* jwt utility class
generating token
extracting token
validates token
 */

//JWT token is readable but not modifiable
//JWT protects data from being modified

