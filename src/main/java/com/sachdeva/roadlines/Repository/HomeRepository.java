package com.sachdeva.roadlines.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sachdeva.roadlines.Entity.HubEntity;
import com.sachdeva.roadlines.Enum.HubStatus;

public interface HomeRepository extends JpaRepository<HubEntity, Long> {

	// Custom method for get LR Number to Hub Data
	Optional<HubEntity> findByLorryReceiptNo(long lorryReceiptNo);

	// Custom method for get Inward Number to Hub Data
	Optional<HubEntity> findByInwardNo(String inward_no);

	// Custom method for get Party Name to Hub Data
	List<HubEntity> findByPartyName(String partyName);

	// Custom method for get CR Number to Hub Data
	Optional<HubEntity> findBycashReceiptNo(int cr_no);

	// Custom Method for get LR Date to Hub Data
	List<HubEntity> findByLorryReceiptDate(LocalDate lorryReceiptDate);

	/* Custom method for get PAID, INITIATED, BENDING status Lists */

	// For Paid (paginated)
	Page<HubEntity> findByPartyNameAndPaymentStatus(String partyName, HubStatus status, Pageable pageable);

	// For others (non-paginated) and DESC order to get list's
	List<HubEntity> findByPartyNameAndPaymentStatus(String partyName, HubStatus status, Sort sort);
		
	List<HubEntity> findByPartyNameAndPaymentStatus(String partyName, HubStatus status);

	// all paymentStatus PAID, PENDING, INITIATED status data's get page based
	Page<HubEntity> findByPartyName(String partyName, Pageable pageable);

}
