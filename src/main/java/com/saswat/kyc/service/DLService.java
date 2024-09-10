package com.saswat.kyc.service;

import com.saswat.kyc.dto.DLnumberrequest;
import com.saswat.kyc.dto.Dlverificationrequest;

public interface DLService {

	public String getVerfication(Dlverificationrequest dlverificationrequest);

	public String getFetchDetails(DLnumberrequest dlnumberrequest);

	

}
