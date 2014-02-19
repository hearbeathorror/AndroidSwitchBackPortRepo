package com.azilen.waiterpad.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Stores details about the items under the categories
 * or subcategories
 * @author dhara.shah
 *
 */
public class Item {
	public enum ItemType {
		DISH(0),
        EXTERNALGOODS(1),
        GOODS(2),
        HALFFINISHEDGOODS(3),
        MODIFIER(4),
        PETROL(5),
        RATE(6),
        SERVICE(7);
        
        private int key;
		
		private ItemType(int key) {
			this.key = key;
		}
	
		public static ItemType getItemType(int key) {
			for(ItemType type : values()) {
				if(type.key == key) {
					return type;
				}
			}
			return null;
		}
	}
	
	@SerializedName(value="Id")
	private String itemId;
	@SerializedName(value="Code")
	private String itemCode;
	@SerializedName(value="Price")
	private double price;
	@SerializedName(value="CategoryId")
	private String categoryId;
	@SerializedName(value="IsActivated")
	private boolean isActivated;
	@SerializedName(value="IsRestricted")
	private boolean isRestricted;
	@SerializedName(value="Description")
	private String itemDescription;
	@SerializedName(value="FullName")
	private String fullName;
	@SerializedName(value="Name")
	private String itemName;
	@SerializedName(value="Modifiers")
	private List<Modifiers> modifiers;
	
	private String parentCategoryName;
	private String parentCategoryIds;
	
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
	 * @return the itemCode
	 */
	public String getItemCode() {
		return itemCode;
	}
	/**
	 * @param itemCode the itemCode to set
	 */
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
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
	 * @return the isActivated
	 */
	public boolean isActivated() {
		return isActivated;
	}
	/**
	 * @param isActivated the isActivated to set
	 */
	public void setActivated(boolean isActivated) {
		this.isActivated = isActivated;
	}
	/**
	 * @return the isRestricted
	 */
	public boolean isRestricted() {
		return isRestricted;
	}
	/**
	 * @param isRestricted the isRestricted to set
	 */
	public void setRestricted(boolean isRestricted) {
		this.isRestricted = isRestricted;
	}
	/**
	 * @return the itemDescription
	 */
	public String getItemDescription() {
		return itemDescription;
	}
	/**
	 * @param itemDescription the itemDescription to set
	 */
	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}
	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}
	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	/**
	 * @return the itemName
	 */
	public String getItemName() {
		return itemName;
	}
	/**
	 * @param itemName the itemName to set
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	/**
	 * @return the modifiers
	 */
	public List<Modifiers> getModifiers() {
		return modifiers;
	}
	/**
	 * @param modifiers the modifiers to set
	 */
	public void setModifiers(List<Modifiers> modifiers) {
		this.modifiers = modifiers;
	}
	
	public String toString(){
		return itemName;
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
