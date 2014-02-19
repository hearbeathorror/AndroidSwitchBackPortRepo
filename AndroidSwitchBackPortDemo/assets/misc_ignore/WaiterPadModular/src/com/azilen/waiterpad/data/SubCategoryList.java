package com.azilen.waiterpad.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Stores a list of subcategories under one category
 * @author dhara.shah
 *
 */
public class SubCategoryList {
	@SerializedName(value="SubCategory")
	private List<Category> lstSubCategories;

	/**
	 * @return the lstSubCategories
	 */
	public List<Category> getLstSubCategories() {
		return lstSubCategories;
	}

	/**
	 * @param lstSubCategories the lstSubCategories to set
	 */
	public void setLstSubCategories(List<Category> lstSubCategories) {
		this.lstSubCategories = lstSubCategories;
	}
}
