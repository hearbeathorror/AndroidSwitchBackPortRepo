package com.azilen.waiterpad.managers.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.azilen.waiterpad.WaiterPadApplication;
import com.google.gson.Gson;

/*
 Network Manager class is used to perform POST and GET Request from Other Manager classes
 */
public class NetworkManager {
	/* Singleton Pattern using Eager Initialization */
	private static final NetworkManager instance = new NetworkManager();
	private int HTTP_POST_CONNECTION_TIMEOUT = 60000;
	private int HTTP_POST_WAITING_FOR_DATA_TIMEOUT = 60000;
	private int HTTP_GET_CONNECTION_TIMEOUT = 300000;
	private int HTTP_GET_WAITING_FOR_DATA_TIMEOUT = 300000;

	enum HttpRequestType {
		GET, POST
	};

	// private Context mContext;

	private NetworkManager() {
	}

	/* Singleton Pattern */
	public static NetworkManager getInstance() {
		return instance;
	}

	public void init() {
	}

	/* Check Internet connected or connecting or not */
	public boolean isNetworkConnected() {
		/* getting systems connectivity manager */
		try {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) WaiterPadApplication
					.getAppContext().getSystemService(
							Context.CONNECTIVITY_SERVICE);

			if (mConnectivityManager != null) {
				NetworkInfo[] mNetworkInfos = mConnectivityManager
						.getAllNetworkInfo();

				if (mNetworkInfos != null) {
					for (int i = 0; i < mNetworkInfos.length; i++) {
						if (mNetworkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
							return true;
						}
					}
				}
			}
		} catch (Exception ex) {

		}

		return false;
	}

	/* Perform Post Request */
	public String performPostRequest(RequestType requestType, Object dataObject) {

		String serviceUrl = createServiceMethodUrlByType(requestType);

		Log.d("Home", "Service Url " + serviceUrl);

		/* Convert Object to JSON String before Sending */
		String jsonString = getJsonStringFromObject(dataObject);
		
		try {
			/* Create Post Request & Execute */
			HttpPost postRequest = createPostRequestWithJSon(serviceUrl,
					jsonString);

			HttpClient client = getHttpClient(HttpRequestType.POST);

			HttpResponse postResponse = client.execute(postRequest);
			final int statusCode = postResponse.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w(getClass().getSimpleName(), "Error " + statusCode
						+ " for URL " + serviceUrl);
				return null;
			}

			String strResponse = getResponseString(postResponse);
			return strResponse;
		} catch (IOException e) {
			Log.w(getClass().getSimpleName(), "Error for URL " + serviceUrl, e);
			/*
			 * writeToLog(getClass().getSimpleName() + "\n" + "Error for URL " +
			 * serviceUrl + " \n " + e);
			 */
		}
		return null;
	}

	/* Get Request */
	public String performGetRequest(String serviceUrl) {

		HttpClient client = getHttpClient(HttpRequestType.GET);
		try {
			HttpGet getRequest = createGetRequest(serviceUrl);

			HttpResponse getResponse = client.execute(getRequest);
			final int statusCode = getResponse.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w(getClass().getSimpleName(), "Error " + statusCode
						+ " for URL " + serviceUrl);
				return null;
			}
			
			String strResponse = getResponseString(getResponse);
			return strResponse;
			
		} catch (IOException e) {
			Log.w(getClass().getSimpleName(), "Error for URL " + serviceUrl, e);
		}
		return null;
	}
	
	/* Get Request */
	public InputStream getInsputStreamGetRequest(String serviceUrl) {

		HttpClient client = getHttpClient(HttpRequestType.GET);
		try {
			HttpGet getRequest = createGetRequest(serviceUrl);

			HttpResponse getResponse = client.execute(getRequest);
			final int statusCode = getResponse.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w(getClass().getSimpleName(), "Error " + statusCode
						+ " for URL " + serviceUrl);
				return null;
			}
			
			InputStream is = getResponse.getEntity().getContent();
			return is;
			
		} catch (IOException e) {
			Log.w(getClass().getSimpleName(), "Error for URL " + serviceUrl, e);
		}
		return null;
	}

	/* Read Response String from Response Entity */
	private String getResponseString(HttpResponse response) throws IOException {
		InputStream instream = response.getEntity().getContent();

		BufferedReader r = new BufferedReader(new InputStreamReader(instream));
		StringBuilder total = new StringBuilder();
		String line;
		while ((line = r.readLine()) != null) {
			total.append(line);
		}

		return total.toString();
	}

	/* Construct Service Method URL BY method Name and Parameters */
	private String createServiceMethodUrlByType(RequestType requestType) {
		return ServiceUrlManager.getInstance().getServiceUrlByType(requestType);
	}

	/* Convert Java Object to JSON String */
	private String getJsonStringFromObject(Object dataObject) {
		/* Create GSON Object */
		Gson gsonObj = new Gson();

		/* Convert Java Object to JSON String */
		return gsonObj.toJson(dataObject);
	}

	/* create Post Request With Provided Json String */
	private HttpPost createPostRequestWithJSon(String url, String jsonString)
			throws UnsupportedEncodingException {
		HttpPost postRequest = new HttpPost(url);
		StringEntity stringEntity = new StringEntity(jsonString, "UTF-8");
		stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
				"application/json"));
		postRequest.setEntity(stringEntity);
		return postRequest;
	}

	/* create Post Request With Provided Json String */
	private HttpGet createGetRequest(String url)
			throws UnsupportedEncodingException {
		HttpGet gettRequest = new HttpGet(url);
		gettRequest.setHeader("Accept", "application/json");
		gettRequest
				.setHeader("Content-type", "application/json; charset=utf-8");

		return gettRequest;
	}

	/* Create HttpClient Instance with Application Settings Specified */
	private HttpClient getHttpClient(HttpRequestType requestType) {
		HttpParams httpParams = new BasicHttpParams();
		HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
		HttpProtocolParams.setHttpElementCharset(httpParams, HTTP.UTF_8);

		if (requestType == HttpRequestType.POST) {
			HttpConnectionParams.setConnectionTimeout(httpParams,
					HTTP_POST_CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams,
					HTTP_POST_WAITING_FOR_DATA_TIMEOUT);
		} else {
			HttpConnectionParams.setConnectionTimeout(httpParams,
					HTTP_GET_CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams,
					HTTP_GET_WAITING_FOR_DATA_TIMEOUT);
		}
		DefaultHttpClient httpclient = new DefaultHttpClient(httpParams);
		httpclient.getParams().setParameter("http.protocol.content-charset",
				HTTP.UTF_8);
		return httpclient;
	}
}