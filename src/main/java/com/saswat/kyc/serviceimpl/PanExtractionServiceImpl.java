package com.saswat.kyc.serviceimpl;

import java.io.File;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saswat.kyc.dto.FileResponse;
import com.saswat.kyc.dto.PanFileData;
import com.saswat.kyc.model.FileApiLog;
import com.saswat.kyc.model.PanExtractionApiLog;
import com.saswat.kyc.repository.FileApiLogRepository;
import com.saswat.kyc.repository.PanExtractionApiLogRepository;
import com.saswat.kyc.service.PanExtractionService;
import com.saswat.kyc.utils.PropertiesConfig;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class PanExtractionServiceImpl implements PanExtractionService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	FileApiLogRepository fileapiLogRepository;
	
	@Autowired
	PanExtractionApiLogRepository extractionapiLogRepository;
	

	@Autowired
	PropertiesConfig propertiesConfig;

	private String directURL;

	private static final Logger logger = LoggerFactory.getLogger(PanExtractionServiceImpl.class);

	@Override
	public FileResponse getFileData(HttpServletRequest request, HttpServletResponse response1) {

		logger.info("Starting getFileData method.");
		FileResponse response = null;
		FileApiLog apiLog = new FileApiLog();
		String url = propertiesConfig.getPanextractionfileurl();

		try {
			String filePath = "C:/Users/VereeshHereemata/Pictures/WhatsApp Image 2024-06-20 at 5.21.00 PM.jpeg";
			String ttl = "3 years";
			String requestURL = request.getRequestURI().toString();

			logger.info("Request URL: {}", requestURL);
			logger.info("File path: {}, TTL: {}", filePath, ttl);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);

			Gson gson = new GsonBuilder().create();
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			File file = new File(filePath);
			body.add("file", new FileSystemResource(file));
			body.add("ttl", ttl);

			String requestBodyJson = "{ \"file\": \"" + file.getName() + "\", \"ttl\": \"" + ttl + "\" }";
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

			apiLog.setUrl(requestURL);
			apiLog.setRequestBody(requestBodyJson);

			logger.info("Sending request to API with body: {}", requestBodyJson);
			response = restTemplate.postForObject(url, requestEntity, FileResponse.class);
			apiLog.setResponseBody(gson.toJson(response));

			apiLog.setStatusCode(HttpStatus.OK.value());
			apiLog.setStatus("SUCCESS");

			this.directURL = response.getFile().getDirectURL();
			logger.info("Direct URL: {}", directURL);

			return response;
		} catch (HttpClientErrorException e) {
			logger.error("HTTP client error during file data retrieval", e);
			apiLog.setStatusCode(e.getStatusCode().value());
			apiLog.setStatus("FAILURE");
			String responseBody = e.getResponseBodyAsString();
			logger.error("Error response body: {}", responseBody);
			apiLog.setResponseBody(responseBody);

			response1.setStatus(e.getStatusCode().value());
			response1.setContentType("application/json");
			return new FileResponse();
		} catch (Exception e) {
			logger.error("Unexpected error during file data retrieval", e);
			apiLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			apiLog.setStatus("ERROR");
			String responseBody = e.getMessage();
			apiLog.setResponseBody(responseBody);
			response1.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response1.setContentType("application/json");
			return new FileResponse();
		} finally {
			fileapiLogRepository.save(apiLog);
			logger.info("File data retrieval process completed, API log saved.");
		}
	}

	@Override
	public String getPanExtractedData(PanFileData panfiledata) {

		logger.info("Starting getPanExtractedData method.");
		PanExtractionApiLog apiLog = new PanExtractionApiLog();
		String response = null;

		try {
			
			panfiledata.setFiles(Collections.singletonList(this.directURL));

			logger.info("Direct URL set in files parameter: {}", panfiledata.getFiles().get(0));
		

			// Optional: Retrieve other properties from pandto (type, getRelativeData)
			logger.info("PAN DTO details - Type: {}, GetRelativeData: {}", panfiledata.getType(), panfiledata.isGetRelativeData());

			Gson gson = new Gson();
			String requestBodyJson = gson.toJson(panfiledata);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", propertiesConfig.getToken());

			HttpEntity<String> entity = new HttpEntity<>(requestBodyJson, headers);

			apiLog.setUrl(propertiesConfig.getPanextractionurl());
			apiLog.setRequestBody(requestBodyJson);

			logger.info("Sending PAN extraction request with body: {}", requestBodyJson);
			response = restTemplate.postForObject(propertiesConfig.getPanextractionurl(), entity, String.class);
			logger.info("PAN extraction successful." +response);
			apiLog.setResponseBody(response);
			apiLog.setStatusCode(HttpStatus.OK.value());
			apiLog.setStatus("SUCCESS");

			return response;

		} catch (HttpClientErrorException.TooManyRequests e) {
			logger.error("Too many requests error during PAN extraction", e);
			apiLog.setStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
			apiLog.setStatus("FAILURE");
			response = e.getResponseBodyAsString();
			logger.error("Error response body: {}", response);
			apiLog.setResponseBodyAsJson("API rate limit exceeded");
		} catch (HttpClientErrorException.Unauthorized e) {
			logger.error("Unauthorized error during PAN extraction", e);
			apiLog.setStatusCode(HttpStatus.UNAUTHORIZED.value());
			apiLog.setStatus("FAILURE");
			response = e.getResponseBodyAsString();
			logger.error("Error response body: {}", response);
			apiLog.setResponseBodyAsJson("No API key found in request");
		} catch (HttpClientErrorException e) {
			logger.error("HTTP client error during PAN extraction", e);
			apiLog.setStatusCode(e.getStatusCode().value());
			apiLog.setStatus("FAILURE");
			response = e.getResponseBodyAsString();
			logger.error("Error response body: {}", response);
			apiLog.setResponseBody(response);
		} catch (Exception e) {
			logger.error("Unexpected error during PAN extraction", e);
			apiLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			apiLog.setStatus("ERROR");
			response = e.getMessage();
			logger.error("Error message: {}", response);
			apiLog.setResponseBody(response);
		} finally {
			extractionapiLogRepository.save(apiLog);
			logger.debug("PAN extraction process completed, API log saved.");
		}
		return response;
	}
}
