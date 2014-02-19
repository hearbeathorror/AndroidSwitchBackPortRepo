package com.azilen.waiterpad.utils.search;

import com.azilen.waiterpad.data.Order;
import com.google.common.base.Predicate;

public class GetOrderWithOrderNumber implements Predicate<Order> {
	private int mOrderNumber;
	
	public GetOrderWithOrderNumber(int orderNumber) {
		mOrderNumber = orderNumber;
	}
	
	@Override
	public boolean apply(Order order) {
		return order.getOrderNumber() == mOrderNumber;
	}
}
