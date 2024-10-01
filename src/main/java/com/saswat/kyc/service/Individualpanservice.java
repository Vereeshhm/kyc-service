package com.saswat.kyc.service;

import org.springframework.http.ResponseEntity;

import com.saswat.kyc.dto.IndividualPanRequest;

public interface Individualpanservice {

	
	public ResponseEntity<String> getPanVerification(IndividualPanRequest panRequest);
}
