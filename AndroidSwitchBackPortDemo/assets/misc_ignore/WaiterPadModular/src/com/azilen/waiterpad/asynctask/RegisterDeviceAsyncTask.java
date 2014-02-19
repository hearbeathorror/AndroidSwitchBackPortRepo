package com.azilen.waiterpad.asynctask;

import java.lang.reflect.Type;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.activities.LoginActivity;
import com.azilen.waiterpad.data.RegistrationStatus;
import com.azilen.waiterpad.data.WSRegisterDevice;
import com.azilen.waiterpad.data.WSWaiterPin;
import com.azilen.waiterpad.managers.language.LanguageManager;
import com.azilen.waiterpad.managers.network.NetworkManager;
import com.azilen.waiterpad.managers.network.RequestType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class RegisterDeviceAsyncTask extends AsyncTask<Void, Void, RegistrationStatus>{
	private Context mContext;
	private WSRegisterDevice mWsRegisterDevice;
	private WSWaiterPin mWsWaiterPin;
	private ProgressDialog progressDialog;

	private String TAG = this.getClass().getSimpleName();

	public RegisterDeviceAsyncTask(Context context,
			WSRegisterDevice wsRegisterDevice, WSWaiterPin wsWaiterPin) {
		mContext = context;
		mWsRegisterDevice = wsRegisterDevice;
		mWsWaiterPin = wsWaiterPin;
	}

	@Override
	protected RegistrationStatus doInBackground(Void... params) {
		String regResponse = NetworkManager.getInstance().
				performPostRequest(RequestType.REGISTER_DEVICE,mWsRegisterDevice);

		if(regResponse == null) {
			return null;
		}else {
			// process the results and convert json to an object
			// using gson
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(RegistrationStatus.OSType.class, new ObjectDeserializer());

			Gson gson = gsonBuilder.create();
			RegistrationStatus registrationStatus = gson.fromJson(regResponse, RegistrationStatus.class);
			return registrationStatus;
		}
	}

	@Override
	protected void onPostExecute(RegistrationStatus result) {
		super.onPostExecute(result);

		if(progressDialog!=null)
		{
			progressDialog.dismiss();
			((LoginActivity)mContext).setProgressDialog(null);
		}

		if(result != null) {
			Log.i(TAG, "result value : "  +result.getResponseErrorMessage());

			switch (result.getResponseCode()) {
			case 100:
				((LoginActivity)mContext).showMessageBox(result.getResponseErrorMessage());
				break;

			case 102:
				// device is already registered
				// redirect to the login page
				Log.i(TAG, "Error 102");
				new LoginAsyncTask(mContext, mWsWaiterPin).execute();
				break;

			case 101:
				Log.i(TAG, "Error 101");
				new LoginAsyncTask(mContext, mWsWaiterPin).execute();
				break;

			case 103:
				((LoginActivity)mContext).showMessageBox(mContext.getString(R.string.LICENCE_LIMIT_EXCEEDED));
				break;

			case 104:
				((LoginActivity)mContext).showMessageBox(result.getResponseErrorMessage());
				break;

			default:
				break;
			}
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		String message = LanguageManager.getInstance().getLoading();
		if(((LoginActivity)mContext).getProgressDialog()==null)
			progressDialog = ProgressDialog.show(mContext, null, message);
	}

	private static class ObjectDeserializer implements JsonDeserializer<RegistrationStatus.OSType> {
		@Override
		public RegistrationStatus.OSType deserialize(JsonElement json,
				Type typeOfT , JsonDeserializationContext ctx)
						throws JsonParseException
						{
			int typeInt = json.getAsInt();
			return RegistrationStatus.OSType.getOsType(typeInt);
		}
	}

}
