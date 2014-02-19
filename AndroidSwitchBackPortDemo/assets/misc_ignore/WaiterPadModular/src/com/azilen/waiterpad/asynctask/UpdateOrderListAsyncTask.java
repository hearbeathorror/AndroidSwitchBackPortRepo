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
import com.azilen.waiterpad.data.ListOfOrders;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class UpdateOrderListAsyncTask extends AsyncTask<Void, Void, List<OrderResponse>>{
	private Context mContext;
	private Order mOrder;
	private ListOfOrders mLstOfOrders;
	private List<OrderResponse> mOrderResponses;
	private ProgressDialog mProgressDialog;
	
	private String TAG = this.getClass().getSimpleName();
	
	/**
	 * Constructor
	 * @param context
	 * @param lstOfOrders
	 * @param method
	 */
	public UpdateOrderListAsyncTask(Context context, 
			ListOfOrders lstOfOrders) {
		Global.isSendOrderCalled = true;
		mContext = context;
		mLstOfOrders = lstOfOrders;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		String loading = LanguageManager.getInstance().getLoading();
		
		mProgressDialog = 
				ProgressDialog.show(mContext, 
						null, 
						loading);
	}
	
	@Override
	protected List<OrderResponse> doInBackground(Void... params) {
		String updateOrdersResponse = NetworkManager.getInstance().
				performPostRequest(RequestType.ADD_NEW_ITEM_LIST, mLstOfOrders);
		
		if(updateOrdersResponse == null) {
			return null;
		}else {
			// process the results and convert json to an object
			// using gson
			GsonBuilder gsonBuilder = new GsonBuilder();
			
			// deserializers for the enum values
			gsonBuilder.registerTypeAdapter(Order.OrderStatus.class, new OrderManager.OrderStatusDeserializer());
			gsonBuilder.registerTypeAdapter(OrderedItem.OrderedItemStatus.class,new OrderManager.OrderedItemStatusDeserializer());
			gsonBuilder.registerTypeAdapter(Tables.TableType.class, new SectionManager.TableTypeDeserializer());
			
			Gson gson = gsonBuilder.create();
			Type collectionType = new TypeToken<List<OrderResponse>>(){}.getType();
			mOrderResponses = gson.fromJson(updateOrdersResponse, collectionType);
			return mOrderResponses;
		}
	}
	
	@Override
	protected void onPostExecute(List<OrderResponse> result) {
		super.onPostExecute(result);
		mProgressDialog.dismiss();
		
		Log.i(TAG, " end here.. response received");
		
		if(result != null) {
			HashMap<String, Order> splitOrders = new HashMap<String, Order>();
			if(result.size() == mLstOfOrders.getOrders().size()) {
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
						
					case 102:

						// decide what to do here
						Order orderFromResp = orderResponse.getOrder();
						
						// store each order into the cache
						// for notification purposes
						OrderResponse orderResponseToSendForCache = 
								new OrderResponse();
						orderResponseToSendForCache.setOrder(orderFromResp);
						
						OrderManager.getInstance().updateOrderAfterGettingResponse(orderResponseToSendForCache);
						
						Global.isSendOrderCalled = false;
						Log.i(TAG, "order is bill split - order number !!! " + orderFromResp.getOrderNumber());
						
						if(index == 0) {
							mOrder = orderResponseToSendForCache.getOrder();
						}
						
						Guest guest = orderFromResp.getGuests().get(0);
						
						// store the order id in the guest object
						if(index == 0) {
							// assign the first order's id and number
							mOrder.setOrderId(orderFromResp.getOrderId());
							mOrder.setOrderNumber(orderFromResp.getOrderNumber());
						}
						
						// changes as on 10th December 2013
						// adding the order id into cache and then it shall be removed
						OrderManager.getInstance().getOrderCache()
								.put(mContext.getString(R.string.order_from_waiterpad), 
										mOrder.getOrderId());
						// changes end here
						
						// set the order id and the guest id to each guest
						guest.setGuestId(orderFromResp.getGuests().get(0).getGuestId());
						guest.setOrderId(orderFromResp.getOrderId());
						guest.setOrderedItems(orderFromResp.getGuests().get(0).getOrderedItems());
						
						splitOrders.put(orderFromResp.getOrderId(), orderFromResp);
						
						if(index == 0) {
							mOrder.getGuests().remove(index);
							mOrder.getGuests().add(index, guest);
						}else {
							mOrder.getGuests().add(index, guest);
						}
						
						index++;
						
						OrderResponse orderResponseNew  =new OrderResponse();
						mOrder.setBillSplit(true);
						orderResponseNew.setOrder(mOrder);
						
						OrderManager.getInstance().getOrderCache().put(Global.SPLIT_ORDERS, splitOrders);
						
						// call refresh method to refresh the list
						if(mContext != null) {
							Global.isSendOrderCalled = false;
							((OrderRelatedActivity)mContext)
								.refreshList(mOrder, Global.ORDER_FROM_ASYNC_SEND,Global.ORDER_ACTION_UPDATED);
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
