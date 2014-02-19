package com.azilen.waiterpad.utils.search;


import java.util.Locale;

import android.util.Log;

import com.azilen.waiterpad.data.Item;
import com.google.common.base.Predicate;

/**
 * Identifies the item objects that have a certain character
 * or name in them
 * @author dhara.shah
 *
 */
public class SearchItemInMenu implements Predicate<Item>{
	private String mItemName;
	private String TAG = this.getClass().getSimpleName();
	
	public SearchItemInMenu(String itemName) {
		mItemName = itemName;
	}
	
	@Override
	public boolean apply(Item item) {
		if(item.getFullName() == null) {
			return item.getItemDescription().toLowerCase(Locale.ENGLISH).contains(mItemName.toLowerCase()) || 
					item.getItemName().toLowerCase(Locale.ENGLISH).contains(mItemName.toLowerCase()) || 
					item.getParentCategoryName().toLowerCase().contains(mItemName.toLowerCase());
		}else {
			return item.getFullName().toLowerCase().contains(mItemName.toLowerCase()) || 
					item.getItemDescription().toLowerCase(Locale.ENGLISH).contains(mItemName.toLowerCase()) || 
					item.getItemName().toLowerCase(Locale.ENGLISH).contains(mItemName.toLowerCase()) || 
					item.getParentCategoryName().toLowerCase().contains(mItemName.toLowerCase());
		}
	}
}
