package com.azilen.waiterpad.data;

import com.google.gson.annotations.SerializedName;

public class CategoryMaster {
	@SerializedName(value="Id")
	private String categoryId;
	@SerializedName(value="Name")
	private String categoryName;
	@SerializedName(value="ParantId")
	private String categoryParentId;
	
	/**
	 * @return the categoryId
	 */
	public String getCategoryId() {
		return categoryId;
	}
	/**
	 * @param categoryId the categoryId to set
	 */
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	/**
	 * @return the categoryName
	 */
	public String getCategoryName() {
		return categoryName;
	}
	/**
	 * @param categoryName the categoryName to set
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	/**
	 * @return the categoryParentId
	 */
	public String getCategoryParentId() {
		return categoryParentId;
	}
	/**
	 * @param categoryParentId the categoryParentId to set
	 */
	public void setCategoryParentId(String categoryParentId) {
		this.categoryParentId = categoryParentId;
	}
}
