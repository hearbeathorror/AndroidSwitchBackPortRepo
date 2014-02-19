package com.azilen.waiterpad.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.azilen.waiterpad.R;

public class Global {

	// intent extras constants
	public static String TABLE_ID = "tableId";
	public static String TABLE_NUMBER = "tableNumber";
	public static String FROM_ACTIVITY = "from";
	public static String TIME_STAMP = "timestamp";
	public static String ACTION = "action";
	public static String ORDER_ID = "orderId";
	public static String ORDER_NUMBER = "orderNumber";
	public static String GUEST_NUMBER = "guestNumber";
	public static String PREVIOUS = "previous";
	public static String NOTIFICATION_COUNT = "notificationCount";

	// from activity string constants
	public static String FROM_ORDER_RELATED = "orderRelated";
	public static String FROM_TABLE_LIST = "tablelist";
	public static String FROM_TABLE_ORDER_LIST = "tableorderlist";
	public static String FROM_HOME_ACTIVITY = "home";
	public static String FROM_LOGIN_ACTIVITY = "loginactivity";
	public static String FROM_SETTINGS_FRAGMENT = "settingsactivity";
	public static String FROM_CHANGE_WAITER_CODE = "changewaitercode";
	public static String FROM_SPLASH = "splash";
	public static String FROM_LOCK = "lock";
	public static String FROM_LOGOUT = "logout";
	public static String FROM_LOGIN = "login";
	public static String FROM_NOTIFICATIONS = "notifications";
	public static String FROM_SETTINGS = "settings";
	public static String FROM_EXIT ="exit";
	public static String FROM_SPLASH_ACTIVITY ="SplashActivity";

	// parameters to functions
	public static String GET_ALL_ORDERS_SERVICE_PARAM = "getallordersservice";
	public static String HOME = "home";
	public static String MY_ORDERS = "myorders";
	public static String BACK = "back";
	public static String ORDER_RELATED = "orderrelated";
	public static String PLACE_NEW_ORDER = "place new order";
	public static String TABLE_LIST = "tablelist";

	// LRUCache Keys
	public static String RUNNING_ORDERS = "runningOrders";
	public static String ORDER_PER_WAITER = "orderPerWaiter";
	public static String WAITER_INFO = "waiterInfo";
	public static String NOTIFICATION_ORDERS = "notificationOrders";
	public static String CURRENT_ORDER = "currentOrder";
	public static String PER_TABLE_ORDERS = "perTableOrders";
	public static String BILL_REQUEST ="billrequest";
	public static String LANGUAGES ="languages";
	public static String SPLIT_ORDERS ="splitOrders";
	public static String TABLE_MAP ="tableMap";
	public static String CATEGORIAL_ITEMS = "categorialItems";
	public static String ITEMLIST_SECTION_WISE ="itemListSectionWise";
	public static String CATEGORY_WISE_ITEMS ="categoryWiseItems";
	public static String SECTIONS = "sections";
	public static String SECTION_WISE_CATEGORIES ="sectionWiseCategories";
	public static String SECTION_WISE_TABLE = "sectionWiseTable";
	public static String SECTION_WISE_MENU="sectionWiseMenu";
	public static String CHECKED_OUT_ORDER_IDS = "checkedOutOrderIds";

	// values to put into shared preferences
	public static String TABLES = "tables";
	public static String SETTINGS = "settings";
	public static String NOTIFICATIONS = "notifications";
	public static String ORDERS = "orders";
	public static String YES = "yes";

	// related to order related refresh list
	public static String ORDER_FROM_ASYNC_SEND = "async-send";
	public static String ORDER_FROM_ADAPTER = "adapter";
	public static String ORDER_ACTION_UPDATED = "updated";
	public static String ORDER_ACTION_CHECKOUT = "checkoutorder";
	public static String ORDER_ACTION_DELETE = "delete";
	public static String ORDER_ACTION_EDIT_ITEM = "edititem";
	public static String ORDER_ACTION_ADDED_ITEM = "added_item";
	public static String ORDER_ACTION_NEW_ORDER="neworder";
	
	//tag name
	private static String TAG = "iiko";
	
	// flag to set whether the user has clicked the send order button 
	// to prevent self notifications
	public static boolean isSendOrderCalled = false;
	// animations

	/**
	 * Slide Activity Right To Left
	 * 
	 * @param mContext
	 *            : Context of Application
	 */
	public static void activityFinishAnimationLeftToRight(Context mContext) {
		((Activity) mContext)
				.overridePendingTransition(R.anim.activity_finish_in_anim,
						R.anim.activity_finish_out_anim);
	}
	
	/**
	 * Slide Activity Right To Left
	 * 
	 * @param mContext
	 *            : Context of Application
	 */
	public static void activityStartAnimationRightToLeft(Context mContext) {
		((Activity) mContext)
				.overridePendingTransition(R.anim.activity_in_anim,R.anim.activity_out_anim);
	}
	
	public static void logd(String message){
		Log.d(TAG,message);
	}
	
	public static void logi(String message){
		Log.i(TAG,message);
	}
	
	public static void logw(String message){
		Log.w(TAG,message);
	}
}
