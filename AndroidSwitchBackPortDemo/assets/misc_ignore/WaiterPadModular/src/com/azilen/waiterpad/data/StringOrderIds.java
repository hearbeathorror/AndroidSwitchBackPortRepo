package com.azilen.waiterpad.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class StringOrderIds {
	@SerializedName(value="OrderIdList")
	private List<String> orderIds;

	/**
	 * @return the orderIds
	 */
	public List<String> getOrderIds() {
		return orderIds;
	}

	/**
	 * @param orderIds the orderIds to set
	 */
	public void setOrderIds(List<String> orderIds) {
		this.orderIds = orderIds;
	}
}
