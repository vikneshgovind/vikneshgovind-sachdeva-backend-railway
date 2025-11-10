package com.sachdeva.roadlines.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "activity_log")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityLogEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long logId;

	private String action;
	private long lorryReceiptNo;
	private String inwardNo;
	private String description;
	private String userId;

	private LocalDateTime timestamp = LocalDateTime.now();

}
