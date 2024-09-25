package com.saswat.kyc.serviceimpl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
	public String getPanDetails(panfetchrequest fetchrequest) {

		logger.info("Starting getPanDetails method.");
		PANDataApiLog apiLog = new PANDataApiLog();
		String response1 = null;

		HttpURLConnection connection = null;

		try {
			String APIURL = propertiesConfig.getPanApiURL();

			logger.info("API URL: {}", APIURL);

			Gson gson = new Gson();
			String requestBodyJson = gson.toJson(fetchrequest);

			URL url = new URL(APIURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Authorization", propertiesConfig.getToken());

			// connection.setRequestProperty("x-client-unique-id",
			// propertiesConfig.getXclientuniqueid());

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

			panfetchv3detailsrepository.save(fetchdetails);

			try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
				wr.writeBytes(requestBodyJson);
				wr.flush();
			}

			int responseCode = connection.getResponseCode();
			logger.info("Response Code: {}", responseCode);
			StringBuilder responseContent = new StringBuilder();

			try (BufferedReader in = new BufferedReader(
					new InputStreamReader(responseCode == HttpURLConnection.HTTP_OK ? connection.getInputStream()
							: connection.getErrorStream()))) {
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					responseContent.append(inputLine);
				}
			}

			response1 = responseContent.toString();
			logger.info("ResponseBody: {}", response1);

			if (responseCode == HttpURLConnection.HTTP_OK) {
				apiLog.setStatusCode(HttpStatus.OK.value());
				apiLog.setResponseBody(response1);

				apiLog.setApiType("Pan fetch v2 ");
				apiLog.setStatusmsg("Success");

				ObjectMapper mapper = new ObjectMapper();
				JsonNode jsonResponse = mapper.readTree(response1);
				JsonNode resultNode = jsonResponse.get("result");

				// Extract the 'name' from the 'result' node
				String name = resultNode.get("name").asText();
				fetchdetails.setName(name);

				panfetchv3detailsrepository.save(fetchdetails);

				return response1;
			} else {
				handleHttpError(responseCode, response1, apiLog);
			}

		} catch (IOException e) {
			handleGeneralError(e, apiLog);
			response1 = e.getMessage();
			logger.error("ResponseBody: {}", response1);
		} finally {
			if (connection != null) {
				connection.disconnect();
				logger.debug("Connection disconnected.");
			}
			apiLogRepository.save(apiLog);
			logger.info("API log saved.");
		}
		return response1;
	}

	private void handleHttpError(int responseCode, String responseBody, PANDataApiLog apiLog) {
		HttpStatus status = HttpStatus.valueOf(responseCode);
		apiLog.setStatusCode(responseCode);
		apiLog.setResponseBody(responseBody);

		apiLog.setApiType("Pan fetch v2 ");
		apiLog.setStatusmsg("Failure");

		logger.error("Handling HTTP error. Status code: {}, Response body: {}", responseCode, responseBody);

		switch (status) {
		case TOO_MANY_REQUESTS:
			logger.error("Error Response: API rate limit exceeded");
			apiLog.setResponseBodyAsJson("API rate limit exceeded");
			break;
		case UNAUTHORIZED:
			logger.error("Error Response: No API key found in request");
			apiLog.setResponseBodyAsJson("No API key found in request");
			break;
		default:
			logger.error("Error Response Body: {}", responseBody);
			apiLog.setResponseBody(responseBody);
			break;
		}
	}

	private void handleGeneralError(Exception e, PANDataApiLog apiLog) {
		apiLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		apiLog.setResponseBody(e.getMessage());

		apiLog.setApiType("Pan fetch v2 ");

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

	@Override
	public ResponseEntity<String> fetchByPanNumber(Panfetchdto panfetchdto) {

		String requestBodyJson = null;
		String response = null;
		HttpURLConnection connection = null;
		PANDataApiLog apiLog = new PANDataApiLog();

		try {

			String Apiurl = propertiesConfig.getPanfetchurl();
			logger.info("Sending Api Url " + Apiurl);

			Gson gson = new Gson();

			requestBodyJson = gson.toJson(panfetchdto);

			logger.info("Sending Request body " + requestBodyJson);
			
			panfetchrequestentity panfetchrequestentity=new panfetchrequestentity();
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

			try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
				wr.writeBytes(requestBodyJson);
				wr.flush();
			}

			int responseCode = connection.getResponseCode();
			logger.info("Response Code: {}", responseCode);

			StringBuilder responseContent = new StringBuilder();

			if (responseCode == HttpStatus.OK.value()) {
				// Read the response from the input stream
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
				logger.info("Recieved Response body: {}", response);

				return ResponseEntity.status(responseCode).body(response);

			} else {

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
				logger.error("Error response body:{}", response);

				return ResponseEntity.status(responseCode).body(response);
			}

		} catch (Exception e) {
			logger.error("Unexpected error during pan fetch ", e);
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

}
