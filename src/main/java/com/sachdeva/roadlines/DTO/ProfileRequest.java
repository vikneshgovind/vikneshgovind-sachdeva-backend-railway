package com.sachdeva.roadlines.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfileRequest {
	/***
	 * IO mean Input-Output , this IO Package we define the Request and Response
	 * here we create Request and Response Classes
	 ***/

	/*
	 * "Validation" Jar file use to we validate the columns while calling that
	 * fields pom.xml file inside we inserted this validation dependency
	 */

	@NotBlank(message = "Neame Should not be empty")
	private String name;
	@Email(message = "Enter a valid email addess")
	@NotNull(message = "Email should not null")
	@NotBlank(message = "Email should not be empty")
	private String email;
	@Size(min = 6, message = "Password must be atleast 6 characters")
	private String password;
	@NotBlank(message = "Role should not be empty")
	private String role; // example: ADMIN / MANAGER / USER

}
