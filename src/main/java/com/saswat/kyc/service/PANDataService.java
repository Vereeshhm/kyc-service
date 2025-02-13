package com.saswat.kyc.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.saswat.kyc.dto.Panfetchdto;
import com.saswat.kyc.dto.panfetchrequest;



@Service
public interface PANDataService {


	public ResponseEntity<String> getPanDetails(panfetchrequest fetchrequest);

	public ResponseEntity<String> fetchByPanNumber(Panfetchdto panfetchdto);

	
	



}
