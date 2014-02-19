package com.azilen.waiterpad.data;

import java.io.Serializable;
import java.util.List;

import android.os.Debug;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

public class OrderedItem implements Serializable{
	public enum OrderedItemStatus {
		ADDED(0), COOKINGSTARTED(1), COOKINGCOMPLETED(2), SERVED(3), PRINTEDCOOKINGNOTSTARTED(4);
		
		private int key;
		
		private OrderedItemStatus(int key) {
			this.key = key;
		}
		
		public static OrderedItemStatus getItemStatus(int key) {
			for(OrderedItemStatus item : values()) {
				if(item.key == key) {
					return item;
				}
			}
			return null;
		}
	}
	
	// item id (order to be placed)
	@SerializedName(value="Id")
	private String id;

	@SerializedName(value="OrderedItemId")
	private String orderedItemId;
	
	// item name
	@SerializedName(value="Name")
	
	private String name;
	@SerializedName(value="Quantity")
	private double quantity;
	
	// kitchen note
	@SerializedName(value="Comment")
	private String comment;
	@SerializedName(value="Status")
	private int orderedItemStatus;
	@SerializedName(value="AppliedModifiers")
	private List<ModifierMaster> modifiers;
	@SerializedName(value="Price")
	private double price;
	@SerializedName(value="Cost")
	private double priceWithModifiers;
	
	private boolean isEditable;
	
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
	 * @return the quantity
	 */
	public double getQuantity() {
		return quantity;
	}
	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}
	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	/**
	 * @return the orderedItemStatus
	 */
	public int getOrderedItemStatus() {
		return orderedItemStatus;
	}
	/**
	 * @param orderedItemStatus the orderedItemStatus to set
	 */
	public void setOrderedItemStatus(int orderedItemStatus) {
		this.orderedItemStatus = orderedItemStatus;
	}
	/**
	 * @return the modifiers
	 */
	public List<ModifierMaster> getModifiers() {
		return modifiers;
	}
	/**
	 * @param modifiers the modifiers to set
	 */
	public void setModifiers(List<ModifierMaster> modifiers) {
		this.modifiers = modifiers;
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
	 * @return the priceWithModifiers
	 */
	public double getPriceWithModifiers() {
		return priceWithModifiers;
	}
	/**
	 * @param priceWithModifiers the priceWithModifiers to set
	 */
	public void setPriceWithModifiers(double priceWithModifiers) {
		this.priceWithModifiers = priceWithModifiers;
	}
	/**
	 * @return the isEditable
	 */
	public boolean isEditable() {
		return isEditable;
	}
	/**
	 * @param isEditable the isEditable to set
	 */
	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}
	public String getOrderedItemId() {
		return orderedItemId;
	}
	public void setOrderedItemId(String orderedItemId) {
		this.orderedItemId = orderedItemId;
	}
}
