package com.sachdeva.roadlines.Util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ActivityLogUtil {

	private ActivityLogUtil() {
		// Prevent instantiation
	}

	/**
	 * Returns the current authenticated user's identifier (e.g., email or userId)
	 */

	public static String getCurrentuserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			return authentication.getName(); // usually email or userName
		}
		return "SYSTEM"; // fallback for background processes
	}
}
