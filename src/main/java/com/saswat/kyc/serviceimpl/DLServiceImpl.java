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
import com.saswat.kyc.dto.DLnumberrequest;
import com.saswat.kyc.dto.Dlverificationrequest;
import com.saswat.kyc.model.Dlnumbrfetchapilog;
import com.saswat.kyc.model.Dlverificationapilog;
import com.saswat.kyc.repository.Dlnumberfetchlogrepository;
import com.saswat.kyc.repository.Dlverificationlogrepository;
import com.saswat.kyc.service.DLService;
import com.saswat.kyc.utils.PropertiesConfig;

@Service
public class DLServiceImpl implements DLService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	Dlverificationlogrepository dlverificationlogrepository;

	@Autowired
	Dlnumberfetchlogrepository dlnumberfetchlogrepository;

	private static final Logger logger = LoggerFactory.getLogger(DLServiceImpl.class);

	@Autowired
	PropertiesConfig propertiesConfig;

	@Override
	public String getVerfication(Dlverificationrequest dlverificationrequest) {
		Dlverificationapilog apiLog = new Dlverificationapilog();
		String response1 = null;
		try {
			String APIURL = propertiesConfig.getDlverificationURL();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", propertiesConfig.getToken()); // Include API key directly without

			String requestBody = "{\"number\": \"" + dlverificationrequest.getNumber() + "\", \"dob\": \""
					+ dlverificationrequest.getDob() + "\", \"issueDate\": \"" + dlverificationrequest.getIssueDate()
					+ "\"}";

			Gson gson = new Gson();

			String requestBodyJson = gson.toJson(dlverificationrequest);

			HttpEntity<String> request2 = new HttpEntity<>(requestBodyJson, headers);
			logger.info("Sending DL verification request to API URL: {}, Request Body: {}", APIURL, requestBodyJson);

			System.out.println("Requestbody  " + requestBody);

			apiLog.setUrl(APIURL);
			apiLog.setRequestBody(requestBodyJson);

			response1 = restTemplate.postForObject(APIURL, request2, String.class);
			logger.info("Received DL verification response: {}", response1);
			apiLog.setResponseBody(response1);
			apiLog.setStatusCode(HttpStatus.OK.value());
			return response1;
		}

		catch (HttpClientErrorException.TooManyRequests e) {
			// Handling Too Many Requests Exception specifically
			logger.error("Too many requests error during DL verification", e);
			apiLog.setStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());

			response1 = e.getResponseBodyAsString();
			System.out.println(response1 + "Response");
			apiLog.setResponseBodyAsJson("API rate limit exceeded");
		} catch (HttpClientErrorException.Unauthorized e) {
			logger.error("Unauthorized error during DL verification", e);
			// Handling Unauthorized Exception specifically
			apiLog.setStatusCode(HttpStatus.UNAUTHORIZED.value());

			response1 = e.getResponseBodyAsString();
			System.out.println(response1 + "Response");
			apiLog.setResponseBodyAsJson("No API key found in request");

		}

		catch (HttpClientErrorException e) {
			logger.error("HTTP client error during DL verification", e);
			apiLog.setStatusCode(e.getStatusCode().value());

			response1 = e.getResponseBodyAsString();
			System.out.println(response1 + " Response ");
			apiLog.setResponseBody(response1);
		}

		catch (Exception e) {
			logger.error("Unexpected error during DL verification", e);
			apiLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

			response1 = e.getMessage();
			apiLog.setResponseBody(response1);
		} finally {
			dlverificationlogrepository.save(apiLog);
			logger.debug("DL verification process completed, API log saved.");
		}
		return response1;
	}

	@Override
	public String getFetchDetails(DLnumberrequest dlnumberrequest) {
		logger.info("Starting DL details fetch process for DTO: {}", dlnumberrequest);
		Dlnumbrfetchapilog apiLogentity = new Dlnumbrfetchapilog();
		String response1 = null;
		try {
			String APIURL = propertiesConfig.getDlnumberbasedURL();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", propertiesConfig.getToken()); // Include API key directly without

			
			String requestBody = "{\"number\": \"" + dlnumberrequest.getNumber() + "\", \"dob\": \""
					+ dlnumberrequest.getDob() + "\"}";

			Gson gson = new Gson();

			String requestBodyJson = gson.toJson(dlnumberrequest);

			HttpEntity<String> request1 = new HttpEntity<>(requestBodyJson, headers);
			logger.info("Sending DL details fetch request to API URL: {}, Request Body: {}", APIURL, requestBodyJson);

			System.out.println("Requestbody  " + requestBody);

			apiLogentity.setUrl(APIURL);
			apiLogentity.setRequestBody(requestBodyJson);

			response1 = restTemplate.postForObject(APIURL, request1, String.class);

			logger.info("Received DL details fetch response: {}", response1);
			apiLogentity.setResponseBody(response1);
			apiLogentity.setStatusCode(HttpStatus.OK.value());
			return response1;
		} catch (HttpClientErrorException.TooManyRequests e) {
			// Handling Too Many Requests Exception specifically
			logger.error("Too many requests error during DL details fetch", e);
			apiLogentity.setStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());

			response1 = e.getResponseBodyAsString();
			System.out.println(response1 + "Response");
			apiLogentity.setResponseBodyAsJson("API rate limit exceeded");
		} catch (HttpClientErrorException.Unauthorized e) {
			logger.error("Unauthorized error during DL details fetch", e);
			// Handling Unauthorized Exception specifically
			apiLogentity.setStatusCode(HttpStatus.UNAUTHORIZED.value());

			response1 = e.getResponseBodyAsString();
			System.out.println(response1 + "Response");
			apiLogentity.setResponseBodyAsJson("No API key found in request");

		}

		catch (HttpClientErrorException e) {

			logger.error("HTTP client error during DL details fetch", e);
			apiLogentity.setStatusCode(e.getStatusCode().value());

			response1 = e.getResponseBodyAsString();
			System.out.println(response1 + " Response ");
			apiLogentity.setResponseBody(response1);
		}

		catch (Exception e) {
			logger.error("Unexpected error during DL details fetch", e);
			apiLogentity.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

			response1 = e.getMessage();
			apiLogentity.setResponseBody(response1);
		}

		finally {
			dlnumberfetchlogrepository.save(apiLogentity);
			logger.info("DL details fetch process completed, API log saved.");
		}
		return response1;

	}

}
