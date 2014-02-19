package com.azilen.waiterpad.asynctask;

import java.lang.reflect.Type;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.azilen.waiterpad.activities.OrderRelatedActivity;
import com.azilen.waiterpad.data.CheckoutData;
import com.azilen.waiterpad.data.CheckoutDataList;
import com.azilen.waiterpad.data.CheckoutResponse;
import com.azilen.waiterpad.data.Guest;
import com.azilen.waiterpad.data.Order;
import com.azilen.waiterpad.data.Order.OrderStatus;
import com.azilen.waiterpad.data.OrderedItem;
import com.azilen.waiterpad.managers.language.LanguageManager;
import com.azilen.waiterpad.managers.network.NetworkManager;
import com.azilen.waiterpad.managers.network.RequestType;
import com.azilen.waiterpad.managers.order.OrderManager;
import com.azilen.waiterpad.utils.Global;
import com.azilen.waiterpad.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Sends the order data for check out purposes
 * @author dharashah
 *
 */
public class CheckoutListOrderAsyncTask extends AsyncTask<Void, Void, List<CheckoutResponse>>{
	private Context mContext;
	private RequestType mRequestType;
	private CheckoutData mCheckoutData;
	private CheckoutDataList mCheckoutDataList;
	private Utils mUtils;
	private List<CheckoutResponse> mCheckoutResponses;
	private String mLoading;
	private ProgressDialog mProgressDialog;
	private Order mOrder;
	
	private String TAG = this.getClass().getSimpleName();
		
	/**
	 * Constructor for a list of orders
	 * @param context
	 * @param checkoutDataList
	 * @param method
	 * @param order
	 */
	public CheckoutListOrderAsyncTask(Context context, 
			CheckoutDataList checkoutDataList, 
			RequestType requestType,
			Order order) { 
		Global.isSendOrderCalled = true;
		mOrder = order;
		mContext = context;
		mRequestType = requestType;
		mCheckoutDataList = checkoutDataList;
		mUtils = new Utils(context);
	}
	
	@Override
	protected List<CheckoutResponse> doInBackground(Void... params) {
		String checkoutResponse = "";
		if(mRequestType == RequestType.CHECKOUT_ORDER) {
			checkoutResponse = NetworkManager.getInstance().performPostRequest(mRequestType, mCheckoutData);
		}else if(mRequestType == RequestType.CHECKOUT_BILL_SPLIT_ORDER){
			checkoutResponse = NetworkManager.getInstance().performPostRequest(mRequestType, mCheckoutDataList);
		}
		
		if(checkoutResponse == null) {
			return null;
		}else {
			// process the results and convert json to an object
			// using gson
			GsonBuilder gsonBuilder = new GsonBuilder();
			Gson gson = gsonBuilder.create();
			
			Type collectionType = new TypeToken<List<CheckoutResponse>>(){}.getType();
			
			mCheckoutResponses = gson.fromJson(checkoutResponse, collectionType);
			return mCheckoutResponses;
		}
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mLoading = LanguageManager.getInstance().getLoading();
		mProgressDialog = ProgressDialog.show(mContext,null, mLoading);
	}
	
	@Override
	protected void onPostExecute(List<CheckoutResponse> result) {
		super.onPostExecute(result);
		
		// store in the cache of bill request data
		// and remove from the list of running orders
		if(result != null) {
			for(CheckoutResponse checkoutResponse : result) {
				switch (checkoutResponse.getResponseCode()) {
				case 100:
					// there is an error
					// notify the activity
					if(mContext != null) {
						((OrderRelatedActivity)mContext).showMessage(checkoutResponse.getResponseErrorMessage());
					}
					
					Global.isSendOrderCalled = false;
					break;

				case 105:
					// it is successful
					OrderManager.getInstance().addToBillRequest(checkoutResponse.getOrderId());
					mOrder.setOrderStatus(OrderStatus.BILL);
					refreshItemStatus();
					
					// changes as on 7th December 2013
					// this will add the id of the order checked out into the cache
					OrderManager.getInstance().storeCheckoutOrders(checkoutResponse.getOrderId());
					// changes end here
					
					if(mContext != null) {
						((OrderRelatedActivity)mContext).refreshList(mOrder, Global.ORDER_FROM_ASYNC_SEND, Global.ORDER_ACTION_CHECKOUT);
					}
				default:
					break;
				}
			}
		}else {
			if(mContext != null) {
				((OrderRelatedActivity)mContext)
					.showMessage(LanguageManager.getInstance().getServerUnreachable());
			}
			Global.isSendOrderCalled = false;
		}
		
		mProgressDialog.dismiss();
	}
	
	/**
	 * Set item editable to false
	 */
	private void refreshItemStatus() {
		List<Guest> guests = mOrder.getGuests();
		
		if(guests != null) {
			for(Guest guest : guests) {
				List<OrderedItem> orderedItems = guest.getOrderedItems();
				
				if(orderedItems != null) {
					for(OrderedItem orderedItem : orderedItems) {
						orderedItem.setEditable(false);
					}
				}
			}
		}
	}
}
