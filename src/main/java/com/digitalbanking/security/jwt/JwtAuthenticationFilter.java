package com.digitalbanking.security.jwt;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.digitalbanking.entities.UserEntity;
import com.digitalbanking.repositories.UserRepository;
import com.digitalbanking.services.IUserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JWTService jwtService;

	@Autowired
	private UserRepository userRepo;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

		String authHeader = request.getHeader("Authorization");
		String token;

		// 1. Check if the header contains a Bearer token
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		// Extract Token

		token = authHeader.substring(7);
		log.info("received token:: " + token);
		
		try {
			// 1. Extract username (subject) from the token
			String username = jwtService.validateAndExtractUsername(token);
			log.info("username extracted from token:: ", username);
			
			//If context is already set, skip re-authentication
			if (SecurityContextHolder.getContext().getAuthentication() == null) {

				// 2. Validate token & check if user exists in your MySQL DB
				UserEntity user = userRepo.findByUsername(username)
						.orElseThrow(() -> new RuntimeException("User not found"));

				// 3. Set Principle, Credential, Authorities
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
						user.getUsername(), null, Collections.emptyList());

				// 4. Adding to security context is mandatory to make spring understand and Authenticate the request
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		} catch (Exception e) {
			// Token is expired or tampered with
			log.error("Exception caught in JWT Authentication filter:", e);
			// System.out.println("JWT Validation failed: " + e.getMessage());
		}

		filterChain.doFilter(request, response);

	}

}
