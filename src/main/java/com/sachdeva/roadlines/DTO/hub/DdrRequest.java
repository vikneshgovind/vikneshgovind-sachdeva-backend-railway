package com.sachdeva.roadlines.DTO.hub;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DdrRequest {

	@NotBlank(message = "party name is required")
	private String fromAddress;
	@NotNull(message = "from date is required")
	private LocalDate fromDate;
	@NotNull(message = "to date is required")
	private LocalDate toDate;

}
