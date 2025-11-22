package com.sachdeva.roadlines.Service.Implementation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.sachdeva.roadlines.DTO.hub.BillingRequest;
import com.sachdeva.roadlines.DTO.hub.BulkBillResponse;
import com.sachdeva.roadlines.DTO.hub.BulkBillResponse.RecordInfo;
import com.sachdeva.roadlines.DTO.hub.CrNoAndNullBillRequest;
import com.sachdeva.roadlines.DTO.hub.DdrRequest;
import com.sachdeva.roadlines.DTO.hub.DdrResponse;
import com.sachdeva.roadlines.DTO.hub.HubEditRequest;
import com.sachdeva.roadlines.DTO.hub.HubEntryRequest;
import com.sachdeva.roadlines.DTO.hub.HubResponse;
import com.sachdeva.roadlines.DTO.hub.XLFileRequest;
import com.sachdeva.roadlines.DTO.hub.XLFileResponse;
import com.sachdeva.roadlines.DTO.hub.XlTestRequest;
import com.sachdeva.roadlines.DTO.hub.XlTestResponse;
import com.sachdeva.roadlines.Entity.ActivityLogEntity;
import com.sachdeva.roadlines.Entity.HubEntity;
import com.sachdeva.roadlines.Enum.HubStatus;
import com.sachdeva.roadlines.Repository.ActivityLogRepository;
import com.sachdeva.roadlines.Repository.HubRepository;
import com.sachdeva.roadlines.Service.HubService;
import com.sachdeva.roadlines.Service.PartyBalanceProjection;
import com.sachdeva.roadlines.Util.ActivityLogUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HubServiceImpl implements HubService {

	private final HubRepository hubRepository;
	private final ActivityLogRepository activityLogRepository;

	@Override
	public HubResponse singleHubEntry(HubEntryRequest request) {

		// Check if the LR Number already existing
		if (hubRepository.findByLorryReceiptNo(request.getLorryReceiptNo()).isPresent()) {
			throw new IllegalArgumentException(
					"with " + request.getLorryReceiptNo() + " Lorry Receipt No already exists!");
		}

		// Check if the inwardNo already existing
		if (hubRepository.findByInwardNo(request.getInwardNo()).isPresent()) {
			throw new IllegalArgumentException("This " + request.getInwardNo() + " Inward Number already exist");
		}
		// HubEntryRequest to Entity
		HubEntity newHubEntry = convertToEntity(request);
		// that Entity save the table
		newHubEntry = hubRepository.save(newHubEntry);

		// recent log register
		saveLog("Created", newHubEntry.getLorryReceiptNo(), newHubEntry.getInwardNo(), "New Record created ",
				ActivityLogUtil.getCurrentuserId());

		// that Entity to hubResponse
		return convertToHubRespone(newHubEntry); 

	}

	private HubStatus checkStatus(Integer lorryReceiptAmount, Integer rebate, Integer afterRebate, Integer others,
			Integer cashReceiptAmount, Integer paidAmount, Integer balanceAmount) {
		

		// PAID
		if (lorryReceiptAmount != null && cashReceiptAmount != null && paidAmount != null && balanceAmount != null
				&& balanceAmount == 0 && paidAmount.equals(cashReceiptAmount)) {
			return HubStatus.PAID;
		}
		
		// INITIATED
		if (lorryReceiptAmount != null && (cashReceiptAmount == null || cashReceiptAmount == 0)) {
			return HubStatus.INITIATED;
		}

		// PENDING
		if (cashReceiptAmount != null && cashReceiptAmount > 0
				&& (paidAmount == null || paidAmount == 0 || balanceAmount == null || balanceAmount > 0)) {
			return HubStatus.PENDING;
		}

		return HubStatus.INITIATED;
	}
	
	private HubEntity convertToEntity(HubEntryRequest hubEntryRequest) {
		HubEntity newHubData = HubEntity.builder().lorryReceiptNo(hubEntryRequest.getLorryReceiptNo())
				.inwardNo(hubEntryRequest.getInwardNo()).partyName(hubEntryRequest.getPartyName())
				.cashReceiptNo(hubEntryRequest.getCashReceiptNo())
				.lorryReceiptDate(hubEntryRequest.getLorryReceiptDate())
				.cashReceiptDate(hubEntryRequest.getCashReceiptDate())
				.pks(hubEntryRequest.getPks())
				.weight(hubEntryRequest.getWeight())
				.lorryReceiptAmount(hubEntryRequest.getLorryReceiptAmount())
				.rebate(hubEntryRequest.getRebate())
				.afterRebate(hubEntryRequest.getAfterRebate())
				.others(hubEntryRequest.getOthers())
				.cashReceiptAmount(hubEntryRequest.getCashReceiptAmount())
				.fromAddress(hubEntryRequest.getFromAddress())
				.branch(hubEntryRequest.getBranch())
				.paidAmount(hubEntryRequest.getPaidAmount())
				.paymentDate(hubEntryRequest.getPaymentDate())
				.paymentType(hubEntryRequest.getPaymentType())
				.balanceAmount(hubEntryRequest.getBalanceAmount())
				.paymentStatus(checkStatus(hubEntryRequest.getLorryReceiptAmount(), hubEntryRequest.getRebate(),
						hubEntryRequest.getAfterRebate(), hubEntryRequest.getOthers(),
						hubEntryRequest.getCashReceiptAmount(), hubEntryRequest.getPaidAmount(),
						hubEntryRequest.getBalanceAmount()))
				.build();

		return newHubData;
	}

	private HubResponse convertToHubRespone(HubEntity hubRequest) {

		return HubResponse.builder().lorryReceiptNo(hubRequest.getLorryReceiptNo()).inwardNo(hubRequest.getInwardNo())
				.partyName(hubRequest.getPartyName()).cashReceiptNo(hubRequest.getCashReceiptNo())
				.lorryReceiptDate(hubRequest.getLorryReceiptDate()).cashReceiptDate(hubRequest.getCashReceiptDate())
				.pks(hubRequest.getPks()).weight(hubRequest.getWeight())
				.lorryReceiptAmount(hubRequest.getLorryReceiptAmount()).rebate(hubRequest.getRebate())
				.afterRebate(hubRequest.getAfterRebate()).others(hubRequest.getOthers())
				.cashReceiptAmount(hubRequest.getCashReceiptAmount()).fromAddress(hubRequest.getFromAddress())
				.branch(hubRequest.getBranch()).paidAmount(hubRequest.getPaidAmount())
				.paymentDate(hubRequest.getPaymentDate()).paymentType(hubRequest.getPaymentType())
				.balanceAmount(hubRequest.getBalanceAmount()).paymentStatus(hubRequest.getPaymentStatus())
				.createdAt(hubRequest.getCreatedAt()).updatedAt(hubRequest.getUpdatedAt()).build();
	}
	
	
	// Shared helper to save + log + return( save the hub record and save log)
    private HubResponse saveAndLog(HubEntity entity, String message) {
    	
    	// save the updated hub Entity
        HubEntity saved = hubRepository.save(entity);
        
        // register the log
		saveLog("Updated", saved.getLorryReceiptNo(), saved.getInwardNo(), message, ActivityLogUtil.getCurrentuserId());
				
		// entity to HubResponse
        return convertToHubRespone(saved);
    }
    
    
	@Override
	public HubResponse updateHub(long lorryReceiptNo, HubEditRequest request) {

		// find existing entry
		HubEntity existingRecord = hubRepository.findByLorryReceiptNo(lorryReceiptNo)
				.orElseThrow(() -> new IllegalArgumentException("LR number " + lorryReceiptNo + " not found"));
		
		HubStatus status = existingRecord.getPaymentStatus();
		
		/* update fields (don’t overwrite ID, createdAt, inwardNo) */
				
		//---------------------- INITIATED records update ---------------------
		if(status == HubStatus.INITIATED) {
	
			existingRecord.setLorryReceiptDate(request.getLorryReceiptDate());
			existingRecord.setFromAddress(request.getFromAddress());
			existingRecord.setBranch(request.getBranch());
			existingRecord.setPartyName(request.getPartyName());
			existingRecord.setPks(request.getPks());
			existingRecord.setWeight(request.getWeight());
			existingRecord.setLorryReceiptAmount(request.getLorryReceiptAmount());
			
			return saveAndLog(existingRecord, "Updated the INITIATED Record ");
		}
		

		//---------------------- BENDING Record updated ---------
		if(status == HubStatus.PENDING) {
			
			if (existingRecord.getBalanceAmount() != 0 && !Objects.equals(existingRecord.getPaidAmount(), existingRecord.getCashReceiptAmount())) {

				
				existingRecord.setLorryReceiptDate(request.getLorryReceiptDate());
				existingRecord.setFromAddress(request.getFromAddress());
				existingRecord.setBranch(request.getBranch());
				existingRecord.setPartyName(request.getPartyName());
				existingRecord.setPks(request.getPks());
				existingRecord.setWeight(request.getWeight());
				existingRecord.setLorryReceiptAmount(request.getLorryReceiptAmount());
				
//				second part 
				
				existingRecord.setCashReceiptNo(request.getCashReceiptNo());
				existingRecord.setCashReceiptDate(request.getCashReceiptDate());
				existingRecord.setRebate(request.getRebate());
				existingRecord.setAfterRebate(request.getLorryReceiptAmount() - request.getRebate());
				existingRecord.setOthers(request.getOthers());
				existingRecord.setCashReceiptAmount(request.getAfterRebate() + request.getOthers());
				existingRecord.setPaidAmount(request.getPaidAmount());
				existingRecord.setPaymentDate(request.getPaymentDate());
				existingRecord.setPaymentType(request.getPaymentType());
				existingRecord.setBalanceAmount(request.getCashReceiptAmount() - request.getPaidAmount()); 
				
				existingRecord.setPaymentStatus(checkStatus(existingRecord.getLorryReceiptAmount(), existingRecord.getRebate(),
						existingRecord.getAfterRebate(), existingRecord.getOthers(), request.getCashReceiptAmount(),
						request.getPaidAmount(), request.getBalanceAmount()));
				
				return saveAndLog(existingRecord, "Updated the PENDING Record");
			}
		}
		
		//---------------------- PAID Record updated ---------
        if (status == HubStatus.PAID) {
            if (Objects.equals(existingRecord.getBalanceAmount(), 0)
                    && Objects.equals(existingRecord.getPaidAmount(), existingRecord.getCashReceiptAmount())) {

                existingRecord.setLorryReceiptDate(request.getLorryReceiptDate());
                existingRecord.setFromAddress(request.getFromAddress());
                existingRecord.setBranch(request.getBranch());
                existingRecord.setPartyName(request.getPartyName());
                existingRecord.setPks(request.getPks());
                existingRecord.setWeight(request.getWeight());

                boolean changed = !Objects.equals(existingRecord.getLorryReceiptAmount(), request.getLorryReceiptAmount())
                        || !Objects.equals(existingRecord.getRebate(), request.getRebate())
                        || !Objects.equals(existingRecord.getPaidAmount(), request.getPaidAmount());

                if (changed) {
                    existingRecord.setLorryReceiptAmount(request.getLorryReceiptAmount());
                    existingRecord.setRebate(request.getRebate() != null ? request.getRebate() : 0);
                    existingRecord.setAfterRebate(request.getLorryReceiptAmount() - existingRecord.getRebate());
                    existingRecord.setOthers(request.getOthers() != null ? request.getOthers() : 0);
                    existingRecord.setCashReceiptAmount(existingRecord.getAfterRebate() + existingRecord.getOthers());

                    if (request.getPaidAmount() != null && request.getPaidAmount() > existingRecord.getCashReceiptAmount()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "paidAmount can't be greater than cashReceiptAmount");
                    }

                    existingRecord.setPaidAmount(request.getPaidAmount() != null ? request.getPaidAmount() : 0);
                    existingRecord.setPaymentDate(request.getPaymentDate());
                    existingRecord.setPaymentType(request.getPaymentType());
                    existingRecord.setBalanceAmount(existingRecord.getCashReceiptAmount() - existingRecord.getPaidAmount());

                    existingRecord.setPaymentStatus(checkStatus(
                            existingRecord.getLorryReceiptAmount(),
                            existingRecord.getRebate(),
                            existingRecord.getAfterRebate(),
                            existingRecord.getOthers(),
                            existingRecord.getCashReceiptAmount(),
                            existingRecord.getPaidAmount(),
                            existingRecord.getBalanceAmount()));
                }

                return saveAndLog(existingRecord, "Updated PAID Record");
            }
        }

		// Default fallback
        return convertToHubRespone(existingRecord);
	}
		

	@Override
	public void deleteHub(long lorryReceiptNo) {

		// find the LR Number data
		HubEntity findedLrRecord = hubRepository.findByLorryReceiptNo(lorryReceiptNo)
				.orElseThrow(() -> new IllegalArgumentException("Lorry Receipt No " + lorryReceiptNo + " not found"));
		// register the log
		saveLog("Delete", findedLrRecord.getLorryReceiptNo(), findedLrRecord.getInwardNo(), "Deleted the Record ",
				ActivityLogUtil.getCurrentuserId());

		hubRepository.delete(findedLrRecord);
	}

	// BULK HUB ENTRY
	@Override
	public List<HubResponse> bulkHubEntry(List<HubEntryRequest> requests) {

		List<HubEntity> savedEntities = requests.stream()
				// filter out requests that already exist in DB
				.filter(req -> hubRepository.findByLorryReceiptNo(req.getLorryReceiptNo()).isEmpty()
						&& hubRepository.findByInwardNo(req.getInwardNo()).isEmpty())
				// map → entity
				.map(this::convertToEntity)
				// save entity
				.map(hubRepository::save).toList();

		// Log once after all are saved
		if (!savedEntities.isEmpty()) {
			saveLog("Created", 0, // if lorryReceiptNo not relevant for bulk, pass 0 or -1
					null, "Bulk created " + savedEntities.size() + " Hub entries", ActivityLogUtil.getCurrentuserId());
		}

		// Convert to HubResponses
		return savedEntities.stream().map(this::convertToHubRespone) // convert entity → response
				.toList(); // Java 16+, use .collect(Collectors.toList()) if Java 11
	}

	@Override
	public List<DdrResponse> getDDR(DdrRequest request) {
		List<HubEntity> ddrList = hubRepository.findByFromAddressAndLorryReceiptDateBetween(request.getFromAddress(),
				request.getFromDate(), request.getToDate());

		// Handle Empty or Null Result
		if (ddrList == null || ddrList.isEmpty()) {
			return List.of(); // Return empty list instead of null
		}

		List<DdrResponse> ddrResponseList = convertToDdrResponse(ddrList);

		if (!ddrResponseList.isEmpty()) {
			saveLog("Getted", 0, // if lorryReceiptNo not relevant for bulk, pass 0 or -1
					null, "DDR " + ddrResponseList.size() + " Hub Record Getted", ActivityLogUtil.getCurrentuserId());
		}

		return ddrResponseList;
	}

	private List<DdrResponse> convertToDdrResponse(List<HubEntity> ddrResponseList) {
		return ddrResponseList.stream()
				.map(entity -> DdrResponse.builder().lorryReceiptNo(entity.getLorryReceiptNo())
						.inwardNo(entity.getInwardNo()).partyName(entity.getPartyName())
						.cashReceiptNo(entity.getCashReceiptNo()).lorryReceiptDate(entity.getLorryReceiptDate())
						.cashReceiptDate(entity.getCashReceiptDate()).pks(entity.getPks()).weight(entity.getWeight())
						.lorryReceiptAmount(entity.getLorryReceiptAmount()).rebate(entity.getRebate())
						.afterRebate(entity.getAfterRebate()).others(entity.getOthers())
						.cashReceiptAmount(entity.getCashReceiptAmount()).build())
				.toList();

	}

	// UPDATE BILLIND DETAILS
	@Override
	public HubResponse updateBillByLr(long lorryReceiptNo, BillingRequest request) {
		// check if the LR exist
		HubEntity existingLrRecord = hubRepository.findByLorryReceiptNo(lorryReceiptNo)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"No hub found with LR Number: " + lorryReceiptNo));

		// if existing LR record have, Update the BILLING details
		updateTheBilling(existingLrRecord, request);

		// convert HubEntity -> HubResponse
		return convertToHubRespone(existingLrRecord);
	}

	@Override
	public HubResponse updateCrAndNullBill(long lorryReceiptNo, CrNoAndNullBillRequest request) {
		// check if the LR exist
		HubEntity existingLrRecord = hubRepository.findByLorryReceiptNo(lorryReceiptNo)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"No hub found with LR Number: " + lorryReceiptNo));

		// if existing LR record have, Update the CR Number and CR Amount BILLING
		// details
		updateCrNoAndCrAmountDetailsOnly(existingLrRecord, request);

		// convert HubEntity -> HubResponse
		return convertToHubRespone(existingLrRecord);
	}

	// Update the coming (BillingRequest)Billing Details to HubEntity
	private HubEntity updateTheBilling(HubEntity existingRecord, BillingRequest request) {

		existingRecord.setPaidAmount(request.getPaidAmount());
		existingRecord.setPaymentDate(request.getPaymentDate());
		existingRecord.setPaymentType(request.getPaymentType());
		existingRecord.setBalanceAmount(request.getBalanceAmount());
		existingRecord.setPaymentStatus(checkStatus(existingRecord.getLorryReceiptAmount(), existingRecord.getRebate(),
				existingRecord.getAfterRebate(), existingRecord.getOthers(), existingRecord.getCashReceiptAmount(),
				request.getPaidAmount(), request.getBalanceAmount()));

		// save the Billing details
		return hubRepository.save(existingRecord);
	}

	// Update the payment cashReceiptNo and cashReceiptAmount details
	private HubEntity updateCrNoAndCrAmountDetailsOnly(HubEntity existingRecord, CrNoAndNullBillRequest request) {

		existingRecord.setCashReceiptNo(request.getCashReceiptNo());
		existingRecord.setCashReceiptDate(request.getCashReceiptDate());
		existingRecord.setRebate(request.getRebate());
		existingRecord.setAfterRebate(request.getCashReceiptAmount() - request.getRebate());
		existingRecord.setOthers(request.getOthers());
		existingRecord.setCashReceiptAmount(request.getCashReceiptAmount()); 
		
		if(existingRecord.getBalanceAmount() == 0 || existingRecord.getBalanceAmount() == existingRecord.getCashReceiptAmount()) {
			existingRecord.setBalanceAmount(request.getCashReceiptAmount());
		} else {
			existingRecord.setBalanceAmount(existingRecord.getBalanceAmount());
		}
		
		// request.getCashReceiptAmount()
		existingRecord.setPaymentStatus(checkStatus(existingRecord.getLorryReceiptAmount(), existingRecord.getRebate(),
				existingRecord.getAfterRebate(), existingRecord.getOthers(), existingRecord.getCashReceiptAmount(),
				existingRecord.getPaidAmount(), existingRecord.getBalanceAmount()));

		// save the CR Number and Amount details only
		return hubRepository.save(existingRecord); 

	}

	public List<HubEntity> convertXlRequestListToEntityListAndSave(List<XLFileRequest> xlRequestList) {

		List<HubEntity> newHubEntryList = xlRequestList.stream()
				.map(xlRequest -> HubEntity.builder().lorryReceiptNo(xlRequest.getLorryReceiptNo())
						.inwardNo(xlRequest.getInwardNo()).partyName(xlRequest.getPartyName())
//				.cashReceiptNo(xlRequest.getCashReceiptNo())
						.lorryReceiptDate(xlRequest.getLorryReceiptDate()).pks(xlRequest.getPks())
						.weight(xlRequest.getWeight()).lorryReceiptAmount(xlRequest.getLorryReceiptAmount())
						.fromAddress(xlRequest.getFromAddress()).build())
				.collect(Collectors.toList());

		// save all HubEntity List
		return hubRepository.saveAll(newHubEntryList);

	}
	
	//--- test 
	public List<HubEntity> convertXlTestRequestListToEntityListAndSave(List<XlTestRequest> xlTestRequestList) {

		List<HubEntity> newHubEntryList = xlTestRequestList.stream()
				.map(xlRequest -> HubEntity.builder()
						.lorryReceiptNo(xlRequest.getLorryReceiptNo())
						.inwardNo(xlRequest.getInwardNo())
						.partyName(xlRequest.getPartyName())
						.lorryReceiptDate(xlRequest.getLorryReceiptDate())
						.pks(xlRequest.getPks())
						.weight(xlRequest.getWeight())
						.lorryReceiptAmount(xlRequest.getLorryReceiptAmount())
						.rebate(xlRequest.getRebate())
						.afterRebate(xlRequest.getAfterRebate())
						.others(xlRequest.getOthers())
						.fromAddress(xlRequest.getFromAddress())
						.cashReceiptAmount(xlRequest.getCashReceiptAmount())
						.fromAddress(xlRequest.getFromAddress())
						.paidAmount(xlRequest.getPaidAmount())
						.balanceAmount(xlRequest.getBalanceAmount())
						.paymentStatus(HubStatus.PENDING)
						.build())
				.collect(Collectors.toList());

		// save all HubEntity List
		return hubRepository.saveAll(newHubEntryList);

	}

	@Override
	public List<HubEntity> getExistingLorryReceiptNumbersList(List<Long> lorryReceiptNumbers) {
		return hubRepository.findByLorryReceiptNoIn(lorryReceiptNumbers);
	}

	@Override
	public List<HubEntity> getExistingInwardNumbersList(List<String> inwardNumbers) {
		return hubRepository.findByInwardNoIn(inwardNumbers);
	}

	@Override
	public XLFileResponse convertToXlResponse(HubEntity hubRequest) {
		
		return XLFileResponse.builder().lorryReceiptNo(hubRequest.getLorryReceiptNo())
				.inwardNo(hubRequest.getInwardNo()).partyName(hubRequest.getPartyName())
				.lorryReceiptDate(hubRequest.getLorryReceiptDate()).pks(hubRequest.getPks())
				.weight(hubRequest.getWeight()).lorryReceiptAmount(hubRequest.getLorryReceiptAmount())
				.fromAddress(hubRequest.getFromAddress()).build();
	}
	@Override
	public XlTestResponse convertToXlResponseTest(HubEntity hubRequest) { 

		return XlTestResponse.builder()
				.lorryReceiptNo(hubRequest.getLorryReceiptNo())
				.inwardNo(hubRequest.getInwardNo())
				.partyName(hubRequest.getPartyName())
				.lorryReceiptDate(hubRequest.getLorryReceiptDate())
				.pks(hubRequest.getPks())
				.weight(hubRequest.getWeight())
				.lorryReceiptAmount(hubRequest.getLorryReceiptAmount())
				.fromAddress(hubRequest.getFromAddress())
				.cashReceiptNo(hubRequest.getCashReceiptNo())
				.rebate(hubRequest.getRebate())
				.afterRebate(hubRequest.getAfterRebate())
				.others(hubRequest.getOthers())
				.cashReceiptAmount(hubRequest.getCashReceiptAmount())
				.paidAmount(hubRequest.getPaidAmount())
				.balanceAmount(hubRequest.getBalanceAmount())
				.build();
	}

	// Log Activity Register method
	private void saveLog(String action, long lorryReceiptNo, String inwardNo, String description, String userid) {

		ActivityLogEntity log = new ActivityLogEntity();
		log.setAction(action);
		log.setLorryReceiptNo(lorryReceiptNo);
		log.setInwardNo(inwardNo);
		log.setDescription(description);
		log.setUserId(userid);

		activityLogRepository.save(log);
	}
	
/* Bulk bill Entry */

    // ✅ 1. Get summary of all records for a party
    @Override 
    @Transactional(readOnly = true)
    public BulkBillResponse getPartyPendingSummaryToBulkBill(String partyName) {
    	
    	// check partyName is there or not 
    	if (partyName == null || partyName.trim().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Party name must not be empty");
		}

		if (!hubRepository.existsByPartyName(partyName)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No records found for party: " + partyName);
		}
		
		
        List<HubEntity> records = hubRepository.findByPartyNameAndPaymentStatusOrderByLorryReceiptNoAsc(partyName, HubStatus.PENDING);

        int totalRebate = 0, totalAfterRebate = 0, totalOthers = 0,
            totalCash = 0, totalPaid = 0, totalBalance = 0; 
        long totalLorryReceiptAmount = 0;
        
     // Prepare list for record details
        List<RecordInfo> recordInfos = new ArrayList<>();

        // Loop through all records to accumulate totals and prepare record info
        for (HubEntity r : records) {
            totalRebate += r.getRebate();
            totalLorryReceiptAmount += r.getLorryReceiptAmount();
            totalAfterRebate += r.getAfterRebate();
            totalOthers += r.getOthers();
            totalCash += r.getCashReceiptAmount();
            totalPaid += r.getPaidAmount();
            totalBalance += r.getBalanceAmount();

            recordInfos.add(RecordInfo.builder()
            		.inwardNo(r.getInwardNo())
                    .lorryReceiptNo(r.getLorryReceiptNo())
                    .lorryReceiptDate(r.getLorryReceiptDate())
                    .lorryReceiptAmount(r.getLorryReceiptAmount())
                    .pks(r.getPks())
                    .weight(r.getWeight())
                    .rebate(r.getRebate()) 
                    .afterRebate(r.getAfterRebate())
                    .others(r.getOthers())
                    .cashReceiptAmount(r.getCashReceiptAmount())
                    .paidAmount(r.getPaidAmount())
                    .balanceAmount(r.getBalanceAmount())
                    .build());
        }
        
        // Build and return the final response
        return BulkBillResponse.builder()
                .partyName(partyName)
                .totalLorryReceiptAmount(totalLorryReceiptAmount)
                .totalRecords(records.size())
                .totalRebate(totalRebate)
                .totalAfterRebate(totalAfterRebate)
                .totalOthers(totalOthers)
                .totalCashReceiptAmount(totalCash)
                .totalPaidAmount(totalPaid)
                .totalBalanceAmount(totalBalance)
                .records(recordInfos)
                .build();
    }

    // 2. Apply payment to a party
    @Override
    @Transactional
    public String applyPartyPaymentToBulkBill(String partyName, int paidAmount, String paymentType , LocalDate paymentDate) {
    	
    	// check partyName is there or not 
    	if (partyName == null || partyName.trim().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Party name must not be empty");
		}

		if (!hubRepository.existsByPartyName(partyName)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No records found for party: " + partyName);
		}
		
        List<HubEntity> records = hubRepository.findByPartyNameAndPaymentStatusOrderByLorryReceiptNoAsc(partyName, HubStatus.PENDING);

        if (records.isEmpty()) {
            return "❌ No records found for party: " + partyName;
        }

        int totalPendingBefore = records.stream().mapToInt(HubEntity::getBalanceAmount).sum();
        
       
        if (paidAmount > totalPendingBefore) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Paid amount (" + paidAmount + ") cannot exceed total pending balance (" + totalPendingBefore + ")"
            );
        }
        
        int remaining = paidAmount;

        for (HubEntity record : records) {
            if (remaining <= 0) break;

            int balance = record.getBalanceAmount();

            if (balance <= 0) continue;

            if (remaining >= balance) {
                record.setPaidAmount(record.getPaidAmount() + balance);
                record.setBalanceAmount(0);
                record.setPaymentStatus(HubStatus.PAID);
                remaining -= balance;
            } else {
                record.setPaidAmount(record.getPaidAmount() + remaining);
                record.setBalanceAmount(balance - remaining);
                record.setPaymentStatus(HubStatus.PENDING);
                remaining = 0;
            }

            record.setPaymentType(paymentType);
            record.setPaymentDate(paymentDate);
            hubRepository.save(record);
        }

        int totalPendingAfter = records.stream().mapToInt(HubEntity::getBalanceAmount).sum();

        return String.format(
                "✅ Payment of ₹%d applied to '%s'. Pending reduced from ₹%d → ₹%d.",
                paidAmount, partyName, totalPendingBefore, totalPendingAfter
        );
    }
    
    
    /* Paryt name Based Get the total Balance Amount*/
	@Override
	public List<PartyBalanceProjection> getPartyNameWiseTotalBalanceAmount() {
		return hubRepository.getPartyWiseTotalBalance();
	}

}

