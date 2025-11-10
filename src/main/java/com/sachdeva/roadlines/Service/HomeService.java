package com.sachdeva.roadlines.Service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;

import com.sachdeva.roadlines.DTO.hub.HubResponse;
import com.sachdeva.roadlines.Entity.HubEntity;

public interface HomeService {

	HubResponse getHubByLrNumber(long lr_number);

	HubResponse getHubByInwardNumber(String inward_no);

	List<HubResponse> getHubByPartyName(String party);

	HubResponse getHubByCrNumber(int cr_number);

	List<HubResponse> getHubByLrDate(LocalDate lr_date);

	Page<HubEntity> getTwentyHubDataThroughPageNumber(int page);

	/* paymentStatus based PAID, INITIATED, BENDING through partyName */

	// Page based get PAID paymentStatus data's using partyName
	Page<HubResponse> getPaidHubsByPartyName(String partyName, int pageNumber);

	// List get INITIATED paymentStatus data's using partyName
	List<HubResponse> getInitiatedHubsByPartyName(String partyName);

	// List get PENDING paymentStatus data's using partyName
	List<HubResponse> getPendingHubsByPartyName(String partyName);

	// all paymentStatus data's page get using partyName
	Page<HubResponse> getAllStatusHubsByPartyName(String partyName, int pageNumber);

	//
//	List<HubResponse> partyNameByPaidStatusList(String partyName);
//	List<HubResponse> partyNameByPendingStatusList(String partyName);
//	List<HubResponse> partyNameByInitiatedStatusList(String partyName);

}
