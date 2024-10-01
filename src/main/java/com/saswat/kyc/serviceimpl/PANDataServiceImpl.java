package com.saswat.kyc.serviceimpl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.saswat.kyc.dto.Panfetchdto;
import com.saswat.kyc.dto.panfetchrequest;
import com.saswat.kyc.model.PANDataApiLog;
import com.saswat.kyc.model.Panfetchv3details;
import com.saswat.kyc.model.panfetchrequestentity;
import com.saswat.kyc.repository.PANDataApiLogRepository;
import com.saswat.kyc.repository.Panfetchv3detailsrepository;
import com.saswat.kyc.repository.panfetchrequestentityrepository;
import com.saswat.kyc.service.PANDataService;
import com.saswat.kyc.utils.PropertiesConfig;

@Service
public class PANDataServiceImpl implements PANDataService {

	@Autowired
	PANDataApiLogRepository apiLogRepository;

	private static final Logger logger = LoggerFactory.getLogger(PANDataServiceImpl.class);

	@Autowired
	PropertiesConfig propertiesConfig;

//	@Autowired
//	private HikariDataSource dataSource;

	@Autowired
	Panfetchv3detailsrepository panfetchv3detailsrepository;

	@Autowired
	panfetchrequestentityrepository panfetchrequestentityrepository;

	@Override
	public ResponseEntity<String> getPanDetails(panfetchrequest fetchrequest) {
		logger.info("Starting getPanDetails method.");
		PANDataApiLog apiLog = new PANDataApiLog();
		String response1 = null;
		HttpURLConnection connection = null;
		Gson gson = new Gson();
		String panNumber = fetchrequest.getNumber();
		StringBuilder responseContent = new StringBuilder();
		String APIURL = propertiesConfig.getPanApiURL();
		logger.info("API URL: {}", APIURL);

		// Convert request to JSON
		String requestBodyJson = gson.toJson(fetchrequest);

		// Validate PAN number before making the API call
		if (!isValidPanNumber(panNumber)) {
			// Return error response for invalid PAN number with error object
			Map<String, Object> errorResponse = new HashMap<>();
			Map<String, String> errorDetails = new HashMap<>();

			errorDetails.put("name", "error");
			errorDetails.put("message", "PAN number is not valid.");
			errorDetails.put("status", "Bad Request");
			errorDetails.put("statusCode", String.valueOf(HttpStatus.BAD_REQUEST.value()));

			errorResponse.put("error", errorDetails);

			String errorResponseBodyJson = gson.toJson(errorResponse);
			logger.info("Error ResponseBody: {}", errorResponseBodyJson);
			apiLog.setUrl(APIURL);
			apiLog.setRequestBody(requestBodyJson);
			apiLog.setResponseBody(errorResponseBodyJson);
			apiLog.setStatusmsg("Failed");
			apiLog.setAuthorizationToken(propertiesConfig.getToken());
			apiLog.setStatusCode(HttpStatus.BAD_REQUEST.value());
			apiLog.setTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
			apiLog.setApiType("pan fetch v2");
			apiLogRepository.save(apiLog);

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseBodyJson);
		}

		try {
			// Create connection
			URL url = new URL(APIURL);
			connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Authorization", propertiesConfig.getToken());
			connection.setDoOutput(true);

			apiLog.setUrl(APIURL);
			apiLog.setRequestBody(requestBodyJson);
			logger.info("RequestBody: {}", requestBodyJson);
			apiLog.setAuthorizationToken(propertiesConfig.getToken());

			Panfetchv3details fetchdetails = new Panfetchv3details();
			fetchdetails.setAuthorizationToken(propertiesConfig.getToken());
			fetchdetails.setNumber(fetchrequest.getNumber());
			fetchdetails.setReturnIndividualTaxComplianceInfo(fetchrequest.getReturnIndividualTaxComplianceInfo());
			fetchdetails.setTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
			fetchdetails.setUrl(APIURL);
			fetchdetails.setStatusmsg("successfully sent");

			// Save request details to the repository
			panfetchv3detailsrepository.save(fetchdetails);

			// Write the request body to the connection
			try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
				wr.writeBytes(requestBodyJson);
				wr.flush();
			}

			// Get the response code
			int responseCode = connection.getResponseCode();
			logger.info("Response Code: {}", responseCode);

			// If the response is successful
			if (responseCode == HttpStatus.OK.value()) {
				try (BufferedReader in = new BufferedReader(
						new InputStreamReader(connection.getInputStream(), "utf-8"))) {
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						responseContent.append(inputLine);
					}
				}

				// Process the response
				response1 = responseContent.toString();
				apiLog.setResponseBody(response1);
				apiLog.setStatusCode(responseCode);
				apiLog.setTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
				apiLog.setStatusmsg("Success");
				apiLog.setApiType("Pan fetch v3");
				logger.info("Received Response body: {}", response1);

				// Parse and save the name from the response
				ObjectMapper mapper = new ObjectMapper();
				JsonNode jsonResponse = mapper.readTree(response1);
				JsonNode resultNode = jsonResponse.get("result");
				String name = resultNode.get("name").asText();
				fetchdetails.setName(name);

				panfetchv3detailsrepository.save(fetchdetails);

				return ResponseEntity.status(responseCode).body(response1);
			} else {
				// If the response is an error, read the error stream
				try (BufferedReader in = new BufferedReader(
						new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						responseContent.append(inputLine);
					}
				}

				// Process the error response
				response1 = responseContent.toString();
				apiLog.setResponseBody(response1);
				apiLog.setStatusCode(responseCode);
				apiLog.setTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
				apiLog.setStatusmsg("Failure");
				apiLog.setApiType("Pan fetch v3");
				logger.error("Error response body: {}", response1);

				// Return the error response with JSON headers
				return ResponseEntity.status(responseCode).body(response1);
			}

		} catch (IOException e) {
			// Handle general errors
			handleGeneralError(e, apiLog);
			response1 = e.getMessage();
			logger.error("ResponseBody: {}", response1);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response1);
		} finally {
			if (connection != null) {
				connection.disconnect();
				logger.debug("Connection disconnected.");
			}
			apiLogRepository.save(apiLog);
			logger.info("API log saved.");
		}
	}

	// Method to handle general errors
	private void handleGeneralError(Exception e, PANDataApiLog apiLog) {
		apiLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		apiLog.setResponseBody(e.getMessage());
		apiLog.setApiType("Pan fetch v3");
		apiLog.setStatusmsg("Failure");

		Panfetchv3details fetchdetails = new Panfetchv3details();
		panfetchrequest fetchrequest = new panfetchrequest();
		fetchdetails.setAuthorizationToken(propertiesConfig.getToken());
		fetchdetails.setNumber(fetchrequest.getNumber());
		fetchdetails.setReturnIndividualTaxComplianceInfo(fetchrequest.getReturnIndividualTaxComplianceInfo());
		fetchdetails.setTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
		fetchdetails.setUrl(propertiesConfig.getPanApiURL());
		fetchdetails.setStatusmsg("failed");

		panfetchv3detailsrepository.save(fetchdetails);

		logger.error("Exception occurred: {}", e.getMessage(), e);
	}

	// Method to validate PAN format
	public boolean isValidPan(String panNumber) {
		String panPattern = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
		return panNumber.matches(panPattern);
	}

	@Override
	public ResponseEntity<String> fetchByPanNumber(Panfetchdto panfetchdto) {

		String requestBodyJson = null;
		String response = null;
		HttpURLConnection connection = null;
		PANDataApiLog apiLog = new PANDataApiLog();

		try {
			String Apiurl = propertiesConfig.getPanfetchurl();
			logger.info("Sending Api URL: {}", Apiurl);

			Gson gson = new Gson();

			String panNumber = panfetchdto.getPanNumber();
			requestBodyJson = gson.toJson(panfetchdto);
			logger.info("Sending Request body: {}", requestBodyJson);
			// Validate PAN number before proceeding
			if (!isValidPanNumber(panNumber)) {
				// Return error response for invalid PAN number with error object
				Map<String, Object> errorResponse = new HashMap<>();
				Map<String, String> errorDetails = new HashMap<>();

				errorDetails.put("name", "error");
				errorDetails.put("message", "PAN number is not valid.");
				errorDetails.put("status", "Bad Request");
				errorDetails.put("statusCode", String.valueOf(HttpStatus.BAD_REQUEST.value()));

				errorResponse.put("error", errorDetails);

				String errorResponseBodyJson = gson.toJson(errorResponse);
				logger.info("Error ResponseBody:{}", gson.toJson(errorResponseBodyJson));
				apiLog.setUrl(Apiurl);
				apiLog.setRequestBody(requestBodyJson);
				apiLog.setResponseBody(errorResponseBodyJson);
				apiLog.setStatusmsg("Failed");
				apiLog.setAuthorizationToken(propertiesConfig.getToken());
				apiLog.setStatusCode(HttpStatus.BAD_REQUEST.value());
				apiLog.setTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
				apiLog.setApiType("pan fetch v3");
				apiLogRepository.save(apiLog);

				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseBodyJson);
			}

			// Proceed if the PAN number is valid

			panfetchrequestentity panfetchrequestentity = new panfetchrequestentity();
			panfetchrequestentity.setPanNumber(panfetchdto.getPanNumber());
			panfetchrequestentity.setStatusmsg("sent successfully");
			panfetchrequestentity.setCreatedDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

			panfetchrequestentityrepository.save(panfetchrequestentity);

			apiLog.setRequestBody(requestBodyJson);
			apiLog.setAuthorizationToken(propertiesConfig.getToken());
			apiLog.setUrl(Apiurl);

			URL url = new URL(Apiurl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Authorization", propertiesConfig.getToken());
			connection.setDoOutput(true);
			// connection.setRequestProperty("x-client-unique-id",
			// propertiesConfig.getXclientuniqueid());
			try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
				wr.writeBytes(requestBodyJson);
				wr.flush();
			}

			int responseCode = connection.getResponseCode();
			logger.info("Response Code: {}", responseCode);

			StringBuilder responseContent = new StringBuilder();

			// Handle success response
			if (responseCode == HttpStatus.OK.value()) {
				try (BufferedReader in = new BufferedReader(
						new InputStreamReader(connection.getInputStream(), "utf-8"))) {
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						responseContent.append(inputLine);
					}
				}

				response = responseContent.toString();
				apiLog.setResponseBody(response);
				apiLog.setStatusCode(responseCode);
				apiLog.setTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
				apiLog.setStatusmsg("Success");
				apiLog.setApiType("pan fetch v3");
				logger.info("Received Response body: {}", response);

				return ResponseEntity.status(responseCode).body(response);

			} else {
				// Handle error response from API
				try (BufferedReader in = new BufferedReader(
						new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						responseContent.append(inputLine);
					}
				}

				response = responseContent.toString();
				apiLog.setResponseBody(response);
				apiLog.setStatusCode(responseCode);
				apiLog.setTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
				apiLog.setStatusmsg("Failure");
				apiLog.setApiType("pan fetch v3");
				logger.error("Error response body: {}", response);

				return ResponseEntity.status(responseCode).body(response);
			}

		} catch (Exception e) {
			logger.error("Unexpected error during PAN fetch", e);
			response = "Internal server error: " + e.getMessage();
			apiLog.setResponseBody(response);
			apiLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			apiLog.setTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
			apiLog.setStatusmsg("Failure");
			apiLog.setApiType("pan fetch v3");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
			apiLogRepository.save(apiLog);
		}
	}

	// Method to validate PAN format
	public boolean isValidPanNumber(String panNumber) {
		String panPattern = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
		return panNumber.matches(panPattern);
	}

}
