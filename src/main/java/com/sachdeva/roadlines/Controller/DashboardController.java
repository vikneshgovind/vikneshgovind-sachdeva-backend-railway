package com.sachdeva.roadlines.Controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sachdeva.roadlines.DTO.Common.PageResponse;
import com.sachdeva.roadlines.DTO.hub.HubRecordCountStatusResponse;
import com.sachdeva.roadlines.DTO.hub.HubResponse;
import com.sachdeva.roadlines.Service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

	private final DashboardService dashboardService;

	@GetMapping("/dashboard-welcome")
	public String dashboardWelcome() {
		return "<h1>Welcome to Dashboard</h1>";
	}
	
	
	
	// Total Record counts Details
	@GetMapping("/record-status")
	public ResponseEntity<HubRecordCountStatusResponse> getRecordCountStatus() {
		HubRecordCountStatusResponse totalRecord = dashboardService.getRecordCounts();
		return ResponseEntity.ok(totalRecord);
	}

	// Get the List's of PAID COUNT, BENDING COUNT, INITIATED COUNT payment status record COUNTS List
	@GetMapping("/party-dashboard")
	public ResponseEntity<List<HubResponse>> getPartyNameToPaymentStatusCountsList(@RequestParam String partyName) {
		List<HubResponse> responseList = dashboardService.getPartyWiseRecordStatusList(partyName);
		return ResponseEntity.ok(responseList);
	}
	
	// Get All PENDING paymentStatus and that filtered by partyName, LR and CR Number, LR date, package, weight
	@GetMapping("/pending-records-page")
	public ResponseEntity<PageResponse<HubResponse>> getPendingRecordsPage(
			
			@RequestParam(required = false) Long lorryReceiptNo, @RequestParam(required = false) String inwardNo,
			@RequestParam(required = false) String partyName,
			@RequestParam(required = false) LocalDate lrDateFrom, @RequestParam(required = false) LocalDate lrDateTo,
			@RequestParam(required = false) Integer pks, @RequestParam(required = false) Integer weight,
			@RequestParam(required = false, defaultValue = "1") Integer pageNo,
			@RequestParam(required = false, defaultValue = "10") Integer pageSize,
			@RequestParam(required = false, defaultValue = "lorryReceiptDate") String sortBy,
			@RequestParam(required = false, defaultValue = "ASC") String sortDir
			) {

		Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		
		

		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
		
		System.out.println("passing is : " + pageable + lorryReceiptNo + inwardNo + lrDateFrom + lrDateTo + weight + pks + partyName);
		
		PageResponse<HubResponse> pendingHubResponsePage = dashboardService.getAllPendingFilteredData(
				lorryReceiptNo,
				inwardNo, partyName, lrDateFrom, lrDateTo, pks, weight, pageable);
		
		return ResponseEntity.ok(pendingHubResponsePage);
	}
	
	
	// Get Recent paid Records (Today, Yesterday, selected Date)
	@GetMapping("/recent-paid")
	public List<HubResponse> getRecentPaidRecords(
			@RequestParam(required = false) String date,
			@RequestParam(defaultValue = "today") String mode // today | yesterday | custom
		) {
		
		LocalDate targetDate;
		
		if("yesterday".equalsIgnoreCase(mode)) {
			targetDate = LocalDate.now().minusDays(1);
		} else if ("custom".equalsIgnoreCase(mode) && date != null) {
			targetDate = LocalDate.parse(date);
		} else {
			targetDate = LocalDate.now();
		}
		
		List<HubResponse> paidHubResponseList = dashboardService.getRecentPaidRecordsUsingDate(targetDate);
		return paidHubResponseList;
	}
	
	

	

}
