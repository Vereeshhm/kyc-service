package com.saswat.kyc.serviceimpl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.saswat.kyc.dto.Experianbureaudto;
import com.saswat.kyc.model.ExperianbureauLog;
import com.saswat.kyc.model.Experianbureaurequestentity;
import com.saswat.kyc.repository.Experianbureaulogrepository;
import com.saswat.kyc.repository.Experianbureaurequestentityrepository;
import com.saswat.kyc.service.Experianbureauservice;
import com.saswat.kyc.utils.PropertiesConfig;

@Service
public class Experianbureauserviceimpl implements Experianbureauservice {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	Experianbureaulogrepository experianbureaulogrepository;

	@Autowired
	PropertiesConfig config;

	@Autowired
	Experianbureaurequestentityrepository experianbureaurequestentityrepository;

	private static final Logger logger = LoggerFactory.getLogger(Experianbureauserviceimpl.class);

	@Override
	public ResponseEntity<String> getBureauReport(Experianbureaudto bureauDto) {

		logger.info("Starting bureau report retrieval for request: {}", bureauDto);

		String urlString = config.getExperianbureauurl();
		logger.info("API URL: {}", urlString);

		logger.info(
				"Request Details - PhoneNumber: {}, PAN: {}, FirstName: {}, LastName: {}, Pincode: {}, DateOfBirth: {}",
				bureauDto.getPhoneNumber(), bureauDto.getPan(), bureauDto.getFirstName(), bureauDto.getLastName(),
				bureauDto.getPincode(), bureauDto.getDateOfBirth());

		Gson gson = new Gson();
		String requestBodyJson = gson.toJson(bureauDto);

		Experianbureaurequestentity experianbureaurequestentity = new Experianbureaurequestentity();
		experianbureaurequestentity.setDateOfBirth(bureauDto.getDateOfBirth());
		experianbureaurequestentity.setFirstName(bureauDto.getFirstName());
		experianbureaurequestentity.setLastName(bureauDto.getLastName());
		experianbureaurequestentity.setPan(bureauDto.getPan());
		experianbureaurequestentity.setPhoneNumber(bureauDto.getPhoneNumber());
		experianbureaurequestentity.setPincode(bureauDto.getPincode());
		experianbureaurequestentity.setStatusmsg("successfully sent");
		experianbureaurequestentity.setTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
		experianbureaurequestentityrepository.save(experianbureaurequestentity);
		ExperianbureauLog apiLog = new ExperianbureauLog();
		apiLog.setUrl(urlString);
		apiLog.setRequestBody(requestBodyJson);

		HttpURLConnection connection = null;
		String response1 = null;
		try {
			// Create URL object and open the connection
			URL url = new URL(urlString);
			connection = (HttpURLConnection) url.openConnection();

			// Set up the connection properties

			connection.setInstanceFollowRedirects(true);

			// Set up the connection properties
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Authorization", config.getToken());
			connection.setDoOutput(true);
			//connection.setRequestProperty("x-client-unique-id", config.getXclientuniqueid());

			// Write request body (JSON)
			try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
				wr.write(requestBodyJson.getBytes(StandardCharsets.UTF_8));
				wr.flush();
			}

			// Get the response code
			int responseCode = connection.getResponseCode();
			logger.info("Response Code: {}", responseCode);

			// Handle the response
			StringBuilder response = new StringBuilder();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// Read response from InputStream
				try (BufferedReader br = new BufferedReader(
						new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {

					String responseLine;
					while ((responseLine = br.readLine()) != null) {
						response.append(responseLine.trim());
					}
				}
				response1 = response.toString();
				logger.info("Received Response: {}", response1);
				apiLog.setResponseBody(response1);
				apiLog.setStatusCode(HttpStatus.OK.value());
				apiLog.setApiType("experian bureau");

				apiLog.setStatus("SUCCESS");
				return ResponseEntity.status(responseCode).body(response1);

			} else {
				// Read error response from ErrorStream
				try (BufferedReader br = new BufferedReader(
						new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {

					String responseLine;
					while ((responseLine = br.readLine()) != null) {
						response.append(responseLine.trim());
					}
				}
				response1 = response.toString();
				logger.error("Error ResponseBody: {}", response1);
				apiLog.setResponseBody(response1);
				apiLog.setApiType("experian bureau");

				apiLog.setStatusCode(responseCode);
				apiLog.setStatus("FAILURE");

				return ResponseEntity.status(responseCode).body(response1);
			}

		} catch (IOException e) {
			logger.error("Unexpected error occurred: {}", e.getMessage());
			apiLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			apiLog.setStatus("ERROR");
			response1 = "Internal server error: " + e.getMessage();
			apiLog.setResponseBody(response1);
			apiLog.setApiType("experian bureau");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response1);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
			experianbureaulogrepository.save(apiLog);
			logger.info("Bureau report retrieval process completed, API log saved.");
		}

		
	}

}