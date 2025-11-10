package com.sachdeva.roadlines.Service;

import java.time.LocalDate;
import java.util.List;

//import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sachdeva.roadlines.DTO.Common.PageResponse;
import com.sachdeva.roadlines.DTO.Dashboard.AdminDashboardUersResponse;
import com.sachdeva.roadlines.DTO.Dashboard.AdminDashboardUsersPageResponse;
import com.sachdeva.roadlines.DTO.hub.HubRecordCountStatusResponse;
import com.sachdeva.roadlines.DTO.hub.HubResponse;

public interface DashboardService {
	//List<HubEntity> findByFromAddressAndLorryReceiptDateBetween(String fromAddress, LocalDate fromDate, LocalDate toDate);
	
	PageResponse<HubResponse> getAllPendingFilteredData(
		Long lorryReceiptNo, String inwardNo, String partyName,
		LocalDate lrDateFrom, LocalDate lrDateTo, Integer pks, Integer weight, Pageable pageable
	);
	
	// Get Recent paid records using date 
	List<HubResponse> getRecentPaidRecordsUsingDate(LocalDate targetDate);
	
	// Total Hub Record count Status get
	HubRecordCountStatusResponse getRecordCounts();
	
	List<HubResponse> getPartyWiseRecordStatusList(String partyName);
	
	

}
