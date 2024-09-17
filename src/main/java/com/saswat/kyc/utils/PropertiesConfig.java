package com.saswat.kyc.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("file:config/application.properties")
public class PropertiesConfig {

	@Value("${Pan.ApiURl}")
	private String PanApiURL;

	@Value("${IndividualPan.Url}")
	private String IndividualPanUrl;

	@Value("${Panextractionfileurl}")
	private String Panextractionfileurl;

	@Value("${Panextractionurl}")
	private String Panextractionurl;

	@Value("${Token}")
	private String Token;

	@Value("${Dlverification.URL}")
	private String DlverificationURL;

	@Value("${Dlnumberbased.URL}")
	private String DlnumberbasedURL;

	@Value("${passportverify.ApiURl}")
	private String passportverifyApiURl;

	@Value("${passportnumberbased.ApiURl}")
	private String passportnumberbasedApiURl;

	@Value("${voterdetailedsearch.ApiURl}")
	private String voterdetailedsearchApiURl;

	@Value("${votersearch.ApiURl}")
	private String votersearchApiURl;

	@Value("${voterverification.ApiUrl}")
	private String voterverificationApiUrl;

	@Value("${Aaadhar_Url}")
	private String Aaadhar_Url;
	
	
	@Value("${Experianbureau.url}")
	private String Experianbureauurl;

//	@Value("${x-client-unique-id}")
//	private String xclientuniqueid;
//
//	public String getXclientuniqueid() {
//		return xclientuniqueid;
//	}
//
//	public void setXclientuniqueid(String xclientuniqueid) {
//		this.xclientuniqueid = xclientuniqueid;
//	}
	
	public String getExperianbureauurl() {
		return Experianbureauurl;
	}

	public void setExperianbureauurl(String experianbureauurl) {
		Experianbureauurl = experianbureauurl;
	}

	public String getAaadhar_Url() {
		return Aaadhar_Url;
	}

	public void setAaadhar_Url(String aaadhar_Url) {
		Aaadhar_Url = aaadhar_Url;
	}



	public String getVoterverificationApiUrl() {
		return voterverificationApiUrl;
	}

	public void setVoterverificationApiUrl(String voterverificationApiUrl) {
		this.voterverificationApiUrl = voterverificationApiUrl;
	}

	public String getVoterdetailedsearchApiURl() {
		return voterdetailedsearchApiURl;
	}

	public void setVoterdetailedsearchApiURl(String voterdetailedsearchApiURl) {
		this.voterdetailedsearchApiURl = voterdetailedsearchApiURl;
	}

	public String getVotersearchApiURl() {
		return votersearchApiURl;
	}

	public void setVotersearchApiURl(String votersearchApiURl) {
		this.votersearchApiURl = votersearchApiURl;
	}

	public String getPassportverifyApiURl() {
		return passportverifyApiURl;
	}

	public void setPassportverifyApiURl(String passportverifyApiURl) {
		this.passportverifyApiURl = passportverifyApiURl;
	}

	public String getPassportnumberbasedApiURl() {
		return passportnumberbasedApiURl;
	}

	public void setPassportnumberbasedApiURl(String passportnumberbasedApiURl) {
		this.passportnumberbasedApiURl = passportnumberbasedApiURl;
	}

	public String getDlverificationURL() {
		return DlverificationURL;
	}

	public void setDlverificationURL(String dlverificationURL) {
		DlverificationURL = dlverificationURL;
	}

	public String getDlnumberbasedURL() {
		return DlnumberbasedURL;
	}

	public void setDlnumberbasedURL(String dlnumberbasedURL) {
		DlnumberbasedURL = dlnumberbasedURL;
	}

	public String getToken() {
		return Token;
	}

	public void setToken(String token) {
		Token = token;
	}

	public String getPanextractionfileurl() {
		return Panextractionfileurl;
	}

	public void setPanextractionfileurl(String panextractionfileurl) {
		Panextractionfileurl = panextractionfileurl;
	}

	public String getPanextractionurl() {
		return Panextractionurl;
	}

	public void setPanextractionurl(String panextractionurl) {
		Panextractionurl = panextractionurl;
	}

	public String getIndividualPanUrl() {
		return IndividualPanUrl;
	}

	public void setIndividualPanUrl(String individualPanUrl) {
		IndividualPanUrl = individualPanUrl;
	}

	public String getPanApiURL() {
		return PanApiURL;
	}

	public void setPanApiURL(String panApiURL) {
		PanApiURL = panApiURL;
	}

}
