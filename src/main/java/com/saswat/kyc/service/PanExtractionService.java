package com.saswat.kyc.service;

import org.springframework.web.multipart.MultipartFile;

import com.saswat.kyc.dto.FileData;
import com.saswat.kyc.dto.PanFileData;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface PanExtractionService {

	//String getFileData(FileData fileData, HttpServletRequest request, HttpServletResponse response1);

	String getPanExtractedData(PanFileData panfiledata);

	String getFileData(MultipartFile file, String ttl, HttpServletRequest request, HttpServletResponse response1);

}
