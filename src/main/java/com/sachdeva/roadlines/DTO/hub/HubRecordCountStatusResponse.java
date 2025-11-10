package com.sachdeva.roadlines.DTO.hub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HubRecordCountStatusResponse {

	private long totalRecord;
	private long initiatedRecord;
	private long pendingRecord;
	private long paidRecord;
	private int totalPendingAmount;
	private int totalInitiatedAmount;

}
