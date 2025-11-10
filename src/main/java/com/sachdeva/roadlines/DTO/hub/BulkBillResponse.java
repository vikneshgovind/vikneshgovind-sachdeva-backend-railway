package com.sachdeva.roadlines.DTO.hub;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BulkBillResponse {
    private String partyName;
    private long totalLorryReceiptAmount;
    private int totalRecords;
    private int totalRebate;
    private int totalAfterRebate;
    private int totalOthers;
    private int totalCashReceiptAmount;
    private int totalPaidAmount;
    private int totalBalanceAmount;
    private List<RecordInfo> records;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RecordInfo {
    	private String inwardNo;
        private long lorryReceiptNo;
        private LocalDate lorryReceiptDate;
        private long lorryReceiptAmount;
        private int pks;
        private int weight;
        private int rebate;
        private int afterRebate;
        private int others;
        private int cashReceiptAmount;
        private int paidAmount;
        private int balanceAmount;
    }
}
