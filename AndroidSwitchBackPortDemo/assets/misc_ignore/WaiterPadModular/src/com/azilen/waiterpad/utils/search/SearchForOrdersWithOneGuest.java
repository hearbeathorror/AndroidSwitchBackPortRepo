package com.azilen.waiterpad.utils.search;

import com.azilen.waiterpad.data.Order;
import com.google.common.base.Predicate;

/**
 * Searches for those orders that have just one guest
 * and if the guest 1 name is passed it would
 * return the order with the guest name
 * @author dharashah
 *
 */
public class SearchForOrdersWithOneGuest implements Predicate<Order>{
	private String mGuestName;
	
	/**
	 * Default constructor
	 */
	public SearchForOrdersWithOneGuest(){}
	
	/**
	 * Constructor with guest name parameter
	 * @param guestName
	 */
	public SearchForOrdersWithOneGuest(String guestName) {
		mGuestName = guestName;
	}
	
	@Override
	public boolean apply(Order order) {
		if(order.getGuests() != null) {
			if(mGuestName != null) {
				if(order.getOrderStatus() != null) {
					return order.getGuests().size() == 1 && 
							order.getGuests().get(0).getGuestName().equalsIgnoreCase(mGuestName) && 
							order.getOrderStatus().ordinal() != 2; 
				}
			}else {
				if(order.getOrderStatus() != null) {
					return order.getGuests().size() == 1 && 
							order.getOrderStatus().ordinal() != 2; 
				}
			}
		}
		
		return false;
	}
}
