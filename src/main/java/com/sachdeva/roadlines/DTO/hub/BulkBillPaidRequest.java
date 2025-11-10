package com.sachdeva.roadlines.DTO.hub;

import java.time.LocalDate;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BulkBillPaidRequest {

 @NotBlank(message = "Party name is required")
 private String partyName;

 @Min(value = 1, message = "Paid amount must be greater than zero")
 private int paidAmount;

 @NotBlank(message = "Payment type is required")
 private String paymentType;

 @NotNull(message = "Payment date is required")
 private LocalDate paymentDate;
}
