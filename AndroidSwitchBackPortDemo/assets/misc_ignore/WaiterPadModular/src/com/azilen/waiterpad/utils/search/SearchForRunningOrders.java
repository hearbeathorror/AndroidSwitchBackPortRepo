package com.azilen.waiterpad.utils.search;

import com.azilen.waiterpad.data.Order;
import com.google.common.base.Predicate;

/**
 * Search for running orders
 * @author dharashah
 *
 */
public class SearchForRunningOrders implements Predicate<Order>{

	@Override
	public boolean apply(Order order) {
		return (order.getOrderStatus() == null || 
				(order.getOrderStatus() != null && 
				order.getOrderStatus().ordinal() != 2));
	}

}
