package com.saswat.kyc.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "pan_verification_logs")
public class IndividualPanApiLog {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pan_verification_logs_seq")
	@SequenceGenerator(name = "pan_verification_logs_seq", sequenceName = "pan_verification_logs_seq", allocationSize = 1)
	private Long id;

	private String url;

	@Column(columnDefinition = "TEXT")
	private String requestBody;


	@Column(columnDefinition = "TEXT")
	private String responseBody;

	private int statusCode;

	private String status;

	@Column(columnDefinition = "TIMESTAMP")
	private LocalDateTime timestamp = LocalDateTime.now();

	
	private String apiType;
	
	 
	public String getApiType() {
		return apiType;
	}

	public void setApiType(String apiType) {
		this.apiType = apiType;
	}


	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	

	public IndividualPanApiLog() {
		this.timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
	}

}
