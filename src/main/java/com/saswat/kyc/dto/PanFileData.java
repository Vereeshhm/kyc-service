package com.saswat.kyc.dto;

import java.util.List;

public class PanFileData {

	private List<String> files;
	private String type;
	private boolean getRelativeData;

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isGetRelativeData() {
		return getRelativeData;
	}

	public void setGetRelativeData(boolean getRelativeData) {
		this.getRelativeData = getRelativeData;
	}
}
