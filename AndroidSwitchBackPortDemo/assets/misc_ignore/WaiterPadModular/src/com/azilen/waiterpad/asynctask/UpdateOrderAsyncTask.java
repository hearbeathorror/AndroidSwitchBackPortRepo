package com.azilen.waiterpad.asynctask;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.activities.OrderRelatedActivity;
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

public class UpdateOrderAsyncTask extends AsyncTask<Void, Void, OrderResponse>{
	private Context mContext;
	private Order mOrder;
	private OrderResponse mOrderResponse;
	private ProgressDialog mProgressDialog;
	public static boolean isRunning = false;
	public static String orderIdUpdated = "";
	
	private String TAG = this.getClass().getSimpleName();
	
	public UpdateOrderAsyncTask(Context context, 
			Order order) {
		Global.isSendOrderCalled = true;
		mContext = context;
		mOrder = order;
		isRunning = true;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		String loading = LanguageManager.getInstance().getLoading();
		
		mProgressDialog = ProgressDialog.show(mContext, 
							null, 
							loading);
	}
	
	@Override
	protected OrderResponse doInBackground(Void... params) {
		String orderUpdateResponse =NetworkManager.getInstance().
				performPostRequest(RequestType.ADD_NEW_ITEM_TO_ORDER, mOrder);
		
		if(orderUpdateResponse == null) {
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
		
			mOrderResponse = gson.fromJson(orderUpdateResponse, OrderResponse.class);
			
			if(mOrderResponse != null) {
				switch (mOrderResponse.getResponseCode()) {
				case 101:
					// new order placed successfully
					// refresh lrucache and also the listview
					
					// changes as on 10th December 2013
					// adding the order id into cache and then it shall be removed
					OrderManager.getInstance().getOrderCache()
							.put(mContext.getString(R.string.order_from_waiterpad), 
									mOrderResponse.getOrderId());
					// changes end here
					
					
					OrderManager.getInstance().refreshDataAfterGettingResponse(mOrderResponse);
					mOrder = mOrderResponse.getOrder();
					
					break;
					
				case 102:
					// display message here
					// new item placed successfully
					Log.i(TAG, "SUCCESS ADD ITEM -- " + mOrderResponse.getResponseErrorMessage());
					
					// changes as on 10th December 2013
					// adding the order id into cache and then it shall be removed
					OrderManager.getInstance().getOrderCache()
							.put(mContext.getString(R.string.order_from_waiterpad), 
									mOrderResponse.getOrder().getOrderId());
					
					Log.i(TAG, "ORDER ID -- " + mOrderResponse.getOrder().getOrderId());
					orderIdUpdated = mOrderResponse.getOrder().getOrderId();
					// changes end here 
					
					OrderManager.getInstance().refreshDataAfterGettingResponse(mOrderResponse);
					
					mOrder = mOrderResponse.getOrder();
				}
			}
			
			return mOrderResponse;
		}
	}
	
	@Override
	protected void onPostExecute(OrderResponse result) {
		super.onPostExecute(result);
		mProgressDialog.dismiss();
		
		if(result != null) {
			switch (result.getResponseCode()) {
			case 100:
				// display message here
				Log.i(TAG, "ERROR -- " + result.getResponseErrorMessage());
				if(mContext != null) {
					((OrderRelatedActivity)mContext).showMessage(result.getResponseErrorMessage());
				}
				Global.isSendOrderCalled = false;
				break;
			
			case 101:
				// new order placed successfully
				// refresh lrucache and also the listview
				//mOrder.setOrderId(result.getOrderId());
				//mOrder.setOrderNumber(result.getOrderNumber());
				
				// call refresh method to refresh the list
				if(mContext != null) {
					Global.isSendOrderCalled = false;
					((OrderRelatedActivity)mContext).refreshList(mOrder, Global.ORDER_FROM_ASYNC_SEND,Global.ORDER_ACTION_NEW_ORDER);
				}
				break;
				
			case 102:
				// display message here
				// new item placed successfully
				Log.i(TAG, "SUCCESS ADD ITEM -- " + result.getResponseErrorMessage());

				// call refresh method to refresh the list
				if(mContext != null) {
					((OrderRelatedActivity)mContext).refreshList(mOrder, Global.ORDER_FROM_ASYNC_SEND,Global.ORDER_ACTION_UPDATED);
				}
				break;

			default:
				break;
			}
		}
		
		isRunning = false;
		
		Log.i("UpdateOrderAsyncTask"," async over " );
	}

}
