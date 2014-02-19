package com.azilen.waiterpad.utils.search;

import com.azilen.waiterpad.data.SectionMenu;
import com.google.common.base.Predicate;

public class SearchForSectionMenu implements Predicate<SectionMenu>{
	private String mSectionId;
	
	public SearchForSectionMenu(String sectionId) {
		mSectionId = sectionId;
	}
	
	@Override
	public boolean apply(SectionMenu sectionMenu) {
		return sectionMenu.getId().equals(mSectionId);
	}
}
