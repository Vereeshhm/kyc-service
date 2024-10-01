package com.saswat.kyc.service;

import org.springframework.http.ResponseEntity;

import com.saswat.kyc.dto.Voterdetaileddto;
import com.saswat.kyc.dto.Voterfetchdto;

public interface Voterservice {

	ResponseEntity<String> getfetchAll(Voterdetaileddto voterdetaileddto);

	ResponseEntity<String> getfetchdetails(Voterfetchdto voterfetchdto);

}
