package com.azilen.waiterpad.managers.settings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.WaiterPadApplication;
import com.azilen.waiterpad.utils.Prefs;

import android.content.Context;
import android.util.Log;

public class SettingsParser {
	private String mResponse;
	private SettingsManager settingsManager;
	
	private static final String EXIT_PIN_REGEX = "<EXIT_PIN>(.*?)</EXIT_PIN>";
	private static final String SERVICE_CALL_TIME_REGEX = "<SERVICECALLTIME>(.*?)</SERVICECALLTIME>";
	private static final String VIBRATION_PATTERN_REGEX = "<VIBRATION_PATTERN>(.*?)</VIBRATION_PATTERN>";
	private static final String FONT_SIZE_REGEX = "<FONT_SIZE>(.*?)</FONT_SIZE>";
	
	private static Pattern EXIT_PIN_PATTERN = Pattern.compile(EXIT_PIN_REGEX);
	private static Pattern SERVICE_CALL_TIME_PATTERN = Pattern.compile(SERVICE_CALL_TIME_REGEX);
	private static Pattern VIBRATION_PATTERN_PATTERN = Pattern.compile(VIBRATION_PATTERN_REGEX);
	private static Pattern FONT_SIZE_PATTERN = Pattern.compile(FONT_SIZE_REGEX);

	/**
	 * Constructor that takes the response as a string
	 * 
	 * @param response
	 */
	public SettingsParser(Context context, String response) {
		mResponse = response;
		settingsManager = SettingsManager.getInstance();
	}

	/**
	 * Parses the document initializes the SAXParser
	 */
	public boolean parseDocument() {
		try{
			Matcher exitPinMatcher = EXIT_PIN_PATTERN.matcher(mResponse);
			Matcher serviceCallTimeMatcher = SERVICE_CALL_TIME_PATTERN.matcher(mResponse);
			Matcher vibrationPatternMatcher = VIBRATION_PATTERN_PATTERN.matcher(mResponse);
			Matcher fontSizeMatcher = FONT_SIZE_PATTERN.matcher(mResponse);
			
			while (exitPinMatcher.find()) {
			    String s = exitPinMatcher.group(1);
			    settingsManager.setExitPin(s);
			    
			    // store the exit pin in the shared preferences
			    // changes as on 27th Jan 2014
			    String exitPin = "";
			    if(s!= null) {
			    	if(s.trim().length() > 0) {
				    	exitPin = s;
				    }else {
				    	exitPin = WaiterPadApplication.getAppContext().getString(R.string.exit_pin);
				    }
			    }else {
			    	exitPin = WaiterPadApplication.getAppContext().getString(R.string.exit_pin);
			    }
			    Prefs.addKey(Prefs.EXIT_PIN, exitPin);
			}
			
			while (serviceCallTimeMatcher.find()) {
			    String s = serviceCallTimeMatcher.group(1);
			    
			    try {
					settingsManager.setServiceCallTime(Integer.parseInt(s));
				} catch (NumberFormatException e) {
					// TODO: handle exception
					// set 2 second Time
					// changes made here as on 12th November 2013
					// 2000 value has to set, since it is in milliseconds
					settingsManager.setServiceCallTime(2000);
				} catch (NullPointerException e) {
					settingsManager.setServiceCallTime(2000);
				}
			}
			
			while (vibrationPatternMatcher.find()) {
			    String s = vibrationPatternMatcher.group(1);
			    settingsManager.setVibrationPattern(s);
			    
			    Log.i("dhara", "parser vib: " + s);
			}
			
			while (fontSizeMatcher.find()) {
			    String s = fontSizeMatcher.group(1);
			    settingsManager.setFontSize(s);
			}
			return true;
		}catch(Exception e) {
			//e.printStackTrace();
		}
		return false;
	}
}
