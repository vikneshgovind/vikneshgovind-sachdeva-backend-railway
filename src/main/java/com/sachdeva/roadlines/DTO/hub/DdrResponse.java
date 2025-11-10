package com.sachdeva.roadlines.DTO.hub;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DdrResponse {

	private Long lorryReceiptNo;
	private String inwardNo;
	private String partyName;
	private int cashReceiptNo;
	private LocalDate lorryReceiptDate;
	private LocalDate cashReceiptDate;
	private int pks;
	private int weight;
	private int lorryReceiptAmount;
	private int rebate;
	private int afterRebate;
	private int others;
	private int cashReceiptAmount;
}
