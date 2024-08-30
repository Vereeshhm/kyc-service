package com.saswat.kyc.service;

import org.springframework.stereotype.Service;

import com.saswat.kyc.dto.panfetchrequest;



@Service
public interface PANDataService {


	public String getPanDetails(panfetchrequest fetchrequest);

	
	



}
