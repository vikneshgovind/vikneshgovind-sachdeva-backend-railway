package com.sachdeva.roadlines.Controller;

import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sachdeva.roadlines.DTO.ProfileRequest;
import com.sachdeva.roadlines.DTO.ProfileResponse;
import com.sachdeva.roadlines.DTO.Dashboard.AdminDashboardUersResponse;
import com.sachdeva.roadlines.DTO.Dashboard.AdminDashboardUsersPageResponse;
import com.sachdeva.roadlines.Service.EmailService;
import com.sachdeva.roadlines.Service.ProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
//@RequestMapping("/api")
@RequiredArgsConstructor
public class ProfileController {

	// Constructor Injection
	private final ProfileService profileService; // ProfileService is a interface but that interface implements a
													// ProfileServiceImpl class so that class methods will come
	private final PasswordEncoder passwordEncoder;

	// Email Service
	private final EmailService emailSerive;
	/*
	 * "Validation" Jar file use to we validate the columns while calling that
	 * fields pom.xml file inside we inserted this validation dependency use
	 * the @valid annotation before passing that in to the request
	 */

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public ProfileResponse register(@Valid @RequestBody ProfileRequest request) {
		ProfileResponse response = profileService.createProfile(request);
		System.out.println("coming profile request \n" + request.toString());
		// Welcome email notification mail send
		emailSerive.sendWelcomeEmail(response.getEmail(), response.getName());
		return response;
	}

	@GetMapping("/profile")
	// in our case .name is email
	public ProfileResponse getPrifile(@CurrentSecurityContext(expression = "authentication?.name") String email) {
		return profileService.getProfile(email);
	}

	// Test Method
	@GetMapping("/welcome")
	public String test() {
		return "Auth is working";
	}

	// Encode the new ADMIN Password
	/*
	 * when the application initially we deploy it to production we have to generate
	 * the ADMINN password
	 */
	@PostMapping("/encode")
	public String encodePassword(@RequestBody Map<String, String> request) {
		return passwordEncoder.encode(request.get("password"));
	}

	// Get profile Details using userId
	@GetMapping("/find-userid/{userid}")
	public ProfileResponse getProfileByUserId(@PathVariable("userid") String userId) {
		return profileService.getProfileDetailsByUserId(userId);
	}
	
	
	//	Get all users page based and Filter using the Email, userId, isAccountVerified, role
    @GetMapping("/users-details-page-based-filter") 
    public AdminDashboardUsersPageResponse<AdminDashboardUersResponse> filterUsers(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int PageSize,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Boolean isAccountVerified,
            @RequestParam(required = false) String role
    ) {
    	Pageable pageable = PageRequest.of(pageNumber-1, PageSize, Sort.by("createdAt").descending());

        return profileService.getAllUsersPageBasedFiltered(email, userId, isAccountVerified, role, pageable); 
    }
}
