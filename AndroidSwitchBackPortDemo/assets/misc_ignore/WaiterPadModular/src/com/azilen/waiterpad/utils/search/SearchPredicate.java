package com.azilen.waiterpad.utils.search;

import com.azilen.waiterpad.data.SectionMenu;
import com.google.common.base.Predicate;

public class SearchPredicate implements Predicate<SectionMenu>{
	private String mId;
	
	public SearchPredicate(String id) {
		mId = id;
	}
	
	@Override
	public boolean apply(SectionMenu sectionMenu) {
		return mId.equalsIgnoreCase(sectionMenu.getId());
	}
}
