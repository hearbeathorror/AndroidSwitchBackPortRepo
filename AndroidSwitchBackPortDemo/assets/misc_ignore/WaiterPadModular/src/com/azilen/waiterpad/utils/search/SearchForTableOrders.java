package com.azilen.waiterpad.utils.search;

import com.azilen.waiterpad.data.Order;
import com.google.common.base.Predicate;

public class SearchForTableOrders implements Predicate<Order>{
	private String mTableId;
	
	public SearchForTableOrders(String tableId) {
		mTableId = tableId;
	}
	@Override
	public boolean apply(Order order) {
		if(order != null) {
			if(order.getTable() != null) {
				if(order.getTable().getTableId().equals(mTableId)) {
					return true;
				}else {
					return false;
				}
			}else {
				return false;
			}
		}else {
			return false;
		}
	}
}
