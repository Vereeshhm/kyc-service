package com.saswat.kyc.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.saswat.kyc.dto.AadharRequest;
import com.saswat.kyc.service.AadharApiLogs;
import com.saswat.kyc.service.AadharService;
import com.saswat.kyc.utils.PropertiesConfig;

@Service
public class AadharServiceImpl implements AadharService {


	@Autowired
	PropertiesConfig propertiesconfig;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	AadharApiLogs aadharApiLogs;

	@Override
	public String fetchAatharVerification(AadharRequest aadharRequest) {

		long timeStamp = System.currentTimeMillis();
		String apiName = "api-preproduction.signzy.app/api/v3/aadhaar/basicVerify";
		String requestPacket = null;
		String Response = null;
		try {
			Gson gson = new Gson();
			requestPacket = gson.toJson(aadharRequest);

			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", propertiesconfig.getToken());
			headers.set("Content-Type", "application/json");

			HttpEntity<AadharRequest> requestEntity = new HttpEntity<AadharRequest>(aadharRequest, headers);
			Response = restTemplate.postForObject(propertiesconfig.getAaadhar_Url(), requestEntity, String.class);
			aadharApiLogs.saveAAdharApiLogs(requestPacket, Response, apiName, timeStamp);
		} catch (Exception e) {
			e.printStackTrace();
			Response = e.getMessage();
			aadharApiLogs.saveAAdharApiLogs(requestPacket, Response, apiName, timeStamp);
			return Response;
		}
		return Response;
	}

}
