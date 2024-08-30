package com.saswat.kyc.serviceimpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.saswat.kyc.dto.Voterverificationdto;
import com.saswat.kyc.model.voterverificationlog;
import com.saswat.kyc.repository.voterverificationlogrepository;
import com.saswat.kyc.service.Voterverificationservice;
import com.saswat.kyc.utils.PropertiesConfig;

@Service
public class voterverificationserviceimpl implements Voterverificationservice {

	private static final Logger logger = LoggerFactory.getLogger(voterverificationserviceimpl.class);

	@Autowired
	voterverificationlogrepository voterverificationlogrepository;

	@Autowired
	PropertiesConfig propertiesconfig;

	@Autowired
	RestTemplate restTemplate;

	@Override
	public String getVerify(Voterverificationdto voterverificationdto) {
		logger.info("Starting getVerify method.");

		
		logger.info("Request URL: {}", propertiesconfig.getVoterverificationApiUrl());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", propertiesconfig.getToken());
		logger.info("Authorization Token: {}", propertiesconfig.getToken());

		Gson gson = new Gson();
		String requestBodyJson = gson.toJson(voterverificationdto);
		logger.info("Request Body: {}", requestBodyJson);

		HttpEntity<String> entity = new HttpEntity<>(requestBodyJson, headers);

		voterverificationlog apiLog = new voterverificationlog();
		apiLog.setUrl(propertiesconfig.getVoterverificationApiUrl());
		apiLog.setRequestBody(requestBodyJson);

		String response1 = null;
		try {
			logger.info("Sending request to API: {}", propertiesconfig.getVoterverificationApiUrl());
			response1 = restTemplate.postForObject(propertiesconfig.getVoterverificationApiUrl(), entity, String.class);
			logger.info("Received Response: {}", response1);

			apiLog.setResponseBody(response1);
			apiLog.setStatusCode(HttpStatus.OK.value());
			apiLog.setStatus("SUCCESS");
		} catch (HttpClientErrorException.TooManyRequests e) {
			logger.error("API rate limit exceeded: {}", e.getMessage());
			apiLog.setStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
			apiLog.setStatus("FAILURE");
			response1 = e.getResponseBodyAsString();
			apiLog.setResponseBodyAsJson("API rate limit exceeded");
		} catch (HttpClientErrorException.Unauthorized e) {
			logger.error("Unauthorized access - No API key found: {}", e.getMessage());
			apiLog.setStatusCode(HttpStatus.UNAUTHORIZED.value());
			apiLog.setStatus("FAILURE");
			response1 = e.getResponseBodyAsString();
			apiLog.setResponseBodyAsJson("No API key found in request");
		} catch (HttpClientErrorException e) {
			logger.error("HTTP error occurred: {}", e.getMessage());
			apiLog.setStatusCode(e.getStatusCode().value());
			apiLog.setStatus("FAILURE");
			response1 = e.getResponseBodyAsString();
			apiLog.setResponseBody(response1);
		} catch (Exception e) {
			logger.error("Unexpected error occurred: {}", e.getMessage());
			apiLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			apiLog.setStatus("ERROR");
			response1 = e.getMessage();
			apiLog.setResponseBody(response1);
		} finally {
			logger.info("API log saved for getVerify.");
			voterverificationlogrepository.save(apiLog);

		}

		return response1;
	}

}
