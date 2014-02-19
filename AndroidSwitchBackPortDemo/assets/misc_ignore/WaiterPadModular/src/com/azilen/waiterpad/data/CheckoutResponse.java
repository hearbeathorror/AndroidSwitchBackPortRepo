package com.azilen.waiterpad.data;

import com.google.gson.annotations.SerializedName;

public class CheckoutResponse extends ResponseEntity{
	@SerializedName(value="OrderId")
	private String orderId;
	@SerializedName(value="OrderNumber")
	private int orderNumber;
	@SerializedName(value="ConfirmOrder")
	private String confirmOrder;
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
	/**
	 * @return the orderNumber
	 */
	public int getOrderNumber() {
		return orderNumber;
	}
	/**
	 * @param orderNumber the orderNumber to set
	 */
	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}
	/**
	 * @return the confirmOrder
	 */
	public String getConfirmOrder() {
		return confirmOrder;
	}
	/**
	 * @param confirmOrder the confirmOrder to set
	 */
	public void setConfirmOrder(String confirmOrder) {
		this.confirmOrder = confirmOrder;
	}
}
