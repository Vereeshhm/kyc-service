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
import com.saswat.kyc.dto.Voterdetaileddto;
import com.saswat.kyc.dto.Voterfetchdto;
import com.saswat.kyc.model.Voterfetchlog;
import com.saswat.kyc.model.voterdetailedlog;
import com.saswat.kyc.repository.voterdetailedlogrepository;
import com.saswat.kyc.repository.voterfetchlogrepository;
import com.saswat.kyc.service.Voterservice;
import com.saswat.kyc.utils.PropertiesConfig;

@Service
public class VoterServiceImpl implements Voterservice {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	voterdetailedlogrepository voterdetailedlogrepository;

	@Autowired
	voterfetchlogrepository voterfetchlogrepository;

	private static final Logger logger = LoggerFactory.getLogger(VoterServiceImpl.class);

	@Autowired
	PropertiesConfig propertiesconfig;

	@Override
	public String getfetchAll(Voterdetaileddto voterdetaileddto) {
		logger.info("Starting getfetchAll method.");
		voterdetailedlog apiLog = new voterdetailedlog();
		String response1 = null;

		try {
			String APIURL = propertiesconfig.getVoterdetailedsearchApiURl();

			logger.info("API URL: {}", APIURL);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", propertiesconfig.getToken()); // Include API key directly without "Bearer"
																		// prefix

			Gson gson = new Gson();
			String requestBodyJson = gson.toJson(voterdetaileddto);

			logger.info("RequestBody: {}", requestBodyJson);
			HttpEntity<String> request1 = new HttpEntity<>(requestBodyJson, headers);

			logger.info("Sending request to voter API.");
			response1 = restTemplate.postForObject(APIURL, request1, String.class);
			logger.info("Received Response: {}", response1);

			apiLog.setUrl(APIURL);
			apiLog.setRequestBody(requestBodyJson);
			apiLog.setResponseBody(response1);
			apiLog.setStatusCode(HttpStatus.OK.value());
		} catch (HttpClientErrorException.TooManyRequests e) {
			logger.error("API rate limit exceeded: {}", e.getMessage());
			apiLog.setStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());

			response1 = e.getResponseBodyAsString();
			apiLog.setResponseBodyAsJson("API rate limit exceeded");
		} catch (HttpClientErrorException.Unauthorized e) {
			logger.error("Unauthorized access - No API key found: {}", e.getMessage());
			apiLog.setStatusCode(HttpStatus.UNAUTHORIZED.value());

			response1 = e.getResponseBodyAsString();

			response1 = e.getResponseBodyAsString();

			apiLog.setResponseBodyAsJson("No API key found in request");
		} catch (HttpClientErrorException e) {
			logger.error("HTTP error occurred: {}", e.getMessage());
			apiLog.setStatusCode(e.getStatusCode().value());

			response1 = e.getResponseBodyAsString();
			apiLog.setResponseBody(response1);
		} catch (Exception e) {
			logger.error("Unexpected error occurred: {}", e.getMessage());
			apiLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

			response1 = e.getMessage();
			apiLog.setResponseBody(response1);
		} finally {

			voterdetailedlogrepository.save(apiLog);
			logger.info("API log saved for getfetchAll.");
		}
		return response1;
	}

	@Override
	public String getfetchdetails(Voterfetchdto voterfetchdto) {
		logger.info("Starting getfetchdetails method.");
		Voterfetchlog apiLog1 = new Voterfetchlog();
		String response2 = null;

		try {
			String APIURL = propertiesconfig.getVotersearchApiURl();

			logger.info("API URL: {}", APIURL);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", propertiesconfig.getToken()); // Include API key directly without "Bearer"
																		// prefix

			Gson gson = new Gson();
			String requestBodyJson = gson.toJson(voterfetchdto);

			logger.info("RequestBody: {}", requestBodyJson);
			HttpEntity<String> request2 = new HttpEntity<>(requestBodyJson, headers);

			logger.info("Sending request to search API.");
			response2 = restTemplate.postForObject(APIURL, request2, String.class);
			logger.info("Received Response: {}", response2);

			apiLog1.setUrl(propertiesconfig.getVotersearchApiURl());
			apiLog1.setRequestBody(requestBodyJson);
			apiLog1.setResponseBody(response2);
			apiLog1.setStatusCode(HttpStatus.OK.value());
		} catch (HttpClientErrorException.TooManyRequests e) {
			logger.error("API rate limit exceeded: {}", e.getMessage());
			apiLog1.setStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());

			response2 = e.getResponseBodyAsString();
			apiLog1.setResponseBodyAsJson("API rate limit exceeded");
		} catch (HttpClientErrorException.Unauthorized e) {
			logger.error("Unauthorized access - No API key found: {}", e.getMessage());
			apiLog1.setStatusCode(HttpStatus.UNAUTHORIZED.value());

			response2 = e.getResponseBodyAsString();
			apiLog1.setResponseBodyAsJson("No API key found in request");
		} catch (HttpClientErrorException e) {
			logger.error("HTTP error occurred: {}", e.getMessage());
			apiLog1.setStatusCode(e.getStatusCode().value());

			response2 = e.getResponseBodyAsString();
			apiLog1.setResponseBody(response2);
		} catch (Exception e) {
			logger.error("Unexpected error occurred: {}", e.getMessage());
			apiLog1.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

			response2 = e.getMessage();
			apiLog1.setResponseBody(response2);
		} finally {
			voterfetchlogrepository.save(apiLog1);
			logger.info("API log saved for getfetchdetails.");
		}
		return response2;
	}
}
