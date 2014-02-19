package com.azilen.waiterpad.managers.network;

/**
 * Created by Kirtan User: eMenuUser Date: 6/4/13 Time: 2:52 PM
 */

/*
 * RequestType Type Enum Is used by Application to Filter Broadcast revived by
 * them from Http Request it also Include Service Method Name
 */

public enum RequestType {
	USER_LOGOUT("Logout"), 
	USER_LOGIN("GetAuthenticateCredentials"),
	REGISTER_DEVICE("RegisterDevice"),
	SEND_NEW_ORDER("PutNewOrder"),
	SEND_ORDER_TO_KITCHEN("SendPrintRequest"),
	GET_SECTIONWISE_TABLE("GetSectionWisetableList"),
	ADD_NEW_ITEM_TO_ORDER("AddNewItemInOrder"),
	ADD_NEW_ITEM_LIST("AddNewItemInOrderList"),
	SYNC_ORDER("SyncOrder"),
	GET_LANGUAGES("GetLanguage"),
	CHECKOUT_ORDER("Checkout"),
	CHECKOUT_BILL_SPLIT_ORDER("CheckoutList"),
	UPLOAD_LOG("UploadLog"),
	GET_MENU_ALL("GetAllMenu"),
	GET_CURRENT_ORDERS("GetCurrentOrderList"),
	EDIT_ORDER("EditOrder"),
	GET_SETTINGS("GetSettings");

	private final String methodName;

	private RequestType(String methodName) {
		this.methodName = methodName;
	}

	public String getMethodName() {
		return methodName;
	}
}
