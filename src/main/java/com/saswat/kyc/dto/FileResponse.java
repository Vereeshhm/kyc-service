package com.saswat.kyc.dto;

public class FileResponse {
	private FileInfo file;

	public FileInfo getFile() {
		return file;
	}

	public void setFile(FileInfo file) {
		this.file = file;
	}

	public static class FileInfo {
		private String id;
		private String filetype;
		private String size;
		private String directURL;
		private boolean Protected;
		
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getFiletype() {
			return filetype;
		}
		public void setFiletype(String filetype) {
			this.filetype = filetype;
		}
		public String getSize() {
			return size;
		}
		public void setSize(String size) {
			this.size = size;
		}
		public String getDirectURL() {
			return directURL;
		}
		public void setDirectURL(String directURL) {
			this.directURL = directURL;
		}
		public boolean isProtected() {
			return Protected;
		}
		public void setProtected(boolean protected1) {
			Protected = protected1;
		}
		
		

	}
}