package com.azilen.waiterpad.utils.search;

import com.azilen.waiterpad.data.Guest;
import com.google.common.base.Predicate;

public class SearchForGuest implements Predicate<Guest>{
	String mGuestId;
	
	public SearchForGuest(String guestId) {
		mGuestId = guestId;
	}

	@Override
	public boolean apply(Guest guest) {
		if(guest.getGuestId() != null) {
			return guest.getGuestId().equals(mGuestId);
		}else {
			return false;
		}
	}
}
