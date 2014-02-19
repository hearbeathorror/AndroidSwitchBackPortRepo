package com.azilen.waiterpad.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Stores the menu items under a section
 * @author dhara.shah
 *
 */
public class SectionMenu {
	@SerializedName(value="Id")
	private String id;
	@SerializedName(value="Name")
	private String name;
	@SerializedName(value="Menu")
	private List<Category> categoryList;
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the categoryList
	 */
	public List<Category> getCategoryList() {
		return categoryList;
	}
	/**
	 * @param categoryList the categoryList to set
	 */
	public void setCategoryList(List<Category> categoryList) {
		this.categoryList = categoryList;
	}
}
