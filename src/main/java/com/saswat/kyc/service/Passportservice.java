package com.saswat.kyc.service;

import org.springframework.http.ResponseEntity;

import com.saswat.kyc.dto.PassportNumberDto;
import com.saswat.kyc.dto.PassportVerificationDto;

public interface Passportservice {

	ResponseEntity<String> getVerify(PassportVerificationDto passportVerificationDto);

	ResponseEntity<String> getDetails(PassportNumberDto pasportdto);

}
