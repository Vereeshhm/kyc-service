package com.saswat.kyc.dto;

public class Phonekycsubmitotpdto {

	private String countryCode;

	private String mobileNumber;

	private String referenceId;

	private String otp;

	private Boolean extraFields;

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public Boolean getExtraFields() {
		return extraFields;
	}

	public void setExtraFields(Boolean extraFields) {
		this.extraFields = extraFields;
	}

	@Override
	public String toString() {
		return "Phonekycsubmitotpdto [countryCode=" + countryCode + ", mobileNumber=" + mobileNumber + ", referenceId="
				+ referenceId + ", otp=" + otp + ", extraFields=" + extraFields + "]";
	}

}
