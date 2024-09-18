package com.saswat.kyc.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "voter_fetch_request_details")
public class voterfetchrequestentity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String epicNumber;

	private String name;

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

	public String getEpicNumber() {
		return epicNumber;
	}

	public void setEpicNumber(String epicNumber) {
		this.epicNumber = epicNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
