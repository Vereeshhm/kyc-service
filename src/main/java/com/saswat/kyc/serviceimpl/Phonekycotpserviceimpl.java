package com.saswat.kyc.serviceimpl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.saswat.kyc.dto.Phonekycgenerateotpdto;
import com.saswat.kyc.dto.Phonekycnonconsentdto;
import com.saswat.kyc.dto.Phonekycsubmitotpdto;
import com.saswat.kyc.model.PhoneKycGeneratedotpentity;
import com.saswat.kyc.model.Phonekycgenerateotprequestentity;
import com.saswat.kyc.model.Phonekycnonconsentrequestentity;
import com.saswat.kyc.model.Phonekycotpapilog;
import com.saswat.kyc.model.Phonekycsubmitotprequestentity;
import com.saswat.kyc.repository.PhoneKycGeneratedotpentityrepository;
import com.saswat.kyc.repository.Phonekycgenerateotprequestentityrepository;
import com.saswat.kyc.repository.Phonekycnonconsentrequestentityrepository;
import com.saswat.kyc.repository.Phonekycotpapilogrepository;
import com.saswat.kyc.repository.Phonekycsubmitotprequestentityrepository;
import com.saswat.kyc.service.Phonekycotpservice;
import com.saswat.kyc.utils.PropertiesConfig;

@Service
public class Phonekycotpserviceimpl implements Phonekycotpservice {

	@Autowired
	PropertiesConfig propertiesConfig;

	@Autowired
	Phonekycgenerateotprequestentityrepository phonekycgenerateotprequestentityrepository;

	@Autowired
	Phonekycotpapilogrepository phonekycotpapilogrepository;

	@Autowired
	Phonekycsubmitotprequestentityrepository phonekycsubmitotprequestentityrepository;

	@Autowired
	Phonekycnonconsentrequestentityrepository phonekycnonconsentrequestentityrepository;

	@Autowired
	PhoneKycGeneratedotpentityrepository generatedotpentityrepository;

	private static final Logger logger = LoggerFactory.getLogger(Phonekycotpserviceimpl.class);

	public void logApi(String url, String requestBody, String responseBody, HttpStatus status, String statusmsg,
			String apiType) {
		Phonekycotpapilog apiLog = new Phonekycotpapilog();

		apiLog.setUrl(url);
		apiLog.setRequestBody(requestBody);
		apiLog.setResponseBody(responseBody);
		apiLog.setStatusCode(status.value()); // Status will now be correctly set
		apiLog.setCreated_date(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
		apiLog.setApiType(apiType);
		apiLog.setStatus(statusmsg);
		phonekycotpapilogrepository.save(apiLog);

		logger.info("API: {}, Status: {}, Request: {}, Response: {}", url, status.value(), requestBody, responseBody,
				statusmsg, apiType);
	}

	@Override
	public ResponseEntity<String> generateOtp(Phonekycgenerateotpdto phonekycgenerateotpdto) {

		String Apiurl = propertiesConfig.getPhoneKycgenerateOtp();
		logger.info("Request URL: " + Apiurl);

		Gson gson = new Gson();

		phonekycgenerateotpdto.setCountryCode(propertiesConfig.getCountryCode());
		String requestbodyJson = gson.toJson(phonekycgenerateotpdto);
		logger.info("Sending Requestbody " + requestbodyJson);

		String response1 = null;
		HttpURLConnection connection = null;

		try {

			Phonekycgenerateotprequestentity phonekycgenerateotprequestentity = new Phonekycgenerateotprequestentity();

			phonekycgenerateotprequestentity.setCountryCode(phonekycgenerateotpdto.getCountryCode());
			phonekycgenerateotprequestentity.setMobileNumber(phonekycgenerateotpdto.getMobileNumber());
			phonekycgenerateotprequestentity.setStatusmsg("sent successfully");
			phonekycgenerateotprequestentity.setCreatedDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

			phonekycgenerateotprequestentityrepository.save(phonekycgenerateotprequestentity);

			PhoneKycGeneratedotpentity generatedotpentity = new PhoneKycGeneratedotpentity();
			generatedotpentity.setCountryCode(propertiesConfig.getCountryCode());
			generatedotpentity.setMobileNumber(phonekycgenerateotpdto.getMobileNumber());
			generatedotpentityrepository.save(generatedotpentity);

			// Open the connection
			URL url = new URL(Apiurl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Authorization", propertiesConfig.getToken());
			connection.setDoOutput(true);

			try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
				wr.writeBytes(requestbodyJson);
				wr.flush();
			}

			int responseCode = connection.getResponseCode();
			logger.info("Response Code: {}", responseCode);

			// Check if the response was successful (HTTP 200)
			StringBuilder response = new StringBuilder();
			if (responseCode == HttpStatus.OK.value()) {
				// Read the response from the input stream
				try (BufferedReader in = new BufferedReader(
						new InputStreamReader(connection.getInputStream(), "utf-8"))) {

					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}

				}
				response1 = response.toString();

				JsonObject jsonResponse = gson.fromJson(response1, JsonObject.class);
				JsonObject result = jsonResponse.getAsJsonObject("result");
				String referenceId = result.get("referenceId").getAsString();

				generatedotpentity.setReferenceId(referenceId);
				generatedotpentityrepository.save(generatedotpentity);

				// Set the referenceId in the entity and save it
				phonekycgenerateotprequestentity.setReferenceId(referenceId);
				phonekycgenerateotprequestentity.setMessage(response1);
				phonekycgenerateotprequestentityrepository.save(phonekycgenerateotprequestentity);

				logApi(Apiurl, requestbodyJson, response1, HttpStatus.valueOf(responseCode), "success",
						"phonekyc generateotp");
				logger.info("Received generateotp response: {}", response1);
				return ResponseEntity.status(responseCode).body(response1);

			} else {
				// If an error response (non-200), read from the error stream
				try (BufferedReader in = new BufferedReader(
						new InputStreamReader(connection.getErrorStream(), "utf-8"))) {

					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}

				}

				response1 = response.toString();
				JsonObject errorResponse = gson.fromJson(response1, JsonObject.class);
				String errorMessage = "Unknown error"; // Default error message

				if (errorResponse.has("message")) {

					errorMessage = errorResponse.get("message").getAsString();
				} else if (errorResponse.has("error")) {

					JsonObject errorObject = errorResponse.getAsJsonObject("error");
					if (errorObject.has("message")) {
						errorMessage = errorObject.get("message").getAsString();
					} else {
						errorMessage = "Error object found, but no 'message' field.";
					}
				} else {

					errorMessage = "No 'message' or 'error' object found in the response.";
				}

				// Set the dynamically captured error message and update the status to "Failed"
				phonekycgenerateotprequestentity.setMessage(errorMessage);
				phonekycgenerateotprequestentity.setStatusmsg("Failed");

				// Save the entity with the updated error message
				phonekycgenerateotprequestentityrepository.save(phonekycgenerateotprequestentity);
				// Log the error and response
				logApi(Apiurl, requestbodyJson, response1, HttpStatus.valueOf(responseCode), "failure",
						"phonekyc generateotp");
				logger.error("Error ResponseBody: {}", response1);

				return ResponseEntity.status(responseCode).body(response1);
			}

		} catch (Exception e) {
			logger.error("Unexpected error during Phone kyc generateotp", e);
			response1 = "Internal server error: " + e.getMessage();

			Phonekycgenerateotprequestentity phonekycgenerateotprequestentity = new Phonekycgenerateotprequestentity();
			phonekycgenerateotprequestentity.setCountryCode(phonekycgenerateotpdto.getCountryCode());
			phonekycgenerateotprequestentity.setMobileNumber(phonekycgenerateotpdto.getMobileNumber());
			phonekycgenerateotprequestentity.setStatusmsg("Failed");
			phonekycgenerateotprequestentity.setMessage(response1);
			phonekycgenerateotprequestentity.setCreatedDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

			phonekycgenerateotprequestentityrepository.save(phonekycgenerateotprequestentity);
			logApi(Apiurl, requestbodyJson, response1, HttpStatus.INTERNAL_SERVER_ERROR, "Failed",
					"phonekyc generateotp");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response1);

		} finally {

			logger.debug("generated otp, API log saved.");
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	@Override
	public ResponseEntity<String> submitOtp(Phonekycsubmitotpdto phonekycsubmitotpdto) {

		String Apiurl = propertiesConfig.getPhoneKycsubmitOtp();
		logger.info("Request URL: " + Apiurl);

		Gson gson = new Gson();
		String requestbodyJson = null;
		String response1 = null;
		HttpURLConnection connection = null;

		try {
			// Fetch the most recent entries for the given mobile number
			List<PhoneKycGeneratedotpentity> entities = generatedotpentityrepository
					.findByMobileNumberOrderByIdDesc(phonekycsubmitotpdto.getMobileNumber());

			if (entities.isEmpty()) {
				logger.error("No referenceId found for mobileNumber: {}", phonekycsubmitotpdto.getMobileNumber());

				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid mobile number.");
			}

			// Get the most recent entry (first in the sorted list)
			PhoneKycGeneratedotpentity generateOtpEntity = entities.get(0);
			String referenceId = generateOtpEntity.getReferenceId();
			logger.info("Retrieved referenceId from the repository :{} ", referenceId);

			phonekycsubmitotpdto.setReferenceId(referenceId);
			phonekycsubmitotpdto.setCountryCode(propertiesConfig.getCountryCode());
			requestbodyJson = gson.toJson(phonekycsubmitotpdto);
			logger.info("Sending Requestbody " + requestbodyJson);

			Phonekycsubmitotprequestentity phonekycsubmitotprequestentity = new Phonekycsubmitotprequestentity();
			phonekycsubmitotprequestentity.setCountryCode(generateOtpEntity.getCountryCode());
			phonekycsubmitotprequestentity.setMobileNumber(phonekycsubmitotpdto.getMobileNumber());
			phonekycsubmitotprequestentity.setOtp(phonekycsubmitotpdto.getOtp());
			phonekycsubmitotprequestentity.setReferenceId(referenceId);
			phonekycsubmitotprequestentity.setExtraFields(phonekycsubmitotpdto.getExtraFields());
			phonekycsubmitotprequestentity.setCreatedDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
			phonekycsubmitotprequestentity.setStatusmsg("sent successfully");

			phonekycsubmitotprequestentityrepository.save(phonekycsubmitotprequestentity);

			// Open the connection
			URL url = new URL(Apiurl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Authorization", propertiesConfig.getToken());
			connection.setDoOutput(true);

			try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
				wr.writeBytes(requestbodyJson);
				wr.flush();
			}

			int responseCode = connection.getResponseCode();
			logger.info("Response Code: {}", responseCode);

			StringBuilder response = new StringBuilder();
			if (responseCode == HttpStatus.OK.value()) {
				try (BufferedReader in = new BufferedReader(
						new InputStreamReader(connection.getInputStream(), "utf-8"))) {

					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
				}

				response1 = response.toString();
				logger.info("Received submitotp response: {}", response1);

				phonekycsubmitotprequestentity.setMessage(response1);
				phonekycsubmitotprequestentityrepository.save(phonekycsubmitotprequestentity);

				logApi(Apiurl, requestbodyJson, response1, HttpStatus.valueOf(responseCode), "success",
						"phonekyc submitotp");
				return ResponseEntity.status(responseCode).body(response1);

			} else {
				// Handle non-200 responses
				try (BufferedReader in = new BufferedReader(
						new InputStreamReader(connection.getErrorStream(), "utf-8"))) {

					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
				}

				response1 = response.toString();
				logger.error("Error ResponseBody: {}", response1);
				JsonObject errorResponse = gson.fromJson(response1, JsonObject.class);
				String errorMessage = "Unknown error"; // Default error message

				if (errorResponse.has("message")) {

					errorMessage = errorResponse.get("message").getAsString();
				} else if (errorResponse.has("error")) {

					JsonObject errorObject = errorResponse.getAsJsonObject("error");
					if (errorObject.has("message")) {
						errorMessage = errorObject.get("message").getAsString();
					} else {
						errorMessage = "Error object found, but no 'message' field.";
					}
				} else {

					errorMessage = "No 'message' or 'error' object found in the response.";
				}

				// Set the dynamically captured error message and update the status to "Failed"
				phonekycsubmitotprequestentity.setMessage(errorMessage);
				phonekycsubmitotprequestentity.setStatusmsg("Failed");

				// Save the entity with the updated error message
				phonekycsubmitotprequestentityrepository.save(phonekycsubmitotprequestentity);

				logApi(Apiurl, requestbodyJson, response1, HttpStatus.valueOf(responseCode), "failure",
						"phonekyc submitotp");

				return ResponseEntity.status(responseCode).body(response1);
			}

		} catch (Exception e) {
			logger.error("Unexpected error during Phone kyc submitOtp", e);
			response1 = "Internal server error: " + e.getMessage();
			logApi(Apiurl, requestbodyJson, response1, HttpStatus.INTERNAL_SERVER_ERROR, "failure",
					"phonekyc submitotp");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response1);

		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	@Override
	public ResponseEntity<String> nonConsent(Phonekycnonconsentdto phonekycnonconsentdto) {

		String Apiurl = propertiesConfig.getPhoneKycnonconsent();

		logger.info("Request URL: " + Apiurl);

		Gson gson = new Gson();
		String requestbodyJson = gson.toJson(phonekycnonconsentdto);
		logger.info("Sending Requestbody " + requestbodyJson);
		String response1 = null;
		HttpURLConnection connection = null;

		try {

			Phonekycnonconsentrequestentity phonekycnonconsentrequestentity = new Phonekycnonconsentrequestentity();
			phonekycnonconsentrequestentity.setMobileNumber(phonekycnonconsentdto.getMobileNumber());
			phonekycnonconsentrequestentity.setStatusmsg("sent successfully");
			phonekycnonconsentrequestentity.setCreatedDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

			phonekycnonconsentrequestentityrepository.save(phonekycnonconsentrequestentity);

			// Open the connection
			URL url = new URL(Apiurl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Authorization", propertiesConfig.getToken());
			connection.setDoOutput(true);

			try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
				wr.writeBytes(requestbodyJson);
				wr.flush();
			}

			int responseCode = connection.getResponseCode();
			logger.info("Response Code: {}", responseCode);

			// Check if the response was successful (HTTP 200)
			StringBuilder response = new StringBuilder();
			if (responseCode == HttpStatus.OK.value()) {
				// Read the response from the input stream
				try (BufferedReader in = new BufferedReader(
						new InputStreamReader(connection.getInputStream(), "utf-8"))) {

					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}

				}
				response1 = response.toString();

				phonekycnonconsentrequestentity.setMessage(response1);
				phonekycnonconsentrequestentityrepository.save(phonekycnonconsentrequestentity);
				logger.info("Received nonconsent response: {}", response1);
				logApi(Apiurl, requestbodyJson, response1, HttpStatus.valueOf(responseCode), "success",
						"phonekyc nonconsent");
				return ResponseEntity.status(responseCode).body(response1);

			} else {
				// If an error response (non-200), read from the error stream
				try (BufferedReader in = new BufferedReader(
						new InputStreamReader(connection.getErrorStream(), "utf-8"))) {

					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}

				}

				response1 = response.toString();
				JsonObject errorResponse = gson.fromJson(response1, JsonObject.class);
				JsonObject errorObject = errorResponse.getAsJsonObject("error");
				String errorMessage = errorObject.has("message") ? errorObject.get("message").getAsString()
						: "Unknown error";

				phonekycnonconsentrequestentity.setMessage(errorMessage);
				phonekycnonconsentrequestentity.setStatusmsg("Failed");
				phonekycnonconsentrequestentityrepository.save(phonekycnonconsentrequestentity);

				logger.error("Error ResponseBody: {}", response1);
				logApi(Apiurl, requestbodyJson, response1, HttpStatus.valueOf(responseCode), "failure",
						"phonekyc nonconsent");
				return ResponseEntity.status(responseCode).body(response1);
			}

		} catch (Exception e) {
			logger.error("Unexpected error during Phone kyc nonconsent", e);
			response1 = "Internal server error: " + e.getMessage();
			logApi(Apiurl, requestbodyJson, response1, HttpStatus.INTERNAL_SERVER_ERROR, "failure",
					"phonekyc nonconsent");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response1);

		} finally {

			logger.debug("nonconsent otp, API log saved.");
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

}
