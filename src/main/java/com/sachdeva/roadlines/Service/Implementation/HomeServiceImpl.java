package com.sachdeva.roadlines.Service.Implementation;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.sachdeva.roadlines.DTO.hub.HubResponse;
import com.sachdeva.roadlines.Entity.HubEntity;
import com.sachdeva.roadlines.Enum.HubStatus;
import com.sachdeva.roadlines.Repository.HomeRepository;
import com.sachdeva.roadlines.Service.HomeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

	private final HomeRepository homeRepository;

	@Override
	public HubResponse getHubByLrNumber(long lr_number) {
		// Handle invalid input
		if (lr_number <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "LR number must be greater than 0");
		}

		// Handle not found separately
		HubEntity entityDetail = homeRepository.findByLorryReceiptNo(lr_number).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No hub found with LR Number: " + lr_number));

		return convertToHubRespone(entityDetail);
	}

	@Override
	public HubResponse getHubByInwardNumber(String inward_number) {

		// Validate input
		if (inward_number == null || inward_number.trim().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inward number must not be empty");
		}

		// Fetch entity or throw 404
		HubEntity inwardResult = homeRepository.findByInwardNo(inward_number)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"No hub found with Inward Number: " + inward_number));

		// Convert to DTO
		return convertToHubRespone(inwardResult);
	}

	@Override
	public List<HubResponse> getHubByPartyName(String partyName) {
		if (partyName == null || partyName.trim().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Party name must not be empty");
		}

		List<HubEntity> PartyNameResults = homeRepository.findByPartyName(partyName);

		if (PartyNameResults.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hub found for party name: " + partyName);
		}

		return PartyNameResults.stream().map(this::convertToHubRespone).toList();

	}

	@Override
	public HubResponse getHubByCrNumber(int cr_number) {

		if (cr_number <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CR number must be greater than 0");
		}

		HubEntity crResult = homeRepository.findBycashReceiptNo(cr_number).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No data found for CR number : " + cr_number));

		return convertToHubRespone(crResult);
	}

	@Override
	public List<HubResponse> getHubByLrDate(LocalDate lr_date) {

		if (lr_date == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "LR date must not be null");
		}

		List<HubEntity> lrDateResultsList = homeRepository.findByLorryReceiptDate(lr_date);

		if (lrDateResultsList.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nod data found for Cr Date : " + lr_date);
		}

		return lrDateResultsList.stream().map(this::convertToHubRespone).toList();
	}

	private HubResponse convertToHubRespone(HubEntity hubRequest) {

		return HubResponse.builder().lorryReceiptNo(hubRequest.getLorryReceiptNo()).inwardNo(hubRequest.getInwardNo())
				.partyName(hubRequest.getPartyName()).cashReceiptNo(hubRequest.getCashReceiptNo())
				.lorryReceiptDate(hubRequest.getLorryReceiptDate()).cashReceiptDate(hubRequest.getCashReceiptDate())
				.pks(hubRequest.getPks()).weight(hubRequest.getWeight())
				.lorryReceiptAmount(hubRequest.getLorryReceiptAmount()).rebate(hubRequest.getRebate())
				.afterRebate(hubRequest.getAfterRebate()).others(hubRequest.getOthers())
				.cashReceiptAmount(hubRequest.getCashReceiptAmount()).fromAddress(hubRequest.getFromAddress())
				.branch(hubRequest.getBranch()).paidAmount(hubRequest.getPaidAmount())
				.paymentDate(hubRequest.getPaymentDate()).paymentType(hubRequest.getPaymentType())
				.balanceAmount(hubRequest.getBalanceAmount()).paymentStatus(hubRequest.getPaymentStatus()) // 🟢 Add
																											// this line
																											// — your
																											// INITIATED/PENDING/PAID
																											// value
				.createdAt(hubRequest.getCreatedAt()).updatedAt(hubRequest.getUpdatedAt()).build();
	}

	// All Hub Record Data's get page based
	@Override
	public Page<HubEntity> getTwentyHubDataThroughPageNumber(int page_number) {
		/*
		 * pagination parameters: page - 1: The page number is zero-based in Spring
		 * Data, meaning that the first page is 0, the second page is 1, and so on.
		 * Therefore, if the user requests page 1, we subtract 1 to get 0, which
		 * corresponds to the first set of records. 20: This specifies the size of the
		 * page, meaning that we want to retrieve 20 records per page. The findAll
		 * method returns a Page<Student> object, which contains the requested page of
		 * student records along with additional pagination information (like total
		 * pages, total elements, etc.).
		 */
//		return homeRepository.findAll(PageRequest.of(page_number - 1, 10));
		Sort sort = Sort.by("lorryReceiptDate").descending();
		return homeRepository.findAll(PageRequest.of(page_number - 1, 10, sort));  
		
	}


	// PAID paymentStatus Pages get by partyName through Page based
	@Override
	public Page<HubResponse> getPaidHubsByPartyName(String partyName, int pageNumber) {

		Pageable pageable = PageRequest.of(pageNumber - 1, 10, Sort.by("createdAt").descending());
		Page<HubEntity> paidPartyNameByPage = homeRepository.findByPartyNameAndPaymentStatus(partyName, HubStatus.PAID,
				pageable);

		// Use Page.map() to convert entities to responses
		Page<HubResponse> paidPartyNameHubsPage = paidPartyNameByPage.map(this::convertToHubRespone);

		return paidPartyNameHubsPage;
	}

	// INITIATED paymentStatus List get by partyName
	@Override
	public List<HubResponse> getInitiatedHubsByPartyName(String partyName) {

//		List<HubEntity> initiatedList = homeRepository.findByPartyNameAndPaymentStatus(partyName, HubStatus.INITIATED);
		Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
		List<HubEntity> initiatedList = homeRepository.findByPartyNameAndPaymentStatus(partyName, HubStatus.INITIATED,
				sort);

		List<HubResponse> initiatedHubResponseList = initiatedList.stream().map(this::convertToHubRespone).toList();

		return initiatedHubResponseList;
	}

	// PENDING paymentStatus List get by partyName
	@Override
	public List<HubResponse> getPendingHubsByPartyName(String partyName) {

		Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
		List<HubEntity> pendingList = homeRepository.findByPartyNameAndPaymentStatus(partyName, HubStatus.PENDING,
				sort);

		List<HubResponse> pendingHubResponeList = pendingList.stream().map(this::convertToHubRespone).toList();

		return pendingHubResponeList;
	}

	// all paymentStatus like PAID, PENDING, INITIATED data's get by partyName
	@Override
	public Page<HubResponse> getAllStatusHubsByPartyName(String partyName, int pageNumber) {

		Pageable pageable = PageRequest.of(pageNumber - 1, 10, Sort.by("createdAt").descending());
		Page<HubEntity> allStatusHubEntityPage = homeRepository.findByPartyName(partyName, pageable);

		Page<HubResponse> allStatusHubResponsePage = allStatusHubEntityPage.map(this::convertToHubRespone);

		return allStatusHubResponsePage;
	}

}
