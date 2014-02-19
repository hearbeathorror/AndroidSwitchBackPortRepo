package com.azilen.waiterpad.utils.search;

import com.azilen.waiterpad.data.Section;
import com.google.common.base.Predicate;

public class SearchForSection implements Predicate<Section>{
	private String mSectionId;
	
	public SearchForSection(String sectionId) {
		mSectionId = sectionId;
	}
	
	@Override
	public boolean apply(Section section) {
		return section.getSectionId().equals(mSectionId);
	}
}
