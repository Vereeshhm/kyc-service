package com.saswat.kyc.service;

import org.springframework.http.ResponseEntity;

import com.saswat.kyc.dto.Voterverificationdto;

public interface Voterverificationservice {

	ResponseEntity<String> getVerify(Voterverificationdto voterverificationdto);

}
