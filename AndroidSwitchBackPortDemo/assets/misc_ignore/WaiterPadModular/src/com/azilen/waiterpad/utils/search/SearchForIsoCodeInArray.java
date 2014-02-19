package com.azilen.waiterpad.utils.search;

import com.google.common.base.Predicate;

public class SearchForIsoCodeInArray implements Predicate<String>{
	private String mStringToSearch;
	
	public SearchForIsoCodeInArray(String stringToSearch) {
		mStringToSearch = stringToSearch;
	}
	
	@Override
	public boolean apply(String isoCode) {
		return isoCode.trim().equalsIgnoreCase(mStringToSearch.trim());
	}

}
