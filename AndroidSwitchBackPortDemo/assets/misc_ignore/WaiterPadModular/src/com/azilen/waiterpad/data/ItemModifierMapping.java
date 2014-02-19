package com.azilen.waiterpad.data;

import com.google.gson.annotations.SerializedName;

public class ItemModifierMapping {
	@SerializedName(value="ItemId")
	private String itemId;
	@SerializedName(value="ModifierId")
	private String modifierId;
	@SerializedName(value="GroupId")
	private String groupId;
	@SerializedName(value="MaxAmount")
	private double maxAmount;
	@SerializedName(value="MinAmount")
	private double minAmount;
	@SerializedName(value="DefaultAmount")
	private double defaultAmount;
	/**
	 * @return the itemId
	 */
	public String getItemId() {
		return itemId;
	}
	/**
	 * @param itemId the itemId to set
	 */
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	/**
	 * @return the modifierId
	 */
	public String getModifierId() {
		return modifierId;
	}
	/**
	 * @param modifierId the modifierId to set
	 */
	public void setModifierId(String modifierId) {
		this.modifierId = modifierId;
	}
	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}
	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	/**
	 * @return the maxAmount
	 */
	public double getMaxAmount() {
		return maxAmount;
	}
	/**
	 * @param maxAmount the maxAmount to set
	 */
	public void setMaxAmount(double maxAmount) {
		this.maxAmount = maxAmount;
	}
	/**
	 * @return the minAmount
	 */
	public double getMinAmount() {
		return minAmount;
	}
	/**
	 * @param minAmount the minAmount to set
	 */
	public void setMinAmount(double minAmount) {
		this.minAmount = minAmount;
	}
	/**
	 * @return the defaultAmount
	 */
	public double getDefaultAmount() {
		return defaultAmount;
	}
	/**
	 * @param defaultAmount the defaultAmount to set
	 */
	public void setDefaultAmount(double defaultAmount) {
		this.defaultAmount = defaultAmount;
	}
}
