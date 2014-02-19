package com.azilen.waiterpad.utils.search;

import com.google.common.base.Predicate;

/**
 * Created as on 7th December 2013, 10:56 am
 * Predicate class that searches for the orderId
 * from the list of order ids for checked out orders
 * @author dhara.shah
 *
 */
public class SearchForIdFromList implements Predicate<String>{
	private String mOrderId;
	
	public SearchForIdFromList(String orderId) {
		mOrderId = orderId;
	}
	
	@Override
	public boolean apply(String orderId) {
		return orderId.equals(mOrderId);
	}
}
