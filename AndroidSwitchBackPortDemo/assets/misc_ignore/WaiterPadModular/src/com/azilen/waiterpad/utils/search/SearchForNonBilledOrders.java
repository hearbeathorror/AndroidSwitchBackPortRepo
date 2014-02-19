package com.azilen.waiterpad.utils.search;

import com.azilen.waiterpad.data.Order;
import com.google.common.base.Predicate;

public class SearchForNonBilledOrders implements Predicate<Order>{
	
	public SearchForNonBilledOrders() {
		
	}
	
	@Override
	public boolean apply(Order order) {
		return order.getOrderStatus() != null && order.getOrderStatus().ordinal()  != 2;
	}
}
