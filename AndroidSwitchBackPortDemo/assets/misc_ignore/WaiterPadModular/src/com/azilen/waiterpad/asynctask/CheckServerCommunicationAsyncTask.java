package com.azilen.waiterpad.asynctask;

import java.lang.reflect.Type;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.activities.ConfigureServiceActivity;
import com.azilen.waiterpad.data.RegistrationStatus;
import com.azilen.waiterpad.data.ServiceConfig;
import com.azilen.waiterpad.data.WSRegisterDevice;
import com.azilen.waiterpad.managers.language.LanguageManager;
import com.azilen.waiterpad.managers.menu.MenuManager;
import com.azilen.waiterpad.managers.network.NetworkManager;
import com.azilen.waiterpad.managers.network.RequestType;
import com.azilen.waiterpad.managers.network.ServiceUrlManager;
import com.azilen.waiterpad.managers.order.OrderManager;
import com.azilen.waiterpad.managers.section.SectionManager;
import com.azilen.waiterpad.utils.Prefs;
import com.azilen.waiterpad.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * Verifies if the server can be connected to and also verifies if the device is
 * registered or not
 * 
 * @author dhara.shah
 * 
 */
public class CheckServerCommunicationAsyncTask extends
		AsyncTask<Void, Void, RegistrationStatus> {
	private Context mContext;
	private ServiceConfig mServiceConfig;
	private ProgressDialog progressDialog;
	private WSRegisterDevice mWsRegisterDevice;
	private String mLoading;
	private Utils mUtils;

	private String TAG = this.getClass().getSimpleName();

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param serviceConfig
	 */
	public CheckServerCommunicationAsyncTask(Context context,
			ServiceConfig serviceConfig, WSRegisterDevice wsRegisterDevice) {
		mContext = context;
		mServiceConfig = serviceConfig;
		mWsRegisterDevice = wsRegisterDevice;
		mUtils = new Utils(mContext);
	}

	@Override
	protected RegistrationStatus doInBackground(Void... params) {

		Log.d("Home", "Inside Task");

		ServiceUrlManager sum = ServiceUrlManager.getInstance();

		if (sum == null)
			Log.d("Home", "Service URL NULL");

		sum.setBaseUrl(urlString());

		String response = NetworkManager.getInstance().performPostRequest(
				RequestType.REGISTER_DEVICE, mWsRegisterDevice);

		if (response == null) {
			return null;
		} else {
			// process the results and convert json to an object
			// using gson
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(RegistrationStatus.OSType.class,
					new ObjectDeserializer());

			Gson gson = gsonBuilder.create();

			RegistrationStatus registrationStatus = gson.fromJson(response,
					RegistrationStatus.class);
			return registrationStatus;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(RegistrationStatus result) {
		super.onPostExecute(result);
		
		if(result != null) {
			// remove all details related to the other IP configuration
			Prefs.removeKey(mContext,Prefs.SECTION_ID);
			Prefs.removeKey(mContext, Prefs.SECTION_NAME);
			MenuManager.getInstance().deleteFromAllTables();
			OrderManager.getInstance().clearAllOrders();
			SectionManager.getInstance().clearSectionDetails();
			mUtils.clearCache();
		}
		
		progressDialog.dismiss();
		
		((ConfigureServiceActivity) mContext).processNext(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		mLoading = LanguageManager.getInstance().getLoading();
		progressDialog = ProgressDialog.show(mContext, null, mLoading);
	}

	/**
	 * Creates the url string using the newly entered configurations
	 * 
	 * @return
	 */
	private String urlString() {
		String url = mContext.getString(R.string.http)
				+ mServiceConfig.getIpAddress()
				+ mContext.getString(R.string.colon)
				+ mServiceConfig.getPortAddress()
				+ mContext.getString(R.string.delimiter)
				+ mContext.getString(R.string.WEBSERVICE_NAME)
		/* + mContext.getString(R.string.delimiter) + mMethod */;

		return url;
	}

	private static class ObjectDeserializer implements
			JsonDeserializer<RegistrationStatus.OSType> {
		@Override
		public RegistrationStatus.OSType deserialize(JsonElement json,
				Type typeOfT, JsonDeserializationContext ctx)
				throws JsonParseException {
			int typeInt = json.getAsInt();
			return RegistrationStatus.OSType.getOsType(typeInt);
		}
	}
}
