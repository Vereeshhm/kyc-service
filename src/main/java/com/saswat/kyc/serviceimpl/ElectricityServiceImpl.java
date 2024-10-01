package com.saswat.kyc.serviceimpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.saswat.kyc.dto.ElectricityRequest;
import com.saswat.kyc.model.ElectricityApiLog;
import com.saswat.kyc.model.ElectricityBillRequestEntity;
import com.saswat.kyc.repository.ElectricityApiLogRepository;
import com.saswat.kyc.repository.ElectricityBillRequestEntityRepository;
import com.saswat.kyc.service.ElectricityService;
import com.saswat.kyc.utils.PropertiesConfig;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class ElectricityServiceImpl implements ElectricityService {

    @Autowired
    ElectricityApiLogRepository apiLogRepository;

    @Autowired
    PropertiesConfig config;
    
    @Autowired
    ElectricityBillRequestEntityRepository electricityBillRequestEntityRepository;

    private static final Logger logger = LoggerFactory.getLogger(ElectricityServiceImpl.class);

    @Override
    public ResponseEntity<String> getElectricityBill(ElectricityRequest electricityRequest) {
        logger.info("Starting electricity bill retrieval for request: {}", electricityRequest);
        ElectricityApiLog apiLog = new ElectricityApiLog();
        String response1 = null;
        HttpURLConnection connection = null;

        try {
            
            String ApiUrl = config.getElectricitybillurl();
            String requestBodyJson = new Gson().toJson(electricityRequest);
            
            logger.info("Sending API request to URL: {}", ApiUrl);
            logger.info("Request Body: {}", requestBodyJson);

            apiLog.setUrl(ApiUrl);
            apiLog.setRequestBody(requestBodyJson);

           
            URL url = new URL(ApiUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization", config.getToken());
            connection.setDoOutput(true);
       //	connection.setRequestProperty("x-client-unique-id", config.getXclientuniqueid());
            
            ElectricityBillRequestEntity billRequestEntity=new ElectricityBillRequestEntity();
            billRequestEntity.setConsumerNo(electricityRequest.getConsumerNo());
            billRequestEntity.setCreatedDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
            billRequestEntity.setElectricityProvider(electricityRequest.getElectricityProvider());
           
            
            // Send the request
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.writeBytes(requestBodyJson);
                wr.flush();
            }

            // Get the response code
            int responseCode = connection.getResponseCode();
            logger.info("Response Code: {}", responseCode);

  
            StringBuilder responseContent = new StringBuilder();

            // If response is OK (200)
            if (responseCode == HttpStatus.OK.value()) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        responseContent.append(inputLine);
                    }
                }

                response1 = responseContent.toString();
                logger.info("Received response: {}", response1);

                // Save successful API log
                apiLog.setResponseBody(response1);
                apiLog.setStatusCode(responseCode);
                apiLog.setStatus("SUCCESS");
                
                billRequestEntity.setResponseMessage(response1);
                billRequestEntity.setStatusMsg("sent successfully");
                electricityBillRequestEntityRepository.save(billRequestEntity);

                return ResponseEntity.status(responseCode).body(response1);
            } else {
                // Handle error responses using the error stream
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        responseContent.append(inputLine);
                    }
                }

                // Capture the error response and log it
                response1 = responseContent.toString();
                logger.error("Error response received from API: {}", response1);

                // Save the error details in API log
                apiLog.setResponseBody(response1);
                apiLog.setStatusCode(responseCode);
                apiLog.setStatus("FAILURE");
                billRequestEntity.setResponseMessage(response1);
                billRequestEntity.setStatusMsg("Failed to sent");
                electricityBillRequestEntityRepository.save(billRequestEntity);
                return ResponseEntity.status(responseCode).body(response1);
            }

        } catch (IOException e) {
            logger.error("Unexpected error during electricity bill retrieval", e);
            response1 = "Internal server error: " + e.getMessage();

            // Log the error in the API log
            apiLog.setResponseBody(response1);
            apiLog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            apiLog.setStatus("ERROR");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response1);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            apiLogRepository.save(apiLog);
            logger.info("Electricity bill retrieval process completed, API log saved.");
        }
    }
    
}
