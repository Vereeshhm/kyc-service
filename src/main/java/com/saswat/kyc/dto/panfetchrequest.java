package com.saswat.kyc.dto;

public class panfetchrequest {

	private String number;
	private String returnIndividualTaxComplianceInfo;

	// Getters and setters
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getReturnIndividualTaxComplianceInfo() {
		return returnIndividualTaxComplianceInfo;
	}

	public void setReturnIndividualTaxComplianceInfo(String returnIndividualTaxComplianceInfo) {
		this.returnIndividualTaxComplianceInfo = returnIndividualTaxComplianceInfo;
	}

	@Override
	public String toString() {
		return "pandto [number=" + number + ", returnIndividualTaxComplianceInfo=" + returnIndividualTaxComplianceInfo
				+ "]";
	}

}
