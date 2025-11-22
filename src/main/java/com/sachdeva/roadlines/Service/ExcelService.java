package com.sachdeva.roadlines.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface ExcelService {

	// Export All HubData's to EXCEL file 
//	ByteArrayInputStream exportAllHubDataToExcel () throws IOException;
	// Export All HubData's to EXCEL file 
    byte[] exportAllHubDataToExcel() throws Exception;
	
	

}
