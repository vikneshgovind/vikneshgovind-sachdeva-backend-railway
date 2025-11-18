package com.sachdeva.roadlines.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.sachdeva.roadlines.Entity.HubEntity;
import com.sachdeva.roadlines.Enum.HubStatus;

public interface HubRepository extends JpaRepository<HubEntity, Long>, JpaSpecificationExecutor<HubEntity>{

	// Custom method for get LR Number to Hub Data
	Optional<HubEntity> findByLorryReceiptNo(long lorryReceiptNo);

	// Custom method for get inwardNo to Hub Data
	Optional<HubEntity> findByInwardNo(String inwardNo);

	// Custom Method for Filter FromAddress used to Between the LR From To dates
	// Data's
	List<HubEntity> findByFromAddressAndLorryReceiptDateBetween(String fromAddress, LocalDate fromDate,
			LocalDate toDate);

	// custom method for the LR Numbers List(LR Number List - for Add multiple hub
	// data add ) we pass this method, this give a HubEntity List
	List<HubEntity> findByLorryReceiptNoIn(List<Long> lorryReceiptNumbers);

	// custom method for the INWARD Numbers List(INWARD Number List - for Add
	// multiple hub data add ) we pass this method, this give a HubEntity List
	List<HubEntity> findByInwardNoIn(List<String> inwardNumbers);

	// Record counts get status using
	long countByPaymentStatus(HubStatus paymentStatus);

	// find the paymentStatus and paymentDate Between
	List<HubEntity> findByPaymentStatusAndPaymentDateBetween(HubStatus status, LocalDate start, LocalDate end);
	
	List<HubEntity> findByPaymentStatusAndPaymentDate(HubStatus status, LocalDate paymentDate);
	
	List<HubEntity> findByPaymentStatus(HubStatus status);
	
	boolean existsByPartyName(String partyName); 
	
	boolean existsByInwardNo(String inwardNo);
	
	/* Bulk Bill creation */
	List<HubEntity> findByPartyNameOrderByLorryReceiptNoAsc(String partyName);

    List<HubEntity> findByPartyNameAndPaymentStatusOrderByLorryReceiptNoAsc(String partyName, HubStatus paymentStatus);
    
	
}
