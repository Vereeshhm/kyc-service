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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.saswat.kyc.dto.Voterdetaileddto;
import com.saswat.kyc.dto.Voterfetchdto;
import com.saswat.kyc.model.Voterdetailedsearchrequestentity;
import com.saswat.kyc.model.Voterfetchlog;
import com.saswat.kyc.model.voterdetailedlog;
import com.saswat.kyc.model.voterfetchrequestentity;
import com.saswat.kyc.repository.Voterdetailedsearchrequestentityrepository;
import com.saswat.kyc.repository.voterdetailedlogrepository;
import com.saswat.kyc.repository.voterfetchlogrepository;
import com.saswat.kyc.repository.voterfetchrequestentityrepository;
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

	@Autowired
	Voterdetailedsearchrequestentityrepository voterdetailedsearchrequestentityrepository;

	@Autowired
	voterfetchrequestentityrepository voterfetchrequestentityrepository;

	private static final Logger logger = LoggerFactory.getLogger(VoterServiceImpl.class);

	@Autowired
	PropertiesConfig propertiesconfig;

	@Override
	public String getfetchAll(Voterdetaileddto voterdetaileddto) {
		logger.info("Starting getfetchAll method.");
		voterdetailedlog apiLog = new voterdetailedlog();
		String response1 = null;
		HttpURLConnection connection = null;

		try {
			String APIURL = propertiesconfig.getVoterdetailedsearchApiURl();
			logger.info("API URL: {}", APIURL);

			Gson gson = new Gson();
			String requestBodyJson = gson.toJson(voterdetaileddto);

			// Save the request details to the database
			Voterdetailedsearchrequestentity voterdetailedsearchrequestentity = new Voterdetailedsearchrequestentity();
			voterdetailedsearchrequestentity.setEpicNumber(voterdetaileddto.getEpicNumber());
			voterdetailedsearchrequestentity.setGetAdditionalData(voterdetaileddto.getGetAdditionalData());
			voterdetailedsearchrequestentity.setStatusmsg("successfully sent");
			voterdetailedsearchrequestentity.setTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
			voterdetailedsearchrequestentityrepository.save(voterdetailedsearchrequestentity);

			// Create connection
			URL url = new URL(APIURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Authorization", propertiesconfig.getToken());
			connection.setRequestProperty("Accept", "application/json");
			connection.setDoOutput(true);
			//connection.setRequestProperty("x-client-unique-id", propertiesConfig.getXclientuniqueid());

			// Log request details
			logger.info("RequestBody: {}", requestBodyJson);
			apiLog.setUrl(APIURL);
			apiLog.setRequestBody(requestBodyJson);

			// Send the request body
			try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
				wr.writeBytes(requestBodyJson);
				wr.flush();
			}

			// Get the response code
			int responseCode = connection.getResponseCode();
			logger.info("Response Code: {}", responseCode);

			// Handle the response
			if (responseCode == HttpStatus.OK.value()) {
				try (BufferedReader in = new BufferedReader(
						new InputStreamReader(connection.getInputStream(), "utf-8"))) {
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = in.readLine()) != null) {
						response.append(line.trim());
					}
					response1 = response.toString();
					logger.info("Received Response: {}", response1);
				}
				apiLog.setResponseBody(response1);
				apiLog.setStatusCode(HttpStatus.OK.value());
				apiLog.setApiType("voter detailedsearch");
			} else {
				// Handle non-200 responses
				try (BufferedReader in = new BufferedReader(
						new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = in.readLine()) != null) {
						response.append(line.trim());
					}
					response1 = response.toString();
				}
				apiLog.setResponseBody(response1);
				apiLog.setStatusCode(responseCode);
				apiLog.setApiType("voter detailedsearch");
				logger.error("Error ResponseBody: {}", response1);
			}
		} catch (IOException e) {
			logger.error("Unexpected error occurred: {}", e.getMessage());
			apiLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response1 = "Internal server error: " + e.getMessage();
			apiLog.setApiType("voter detailedsearch");
			apiLog.setResponseBody(response1);
		} finally {
			voterdetailedlogrepository.save(apiLog);
			logger.info("API log saved for getfetchAll.");
			if (connection != null) {
				connection.disconnect();
			}
		}
		return response1;
	}

	@Override
	public String getfetchdetails(Voterfetchdto voterfetchdto) {
	    logger.info("Starting getfetchdetails method.");
	    Voterfetchlog apiLog1 = new Voterfetchlog();
	    String response2 = null;
	    HttpURLConnection connection = null;

	    try {
	        // Prepare API URL
	        String APIURL = propertiesconfig.getVotersearchApiURl();
	        logger.info("API URL: {}", APIURL);

	        // Convert request data to JSON
	        Gson gson = new Gson();
	        String requestBodyJson = gson.toJson(voterfetchdto);

	        // Save request data in the database
	        voterfetchrequestentity voterfetchrequestentity = new voterfetchrequestentity();
	        voterfetchrequestentity.setEpicNumber(voterfetchdto.getEpicNumber());
	        voterfetchrequestentity.setName(voterfetchdto.getName());
	        voterfetchrequestentity.setStatusmsg("successfully sent");
	        voterfetchrequestentity.setTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
	        voterfetchrequestentityrepository.save(voterfetchrequestentity);

	        // Initialize HttpURLConnection
	        URL url = new URL(APIURL);
	        connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Content-Type", "application/json");
	        connection.setRequestProperty("Authorization", propertiesconfig.getToken());
	        connection.setRequestProperty("Accept", "application/json");
	        connection.setDoOutput(true); // To allow sending request body
	        
	        //connection.setRequestProperty("x-client-unique-id", propertiesConfig.getXclientuniqueid());

	        // Log request details
	        logger.info("RequestBody: {}", requestBodyJson);
	        apiLog1.setUrl(APIURL);
	        apiLog1.setRequestBody(requestBodyJson);

	        // Send request body
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
	            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
	                
	                String responseLine;
	                while ((responseLine = br.readLine()) != null) {
	                    response.append(responseLine.trim());
	                }
	              
	            }
	            response2 = response.toString();
                logger.info("Received Response: {}", response2);
	            apiLog1.setResponseBody(response2);
	            apiLog1.setApiType("voter fetch");
	            apiLog1.setStatusCode(HttpStatus.OK.value());
	        } else {
	            // Read error response from ErrorStream
	            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
	                
	                String responseLine;
	                while ((responseLine = br.readLine()) != null) {
	                    response.append(responseLine.trim());
	                }
	                
	            }
	            response2 = response.toString();
	            apiLog1.setResponseBody(response2);
	            apiLog1.setStatusCode(responseCode);
	            apiLog1.setApiType("voter fetch");
	            logger.error("Error ResponseBody: {}", response2);
	        }
	    } catch (IOException e) {
	        logger.error("Unexpected error occurred: {}", e.getMessage());
	        apiLog1.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	        response2 = e.getMessage();
	        apiLog1.setApiType("voter fetch");
	        apiLog1.setResponseBody(response2);
	    } finally {
	        if (connection != null) {
	            connection.disconnect();
	        }
	        voterfetchlogrepository.save(apiLog1);
	        logger.info("API log saved for getfetchdetails.");
	    }
	    return response2;
	}

}
