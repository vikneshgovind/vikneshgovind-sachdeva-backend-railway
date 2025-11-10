package com.sachdeva.roadlines.Controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sachdeva.roadlines.DTO.hub.HubResponse;
import com.sachdeva.roadlines.Entity.HubEntity;
import com.sachdeva.roadlines.Service.HomeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor

public class HomeController {

	private final HomeService homeService;

	// LR Number to get data
	@GetMapping("/lrno/{lr_number}")
	public ResponseEntity<HubResponse> getLorryreceiptNoDetails(@PathVariable("lr_number") long lrNumber) {
		HubResponse response = homeService.getHubByLrNumber(lrNumber);
		return ResponseEntity.ok(response);
	}

	// GET - get the hub data by Inward Number
	@GetMapping("/inward/{inward}")
	public ResponseEntity<HubResponse> getInwardNoDetails(@PathVariable("inward") String inward_number) {
		HubResponse inwardResponse = homeService.getHubByInwardNumber(inward_number);
		return ResponseEntity.ok(inwardResponse);
	}

	// GET - get the hub data by PartyName Number
	@GetMapping("/partyName/{party_name}")
	public ResponseEntity<List<HubResponse>> getPartyNameDetails(@PathVariable("party_name") String partyName) {
		List<HubResponse> partyNameResponseLists = homeService.getHubByPartyName(partyName);
		return ResponseEntity.ok(partyNameResponseLists);

	}

	// GET -get the hub data CR Number
	@GetMapping("/crno/{cr_number}")
	public ResponseEntity<HubResponse> getCashReceiptNoDetails(@PathVariable("cr_number") int cashReceiptNumber) {
		HubResponse crNumberResponse = homeService.getHubByCrNumber(cashReceiptNumber);
		return ResponseEntity.ok(crNumberResponse);
	}

	// GET - get the hub data by LR Date details
	@GetMapping("/lrDate/{lr_date}")
	public ResponseEntity<List<HubResponse>> getHubByLrDate(@PathVariable("lr_date") LocalDate lorryReceiptDate) {
		List<HubResponse> lorryReceiptDateResponseLists = homeService.getHubByLrDate(lorryReceiptDate);
		return ResponseEntity.ok(lorryReceiptDateResponseLists);

	}

	// GET - get the Data from Page Number based One page Limit is 20
	@GetMapping("/page/{page}")
	public ResponseEntity<Page<HubEntity>> getHubByPageNumber(@PathVariable("page") int page_number) {

		if (page_number <= 0) {
			return ResponseEntity.badRequest().body(null);
		}
		Page<HubEntity> theTwentyPageData = homeService.getTwentyHubDataThroughPageNumber(page_number);
		return ResponseEntity.ok().body(theTwentyPageData);
	}


	@GetMapping("/paid-page-by-party-name")
	public ResponseEntity<?> getPaidPageByPartyName(@RequestParam String partyName,
			@RequestParam(defaultValue = "1", required = false) int pageNumber) { 
		Page<HubResponse> paidPartyNamePage = homeService.getPaidHubsByPartyName(partyName, pageNumber);

		if (paidPartyNamePage.isEmpty()) {
			return ResponseEntity.status(404).body("No pending records found for partyName: " + partyName);
		}
		return ResponseEntity.ok(paidPartyNamePage);
	}

	// payment Status INITIATED data's get by PartyName
	@GetMapping("/initiated-list-by-party-name")
	public ResponseEntity<List<HubResponse>> getInitiatedListByPartyName(@RequestParam String partyName) {

		List<HubResponse> initiatedList = homeService.getInitiatedHubsByPartyName(partyName);
		return ResponseEntity.ok(initiatedList);
	}

	// payment Status INITIATED data's get by PartyName

	@GetMapping("/pending-list-by-party-name")
	public ResponseEntity<List<HubResponse>> getPendingListByPartyName(@RequestParam String partyName) {

		List<HubResponse> pendingList = homeService.getPendingHubsByPartyName(partyName);
		System.out.println("the pending list " + pendingList);
		return ResponseEntity.ok(pendingList);
	}
 
	// Search PartyName based all data's Like PAID, PENDING, INITIATED through Page based
	@GetMapping("/all-status-party-name-data-by-page-based")
	public ResponseEntity<Page<HubResponse>> getAllStatusPageByPartyName(@RequestParam String partyName,
			@RequestParam(defaultValue = "1", required = false) int pageNumber) {
		System.out.println("the \n page \n is : "+ pageNumber);
		Page<HubResponse> allStatusPartyNamePage = homeService.getAllStatusHubsByPartyName(partyName, pageNumber);

		return ResponseEntity.ok(allStatusPartyNamePage);
	}


}
