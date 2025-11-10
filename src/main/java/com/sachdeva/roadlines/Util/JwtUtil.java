package com.sachdeva.roadlines.Util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {

	// security key define
	@Value("${jwt.security.key}")
	private String SECURITY_KEY;

	public String generateToken(UserDetails userDetails) {

		Map<String, Object> cliams = new HashMap<>();
		return createToken(cliams, userDetails.getUsername());

	}

	// Before we add the JWT TWO Dependency's
	/*
	 * Before we add the JWT TWO Dependency's 1. JWT - Dependency 2. Java XML Bind -
	 * Dependency
	 */
	private String createToken(Map<String, Object> cliams, String email) {

		return Jwts.builder().setClaims(cliams).setSubject(email).setIssuedAt(new Date(System.currentTimeMillis())) // Token
																													// Created
																													// time
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Token Expire in 10 Hour's
																							// expiration
				.signWith(SignatureAlgorithm.HS256, SECURITY_KEY) // Algorithm for Security Key creation
				.compact();
	}

	// ✅ Extract all claims
	private Claims extractAllClaims(String token) {
		return Jwts.parser().setSigningKey(SECURITY_KEY).parseClaimsJws(token).getBody();
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public String extractEmail(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String email = extractEmail(token);
		return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
}
