package com.sachdeva.roadlines.Controller;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sachdeva.roadlines.DTO.AuthRequest;
import com.sachdeva.roadlines.DTO.AuthResponse;
import com.sachdeva.roadlines.DTO.ResetPasswordRequest;
import com.sachdeva.roadlines.Service.AppUserDetailsService;
import com.sachdeva.roadlines.Service.ProfileService;
import com.sachdeva.roadlines.Util.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final AppUserDetailsService appUserdetailsService;

	// JWT TOKEN creation Component class
	private final JwtUtil jwtUtil;

	private final ProfileService profileService;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AuthRequest request) {

		try {
			authenticate(request.getEmail(), request.getPassword());
			final UserDetails userDetails = appUserdetailsService.loadUserByUsername(request.getEmail());

			final String jwtToken = jwtUtil.generateToken(userDetails);

			/*ResponseCookie cookie = ResponseCookie.from("jwt", jwtToken).httpOnly(true).path("/")
					.maxAge(Duration.ofDays(1)).sameSite("Strict").build(); */
			
			ResponseCookie cookie = ResponseCookie.from("jwt", jwtToken)
				    .httpOnly(true)
				    .secure(true) // must be true for SameSite=None
				    .path("/")
				    .maxAge(Duration.ofDays(1))
				    .sameSite("None") // allow cross-site cookie
				    .build();
			
			return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
					.body(new AuthResponse(request.getEmail(), jwtToken));

		} catch (BadCredentialsException bcx) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", true);
			error.put("message", "Email or password is incorrect");
			return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(error);

		} catch (DisabledException dex) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", true);
			error.put("message", "Account is disabled");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);

		} catch (Exception ex) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", true);
			error.put("message", "Authentication failed");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
		}
	}

	private void authenticate(String email, String password) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

	}

	/* we there the user is authenticated or not */

	@GetMapping("/is-authenticated")
	public ResponseEntity<Boolean> isAuthenticated(
			@CurrentSecurityContext(expression = "authentication?.name") String email) {
		return ResponseEntity.ok(email != null);
	}

	/* send resetOTP for reset password */
	@PostMapping("/send-reset-otp")
	public void senResetOtp(@RequestParam String email) {
		try {
			profileService.sendResetOtp(email);
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	// Reset Password
	@PostMapping("/reset-password")
	public void resetPassword(@RequestBody ResetPasswordRequest request) {
		try {
			profileService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	// Send OTP for email verification
	@PostMapping("/send-otp")
	public void sendVerifyOtp(@CurrentSecurityContext(expression = "authentication?.name") String email) {

		try {
			profileService.sendOtp(email);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	// verify Email for (account verified or not)
	@PostMapping("/verify-otp")
	/*
	 * Here why we don't sent a account verification OTP through using PathVarilable
	 * RequestParam, Because that OTP is sensitive information show then only we use
	 * here "Map"
	 */
	public void verifyEmail(@RequestBody Map<String, Object> request,
			@CurrentSecurityContext(expression = "authentication?.name") String email) {

		System.out.println("Entered the Verify OTP  and comming OTP is :" + request.get("otp"));

		// check if the coming verifyOTP not null
		if (request.get("otp").toString() == null || request.get("otp").toString().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing Details");
		}
		try {
			profileService.verifyOtp(email, request.get("otp").toString()); // here "otp" is a our email we sanded for
																			// our account verification verifyOtp
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	// LogOut API method
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletResponse response) {
		/* ResponseCookie cookie = ResponseCookie.from("jwt", "").httpOnly(true).secure(false).path("/").maxAge(0)
				.sameSite("Strict").build(); */
		ResponseCookie cookie = ResponseCookie.from("jwt", "")
			    .httpOnly(true)
			    .secure(true)
			    .path("/")
			    .maxAge(0)
			    .sameSite("None")
			    .build();

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body("Logged out successfully!");
	}

	// Delete the user using user Id
	@DeleteMapping("/user/{user_id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteUser(@PathVariable("user_id") String userId) {

		System.out.println("Delete user is Executed");

		try {
			profileService.deleteUser(userId);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, userId + " " + e.getMessage());
		}

	}

}
