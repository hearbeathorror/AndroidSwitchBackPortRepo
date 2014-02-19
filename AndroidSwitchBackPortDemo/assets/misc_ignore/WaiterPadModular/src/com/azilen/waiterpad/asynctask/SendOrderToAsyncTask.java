package com.azilen.waiterpad.asynctask;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.activities.OrderRelatedActivity;
import com.azilen.waiterpad.data.Guest;
import com.azilen.waiterpad.data.Order;
import com.azilen.waiterpad.data.OrderResponse;
import com.azilen.waiterpad.data.OrderedItem;
import com.azilen.waiterpad.data.Tables;
import com.azilen.waiterpad.managers.language.LanguageManager;
import com.azilen.waiterpad.managers.network.NetworkManager;
import com.azilen.waiterpad.managers.network.RequestType;
import com.azilen.waiterpad.managers.order.OrderManager;
import com.azilen.waiterpad.managers.section.SectionManager;
import com.azilen.waiterpad.utils.Global;
import com.azilen.waiterpad.utils.Prefs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class SendOrderToAsyncTask extends AsyncTask<Void, Void, List<OrderResponse>>{
	private Context mContext;
	private Order mOrder;
	private RequestType mRequestType;
	private List<OrderResponse> mOrderResponse;
	private ProgressDialog mProgressDialog;
	public static String sendOrderOrderId = "";
	
	private String TAG = this.getClass().getSimpleName();
	
	public SendOrderToAsyncTask(Context context, Order order, 
			RequestType requestType) {
		// set to true when the send order button is clicked on
		Global.isSendOrderCalled = true;
		mOrder = order;
		mContext = context;
		mRequestType = requestType;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		String loading = LanguageManager.getInstance().getLoading();
		mProgressDialog = ProgressDialog.show(mContext, null,loading);
	}
	
	@Override
	protected List<OrderResponse> doInBackground(Void... params) {
		String orderResponse = "";
		
		if(mRequestType == RequestType.SEND_NEW_ORDER) {
			orderResponse = NetworkManager.getInstance().performPostRequest(RequestType.SEND_NEW_ORDER, mOrder);
		}else if(mRequestType == RequestType.EDIT_ORDER) {
			orderResponse = NetworkManager.getInstance().performPostRequest(RequestType.EDIT_ORDER, mOrder);
		}
		
		if(orderResponse == null) {
			return null;
		}else {
			// process the results and convert json to an object
			// using gson
			GsonBuilder gsonBuilder = new GsonBuilder();
			
			// deserializers for the enum values
			gsonBuilder.registerTypeAdapter(Order.OrderStatus.class, new OrderManager.OrderStatusDeserializer());
			gsonBuilder.registerTypeAdapter(OrderedItem.OrderedItemStatus.class,new OrderManager.OrderedItemStatusDeserializer());
			gsonBuilder.registerTypeAdapter(Tables.TableType.class, new SectionManager.TableTypeDeserializer());
			
			Type collectionType = new TypeToken<List<OrderResponse>>(){}.getType();
			
			Gson gson = gsonBuilder.create();
			
			mOrderResponse = gson.fromJson(orderResponse, collectionType);
			return mOrderResponse;
		}
	}
	
	@Override
	protected void onPostExecute(List<OrderResponse> result) {
		super.onPostExecute(result);
		mProgressDialog.dismiss();
		
		if(result != null) {
			if(mOrder.isBillSplit()) {
				HashMap<String, Order> splitOrders = new HashMap<String, Order>();
				if(result.size() == mOrder.getGuests().size()) {
					Log.i(TAG, "size of orderResponse : " + result.size());
					int index = 0;
					for(OrderResponse orderResponse : result) {
						switch (orderResponse.getResponseCode()) {
						case 100:
							
							if(mContext != null) {
								((OrderRelatedActivity)mContext)
									.showMessage(orderResponse.getResponseErrorMessage());
							}
							Global.isSendOrderCalled = false;
							break;
							
						case 101:

							// decide what to do here
							Order orderFromResp = orderResponse.getOrder();
							
							// store each order into the cache
							// for notification purposes
							OrderResponse orderResponseToSendForCache = 
									new OrderResponse();
							orderResponseToSendForCache.setOrder(orderFromResp);
							
							// changes as on 10th December 2013
							// adding the order id into cache and then it shall be removed
							OrderManager.getInstance().getOrderCache()
									.put(mContext.getString(R.string.order_from_waiterpad), 
											orderFromResp.getOrderId());
							// changes end here
							
							OrderManager.getInstance().refreshDataAfterGettingResponse(orderResponseToSendForCache);
							
							
							Global.isSendOrderCalled = false;
							
							Log.i(TAG, "order is bill split - order number !!! " + orderFromResp.getOrderNumber());
							Guest guest = mOrder.getGuests().get(index);
							
							// store the order id in the guest object
							if(index == 0) {
								// assign the first order's id and number
								mOrder.setOrderId(orderFromResp.getOrderId());
								sendOrderOrderId = orderFromResp.getOrderId();
								
								Prefs.addKey(Prefs.ORDER_ID, orderFromResp.getOrderId());
								
								mOrder.setOrderNumber(orderFromResp.getOrderNumber());
								mOrder.setModified(false);
							}
							
							// set the order id and the guest id to each guest
							guest.setGuestId(orderFromResp.getGuests().get(0).getGuestId());
							guest.setOrderId(orderFromResp.getOrderId());
							guest.setOrderedItems(orderFromResp.getGuests().get(0).getOrderedItems());
							
							splitOrders.put(orderFromResp.getOrderId(), orderFromResp);
							mOrder.getGuests().remove(index);
							mOrder.getGuests().add(index, guest);
							
							Log.e("dhara", "order status of bill split items: " + index + " " + guest.getOrderedItems().get(0).getOrderedItemStatus());
							
							index++;
							
							OrderResponse orderResponseNew  =new OrderResponse();
							mOrder.setBillSplit(true);
							orderResponseNew.setOrder(mOrder);
							
							OrderManager.getInstance().getOrderCache().put(Global.SPLIT_ORDERS, splitOrders);
							
							// call refresh method to refresh the list
							if(mContext != null) {
								((OrderRelatedActivity)mContext)
									.refreshList(mOrder, Global.ORDER_FROM_ASYNC_SEND,Global.ORDER_ACTION_NEW_ORDER);
							}
						
							break;

						default:
							break;
						}
					}
				}
			}else {
				if(result.get(0) != null) {
					OrderResponse orderResponse = result.get(0);
					switch (orderResponse.getResponseCode()) {
					case 100:
						// display message here
						Log.i(TAG, "ERROR -- " + orderResponse.getResponseErrorMessage());
						if(mContext != null) {
							((OrderRelatedActivity)mContext)
								.showMessage(orderResponse.getResponseErrorMessage());
						}
						Global.isSendOrderCalled = false;
						break;
					
					case 101:
						// new order placed successfully
						// refresh lrucache and also the listview
						//mOrder.setOrderId(result.getOrderId());
						//mOrder.setOrderNumber(result.getOrderNumber());
						
						// changes as on 10th December 2013
						// adding the order id into cache and then it shall be removed
						OrderManager.getInstance().getOrderCache()
								.put(mContext.getString(R.string.order_from_waiterpad), 
										orderResponse.getOrderId());
						// changes end here
						
						sendOrderOrderId = orderResponse.getOrderId();
						OrderManager.getInstance().refreshDataAfterGettingResponse(orderResponse);
						mOrder = orderResponse.getOrder();
						
						Prefs.addKey(Prefs.ORDER_ID, mOrder.getOrderId());
						
						Global.isSendOrderCalled = false;
						
						// call refresh method to refresh the list
						if(mContext != null) {
							((OrderRelatedActivity)mContext)
								.refreshList(mOrder, Global.ORDER_FROM_ASYNC_SEND,Global.ORDER_ACTION_NEW_ORDER);
						}
						break;
						
					case 102:
						// display message here
						// new item placed successfully
						Log.i(TAG, "ERROR -- " + orderResponse.getResponseErrorMessage());
						
						// changes as on 10th December 2013
						// adding the order id into cache and then it shall be removed
						OrderManager.getInstance().getOrderCache()
								.put(mContext.getString(R.string.order_from_waiterpad), 
										orderResponse.getOrderId());
						// changes end here
						
						OrderManager.getInstance().refreshDataAfterGettingResponse(orderResponse);
						
						mOrder = orderResponse.getOrder();
						sendOrderOrderId = orderResponse.getOrderId();
						Global.isSendOrderCalled = false;
						// call refresh method to refresh the list
						if(mContext != null) {
							((OrderRelatedActivity)mContext).refreshList(mOrder, Global.ORDER_FROM_ASYNC_SEND,Global.ORDER_ACTION_UPDATED);
						}
						break;

					default:
						break;
					}
				}
			}
		}else {
			Global.isSendOrderCalled = false;
			if(mContext != null) {
				((OrderRelatedActivity)mContext)
					.showMessage(LanguageManager.getInstance().getServerUnreachable());
			}
		}
	}
}
