package com.azilen.waiterpad.utils.search;

import com.azilen.waiterpad.data.Guest;
import com.google.common.base.Predicate;

public class SearchForGuestWithName implements Predicate<Guest>{
	private String mGuestName;
	
	/**
	 * Constructor default
	 */
	public SearchForGuestWithName() {}
	
	/**
	 * Constructor with guest name parameter
	 * @param guestName
	 */
	public SearchForGuestWithName(String guestName) {
		mGuestName = guestName;
	}
	
	@Override
	public boolean apply(Guest guest) {
		return guest.getGuestName().equalsIgnoreCase(mGuestName);
	}
}
