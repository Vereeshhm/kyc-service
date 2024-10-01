package com.saswat.kyc.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "electricity_bill_requestdetails")
public class ElectricityBillRequestEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	private String electricityProvider;

	private String consumerNo;

	private String statusMsg;

	private LocalDateTime createdDate;

	@Column(columnDefinition = "TEXT")
	private String responseMessage;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStatusMsg() {
		return statusMsg;
	}

	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getElectricityProvider() {
		return electricityProvider;
	}

	public void setElectricityProvider(String electricityProvider) {
		this.electricityProvider = electricityProvider;
	}

	public String getConsumerNo() {
		return consumerNo;
	}

	public void setConsumerNo(String consumerNo) {
		this.consumerNo = consumerNo;
	}

	@Override
	public String toString() {
		return "ElectricityBillRequestEntity [id=" + id + ", electricityProvider=" + electricityProvider
				+ ", consumerNo=" + consumerNo + ", statusMsg=" + statusMsg + ", createdDate=" + createdDate
				+ ", responseMessage=" + responseMessage + "]";
	}

}
