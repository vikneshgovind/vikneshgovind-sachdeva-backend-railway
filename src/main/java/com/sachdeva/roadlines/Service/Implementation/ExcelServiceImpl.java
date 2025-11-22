package com.sachdeva.roadlines.Service.Implementation;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.sachdeva.roadlines.Entity.HubEntity;
import com.sachdeva.roadlines.Repository.ExcelRepository;
import com.sachdeva.roadlines.Service.ExcelService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {

    private final ExcelRepository excelRepository;

    @Override
    public byte[] exportAllHubDataToExcel() throws Exception {

        List<HubEntity> allHubDataList = excelRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sachdeva Roadlines");

        Row header = sheet.createRow(0);

        String[] columns = {
                "LR Number", "LR Date", "Inward", "Party Name",
                "PKS", "Weight", "LR Amount",
                "CR Number", "CR Date",
                "Rebate", "After Rebate", "Others",
                "CR Amount", "Paid Amount", "Balance Amount",
                "Hub Status", "Payment Date", "Payment Type",
                "From Address", "Branch",
                "Created At", "Updated At", "ID"
        };

        for (int i = 0; i < columns.length; i++) {
            header.createCell(i).setCellValue(columns[i]);
        }

        int rowIdx = 1;

        for (HubEntity hub : allHubDataList) {
            Row row = sheet.createRow(rowIdx++);

            row.createCell(0).setCellValue(hub.getLorryReceiptNo());
            row.createCell(1).setCellValue(hub.getLorryReceiptDate().toString());
            row.createCell(2).setCellValue(hub.getInwardNo());
            row.createCell(3).setCellValue(hub.getPartyName());
            row.createCell(4).setCellValue(hub.getPks());
            row.createCell(5).setCellValue(hub.getWeight());
            row.createCell(6).setCellValue(hub.getLorryReceiptAmount());

            row.createCell(7).setCellValue(hub.getCashReceiptNo());
            row.createCell(8).setCellValue(hub.getCashReceiptDate() != null ? hub.getCashReceiptDate().toString() : "");

            row.createCell(9).setCellValue(hub.getRebate());
            row.createCell(10).setCellValue(hub.getAfterRebate());
            row.createCell(11).setCellValue(hub.getOthers());
            row.createCell(12).setCellValue(hub.getCashReceiptAmount());
            row.createCell(13).setCellValue(hub.getPaidAmount());
            row.createCell(14).setCellValue(hub.getBalanceAmount());

            row.createCell(15).setCellValue(hub.getPaymentStatus().toString());

            row.createCell(16).setCellValue(hub.getPaymentDate() != null ? hub.getPaymentDate().toString() : "");
            row.createCell(17).setCellValue(hub.getPaymentType());
            row.createCell(18).setCellValue(hub.getFromAddress());
            row.createCell(19).setCellValue(hub.getBranch());

            row.createCell(20).setCellValue(hub.getCreatedAt() != null ? hub.getCreatedAt().toString() : "");
            row.createCell(21).setCellValue(hub.getUpdatedAt() != null ? hub.getUpdatedAt().toString() : "");

            row.createCell(22).setCellValue(hub.getId());
        }

        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }
}
