package com.azilen.waiterpad.managers.network;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

/* ServiceUrlManager maintains Mapping of ServiceFunction vs Function's Url*/
/* When application Starts it Requests Mapping From Main Server and Maintain Map DataStructure*/
public class ServiceUrlManager {

	public String RESTAURANT_SERVICE_URL = "";

	private static ServiceUrlManager instance = new ServiceUrlManager();
	private Map<RequestType, String> serviceUrls = null;
	public static final String SEPARATOR = "/";
	public static final String BASE_URL_HTTP = "http://";
	public static final String BASE_URL_COLON = ":";
	public static final String BASE_URL_METHOD = "/IIKO_WCF_Service";

	public static ServiceUrlManager getInstance() {
		return instance;
	}

	/* Private Constructor for Singleton pattern */
	private ServiceUrlManager() {
		serviceUrls = new HashMap<RequestType, String>();
	}

	public void init() {
		// call to be initialize
	}

	public void setBaseUrl(String url) {
		RESTAURANT_SERVICE_URL = url;
		setServiceUrlByType();
	}

	/* Get Service Function URl by providing its Request Type */
	public String getServiceUrlByType(RequestType requestType) {
		return serviceUrls.get(requestType);
	}

	/* Get service URL */
	public String getServiceUrl() {
		return RESTAURANT_SERVICE_URL;
	}

	/**
	 * Creates the url string using the newly entered configurations
	 * 
	 * @return
	 */
	public void setBaseURL(String ipAddress, String portAddress) {
		String url = BASE_URL_HTTP + ipAddress + BASE_URL_COLON + portAddress
				+ BASE_URL_METHOD;
		setBaseUrl(url);
	}

	/* Get Service Function URl by providing its Request Type */
	public String setServiceUrl(RequestType requestType, String serviceUrl) {
		StringBuilder builder = new StringBuilder();
		builder.append(serviceUrl);
		builder.append(SEPARATOR + requestType.getMethodName());
		return builder.toString();
	}

	/* Get Service Function URl by providing its Request Type */
	public void setServiceUrlByType() {

		if (serviceUrls == null)
			Log.d("Home", "Service Url Null");

		// ============= User services URL ================//

		serviceUrls.put(RequestType.USER_LOGIN,
				setServiceUrl(RequestType.USER_LOGIN, RESTAURANT_SERVICE_URL));

		serviceUrls.put(RequestType.USER_LOGOUT,
				setServiceUrl(RequestType.USER_LOGOUT, RESTAURANT_SERVICE_URL));

		serviceUrls
				.put(RequestType.GET_LANGUAGES,
						setServiceUrl(RequestType.GET_LANGUAGES,
								RESTAURANT_SERVICE_URL));

		serviceUrls.put(
				RequestType.GET_CURRENT_ORDERS,
				setServiceUrl(RequestType.GET_CURRENT_ORDERS,
						RESTAURANT_SERVICE_URL));

		serviceUrls.put(
				RequestType.REGISTER_DEVICE,
				setServiceUrl(RequestType.REGISTER_DEVICE,
						RESTAURANT_SERVICE_URL));

		serviceUrls.put(
				RequestType.SEND_NEW_ORDER,
				setServiceUrl(RequestType.SEND_NEW_ORDER,
						RESTAURANT_SERVICE_URL));

		serviceUrls.put(RequestType.EDIT_ORDER,
				setServiceUrl(RequestType.EDIT_ORDER, RESTAURANT_SERVICE_URL));

		serviceUrls.put(
				RequestType.ADD_NEW_ITEM_TO_ORDER,
				setServiceUrl(RequestType.ADD_NEW_ITEM_TO_ORDER,
						RESTAURANT_SERVICE_URL));

		serviceUrls.put(
				RequestType.ADD_NEW_ITEM_LIST,
				setServiceUrl(RequestType.ADD_NEW_ITEM_LIST,
						RESTAURANT_SERVICE_URL));

		serviceUrls.put(
				RequestType.CHECKOUT_ORDER,
				setServiceUrl(RequestType.CHECKOUT_ORDER,
						RESTAURANT_SERVICE_URL));

		serviceUrls.put(
				RequestType.CHECKOUT_BILL_SPLIT_ORDER,
				setServiceUrl(RequestType.CHECKOUT_BILL_SPLIT_ORDER,
						RESTAURANT_SERVICE_URL));

		serviceUrls
				.put(RequestType.GET_MENU_ALL,
						setServiceUrl(RequestType.GET_MENU_ALL,
								RESTAURANT_SERVICE_URL));

		serviceUrls.put(
				RequestType.GET_SECTIONWISE_TABLE,
				setServiceUrl(RequestType.GET_SECTIONWISE_TABLE,
						RESTAURANT_SERVICE_URL));

		serviceUrls.put(
				RequestType.SEND_ORDER_TO_KITCHEN,
				setServiceUrl(RequestType.SEND_ORDER_TO_KITCHEN,
						RESTAURANT_SERVICE_URL));

		serviceUrls.put(RequestType.SYNC_ORDER,
				setServiceUrl(RequestType.SYNC_ORDER, RESTAURANT_SERVICE_URL));

		serviceUrls.put(RequestType.UPLOAD_LOG,
				setServiceUrl(RequestType.UPLOAD_LOG, RESTAURANT_SERVICE_URL));
		
		serviceUrls.put(RequestType.GET_SETTINGS,
				setServiceUrl(RequestType.GET_SETTINGS, RESTAURANT_SERVICE_URL));
	}
}
