package com.azilen.waiterpad.utils.search;

import com.azilen.waiterpad.data.Order;
import com.google.common.base.Predicate;

/**
 * Returns the order with an order id
 * @author dhara.shah
 *
 */
public class SearchForOrder implements Predicate<Order>{
	private String mOrderId;
	
	public SearchForOrder(String orderId) {
		mOrderId = orderId;
	}
	
	@Override
	public boolean apply(Order order) {
		if(order!=null && mOrderId!=null)
			return order.getOrderId().equals(mOrderId);
		else
			return false;
	}

}
