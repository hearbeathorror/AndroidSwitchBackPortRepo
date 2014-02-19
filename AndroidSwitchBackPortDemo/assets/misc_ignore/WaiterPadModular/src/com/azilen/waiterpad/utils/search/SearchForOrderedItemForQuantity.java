package com.azilen.waiterpad.utils.search;

import com.azilen.waiterpad.data.OrderedItem;
import com.google.common.base.Predicate;

/**
 * Predicate that searches for ordereditems
 * based on the ordered id and also the id
 * @author dhara.shah
 *
 */
public class SearchForOrderedItemForQuantity  implements Predicate<OrderedItem>{
	private String mOrderedItemId;
	private String mId;
	
	public SearchForOrderedItemForQuantity(String id, String orderedItemId) {
		mId = id;
		mOrderedItemId = orderedItemId;
	}
	
	@Override
	public boolean apply(OrderedItem orderedItem) {
		// returns true if both the id and the ordereditem id is true
		// ordered item id will never be the same 
		// for two items of the same name ordered
		
		// if the orderedItemId is blank or a white space
		// then return that item which has the same id
		// and whose ordereditem id is null
		if(mOrderedItemId.trim().length() > 0) {
			if(orderedItem.getOrderedItemId() == null) {
				return false;
			}else {
				return (orderedItem.getId().equals(mId) && 
						orderedItem.getOrderedItemId().equals(mOrderedItemId));
			}
		}else {
			return (orderedItem.getId().equals(mId) && 
					orderedItem.getOrderedItemId() == null);
		}
	}
}
