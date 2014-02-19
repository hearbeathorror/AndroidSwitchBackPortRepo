package com.azilen.waiterpad.managers.settings;

import java.util.List;

import android.support.v4.util.LruCache;
import android.util.Log;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.WaiterPadApplication;
import com.azilen.waiterpad.data.Languages;
import com.azilen.waiterpad.managers.network.NetworkManager;
import com.azilen.waiterpad.managers.network.RequestType;
import com.azilen.waiterpad.managers.network.ServiceUrlManager;
import com.azilen.waiterpad.utils.Global;
import com.azilen.waiterpad.utils.Prefs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SettingsManager {
	private static SettingsManager instance = new SettingsManager();
	private Languages mLanguages;

	private LruCache<String, Object> settingsCache;

	// settings cache strings
	private int serviceCallTime = 2000;
	private String serviceCallTimeKey = "serviceCallTime";

	private String vibrationPattern ="1";
	private String vibrationPatternKey = "vibrationPattern";
	private String fontSize = "1";
	private String fontSizeKey = "fontSize";
	private String exitPinKey = "exitPin";

	// max cache size 1 MB = 1024 KB
	private int maxSize = 1 * 1024 * 1024;

	public SettingsManager() {
		settingsCache = new LruCache<String, Object>(maxSize);
	}

	/* singleton object */
	public static SettingsManager getInstance() {
		return instance;
	}

	public LruCache<String, Object> getLanguageCache() {
		return settingsCache;
	}

	/**
	 * Returns the list of languages that are present
	 * 
	 * @return
	 */
	public Languages getLanguages(String parameterSent) {
		String url = ServiceUrlManager.getInstance().getServiceUrlByType(
				RequestType.GET_LANGUAGES)
				+ ServiceUrlManager.SEPARATOR + parameterSent;

		Global.logd("URL : " + url);

		String languageResponse = NetworkManager.getInstance()
				.performGetRequest(url);

		if (languageResponse == null) {
			return null;
		} else {
			try {
				GsonBuilder gsonBuilder = new GsonBuilder();
				Gson gson = gsonBuilder.create();
				mLanguages = gson.fromJson(languageResponse, Languages.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return mLanguages;
		}
	}

	/**
	 * Stores all the languages obtained into the memcache of the device. Used
	 * by @GetAllDataAsyncTask and @SettingsFragment
	 * 
	 * @param languages
	 * @param memCache
	 */
	public void storeLangugagesIntoCache(Languages result, String parameterSent) {
		if (result != null) {
			switch (result.getResponseCode()) {
			case 1000:
				// There's an error that takes place
				Prefs.addKey(WaiterPadApplication.getAppContext(),
						Prefs.IS_LANGUAGE_PRESENT, false);
				break;

			case 1004:
				// store the languages into the cache
				if (result.getLanguages() != null) {

					Prefs.addKey(WaiterPadApplication.getAppContext(),
							Prefs.IS_LANGUAGE_PRESENT, true);

					List<String> languages = result.getLanguages();
					settingsCache.put(Global.LANGUAGES, languages);

					Global.logd("inside if of languages");
				} else {
					// no language list obtained
					// use english by default
					Global.logd("inside else of languages");
				}
				break;

			case 1005:

				// store the xml into the cache
				if (result.getLanguageXml() != null) {
					// the language xml file is there
					SettingsParser languageParser = new SettingsParser(
							WaiterPadApplication.getAppContext(),
							result.getLanguageXml());
					boolean isParsed = languageParser.parseDocument();

					if (isParsed) {
						// store it in the memory
						// languageCache.put(parameterSent, languageXml);
						Prefs.addKey(parameterSent, result.getLanguageXml());
					}
				} else {
					// use english by default
					// and display a message to the user
				}
				break;

			default:
				break;
			}
		}
	}
	
	public String getValueFromKey(String keyParam) {
		return (settingsCache.get(keyParam) == null ? "" : settingsCache.get(
				keyParam).toString());
	}
	
	public int getServiceCallTime() {
		String temp = getValueFromKey(serviceCallTimeKey);
		return temp.equals("") ? Integer.parseInt(WaiterPadApplication
				.getAppContext().getString(
						R.string.time_for_gettings_all_orders)) : Integer
				.parseInt(temp);
	}

	public void setServiceCallTime(int serviceCallTime) {
		settingsCache.put(this.serviceCallTimeKey, serviceCallTime);
	}
	
	public String getFontSize() {
		String temp = getValueFromKey(fontSizeKey);
		return temp.equals("") ? WaiterPadApplication
				.getAppContext().getString(
						R.string.medium_font) : temp;
	}
	
	public void setFontSize(String fontSize) {
		settingsCache.put(this.fontSizeKey, fontSize);
	}
	
	public String getVibrationPattern() {
		String temp = getValueFromKey(vibrationPatternKey);
		return temp.equals("") ? WaiterPadApplication
				.getAppContext().getString(
						R.string.normal_vibration) : temp;
	}
	
	public void setVibrationPattern(String vibrationPattern) {
		Log.i("dhara", "vib: " + vibrationPattern);
		settingsCache.put(this.vibrationPatternKey, vibrationPattern);
	}
	
	public void setExitPin(String exitPin) {
		settingsCache.put(this.exitPinKey, exitPin);
	}
	
	public String getExitPin() {
		String temp = getValueFromKey(exitPinKey);
		return temp.equals("") ? WaiterPadApplication
				.getAppContext().getString(
						R.string.exit_pin) : temp;
	}
}
