package com.sachdeva.roadlines.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sachdeva.roadlines.Entity.ActivityLogEntity;

public interface ActivityLogRepository extends JpaRepository<ActivityLogEntity, Long> {

}
