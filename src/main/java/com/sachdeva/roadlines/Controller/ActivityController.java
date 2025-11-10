package com.sachdeva.roadlines.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sachdeva.roadlines.Entity.ActivityLogEntity;
import com.sachdeva.roadlines.Repository.ActivityLogRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/activity")
@RequiredArgsConstructor
public class ActivityController {

	private final ActivityLogRepository activityLogRepository; 

	@GetMapping("/recent")
	public List<ActivityLogEntity> getRecentActivities() {
		return activityLogRepository.findAll().stream().sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp())) // newest
																														// first
				.limit(20).toList();
	}

}
