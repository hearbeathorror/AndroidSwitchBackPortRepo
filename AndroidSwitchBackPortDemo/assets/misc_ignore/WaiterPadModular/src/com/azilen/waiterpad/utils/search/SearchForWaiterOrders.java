package com.azilen.waiterpad.utils.search;

import com.azilen.waiterpad.data.Order;
import com.google.common.base.Predicate;

public class SearchForWaiterOrders implements Predicate<Order>{
	private String mWaiterId;
	
	public SearchForWaiterOrders(String waiterId) {
		mWaiterId = waiterId;
	}
	
	@Override
	public boolean apply(Order order) {
		return mWaiterId.equals(order.getWaiterId());
	}
	
}
