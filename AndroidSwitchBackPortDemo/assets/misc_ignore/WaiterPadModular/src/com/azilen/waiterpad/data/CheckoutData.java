package com.azilen.waiterpad.data;

import com.google.gson.annotations.SerializedName;

public class CheckoutData {
	@SerializedName(value="OrderData")
	private CheckOutOrderDetails checkOutOrderDetails;

	/**
	 * @return the checkOutOrderDetails
	 */
	public CheckOutOrderDetails getCheckOutOrderDetails() {
		return checkOutOrderDetails;
	}

	/**
	 * @param checkOutOrderDetails the checkOutOrderDetails to set
	 */
	public void setCheckOutOrderDetails(CheckOutOrderDetails checkOutOrderDetails) {
		this.checkOutOrderDetails = checkOutOrderDetails;
	}
	
}
