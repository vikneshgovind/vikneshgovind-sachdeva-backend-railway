package com.sachdeva.roadlines.Service.Implementation;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sachdeva.roadlines.DTO.ProfileRequest;
import com.sachdeva.roadlines.DTO.ProfileResponse;
import com.sachdeva.roadlines.DTO.Dashboard.AdminDashboardUersResponse;
import com.sachdeva.roadlines.DTO.Dashboard.AdminDashboardUsersPageResponse;
import com.sachdeva.roadlines.Entity.UserEntity;

import com.sachdeva.roadlines.Repository.UserRepository;

import com.sachdeva.roadlines.Service.EmailService;
import com.sachdeva.roadlines.Service.ProfileService;
import com.sachdeva.roadlines.specification.UserSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

	// Constructor Injection here we use
	private final UserRepository userRepository;
	// Password Encode
	private final PasswordEncoder passwordEncoder;
	// Email service
	private final EmailService emailService;

	@Override
	public ProfileResponse createProfile(ProfileRequest request) {
		UserEntity newProfile = convertToUserEntity(request);

		// Before going to save the new user we check the user Email is already there or
		// not
		if (!userRepository.existsByEmail(request.getEmail())) {
			userRepository.save(newProfile);
			return convertToProfileResponse(newProfile);
		}

		throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already Exists");

	}

	// Builder Pattern here we use

	private ProfileResponse convertToProfileResponse(UserEntity newProfile) {
		return ProfileResponse.builder().name(newProfile.getName()).email(newProfile.getEmail())
				.userId(newProfile.getUserId()).isAccountVerified(newProfile.getIsAccountVerified())
				.role(newProfile.getRole()) // role in response
				.build();

	}

	private UserEntity convertToUserEntity(ProfileRequest request) {

		// Normalize the role
		String role = request.getRole() != null ? request.getRole().toUpperCase() : "USER";
		String finalRole = "ROLE_" + role;

		return UserEntity.builder().email(request.getEmail()).userId(UUID.randomUUID().toString())
				.name(request.getName())
				// Encode Password store coming ProfileRequest password
				.password(passwordEncoder.encode(request.getPassword())).isAccountVerified(false).role(finalRole) // role
																													// here
				.resetOtpExpireAt(0L).verifyOtp(null).verifyOtpExpierAt(0L).resetOtp(null).build();

	}

	// Get a profile details
	@Override
	public ProfileResponse getProfile(String email) {
		UserEntity existingUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found : " + email));
		return convertToProfileResponse(existingUser);

	}

	/* send OTP for reset password */
	@Override
	public void sendResetOtp(String email) {
		UserEntity existingEntity = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found" + email));

		// Generated 6 digit OTP
		String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

		// calculate the expire time (current time + 2 minutes in milliseconds)
		long expireTime = System.currentTimeMillis() + (2 * 60 * 1000);

		// update the profile/user
		existingEntity.setResetOtp(otp);
		existingEntity.setResetOtpExpireAt(expireTime);

		// save into the database
		userRepository.save(existingEntity);

		try {
			emailService.sendResetOtpEmail(existingEntity.getEmail(), otp);

		} catch (Exception ex) {
			throw new RuntimeException("Unable to send email");
		}

	}

	// Reset the Password
	@Override
	public void resetPassword(String email, String otp, String newPassword) {

		UserEntity existingEntityUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not founded " + email));

		if (existingEntityUser.getResetOtp() == null || !existingEntityUser.getResetOtp().equals(otp)) {
			throw new RuntimeException("Invalid OTP");
		}
		if (existingEntityUser.getResetOtpExpireAt() < System.currentTimeMillis()) {
			throw new RuntimeException("OTP Expired");
		}

		existingEntityUser.setPassword(passwordEncoder.encode(newPassword));
		existingEntityUser.setResetOtp(null);
		existingEntityUser.setResetOtpExpireAt(0L);

		userRepository.save(existingEntityUser);

	}

	// send OTP for (account verification)
	@Override
	public void sendOtp(String email) {

		UserEntity existingUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not founded " + email));

		if (existingUser.getIsAccountVerified() != null && existingUser.getIsAccountVerified()) {
			System.out.println("Entered the Send OTP check is account is verified return empty");
			return;
		}

		// Generate the 6 digit OTP
		String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

		// calculate expire time (current time + 24 hours in milliseconds)
		long expiryTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000);

		// update the profile/user
		existingUser.setVerifyOtp(otp);
		existingUser.setVerifyOtpExpierAt(expiryTime);

		// save the changes database
		userRepository.save(existingUser);

		try {
			System.out.println("Entered the Send OTP Function send a Email ");
			emailService.sendOtpEmail(existingUser.getEmail(), existingUser.getVerifyOtp());
		} catch (Exception ex) {
			throw new RuntimeException("Unable to send email");
		}
	}

	// set IsAccountVerified true
//	@Override
//	public void verifyOtp(String email, String otp) {
//		UserEntity existingUser = userRepository.findByEmail(email)
//				.orElseThrow(() -> new UsernameNotFoundException("User not fouded"+email));
//		System.out.println("Existing OTP : "+existingUser.getVerifyOtp());
//		System.out.println("Comming OTP : "+otp);
//		
//		if(existingUser.getVerifyOtp() == null || !existingUser.getVerifyOtp().equals(otp)) {
//			throw new RuntimeException("Invalid OTP");
//		}
//		
//		
//		if(existingUser.getVerifyOtpExpierAt() < System.currentTimeMillis()) {
//			throw new RuntimeException("OTP Expired");
//		}
//		
//		existingUser.setIsAccountVerified(true);	// Account verified is set true
//		System.out.println("Her is the error will throw ");
//		existingUser.setVerifyOtp(null);
//		existingUser.setVerifyOtpExpierAt(0L);
//		
//		userRepository.save(existingUser); 	// save the changes to database
//		
//	}

	// set IsAccountVerified true
	@Override
	public void verifyOtp(String email, String otp) {
		UserEntity existingUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not fouded" + email));

		System.out.println("Existing OTP : " + existingUser.getVerifyOtp());
		System.out.println("Comming OTP : " + otp);

		// 1. OTP invalid check
		if (existingUser.getVerifyOtp() == null || !existingUser.getVerifyOtp().equals(otp)) {
			throw new RuntimeException("Invalid OTP");
		}

		// 2. OTP expired check
		if (existingUser.getVerifyOtpExpierAt() < System.currentTimeMillis()) {
			throw new RuntimeException("OTP Expired");
		}

		// 3. Success case: mark verified
		existingUser.setIsAccountVerified(true);
		System.out.println("Her is the error will throw ");

		// 4. Clear OTP data
		existingUser.setVerifyOtp(null);
		existingUser.setVerifyOtpExpierAt(0L);

		// 5. Save
		userRepository.save(existingUser);
	}

	@Override
	public void deleteUser(String userId) {
		// TODO Auto-generated method stub
		UserEntity existingUser = userRepository.findByUserId(userId)
				.orElseThrow(() -> new UsernameNotFoundException("user not found"));
		userRepository.delete(existingUser);
	}

	@Override
	public ProfileResponse getProfileDetailsByUserId(String userId) {

		UserEntity existingUserIdDetails = userRepository.findByUserId(userId)
				.orElseThrow(() -> new UsernameNotFoundException("User not found : " + userId));
		// TODO Auto-generated method stub
		return convertToProfileResponse(existingUserIdDetails);
	}
	

	// get all the user page based and Filter
	public AdminDashboardUsersPageResponse<AdminDashboardUersResponse> getAllUsersPageBasedFiltered(
            String email,
            String userId,
            Boolean isAccountVerified,
            String role,
            Pageable pageable
    ) {
        Page<UserEntity>  userEntityPages = userRepository.findAll(
            UserSpecification.filterUsers(email, userId, isAccountVerified, role), pageable
		);
        
        List<AdminDashboardUersResponse> listOfAdminDashboarduserResponses = userEntityPages.stream().map(this::convertToAdminDashboardUserResponse).toList();
        
//        return userEntityPage.s  convertToAdminDashboardUserResponse(null)
        return AdminDashboardUsersPageResponse.<AdminDashboardUersResponse>builder()
	        .content(listOfAdminDashboarduserResponses)
	        .totalElements(userEntityPages.getTotalElements())
	        .totalPages(userEntityPages.getTotalPages())
	        .pageNumber(userEntityPages.getNumber()) // 0-based → 1-based
	        .pageSize(userEntityPages.getSize())
	        .build();
    }

	private AdminDashboardUersResponse convertToAdminDashboardUserResponse(UserEntity entity) {
		return AdminDashboardUersResponse.builder()
				.userId(entity.getUserId())
				.name(entity.getName())
				.role(entity.getRole())
				.email(entity.getEmail())
				.isAccountVerified(entity.getIsAccountVerified())
				.createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt())
				.build();
				
	}

}
