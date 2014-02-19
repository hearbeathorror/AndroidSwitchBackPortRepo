package com.azilen.waiterpad.data;

import com.google.gson.annotations.SerializedName;

public class User {
	@SerializedName(value="Id")
	private String userId;
	@SerializedName(value="Name")
	private String userName;
	@SerializedName(value="IsSessionOpen")
	private boolean isSessionOpen;
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the isSessionOpen
	 */
	public boolean isSessionOpen() {
		return isSessionOpen;
	}
	/**
	 * @param isSessionOpen the isSessionOpen to set
	 */
	public void setSessionOpen(boolean isSessionOpen) {
		this.isSessionOpen = isSessionOpen;
	}
}
