package com.sachdeva.roadlines.Entity;

import java.sql.Timestamp;
import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.sachdeva.roadlines.Enum.HubStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roadlines")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HubEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "lr_number", unique = true, nullable = false)
	private long lorryReceiptNo;

	@Column(name = "inward", unique = true, nullable = false)
	private String inwardNo;

	@Column(nullable = false)
	private String partyName;

	@Column(name = "cr_number")
	private int cashReceiptNo;

	@Column(name = "lr_date", nullable = false)
	private LocalDate lorryReceiptDate;

	@Column(name = "cr_date")
	private LocalDate cashReceiptDate;

	private int pks;
	private int weight;

	@Column(name = "lr_amount", nullable = false)
	private int lorryReceiptAmount;

	private int rebate;

	@Column(name = "after_rebate")
	private int afterRebate;

	private int others;

	@Column(name = "cr_amount")
	private int cashReceiptAmount;

	@Column(nullable = false)
	private String fromAddress;

	private String branch;
	private int paidAmount;
	private LocalDate paymentDate;
	private String paymentType;
	private int balanceAmount;

	@Enumerated(EnumType.STRING)
	@Column(name = "hub_status", nullable = false)
	@Builder.Default
	private HubStatus paymentStatus = HubStatus.INITIATED;

	@CreationTimestamp
	@Column(updatable = false)
	private Timestamp createdAt;

	@UpdateTimestamp
	private Timestamp updatedAt;

	// 🧠 Auto-update Hub Status based on field values (Optional logic to
	// auto-update status based on payment)
	public void updateHubStatus() {

		// 1️. Check if LR details are present but no payment details
		if (lorryReceiptAmount > 0 && (rebate == 0 && others == 0 && cashReceiptAmount == 0 && paidAmount == 0)) {

			this.paymentStatus = HubStatus.INITIATED;
			return;
		}

		// 2️. Check if partial payment details exist but not fully paid
		if ((cashReceiptAmount > 0 || rebate > 0 || others > 0) && (paidAmount == 0 || balanceAmount > 0)) {

			this.paymentStatus = HubStatus.PENDING;
			return;
		}

		// 3️. Check if everything matches (fully paid)
		if (lorryReceiptAmount > 0 && lorryReceiptAmount == cashReceiptAmount && lorryReceiptAmount == paidAmount
				&& balanceAmount == 0) {

			this.paymentStatus = HubStatus.PAID;
			return;
		}

		// 4️. Default fallback
		this.paymentStatus = HubStatus.INITIATED;
	}

}
