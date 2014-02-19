package com.azilen.waiterpad.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.azilen.waiterpad.activities.LoginActivity;
import com.azilen.waiterpad.activities.MyOrderActivity;
import com.azilen.waiterpad.activities.NotificationListActivity;
import com.azilen.waiterpad.activities.OrderRelatedActivity;
import com.azilen.waiterpad.activities.SettingsActivity;
import com.azilen.waiterpad.activities.TableListActivity;
import com.azilen.waiterpad.activities.TableOrderListActivity;
import com.azilen.waiterpad.data.WSWaiterPin;
import com.azilen.waiterpad.data.WaiterPadResponse;
import com.azilen.waiterpad.managers.language.LanguageManager;
import com.azilen.waiterpad.managers.network.NetworkManager;
import com.azilen.waiterpad.managers.network.RequestType;
import com.azilen.waiterpad.managers.network.ServiceUrlManager;
import com.azilen.waiterpad.utils.Global;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LogoutAsyncTask extends AsyncTask<Void, Void, WaiterPadResponse>{
	private Context mContext;
	private String mFrom;
	private String mFromWhichButtonCall;
	private WSWaiterPin mWsWaiterPin;
	private ProgressDialog progressDialog;
	private WaiterPadResponse mWaiterPadResponse;
	private String TAG = this.getClass().getSimpleName();
	
	/**
	 * Constructor 
	 * @param context
	 * @param wsWaiterPin
	 * @param method
	 * @param from
	 */
	public LogoutAsyncTask(Context context, 
			WSWaiterPin wsWaiterPin, 
			String from) {
		mContext = context;
		mWsWaiterPin = wsWaiterPin;
		mFrom = from;
	}
	
	/**
	 * Constructor
	 * @param context
	 * @param wsWaiterPin
	 * @param method
	 * @param from
	 * @param fromWhichButtonCall
	 */
	public LogoutAsyncTask(Context context, 
			WSWaiterPin wsWaiterPin, 
			String from,
			String fromWhichButtonCall) {
		mContext = context;
		mWsWaiterPin = wsWaiterPin;
		mFrom = from;
		mFromWhichButtonCall = fromWhichButtonCall;
	}
	
	@Override
	protected WaiterPadResponse doInBackground(Void... params) {
		String serviceUrl = ServiceUrlManager.getInstance().getServiceUrlByType(RequestType.USER_LOGOUT) +
				ServiceUrlManager.SEPARATOR +
				mWsWaiterPin.getWaiterPin() +  
				ServiceUrlManager.SEPARATOR +
				mWsWaiterPin.getMacAddress(); 
				
		String logoutResponse = NetworkManager.getInstance().performGetRequest(serviceUrl);
		
		if(logoutResponse == null) {
			return null;
		}else {
			try {
				GsonBuilder gsonBuilder = new GsonBuilder();			
				Gson gson = gsonBuilder.create();
				
		        mWaiterPadResponse =  gson.fromJson(logoutResponse, WaiterPadResponse.class);
			}catch(Exception e) {
				e.printStackTrace();
			}
			return mWaiterPadResponse;
		}
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		String message = LanguageManager.getInstance().getLoading();

		if(mFrom != null && mFrom.trim().length() >0) {
			if(!mFrom.equalsIgnoreCase(Global.FROM_HOME_ACTIVITY) ) {
				progressDialog = ProgressDialog.show(mContext, null,message );
			}
		}else{
			progressDialog = ProgressDialog.show(mContext, null, message);
		}
	}
	
	@Override
	protected void onPostExecute(WaiterPadResponse result) {
		super.onPostExecute(result);
		if(result != null) {
			Log.i(TAG ,"response : " + result.getErrorMessage());
		}
		
		if(progressDialog != null) {
			progressDialog.dismiss();
		}
		Log.d("Home","Logout Async : mFrom : "+mFrom);
		if(mFrom != null) {
			if(mFrom.equalsIgnoreCase(Global.FROM_SETTINGS)) {
				Log.d("Home","Logout Async : From Setting if condition");
				((SettingsActivity)mContext).logoutMessage(result, mFromWhichButtonCall);
			}else if(mFrom.equalsIgnoreCase(Global.FROM_TABLE_LIST)){
				((TableListActivity)mContext).logoutMessage(result);
			}else if(mFrom.equalsIgnoreCase(Global.FROM_HOME_ACTIVITY)){
				((MyOrderActivity)mContext).logoutMessage(result);
			}else if(mFrom.equalsIgnoreCase(Global.FROM_NOTIFICATIONS)) {
				((NotificationListActivity)mContext).logoutMessage(result);
			}else if(mFrom.equalsIgnoreCase(Global.ORDER_RELATED)){
				((OrderRelatedActivity)mContext).logoutMessage(result);
			}else if(mFrom.equalsIgnoreCase(Global.FROM_TABLE_ORDER_LIST)){
				((TableOrderListActivity)mContext).logoutMessage(result);
			}else {
				((LoginActivity)mContext).logoutMessage(result);
			}
		}
	}
}
