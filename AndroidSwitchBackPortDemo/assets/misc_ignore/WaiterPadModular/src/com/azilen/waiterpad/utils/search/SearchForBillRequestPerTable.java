package com.azilen.waiterpad.utils.search;

import com.azilen.waiterpad.data.Order;
import com.google.common.base.Predicate;

public class SearchForBillRequestPerTable implements Predicate<Order>{
	private String mTableId;
	
	public SearchForBillRequestPerTable(String tableId) {
		mTableId = tableId;
	}
	
	@Override
	public boolean apply(Order order) {
		if(order != null) {
			return order.getTable().getTableId().equals(mTableId);
		}else {
			return false;
		}
	}

}
