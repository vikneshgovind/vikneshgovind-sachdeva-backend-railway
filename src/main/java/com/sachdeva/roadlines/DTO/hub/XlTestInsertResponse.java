package com.sachdeva.roadlines.DTO.hub;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class XlTestInsertResponse {

	private int insertedCountTest;
	private int skippedCountTest;

	private List<XlTestResponse> insertedXlDatasTest;
	private List<XlTestRequest> skippedXlDatasTest;
}
