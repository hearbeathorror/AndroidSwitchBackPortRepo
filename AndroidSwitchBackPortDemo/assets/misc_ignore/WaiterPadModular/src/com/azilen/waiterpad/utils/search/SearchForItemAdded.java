package com.azilen.waiterpad.utils.search;

import com.azilen.waiterpad.data.OrderedItem;
import com.google.common.base.Predicate;

/**
 * Returns an item that has the id it receives
 * Used by {@AddItemActivity}
 * @author dhara.shah
 *
 */
public class SearchForItemAdded implements Predicate<OrderedItem>{
	private String mItemId;
	private String TAG = this.getClass().getSimpleName();
	
	/**
	 * Constructor
	 * Takes the item id as the parameter
	 * @param itemId
	 */
	public SearchForItemAdded(String itemId) {
		mItemId = itemId;
	}
	
	@Override
	public boolean apply(OrderedItem orderedItem) {
		return orderedItem.getId().equals(mItemId) && 
				orderedItem.getOrderedItemStatus() == 0;
	}
	
}
