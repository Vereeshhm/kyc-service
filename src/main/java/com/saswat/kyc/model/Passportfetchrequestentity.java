package com.saswat.kyc.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="passport_fetch_request_details")
public class Passportfetchrequestentity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	
	private Long id;
	
	
	private String fileNumber;

	private String dob;
	
	private String statusmsg;
	
	private LocalDateTime timestamp;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStatusmsg() {
		return statusmsg;
	}

	public void setStatusmsg(String statusmsg) {
		this.statusmsg = statusmsg;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getFileNumber() {
		return fileNumber;
	}

	public void setFileNumber(String fileNumber) {
		this.fileNumber = fileNumber;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}
}
