package com.saswat.kyc.service;

import com.saswat.kyc.dto.PassportNumberDto;
import com.saswat.kyc.dto.PassportVerificationDto;

public interface Passportservice {

	String getVerify(PassportVerificationDto passportVerificationDto);

	String getDetails(PassportNumberDto pasportdto);

}
