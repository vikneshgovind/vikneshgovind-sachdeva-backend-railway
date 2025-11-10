package com.sachdeva.roadlines.Service.Implementation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.sachdeva.roadlines.DTO.Common.PageResponse;
import com.sachdeva.roadlines.DTO.hub.HubRecordCountStatusResponse;
import com.sachdeva.roadlines.DTO.hub.HubResponse;
import com.sachdeva.roadlines.Entity.HubEntity;
import com.sachdeva.roadlines.Enum.HubStatus;
import com.sachdeva.roadlines.Repository.HomeRepository;
import com.sachdeva.roadlines.Repository.HubRepository;
import com.sachdeva.roadlines.Service.DashboardService;
import com.sachdeva.roadlines.specification.HubSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
	
	private final HubRepository hubRepository;
	private final HomeRepository homeRepository;
	
	// Hub Record counts status Get
	@Override
	public HubRecordCountStatusResponse getRecordCounts() {
		long totalRecordCount = hubRepository.count();
		long initiatedCount = hubRepository.countByPaymentStatus(HubStatus.INITIATED);
		long pendingCount = hubRepository.countByPaymentStatus(HubStatus.PENDING);
		long paidCount = hubRepository.countByPaymentStatus(HubStatus.PAID);
		
		// Get all pending records
		List<HubEntity> pendingRecoresList = hubRepository.findByPaymentStatus(HubStatus.PENDING);
		
		// Initialize totals
		int balanceAmountTotal = 0;
		
		for(HubEntity item: pendingRecoresList) {
			balanceAmountTotal += item.getBalanceAmount();
		}
		
		// Compute total pending amount
		int totalPendingAmount = balanceAmountTotal;
		
		
		int totalInitiatedAmount = 0;
		// Get all initiated records
		List<HubEntity> initiatedRecordsList = hubRepository.findByPaymentStatus(HubStatus.INITIATED);
		
		for(HubEntity item : initiatedRecordsList) {
			totalInitiatedAmount += item.getLorryReceiptAmount();
		}
		
		if(totalInitiatedAmount < 0) totalInitiatedAmount = 0;
		
	

		return HubRecordCountStatusResponse.builder()
				.totalRecord(totalRecordCount)
				.initiatedRecord(initiatedCount)
				.pendingRecord(pendingCount)
				.paidRecord(paidCount)
				.totalInitiatedAmount(totalInitiatedAmount)
				.totalPendingAmount(totalPendingAmount)
				.build();
	}
 
	// Get all Pending records page based using Filter 
	@Override
	public PageResponse<HubResponse> getAllPendingFilteredData(
			Long lorryReceiptNo, String inwardNo, String partyName,
			LocalDate lrDateFrom, LocalDate lrDateTo, Integer pks, Integer weight, Pageable pageable
		) {
		
//		Specification<HubEntity> spec = Specification.where(HubSpecification.hasPaymentStatus(HubStatus.PENDING));
		Specification<HubEntity> spec = HubSpecification.hasPaymentStatus(HubStatus.PENDING);

	
		
		if(lorryReceiptNo != null && lorryReceiptNo > 0 ) {
			spec = spec.and(HubSpecification.hasLorryReceiptNo(lorryReceiptNo));
		}
		
		if(inwardNo != null && !inwardNo.trim().isEmpty()) {
			spec = spec.and(HubSpecification.hasInwardNo(inwardNo)); 
		}
		
		if (partyName != null && !partyName.trim().isEmpty()) {
            spec = spec.and(HubSpecification.hasPartyName(partyName.trim()));
        }

		
		if(lrDateFrom != null && lrDateTo != null) {
			spec = spec.and(HubSpecification.hasDateBetween(lrDateFrom, lrDateTo));
		}
		
		if (pks != null && pks > 0) {
			spec = spec.and(HubSpecification.hasPackage(pks));
		}
	
        if (weight != null && weight > 0) {
            spec = spec.and(HubSpecification.hasWeight(weight)); 
        }
		
		Page<HubEntity> pendingHubResultPage = hubRepository.findAll(spec, pageable);
		
		List<HubResponse> content = pendingHubResultPage.stream()
				.map(this::convertToHubResponse)
				.toList();
		
		return PageResponse.<HubResponse>builder()
	            .content(content)
	            .totalElements(pendingHubResultPage.getTotalElements())
	            .totalPages(pendingHubResultPage.getTotalPages())
	            .pageNumber(pendingHubResultPage.getNumber()) // 0-based → 1-based
	            .pageSize(pendingHubResultPage.getSize())
	            .build();
	}
	
	@Override
	public List<HubResponse> getPartyWiseRecordStatusList(String partyName) {

		// Sort newest first
		Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

		// 1️⃣ Get each list separately
		List<HubEntity> paidList = homeRepository.findByPartyNameAndPaymentStatus(partyName, HubStatus.PAID, sort);
		List<HubEntity> pendingList = homeRepository.findByPartyNameAndPaymentStatus(partyName, HubStatus.PENDING,
				sort);
		List<HubEntity> initiatedList = homeRepository.findByPartyNameAndPaymentStatus(partyName, HubStatus.INITIATED,
				sort);

		// 2️⃣ Combine them: PAID first → PENDING → INITIATED
		List<HubEntity> combinedList = new ArrayList<>();
		combinedList.addAll(paidList);
		combinedList.addAll(pendingList);
		combinedList.addAll(initiatedList);

		// Convert entities to responses
		List<HubResponse> responseList = combinedList.stream().map(this::convertToHubResponse).toList();

		return responseList;
	}

	
	// Get Recent paid records and get if we want (yesterday or custom date 
	@Override
	public List<HubResponse> getRecentPaidRecordsUsingDate(LocalDate targetDate) {
//		System.out.println( "the result is : \nFrom Date" + targetDate.atStartOfDay() + "\nTo Date : " + targetDate.plusDays(1).atStartOfDay());
		System.out.println( "the coming date : " + targetDate + "\nthe status :" + HubStatus.PAID);
		
//		List<HubEntity> recentPaidHubRecordsList = hubRepository.findByPaymentStatusAndPaymentDateBetween(
//			HubStatus.PAID, 
//			targetDate.atStartOfDay(),
//			targetDate.plusDays(1).atStartOfDay()
//		); 
		
		List<HubEntity> recentPaidHubRecordsList = hubRepository.findByPaymentStatusAndPaymentDate(HubStatus.PAID, targetDate);
		
		List<HubResponse> recentPaidHubResponseList =  recentPaidHubRecordsList.stream()
				.map(this::convertToHubResponse)
				.toList();
		
		return recentPaidHubResponseList;
	}
	
	
	//	converting method
	private HubResponse convertToHubResponse(HubEntity hubRequest) { 

		return HubResponse.builder().lorryReceiptNo(hubRequest.getLorryReceiptNo()).inwardNo(hubRequest.getInwardNo())
				.partyName(hubRequest.getPartyName()).cashReceiptNo(hubRequest.getCashReceiptNo())
				.lorryReceiptDate(hubRequest.getLorryReceiptDate()).cashReceiptDate(hubRequest.getCashReceiptDate())
				.pks(hubRequest.getPks()).weight(hubRequest.getWeight())
				.lorryReceiptAmount(hubRequest.getLorryReceiptAmount()).rebate(hubRequest.getRebate())
				.afterRebate(hubRequest.getAfterRebate()).others(hubRequest.getOthers())
				.cashReceiptAmount(hubRequest.getCashReceiptAmount()).fromAddress(hubRequest.getFromAddress())
				.branch(hubRequest.getBranch()).paidAmount(hubRequest.getPaidAmount())
				.paymentDate(hubRequest.getPaymentDate()).paymentType(hubRequest.getPaymentType())
				.balanceAmount(hubRequest.getBalanceAmount()).paymentStatus(hubRequest.getPaymentStatus())
				.createdAt(hubRequest.getCreatedAt()).updatedAt(hubRequest.getUpdatedAt()).build();
	}
	
}
