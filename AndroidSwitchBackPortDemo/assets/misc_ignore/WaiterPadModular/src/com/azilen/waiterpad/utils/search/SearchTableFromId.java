package com.azilen.waiterpad.utils.search;

import com.azilen.waiterpad.data.Order;
import com.google.common.base.Predicate;

/**
 * Class that implements Guava Predicate
 * Searches through the orders for an order
 * that has a table with the id specified and 
 * also of a particular section
 * @author dhara.shah
 *
 */
public class SearchTableFromId implements Predicate<Order>{
	private String mTableId;
	private String mSectionId;

	/**
	 * Constructor that takes the table id as a parameter
	 * @param tableId
	 */
	public SearchTableFromId(String tableId, String sectionId) {
		mTableId = tableId;
		mSectionId = sectionId;
	}
	
	@Override
	public boolean apply(Order order) {
		return (order.getTable().getTableId().equals(mTableId) && 
				order.getTable().getSectionId().equals(mSectionId));
	}

}
