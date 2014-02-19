package com.azilen.waiterpad.utils;

import com.azilen.waiterpad.WaiterPadApplication;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
	public static final String IP_ADDRESS = "ipAddress";
	public static final String MENU_SELECTED = "menuSelected";
	public static final String USER_PIN = "userPin";
	public static final String LANGUAGE_SELECTED = "languageSelected";
	public static final String SECTION_ID = "sectionId";
	public static final String TAB_SELECTED = "tabSelected";
	public static final String SECTION_NAME = "sectionName";
	public static final String BILL_SPLIT_CHECKED = "billSplitChecked";
	public static final String PORT_ADDRESS = "portAddress";
	public static final String WAITER_ID = "waiterId";
	public static final String WAITER_CODE = "waiterCode";
	public static final String WAITER_NAME = "waiterName";
	public static final String IS_MENU_ORGANIZED = "organizeMenu";
	public static final String IS_NOTIFICATIONS_ENABLED = "notificationsEnabled";
	public static final String IS_BILL_SPLIT = "isBillSplit";
	public static final String ORDER_ID = "orderId";
	public static final String IS_EXIT_CALLED = "isExitCalled";
	public static final String EXIT_PIN = "exitPin";

	public static final String IS_LANGUAGE_PRESENT = "isLanguagePresent";
	public static final String ARE_NOTIFICATIONS_ENABLED = "areNotificationsEnabled";
	
	public static final String RUSSIAN_LANGUAGE = "RUSSIAN";
	public static final String ENGLISH_LANGUAGE = "ENGLISH";
	
	// preference file name
	private static final String WaiterPadPref = "WAITERPAD";

	/**
	 * @param context
	 *            - pass context
	 * @return SharedPreferences
	 */
	public static SharedPreferences get(Context context) {
		return context.getSharedPreferences(WaiterPadPref, 0);
	}

	/**
	 * @param context
	 *            - context
	 * @param key
	 *            - Constant key, will be used for accessing the stored value
	 * @param val
	 *            - String value to be stored
	 */
	public static void addKey(Context context, String key, String val) {
		SharedPreferences settings = Prefs.get(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, val);
		editor.commit();
	}

	/**
	 * @param key
	 *            - Constant key, will be used for accessing the stored value
	 * @param val
	 *            - String value to be stored
	 */
	public static void addKey(String key, String val) {
		SharedPreferences settings = Prefs.get(WaiterPadApplication
				.getAppContext());
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, val);
		editor.commit();
	}

	/**
	 * @param context
	 * @param key
	 * @param val
	 */
	public static void addKey(Context context, String key, boolean val) {
		SharedPreferences settings = Prefs.get(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(key, val);
		editor.commit();
	}

	/**
	 * @param context
	 * @param key
	 * @param val
	 */
	public static void addKey(Context context, String key, int val) {
		SharedPreferences settings = Prefs.get(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(key, val);
		editor.commit();
	}

	/**
	 * Add preferences
	 * 
	 * @param context
	 *            - context
	 * @param key
	 *            - Constant key, will be used for accessing the stored value
	 * @param val
	 *            - long value to be stored, mostly used to store FB Session
	 *            value
	 */
	public static void addKey(Context context, String key, Long val) {
		SharedPreferences settings = Prefs.get(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(key, val);
		editor.commit();
	}

	/**
	 * Remove preference key
	 * 
	 * @param context
	 *            - context
	 * @param key
	 *            - the key which you stored before
	 */
	public static void removeKey(Context context, String key) {
		SharedPreferences settings = Prefs.get(context);
		SharedPreferences.Editor editor = settings.edit();

		if (settings.contains(key)) {
			editor.remove(key);
		}

		editor.commit();
	}

	/**
	 * Remove preference key
	 * 
	 * @param context
	 *            - context
	 * @param keys
	 *            - array of keys that needs to be removed
	 */
	public static void removeKeys(Context context, String[] keys) {
		SharedPreferences settings = Prefs.get(context);
		SharedPreferences.Editor editor = settings.edit();
		for (String key : keys) {
			if (settings.contains(key)) {
				editor.remove(key);
			}
		}
		editor.commit();
	}

	/**
	 * Get preference value by passing related key
	 * 
	 * @param context
	 *            - context
	 * @param key
	 *            - key value used when adding preference
	 * @return - String value
	 */
	public static String getKey(Context context, String key) {
		SharedPreferences prefs = Prefs.get(context);
		return prefs.getString(key, "");
	}

	/**
	 * Get preference value by passing related key
	 * 
	 * @param key
	 *            - key value used when adding preference
	 * @return - String value
	 */
	public static String getKey(String key) {
		SharedPreferences prefs = Prefs.get(WaiterPadApplication
				.getAppContext());
		return prefs.getString(key, "");
	}

	/**
	 * Get preference value by passing related key
	 * 
	 * @param context
	 *            - context
	 * @param key
	 *            - key value used when adding preference
	 * @return - long value
	 */
	public static long getKey_long(Context context, String key) {
		SharedPreferences prefs = Prefs.get(context);
		return prefs.getLong(key, 0);
	}

	public static boolean getKey_boolean(Context context, String key) {
		SharedPreferences prefs = Prefs.get(context);
		if (prefs.contains(key)) {
			return prefs.getBoolean(key, false);
		}
		return false;
	}
	
	public static boolean getKey_boolean_with_default_true(Context context, String key) {
		SharedPreferences prefs = Prefs.get(context);
		if(prefs.contains(key)) {
			return prefs.getBoolean(key, true);
		}
		return true;
	}

	public static int getKey_int(Context context, String key) {
		SharedPreferences prefs = Prefs.get(context);
		return prefs.getInt(key, 0);
	}

	/**
	 * Clear all stored preferences
	 * 
	 * @param context
	 *            - context
	 */
	public static void clear(Context context) {
		SharedPreferences settings = Prefs.get(context);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.commit();
	}
}
