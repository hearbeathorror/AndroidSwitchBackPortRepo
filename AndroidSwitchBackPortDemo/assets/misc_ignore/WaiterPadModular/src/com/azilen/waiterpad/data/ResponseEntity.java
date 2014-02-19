package com.azilen.waiterpad.data;

import com.google.gson.annotations.SerializedName;

public class ResponseEntity {
	@SerializedName(value="ResponseCode")
	private int responseCode;
	@SerializedName(value="ErrorMessage")
	private String responseErrorMessage;
	/**
	 * @return the responseCode
	 */
	public int getResponseCode() {
		return responseCode;
	}
	/**
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	/**
	 * @return the responseErrorMessage
	 */
	public String getResponseErrorMessage() {
		return responseErrorMessage;
	}
	/**
	 * @param responseErrorMessage the responseErrorMessage to set
	 */
	public void setResponseErrorMessage(String responseErrorMessage) {
		this.responseErrorMessage = responseErrorMessage;
	}
}
