package com.sachdeva.roadlines.Controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sachdeva.roadlines.Service.ExcelService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/excel")
public class ExeclController {

 private final ExcelService excelService;
 
 @GetMapping("/download-all-hub-datas-excel")
 public ResponseEntity<?> downloadAllHubDataExcel() {
     try {
         byte[] excelBytes = excelService.exportAllHubDataToExcel();

         return ResponseEntity.ok()
                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=hub-data.xlsx")
                 .contentType(MediaType.APPLICATION_OCTET_STREAM)
                 .body(excelBytes);

     } catch (Exception e) {
         e.printStackTrace();
         return ResponseEntity.status(500)
                 .body("Excel Export Failed: " + e.getMessage());
     }
 }
 
 
 /*

 // Import All the Hub Data's
 @PostMapping("/upload-hub-data") 
 public ResponseEntity<String> uploadHubData(@RequestParam("file") MultipartFile file) {

     if (file.isEmpty()) {
         return ResponseEntity.badRequest().body("File is empty");
     }

     try {
         excelImportService.importExcelData(file);
         return ResponseEntity.ok("Excel data imported successfully");
     }
     catch (Exception e) {
         e.printStackTrace();
         return ResponseEntity.internalServerError().body("Failed to import");
     }
 }

*/
}

