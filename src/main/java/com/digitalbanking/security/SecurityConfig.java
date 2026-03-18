package com.digitalbanking.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.digitalbanking.security.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private JwtAuthenticationFilter jwtFilter;
	
    // In Spring Security 5+: Every password must have an encoding prefix. Example if you dont want encoding yet
//    @Bean
//    public InMemoryUserDetailsManager userDetailsService() {
//    	UserDetails admin = User.builder().username("admin")
//    	.password("{noop}admin123")
//    	.roles("ADMIN")
//    	.build();
//    	
//    	return new InMemoryUserDetailsManager(admin);
//    }
	
//	@Bean
//	public InMemoryUserDetailsManager userDetailsManager() {
//		UserDetails ud = User.builder().username("admin")
//				.password(passwordEncoder().encode("admin123"))
//				.build();
//		return new InMemoryUserDetailsManager(ud);
//	}
	
	@Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	// Basic Authentication example
//		@Bean
//	    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
//			httpSecurity
//	            .csrf(csrf -> csrf.disable()) // Disable CSRF for APIs
//	            .authorizeHttpRequests(auth -> auth
//	                .requestMatchers("/api/user/**").permitAll() // Allow your API
//	                .anyRequest().authenticated()
//	            )
//	            .httpBasic(Customizer.withDefaults()); // Use Basic Auth instead of Form Login
				// Do NOT add .httpBasic() or .formLogin() if you want to skip the popup or default spring security form
//	        
//	        return httpSecurity.build();
//	    }
	
		
	// JWT Authentication example
	// Now instead of basic auth , lets lets Spring Context to use new filter i.e JWTAuthenticationFilter
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		
		httpSecurity
		.cors(Customizer.withDefaults()) // 1. Enable CORS support
		.csrf(csrf -> csrf.disable())
		.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/user/login", 
                        "/api/user/register-user", 
                        "/api/user/reset-password/**").permitAll()
				.anyRequest().authenticated()
		)
	    // Tell Spring: "Check the JWT before checking the spring security's default UsernamePasswordAuthenticationFilter"
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		
		
		
		return httpSecurity.build();
	}
	
	
	// 2. Define the CORS rules
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration configuration = new CorsConfiguration();
	    // Allow your React app's origin
	    configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:5174", "http://localhost:3000")); 
	    // Allow all standard HTTP methods
	    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	    // Allow necessary headers
	    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
	    // Allow credentials (if you use cookies/sessions later)
	    configuration.setAllowCredentials(true);
	    
	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", configuration);
	    return source;
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
