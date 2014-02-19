package com.azilen.waiterpad.data;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Guest implements Serializable{
	@SerializedName(value="Id")
	private String guestId;
	@SerializedName(value="Name")
	private String guestName;
	@SerializedName(value="Items")
	private List<OrderedItem> orderedItems;
	@SerializedName(value="IsDeleted")
	private boolean isDeleted;
	
	private String orderId;
	
	/**
	 * @return the guestId
	 */
	public String getGuestId() {
		return guestId;
	}
	/**
	 * @param guestId the guestId to set
	 */
	public void setGuestId(String guestId) {
		this.guestId = guestId;
	}
	/**
	 * @return the guestName
	 */
	public String getGuestName() {
		return guestName;
	}
	/**
	 * @param guestName the guestName to set
	 */
	public void setGuestName(String guestName) {
		this.guestName = guestName;
	}
	/**
	 * @return the orderedItems
	 */
	public List<OrderedItem> getOrderedItems() {
		return orderedItems;
	}
	/**
	 * @param orderedItems the orderedItems to set
	 */
	public void setOrderedItems(List<OrderedItem> orderedItems) {
		this.orderedItems = orderedItems;
	}
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
	
	public String toString() {
		return guestName;
	}
	/**
	 * @return the isDeleted
	 */
	public boolean isDeleted() {
		return isDeleted;
	}
	/**
	 * @param isDeleted the isDeleted to set
	 */
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
}
