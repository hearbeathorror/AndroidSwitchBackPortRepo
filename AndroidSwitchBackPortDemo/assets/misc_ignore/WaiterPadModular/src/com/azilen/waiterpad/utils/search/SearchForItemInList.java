package com.azilen.waiterpad.utils.search;

import com.azilen.waiterpad.data.Item;
import com.google.common.base.Predicate;

/**
 * Returns the item that matches the item id
 * @author dhara.shah
 *
 */
public class SearchForItemInList implements Predicate<Item>{
	private String mItemId;
	
	public SearchForItemInList(String itemId) {
		mItemId = itemId;
	}
	
	@Override
	public boolean apply(Item item) {
		return item.getItemId().equals(mItemId);
	}
}
