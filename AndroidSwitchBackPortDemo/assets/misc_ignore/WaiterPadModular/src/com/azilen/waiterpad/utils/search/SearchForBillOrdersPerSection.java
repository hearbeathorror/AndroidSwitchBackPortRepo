package com.azilen.waiterpad.utils.search;

import com.azilen.waiterpad.data.Order;
import com.google.common.base.Predicate;

public class SearchForBillOrdersPerSection implements Predicate<Order>{
	private String mSectionId;
	
	public SearchForBillOrdersPerSection(String sectionId) {
		mSectionId = sectionId;
	}
	@Override
	public boolean apply(Order order) {
		if(order != null) {
			return order.getTable().getSectionId().equals(mSectionId);
		}else {
			return false;
		}
	}
}
