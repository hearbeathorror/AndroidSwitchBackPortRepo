package com.azilen.waiterpad.data;

import java.util.List;

/**
 * Stores all the data obtained first:
 * Sectionwise menu
 * sectionwise table list
 * orderlist
 * @author dhara.shah
 *
 */
public class AllData {
	private List<SectionMenu> lstSectionMenu;
	private SectionWiseTable sectionWiseTable;
	private OrderList orderList;
	
	/**
	 * @return the lstSectionMenu
	 */
	public List<SectionMenu> getLstSectionMenu() {
		return lstSectionMenu;
	}
	/**
	 * @param lstSectionMenu the lstSectionMenu to set
	 */
	public void setLstSectionMenu(List<SectionMenu> lstSectionMenu) {
		this.lstSectionMenu = lstSectionMenu;
	}
	/**
	 * @return the sectionWiseTable
	 */
	public SectionWiseTable getSectionWiseTable() {
		return sectionWiseTable;
	}
	/**
	 * @param sectionWiseTable the sectionWiseTable to set
	 */
	public void setSectionWiseTable(SectionWiseTable sectionWiseTable) {
		this.sectionWiseTable = sectionWiseTable;
	}
	/**
	 * @return the orderList
	 */
	public OrderList getOrderList() {
		return orderList;
	}
	/**
	 * @param orderList the orderList to set
	 */
	public void setOrderList(OrderList orderList) {
		this.orderList = orderList;
	}
}
