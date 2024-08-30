package com.saswat.kyc.service;

import com.saswat.kyc.dto.FileResponse;
import com.saswat.kyc.dto.PanFileData;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface PanExtractionService {

	FileResponse getFileData(HttpServletRequest request, HttpServletResponse response1);

	String getPanExtractedData(PanFileData panfiledata);

}
