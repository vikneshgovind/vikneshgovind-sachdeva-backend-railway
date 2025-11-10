package com.sachdeva.roadlines.Service;

import org.springframework.data.domain.Pageable;

import com.sachdeva.roadlines.DTO.ProfileRequest;
import com.sachdeva.roadlines.DTO.ProfileResponse;
import com.sachdeva.roadlines.DTO.Dashboard.AdminDashboardUersResponse;
import com.sachdeva.roadlines.DTO.Dashboard.AdminDashboardUsersPageResponse;

public interface ProfileService {

	/*
	 * createProfile() method parameter inside ProfileRequest Class in request And
	 * this createProfile() method Return the ProfileResponse class return type
	 */

	ProfileResponse createProfile(ProfileRequest request);

	/* get a profile details */
	ProfileResponse getProfile(String email);

	/* send OTP reset password */
	void sendResetOtp(String email);

	// reset the password
	void resetPassword(String email, String otp, String newPassword);

	// send OTP from account verification
	void sendOtp(String email);

	// coming account verify OTP use to check and set isAccountverified is true
	void verifyOtp(String email, String otp);

	// delete use by userId
	void deleteUser(String userId);

	// get Profile details using userId
	ProfileResponse getProfileDetailsByUserId(String userId);
	
	// get totals user page based and Filter (Email userId, role, created, isAccountVerified 
	AdminDashboardUsersPageResponse<AdminDashboardUersResponse> getAllUsersPageBasedFiltered(String email,
        String userId, Boolean isAccountVerified,
        String role, Pageable pageable
    );

}
