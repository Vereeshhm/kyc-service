package com.saswat.kyc.service;

import com.saswat.kyc.dto.Voterdetaileddto;
import com.saswat.kyc.dto.Voterfetchdto;

public interface Voterservice {

	String getfetchAll(Voterdetaileddto voterdetaileddto);

	String getfetchdetails(Voterfetchdto voterfetchdto);

}
