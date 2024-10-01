package com.saswat.kyc.service;

import org.springframework.http.ResponseEntity;

import com.saswat.kyc.dto.ElectricityRequest;

public interface ElectricityService {

	ResponseEntity<String> getElectricityBill(ElectricityRequest electricityRequest);

}
