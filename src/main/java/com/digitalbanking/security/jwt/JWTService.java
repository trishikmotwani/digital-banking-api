package com.digitalbanking.security.jwt;

import java.util.Date;
import java.util.HashMap;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTService {

	private static final String SECRET_STRING = "your_very_secure_long_secret_key_at_least_32_chars";
	private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());
	
	// 5 minutes in milliseconds
	private static final long EXPIRATION_TIME = 5 * 60 * 1000; 
    
	public String generateToken(String username) {
		return Jwts.builder()
		.claims(new HashMap<String , Object>())
		.subject(username)
		.issuedAt(new Date(System.currentTimeMillis()))
		.expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
		.signWith(SECRET_KEY)
		.compact();
		
	}
	
	public String validateAndExtractUsername(String token) {
	    return Jwts.parser()
	            .verifyWith(SECRET_KEY)
	            .build()
	            .parseSignedClaims(token)
	            .getPayload()
	            .getSubject();
	}

	public boolean isTokenValid(String token) {
	    try {
	        Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token);
	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}
}
