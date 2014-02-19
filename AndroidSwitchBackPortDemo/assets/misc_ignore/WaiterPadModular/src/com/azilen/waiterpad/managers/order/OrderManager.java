package com.azilen.waiterpad.managers.order;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.map.MultiKeyMap;

import android.support.v4.util.LruCache;
import android.util.Log;

import com.azilen.waiterpad.WaiterPadApplication;
import com.azilen.waiterpad.asynctask.GetCurrentOrderListAsyncTask;
import com.azilen.waiterpad.data.Guest;
import com.azilen.waiterpad.data.ModifierMaster;
import com.azilen.waiterpad.data.Order;
import com.azilen.waiterpad.data.Order.OrderStatus;
import com.azilen.waiterpad.data.OrderList;
import com.azilen.waiterpad.data.OrderResponse;
import com.azilen.waiterpad.data.OrderedItem;
import com.azilen.waiterpad.data.OrderedItem.OrderedItemStatus;
import com.azilen.waiterpad.data.Tables;
import com.azilen.waiterpad.managers.language.LanguageManager;
import com.azilen.waiterpad.utils.Global;
import com.azilen.waiterpad.utils.search.SearchForBillRequestOrders;
import com.azilen.waiterpad.utils.search.SearchForGuest;
import com.azilen.waiterpad.utils.search.SearchForIdFromList;
import com.azilen.waiterpad.utils.search.SearchForOrder;
import com.azilen.waiterpad.utils.search.SearchForOrderedItemForQuantity;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class OrderManager {
	/* Singleton Pattern using Eager Initialization */
	private static final OrderManager instance = new OrderManager();
	private LruCache<String, Object> orderCache;
	
	// max cache size 1 MB = 1024 KB
	private int maxSize = 5 * 1024 * 1024;

	/* Singleton Pattern */
	public static OrderManager getInstance() {
		return instance;
	}
	
	public OrderManager() {
		orderCache = new LruCache<String, Object>(maxSize);
	}
	
	public LruCache<String, Object> getOrderCache() {
		return orderCache;
	}
	
	/**
	 * Deserializes the {@link OrderStatus} enum that gson receives
	 * 
	 * @author dhara.shah
	 * 
	 */
	public static class OrderStatusDeserializer implements
			JsonDeserializer<Order.OrderStatus> {
		@Override
		public OrderStatus deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext ctx) throws JsonParseException {
			int typeInt = json.getAsInt();
			return OrderStatus.getOrderStatus(typeInt);
		}
	}

	/**
	 * Deserializes the {@link OrderedItemStatus} enum that gson receives
	 * 
	 * @author dhara.shah
	 * 
	 */
	public static class OrderedItemStatusDeserializer implements
			JsonDeserializer<OrderedItem.OrderedItemStatus> {
		@Override
		public OrderedItem.OrderedItemStatus deserialize(JsonElement json,
				Type typeOfT, JsonDeserializationContext ctx)
				throws JsonParseException {
			int typeInt = json.getAsInt();
			return OrderedItemStatus.getItemStatus(typeInt);
		}
	}
	
	/**
	 * Stores all the current orders in the memory of the device in the form of
	 * orders per waiter and also orders per table Used by
	 * {@GetAllDataAsyncTask} and
	 * {@GetCurrentOrderListAsyncTask}
	 * 
	 * @param result
	 * @param memCache
	 */
	public void storeCurrentOrdersInMemory(OrderList result) {
		if (result != null) {

			if (result.getOrders() != null && result.getOrders().size() > 0) {

				List<Order> lst = result.getOrders();

				if (lst != null) {
					orderCache.put(Global.RUNNING_ORDERS, lst);
				}

				if (lst != null) {
					Collection<Order> collectionOrders = Collections2.filter(
							lst, new SearchForBillRequestOrders());

					if (collectionOrders != null) {
						List<Order> billRequestOrders = new ArrayList<Order>(
								collectionOrders);

						if (billRequestOrders != null) {
							orderCache.put(Global.BILL_REQUEST, billRequestOrders);
						}
					}
				}

				// This will be used by MyCurrentOrders Screen
				// hash map stores the order list per waiter id
				// the waiter id being a unique id
				HashMap<String, List<Order>> orderMap = new HashMap<String, List<Order>>();

				// list that stores the orders against each waiter id
				List<Order> ordersPerWaiter = new ArrayList<Order>();

				// Stores the orders per table section wise
				MultiKeyMap multiKeyMap = new MultiKeyMap();

				// store the orders per table now
				List<Order> ordersPerTable = new ArrayList<Order>();

				// iterate through the list of orders
				for (Order order : lst) {
					Tables table = order.getTable();
					// store the orders as per the waiter id and order id keys
					if (orderMap.containsKey(order.getWaiterId())) {
						// if the hashmap contains the waiter id
						// add the order to the list
						ordersPerWaiter.add(order);
					} else {
						// if the waiter id is not there in the hashmap
						// create a new order list for the waiter
						// and add the order to the list
						ordersPerWaiter = new ArrayList<Order>();
						ordersPerWaiter.add(order);
					}

					if (multiKeyMap.containsKey(table.getSectionId(),
							table.getTableId())) {
						// case when the table and section details are there
						ordersPerTable = (List<Order>) multiKeyMap.get(
								table.getSectionId(), table.getTableId());
						ordersPerTable.add(order);
					} else {
						// case when the table and section details are not there
						ordersPerTable = new ArrayList<Order>();
						ordersPerTable.add(order);
					}

					orderMap.put(order.getWaiterId(), ordersPerWaiter);
					multiKeyMap.put(table.getSectionId(), table.getTableId(),
							ordersPerTable);
					//Log.i(TAG, "size of orders : " + ordersPerWaiter.size());
				}

				orderCache.put(Global.ORDER_PER_WAITER, orderMap);
				orderCache.put(Global.PER_TABLE_ORDERS, multiKeyMap);
			}
		} else {
			orderCache.remove(Global.ORDER_PER_WAITER);
			orderCache.remove(Global.PER_TABLE_ORDERS);
			orderCache.remove(Global.BILL_REQUEST);
			orderCache.remove(Global.RUNNING_ORDERS);
			orderCache.remove(Global.CHECKED_OUT_ORDER_IDS);
		}
	}

	public void clearAllOrders() {
		orderCache.remove(Global.ORDER_PER_WAITER);
		orderCache.remove(Global.PER_TABLE_ORDERS);
		orderCache.remove(Global.BILL_REQUEST);
		orderCache.remove(Global.RUNNING_ORDERS);
		orderCache.remove(Global.CHECKED_OUT_ORDER_IDS);
	}
	
	/**
	 * Created on : 7th December 2013, 10:48 am
	 * storeCheckoutOrders will store those orders
	 * that have been checked out from waiterpad
	 * so as to prevent notification receival when the 
	 * user checks the order out from waiterpad
	 */
	public void storeCheckoutOrders(String orderId) {
		List<String> lstOrderIds = (List<String>)orderCache.get(Global.CHECKED_OUT_ORDER_IDS);
		
		if(lstOrderIds != null) {
			String checkedOutOrderId = 
					Iterables.find(lstOrderIds, new SearchForIdFromList(orderId),null);
			
			// it is not present in the list
			// so add to the list
			if(checkedOutOrderId == null) {
				lstOrderIds.add(orderId);
				orderCache.put(Global.CHECKED_OUT_ORDER_IDS, lstOrderIds);
			}
		}else {
			lstOrderIds = new ArrayList<String>();
			lstOrderIds.add(orderId);
			orderCache.put(Global.CHECKED_OUT_ORDER_IDS, lstOrderIds);
		}
	}
	

	/**
	 * The order is replaced in the cache and added to the orderPerWaiter cache
	 * called when a new order is placed successfully and called when a new item
	 * is added successfully
	 * 
	 * @param result
	 */
	@SuppressWarnings("unchecked")
	public void refreshDataAfterGettingResponse(OrderResponse result) {
		Order order = result.getOrder();
		List<Order> lstOrders = (List<Order>) orderCache
				.get(Global.RUNNING_ORDERS);

		if (lstOrders != null && lstOrders.size() > 0) {
			Order orderPresent = Iterables.find(lstOrders, new SearchForOrder(
					order.getOrderId()), null);

			if (orderPresent != null) {
				lstOrders.remove(orderPresent);
				lstOrders.add(order);
			} else {
				lstOrders.add(order);
			}
		} else {
			lstOrders = new ArrayList<Order>();
			lstOrders.add(order);
		}

		orderCache.remove(Global.RUNNING_ORDERS);
		orderCache.put(Global.RUNNING_ORDERS, lstOrders);

		//Log.i(TAG, " list of running orders " + lstOrders.size());

		// Store this order in the orderper waiter list
		HashMap<String, List<Order>> orderMap = (HashMap<String, List<Order>>) orderCache
				.get(Global.ORDER_PER_WAITER);

		// get the map that holds the orders per waiter
		// and check if its empty or null
		if (orderMap != null) {
			if (orderMap.get(order.getWaiterId()) != null) {
				lstOrders = orderMap.get(order.getWaiterId());

				if (lstOrders != null && lstOrders.size() > 0) {
					Order orderPresent = Iterables.find(lstOrders,
							new SearchForOrder(order.getOrderId()), null);

					if (orderPresent != null) {
						lstOrders.remove(orderPresent);
						lstOrders.add(order);
					} else {
						lstOrders.add(order);
					}
				}
				// orderMap.remove(order.getWaiterId());
			}
		} else {
			//Log.i(TAG, "ordermap is null");
			// create a new order map to store the orders fresh
			orderMap = new HashMap<String, List<Order>>();
			lstOrders = new ArrayList<Order>();
			lstOrders.add(order);
		}

		orderMap.put(order.getWaiterId(), lstOrders);

		// Stores the orders per table section wise
		MultiKeyMap multiKeyMap = (MultiKeyMap) orderCache
				.get(Global.PER_TABLE_ORDERS);
		// MultiKeyMap sessionMap = (MultiKeyMap)memCache.get("sessionOrders");

		// iterate through the list of orders
		// and set editable false and also store the orders table wise
		if (lstOrders != null) {
			for (Order orderObj : lstOrders) {
				List<Guest> guests = orderObj.getGuests();
				if (guests != null) {
					for (Guest guest : guests) {
						List<OrderedItem> orderedItems = guest
								.getOrderedItems();
						if (orderedItems != null) {
							for (OrderedItem oObj : orderedItems) {
								oObj.setEditable(false);
							}
						}
					}
				}
			}
		}

		Tables table = order.getTable();
		// store the orders per table now
		List<Order> ordersPerTable = null;

		if (order.getOrderId() != null && !order.isModified()) {
			if (multiKeyMap != null) {
				if (multiKeyMap.get(table.getSectionId(), table.getTableId()) != null) {
					ordersPerTable = (List<Order>) multiKeyMap.get(
							table.getSectionId(), table.getTableId());
					if (multiKeyMap.containsKey(table.getSectionId(),
							table.getTableId())) {
						// case when the table and section details are there

						Order orderPresent = Iterables.find(ordersPerTable,
								new SearchForOrder(order.getOrderId()), null);

						if (orderPresent != null) {
							ordersPerTable.remove(orderPresent);
							ordersPerTable.add(order);
						} else {
							ordersPerTable.add(order);
						}
					} else {
						// case when the table and section details are not there
						ordersPerTable = new ArrayList<Order>();
						ordersPerTable.add(order);
					}
				} else {
					// case when the table and section details are not there
					ordersPerTable = new ArrayList<Order>();
					ordersPerTable.add(order);
				}
			} else {
				multiKeyMap = new MultiKeyMap();
			}

		}

		orderMap.put(order.getWaiterId(), lstOrders);
		//Log.i(TAG, "added to the ordermap and or list");
		multiKeyMap.put(table.getSectionId(), table.getTableId(),
				ordersPerTable);
		
		orderCache.remove(Global.ORDER_PER_WAITER);
		orderCache.put(Global.ORDER_PER_WAITER, orderMap);
		
		orderCache.remove(Global.PER_TABLE_ORDERS);
		orderCache.put(Global.PER_TABLE_ORDERS, multiKeyMap);
	}
	
	/**
	 * Update order after updating items
	 * 
	 * @param result
	 * @param memCache
	 */
	public void updateOrderAfterGettingResponse(OrderResponse result) {
		Order order = result.getOrder();
		List<Order> lstOrders = (List<Order>) orderCache
				.get(Global.RUNNING_ORDERS);

		if (lstOrders != null && lstOrders.size() > 0) {
			Order orderPresent = Iterables.find(lstOrders, new SearchForOrder(
					order.getOrderId()), null);

			if (orderPresent != null) {
				lstOrders.remove(orderPresent);
				orderPresent = order;
			}

			lstOrders.add(orderPresent);
		} else {
			lstOrders = new ArrayList<Order>();
			lstOrders.add(order);
		}

		orderCache.put(Global.RUNNING_ORDERS, lstOrders);
		
		// Store this order in the orderper waiter list
		HashMap<String, List<Order>> orderMap = (HashMap<String, List<Order>>) orderCache
				.get(Global.ORDER_PER_WAITER);

		// get the map that holds the orders per waiter
		// and check if its empty or null
		if (orderMap != null) {
			if (orderMap.get(order.getWaiterId()) != null) {
				lstOrders = orderMap.get(order.getWaiterId());

				if (lstOrders != null && lstOrders.size() > 0) {
					Order orderPresent = Iterables.find(lstOrders,
							new SearchForOrder(order.getOrderId()), null);

					if (orderPresent != null) {
						lstOrders.remove(orderPresent);
						orderPresent = order;
					}
					lstOrders.add(orderPresent);
				}
				// orderMap.remove(order.getWaiterId());
			}
		} else {
			//Log.i(TAG, "ordermap is null");
			// create a new order map to store the orders fresh
			orderMap = new HashMap<String, List<Order>>();
			lstOrders = new ArrayList<Order>();
			lstOrders.add(order);
		}

		orderMap.put(order.getWaiterId(), lstOrders);

		// Stores the orders per table section wise
		MultiKeyMap multiKeyMap = (MultiKeyMap) orderCache
				.get(Global.PER_TABLE_ORDERS);
		// MultiKeyMap sessionMap = (MultiKeyMap)memCache.get("sessionOrders");

		// iterate through the list of orders
		// and set editable false and also store the orders table wise
		if (lstOrders != null) {
			for (Order orderObj : lstOrders) {
				if (orderObj != null) {
					List<Guest> guests = orderObj.getGuests();
					if (guests != null) {
						for (Guest guest : guests) {
							List<OrderedItem> orderedItems = guest
									.getOrderedItems();
							if (orderedItems != null) {
								for (OrderedItem oObj : orderedItems) {
									oObj.setEditable(false);
								}
							}
						}
					}
				}
			}
		}

		Tables table = order.getTable();
		// store the orders per table now
		List<Order> ordersPerTable = null;

		if (order.getOrderId() != null && !order.isModified()) {
			if (multiKeyMap != null) {
				if (multiKeyMap.get(table.getSectionId(), table.getTableId()) != null) {
					ordersPerTable = (List<Order>) multiKeyMap.get(
							table.getSectionId(), table.getTableId());
					if (multiKeyMap.containsKey(table.getSectionId(),
							table.getTableId())) {
						// case when the table and section details are there

						if (ordersPerTable != null && ordersPerTable.size() > 0) {
							Order orderPresent = Iterables.find(ordersPerTable,
									new SearchForOrder(order.getOrderId()),
									null);

							if (orderPresent != null) {
								ordersPerTable.remove(orderPresent);
								orderPresent = order;
							}
							ordersPerTable.add(orderPresent);
						}
					} else {
						// case when the table and section details are not there
						ordersPerTable = new ArrayList<Order>();
						ordersPerTable.add(order);
					}
				} else {
					// case when the table and section details are not there
					ordersPerTable = new ArrayList<Order>();
					ordersPerTable.add(order);
				}
			} else {
				multiKeyMap = new MultiKeyMap();
			}

		}

		orderMap.put(order.getWaiterId(), lstOrders);
		//Log.i(TAG, "added to the ordermap and or list");
		multiKeyMap.put(table.getSectionId(), table.getTableId(),
				ordersPerTable);
		orderCache.put(Global.ORDER_PER_WAITER, orderMap);
		orderCache.put(Global.PER_TABLE_ORDERS, multiKeyMap);
	}

	@SuppressWarnings("unchecked")
	public void removeFromOrders(Order order) {
		List<Order> lstOrders = (List<Order>) orderCache
				.get(Global.RUNNING_ORDERS);

		
		if (lstOrders != null && lstOrders.size() > 0) {
			if (order.getOrderId() == null) {
				lstOrders.remove(order);
				orderCache.put(Global.RUNNING_ORDERS, lstOrders);
			}
		}

		// Store this order in the orderper waiter list
		HashMap<String, List<Order>> orderMap = (HashMap<String, List<Order>>) orderCache
				.get(Global.ORDER_PER_WAITER);

		// get the map that holds the orders per waiter
		// and check if its empty or null
		if (orderMap != null) {
			if (orderMap.get(order.getWaiterId()) != null) {
				lstOrders = orderMap.get(order.getWaiterId());

				if (lstOrders != null && lstOrders.size() > 0) {
					if (order.getOrderId() == null) {
						lstOrders.remove(order);
						orderMap.remove(order.getWaiterId());
					}
				}
			}
		}

		// Stores the orders per table section wise
		MultiKeyMap multiKeyMap = (MultiKeyMap) orderCache
				.get(Global.PER_TABLE_ORDERS);

		Tables table = order.getTable();
		// store the orders per table now
		List<Order> ordersPerTable = null;

		if (multiKeyMap != null) {
			if (multiKeyMap.get(table.getSectionId(), table.getTableId()) != null) {
				ordersPerTable = (List<Order>) multiKeyMap.get(
						table.getSectionId(), table.getTableId());
				if (multiKeyMap.containsKey(table.getSectionId(),
						table.getTableId())) {
					// case when the table and section details are there
					if (order.getOrderId() == null) {
						ordersPerTable.remove(order);
						multiKeyMap.put(table.getSectionId(),
								table.getTableId(), ordersPerTable);
					}
				}
			}
		}

		if (orderMap != null) {
			orderMap.put(order.getWaiterId(), lstOrders);
			//Log.i(TAG, "added to the ordermap and or list");
			orderCache.put(Global.ORDER_PER_WAITER, orderMap);
		}

		if (multiKeyMap != null) {
			orderCache.put(Global.PER_TABLE_ORDERS, multiKeyMap);
		}
	}

	@SuppressWarnings("unchecked")
	public void addToBillRequest(String orderId) {
		if (orderId != null) {
			// save to bill request and
			// remove from current running orders
			List<Order> lstOrders = (List<Order>) orderCache
					.get(Global.RUNNING_ORDERS);
			if (lstOrders != null) {
				Order order = Iterables.find(lstOrders, new SearchForOrder(
						orderId), null);

				if (order != null) {
					// remove from the list
					lstOrders.remove(order);
					order.setOrderStatus(OrderStatus.BILL);
					lstOrders.add(order);
					orderCache.put(Global.RUNNING_ORDERS, lstOrders);

					HashMap<String, List<Order>> orderMap = (HashMap<String, List<Order>>) orderCache
							.get(Global.ORDER_PER_WAITER);
					if (orderMap != null) {
						List<Order> orders = orderMap.get(order.getWaiterId());

						if (orders != null) {
							Order waiterOrder = Iterables.find(orders,
									new SearchForOrder(orderId), null);
							if (waiterOrder != null) {
								orders.remove(waiterOrder);
								order.setOrderStatus(OrderStatus.BILL);
								orders.add(order);
								orderMap.remove(waiterOrder.getWaiterId());
								orderMap.put(waiterOrder.getWaiterId(), orders);
							}
						}
					}

					MultiKeyMap multiKeyMap = (MultiKeyMap) orderCache
							.get(Global.PER_TABLE_ORDERS);

					if (multiKeyMap != null) {
						Tables table = order.getTable();
						if (multiKeyMap.get(table.getSectionId(),
								table.getTableId()) != null) {
							List<Order> ordersPerTable = (List<Order>) multiKeyMap
									.get(table.getSectionId(),
											table.getTableId());
							if (multiKeyMap.containsKey(table.getSectionId(),
									table.getTableId())) {
								// case when the table and section details are
								// there
								ordersPerTable.remove(order);
								order.setOrderStatus(OrderStatus.BILL);
								ordersPerTable.add(order);
								multiKeyMap.put(table.getSectionId(),
										table.getTableId(), ordersPerTable);
							}
						}
					}
				}

				// add to the billrequested list
				List<Order> billRequestOrders = (List<Order>)orderCache
						.get(Global.BILL_REQUEST);

				if (billRequestOrders != null) {
					Order orderBilled = Iterables.find(billRequestOrders,
							new SearchForOrder(orderId), null);

					if (order != null)
						order.setOrderStatus(OrderStatus.BILL);

					if (orderBilled != null) {
						billRequestOrders.remove(orderBilled);
					}
					billRequestOrders.add(order);
				} else {
					billRequestOrders = new ArrayList<Order>();
					order.setOrderStatus(OrderStatus.BILL);
					billRequestOrders.add(order);
				}
				orderCache.put(Global.BILL_REQUEST, billRequestOrders);
			}
		}
	}
	
	/**
	 * Gets the current orders that are running
	 * not used currently
	 */
	public void getOpenOrders() {
		GetCurrentOrderListAsyncTask getCurrentOrderListAsyncTask = new GetCurrentOrderListAsyncTask(
				WaiterPadApplication.getAppContext(), "utils");
		getCurrentOrderListAsyncTask.execute();
	}
	
	/**
	 * Checks for order changes, incase they are updated from iiko
	 * And changes the qty number or status accordingly
	 * If a new item is added or a new guest is added, or an item is deleted
	 * The order will be replaced with the order from IIKO.
	 * Limitations: Any new item , guest addition, or item or guest deletion
	 * will cause the order to be fully replaced, 
	 * and the order at the Waiter pad front  will be lost
	 * 
	 * Created as on 6th Jan 2014, 2:07 pm
	 * 
	 * @param orderNew : the order from IIKO
	 * @param orderOld : the old order, the order currently being displayed
	 * @return
	 */
	public synchronized Order performOperationsOnOrderJustForOrderStatusChange(Order orderNew, Order orderOld) {
 		Order order = orderNew;
		
		if(orderNew != null && orderOld != null) {
			
			if(orderNew.getOrderStatus() != null && orderOld.getOrderStatus() != null) {
				// the order has been bill requested, then replace the order
				// no new items can be added
				if(orderNew.getOrderStatus().ordinal() == 2) {
					order = orderNew;
					return order;
				}
			}
			
			if(orderNew.getGuests() != null && 
					orderOld.getGuests() != null) {
				// the size of the guests is more in the new order
				// return the new order, order will be replaced
				if(orderNew.getGuests().size() > orderOld.getGuests().size()) {
					// add the new guests to the old order
					// changes after discussion as on 6th Jan 2014, 3:27pm
					
					orderOld = addNewGuests(orderNew, orderOld);
					order = orderOld;
					order = setGuestNames(order);
					return order;
				}else if(orderNew.getGuests().size() == orderOld.getGuests().size()){
					// orders have the same number of guests
					// get each guest and compare ordered items
					// if different return the new order
					List<Guest> guestsOfNewOrder = orderNew.getGuests();
					List<Guest> guestsOfOldOrder = orderOld.getGuests();

					for(int i=0;i<guestsOfNewOrder.size();i++) {
						Guest singleGuestOfNewOrder = guestsOfNewOrder.get(i);

						Guest singleGuestOfOldOrder = Iterables.find(guestsOfOldOrder,
								new SearchForGuest(singleGuestOfNewOrder.getGuestId()),null);

						// the guest is present
						// since the orders are marked as being the same
						if(singleGuestOfOldOrder != null) {
							// check that the guests have ordered items
							if(singleGuestOfNewOrder.getOrderedItems() != null &&
									singleGuestOfOldOrder.getOrderedItems() != null) {
								List<OrderedItem> orderedItemsOfNewGuest = 
										singleGuestOfNewOrder.getOrderedItems();

								List<OrderedItem> orderedItemsOfOldGuest = 
										singleGuestOfOldOrder.getOrderedItems();


								if(orderedItemsOfNewGuest != null && orderedItemsOfOldGuest != null) {
									if(orderedItemsOfNewGuest.size() > orderedItemsOfOldGuest.size()) {
										Order orderObj = addNewItemsToOrder(orderedItemsOfNewGuest,orderedItemsOfOldGuest, 
												singleGuestOfOldOrder,guestsOfOldOrder,orderOld, i);
									
										if(orderObj != null) {
											orderOld = orderObj;
											order = orderOld;
										}
									}else if(orderedItemsOfNewGuest.size() == orderedItemsOfOldGuest.size()){

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


											// the item exists
											// check the ordered item quantity and also the status
											// if same: no change, the items are the same
											// if different then in that case there is a change in the order
											if(orderedItemOfSingleOldGuest != null) {
												
												if(orderedItemOfSingleNewGuest.getModifiers() != null) {
													orderedItemOfSingleOldGuest.setModifiers(orderedItemOfSingleNewGuest.getModifiers());
												}else {
													if(!orderedItemOfSingleNewGuest.isEditable()) {
														orderedItemOfSingleOldGuest.setModifiers(orderedItemOfSingleNewGuest.getModifiers());
													}
												}
												
												if((orderedItemOfSingleNewGuest.getQuantity() == 
														orderedItemOfSingleOldGuest.getQuantity())) {
													
													// status are not the same
													if(orderedItemOfSingleNewGuest.getOrderedItemStatus() != 
															orderedItemOfSingleOldGuest.getOrderedItemStatus()) {
														// replace this ordered item in the old order
														
														orderedItemsOfOldGuest.set(j, orderedItemOfSingleNewGuest);
														singleGuestOfOldOrder.setOrderedItems(orderedItemsOfOldGuest);
														guestsOfOldOrder.set(i,singleGuestOfOldOrder);
														orderOld.setGuests(guestsOfOldOrder);
														order = orderOld;
													}else {
														order = orderOld;
													}	
												}else {
													// quantities are not the same
													// replace order
													
													Order orderObj = quantityNotSame(orderedItemOfSingleNewGuest, 
															orderedItemOfSingleOldGuest, orderedItemsOfOldGuest, singleGuestOfOldOrder, 
															guestsOfOldOrder, orderOld, j, i);
													
													if(orderObj  != null) {
														orderOld = orderObj;
														order  = orderOld;
													}
												}
											}else {
												orderedItemsOfOldGuest.add(orderedItemOfSingleNewGuest);
												singleGuestOfOldOrder.setOrderedItems(orderedItemsOfOldGuest);
												guestsOfOldOrder.set(i,singleGuestOfOldOrder);
												orderOld.setGuests(guestsOfOldOrder);
												order = orderOld;
											}
											//return order;
										}
									}else if(orderedItemsOfNewGuest.size() < orderedItemsOfOldGuest.size()) {
										for(int j=0;j<orderedItemsOfOldGuest.size();j++) {
											OrderedItem orderedItemOfSingleOldGuest =
													orderedItemsOfOldGuest.get(j);
											OrderedItem orderedItemOfSingleNewGuest = null;

											// if the item is added to the order from waiterpad
											if(orderedItemOfSingleOldGuest.getOrderedItemId() != null) {
												orderedItemOfSingleNewGuest = 
														Iterables.find(orderedItemsOfNewGuest,
																new SearchForOrderedItemForQuantity(orderedItemOfSingleOldGuest.getId(),
																		orderedItemOfSingleOldGuest.getOrderedItemId()),null);
											}else {
												orderedItemOfSingleNewGuest = 
														Iterables.find(orderedItemsOfNewGuest,
																new SearchForOrderedItemForQuantity(orderedItemOfSingleOldGuest.getId(),
																		""),null);
											}

											// the new order has this item
											if(orderedItemOfSingleNewGuest != null) {
												
												if(orderedItemOfSingleNewGuest.getModifiers() != null) {
													orderedItemOfSingleOldGuest.setModifiers(orderedItemOfSingleNewGuest.getModifiers());
												}else {
													if(!orderedItemOfSingleNewGuest.isEditable()) {
														orderedItemOfSingleOldGuest.setModifiers(orderedItemOfSingleNewGuest.getModifiers());
													}
												}
												
												if((orderedItemOfSingleNewGuest.getQuantity() == 
														orderedItemOfSingleOldGuest.getQuantity())) {

													// status are not the same
													if(orderedItemOfSingleNewGuest.getOrderedItemStatus() != 
															orderedItemOfSingleOldGuest.getOrderedItemStatus()) {
														// replace this ordered item in the old order
														orderedItemOfSingleOldGuest
															.setOrderedItemStatus(orderedItemOfSingleNewGuest.getOrderedItemStatus());
														
														orderedItemsOfOldGuest.set(j, orderedItemOfSingleOldGuest);
														singleGuestOfOldOrder.setOrderedItems(orderedItemsOfOldGuest);
														guestsOfOldOrder.set(i,singleGuestOfOldOrder);
														orderOld.setGuests(guestsOfOldOrder);
														order = orderOld;
													}else {
														orderedItemsOfOldGuest.set(j, orderedItemOfSingleOldGuest);
														singleGuestOfOldOrder.setOrderedItems(orderedItemsOfOldGuest);
														guestsOfOldOrder.set(i,singleGuestOfOldOrder);
														orderOld.setGuests(guestsOfOldOrder);
														order = orderOld;
													}	
												}else {
													// quantities are not the same
													// replace order

													Order orderObj = quantityNotSame(orderedItemOfSingleNewGuest, 
															orderedItemOfSingleOldGuest, orderedItemsOfOldGuest, singleGuestOfOldOrder, 
															guestsOfOldOrder, orderOld, j, i);
													
													if(orderObj  != null) {
														orderOld = orderObj;
														order = orderOld;
													}
												}
											}else {
												// the new order does not have this item
												if(!orderedItemOfSingleOldGuest.isEditable()) {
													// if the item inside the old order is not editable
													// it means it has been removed from iiko
													// therefore remove the same item from the old order
													
													orderedItemsOfOldGuest.remove(orderedItemOfSingleOldGuest);
											        singleGuestOfOldOrder.setOrderedItems(orderedItemsOfOldGuest);
													guestsOfOldOrder.set(i,singleGuestOfOldOrder);
													orderOld.setGuests(guestsOfOldOrder);
													order = orderOld;
												}else {
													order = orderOld;
												}
											}
										}
										
										Order orderObj = addNewItemsToOrderFromIIKO(orderedItemsOfNewGuest, 
												orderedItemsOfOldGuest,singleGuestOfOldOrder, guestsOfOldOrder, i, orderOld);
										
										if(orderObj != null) {
											orderOld = orderObj;
											order = orderOld;
										}
									}
								}else if(orderedItemsOfNewGuest != null && orderedItemsOfOldGuest == null) {
									singleGuestOfOldOrder.setOrderedItems(orderedItemsOfNewGuest);
									guestsOfOldOrder.set(i,singleGuestOfOldOrder);
									orderOld.setGuests(guestsOfOldOrder);
									order = orderOld;
								}else if(orderedItemsOfNewGuest == null && orderedItemsOfOldGuest != null) {
									synchronized (orderedItemsOfOldGuest) {
										for(OrderedItem orderedItemObj : orderedItemsOfOldGuest) {
											if(orderedItemObj.getOrderedItemStatus() == 0 && 
													!orderedItemObj.isEditable()) {
												orderedItemsOfOldGuest.remove(orderedItemObj);
										        singleGuestOfOldOrder.setOrderedItems(orderedItemsOfOldGuest);
												guestsOfOldOrder.set(i,singleGuestOfOldOrder);
												orderOld.setGuests(guestsOfOldOrder);
												order = orderOld;
											}else {
												order = orderOld;
											}
										}
									}
									order = orderOld;
								}
							}else if(singleGuestOfNewOrder.getOrderedItems() !=null && 
									singleGuestOfOldOrder.getOrderedItems() == null) {
								
								singleGuestOfOldOrder.setOrderedItems(singleGuestOfNewOrder.getOrderedItems());
								guestsOfOldOrder.set(i,singleGuestOfOldOrder);
								orderOld.setGuests(guestsOfOldOrder);
								order = orderOld;
								order = setGuestNames(order);
								return order;
							}else if(singleGuestOfNewOrder.getOrderedItems() == null && 
									singleGuestOfOldOrder.getOrderedItems() != null) {
								try {
									synchronized (singleGuestOfOldOrder.getOrderedItems()) {
										for(OrderedItem orderedItemObj : singleGuestOfOldOrder.getOrderedItems()) {
											// the old order has an item that is supposed to be deleted
											if(!orderedItemObj.isEditable()) {
												singleGuestOfOldOrder.getOrderedItems().remove(orderedItemObj);
										        singleGuestOfOldOrder.setOrderedItems(singleGuestOfOldOrder.getOrderedItems());
												guestsOfOldOrder.set(i,singleGuestOfOldOrder);
												orderOld.setGuests(guestsOfOldOrder);
												order = orderOld;
											}else {
												order = orderOld;
												continue;
											}
										}
									}
								}catch(ConcurrentModificationException e) {
									e.printStackTrace();
								}
							}
						}else {
							for(int l=0;l<guestsOfOldOrder.size();l++) {
								if(singleGuestOfNewOrder.getGuestName().equalsIgnoreCase(guestsOfOldOrder.get(l).getGuestName())) {
									guestsOfOldOrder.get(l).setGuestId(singleGuestOfNewOrder.getGuestId());
									
									// the guest from iiko doesnot have any items but the guest in waiterpad has
									if(guestsOfOldOrder.get(l).getOrderedItems() != null && 
											singleGuestOfNewOrder.getOrderedItems() == null) {
										guestsOfOldOrder.get(l).setOrderedItems(guestsOfOldOrder.get(l).getOrderedItems());
									}else if(guestsOfOldOrder.get(l).getOrderedItems() == null && 
											singleGuestOfNewOrder.getOrderedItems() != null) {
										// the guest of iiko has ordered items but Waiterpads guest doesnot
										guestsOfOldOrder.get(l).setOrderedItems(singleGuestOfNewOrder.getOrderedItems());
									}else if(guestsOfOldOrder.get(l).getOrderedItems() != null && 
											singleGuestOfNewOrder.getOrderedItems() != null) {
										// both the guests have ordered items
										// merge the ordered items
										guestsOfOldOrder.get(l).getOrderedItems().addAll(singleGuestOfNewOrder.getOrderedItems());
									}
									
									orderOld.setGuests(guestsOfOldOrder);
									order = orderOld;
								}
							}
						}
					}
				}else if(orderNew.getGuests().size() < orderOld.getGuests().size()) {
					// guest has been reduced from IIKO front
					// hence get the guest not present in the new order
					// and remove the same guest from the old order

					Order orderObj = operationOnGuests(orderNew, orderOld);
					
					if(orderObj != null) {
						orderOld = orderObj;
						order = orderOld;
						order = setGuestNames(order);
					}
					
					return order;
				}
			}
		}
		
		
		order = setGuestNames(order);
		
		return order;
	}
	
	private Order operationOnGuests(Order orderNew, Order orderOld) {
		Order order = null;
		for(int i=0;i<orderOld.getGuests().size();i++) {
			Guest guestOld = orderOld.getGuests().get(i);
			
			if(guestOld != null) {
				if(guestOld.getGuestId() != null) {

					// the new guest
					Guest guest = Iterables.find(orderNew.getGuests(), 
							new SearchForGuest(guestOld.getGuestId()),null);
					
					// the new order does not have the guest
					if(guest == null) {
						if(guestOld.getOrderedItems() == null || (guestOld.getOrderedItems() != null && 
								guestOld.getOrderedItems().size() <= 0)) {
							orderOld.getGuests().remove(guestOld);
							orderOld.setGuests(orderOld.getGuests());
						}else {
							guestOld.setGuestId(null);
							orderOld.getGuests().set(i,guestOld);
							orderOld.setGuests(orderOld.getGuests());
						}
					}else {
						// the guest is not null
						// but it is removed from iiko
						if(guestOld.getOrderedItems() != null) {
							orderOld.getGuests().set(i,guest);
							orderOld.setGuests(orderOld.getGuests());
						}else {
							orderOld.getGuests().remove(guestOld);
							orderOld.setGuests(orderOld.getGuests());
						}
					}
				}else {
					if(guestOld.getOrderedItems() != null) {
						// the guest has items
						// do nothing then
					}else {
						orderOld.getGuests().remove(guestOld);
						order = orderOld;
					}
				}
			}
		}
		
		return order;
	}
	
	private Order addNewItemsToOrder(List<OrderedItem> orderedItemsOfNewGuest, 
			List<OrderedItem> orderedItemsOfOldGuest, Guest singleGuestOfOldOrder, 
			List<Guest> guestsOfOldOrder, Order orderOld, int i) {
		Order order = null;
		for(int k=0;k<orderedItemsOfNewGuest.size();k++) {
			OrderedItem orderedItem = orderedItemsOfNewGuest.get(k);
			
			if(orderedItem != null) {
				OrderedItem orderedItemObj = 
						Iterables.find(orderedItemsOfOldGuest, 
								new SearchForOrderedItemForQuantity(orderedItem.getId(), orderedItem.getOrderedItemId()), 
								null);
				
				// the item is not present in the old order
				if(orderedItemObj == null) {
					orderedItemsOfOldGuest.add(orderedItem);
					singleGuestOfOldOrder.setOrderedItems(orderedItemsOfOldGuest);
					guestsOfOldOrder.set(i,singleGuestOfOldOrder);
					orderOld.setGuests(guestsOfOldOrder);
					order = orderOld;
				}
			}
		}
		return order;
	}
	
	private Order addNewGuests(Order orderNew, Order orderOld) {
		for(int i=0;i<orderNew.getGuests().size();i++) {
			Guest guestNew = orderNew.getGuests().get(i);
			
			if(guestNew != null) {
				Guest guestOld = Iterables.find(orderOld.getGuests(), 
						new SearchForGuest(guestNew.getGuestId()),null);
				
				if(guestOld == null) {
					// the guest is not present in the old order
					// so add the guest to the order
					orderOld.getGuests().add(guestNew);
				}
			}
		}
		return orderOld;
	}
	
	private Order addNewItemsToOrderFromIIKO(List<OrderedItem> orderedItemsOfNewGuest,
			List<OrderedItem> orderedItemsOfOldGuest, Guest singleGuestOfOldOrder, List<Guest> guestsOfOldOrder, 
			int i, Order orderOld) {
		Order order = null;
		// to add new items to the order from IIKO
		// and not present in the current order
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
			
			if(orderedItemOfSingleOldGuest == null) {
				orderedItemsOfOldGuest.add(orderedItemOfSingleNewGuest);
				singleGuestOfOldOrder.setOrderedItems(orderedItemsOfOldGuest);
				guestsOfOldOrder.set(i,singleGuestOfOldOrder);
				orderOld.setGuests(guestsOfOldOrder);
				order = orderOld;
			}
		}
		
		return order;
	}
	
	private Order quantityNotSame(OrderedItem orderedItemOfSingleNewGuest, 
			OrderedItem orderedItemOfSingleOldGuest, List<OrderedItem> orderedItemsOfOldGuest, 
			Guest singleGuestOfOldOrder, List<Guest> guestsOfOldOrder, Order orderOld, int j, int i) {
		Order order = null;
		
		orderedItemOfSingleOldGuest.setQuantity(orderedItemOfSingleNewGuest.getQuantity());
		orderedItemsOfOldGuest.set(j, orderedItemOfSingleOldGuest);
		singleGuestOfOldOrder.setOrderedItems(orderedItemsOfOldGuest);
		guestsOfOldOrder.set(i,singleGuestOfOldOrder);
		orderOld.setGuests(guestsOfOldOrder);
		order = orderOld;
		
		return order;
	}
	
	/**
	 * Sets the name of the guests according to the count.
	 * @param order
	 * @return
	 */
	private Order setGuestNames(Order order){
		Order orderToReturn = order;
		if(orderToReturn != null && orderToReturn.getGuests() != null) {
			for(int i=0;i<orderToReturn.getGuests().size();i++) {
				Guest guest = orderToReturn.getGuests().get(i);
				guest.setGuestName(LanguageManager.getInstance().getGuest() + 
						" " + 
						(i+1));
				orderToReturn.getGuests().set(i, guest);
				orderToReturn.setGuests(orderToReturn.getGuests());
			}
		}
		return orderToReturn;
	}
	
	/**
	 * If an order is updated from IIKO and 
	 * an item or guest is also added from WaiterPad
	 * And if the order is Printed from iiko, then, the 
	 * item added from WP will be in the editable state
	 * Returns the state if the order has editable items or not
	 * @param order
	 * @return
	 */
	public boolean hasEditableItems(Order order) {
		boolean flag = false;
		if(order != null) {
			if(order.getGuests() != null) {
				for(int i=0;i<order.getGuests().size();i++) {
					Guest guest = order.getGuests().get(i);
					if(guest != null && guest.getOrderedItems() != null) {
						List<OrderedItem> orderedItems = guest.getOrderedItems();
						for(OrderedItem orderedItem : orderedItems) {
							if(orderedItem.isEditable()) {
								flag = true;
								break;
							}else {
								continue;
							}
						}
					}
				}
			}
		}
		return flag;
	}
	
	/**
	 * Gets the total count of items in the entire order
	 * Moved from OrderRelatedActivity
	 * @param orderToDisplay
	 * @return
	 */
	public int getCountOfItems(Order orderToDisplay) {
		int count = 0;
		if(orderToDisplay != null) {
			if(orderToDisplay.getGuests() != null) {
				for(Guest guest : orderToDisplay.getGuests()) {
					if(guest.getOrderedItems() != null) {
						count += guest.getOrderedItems().size();
					}
				}
			}
		}
		return count;
	}
	
	/**
	 * Method created on 8th Jan 2014, 5:30 PM
	 * Earlier only  one guest was checked with items,
	 * but because of an empty guest, continuous notifications are being received
	 * even if the order is sent from WaiterPad.
	 * 
	 * Thus, maintaing a hashmap for each guest.
	 * The map will be empty if there are no guests with no items.
	 * Else it will hold the guest and the count.
	 * @param orderToDisplay
	 * @return
	 */
	public HashMap<String, Integer> getCountOfItemsPerGuest(Order orderToDisplay) {
		int count = 0;
		HashMap<String, Integer> countOfItemsPerGuest = new HashMap<String, Integer>();
		if(orderToDisplay != null) {
			if(orderToDisplay.getGuests() != null) {
				for(Guest guest : orderToDisplay.getGuests()) {
					count = 0;
					if(guest.getOrderedItems() != null) {
						count = guest.getOrderedItems().size();
					}
					
					// add the guest to the hash map only if the count of items is 0
					if(count == 0) {
						countOfItemsPerGuest.put(guest.getGuestName(), count);
					}
				}
			}
		}
		return countOfItemsPerGuest;
	}
	
	/**
	 * Compare the ordered items
	 * For updation purposes (item)
	 * returns true if the ordered items are the same
	 * returns false if the ordered items are not the same
	 * @param orderToReplace
	 * @param orderToCompareWith
	 * @return
	 */
	public boolean compareOrderedItems(OrderedItem orderToReplace, OrderedItem orderToCompareWith) {
		boolean areEqual = true;

		// compares the id
		if(orderToReplace.getId().equals(orderToCompareWith.getId())) {
			// compares the ordered item status
			if(orderToReplace.getOrderedItemStatus() == orderToCompareWith.getOrderedItemStatus()) {
				// checks if both of the items are editable or not
				if(orderToReplace.isEditable() == orderToCompareWith.isEditable()) {
					// checks if the quantities are the same
					if(orderToReplace.getQuantity() == orderToCompareWith.getQuantity()) {
						// check if both the items do not have modifiers
						if(orderToReplace.getModifiers() == null && orderToCompareWith.getModifiers() == null) {
							areEqual = true;
						}else {
							// if the modifiers are present
							if(orderToReplace.getModifiers() != null && orderToCompareWith.getModifiers() != null) {
								// checks if the modifiers' size are the same
								if(orderToReplace.getModifiers().size() == orderToCompareWith.getModifiers().size()) {
									int counter = 0;
									for(ModifierMaster modifier : orderToReplace.getModifiers()) {
										// checks if the modifiers id are the same 
										// (that is the modifiers are the same)
										if(modifier.getId().equals(orderToCompareWith.getModifiers().get(counter).getId())) {
											areEqual = true;
											counter++;
										}else {
											areEqual = false;
											break;
										}
									}
								}else {
									areEqual = false;
								}
							}else {
								areEqual = false;
							}
						}
					}else {
						areEqual = false;
					}
				}else {
					areEqual = false;
				}
			}else {
				areEqual = false;
			}
		}else {
			areEqual = false;
		}
		return areEqual;
	}
	
	/**
	 * Method that returns whether all the guests in a bill split order
	 * have items or not
	 * Moved from OrderRelatedActivity
	 * @param guests
	 * @return
	 */
	public boolean getCountOfItemsForSplitOrders(List<Guest> guests) {
		boolean flag = false;
		if(guests != null) {
			for(Guest guest : guests ) {
				if(guest.getOrderedItems() != null) {
					flag = false;
				}else {
					flag = true;
					break;
				}
			}
		}
		return flag;
	}
	
	/**
	 * Created on 8th Jan 2014, 5:30 PM
	 * Orders created from iiko with quantities more than one 
	 * And having modifiers, have their modifier quantities set to the
	 * quantity of the item, thus when sending such an order
	 * IIKO fires an error that modifier quantity is high when expected is less
	 * 
	 * BUG IN IIKO, to overcome this, setting each modifiers quantity to 1
	 * @param orderToSend
	 * @return
	 */
	public Order setModifiersQtyToOne(Order orderToSend) {
		if(orderToSend != null) {
			if(orderToSend.getGuests() != null) {
				for(Guest guest : orderToSend.getGuests()) {
					if(guest.getOrderedItems() != null) {
						for(OrderedItem orderedItem : guest.getOrderedItems()) {
							if(orderedItem.getModifiers() != null) {
								for(int i=0;i<orderedItem.getModifiers().size();i++) {
									ModifierMaster modifierMaster = orderedItem.getModifiers().get(i);
									modifierMaster.setQuantity(1.0);
									orderedItem.getModifiers().set(i, modifierMaster);
								}
							}
						}
					}
				}
			}
		}
		return orderToSend;
	}
}
