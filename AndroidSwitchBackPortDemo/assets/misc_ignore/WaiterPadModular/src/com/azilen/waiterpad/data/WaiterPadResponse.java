package com.azilen.waiterpad.data;

import com.google.gson.annotations.SerializedName;

/**
 * Stores the response identifying 
 * if the waiter is aunthenticated or not
 * @author dhara.shah
 *
 */
public class WaiterPadResponse extends CommonFields{
	@SerializedName(value="ErrorMessage")
	private String errorMessage;
	@SerializedName(value="IsError")
	private boolean isError;
	@SerializedName(value="User")
	private User user;
	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	/**
	 * @return the isError
	 */
	public boolean isError() {
		return isError;
	}
	/**
	 * @param isError the isError to set
	 */
	public void setError(boolean isError) {
		this.isError = isError;
	}
	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
}
