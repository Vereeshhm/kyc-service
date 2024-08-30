package com.saswat.kyc.serviceimpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.saswat.kyc.dto.IndividualPanRequest;
import com.saswat.kyc.model.IndividualPanApiLog;
import com.saswat.kyc.repository.IndividualPanApiLogRepository;
import com.saswat.kyc.service.Individualpanservice;
import com.saswat.kyc.utils.PropertiesConfig;

@Service
public class Individualpanserviceimpl implements Individualpanservice {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	IndividualPanApiLogRepository apiLogRepository;

	@Autowired
	PropertiesConfig propertiesConfig;

	private static final Logger logger = LoggerFactory.getLogger(Individualpanserviceimpl.class);

	@Override
	public String getPanVerification(IndividualPanRequest panRequest) {

		String response1 = null;
		IndividualPanApiLog apilog = new IndividualPanApiLog();

		try {

			Gson gson = new Gson();

			String requestBodyJson = gson.toJson(panRequest);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", propertiesConfig.getIndividualpantoken());

			HttpEntity<String> entity = new HttpEntity<>(requestBodyJson, headers);

			apilog.setUrl(propertiesConfig.getIndividualPanUrl());
			apilog.setRequestBody(requestBodyJson);

			logger.info("RequestBody" + requestBodyJson);
			response1 = restTemplate.postForObject(propertiesConfig.getIndividualPanUrl(), entity, String.class);
			apilog.setResponseBody(response1);
			apilog.setStatus("SUCCESS");
			apilog.setStatusCode(HttpStatus.OK.value());
			logger.info("ResponseBody" + response1);
			return response1;
		} catch (HttpClientErrorException.TooManyRequests e) {

			apilog.setStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
			apilog.setStatus("FAILURE");
			response1 = e.getResponseBodyAsString();
			logger.error("ResponseBody" + response1);
			apilog.setResponseBodyAsJson("API rate limit exceeded");
		} catch (HttpClientErrorException.Unauthorized e) {

			apilog.setStatusCode(HttpStatus.UNAUTHORIZED.value());
			apilog.setStatus("FAILURE");
			response1 = e.getResponseBodyAsString();

			logger.error("ResponseBody" + response1);
			apilog.setResponseBodyAsJson("No API key found in request");

		} catch (HttpClientErrorException e) {
			apilog.setStatusCode(e.getStatusCode().value());
			apilog.setStatus("FAILURE");
			response1 = e.getResponseBodyAsString();
			logger.error("ResponseBody" + response1);
			apilog.setResponseBody(response1);

		}

		catch (Exception e) {
			apilog.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			apilog.setStatus("ERROR");
			response1 = e.getMessage();
			String errorMessage = "Error inserting into pan_verification_logs: " + e.getMessage();
			System.err.println(errorMessage);
			logger.error("ResponseBody" + response1);
			apilog.setResponseBody(response1);
		}

		finally {
			apiLogRepository.save(apilog);
		}
		return response1;
	}

}
