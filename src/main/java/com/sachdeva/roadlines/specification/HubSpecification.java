package com.sachdeva.roadlines.specification;

import java.time.LocalDate;



import org.springframework.data.jpa.domain.Specification;

import com.sachdeva.roadlines.Entity.HubEntity;
import com.sachdeva.roadlines.Enum.HubStatus;


public class HubSpecification {
	
	public static Specification<HubEntity> hasPaymentStatus(HubStatus status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("paymentStatus"), status);
    }
	
	public static Specification<HubEntity> hasLorryReceiptNo(Long lorryReceiptNo) {
        return (root, query, cb) -> cb.equal(root.get("lorryReceiptNo"), lorryReceiptNo);
    }

    public static Specification<HubEntity> hasInwardNo(String inwardNo) {
        return (root, query, cb) -> 
            cb.equal(root.get("inwardNo"), inwardNo);
    } 
    
    public static Specification<HubEntity> hasPartyName(String partyName) {
    	return (root, query, cb) -> 
    	cb.like(cb.lower(root.get("partyName")), "%" + partyName.toLowerCase() + "%");
    }

    public static Specification<HubEntity> hasDateBetween(LocalDate start, LocalDate end) {
        return (root, query, cb) -> cb.between(root.get("lorryReceiptDate"), start, end);
    }

    public static Specification<HubEntity> hasPackage(Integer pks) {
        return (root, query, cb) -> cb.equal(root.get("pks"), pks);
    }

    public static Specification<HubEntity> hasWeight(Integer weight) {
        return (root, query, cb) -> cb.equal(root.get("weight"), weight);
    }
    
		//Long lorryReceiptNo, String inwardNo, LocalDate lrDateFrom, 
		//LocalDate lrDateTo, Integer weight, Integer pks, String partyName) 
		

}
