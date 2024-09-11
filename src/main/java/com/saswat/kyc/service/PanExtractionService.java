package com.saswat.kyc.service;

import org.springframework.web.multipart.MultipartFile;

import com.saswat.kyc.dto.PanFileData;

public interface PanExtractionService {

	//String getFileData(FileData fileData, HttpServletRequest request, HttpServletResponse response1);

	String getPanExtractedData(PanFileData panfiledata);

	String getFileData(MultipartFile file, String ttl);

}
