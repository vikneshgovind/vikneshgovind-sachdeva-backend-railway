package com.sachdeva.roadlines.Service;

import java.time.LocalDate;
import java.util.List;

import com.sachdeva.roadlines.DTO.hub.BillingRequest;
import com.sachdeva.roadlines.DTO.hub.BulkBillResponse;
import com.sachdeva.roadlines.DTO.hub.CrNoAndNullBillRequest;
import com.sachdeva.roadlines.DTO.hub.DdrRequest;
import com.sachdeva.roadlines.DTO.hub.DdrResponse;
import com.sachdeva.roadlines.DTO.hub.HubEditRequest;
import com.sachdeva.roadlines.DTO.hub.HubEntryRequest;
import com.sachdeva.roadlines.DTO.hub.HubResponse;
import com.sachdeva.roadlines.DTO.hub.XLFileRequest;
import com.sachdeva.roadlines.DTO.hub.XLFileResponse;
import com.sachdeva.roadlines.DTO.hub.XlTestRequest;
import com.sachdeva.roadlines.DTO.hub.XlTestResponse;
import com.sachdeva.roadlines.Entity.HubEntity;

public interface HubService {

	// add single hub data
	HubResponse singleHubEntry(HubEntryRequest request);

	// update the hub data
	HubResponse updateHub(long lorryReceiptNo, HubEditRequest request);

	// delete hub record
	void deleteHub(long lorryReceiptNo);

	// Bulk hub entry
	List<HubResponse> bulkHubEntry(List<HubEntryRequest> requests);

	// get DDR
	List<DdrResponse> getDDR(DdrRequest request);

	// Update the BILLING using LR number
	HubResponse updateBillByLr(long lorryReceiptNo, BillingRequest request);

	// Update the Billing CR Number and CR Amount only
	HubResponse updateCrAndNullBill(long lrNumber, CrNoAndNullBillRequest request);

	// Existing LR Numbers List get
	List<HubEntity> getExistingLorryReceiptNumbersList(List<Long> lorryReceiptNumbers);

	// Existing INWARD Numbers List get
	List<HubEntity> getExistingInwardNumbersList(List<String> inwardNumbers);

	// List of XlFileRequest convert to List of HubRespone and save that
	List<HubEntity> convertXlRequestListToEntityListAndSave(List<XLFileRequest> xlRequestList);
	// -- test
	List<HubEntity> convertXlTestRequestListToEntityListAndSave(List<XlTestRequest> xlTestRequestList);

	// HubEntity -> XlFileRsponse
	XLFileResponse convertToXlResponse(HubEntity hubRequest);
	// -- test
	XlTestResponse convertToXlResponseTest(HubEntity hubRequest);
	
	// bulk Bill creation
	BulkBillResponse getPartyPendingSummaryToBulkBill(String partyName);
    String applyPartyPaymentToBulkBill(String partyName, int paidAmount, String paymentType, LocalDate paymentDate);

}
