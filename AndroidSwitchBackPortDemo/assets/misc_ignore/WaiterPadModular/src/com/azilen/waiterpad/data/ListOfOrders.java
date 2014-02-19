package com.azilen.waiterpad.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ListOfOrders {
	@SerializedName(value="OrderData")
	private List<Order> orders;

	/**
	 * @return the orders
	 */
	public List<Order> getOrders() {
		return orders;
	}

	/**
	 * @param orders the orders to set
	 */
	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}
}
