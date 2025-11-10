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
public class XLDataInsertResponse {

	private int insertedCount;
	private int skippedCount;

	private List<XLFileResponse> insertedXlDatas;
	private List<XLFileRequest> skippedXlDatas;
}
