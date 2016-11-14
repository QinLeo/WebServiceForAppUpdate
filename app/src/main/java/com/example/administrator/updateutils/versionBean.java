package com.example.administrator.updateutils;


public class versionBean {
	private int versionId;
	private String versionNumber;
	private String versionDate;
	private String versionName;
	private String versionPath;

	public versionBean() {
	}

	public versionBean(int versionId, String versionNumber, String versionDate,
			String versionName, String versionPath) {
		this.versionId = versionId;
		this.versionNumber = versionNumber;
		this.versionDate = versionDate;
		this.versionName = versionName;
		this.versionPath = versionPath;
	}

	public int getVersionId() {
		return versionId;
	}

	public void setVersionId(int versionId) {
		this.versionId = versionId;
	}

	public String getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

	public String getVersionDate() {
		return versionDate;
	}

	public void setVersionDate(String versionDate) {
		this.versionDate = versionDate;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getVersionPath() {
		return versionPath;
	}

	public void setVersionPath(String versionPath) {
		this.versionPath = versionPath;
	}

}
