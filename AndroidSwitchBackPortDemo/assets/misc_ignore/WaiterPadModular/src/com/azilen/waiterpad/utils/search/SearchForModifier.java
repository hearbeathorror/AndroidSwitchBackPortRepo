package com.azilen.waiterpad.utils.search;

import com.azilen.waiterpad.data.ModifierMaster;
import com.google.common.base.Predicate;

public class SearchForModifier implements Predicate<ModifierMaster>{
	private String mId;
	public SearchForModifier(String id) {
		mId = id;
	}
	
	@Override
	public boolean apply(ModifierMaster modifier) {
		if(modifier != null) {
			return modifier.getId().equals(mId);
		}else {
			return false;
		}
	}
	
	
}
