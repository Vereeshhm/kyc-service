package com.saswat.kyc.service;

import org.springframework.http.ResponseEntity;

import com.saswat.kyc.dto.DLnumberrequest;
import com.saswat.kyc.dto.Dlverificationrequest;

public interface DLService {

	public ResponseEntity<String> getVerfication(Dlverificationrequest dlverificationrequest);

	public ResponseEntity<String> getFetchDetails(DLnumberrequest dlnumberrequest);

	

}
