package com.azilen.waiterpad.utils.search;

import com.azilen.waiterpad.data.OrderedItem;
import com.google.common.base.Predicate;

/**
 * Returns all those items that are editable
 * @author dhara.shah
 *
 */
public class GetAllNewlyAddedItems implements Predicate<OrderedItem>{
	@Override
	public boolean apply(OrderedItem orderedItem) {
		return orderedItem.isEditable() == true;
	}
}
