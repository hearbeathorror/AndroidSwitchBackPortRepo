package com.azilen.insuranceapp.asynctasks;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.azilen.insuranceapp.R;
import com.azilen.insuranceapp.managers.network.NetworkManager;
import com.azilen.insuranceapp.managers.network.NetworkManager.HttpRequestType;
import com.azilen.insuranceapp.utils.CommonUtility;
import com.azilen.insuranceapp.utils.Logger;
import com.azilen.insuranceapp.utils.Logger.modules;
import com.azilen.insuranceapp.videostreaming.DroidActivity;

import android.content.Context;
import android.os.AsyncTask;

public class CheckIfServerIsRunningAsyncTask extends AsyncTask<Void, Void, Boolean>{
	private Context mContext;
	private NetworkManager mNetworkManager;
	private String TAG = this.getClass().getSimpleName();
	
	public CheckIfServerIsRunningAsyncTask(Context context) {
		mContext =context;
		mNetworkManager = NetworkManager.getInstance();
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		HttpClient client = mNetworkManager.getHttpClient(HttpRequestType.GET);
		String serverUrl = mContext.getString(R.string.VIDEO_SERVER_URL);
		try {
			HttpGet getRequest = mNetworkManager.createGetRequest(serverUrl);

			HttpResponse getResponse = client.execute(getRequest);
			final int statusCode = getResponse.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Logger.w(modules.INSURANCE_APP, TAG, "Error " + statusCode
						+ " for URL " + serverUrl);
				return false;
			}
		} catch (IOException e) {
			Logger.w(modules.INSURANCE_APP, TAG, 
					"Error for URL " + serverUrl + "\n" + CommonUtility.getExceptionMSG(e));
			return false;
		}
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		
		if(mContext!= null) {
			((DroidActivity)mContext).recordVideo(result);
		}
		
	}

}
