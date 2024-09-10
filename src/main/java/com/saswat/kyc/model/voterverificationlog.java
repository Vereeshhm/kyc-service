package com.saswat.kyc.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.hibernate.annotations.Type;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "voter_verification_logss")
public class voterverificationlog {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "voter_verification_logss_seq")
	@SequenceGenerator(name = "voter_verification_logss_seq", sequenceName = "voter_verification_logss_seq", allocationSize = 1)
	private Long id;

	private String url;

	@Type(JsonBinaryType.class)
	@Column(columnDefinition = "jsonb")
	private String requestBody;
	@Column(columnDefinition = "TEXT")
	private String responseBody;

	private int statusCode;

	private String status;

	private LocalDateTime timestamp = LocalDateTime.now();

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public voterverificationlog() {
		this.timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
	}

	public void setResponseBodyAsJson(String message) {
		this.responseBody = "{\"message\": \"" + message.replace("\"", "\\\"") + "\"}";
	}
}
