package com.azilen.waiterpad.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.azilen.waiterpad.activities.MyOrderActivity;
import com.azilen.waiterpad.activities.OrderRelatedActivity;
import com.azilen.waiterpad.activities.TableListActivity;
import com.azilen.waiterpad.activities.TableOrderListActivity;
import com.azilen.waiterpad.data.Order;
import com.azilen.waiterpad.data.OrderList;
import com.azilen.waiterpad.data.OrderedItem;
import com.azilen.waiterpad.data.Tables;
import com.azilen.waiterpad.managers.language.LanguageManager;
import com.azilen.waiterpad.managers.network.NetworkManager;
import com.azilen.waiterpad.managers.network.RequestType;
import com.azilen.waiterpad.managers.network.ServiceUrlManager;
import com.azilen.waiterpad.managers.order.OrderManager;
import com.azilen.waiterpad.managers.section.SectionManager;
import com.azilen.waiterpad.service.GetAllOrdersService;
import com.azilen.waiterpad.utils.Global;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GetCurrentOrderListAsyncTask extends
		AsyncTask<Void, Void, OrderList> {
	private Context mContext;
	private OrderList orderList;
	private String mFrom;
	private ProgressDialog mProgressDialog;

	public static boolean isAsyncStarted = false;

	public GetCurrentOrderListAsyncTask(Context context, String from) {
		mContext = context;
		mFrom = from;
	}

	@Override
	protected OrderList doInBackground(Void... params) {

		String url = ServiceUrlManager.getInstance().getServiceUrlByType(
				RequestType.GET_CURRENT_ORDERS);

		String currentOrdersResponse = NetworkManager.getInstance()
				.performGetRequest(url);

		Global.logi("url called!!! " + url);

		if (currentOrdersResponse == null) {
			return null;
		} else {
			try {
				GsonBuilder gsonBuilder = new GsonBuilder();

				// deserializers for the enum values
				gsonBuilder.registerTypeAdapter(Order.OrderStatus.class,
						new OrderManager.OrderStatusDeserializer());
				gsonBuilder.registerTypeAdapter(
						OrderedItem.OrderedItemStatus.class,
						new OrderManager.OrderedItemStatusDeserializer());
				gsonBuilder.registerTypeAdapter(Tables.TableType.class,
						new SectionManager.TableTypeDeserializer());

				Gson gson = gsonBuilder.create();
				orderList = gson.fromJson(currentOrdersResponse,
						OrderList.class);

				Global.logi("value : " + orderList.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return orderList;
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		isAsyncStarted = true;
		String loading = "";

		if (!mFrom.equalsIgnoreCase(Global.GET_ALL_ORDERS_SERVICE_PARAM)) {
			loading = LanguageManager.getInstance().getLoading();
			mProgressDialog = ProgressDialog.show(mContext, null, loading);
		}
	}

	@Override
	protected void onPostExecute(OrderList result) {
		super.onPostExecute(result);

		if (mFrom != null && mFrom.equalsIgnoreCase(Global.MY_ORDERS)) {
			mProgressDialog.dismiss();
			((MyOrderActivity) mContext).refreshOrders(result);
		} else if (mFrom != null && mFrom.equalsIgnoreCase(Global.TABLE_LIST)) {
			mProgressDialog.dismiss();
			((TableListActivity) mContext).refreshTables(result);
		} else if (mFrom != null
				&& mFrom.equalsIgnoreCase(Global.ORDER_RELATED)) {
			// replace the orders with the list obtained
			// and refresh the order already present or
			// opened in the order screen
			// changes as on 11th November 2013
			OrderManager.getInstance().storeCurrentOrdersInMemory(result);
			mProgressDialog.dismiss();
			((OrderRelatedActivity) mContext).refreshOrders(result);
		} else if (mFrom != null
				&& mFrom.equalsIgnoreCase(Global.FROM_TABLE_ORDER_LIST)) {
			OrderManager.getInstance().storeCurrentOrdersInMemory(result);
			mProgressDialog.dismiss();
			((TableOrderListActivity) mContext).refreshOrders();
		} else if (mFrom != null
				&& mFrom.equalsIgnoreCase(Global.GET_ALL_ORDERS_SERVICE_PARAM)) {
			if(!UpdateOrderAsyncTask.isRunning) {
				((GetAllOrdersService) mContext).refreshOrders(result);
			}
		}

		isAsyncStarted = false;
	}
}
