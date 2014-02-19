package com.azilen.waiterpad.managers.notification;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.WaiterPadApplication;
import com.azilen.waiterpad.activities.NotificationListActivity;
import com.azilen.waiterpad.data.Guest;
import com.azilen.waiterpad.data.ModifierMaster;
import com.azilen.waiterpad.data.Order;
import com.azilen.waiterpad.data.OrderedItem;
import com.azilen.waiterpad.managers.language.LanguageManager;
import com.azilen.waiterpad.managers.settings.SettingsManager;
import com.azilen.waiterpad.receiver.NotificationReceiver;
import com.azilen.waiterpad.utils.Global;
import com.azilen.waiterpad.utils.Prefs;
import com.azilen.waiterpad.utils.search.SearchForGuest;
import com.azilen.waiterpad.utils.search.SearchForModifier;
import com.azilen.waiterpad.utils.search.SearchForOrder;
import com.azilen.waiterpad.utils.search.SearchForOrderedItem;
import com.azilen.waiterpad.utils.search.SearchForOrderedItemForQuantity;
import com.google.common.collect.Iterables;

public class NotificationManager {
	/* Singleton Pattern using Eager Initialization */
	private static final NotificationManager instance = new NotificationManager();
	private LruCache<String, Object> notificationCache;

	// max cache size 1 MB = 1024 KB
	private int maxSize = 2 * 1024 * 1024;
	private String mMessage;
	
	private static String TAG = NotificationManager.class.getSimpleName();

	/* Singleton Pattern */
	public static NotificationManager getInstance() {
		return instance;
	}

	public NotificationManager() {
		notificationCache = new LruCache<String, Object>(maxSize);
	}

	public LruCache<String, Object> getNotificationCache() {
		return notificationCache;
	}
	
	public void clearAll() {
		notificationCache.remove(Global.NOTIFICATION_ORDERS);
	}

	public void generateNotification(List<Order> ordersForNotification) {
		// create a notification message here
		android.app.NotificationManager notificationManager = 
				(android.app.NotificationManager) WaiterPadApplication
				.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
		
		boolean areNotificationsEnabled = 
				Prefs.getKey_boolean_with_default_true(WaiterPadApplication.getAppContext(), 
						Prefs.ARE_NOTIFICATIONS_ENABLED);

		// changes as on 29th November 2013
		// Client's requirement to have configurable vibration patterns
		// 0: Short
		// 1: Normal
		// 2: Long
		long[] vibration = new long[4];
		String vibrationPattern = SettingsManager.getInstance().getVibrationPattern();
		String[] vibrationPatternsArr = null;
		String vibrationPatternToTake = "";
		
		Log.i("dhara", "vib: " + vibrationPattern);
		
		if(vibrationPattern.equals("0")) {
			 vibrationPatternToTake = WaiterPadApplication.getAppContext().getString(R.string.short_vibration_pattern);
		}else if(vibrationPattern.equals("1")) {
			 vibrationPatternToTake = WaiterPadApplication.getAppContext().getString(R.string.normal_vibration_pattern);
		}else if(vibrationPattern.equals("2")) {
			 vibrationPatternToTake = WaiterPadApplication.getAppContext().getString(R.string.long_vibration_pattern);
		}
		
		vibrationPatternsArr = vibrationPatternToTake.split(",");
		
		for(int i=0;i<vibrationPatternsArr.length;i++) {
			vibration[i] = Long.parseLong(vibrationPatternsArr[i].trim());
 		}
		Global.logd(TAG + " " + vibration);
		// changes end here

		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
				WaiterPadApplication.getAppContext());

		// Intent resultNotificationIntent = new
		// Intent(WaiterPadDataService.this, OrderRelatedFragment.class);

		Intent resultNotificationIntent = new Intent(
				WaiterPadApplication.getAppContext(),
				NotificationReceiver.class);

		if (ordersForNotification != null && ordersForNotification.size() > 0) {
			int index = 0;
			for (Order order : ordersForNotification) {
				
				// changes as on 6th December 2013
				// if notifications from the app settings is set to off
				// no notifications will be given, otherwise they will
				
				if(areNotificationsEnabled) {
					mMessage = LanguageManager.getInstance().getOrderNumberLabel()
							+ "  " + order.getTable().getTableNumber() + " - "
							+ +order.getOrderNumber() + "  "
							+ LanguageManager.getInstance().getHasBeenUpdated();
					
					notificationBuilder
							.setAutoCancel(true)
							.setContentText(mMessage)
							.setContentTitle(mMessage)
							.setSmallIcon(R.drawable.app_icon)
							.setOngoing(false)
							.setTicker(mMessage)
							.setVibrate(vibration)
							.setOnlyAlertOnce(true)
							.setStyle(
									new NotificationCompat.BigTextStyle(
											notificationBuilder).bigText(mMessage))
							.setWhen(System.currentTimeMillis())
							.setLights(android.R.color.white, 5000, 0);
					
					Notification notification = notificationBuilder.build();

					PendingIntent pendingIntent = PendingIntent.getActivity(
							WaiterPadApplication.getAppContext(), 2,
							resultNotificationIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					notification.contentIntent = pendingIntent;
					notificationBuilder.setContentIntent(pendingIntent);

					notificationManager.cancel(order.getOrderNumber());

					notificationManager
							.notify(order.getOrderNumber(), notification);
				}
				
				// changes end here

				storeNotificationOrders(order);

				index++;
			}
		}
	}

	private void storeNotificationOrders(Order order) {
		List<Order> mNotificationOrderList = (List<Order>) notificationCache
				.get(Global.NOTIFICATION_ORDERS);
		if (mNotificationOrderList != null && mNotificationOrderList.size() > 0) {
			// check if order is present

			Order orderPresent = Iterables.find(mNotificationOrderList,
					new SearchForOrder(order.getOrderId()), null);
			if (orderPresent != null) {
				mNotificationOrderList.remove(orderPresent);
				mNotificationOrderList.add(orderPresent);
			} else {
				mNotificationOrderList.add(order);
			}
		} else {
			mNotificationOrderList = new ArrayList<Order>();
			mNotificationOrderList.add(order);
		}
		notificationCache.put(Global.NOTIFICATION_ORDERS,
				mNotificationOrderList);

		if (NotificationListActivity.notificationAdapter != null) {
			NotificationListActivity.notificationAdapter.notifyDataSetChanged();
		}
		
		notifyAboutNotifications();
	}
	
	@SuppressWarnings("unchecked")
	public void notifyAboutNotifications() {
		// changes as on 28th November 2013
		// adding this in order to update the GUI from the service
		// using broadcast receiver
		List<Order> mNotificationOrderList = (List<Order>)notificationCache.get(Global.NOTIFICATION_ORDERS);
		if(mNotificationOrderList != null) {
			Intent updateBroadCast = 
					new Intent(WaiterPadApplication.getAppContext()
							.getString(R.string.broadcast_notification_update));
			updateBroadCast.putExtra(Global.NOTIFICATION_COUNT, mNotificationOrderList.size());
			WaiterPadApplication.getAppContext().sendBroadcast(updateBroadCast);
		}else {
			Intent updateBroadCast = 
					new Intent(WaiterPadApplication.getAppContext()
							.getString(R.string.broadcast_notification_update));
			updateBroadCast.putExtra(Global.NOTIFICATION_COUNT, 0);
			WaiterPadApplication.getAppContext().sendBroadcast(updateBroadCast);
		}
		// changes end here
	}
	// changes end here
	
	// changes as on 28th November 2013
	// adding this method to get the count of notifications
	// inorder to set text on opening a new activity
	@SuppressWarnings("unchecked")
	public int getNotificationOrderCount() {
		List<Order> orders = (List<Order>)notificationCache.get(Global.NOTIFICATION_ORDERS);

		if(orders !=null) {
			return orders.size();
		}else {
			return 0;
		}
	}

	public boolean areOrdersEqual(Order order, Order orderOld) {
		boolean flag = true;
		// Log.i(TAG, " order number old : " + orderOld.getOrderNumber());
		// Log.i(TAG, " order number new : " + order.getOrderNumber());

		if ((orderOld.getOrderId() != null && order.getOrderId() != null)
				&& (orderOld.getOrderId().equals(order.getOrderId()))
				&& orderOld.getOrderStatus() != order.getOrderStatus()
				&& orderOld.getWaiterId().equals(
						Prefs.getKey(WaiterPadApplication.getAppContext(),
								Prefs.WAITER_ID)))
			return false;

		if ((orderOld.getGuests() != null && order.getGuests() != null)
				&& orderOld.getGuests().size() == order.getGuests().size()) {
			for (int i = 0; i < orderOld.getGuests().size(); i++) {
				Guest guestOld = orderOld.getGuests().get(i);
				if (guestOld.getGuestId() != null) {
					Guest guestNewPresent = Iterables.find(order.getGuests(),
							new SearchForGuest(guestOld.getGuestId()), null);

					if (guestNewPresent != null) {
						if (guestNewPresent.getOrderedItems() != null
								&& guestOld.getOrderedItems() != null) {
							if (guestNewPresent.getOrderedItems().size() == guestOld
									.getOrderedItems().size()) {
								for (int j = 0; j < guestNewPresent
										.getOrderedItems().size(); j++) {
									// ordereditem in the new order
									OrderedItem orderedItemPresent = guestNewPresent
											.getOrderedItems().get(j);

									// check for item status at the same
									// position
									OrderedItem orderItemCheck = Iterables
											.find(guestOld.getOrderedItems(),
													new SearchForOrderedItem(
															orderedItemPresent
																	.getId()),
													null);

									if (orderItemCheck != null) {
										// the ordered item is present
										// since the sizes are the same
										// get the ordered item at the same
										// position and compare the item status
										// ordered item in the old order
										OrderedItem orderItemCheckInner = guestOld
												.getOrderedItems().get(j);
										

										// changes as on 7th Jan 2014
										// adding a change here, so that the user is notified of modifier changes
										if(orderedItemPresent.getId().equals(
												orderItemCheckInner.getId()) && 
												(orderedItemPresent.getOrderedItemId().equals(
														orderItemCheckInner.getOrderedItemId()))) {
											
											if(orderedItemPresent.getQuantity() != 
													orderItemCheckInner.getQuantity()) {
												flag = false;
												break;
											}else {
												flag = true;
											}
											
											// checks the comments
											if(orderedItemPresent.getComment() != null && 
													orderItemCheckInner.getComment() !=null) {
												if(!orderedItemPresent.getComment().equals(orderItemCheckInner.getComment())) {
													// comments are different
													flag = false;
													break;
												}else {
													flag = true;
												}
											}else if(orderedItemPresent.getComment() == null && 
													orderItemCheckInner.getComment() != null) {
												flag = false;
												break;
											}else if(orderedItemPresent.getComment() != null && 
													orderItemCheckInner.getComment() == null) {
												flag = false;
												break;
											}else if(orderedItemPresent.getComment() == null && 
													orderItemCheckInner.getComment() == null) {
												flag = true;
											}
											
											if (orderedItemPresent.getId().equals(
													orderItemCheckInner.getId()) 
													&& (orderedItemPresent.getOrderedItemId().equals(
															orderItemCheckInner.getOrderedItemId()))
													&& orderedItemPresent
															.getOrderedItemStatus() == orderItemCheckInner
															.getOrderedItemStatus()) {
												flag = true;
											} else if (orderedItemPresent.getId()
													.equals(orderItemCheckInner
															.getId())
													&& (orderedItemPresent.getOrderedItemId().equals(
															orderItemCheckInner.getOrderedItemId()))
													&& (orderedItemPresent
															.getOrderedItemStatus() != orderItemCheckInner
															.getOrderedItemStatus())) {
												
												flag = false;
												break;
											}
											
											if(orderedItemPresent.getModifiers() != null && 
													orderItemCheckInner.getModifiers() == null) {
												// modifiers are either both present
												// or modifiers are either null
												flag = false;
												break;
											}else if(orderedItemPresent.getModifiers() == null && 
													orderItemCheckInner.getModifiers() != null) {
												flag = false;
												break;
											}else if(orderedItemPresent.getModifiers()!= null && 
													orderItemCheckInner.getModifiers() != null) {
												// both have modifiers
												List<ModifierMaster> modifiersOfItemFromService = orderedItemPresent.getModifiers();
												List<ModifierMaster> modifiersOfOldItem = orderItemCheckInner.getModifiers();
												
												if(modifiersOfItemFromService.size() != modifiersOfOldItem.size()) {
													flag = false;
													break;
												}else {
													// sizes are the same, check if the ids are the same or not
													for(int m = 0;m<modifiersOfItemFromService.size();m++) {
														ModifierMaster modifierOfItemFromService = modifiersOfItemFromService.get(i);
														ModifierMaster modifierOfOldItem = 
																Iterables.find(modifiersOfOldItem, 
																		new SearchForModifier(modifierOfItemFromService.getId()),null);
														
														// the modifier exists
														// check for the group ids
														if(modifierOfOldItem != null) {
															if(modifierOfItemFromService.getGroupId() != null && 
																	modifierOfOldItem.getGroupId() != null) {
																if(modifierOfItemFromService.getGroupId().equals(modifierOfOldItem.getGroupId())) {
																	// same
																	flag = true;
																}else {
																	flag = false;
																	break;
																}
															}else if(modifierOfItemFromService.getGroupId() == null && 
																	modifierOfOldItem.getGroupId() != null) {
																flag = false;
																break;
															}else if(modifierOfOldItem.getGroupId() == null && 
																		modifierOfItemFromService.getGroupId() != null) {
																flag = false;
																break;
															}else if(modifierOfItemFromService.getGroupId() == null && 
																		modifierOfOldItem.getGroupId() == null) {
																flag  =true;
															}
														}else {
															// this modifiers is new
															// there is a change in modifiers 
															// even if the sizes are the same
															flag = false;
															break;
														}
													}
													
													if(flag == false) {
														break;
													}else {
														continue;
													}
												}
											}
										
										}
										// changes end here
									} else {

										/*
										 * Log.i(TAG,
										 * " order number old inside else  : " +
										 * orderOld .getOrderNumber());
										 * Log.i(TAG,
										 * " order number new inside else : " +
										 * order.getOrderNumber());
										 * 
										 * Log.i(TAG,
										 * " orderitem status old orderItemCheck.getOrderedItemStatus() : "
										 * + orderItemCheck
										 * .getOrderedItemStatus()); Log.i(TAG,
										 * " orderitem status old orderedItemPresent.getOrderedItemStatus() : "
										 * + orderedItemPresent
										 * .getOrderedItemStatus());
										 */
										flag = false;
										/*
										 * Log.i(TAG,
										 * "falg is false, ordered item is new"
										 * );
										 */
										break;
									}
								}

								if (flag == false) {
									break;
								} else {
									continue;
								}
							} else {
								// when the guests' ordered item sizes are not
								// the same
								/*
								 * Log.i(TAG, " order number old : " +
								 * orderOld.getOrderNumber()); Log.i(TAG,
								 * " order number new : " +
								 * order.getOrderNumber());
								 * 
								 * Log.i(TAG, " order number new size: " +
								 * (guestNewPresent .getOrderedItems() != null ?
								 * guestNewPresent .getOrderedItems() .size() :
								 * "null")); Log.i(TAG,
								 * " order number old size: " +
								 * (guestOld.getOrderedItems() != null ?
								 * guestOld .getOrderedItems() .size() :
								 * "null"));
								 * 
								 * Log.i(TAG,
								 * "falg is false, ordered item size is not the same"
								 * );
								 */
								flag = false;
								break;
							}
						}else {
							flag = false;
							break;
						}
					} else {
						flag = false;
						// Log.i(TAG, "falg is false, guest is not present ");
						break;
					}
				}
			}
		} else if ((orderOld.getGuests() != null && order.getGuests() != null)
				&& (orderOld.getGuests().size() != order.getGuests().size())) {
			flag = false;
		} else if (orderOld.getOrderId().equals(order.getOrderId())
				&& orderOld.getGuests() == null && order.getGuests() != null) {
			flag = false;
		} else if (orderOld.getOrderId().equals(order.getOrderId())
				&& order.getGuests() == null && orderOld.getGuests() != null) {
			flag = false;
		}

		// Log.i(TAG, " value of flag in the equals method " + flag);
		return flag;
	}

	public boolean checkIfPresentInNotificationAndIfEqual(Order order) {
		boolean flag = true;
		List<Order> mNotificationOrderList = (List<Order>) notificationCache
				.get(Global.NOTIFICATION_ORDERS);
		if (mNotificationOrderList != null && mNotificationOrderList.size() > 0) {
			// check if order is present

			Order orderPresent = Iterables.find(mNotificationOrderList,
					new SearchForOrder(order.getOrderId()), null);
			if (orderPresent != null) {
				if (areOrdersEqual(order, orderPresent)) {
					// there is no change in the order
					// return true
					flag = true;
				} else {
					flag = false;
				}
			} else {
				flag = false;
			}
		} else {
			flag = false;
		}

		return flag;
	}
	
	/**
	 * This function will compare the ordered items of two orders
	 * Called after checking if the orders are equal
	 * hence the assumption that the orders are equal holds
	 * @param orderNew : The order from the service
	 * @param orderOld : The order present already
	 * @Date : 5th Decemeber 2013, 10:43 am
	 * @author dhara.shah
	 * @return
	 */
	public boolean checkForOrderQuantities(Order orderNew, Order orderOld) {
		boolean flagIsDifferent = false;
		
		// orderNew: order from the service
		// orderPresent: order already present
		
		// check that the guests are not null
		// assuming that the orders have the same number of guests
		// since the orders are marked as equal (or being the same)
		if(orderNew.getGuests() != null && 
				orderOld.getGuests() != null) {
			
			List<Guest> guestsOfNewOrder = orderNew.getGuests();
			List<Guest> guestsOfOldOrder = orderOld.getGuests();
			
			for(int i=0;i<guestsOfNewOrder.size();i++) {
				Guest singleGuestOfNewOrder = guestsOfNewOrder.get(i);
				
				Guest singleGuestOfOldOrder = Iterables.find(guestsOfOldOrder,
						new SearchForGuest(singleGuestOfNewOrder.getGuestId()),null);
				
				// the guest is present
				// assuming that the guest is present
				// since the orders are marked as being the same
				if(singleGuestOfOldOrder != null) {
					// check that the guests have ordered items
					if(singleGuestOfNewOrder.getOrderedItems() != null &&
							singleGuestOfOldOrder.getOrderedItems() != null) {
						List<OrderedItem> orderedItemsOfNewGuest = 
								singleGuestOfNewOrder.getOrderedItems();
						
						List<OrderedItem> orderedItemsOfOldGuest = 
								singleGuestOfOldOrder.getOrderedItems();
						
						// assuming that the guests have the same number of ordered quantities
						for(int j=0;j<orderedItemsOfNewGuest.size();j++) {
							OrderedItem orderedItemOfSingleNewGuest =
									orderedItemsOfNewGuest.get(j);
							OrderedItem orderedItemOfSingleOldGuest = null;
							
							// if the item is added to the order from waiterpad
							if(orderedItemOfSingleNewGuest.getOrderedItemId() != null) {
								orderedItemOfSingleOldGuest = 
										Iterables.find(orderedItemsOfOldGuest,
												new SearchForOrderedItemForQuantity(orderedItemOfSingleNewGuest.getId(),
														orderedItemOfSingleNewGuest.getOrderedItemId()),null);
							}else {
								orderedItemOfSingleOldGuest = 
										Iterables.find(orderedItemsOfOldGuest,
												new SearchForOrderedItemForQuantity(orderedItemOfSingleNewGuest.getId(),
														""),null);
							}
									
							
							// assuming that the item exists
							// since the orders are marked as equal
							// check the ordered item quantity and also the status
							// if same: no change, the items are the same
							// if different then in that case there is a change in the order
							if(orderedItemOfSingleOldGuest != null) {
								if((orderedItemOfSingleNewGuest.getQuantity() == 
										orderedItemOfSingleOldGuest.getQuantity()) && 
										orderedItemOfSingleNewGuest.getOrderedItemStatus() == 
										orderedItemOfSingleOldGuest.getOrderedItemStatus()) {
									flagIsDifferent = false;
								}else {
									flagIsDifferent = true;
									return flagIsDifferent;
								}
							}
						}
					}
				}
			}
		}
		
		return flagIsDifferent;
	}
}
