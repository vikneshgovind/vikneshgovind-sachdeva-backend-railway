package com.sachdeva.roadlines.Configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;

import static org.springframework.http.HttpMethod.*;

import com.sachdeva.roadlines.Filter.JwtRequestFilter;
import com.sachdeva.roadlines.Service.AppUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final AppUserDetailsService appUserDetailsService; // Constructor Injection
	// JwtRequestFilter our Filter register
	private final JwtRequestFilter jwtRequestFilter;
	// Global Exception Handler our filter register
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	@Bean
	public SecurityFilterChain securityFilterChin(HttpSecurity http) throws Exception {

		http.cors(Customizer.withDefaults()).csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(auth -> auth

				// Public end points
				.requestMatchers("/login", "/register", "/send-reset-otp", "/reset-password", "/logout", "/encode", "/welcome")
				.permitAll()
				

				// Role-based access
				
				// Same End point but different HTTP Method 
				// Only ADMIN can delete
				.requestMatchers(DELETE, "/hub/{lr_number}" ).hasRole("ADMIN")
				
				// ADMIN + MANAGER can update (PUT)
				.requestMatchers(PUT, "/hub/{lr_number}" ).hasAnyRole("MANAGER", "ADMIN")
				
				// ADMIN only 
				.requestMatchers( "/user/{user_id}", "/users-details-page-based-filter").hasRole("ADMIN") // only ADMIN
				.requestMatchers( "/hub/**", "/dashboard/**", "/activity/**").hasAnyRole("ADMIN", "MANAGER") // ADMIN + MANAGER
				
				// All roles can access home APIs
				.requestMatchers("/home/**", "/excel/**").hasAnyRole("ADMIN", "MANAGER", "USER") // all roles

				// Any other must be authenticated
				.anyRequest().authenticated())

				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.logout(AbstractHttpConfigurer::disable)

				// JwtRequestFilter before UsernamePasswordAuthenticationFilter(JwtRequestFilter
				// our Filter register)
				.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)

				// Global Exception Handler register
				.exceptionHandling(ex -> ex.authenticationEntryPoint(customAuthenticationEntryPoint));

		return http.build();

	}

	// Password Encoder and CORS Filter
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/*
	@Bean
	public CorsFilter corsFilter() {
		return new CorsFilter(corsConfigurationSource());
	}

	private CorsConfigurationSource corsConfigurationSource() {
		// create a object for corsConfiguration
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("https://sachdeva-erode-hub.vercel.app", "http://localhost:5174"));
		config.setAllowedMethods(List.of("GET", "PUT", "POST", "DELETE", "PATCH", "OPTIONS"));
		config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);

		return source;
	}
	*/
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration config = new CorsConfiguration();
	    config.setAllowedOrigins(List.of(
	        "https://sachdeva-erode-hub.vercel.app",
	        "http://localhost:5174"
	    ));
	    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
	    config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
	    config.setAllowCredentials(true);

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", config);

	    return source;
	}



	@Bean
	public AuthenticationManager authonticationManager() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

		authenticationProvider.setUserDetailsService(appUserDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());

		return new ProviderManager(authenticationProvider);
	}

}
