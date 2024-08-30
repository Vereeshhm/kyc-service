package com.saswat.kyc.serviceimpl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.saswat.kyc.model.AadharEntity;
import com.saswat.kyc.repository.AadharRepository;
import com.saswat.kyc.service.AadharApiLogs;



@Service
public class AadharApiLogsImpl implements AadharApiLogs {

	@Autowired
	AadharRepository aadharRepository;

	@Override
	public void saveAAdharApiLogs(String requestPacket, String responsePacket, String apiName, long timeStamp) {

		try {
			Date date = new Date();
			AadharEntity aadharEntity = new AadharEntity();

			aadharEntity.setRequestPacket(requestPacket);
			aadharEntity.setResponsePacket(responsePacket);
			aadharEntity.setApiName(apiName);
			aadharEntity.setTimeStamp(date);
			aadharRepository.save(aadharEntity);

		} catch (Exception e) {

		}

	}

}
