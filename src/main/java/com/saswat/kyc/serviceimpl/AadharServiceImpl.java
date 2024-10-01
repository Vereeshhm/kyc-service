package com.saswat.kyc.serviceimpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saswat.kyc.dto.AadharRequest;
import com.saswat.kyc.model.Aadharrequestenity;
import com.saswat.kyc.repository.Aadharrequestenityrepository;
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
	
	@Autowired
	Aadharrequestenityrepository aadharrequestenityrepository;

	@Override
	public String fetchAatharVerification(AadharRequest aadharRequest) {

		long timeStamp = System.currentTimeMillis();
		String apiName = "api-preproduction.signzy.app/api/v3/aadhaar/basicVerify";
		String requestPacket = null;
		String Response = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			requestPacket = objectMapper.writeValueAsString(aadharRequest);


			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", propertiesconfig.getToken());

			Aadharrequestenity aadharrequestenity = new Aadharrequestenity();
			aadharrequestenity.setUid(aadharRequest.getUid());
			aadharrequestenity.setStatusmsg("successfully sent");
			aadharrequestenity.setTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

			aadharrequestenityrepository.save(aadharrequestenity);
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
