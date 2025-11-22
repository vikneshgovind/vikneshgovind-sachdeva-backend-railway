package com.sachdeva.roadlines.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sachdeva.roadlines.Entity.HubEntity;

public interface ExcelRepository extends JpaRepository<HubEntity, Long> {

	
}
