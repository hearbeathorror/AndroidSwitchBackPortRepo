package com.azilen.waiterpad.utils.search;

import com.azilen.waiterpad.data.Order;
import com.google.common.base.Predicate;

/**
 * Predicate search that returns an order under a certain section
 * @author dhara.shah
 *
 */
public class SearchForOrdersSectionWise implements Predicate<Order>{
	private String mSectionId;
	public SearchForOrdersSectionWise(String sectionId) {
		mSectionId = sectionId;
	}
	
	@Override
	public boolean apply(Order order) {
		return order.getTable().getSectionId().equals(mSectionId);
	}
}
