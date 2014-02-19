package com.azilen.waiterpad.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.WaiterPadApplication;
import com.azilen.waiterpad.activities.MyOrderActivity;
import com.azilen.waiterpad.asynctask.GetCurrentOrderListAsyncTask;
import com.azilen.waiterpad.asynctask.UpdateOrderAsyncTask;
import com.azilen.waiterpad.data.Order;
import com.azilen.waiterpad.data.OrderList;
import com.azilen.waiterpad.managers.network.ServiceUrlManager;
import com.azilen.waiterpad.managers.notification.NotificationManager;
import com.azilen.waiterpad.managers.order.OrderManager;
import com.azilen.waiterpad.utils.Global;
import com.azilen.waiterpad.utils.Prefs;
import com.azilen.waiterpad.utils.search.SearchForIdFromList;
import com.azilen.waiterpad.utils.search.SearchForOrder;
import com.azilen.waiterpad.utils.search.SearchForWaiterOrders;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

public class GetAllOrdersService extends Service {
	private String mIpAddress;
	private String mPortAddress;
	private GetCurrentOrderListAsyncTask mGetCurrentOrderListAsyncTask;

	private String mWaiterId;
	private String mOrderId;
	private HashMap<String, List<Order>> mOrdersPerWaiterMap;
	private List<Order> mListOfOrders;
	
	private static boolean hasNewUpdate = false;
	private static boolean isOrderUpdated = false;
	private static boolean areMyOrdersUpdated = false;
	private static boolean areTableOrdersUpdated = false;

	private String TAG = this.getClass().getSimpleName();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		// Added Likhit
		if (!GetCurrentOrderListAsyncTask.isAsyncStarted) {

			//Global.logd(TAG + " called !");

			mIpAddress = Prefs.getKey(GetAllOrdersService.this,
					Prefs.IP_ADDRESS);
			mPortAddress = Prefs.getKey(GetAllOrdersService.this,
					Prefs.PORT_ADDRESS);
			mWaiterId = Prefs.getKey(GetAllOrdersService.this, Prefs.WAITER_ID);
			mOrderId =  Prefs.getKey(GetAllOrdersService.this, Prefs.ORDER_ID);

			if (ServiceUrlManager.getInstance().getServiceUrl().equals("")) {
				Global.logd("ServiceUrlManager URL null");
				ServiceUrlManager.getInstance().setBaseURL(mIpAddress,
						mPortAddress);
			}

			if (mIpAddress != null && mIpAddress.trim().length() > 0
					&& mPortAddress != null && mPortAddress.trim().length() > 0
					&& mWaiterId != "") {
				callAsyncMethod();
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void callAsyncMethod() {
		mGetCurrentOrderListAsyncTask = new GetCurrentOrderListAsyncTask(
				GetAllOrdersService.this, Global.GET_ALL_ORDERS_SERVICE_PARAM);
		mGetCurrentOrderListAsyncTask.execute();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void refreshOrders(OrderList result) {
		hasNewUpdate = true;

		List<Order> orderListForNotification = null;
		
		// list of running orders
		List<Order> lstOrders = (List<Order>)OrderManager.getInstance().getOrderCache().get(Global.RUNNING_ORDERS);

		if (result != null && mWaiterId != null
				&& mWaiterId.trim().length() > 0) {
			mOrdersPerWaiterMap = (HashMap<String, List<Order>>) OrderManager.getInstance().getOrderCache().get(Global.ORDER_PER_WAITER);

			if (mOrdersPerWaiterMap != null) {
				if (mOrdersPerWaiterMap.containsKey(mWaiterId)) {
					// list of orders of the waiter
					// old record
					mListOfOrders = mOrdersPerWaiterMap.get(mWaiterId);
				}
			}
			

			// the new record
			List<Order> orders = result.getOrders();

			if (orders != null) {
				if (lstOrders != null && lstOrders.size() != orders.size()) {
					hasNewUpdate = true;
					areTableOrdersUpdated = true;
				} else if (lstOrders == null && result != null) {
					hasNewUpdate = true;
					areTableOrdersUpdated = true;
				}
				
				// changes as on 10th December 2013
				// getting the list of orders that belong to the waiter
				// in order to find out if an order has been deleted or not
				// deleted orders will cause a size difference
				Collection<Order> collections =
						Collections2.filter(orders, new SearchForWaiterOrders(mWaiterId));
				
				List<Order> waiterOrdersFromService = new ArrayList<Order>();
				if(collections != null) {
					waiterOrdersFromService.addAll(collections);
				}else {
					waiterOrdersFromService = null;
				}
				
				if(waiterOrdersFromService != null && 
						mListOfOrders != null && 
						waiterOrdersFromService.size() != mListOfOrders.size()) {
					areMyOrdersUpdated = true;
				}else if(waiterOrdersFromService == null && 
						mListOfOrders != null) {
					areMyOrdersUpdated = true;
				}
				// changes end here

				int index = 0;
				orderListForNotification = new ArrayList<Order>();
				for (Order order : orders) {
					if (mListOfOrders != null) {

						Order orderPresent = Iterables.find(mListOfOrders,
								new SearchForOrder(order.getOrderId()), null);

						// its a new order not in the waiter list
						if (orderPresent == null) {
							hasNewUpdate = true;
							areTableOrdersUpdated = true;
							
							if (order.getWaiterId().equals(mWaiterId)) {
								// changes as on 10th December 2013
								areMyOrdersUpdated = true;
								// changes end here
								
								// notify the user of a new order only when the sent order 
								// has been replaced in the cache
								// and its a new order from iiko
								if(!Global.isSendOrderCalled) {
									boolean flag = NotificationManager.getInstance()
											.checkIfPresentInNotificationAndIfEqual(order);
									if (!flag) {
										orderListForNotification.add(order);
									}
								}
							}

							// changes as on 29th july 2013
						} else {
							// the order is not new
							// hence change the values of the order
							index = getPositionFromList(order, mListOfOrders);

							if (index == -1) {
								
								// changes as on 10th December 2013
								areTableOrdersUpdated = true;
								areMyOrdersUpdated = true;
								// changes end here
								
								Log.i(TAG,
										"the order is not present in the list !!! ");
								// the order does not exist
								// notify the user of a new order only when the sent order 
								// has been replaced in the cache
								// and its a new order from iiko
								if(!Global.isSendOrderCalled) {
									boolean flag = NotificationManager.getInstance()
											.checkIfPresentInNotificationAndIfEqual(order);
									if (!flag) {
										orderListForNotification.add(order);
									}
								}
							} else {
								// compare the values and then send a
								// notification

								if (NotificationManager.getInstance().areOrdersEqual(order, orderPresent)) {
									// nothing has changed
									Log.i(TAG,
											" ---- GENERATE NOTIFICATION CALLED ---- get all orders called equal");
									
									// changes as on 5th Decemember 2013
									// checks if the quantity of the items differ or not
									boolean areQuantitiesDiff =
											NotificationManager.getInstance()
												.checkForOrderQuantities(order, orderPresent);
									
									if(areQuantitiesDiff) {
										isOrderUpdated = true;
										areTableOrdersUpdated = true;
										areMyOrdersUpdated = true;
									}else {
										isOrderUpdated = false;
									}
									// changes end here
								} else {

									// Toast.makeText(getApplicationContext(),
									// "Not Equal" , 1000).show();
									// the order has been generated
									Log.i(TAG,
											" ---- GENERATE NOTIFICATION CALLED ---- get all orders called not equal");
									hasNewUpdate = true;
									areTableOrdersUpdated = true;
									
									// changes as on 28th November 2013
									// since the orders present and the order from the service 
									// are not the same, check if the order ids are the same
									// as that in the order screen
									// TRUE: Update the order screen
									// FALSE: Don't do anything
									
									// order ids are compared to see if they are the same or not
									if(order.getOrderId().equals(mOrderId)) {
										isOrderUpdated = true;
										areMyOrdersUpdated = true;
									}else {
										isOrderUpdated = false;
										areMyOrdersUpdated = true;
									}
									
									// changes end here
									
									List<String> lstOrderIds = 
											(List<String>)OrderManager.getInstance()
												.getOrderCache().get(Global.CHECKED_OUT_ORDER_IDS);
									
									if(lstOrderIds != null) {
										String orderIdInList = 
												Iterables.find(lstOrderIds, 
														new SearchForIdFromList(order.getOrderId()),null);
										
										// changes as on 7th December 2013
										// that means that the order is not present in the order checked-out list
										// not an order from waiterpad
										if(orderIdInList == null) {
											if(!Global.isSendOrderCalled) {
												String orderIdInCache = null;
												if((String)OrderManager.getInstance().getOrderCache().get(getString(R.string.order_from_waiterpad)) != null) {
													orderIdInCache = (String)OrderManager.getInstance().getOrderCache()
																.get(getString(R.string.order_from_waiterpad));
												}
												
												if(orderIdInCache != null) {
													if(!orderIdInCache.equals(order.getOrderId())) {
														orderListForNotification.add(order);
														Log.i("Dhara", " "
																+ orderListForNotification);
													}
												}else {
													if(!UpdateOrderAsyncTask.isRunning) {
														orderListForNotification.add(order);
														Log.i("Dhara", " "
																+ orderListForNotification);
													}
												}
											}
										}
										// changes end here
									}else {
										if(!Global.isSendOrderCalled) {
											Log.i("UpdateOrderAsyncTask", "service called now!!!!");
											String orderIdInCache = null;
											if((String)OrderManager.getInstance().getOrderCache().get(getString(R.string.order_from_waiterpad)) != null) {
												orderIdInCache = (String)OrderManager.getInstance().getOrderCache()
															.get(getString(R.string.order_from_waiterpad));
											}
											
											if(orderIdInCache != null) {
												if(!orderIdInCache.equals(order.getOrderId())) {
													orderListForNotification.add(order);
													Log.i("Dhara", " "
															+ orderListForNotification);
												}
											}else {
												Log.i("UpdateOrderAsyncTask", "notification called now!!!! order id not in cache");
												if(!UpdateOrderAsyncTask.isRunning) {
													orderListForNotification.add(order);
													Log.i("Dhara", " "
															+ orderListForNotification);
												}
											}
										}
									}
								}
							}
						}
					} else {
						// TODO: 29th September, 1st order notification
						// checks if the order generated has an order id (not an
						// empty order
						// also checks if the order is the waiters own order
						if (order.getOrderId() != null
								&& order.getWaiterId().equals(mWaiterId)) {
							if(!Global.isSendOrderCalled) {
								boolean flag = com.azilen.waiterpad.managers.notification.NotificationManager.getInstance()
										.checkIfPresentInNotificationAndIfEqual(order);
								
								if (!flag) {
									orderListForNotification.add(order);
									Log.i("Dhara", " "
										+ orderListForNotification);
								}
								
								areMyOrdersUpdated = true;
								hasNewUpdate = true;
								areTableOrdersUpdated = true;
							}
						}
					}
				}
			}
		} else if (result == null) {
			hasNewUpdate = true;
			isOrderUpdated = false;
			areMyOrdersUpdated = true;
			areTableOrdersUpdated = true;
			mOrderId = "";
		}
		
		OrderManager.getInstance().storeCurrentOrdersInMemory(result);

		if (MyOrderActivity.mOpenOrdersAdapter != null) {
			MyOrderActivity.mOpenOrdersAdapter.notifyDataSetChanged();
		}

		if (hasNewUpdate) {
			Intent updateBroadCast = new Intent(getString(R.string.broadcast_table_update));
			this.sendBroadcast(updateBroadCast);
			hasNewUpdate = false;
		}
		
		if(isOrderUpdated) {
			Intent updateBroadCast = new Intent(getString(R.string.broadcast_order_update));
			updateBroadCast.putExtra(Global.ORDER_ID, mOrderId);
			this.sendBroadcast(updateBroadCast);
			isOrderUpdated = false;
		}
		
		// changes as on 10th December 2013
		if (areMyOrdersUpdated) {
			Intent updateBroadCast = new Intent(getString(R.string.broadcast_myorders_update));
			this.sendBroadcast(updateBroadCast);
			areMyOrdersUpdated = false;
		}
		
		if (areTableOrdersUpdated) {
			Intent updateBroadCast = new Intent(getString(R.string.broadcast_tableorders_update));
			this.sendBroadcast(updateBroadCast);
			areTableOrdersUpdated = false;
		}
		
		NotificationManager.getInstance().generateNotification(orderListForNotification);
		// changes end here
	}

	private int getPositionFromList(Order order, List<Order> orderList) {
		int value = -1;
		for (int i = 0; i < orderList.size(); i++) {
			Order orderObj = orderList.get(i);
			if (orderObj != null) {
				if (orderObj.getOrderId().equals(order.getOrderId())) {
					value = i;
					break;
				}
			}
		}

		return value;
	}
}
