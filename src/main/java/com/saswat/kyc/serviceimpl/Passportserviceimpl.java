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
import com.saswat.kyc.dto.PassportNumberDto;
import com.saswat.kyc.dto.PassportVerificationDto;
import com.saswat.kyc.model.Passportnumberedlog;
import com.saswat.kyc.model.Passportverficationlog;
import com.saswat.kyc.repository.Passportnumberedlogrepository;
import com.saswat.kyc.repository.Passportverificationlogrepository;
import com.saswat.kyc.service.Passportservice;
import com.saswat.kyc.utils.PropertiesConfig;

@Service
public class Passportserviceimpl implements Passportservice {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	Passportverificationlogrepository passportverificationlogrepository;

	@Autowired
	Passportnumberedlogrepository passportnumberedlogrepository;

	private static final Logger logger = LoggerFactory.getLogger(Passportserviceimpl.class);

	@Autowired
	PropertiesConfig propertiesconfig;;

	@Override
	public String getVerify(PassportVerificationDto passportVerificationDto) {
		logger.info("Starting getVerify method.");
		Passportverficationlog apiLog = new Passportverficationlog();
		String response1 = null;
		try {
			String APIURL = propertiesconfig.getPassportverifyApiURl();

			logger.info("API URL: {}", APIURL);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", propertiesconfig.getToken());

			Gson gson = new Gson();
			String requestBodyJson = gson.toJson(passportVerificationDto);

			logger.info("RequestBody: {}", requestBodyJson);
			HttpEntity<String> request1 = new HttpEntity<>(requestBodyJson, headers);

			apiLog.setUrl(APIURL);
			apiLog.setRequestBody(requestBodyJson);

			logger.info("Sending request to verification API.");
			response1 = restTemplate.postForObject(APIURL, request1, String.class);
			logger.info("Received Response: {}", response1);

			apiLog.setResponseBody(response1);
			apiLog.setStatusCode(HttpStatus.OK.value());
			return response1;
		} catch (HttpClientErrorException.TooManyRequests e) {
			logger.error("API rate limit exceeded: {}", e.getMessage());
			apiLog.setStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());

			response1 = e.getResponseBodyAsString();
			apiLog.setResponseBodyAsJson("API rate limit exceeded");
		} catch (HttpClientErrorException.Unauthorized e) {
			logger.error("Unauthorized access - No API key found: {}", e.getMessage());
			apiLog.setStatusCode(HttpStatus.UNAUTHORIZED.value());

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
			passportverificationlogrepository.save(apiLog);
			logger.info("API log saved for getVerify.");
		}
		return response1;
	}

	@Override
	public String getDetails(PassportNumberDto pasportdto) {
		logger.info("Starting getDetails method.");
		Passportnumberedlog apiLogentity = new Passportnumberedlog();
		String response1 = null;
		try {
			String APIURL = propertiesconfig.getPassportnumberbasedApiURl();

			logger.info("API URL: {}", APIURL);

			pasportdto.getFileNumber();
			pasportdto.getDob();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", propertiesconfig.getToken());

			Gson gson = new Gson();
			String requestBodyJson = gson.toJson(pasportdto);

			logger.info("RequestBody: {}", requestBodyJson);
			HttpEntity<String> request1 = new HttpEntity<>(requestBodyJson, headers);

			apiLogentity.setUrl(APIURL);
			apiLogentity.setRequestBody(requestBodyJson);

			logger.info("Sending request to passport API.");
			response1 = restTemplate.postForObject(APIURL, request1, String.class);
			logger.info("Received Response: {}", response1);

			apiLogentity.setResponseBody(response1);
			apiLogentity.setStatusCode(HttpStatus.OK.value());
			return response1;
		} catch (HttpClientErrorException.TooManyRequests e) {
			logger.error("API rate limit exceeded: {}", e.getMessage());
			apiLogentity.setStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());

			response1 = e.getResponseBodyAsString();
			apiLogentity.setResponseBodyAsJson("API rate limit exceeded");
		} catch (HttpClientErrorException.Unauthorized e) {
			logger.error("Unauthorized access - No API key found: {}", e.getMessage());
			apiLogentity.setStatusCode(HttpStatus.UNAUTHORIZED.value());

			response1 = e.getResponseBodyAsString();
			apiLogentity.setResponseBodyAsJson("No API key found in request");
		} catch (HttpClientErrorException e) {
			logger.error("HTTP error occurred: {}", e.getMessage());
			apiLogentity.setStatusCode(e.getStatusCode().value());

			response1 = e.getResponseBodyAsString();
			apiLogentity.setResponseBody(response1);
		} catch (Exception e) {
			logger.error("Unexpected error occurred: {}", e.getMessage());
			apiLogentity.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

			response1 = e.getMessage();
			apiLogentity.setResponseBody(response1);
		} finally {
			passportnumberedlogrepository.save(apiLogentity);
			logger.info("API log saved for getDetails.");
		}
		return response1;
	}
}
