package com.saswat.kyc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import com.saswat.kyc.dto.FileData;
import com.saswat.kyc.dto.FileResponse;
import com.saswat.kyc.dto.IndividualPanRequest;
import com.saswat.kyc.dto.PanFileData;
import com.saswat.kyc.dto.PassportNumberDto;
import com.saswat.kyc.dto.PassportVerificationDto;
import com.saswat.kyc.dto.Voterdetaileddto;
import com.saswat.kyc.dto.Voterfetchdto;
import com.saswat.kyc.dto.Voterverificationdto;
import com.saswat.kyc.dto.panfetchrequest;
import com.saswat.kyc.service.AadharService;
import com.saswat.kyc.service.DLService;
import com.saswat.kyc.service.Individualpanservice;
import com.saswat.kyc.service.PANDataService;
import com.saswat.kyc.service.PanExtractionService;
import com.saswat.kyc.service.Passportservice;
import com.saswat.kyc.service.Voterservice;
import com.saswat.kyc.service.Voterverificationservice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

	@PostMapping("pan/fetch")
	public String PanDetails(@RequestBody panfetchrequest fetchrequest) {

		return panservice.getPanDetails(fetchrequest);
	}

	@PostMapping(value = "/panverification", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getPanVerification(@RequestBody IndividualPanRequest panRequest) {
		return individualpanservice.getPanVerification(panRequest);

	}

	@PostMapping(value = "file/upload")
	public String getFiledata(@RequestParam("file")   MultipartFile file, @RequestParam("ttl") String ttl, HttpServletRequest request, HttpServletResponse response1) {

		//FileResponse response = extractionService.getFileData(fileData,request, response1);
		return extractionService.getFileData(file,ttl,request, response1);
	}

	@PostMapping("pan/extraction")
	public String getPanExtractedData(@RequestBody PanFileData panfiledata) {
		return extractionService.getPanExtractedData(panfiledata);
	}

	@PostMapping("api/DL/Verification")
	public String Verification(@RequestBody Dlverificationrequest dlverificationrequest) {

		return dlService.getVerfication(dlverificationrequest);
	}

	@PostMapping("api/DL/numberbased")
	public String fetchdetails(@RequestBody DLnumberrequest dlnumberrequest) {

		return dlService.getFetchDetails(dlnumberrequest);
	}

	@PostMapping("api/passport/verify")
	public String Verify(@RequestBody PassportVerificationDto passportVerificationDto) {

		return passportservice.getVerify(passportVerificationDto);
	}

	@PostMapping("api/passport/fetch")
	public String details(@RequestBody PassportNumberDto pasportdto) {

		return passportservice.getDetails(pasportdto);
	}

	@PostMapping("api/voterid/detailedsearch")
	public String fetchAll(@RequestBody Voterdetaileddto voterdetaileddto) throws JsonProcessingException {

		return voterservice.getfetchAll(voterdetaileddto);
	}

	@PostMapping("api/voterid/fetch")
	public String fetchdetails(@RequestBody Voterfetchdto voterfetchdto) throws JsonProcessingException {

		return voterservice.getfetchdetails(voterfetchdto);
	}

	@PostMapping("api/voter-id/verification")
	public String getVerify(@RequestBody Voterverificationdto voterverificationdto) {
		return voterverificationservice.getVerify(voterverificationdto);
	}

	@PostMapping(value = "/aadhar_verification", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getAatharVerification(@RequestBody AadharRequest aadharRequest) {
		return aadharService.fetchAatharVerification(aadharRequest);

	}
}
