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
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.saswat.kyc.dto.Voterverificationdto;
import com.saswat.kyc.model.Voterverificationrequestentity;
import com.saswat.kyc.model.voterverificationlog;
import com.saswat.kyc.repository.Voterverificationrequestentityrepository;
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

	@Autowired
	Voterverificationrequestentityrepository voterverificationrequestentityrepository;

	@Override
	public ResponseEntity<String> getVerify(Voterverificationdto voterverificationdto) {
		logger.info("Starting getVerify method.");
		voterverificationlog apiLog = new voterverificationlog();
		String response1 = null;
		HttpURLConnection connection = null;

		try {
			// Prepare API URL
			String APIURL = propertiesconfig.getVoterverificationApiUrl();
			logger.info("Request URL: {}", APIURL);

			// Convert request data to JSON
			Gson gson = new Gson();
			String requestBodyJson = gson.toJson(voterverificationdto);

			// Save request data in the database
			Voterverificationrequestentity voterverificationrequestentity = new Voterverificationrequestentity();
			voterverificationrequestentity.setEpicNumber(voterverificationdto.getEpicNumber());
			voterverificationrequestentity.setName(voterverificationdto.getName());
			voterverificationrequestentity.setState(voterverificationdto.getState());
			voterverificationrequestentity.setStatusmsg("successfully sent");
			voterverificationrequestentity.setTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
			voterverificationrequestentityrepository.save(voterverificationrequestentity);

			// Initialize HttpURLConnection
			URL url = new URL(APIURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Authorization", propertiesconfig.getToken());
			connection.setRequestProperty("Accept", "application/json");
			connection.setDoOutput(true); // To allow sending request body
			// connection.setRequestProperty("x-client-unique-id",propertiesconfig.getXclientuniqueid());

			logger.info("Request Body: {}", requestBodyJson);
			apiLog.setUrl(APIURL);
			apiLog.setRequestBody(requestBodyJson);

			try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
				wr.writeBytes(requestBodyJson);
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
						new InputStreamReader(connection.getInputStream(), "utf-8"))) {

					String responseLine;
					while ((responseLine = br.readLine()) != null) {
						response.append(responseLine.trim());
					}

				}
				response1 = response.toString();
				logger.info("Received Response: {}", response1);
				apiLog.setResponseBody(response1);
				apiLog.setStatusCode(HttpStatus.OK.value());
				apiLog.setApiType("voter verification");
				apiLog.setStatus("SUCCESS");
				return ResponseEntity.status(responseCode).body(response1);
			} else {
				// Read error response from ErrorStream
				try (BufferedReader br = new BufferedReader(
						new InputStreamReader(connection.getErrorStream(), "utf-8"))) {

					String responseLine;
					while ((responseLine = br.readLine()) != null) {
						response.append(responseLine.trim());
					}

				}
				response1 = response.toString();
				apiLog.setResponseBody(response1);
				apiLog.setApiType("voter verification");
				apiLog.setStatusCode(responseCode);
				apiLog.setStatus("FAILURE");
				logger.error("Error ResponseBody: {}", response1);
				return ResponseEntity.status(responseCode).body(response1);
			}
		} catch (IOException e) {
			logger.error("Unexpected error occurred: {}", e.getMessage());
			apiLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			apiLog.setStatus("ERROR");
			response1 = "Internal server error: " + e.getMessage();
			apiLog.setResponseBody(response1);
			apiLog.setApiType("voter verification");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response1);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
			logger.info("API log saved for getVerify.");
			voterverificationlogrepository.save(apiLog);
		}

	}

}
