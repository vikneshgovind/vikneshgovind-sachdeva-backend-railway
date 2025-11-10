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
public class XLFileResponse {

	private long lorryReceiptNo;
	private String inwardNo;
	private String partyName;

//	private int cashReceiptNo;
	private LocalDate lorryReceiptDate;

	private int pks;
	private int weight;

	private int lorryReceiptAmount;
	private String fromAddress;

}
