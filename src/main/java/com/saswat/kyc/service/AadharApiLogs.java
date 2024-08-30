package com.saswat.kyc.service;

public interface AadharApiLogs {

	public void saveAAdharApiLogs(String requestPacket, String responsePacket, String apiName, long timeStamp);

}
