package com.azilen.waiterpad.data;

import java.util.List;

public class ItemAndGuestRelatedTemp {
	private int childPosition;
	private int groupPosition;
	private Guest guest;
	private List<OrderedItem> orderedItems;
	private List<Guest> guests;
	private OrderedItem orderedItem;
	private ItemMaster item;
	
	/**
	 * @return the childPosition
	 */
	public int getChildPosition() {
		return childPosition;
	}
	/**
	 * @param childPosition the childPosition to set
	 */
	public void setChildPosition(int childPosition) {
		this.childPosition = childPosition;
	}
	/**
	 * @return the groupPosition
	 */
	public int getGroupPosition() {
		return groupPosition;
	}
	/**
	 * @param groupPosition the groupPosition to set
	 */
	public void setGroupPosition(int groupPosition) {
		this.groupPosition = groupPosition;
	}
	/**
	 * @return the guest
	 */
	public Guest getGuest() {
		return guest;
	}
	/**
	 * @param guest the guest to set
	 */
	public void setGuest(Guest guest) {
		this.guest = guest;
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
	 * @return the guests
	 */
	public List<Guest> getGuests() {
		return guests;
	}
	/**
	 * @param guests the guests to set
	 */
	public void setGuests(List<Guest> guests) {
		this.guests = guests;
	}
	/**
	 * @return the orderedItem
	 */
	public OrderedItem getOrderedItem() {
		return orderedItem;
	}
	/**
	 * @param orderedItem the orderedItem to set
	 */
	public void setOrderedItem(OrderedItem orderedItem) {
		this.orderedItem = orderedItem;
	}
	/**
	 * @return the item
	 */
	public ItemMaster getItem() {
		return item;
	}
	/**
	 * @param item the item to set
	 */
	public void setItem(ItemMaster item) {
		this.item = item;
	}
}
