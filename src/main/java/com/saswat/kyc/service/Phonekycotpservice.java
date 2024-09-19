package com.saswat.kyc.service;

import org.springframework.http.ResponseEntity;

import com.saswat.kyc.dto.Phonekycgenerateotpdto;
import com.saswat.kyc.dto.Phonekycnonconsentdto;
import com.saswat.kyc.dto.Phonekycsubmitotpdto;

public interface Phonekycotpservice {

	ResponseEntity<String> generateOtp(Phonekycgenerateotpdto phonekycgenerateotpdto);

	ResponseEntity<String> submitOtp(Phonekycsubmitotpdto phonekycsubmitotpdto);

	ResponseEntity<String> nonConsent(Phonekycnonconsentdto phonekycnonconsentdto);

}
