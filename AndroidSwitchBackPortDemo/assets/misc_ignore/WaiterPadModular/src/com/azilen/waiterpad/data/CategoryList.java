package com.azilen.waiterpad.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Stores the list of categories under the menu
 * @author dhara.shah
 *
 */
public class CategoryList {
	@SerializedName(value="Menu")
	private List<Category> lstCategory;

	/**
	 * @return the lstCategory
	 */
	public List<Category> getLstCategory() {
		return lstCategory;
	}

	/**
	 * @param lstCategory the lstCategory to set
	 */
	public void setLstCategory(List<Category> lstCategory) {
		this.lstCategory = lstCategory;
	}
}
