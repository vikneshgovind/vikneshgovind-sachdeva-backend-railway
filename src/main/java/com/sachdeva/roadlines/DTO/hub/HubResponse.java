package com.sachdeva.roadlines.DTO.hub;

import java.sql.Timestamp;
import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.sachdeva.roadlines.Enum.HubStatus;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HubResponse {

	private long lorryReceiptNo;
	private String inwardNo;
	private String partyName;
	private int cashReceiptNo;

	private LocalDate lorryReceiptDate;
	private LocalDate cashReceiptDate;

	private int pks;
	private int weight;

	private int lorryReceiptAmount;
	private int rebate;
	private int afterRebate;
	private int others;
	private int cashReceiptAmount;

	private String fromAddress;

	private String branch;
	private int paidAmount;
	private LocalDate paymentDate;
	private String paymentType;
	private int balanceAmount;
	private HubStatus paymentStatus;

	@CreationTimestamp
	@Column(updatable = false)
	private Timestamp createdAt;

	@UpdateTimestamp
	private Timestamp updatedAt;
}
