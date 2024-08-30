package com.saswat.kyc.serviceimpl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.saswat.kyc.dto.panfetchrequest;
import com.saswat.kyc.model.PANDataApiLog;
import com.saswat.kyc.repository.PANDataApiLogRepository;
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
			connection.setDoOutput(true);

			apiLog.setUrl(APIURL);
			apiLog.setRequestBody(requestBodyJson);
			logger.info("RequestBody: {}", requestBodyJson);
			apiLog.setAuthorizationToken(propertiesConfig.getToken());

			
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
		logger.error("Exception occurred: {}", e.getMessage(), e);
	}

}
