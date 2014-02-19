package com.azilen.waiterpad.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Holds all the category items returned in the menu
 * @author dhara.shah
 *
 */
public class Category {
	@SerializedName(value="SubCategory")
	private List<Category> subCategoryList; 
	@SerializedName(value="Items")
	private List<Item> itemList;
	@SerializedName(value="Id")
	private String categoryId;
	@SerializedName(value="Name")
	private String categoryName;
	@SerializedName(value="Modifiers")
	private List<Modifiers> modifierList;
	
	private String parentCategoryName;
	private String parentCategoryIds;
	
	/**
	 * @return the subCategoryList
	 */
	public List<Category> getSubCategoryList() {
		return subCategoryList;
	}
	/**
	 * @param subCategoryList the subCategoryList to set
	 */
	public void setSubCategoryList(List<Category> subCategoryList) {
		this.subCategoryList = subCategoryList;
	}
	/**
	 * @return the itemList
	 */
	public List<Item> getItemList() {
		return itemList;
	}
	/**
	 * @param itemList the itemList to set
	 */
	public void setItemList(List<Item> itemList) {
		this.itemList = itemList;
	}
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
	 * @return the modifierList
	 */
	public List<Modifiers> getModifierList() {
		return modifierList;
	}
	/**
	 * @param modifierList the modifierList to set
	 */
	public void setModifierList(List<Modifiers> modifierList) {
		this.modifierList = modifierList;
	}
	
	public String toString() {
		return categoryName;
	}
	/**
	 * @return the parentCategoryName
	 */
	public String getParentCategoryName() {
		return parentCategoryName;
	}
	/**
	 * @param parentCategoryName the parentCategoryName to set
	 */
	public void setParentCategoryName(String parentCategoryName) {
		this.parentCategoryName = parentCategoryName;
	}
	/**
	 * @return the parentCategoryIds
	 */
	public String getParentCategoryIds() {
		return parentCategoryIds;
	}
	/**
	 * @param parentCategoryIds the parentCategoryIds to set
	 */
	public void setParentCategoryIds(String parentCategoryIds) {
		this.parentCategoryIds = parentCategoryIds;
	}
}
