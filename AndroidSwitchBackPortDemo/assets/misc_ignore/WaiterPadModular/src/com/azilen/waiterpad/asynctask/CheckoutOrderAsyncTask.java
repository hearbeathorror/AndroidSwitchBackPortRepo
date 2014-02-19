package com.azilen.waiterpad.asynctask;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;

import com.azilen.waiterpad.activities.OrderRelatedActivity;
import com.azilen.waiterpad.activities.TableOrderListActivity;
import com.azilen.waiterpad.data.CheckoutData;
import com.azilen.waiterpad.data.CheckoutDataList;
import com.azilen.waiterpad.data.CheckoutResponse;
import com.azilen.waiterpad.data.Order;
import com.azilen.waiterpad.managers.language.LanguageManager;
import com.azilen.waiterpad.managers.network.NetworkManager;
import com.azilen.waiterpad.managers.network.RequestType;
import com.azilen.waiterpad.managers.order.OrderManager;
import com.azilen.waiterpad.utils.Global;
import com.azilen.waiterpad.utils.search.SearchForOrder;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Sends the order data for check out purposes
 * 
 * @author dharashah
 * 
 */
public class CheckoutOrderAsyncTask extends
		AsyncTask<Void, Void, CheckoutResponse> {
	private Context mContext;
	private RequestType mRequestType;
	private String mFrom;
	private CheckoutData mCheckoutData;
	private CheckoutDataList mCheckoutDataList;
	private LruCache<String, Object> memCache;
	private CheckoutResponse mCheckoutResponse;
	private ProgressDialog mProgressDialog;
	private String mLoading;

	private String TAG = this.getClass().getSimpleName();

	/**
	 * Constructor for a single order
	 * 
	 * @param context
	 * @param checkoutData
	 * @param method
	 */
	public CheckoutOrderAsyncTask(Context context, CheckoutData checkoutData,
			RequestType requestType, String from) {
		Global.isSendOrderCalled = true;
		mContext = context;
		mRequestType = requestType;
		mCheckoutData = checkoutData;
		mFrom = from;
		memCache = OrderManager.getInstance().getOrderCache();
	}

	@Override
	protected CheckoutResponse doInBackground(Void... params) {
		String checkoutResponse = "";
		if (mRequestType == RequestType.CHECKOUT_ORDER) {
			checkoutResponse = NetworkManager.getInstance().performPostRequest(
					mRequestType, mCheckoutData);
		} else if (mRequestType == RequestType.CHECKOUT_BILL_SPLIT_ORDER) {
			checkoutResponse = NetworkManager.getInstance().performPostRequest(
					mRequestType, mCheckoutDataList);
		}

		if (checkoutResponse == null) {
			return null;
		} else {
			// process the results and convert json to an object
			// using gson
			GsonBuilder gsonBuilder = new GsonBuilder();
			Gson gson = gsonBuilder.create();

			mCheckoutResponse = gson.fromJson(checkoutResponse,
					CheckoutResponse.class);
			return mCheckoutResponse;
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		mLoading = LanguageManager.getInstance().getLoading();
		mProgressDialog = ProgressDialog.show(mContext, null, mLoading);
	}

	@Override
	protected void onPostExecute(CheckoutResponse result) {
		super.onPostExecute(result);
		mProgressDialog.dismiss();

		// store in the cache of bill request data
		// and remove from the list of running orders
		if (result != null) {
			switch (result.getResponseCode()) {
			case 100:
				// there is an error
				// notify the activity
				if (mFrom.equalsIgnoreCase(Global.ORDER_RELATED)) {
					((OrderRelatedActivity) mContext).showMessage(result
							.getResponseErrorMessage());
				} else if (mFrom.equalsIgnoreCase(Global.FROM_TABLE_ORDER_LIST)) {
					((TableOrderListActivity) mContext).showMessage(result
							.getResponseErrorMessage());
				}
				Global.isSendOrderCalled = false;
				break;

			case 105:
				// it is successful
				OrderManager.getInstance()
						.addToBillRequest(result.getOrderId());

				// changes as on 7th December 2013
				// this will add the id of the order checked out into the cache
				OrderManager.getInstance().storeCheckoutOrders(result.getOrderId());
				// changes end here
				
				if (mFrom.equalsIgnoreCase(Global.ORDER_RELATED)) {
					@SuppressWarnings("unchecked")
					List<Order> orders = (List<Order>) memCache
							.get(Global.BILL_REQUEST);

					if (orders != null && result.getOrderId() != null) {
						Order order = Iterables.find(orders,
								new SearchForOrder(result.getOrderId()), null);

						if (order != null) {
							((OrderRelatedActivity) mContext).refreshList(
									order, Global.ORDER_FROM_ASYNC_SEND,
									Global.ORDER_ACTION_CHECKOUT);
						}
					}
				} else if (mFrom.equalsIgnoreCase(Global.FROM_TABLE_ORDER_LIST)) {
					Global.isSendOrderCalled = false;
					((TableOrderListActivity) mContext).refresh(result
							.getOrderId());
				}
			default:
				break;
			}
		} else {
			Global.isSendOrderCalled = false;
			if (mFrom.equalsIgnoreCase(Global.ORDER_RELATED)) {
				if (mContext != null) {
					((OrderRelatedActivity) mContext)
							.showMessage(LanguageManager.getInstance()
									.getServerUnreachable());
				}
			} else if (mFrom.equalsIgnoreCase(Global.FROM_TABLE_ORDER_LIST)) {
				if (mContext != null) {
					((TableOrderListActivity) mContext)
							.showMessage(LanguageManager.getInstance()
									.getServerUnreachable());
				}
			}
		}
	}
}
