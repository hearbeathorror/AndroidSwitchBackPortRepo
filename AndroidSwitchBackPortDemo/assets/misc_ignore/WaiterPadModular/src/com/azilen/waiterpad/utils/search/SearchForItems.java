package com.azilen.waiterpad.utils.search;

import com.azilen.waiterpad.data.Item;
import com.google.common.base.Predicate;

/**
 * Gets all those items that would be part 
 * of a certain category or subcategory
 * @author dharashah
 *
 */
public class SearchForItems implements Predicate<Item>{
	private String mToSearch;
	
	public SearchForItems(String toSearch) {
		mToSearch = toSearch;
	}
	
	@Override
	public boolean apply(Item item) {
		return item.getParentCategoryIds().trim().contains(mToSearch.trim());
	}

}
