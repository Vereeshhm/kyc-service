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
import com.saswat.kyc.dto.DLnumberrequest;
import com.saswat.kyc.dto.Dlverificationrequest;
import com.saswat.kyc.model.Dlnumberbasedrequestentity;
import com.saswat.kyc.model.Dlnumbrfetchapilog;
import com.saswat.kyc.model.Dlverificationapilog;
import com.saswat.kyc.model.Dlverificationrequestentity;
import com.saswat.kyc.repository.Dlnumberbasedrequestentityrepository;
import com.saswat.kyc.repository.Dlnumberfetchlogrepository;
import com.saswat.kyc.repository.Dlverificationlogrepository;
import com.saswat.kyc.repository.Dlverificationrequestentityrepository;
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

	@Autowired
	Dlnumberbasedrequestentityrepository dlnumberbasedrequestentityrepository;

	@Autowired
	Dlverificationrequestentityrepository dlverificationrequestentityrepository;

	@Override
	public ResponseEntity<String> getVerfication(Dlverificationrequest dlverificationrequest) {
	    Dlverificationapilog apiLog = new Dlverificationapilog();
	    String response1 = null;
	    HttpURLConnection connection = null;

	    try {
	        String APIURL = propertiesConfig.getDlverificationURL();

	        // Construct the request body
	        Gson gson = new Gson();
	        String requestBodyJson = gson.toJson(dlverificationrequest);

	        // Save request to the database
	        Dlverificationrequestentity dlverificationrequestentity = new Dlverificationrequestentity();
	        dlverificationrequestentity.setDob(dlverificationrequest.getDob());
	        dlverificationrequestentity.setNumber(dlverificationrequest.getNumber());
	        dlverificationrequestentity.setIssueDate(dlverificationrequest.getIssueDate());
	        dlverificationrequestentity.setStatusmsg("successfully sent");
	        dlverificationrequestentity.setTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
	        dlverificationrequestentityrepository.save(dlverificationrequestentity);

	        // Open the connection
	        URL url = new URL(APIURL);
	        connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Content-Type", "application/json");
	        connection.setRequestProperty("Accept", "application/json");
	    //  connection.setRequestProperty("x-client-unique-id", propertiesConfig.getXclientuniqueid());
	        connection.setRequestProperty("Authorization", propertiesConfig.getToken());
	        connection.setDoOutput(true);

	        // Log the request
	        logger.info("Sending DL verification request to API URL: {}, Request Body: {}", APIURL, requestBodyJson);
	        apiLog.setUrl(APIURL);
	        apiLog.setRequestBody(requestBodyJson);

	        // Send the request body
	        try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
	            wr.writeBytes(requestBodyJson);
	            wr.flush();
	        }

	        int responseCode = connection.getResponseCode();
	        logger.info("Response Code: {}", responseCode);

	        // Check if the response was successful (HTTP 200)	
	        StringBuilder response = new StringBuilder();
	        if (responseCode == HttpStatus.OK.value()) {
	            // Read the response from the input stream
	            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
	               
	                String inputLine;
	                while ((inputLine = in.readLine()) != null) {
	                    response.append(inputLine);
	                }
	              
	                logger.info("Received DL verification response: {}", response1);
	            }
	            response1 = response.toString();
	            apiLog.setResponseBody(response1);
	            apiLog.setStatusCode(HttpStatus.OK.value());
	            apiLog.setApiType("Dl verification");
	            return ResponseEntity.status(responseCode).body(response1);
	        } else {
	            // If an error response (non-200), read from the error stream
	            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
	                
	                String inputLine;
	                while ((inputLine = in.readLine()) != null) {
	                    response.append(inputLine);
	                }
	                
	            }
	            
	            response1 = response.toString();
	            apiLog.setResponseBody(response1);
	            apiLog.setApiType("Dl verification");
	            apiLog.setStatusCode(responseCode);
	            logger.error("Error ResponseBody: {}", response1);
	            
	            return ResponseEntity.status(responseCode).body(response1);
	        }

	    } catch (Exception e) {
	        logger.error("Unexpected error during DL verification", e);
	        apiLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	        response1 = "Internal server error: " + e.getMessage();
	        apiLog.setApiType("Dl verification");
	        apiLog.setResponseBody(response1);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response1);
	    } finally {
	        dlverificationlogrepository.save(apiLog);
	        logger.debug("DL verification process completed, API log saved.");
	        if (connection != null) {
	            connection.disconnect();
	        }
	    }
	    
	}


	@Override
	public ResponseEntity<String> getFetchDetails(DLnumberrequest dlnumberrequest) {
	    logger.info("Starting DL details fetch process for DTO: {}", dlnumberrequest);
	    Dlnumbrfetchapilog apiLogentity = new Dlnumbrfetchapilog();
	    String response1 = null;
	    HttpURLConnection connection = null;

	    try {
	        String APIURL = propertiesConfig.getDlnumberbasedURL();

	        // Construct the request body
	        String requestBody = "{\"number\": \"" + dlnumberrequest.getNumber() + "\", \"dob\": \""
	                + dlnumberrequest.getDob() + "\"}";
	        logger.info("Constructed request body: {}", requestBody);

	        // Save request to the database
	        Dlnumberbasedrequestentity dlnumberbasedrequestentity = new Dlnumberbasedrequestentity();
	        dlnumberbasedrequestentity.setNumber(dlnumberrequest.getNumber());
	        dlnumberbasedrequestentity.setDob(dlnumberrequest.getDob());
	        dlnumberbasedrequestentity.setStatusmsg("successfully sent");
	        dlnumberbasedrequestentity.setTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
	        dlnumberbasedrequestentityrepository.save(dlnumberbasedrequestentity);

	        // Open the connection
	        URL url = new URL(APIURL);
	        connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Content-Type", "application/json");
	        connection.setRequestProperty("Authorization", propertiesConfig.getToken());
	        connection.setRequestProperty("Accept", "application/json"); // Added as per request
	        connection.setDoOutput(true);
	    // connection.setRequestProperty("x-client-unique-id", propertiesConfig.getXclientuniqueid());

	        // Log the request
	        logger.info("Sending DL details fetch request to API URL: {}, Request Body: {}", APIURL, requestBody);
	        apiLogentity.setUrl(APIURL);
	        apiLogentity.setRequestBody(requestBody);

	        // Send the request body
	        try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
	            wr.writeBytes(requestBody);
	            wr.flush();
	        }

	        int responseCode = connection.getResponseCode();
	        logger.info("Response Code: {}", responseCode);

	        // Check if the response was successful (HTTP 200)
	        StringBuilder response = new StringBuilder();
	        if (responseCode == HttpStatus.OK.value()) {
	            // Read the response from the input stream
	            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
	               
	                String line;
	                while ((line = in.readLine()) != null) {
	                    response.append(line.trim());
	                }
	              
	                
	            }
	            response1 = response.toString();
	            logger.info("Received DL details fetch response: {}", response1);
	            apiLogentity.setResponseBody(response1);
	            apiLogentity.setStatusCode(HttpStatus.OK.value());
	            apiLogentity.setApiType("Dl numberbased");
	            
	            return ResponseEntity.status(responseCode).body(response1);
	        } else {
	            // If an error response (non-200), read from the error stream
	            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
	             
	                String line;
	                while ((line = in.readLine()) != null) {
	                    response.append(line.trim());
	                }
	              
	            }
	            response1 = response.toString();
	            apiLogentity.setResponseBody(response1);
	            apiLogentity.setStatusCode(responseCode);
	            apiLogentity.setApiType("Dl numberbased");
	            logger.error("Error ResponseBody: {}", response1);
	            return ResponseEntity.status(responseCode).body(response1);
	        }

	    } catch (IOException e) {
	        logger.error("Unexpected error during DL details fetch", e);
	        apiLogentity.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	        response1 = "Internal server error: " + e.getMessage();
	        apiLogentity.setApiType("Dl numberbased");
	        apiLogentity.setResponseBody(response1);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response1);
	    } finally {
	        dlnumberfetchlogrepository.save(apiLogentity);
	        logger.info("DL details fetch process completed, API log saved.");
	        if (connection != null) {
	            connection.disconnect();
	        }
	    }
	    
	}

}
