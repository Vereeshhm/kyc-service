package com.saswat.kyc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.saswat.kyc.dto.AadharRequest;
import com.saswat.kyc.dto.DLnumberrequest;
import com.saswat.kyc.dto.Dlverificationrequest;
import com.saswat.kyc.dto.ElectricityRequest;
import com.saswat.kyc.dto.Experianbureaudto;
import com.saswat.kyc.dto.IndividualPanRequest;
import com.saswat.kyc.dto.PanFileData;
import com.saswat.kyc.dto.Panfetchdto;
import com.saswat.kyc.dto.PassportNumberDto;
import com.saswat.kyc.dto.PassportVerificationDto;
import com.saswat.kyc.dto.Phonekycgenerateotpdto;
import com.saswat.kyc.dto.Phonekycnonconsentdto;
import com.saswat.kyc.dto.Phonekycsubmitotpdto;
import com.saswat.kyc.dto.Voterdetaileddto;
import com.saswat.kyc.dto.Voterfetchdto;
import com.saswat.kyc.dto.Voterverificationdto;
import com.saswat.kyc.dto.panfetchrequest;
import com.saswat.kyc.service.AadharService;
import com.saswat.kyc.service.DLService;
import com.saswat.kyc.service.ElectricityService;
import com.saswat.kyc.service.Experianbureauservice;
import com.saswat.kyc.service.Individualpanservice;
import com.saswat.kyc.service.PANDataService;
import com.saswat.kyc.service.PanExtractionService;
import com.saswat.kyc.service.Passportservice;
import com.saswat.kyc.service.Phonekycotpservice;
import com.saswat.kyc.service.Voterservice;
import com.saswat.kyc.service.Voterverificationservice;

@RestController
@RequestMapping("/saswat")
public class kyc_controller {

	@Autowired
	PANDataService panservice;

	@Autowired
	Individualpanservice individualpanservice;

	@Autowired
	PanExtractionService extractionService;

	@Autowired
	DLService dlService;

	@Autowired
	Passportservice passportservice;

	@Autowired
	Voterservice voterservice;

	@Autowired
	Voterverificationservice voterverificationservice;

	@Autowired
	AadharService aadharService;

	@Autowired
	Experianbureauservice experianbureauservice;

	@Autowired
	Phonekycotpservice phonekycotpservice;

	@Autowired
	ElectricityService electricityService;

	@PostMapping(value="pan/fetchV2", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> PanDetails(@RequestBody panfetchrequest fetchrequest) {

		return panservice.getPanDetails(fetchrequest);
	}

	@PostMapping(value = "/panverification", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getPanVerification(@RequestBody IndividualPanRequest panRequest) {
		return individualpanservice.getPanVerification(panRequest);

	}

	@PostMapping(value = "file/upload",produces=MediaType.APPLICATION_JSON_VALUE)
	public String getFiledata(@RequestParam("file") MultipartFile file, @RequestParam("ttl") String ttl) {

		return extractionService.getFileData(file, ttl);
	}

	@PostMapping(value="pan/extraction",produces=MediaType.APPLICATION_JSON_VALUE)
	public String getPanExtractedData(@RequestBody PanFileData panfiledata) {
		return extractionService.getPanExtractedData(panfiledata);
	}

	@PostMapping(value="api/DL/Verification",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> Verification(@RequestBody Dlverificationrequest dlverificationrequest) {

		return dlService.getVerfication(dlverificationrequest);
	}

	@PostMapping(value="api/DL/numberbased",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> fetchdetails(@RequestBody DLnumberrequest dlnumberrequest) {

		return dlService.getFetchDetails(dlnumberrequest);
	}

	@PostMapping(value="api/passport/verify",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> Verify(@RequestBody PassportVerificationDto passportVerificationDto) {

		return passportservice.getVerify(passportVerificationDto);
	}

	@PostMapping(value="api/passport/fetch",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> details(@RequestBody PassportNumberDto pasportdto) {

		return passportservice.getDetails(pasportdto);
	}

	@PostMapping(value="api/voterid/detailedsearch",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> fetchAll(@RequestBody Voterdetaileddto voterdetaileddto) throws JsonProcessingException {

		return voterservice.getfetchAll(voterdetaileddto);
	}

	@PostMapping(value="api/voterid/fetch",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> fetchdetails(@RequestBody Voterfetchdto voterfetchdto) throws JsonProcessingException {

		return voterservice.getfetchdetails(voterfetchdto);
	}

	@PostMapping(value="api/voter-id/verification",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getVerify(@RequestBody Voterverificationdto voterverificationdto) {
		return voterverificationservice.getVerify(voterverificationdto);
	}

	@PostMapping(value = "/aadhar_verification", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getAatharVerification(@RequestBody AadharRequest aadharRequest) {
		return aadharService.fetchAatharVerification(aadharRequest);

	}

	@PostMapping(value="/experian-report",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getBureauReport(@RequestBody Experianbureaudto bureauDto) {
		return experianbureauservice.getBureauReport(bureauDto);
	}

	@PostMapping(value="/v3/phone/generateOtp",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> generateOtp(@RequestBody Phonekycgenerateotpdto phonekycgenerateotpdto)

	{
		return phonekycotpservice.generateOtp(phonekycgenerateotpdto);
	}

	@PostMapping(value="/v3/phone/submitOtp",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> submitOtp(@RequestBody Phonekycsubmitotpdto phonekycsubmitotpdto) {
		return phonekycotpservice.submitOtp(phonekycsubmitotpdto);
	}

	@PostMapping(value="/v3/phone/phonekycotpless",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> nonConsent(@RequestBody Phonekycnonconsentdto phonekycnonconsentdto) {
		return phonekycotpservice.nonConsent(phonekycnonconsentdto);
	}

	@PostMapping(value="/v3/pan/fetch",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> fetchByPanNumber(@RequestBody Panfetchdto panfetchdto) {
		return panservice.fetchByPanNumber(panfetchdto);
	}

	@PostMapping(value="/electricitybill/fetch",produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getElectricityBill(@RequestBody ElectricityRequest electricityRequest) {
		return electricityService.getElectricityBill(electricityRequest);
	}

}
