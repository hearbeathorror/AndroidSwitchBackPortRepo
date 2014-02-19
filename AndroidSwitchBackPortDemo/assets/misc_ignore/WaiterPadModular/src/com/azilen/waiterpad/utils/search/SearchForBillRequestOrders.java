package com.azilen.waiterpad.utils.search;

import com.azilen.waiterpad.data.Order;
import com.google.common.base.Predicate;

/**
 * Returns all those orders that have bill requests
 * with the status 2
 * @author dharashah
 *
 */
public class SearchForBillRequestOrders implements Predicate<Order>{

	/**
	 * Constructor
	 */
	public SearchForBillRequestOrders() {}
	
	@Override
	public boolean apply(Order order) {
		if(order != null && order.getOrderStatus() != null) {
			return order.getOrderStatus().ordinal() == 2;
		}else {
			return false;
		}
	}
}
