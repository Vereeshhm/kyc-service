package com.saswat.kyc.dto;

public class ElectricityRequest {

	private String electricityProvider;

	private String consumerNo;

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
		return "ElectricityRequest [electricityProvider=" + electricityProvider + ", consumerNo=" + consumerNo + "]";
	}

}
