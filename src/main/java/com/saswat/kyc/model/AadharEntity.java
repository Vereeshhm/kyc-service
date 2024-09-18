package com.saswat.kyc.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "aadhar_api_logs")
public class AadharEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "request_packet")
	private String requestPacket;

	@Column(name = "response_packet")
	private String responsePacket;

	@Column(name = "api_name")
	private String apiName;

	@Column(name = "time_stamp")
	private Date timeStamp;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRequestPacket() {
		return requestPacket;
	}

	public void setRequestPacket(String requestPacket) {
		this.requestPacket = requestPacket;
	}

	public String getResponsePacket() {
		return responsePacket;
	}

	public void setResponsePacket(String responsePacket) {
		this.responsePacket = responsePacket;
	}

	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp2) {
		this.timeStamp = timeStamp2;
	}

}
