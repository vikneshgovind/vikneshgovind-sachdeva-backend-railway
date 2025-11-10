package com.sachdeva.roadlines.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileResponse {
	/***
	 * IO mean Input-Output , this IO Package we define the Request and Response
	 * here we create Request and Response Classes
	 ***/

	// Response

	private String userId;
	private String name;
	private String email;
	private Boolean isAccountVerified;
	private String role;
}
