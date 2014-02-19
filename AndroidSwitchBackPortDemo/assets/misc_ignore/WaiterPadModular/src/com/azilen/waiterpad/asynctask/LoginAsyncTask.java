package com.azilen.waiterpad.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.azilen.waiterpad.activities.LoginActivity;
import com.azilen.waiterpad.data.WSWaiterPin;
import com.azilen.waiterpad.data.WaiterPadResponse;
import com.azilen.waiterpad.managers.language.LanguageManager;
import com.azilen.waiterpad.managers.network.NetworkManager;
import com.azilen.waiterpad.managers.network.RequestType;
import com.azilen.waiterpad.managers.network.ServiceUrlManager;
import com.azilen.waiterpad.utils.Prefs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LoginAsyncTask extends AsyncTask<Void, Void, WaiterPadResponse>{
	private Context mContext;
	private WSWaiterPin mWsWaiterPin;
	private ProgressDialog progressDialog;
	private WaiterPadResponse mWaiterPadResponse;
	private String TAG = this.getClass().getSimpleName();

	/**
	 * Constructor
	 * @param context
	 * @param wsWaiterPin
	 */
	public LoginAsyncTask(Context context, WSWaiterPin wsWaiterPin) {
		mContext = context;
		mWsWaiterPin = wsWaiterPin;
	}

	@Override
	protected WaiterPadResponse doInBackground(Void... params) {
		String serviceUrl  = ServiceUrlManager.getInstance().getServiceUrlByType(RequestType.USER_LOGIN)
				+ ServiceUrlManager.SEPARATOR
				+ mWsWaiterPin.getWaiterPin() 
				+ ServiceUrlManager.SEPARATOR+
				mWsWaiterPin.getMacAddress();
		
		Log.i("dhara" , " " + serviceUrl);

		String loginResponse = NetworkManager.getInstance().performGetRequest(serviceUrl);
		
		if(loginResponse == null) {
			return null;
		}else {
			try {
				GsonBuilder gsonBuilder = new GsonBuilder();			
				Gson gson = gsonBuilder.create();
				mWaiterPadResponse =  gson.fromJson(loginResponse, WaiterPadResponse.class);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}

		return mWaiterPadResponse;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		LoginActivity activity=((LoginActivity)mContext);

		String message = LanguageManager.getInstance().getLoading();

		if(activity.getProgressDialog()==null)
		{
			progressDialog = ProgressDialog.show(mContext, null, message);
			((LoginActivity)mContext).setProgressDialog(progressDialog);
		}
	}

	@Override
	protected void onPostExecute(WaiterPadResponse result) {
		super.onPostExecute(result);
		if(result != null) {
			Log.i(TAG ,"response : " + result.getErrorMessage());

			if(!result.isError()) {
				Prefs.addKey(mContext, Prefs.USER_PIN, mWsWaiterPin.getWaiterPin());
			}
		}

		((LoginActivity)mContext).refreshResponse(result);
	}
}
