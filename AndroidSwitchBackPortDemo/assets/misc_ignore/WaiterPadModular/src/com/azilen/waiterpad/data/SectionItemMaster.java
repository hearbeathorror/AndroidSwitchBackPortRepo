package com.azilen.waiterpad.data;

import com.google.gson.annotations.SerializedName;

public class SectionItemMaster {
	@SerializedName(value="ItemId")
	private String itemId;
	@SerializedName(value="SectionId")
	private String sectionId;
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
	 * @return the sectionId
	 */
	public String getSectionId() {
		return sectionId;
	}
	/**
	 * @param sectionId the sectionId to set
	 */
	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}
}
