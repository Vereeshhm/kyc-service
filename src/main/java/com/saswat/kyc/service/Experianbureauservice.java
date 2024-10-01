package com.saswat.kyc.service;



import org.springframework.http.ResponseEntity;

import com.saswat.kyc.dto.Experianbureaudto;

public interface Experianbureauservice {

	public ResponseEntity<String> getBureauReport(Experianbureaudto bureauDto);

}
