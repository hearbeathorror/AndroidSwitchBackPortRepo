package com.azilen.waiterpad.data;

import com.google.gson.annotations.SerializedName;

public class CheckoutDataToSend extends CommonFields{
	@SerializedName(value="Id")
	private String orderId;

	/**
	 * @return the orderId
	 */
	public String getOrderId() {
		return orderId;
	}

	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
}
