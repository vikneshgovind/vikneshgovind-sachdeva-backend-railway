package com.sachdeva.roadlines.Controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sachdeva.roadlines.DTO.hub.BillingRequest;
import com.sachdeva.roadlines.DTO.hub.BulkBillPaidRequest;
import com.sachdeva.roadlines.DTO.hub.BulkBillResponse;
import com.sachdeva.roadlines.DTO.hub.CrNoAndNullBillRequest;
import com.sachdeva.roadlines.DTO.hub.DdrRequest;
import com.sachdeva.roadlines.DTO.hub.DdrResponse;
import com.sachdeva.roadlines.DTO.hub.HubEditRequest;
import com.sachdeva.roadlines.DTO.hub.HubEntryRequest;
import com.sachdeva.roadlines.DTO.hub.HubResponse;
import com.sachdeva.roadlines.DTO.hub.XLDataInsertResponse;
import com.sachdeva.roadlines.DTO.hub.XLFileRequest;
import com.sachdeva.roadlines.DTO.hub.XLFileResponse;
import com.sachdeva.roadlines.DTO.hub.XlTestInsertResponse;
import com.sachdeva.roadlines.DTO.hub.XlTestRequest;
import com.sachdeva.roadlines.DTO.hub.XlTestResponse;
import com.sachdeva.roadlines.Entity.HubEntity;
import com.sachdeva.roadlines.Service.HubService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/hub")
@RequiredArgsConstructor
public class HubController {

	private final HubService hubService;

	// Test Method
	@GetMapping("/tests")
	public String tests() {
		return "<h1>Hub Controller test method Welcome you</h1>";
	}

	// create a single hub data
	// http://localhost:8080/api/v1/hub
	@PostMapping()
	public ResponseEntity<HubResponse> singleHubEntry(@Valid @RequestBody HubEntryRequest request) {
		System.out.println(request.toString());
		HubResponse addedHub = hubService.singleHubEntry(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(addedHub);
	}

	// update the hub record 
	@PutMapping("/{lr_number}")
	public ResponseEntity<HubResponse> updateHub(@PathVariable("lr_number") long lrNo,
			@Valid @RequestBody HubEditRequest hubEditRequest) {
		HubResponse updatedHub = hubService.updateHub(lrNo, hubEditRequest);
		return ResponseEntity.ok(updatedHub);
	}

	// delete the hub record
	@DeleteMapping("/{lr_number}")
	public ResponseEntity<Void> deleteHub(@PathVariable("lr_number") long lrNo) {
		hubService.deleteHub(lrNo);
		return ResponseEntity.noContent().build(); // returns 204 No Content
	}

	// Bulk create the hub data
	@PostMapping("/bulk")
	public ResponseEntity<List<HubResponse>> bulkHubEntry(
			@RequestBody @Valid @NotEmpty(message = "Request Hub list can't be Empty") List<@Valid HubEntryRequest> hubEntryRequestList) {

		List<HubResponse> savedHubLists = hubService.bulkHubEntry(hubEntryRequestList);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedHubLists);

	}

	// DDR List get
	@PostMapping("/ddr")
	public ResponseEntity<?> getDdrList(@Valid @RequestBody DdrRequest ddrRequest) {

		List<DdrResponse> ddrLists = hubService.getDDR(ddrRequest);

		if (ddrLists.isEmpty()) {
			return ResponseEntity.noContent().build(); // 204 for no content
		}
		return ResponseEntity.ok(ddrLists);

	}

	// LR Number to Update the Cash Details
	@PutMapping("/bill-to-lr/{lr_number}")
	public ResponseEntity<HubResponse> updatedBillToLR(@PathVariable("lr_number") long lrNumber,
			@Valid @RequestBody BillingRequest billingRequest) {
		HubResponse savedBillDetails = hubService.updateBillByLr(lrNumber, billingRequest);
		return ResponseEntity.ok(savedBillDetails);
	}

	// CR number and Null Bill to Update the Cash Details
	@PutMapping("/cr-no-null-bill/{lr_number}")
	public ResponseEntity<HubResponse> updatedCrNoAndnullBill(@PathVariable("lr_number") long lrNumber,
			@Valid @RequestBody CrNoAndNullBillRequest crNoAndNullBillRequest) {
		HubResponse savedBillDetails = hubService.updateCrAndNullBill(lrNumber, crNoAndNullBillRequest);
		return ResponseEntity.ok(savedBillDetails);
	}

	// XL File hub Data's Add
	@PostMapping("/xl-file")
	public ResponseEntity<XLDataInsertResponse> createdBulkHub(
			@RequestBody @Valid List<@Valid XLFileRequest> xlListRequestList) {

		// coming Request in LR and INWARD Numbers
		List<Long> comingLRNumbers = xlListRequestList.stream().map(XLFileRequest::getLorryReceiptNo).toList();
		List<String> comingInwardNumbers = xlListRequestList.stream().map(XLFileRequest::getInwardNo).toList();

		// Table already there LR and INWARD Numbers get
		Set<Long> existingLRNumbers = hubService.getExistingLorryReceiptNumbersList(comingLRNumbers).stream()
				.map(HubEntity::getLorryReceiptNo).collect(Collectors.toSet());
		Set<String> existingInwardNumbers = hubService.getExistingInwardNumbersList(comingInwardNumbers).stream()
				.map(HubEntity::getInwardNo).collect(Collectors.toSet());

		// Existing LR and INWARD Numbers without new coming xlListRequestList LR and
		// INWARD Numbers
		List<XLFileRequest> newHubDatas = xlListRequestList.stream()
				.filter(req -> !existingLRNumbers.contains(req.getLorryReceiptNo())
						&& !existingInwardNumbers.contains(req.getInwardNo()))
				.toList();

		// Already existing LR and INWARD Numbers contains the coming LXFileRequest
		List<XLFileRequest> skippedHubData = xlListRequestList.stream()
				.filter(req -> existingLRNumbers.contains(req.getLorryReceiptNo())
						&& existingInwardNumbers.contains(req.getInwardNo()))
				.toList();
		System.out.println("skippedHubData datas :" + skippedHubData);

		// save the new Hub data's
		List<HubEntity> savedHubDatas = hubService.convertXlRequestListToEntityListAndSave(newHubDatas);

		// saved Hub data's response
		List<XLFileResponse> insertedHubDatas = savedHubDatas.stream().map(hubService::convertToXlResponse).toList();
		System.out.println("inserted datas :" + insertedHubDatas);
		XLDataInsertResponse xLDataInsertResponse = XLDataInsertResponse.builder()
				.insertedCount(insertedHubDatas.size()).skippedCount(skippedHubData.size())
				.insertedXlDatas(insertedHubDatas).skippedXlDatas(skippedHubData).build();

		return ResponseEntity.ok(xLDataInsertResponse);
	}
	
	
	// --------
	// XL File hub Data's PENDING All record Add
		@PostMapping("/xl-file-test")
		public ResponseEntity<XlTestInsertResponse> createdBulkPendingHub(
				@RequestBody @Valid List<@Valid XlTestRequest> xlTestRequestList) {

			// coming Request in LR and INWARD Numbers
			List<Long> comingLRNumbers = xlTestRequestList.stream().map(XlTestRequest::getLorryReceiptNo).toList();
			List<String> comingInwardNumbers = xlTestRequestList.stream().map(XlTestRequest::getInwardNo).toList();

			// Table already there LR and INWARD Numbers get
			Set<Long> existingLRNumbers = hubService.getExistingLorryReceiptNumbersList(comingLRNumbers).stream()
					.map(HubEntity::getLorryReceiptNo).collect(Collectors.toSet());
			Set<String> existingInwardNumbers = hubService.getExistingInwardNumbersList(comingInwardNumbers).stream()
					.map(HubEntity::getInwardNo).collect(Collectors.toSet());

			// Existing LR and INWARD Numbers without new coming xlListRequestList LR and
			// INWARD Numbers
			List<XlTestRequest> newHubDatas = xlTestRequestList.stream()
					.filter(req -> !existingLRNumbers.contains(req.getLorryReceiptNo())
							&& !existingInwardNumbers.contains(req.getInwardNo()))
					.toList();

			// Already existing LR and INWARD Numbers contains the coming LXFileRequest
			List<XlTestRequest> skippedHubData = xlTestRequestList.stream()
					.filter(req -> existingLRNumbers.contains(req.getLorryReceiptNo())
							&& existingInwardNumbers.contains(req.getInwardNo()))
					.toList();
			System.out.println("skippedHubData datas :" + skippedHubData);

			// save the new Hub data's
			List<HubEntity> savedHubDatas = hubService.convertXlTestRequestListToEntityListAndSave(newHubDatas);
			

			// saved Hub data's response
			List<XlTestResponse> insertedHubDatas = savedHubDatas.stream().map(hubService::convertToXlResponseTest).toList();
			System.out.println("inserted datas :" + insertedHubDatas);
			XlTestInsertResponse xLDataInsertResponseTest = XlTestInsertResponse.builder()
					.insertedCountTest(insertedHubDatas.size())
					.skippedCountTest(skippedHubData.size())
					.insertedXlDatasTest(insertedHubDatas)
					.skippedXlDatasTest(skippedHubData)
					.build();

			return ResponseEntity.ok(xLDataInsertResponseTest);
		}

/* Bulk bill controller methods */
	
	// Get party pending summary
    @GetMapping("/party-bulk-pending-summary/{party-name}")
    public ResponseEntity<BulkBillResponse> getPartyPendingRecordForBulkBill(
            @PathVariable("party-name") String partyName) {
        return ResponseEntity.ok(hubService.getPartyPendingSummaryToBulkBill(partyName));
    }
    
    // Apply payment for party
    @PostMapping("/party-bulk-bill-payment")
    public ResponseEntity<String> applyPaymentToBullBill( @Valid @RequestBody BulkBillPaidRequest request) {
    	System.out.println("coming bulk paid Details :\n"+ request.toString());
        return ResponseEntity.ok(hubService.applyPartyPaymentToBulkBill(request.getPartyName(), request.getPaidAmount(), request.getPaymentType(), request.getPaymentDate()));
    }

}

