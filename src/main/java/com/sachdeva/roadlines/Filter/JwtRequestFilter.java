package com.sachdeva.roadlines.Filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sachdeva.roadlines.Service.AppUserDetailsService;
import com.sachdeva.roadlines.Util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

	private final AppUserDetailsService appUserDetailsService;
	private final JwtUtil jwtUtil;

	private static final List<String> PUBLIC_URLS = List.of("/login", "/register", "/send-reset-otp", "/reset-password",
			"/logout");

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String path = request.getServletPath();

		// Skip the JWT Validation for the Public URLS
		if (PUBLIC_URLS.contains(path)) {
			// the Filter chain pass the Response to the next filter
			filterChain.doFilter(request, response);
			return;
		}

		String jwt = null;
		String email = null;

		// 1. check the authorization header(authorization header is found)
		final String authorizationHeader = request.getHeader("Authorization");
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
			jwt = authorizationHeader.substring(7);
		}

		// 2. If not found in header, check cookies. (Authorization header is not found)
		if (jwt == null) {
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if ("jwt".equals(cookie.getName())) {
						jwt = cookie.getValue();
						break;
					}
				}
			}
		}

		// 3. validate the token and set security context ( we need to validate the
		// token and security context)

		if (jwt != null) {
			email = jwtUtil.extractEmail(jwt);

			if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = appUserDetailsService.loadUserByUsername(email);

				if (jwtUtil.validateToken(jwt, userDetails)) {
					UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				}
			}
		}

		filterChain.doFilter(request, response);
	}

}
