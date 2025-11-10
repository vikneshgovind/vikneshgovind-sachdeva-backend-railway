package com.sachdeva.roadlines.DTO.hub;

import java.time.LocalDate;
import jakarta.validation.constraints.Max;
// NOTE: Min constraints on number fields have been removed/adjusted
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
// NOTE: NotNull constraints have been replaced with nullable types or removed
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HubEditRequest {

	@Min(value = 100, message = "Inward No must be at least 3 digits")
	@Max(value = 99999999, message = "Inward No must not exceed 8 digits")
	private long lorryReceiptNo;

	@Pattern(regexp = "^[0-9]{3,7}[A-Za-z]?$", message = "Inward No must be 3–8 characters: 3–7 digits followed by an optional letter")
	private String inwardNo;

	@NotBlank(message = "Party name can't be blank")
	private String partyName;

	// ⚠️ FIX FOR cashReceiptNo: Set minimum to 0 (or remove it) to allow frontend 0
	// or cleared value (which becomes 0).
	@Min(value = 0, message = "Cash Receipt No must be a non-negative number.")
	@Max(value = 999999999, message = "CR No must not exceed 9 digits")
	private Integer cashReceiptNo; // Changed to Integer to handle null if frontend sends empty/null

	@NotNull(message = "Lorry Receipt Date is required")
	private LocalDate lorryReceiptDate;

	// ⚠️ FIX FOR cashReceiptDate: Must be nullable to allow frontend to send
	// null/clear the date.
	// If you want to accept null, you must remove the @NotNull constraint.
	private LocalDate cashReceiptDate; // @NotNull removed

	private int pks;
	private int weight;

	private int lorryReceiptAmount;

	@Min(value = 0, message = "Rebate must be non-negative")
	private Integer rebate; // Changed to Integer for null-safety

	@Min(value = 0, message = "After Rebate must be non-negative")
	private Integer afterRebate; // Changed to Integer for null-safety

	@Min(value = 0, message = "Others must be non-negative")
	private Integer others; // Changed to Integer for null-safety

	@Min(value = 0, message = "CR Amount must be non-negative")
	private Integer cashReceiptAmount; // Changed to Integer for null-safety

	@NotBlank(message = "From Address is required")
	private String fromAddress;

	private String branch;

	@Min(value = 0, message = "Paid Amount must be non-negative")
	private Integer paidAmount; // Changed to Integer for null-safety

	private LocalDate paymentDate; // @NotNull removed to allow update without date
	private String paymentType;

	@Min(value = 0, message = "Balance Amount must be non-negative")
	private Integer balanceAmount; // Changed to Integer for null-safety

}
