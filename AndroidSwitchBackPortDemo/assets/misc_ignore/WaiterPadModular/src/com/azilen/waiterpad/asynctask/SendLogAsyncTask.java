package com.azilen.waiterpad.asynctask;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.azilen.waiterpad.WaiterPadApplication;
import com.azilen.waiterpad.managers.language.LanguageManager;
import com.azilen.waiterpad.managers.network.RequestType;
import com.azilen.waiterpad.managers.network.ServiceUrlManager;

public class SendLogAsyncTask extends AsyncTask<Void, Void, Void>{
	private Context mContext;
	private InputStream mIs;
	private ProgressDialog mProgressDialog;
	private String TAG = this.getClass().getSimpleName();
	
	private static Logger logger = LoggerFactory.getLogger(SendLogAsyncTask.class);
	
	public SendLogAsyncTask(Context context, InputStream is) {
		mContext = context;
		mIs = is;
	}
	
	@Override
	protected void onPreExecute()  {
		super.onPreExecute();
		String message = LanguageManager.getInstance().getLoading();
		mProgressDialog = ProgressDialog.show(mContext,null, message);
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		String url = ServiceUrlManager.getInstance().getServiceUrlByType(RequestType.UPLOAD_LOG);
		
		InputStream inputStream = sendLogData(url, mIs);
		if(inputStream == null) {
			Log.i(TAG, " log not sent !");
			logger.debug(" log not sent !");
			return null;
		}else {
			Log.i(TAG, "log sent");
			logger.debug("log sent");
		}
		
		//mUtils.uploadFile(url);
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		mProgressDialog.dismiss();
	}
	
	public InputStream sendLogData(String url, InputStream is) {
		try {
			HttpPost request = new HttpPost(url);
			//request.setEntity(new StringEntity(body, HTTP.UTF_8));
			request.setHeader("Accept", "binary/octet-stream");
			request.setHeader("Content-type", "binary/octet-stream; charset=utf-8");
			//request.setHeader("charset", "utf-8");
			
			InputStreamEntity reqEntity = new InputStreamEntity(is, -1);
			reqEntity.setContentType("binary/octet-stream");
			reqEntity.setChunked(false); // Send in multiple parts if needed
			request.setEntity(reqEntity);

			HttpParams httpParameters = new BasicHttpParams();
			
			HttpProtocolParams.setContentCharset(httpParameters, HTTP.UTF_8);
			HttpProtocolParams.setHttpElementCharset(httpParameters, HTTP.UTF_8);
			
			HttpConnectionParams.setConnectionTimeout(httpParameters, 60000);
			HttpConnectionParams.setSoTimeout(httpParameters, 60000);

			DefaultHttpClient client = new DefaultHttpClient(httpParameters);
			client.getParams().setParameter("http.protocol.content-charset", HTTP.UTF_8);
			HttpResponse response = client.execute(request);

			final int statusCode = response.getStatusLine().getStatusCode();
			Log.i(TAG, "error 202" + response.getStatusLine());

			if (statusCode == HttpStatus.SC_ACCEPTED) {
				org.apache.http.HttpEntity getResponseEntity = response
						.getEntity();
				return getResponseEntity.getContent();
			} else if (statusCode != HttpStatus.SC_OK) {
				Log.w(getClass().getSimpleName(), "Error " + statusCode
						+ " for URL " + url);
				return null;
			}
		} catch (ClientProtocolException e) {
			WaiterPadApplication.LOG.debug(e.getMessage());
		} catch (IOException e) {
			WaiterPadApplication.LOG.debug(e.getMessage());
		}

		return null;
	}

}
