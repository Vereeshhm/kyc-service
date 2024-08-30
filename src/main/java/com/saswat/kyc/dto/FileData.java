package com.saswat.kyc.dto;

import org.springframework.web.multipart.MultipartFile;

public class FileData {

	private MultipartFile file;

	private String ttl;

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public String getTtl() {
		return ttl;
	}

	public void setTtl(String ttl) {
		this.ttl = ttl;
	}

}
