package com.azilen.waiterpad.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class WholeMenu {
	@SerializedName(value="ItemMaster")
	private List<ItemMaster> lstItems;
	@SerializedName(value="CategoryMaster")
	private List<CategoryMaster> lstCategories;
	@SerializedName(value="ModifierMaster")
	private List<ItemModifiers> lstModifiers;
	@SerializedName(value="SectionItem")
	private List<SectionItemMaster> lstSectionItemMapping;
	@SerializedName(value="ItemModifier")
	private List<List<ItemModifierMapping>> itemModifierMapping;
	@SerializedName(value="GroupModifierMaster")
	private List<GroupModifierMaster> groupList;
	
	/**
	 * @return the lstItems
	 */
	public List<ItemMaster> getLstItems() {
		return lstItems;
	}
	/**
	 * @param lstItems the lstItems to set
	 */
	public void setLstItems(List<ItemMaster> lstItems) {
		this.lstItems = lstItems;
	}
	/**
	 * @return the lstCategories
	 */
	public List<CategoryMaster> getLstCategories() {
		return lstCategories;
	}
	/**
	 * @param lstCategories the lstCategories to set
	 */
	public void setLstCategories(List<CategoryMaster> lstCategories) {
		this.lstCategories = lstCategories;
	}
	/**
	 * @return the lstModifiers
	 */
	public List<ItemModifiers> getLstModifiers() {
		return lstModifiers;
	}
	/**
	 * @param lstModifiers the lstModifiers to set
	 */
	public void setLstModifiers(List<ItemModifiers> lstModifiers) {
		this.lstModifiers = lstModifiers;
	}
	/**
	 * @return the lstSectionItemMapping
	 */
	public List<SectionItemMaster> getLstSectionItemMapping() {
		return lstSectionItemMapping;
	}
	/**
	 * @param lstSectionItemMapping the lstSectionItemMapping to set
	 */
	public void setLstSectionItemMapping(
			List<SectionItemMaster> lstSectionItemMapping) {
		this.lstSectionItemMapping = lstSectionItemMapping;
	}
	/**
	 * @return the itemModifierMapping
	 */
	public List<List<ItemModifierMapping>> getItemModifierMapping() {
		return itemModifierMapping;
	}
	/**
	 * @param itemModifierMapping the itemModifierMapping to set
	 */
	public void setItemModifierMapping(
			List<List<ItemModifierMapping>> itemModifierMapping) {
		this.itemModifierMapping = itemModifierMapping;
	}
	/**
	 * @return the groupList
	 */
	public List<GroupModifierMaster> getGroupList() {
		return groupList;
	}
	/**
	 * @param groupList the groupList to set
	 */
	public void setGroupList(List<GroupModifierMaster> groupList) {
		this.groupList = groupList;
	}
}
