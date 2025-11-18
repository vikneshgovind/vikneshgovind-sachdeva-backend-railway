package com.sachdeva.roadlines.DTO.hub;

import java.time.LocalDate;

import jakarta.persistence.Column;

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
public class XlTestRequest {

	@Min(value = 100, message = "Inward No must be at least 3 digits")
	@Max(value = 99999999, message = "Inward No must not exceed 8 digits")
	private long lorryReceiptNo;

	@NotBlank(message = "Inwardnumber cannot be empty")
	@Pattern(regexp = "^[0-9]{3,7}[A-Za-z]?$", message = "Inward No must be 3–8 characters: 3–7 digits followed by an optional letter")
	private String inwardNo;
	
	@NotNull(message = "Lorry Receipt Date is required")
	private LocalDate lorryReceiptDate;


	@NotBlank(message = "Party name can't be blank")
	private String partyName;

	@Min(value = 100, message = "Inward No must be at least 3 digits")
	@Max(value = 999999999, message = "Inward No must not exceed 9 digits")
	private int cashReceiptNo;

	
	private int pks;
	private int weight;

	@Min(value = 0, message = "Lorry Receipt Amount must be zero or positive")
	private int lorryReceiptAmount;
	
	
	//_____ lr_number	inward	lr_date	party_name	cr_number	pks	weight	lr_amount	rebate	after_rebate	others	cr_amount	from_addres	payment_amount	balance_amount

	private int rebate;

	@Column(name = "after_rebate")
	private int afterRebate;

	private int others;

	@Column(name = "cr_amount")
	private int cashReceiptAmount;
	
	@NotBlank(message = "From Address is required")
	private String fromAddress;

	private int paidAmount;
	
	private int balanceAmount;


	
}
