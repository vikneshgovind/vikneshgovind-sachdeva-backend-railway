package com.sachdeva.roadlines.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.sachdeva.roadlines.Entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

	Optional<UserEntity> findByEmail(String email);

	// Email is there or not check method
	Boolean existsByEmail(String email);

	// find the userId
	Optional<UserEntity> findByUserId(String userId);
	
	long countByEmail (String email);
	long countByIsAccountVerified(Boolean value);
}
