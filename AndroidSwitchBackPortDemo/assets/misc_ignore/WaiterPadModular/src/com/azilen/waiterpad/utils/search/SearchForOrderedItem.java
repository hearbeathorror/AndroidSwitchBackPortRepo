package com.azilen.waiterpad.utils.search;

import com.azilen.waiterpad.data.OrderedItem;
import com.google.common.base.Predicate;

public class SearchForOrderedItem implements Predicate<OrderedItem>{
	private String mOrderedItemId;
	public SearchForOrderedItem(String orderedItemId) {
		mOrderedItemId = orderedItemId;
	}
	@Override
	public boolean apply(OrderedItem orderedItem) {
		return orderedItem.getId().equals(mOrderedItemId);
	}

}
