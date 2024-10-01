package com.saswat.kyc.serviceimpl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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

import com.google.gson.Gson;
import com.saswat.kyc.dto.IndividualPanRequest;
import com.saswat.kyc.model.IndividualPanApiLog;
import com.saswat.kyc.model.Individualpandetails;
import com.saswat.kyc.repository.IndividualPanApiLogRepository;
import com.saswat.kyc.repository.Individualpandetailsrepository;
import com.saswat.kyc.service.Individualpanservice;
import com.saswat.kyc.utils.PropertiesConfig;

@Service
public class Individualpanserviceimpl implements Individualpanservice {

	@Autowired
	IndividualPanApiLogRepository apiLogRepository;

	@Autowired
	PropertiesConfig propertiesConfig;

	@Autowired
	Individualpandetailsrepository individualpandetailsrepository;

	private static final Logger logger = LoggerFactory.getLogger(Individualpanserviceimpl.class);

	@Override
	public ResponseEntity<String> getPanVerification(IndividualPanRequest panRequest) {

		String response1 = null;
		IndividualPanApiLog apilog = new IndividualPanApiLog();

		HttpURLConnection connection = null;

		try {

			Gson gson = new Gson();
			String requestBodyJson = gson.toJson(panRequest);
			String urlParameters = requestBodyJson;

			URL url = new URL(propertiesConfig.getIndividualPanUrl());
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			//connection.setRequestProperty("x-client-unique-id", propertiesConfig.getXclientuniqueid());
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Authorization", propertiesConfig.getToken());
			connection.setDoOutput(true);

			
			apilog.setUrl(propertiesConfig.getIndividualPanUrl());
			apilog.setRequestBody(requestBodyJson);
			Individualpandetails indiviualpan = new Individualpandetails();
			indiviualpan.setAuthorizationToken(propertiesConfig.getToken());
			indiviualpan.setFuzzy(panRequest.getFuzzy());
			indiviualpan.setName(panRequest.getName());
			indiviualpan.setNumber(panRequest.getNumber());
			indiviualpan.setTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
			indiviualpan.setPanStatus("true");
			indiviualpan.setUrl(propertiesConfig.getIndividualPanUrl());
			indiviualpan.setStatusmsg("successfully sent");

			individualpandetailsrepository.save(indiviualpan);

		
			try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
				wr.writeBytes(urlParameters); 
				wr.flush();
			}

			int responseCode = connection.getResponseCode();
			logger.info("RequestBody: " + requestBodyJson);
			logger.info("individual pan url: " + propertiesConfig.getIndividualPanUrl());

			// Check for success response
			StringBuilder response = new StringBuilder();
			if (responseCode == HttpStatus.OK.value()) {
				// Reading Response
				try (BufferedReader br = new BufferedReader(
						new InputStreamReader(connection.getInputStream(), "utf-8"))) {

					String responseLine;
					while ((responseLine = br.readLine()) != null) {
						response.append(responseLine.trim());
					}

				}
				response1 = response.toString();
				apilog.setResponseBody(response1);
				apilog.setStatus("SUCCESS");
				apilog.setStatusCode(HttpStatus.OK.value());
				apilog.setResponseBody(response1);
				apilog.setApiType("Individual pan verification");
				logger.info("ResponseBody: " + response1);
				return ResponseEntity.status(responseCode).body(response1);
			} else {
				// Handle error stream
				try (BufferedReader br = new BufferedReader(
						new InputStreamReader(connection.getErrorStream(), "utf-8"))) {

					String responseLine;
					while ((responseLine = br.readLine()) != null) {
						response.append(responseLine.trim());
					}

				}
				response1 = response.toString();
				apilog.setResponseBody(response1);
				apilog.setStatus("FAILURE");
				apilog.setStatusCode(responseCode);
				apilog.setResponseBody(response1);
				apilog.setApiType("Individual pan verification");
				logger.error("Error ResponseBody: " + response1);
			}

			return ResponseEntity.status(responseCode).body(response1);

		} catch (Exception e) {
			apilog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			apilog.setStatus("ERROR");
			apilog.setApiType("Individual pan verification");
			response1 = e.getMessage();
			apilog.setResponseBody(response1);
			String errorMessage = "Error inserting into pan_verification_logs: " + e.getMessage();
			System.err.println(errorMessage);
			logger.error("ResponseBody: " + response1);
			apilog.setResponseBody(response1);
		} finally {
			apiLogRepository.save(apilog);
			if (connection != null) {
				connection.disconnect();
			}
		}
		return null;
	}

}
