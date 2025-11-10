package com.sachdeva.roadlines.DTO.hub;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HubEntryRequest {

	@Min(value = 100, message = "Inward No must be at least 3 digits")
	@Max(value = 99999999, message = "Inward No must not exceed 8 digits")
	private long lorryReceiptNo;

	@Pattern(regexp = "^[0-9]{3,7}[A-Za-z]?$", message = "Inward No must be 3–8 characters: 3–7 digits followed by an optional letter")
	private String inwardNo;

	@NotBlank(message = "Party name can't be blank")
	private String partyName;

	@Min(value = 100, message = "Inward No must be at least 3 digits")
	@Max(value = 999999999, message = "Inward No must not exceed 9 digits")
	private int cashReceiptNo;

	@NotNull(message = "Lorry Receipt Date is required")
	private LocalDate lorryReceiptDate;

	@NotNull(message = "Cash Receipt Date is required")
	private LocalDate cashReceiptDate;

	private int pks;
	private int weight;

	private int lorryReceiptAmount;
	private int rebate;
	private int afterRebate;
	private int others;
	private int cashReceiptAmount;

	@NotBlank(message = "From Address is required")
	private String fromAddress;

	private String branch;
	private int paidAmount;
	private LocalDate paymentDate;
	private String paymentType;
	private int balanceAmount;

}
