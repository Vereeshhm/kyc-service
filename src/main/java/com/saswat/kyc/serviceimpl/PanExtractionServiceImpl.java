package com.saswat.kyc.serviceimpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.saswat.kyc.dto.PanFileData;
import com.saswat.kyc.model.FileApiLog;
import com.saswat.kyc.model.PanExtractionApiLog;
import com.saswat.kyc.repository.FileApiLogRepository;
import com.saswat.kyc.repository.PanExtractionApiLogRepository;
import com.saswat.kyc.service.PanExtractionService;
import com.saswat.kyc.utils.PropertiesConfig;

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

	private static final Logger logger = LoggerFactory.getLogger(PanExtractionServiceImpl.class);

	public String getFileData(MultipartFile file, String ttl) {

		logger.info("Starting getFileData method.");
		FileApiLog apiLog = new FileApiLog();
		String url = propertiesConfig.getPanextractionfileurl();

		try {

			logger.info("Request URL: {}", url);
			logger.info("TTL: {}", ttl);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);

			// Construct the request body
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("file", new ByteArrayResource(file.getBytes()) {
				@Override
				public String getFilename() {
					return file.getOriginalFilename();
				}
			});
			body.add("ttl", ttl);

			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

			apiLog.setUrl(url);
			apiLog.setRequestBody("TTL: " + ttl); // Avoid logging large file data

			logger.info("Sending request to API with body: TTL: {}", ttl);
			String responseBody = restTemplate.postForObject(url, requestEntity, String.class);
			apiLog.setResponseBody(responseBody);

			apiLog.setStatusCode(HttpStatus.OK.value());
			apiLog.setStatus("SUCCESS");

			logger.info("Response body: {}", responseBody);

			return responseBody; // Return the full JSON response as a string
		} catch (HttpClientErrorException e) {
			logger.error("HTTP client error during file data retrieval", e);
			apiLog.setStatusCode(e.getStatusCode().value());
			apiLog.setStatus("FAILURE");
			String responseBody = e.getResponseBodyAsString();
			logger.error("Error response body: {}", responseBody);
			apiLog.setResponseBody(responseBody);

			apiLog.setStatusCode(e.getStatusCode().value());
			apiLog.setStatus("Failure");

			return "Error: " + responseBody; // Return an error message as a string
		} catch (Exception e) {
			logger.error("Unexpected error during file data retrieval", e);
			apiLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			apiLog.setStatus("ERROR");
			String responseBody = e.getMessage();
			apiLog.setResponseBody(responseBody);
			apiLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			apiLog.setStatus("Failure");

			return "Unexpected error: " + responseBody; // Return an unexpected error message as a string
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

			logger.info("PAN DTO details - Type: {}, GetRelativeData: {}", panfiledata.getType(),
					panfiledata.isGetRelativeData());

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
			logger.info("PAN extraction successful." + response);
			apiLog.setResponseBody(response);
			apiLog.setStatusCode(HttpStatus.OK.value());
			apiLog.setStatus("SUCCESS");

			return response;

		} catch (HttpClientErrorException.TooManyRequests e) {
			logger.error("Too many requests error during PAN extraction", e);
			apiLog.setStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
			apiLog.setStatus("FAILURE");
			response = "API rate limit exceeded";
			logger.error("Error response body: {}", response);
			apiLog.setResponseBody(response);
			;
		} catch (HttpClientErrorException.Unauthorized e) {
			logger.error("Unauthorized error during PAN extraction", e);
			apiLog.setStatusCode(HttpStatus.UNAUTHORIZED.value());
			apiLog.setStatus("FAILURE");
			response = "No API key found in request";
			logger.error("Error response body: {}", response);
			apiLog.setResponseBody(response);
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
