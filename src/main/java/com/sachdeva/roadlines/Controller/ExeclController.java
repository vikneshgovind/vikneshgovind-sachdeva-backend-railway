package com.sachdeva.roadlines.Controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sachdeva.roadlines.Service.ExcelService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/excel")
public class ExeclController {

	private final ExcelService excelService;
	
	
	@GetMapping("/download-all-hub-datas-excel")
    public ResponseEntity<InputStreamResource> downloadAllHubDatasExcel() throws IOException {

        ByteArrayInputStream in = excelService.exportAllHubDataToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=roadlines_data.xlsx");
 
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(in));
    }
	
}
