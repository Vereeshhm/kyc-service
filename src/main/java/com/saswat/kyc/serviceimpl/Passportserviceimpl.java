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
import com.saswat.kyc.dto.PassportNumberDto;
import com.saswat.kyc.dto.PassportVerificationDto;
import com.saswat.kyc.model.Passportfetchrequestentity;
import com.saswat.kyc.model.Passportnumberedlog;
import com.saswat.kyc.model.Passportverficationlog;
import com.saswat.kyc.model.Passportverificationrequestentity;
import com.saswat.kyc.repository.Passportfetchrequestentityrepository;
import com.saswat.kyc.repository.Passportnumberedlogrepository;
import com.saswat.kyc.repository.Passportverificationlogrepository;
import com.saswat.kyc.repository.Passportverificationrequestentityrepository;
import com.saswat.kyc.service.Passportservice;
import com.saswat.kyc.utils.PropertiesConfig;

@Service
public class Passportserviceimpl implements Passportservice {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	Passportverificationlogrepository passportverificationlogrepository;

	@Autowired
	Passportnumberedlogrepository passportnumberedlogrepository;

	private static final Logger logger = LoggerFactory.getLogger(Passportserviceimpl.class);

	@Autowired
	PropertiesConfig propertiesconfig;

	@Autowired
	Passportfetchrequestentityrepository passportfetchrequestentityrepository;

	@Autowired
	Passportverificationrequestentityrepository passportverificationrequestentityrepository;

	@Override
	public String getVerify(PassportVerificationDto passportVerificationDto) {
	    logger.info("Starting getVerify method.");
	    Passportverficationlog apiLog = new Passportverficationlog();
	    String response1 = null;
	    HttpURLConnection connection = null;

	    try {
	        String APIURL = propertiesconfig.getPassportverifyApiURl();
	        logger.info("API URL: {}", APIURL);

	        // Convert DTO to JSON
	        Gson gson = new Gson();
	        String requestBodyJson = gson.toJson(passportVerificationDto);

	        // Save the request details to the database
	        Passportverificationrequestentity passportverificationrequestentity = new Passportverificationrequestentity();
	        passportverificationrequestentity.setDob(passportVerificationDto.getDob());
	        passportverificationrequestentity.setFileNumber(passportVerificationDto.getFileNumber());
	        passportverificationrequestentity.setName(passportVerificationDto.getName());
	        passportverificationrequestentity.setStatusmsg("successfuly sent");
	        passportverificationrequestentity.setTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
	        passportverificationrequestentityrepository.save(passportverificationrequestentity);

	        // Open the HTTP connection
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

	        // If the request was successful (HTTP 200)
	        StringBuilder response = new StringBuilder();
	        if (responseCode == HttpStatus.OK.value()) {
	            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
	              
	                String line;
	                while ((line = in.readLine()) != null) {
	                    response.append(line.trim());
	                }
	                
	            }
	            response1 = response.toString();
                logger.info("Received Response: {}", response1);
	            apiLog.setResponseBody(response1);
	            apiLog.setStatusCode(HttpStatus.OK.value());
	            apiLog.setApiType("passport verify");
	        } else {
	            // Handle non-200 responses (error responses)
	            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
	                
	                String line;
	                while ((line = in.readLine()) != null) {
	                    response.append(line.trim());
	                }
	               
	            }
	            response1 = response.toString();
	            apiLog.setResponseBody(response1);
	            apiLog.setStatusCode(responseCode);
	            apiLog.setApiType("passport verify");
	            logger.error("Error ResponseBody: {}", response1);
	        }
	    } catch (IOException e) {
	        logger.error("Unexpected error occurred: {}", e.getMessage());
	        apiLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	        response1 = "Internal server error: " + e.getMessage();
	        apiLog.setApiType("passport verify");
	        apiLog.setResponseBody(response1);
	    } finally {
	        passportverificationlogrepository.save(apiLog);
	        logger.info("API log saved for getVerify.");
	        if (connection != null) {
	            connection.disconnect();
	        }
	    }
	    return response1;
	}


	@Override
	public String getDetails(PassportNumberDto pasportdto) {
	    logger.info("Starting getDetails method.");
	    Passportnumberedlog apiLogentity = new Passportnumberedlog();
	    String response1 = null;
	    HttpURLConnection connection = null;

	    try {
	        String APIURL = propertiesconfig.getPassportnumberbasedApiURl();
	        logger.info("API URL: {}", APIURL);

	        // Convert DTO to JSON
	        Gson gson = new Gson();
	        String requestBodyJson = gson.toJson(pasportdto);

	        // Save the request details to the database
	        Passportfetchrequestentity passportfetchrequestentity = new Passportfetchrequestentity();
	        passportfetchrequestentity.setFileNumber(pasportdto.getFileNumber());
	        passportfetchrequestentity.setDob(pasportdto.getDob());
	        passportfetchrequestentity.setStatusmsg("successfuly sent");
	        passportfetchrequestentity.setTimestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
	        passportfetchrequestentityrepository.save(passportfetchrequestentity);

	        // Open the HTTP connection
	        URL url = new URL(APIURL);
	        connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Content-Type", "application/json");
	        connection.setRequestProperty("Authorization", propertiesconfig.getToken());
	        connection.setRequestProperty("Accept", "application/json");
	        connection.setDoOutput(true);
	     //   connection.setRequestProperty("x-client-unique-id", propertiesConfig.getXclientuniqueid());
	        // Log request details
	        logger.info("RequestBody: {}", requestBodyJson);
	        apiLogentity.setUrl(APIURL);
	        apiLogentity.setRequestBody(requestBodyJson);

	        // Send the request body
	        try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
	            wr.writeBytes(requestBodyJson);
	            wr.flush();
	        }

	        // Get the response code
	        int responseCode = connection.getResponseCode();
	        logger.info("Response Code: {}", responseCode);

	        // Handle the response
	        StringBuilder response = new StringBuilder();
	        if (responseCode == HttpStatus.OK.value()) {
	            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
	              
	                String line;
	                while ((line = in.readLine()) != null) {
	                    response.append(line.trim());
	                }
	                
	            }
	            response1 = response.toString();
                logger.info("Received Response: {}", response1);
	            apiLogentity.setResponseBody(response1);
	            apiLogentity.setStatusCode(HttpStatus.OK.value());
	            apiLogentity.setApiType("passport numberbased");
	        } else {
	            // Handle non-200 responses
	            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
	              
	                String line;
	                while ((line = in.readLine()) != null) {
	                    response.append(line.trim());
	                }
	               
	            }
	            response1 = response.toString();
	            apiLogentity.setResponseBody(response1);
	            apiLogentity.setStatusCode(responseCode);
	            apiLogentity.setApiType("passport numberbased");
	            logger.error("Error ResponseBody: {}", response1);
	        }
	    } catch (IOException e) {
	        logger.error("Unexpected error occurred: {}", e.getMessage());
	        apiLogentity.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	        response1 = "Internal server error: " + e.getMessage();
	        apiLogentity.setApiType("passport numberbased");
	        apiLogentity.setResponseBody(response1);
	    } finally {
	    	passportnumberedlogrepository.save(apiLogentity);
		        logger.info("API log saved for getVerify.");
		        if (connection != null) {
		            connection.disconnect();
		        }
		    }
		    return response1;
	}
}
