package com.sachdeva.roadlines.DTO.Dashboard;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardUersResponse {
	
	private String userId;
	private String name;
	private String role; // e.g. "ROLE_ADMIN", "ROLE_MANAGER", "ROLE_USER"
	private String email;
	private Boolean isAccountVerified;
	private Timestamp createdAt;
	private Timestamp updatedAt;

}
