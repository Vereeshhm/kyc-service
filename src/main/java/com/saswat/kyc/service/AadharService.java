package com.saswat.kyc.service;

import com.saswat.kyc.dto.AadharRequest;

public interface AadharService {

	String fetchAatharVerification(AadharRequest aadharRequest);

}
