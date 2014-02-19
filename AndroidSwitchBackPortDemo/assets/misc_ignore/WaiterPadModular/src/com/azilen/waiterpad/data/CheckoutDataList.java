package com.azilen.waiterpad.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class CheckoutDataList {
	@SerializedName(value="OrderData")
	private List<CheckoutDataToSend> lstCheckoutData;

	/**
	 * @return the lstCheckoutData
	 */
	public List<CheckoutDataToSend> getLstCheckoutData() {
		return lstCheckoutData;
	}

	/**
	 * @param lstCheckoutData the lstCheckoutData to set
	 */
	public void setLstCheckoutData(List<CheckoutDataToSend> lstCheckoutData) {
		this.lstCheckoutData = lstCheckoutData;
	}
}
