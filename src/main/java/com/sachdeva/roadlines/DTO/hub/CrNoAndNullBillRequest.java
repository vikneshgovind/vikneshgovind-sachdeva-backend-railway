package com.sachdeva.roadlines.DTO.hub;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CrNoAndNullBillRequest {

	@Min(value = 100, message = "Inward No must be at least 3 digits")
	@Max(value = 999999999, message = "Inward No must not exceed 9 digits")
	private int cashReceiptNo;

	@NotNull(message = "Cash Receipt Date is required")
	private LocalDate cashReceiptDate;

	private int lorryReceiptAmount;

	private int rebate;
	private int afterRebate;
	private int others;

	@Min(value = 0, message = "Cash Receipt Amount must be zero or positive")
	private int cashReceiptAmount;

//	@Min(value = 0, message = "Paid Amount must be zero or positive")
//	private int paidAmount;
//	
//	@NotNull(message = "Payment Date Date is required")
//	private LocalDate paymentDate;
//	
//	@NotBlank(message = "payment Type is required")
//	private String paymentType;
//	private int balanceAmount;

}
