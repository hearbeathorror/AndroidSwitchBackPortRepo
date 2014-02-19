package com.azilen.waiterpad.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.FileAppender;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.WaiterPadApplication;
import com.azilen.waiterpad.activities.BaseActivity;
import com.azilen.waiterpad.managers.language.LanguageManager;
import com.azilen.waiterpad.utils.search.SearchForIsoCodeInArray;
import com.google.common.collect.Iterables;

/**
 * Contains all the common functions needed by the application
 * 
 * @author dhara.shah MultipartEntity*
 */
public class Utils {
	private Context mContext;
	private int mCounter;

	/**
	 * Constructor
	 * 
	 * @param context
	 */
	public Utils(Context context) {
		mContext = context;
	}

	/**
	 * Returns the MAC Address of the device if it is connected to wifi
	 * 
	 * @return
	 */
	public static String getMacAddress() {
		WifiManager wifiMan = (WifiManager) WaiterPadApplication
				.getAppContext().getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInf = wifiMan.getConnectionInfo();
		String macAddr = wifiInf.getMacAddress();
		return macAddr;
	}

	/**
	 * Returns the OS version of the device
	 * 
	 * @return
	 */
	public String getOsVersion() {
		String osVersion = Build.VERSION.RELEASE;
		return osVersion;
	}

	/**
	 * Returns the Device Name of the device
	 * 
	 * @return
	 */
	public static String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;

		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		} else {
			if (manufacturer.equalsIgnoreCase("unknown")) {
				manufacturer = manufacturer.toLowerCase()
						.replace("unknown", "");
			}
			return capitalize(manufacturer) + " " + model;
		}
	}

	/**
	 * Returns true if the device is connected to wifi else returns false if the
	 * device is not connected to wifi
	 * 
	 * @return
	 */
	public boolean isConnected() {
		boolean isConnected = false;
		ConnectivityManager connManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi != null
				&& (mWifi.isConnected() || mWifi.isConnectedOrConnecting())) {
			isConnected = true;
		} else {
			isConnected = false;
		}
		return isConnected;
	}

	/**
	 * Capitalizes the first character of each new string
	 * 
	 * @param s
	 * @return
	 */
	private static String capitalize(String s) {
		if (s == null || s.trim().length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	}

	/**
	 * Returns the url generated
	 * 
	 * @param method
	 * @return
	 */
	public String getURLString(String method) {
		String url = mContext.getString(R.string.http)
				+ Prefs.getKey(mContext, Prefs.IP_ADDRESS)
				+ mContext.getString(R.string.colon)
				+ Prefs.getKey(mContext, Prefs.PORT_ADDRESS)
				+ mContext.getString(R.string.delimiter)
				+ mContext.getString(R.string.WEBSERVICE_NAME)
				+ mContext.getString(R.string.delimiter) + method;

		// String url = "http://192.168.3.139:13050/IIKO_WCF_Service/" + method;

		return url;
	}



	

	/**
	 * Reads the log file
	 * 
	 * @return
	 */
	public InputStream readFromLogFile() {
		File sdcard = Environment.getExternalStorageDirectory();
		File file = null;

		if (sdcard.exists()) {
			// Get the text file

			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

			file = new File(sdcard, "waiterpad/files/log/"
					+ sdf.format(calendar.getTime()) + ".log");

			try {
				StringBuffer contents = fileToString(file);

				if (contents != null) {
					InputStream inputStream = new ByteArrayInputStream(contents
							.toString().getBytes());
					return inputStream;
				}
			} catch (NullPointerException e) {
				WaiterPadApplication.LOG.debug(e.getMessage());
			}
		}

		return null;
	}

	public File getFile() {
		File sdcard = Environment.getExternalStorageDirectory();
		File file = null;

		if (sdcard.exists()) {
			// Get the text file

			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

			file = new File(sdcard, "waiterpad/files/log/"
					+ sdf.format(calendar.getTime()) + ".log");
		}

		return file;
	}

	/**
	 * convert file to Str
	 */
	public static StringBuffer fileToString(File file) {

		// Read text from file
		StringBuffer text = new StringBuffer();

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;

			while ((line = br.readLine()) != null) {
				text.append(line);
				text.append('\n');
			}

			return text;
		} catch (IOException e) {
			// You'll need to add proper error handling here
		}

		return null;
	}


	/**
	 * Gets the menu from the sdcard
	 * 
	 * @return
	 */
	public StringBuffer getStringFromFile() {
		File sdcard = Environment.getExternalStorageDirectory();
		File file = null;

		if (sdcard.exists()) {
			// Get the text file
			file = new File(sdcard, "waiterpad/files/menu.txt");
		}

		if (file != null) {
			StringBuffer buffer = fileToString(file);
			if (buffer != null) {
				return buffer;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Changes the keyboard settings as per the language change
	 * 
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException
	 */
	public void changeKeyBoardSettings() throws ClassNotFoundException,
			NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			NoSuchFieldException {

		String localName = "";
		String isoCode = "";

		localName = LanguageManager.getInstance().getStartNode();
		isoCode = LanguageManager.getInstance().getIsoCode();

		String[] isoCodeLangArray = Locale.getISOLanguages();
		List<String> isoCodeList = Arrays.asList(isoCodeLangArray);

		String codeIsThere = Iterables.find(isoCodeList,
				new SearchForIsoCodeInArray(isoCode.toLowerCase()), null);

		Locale locale = null;

		// check if the language exists in the database
		if (codeIsThere != null) {
			locale = new Locale(isoCode.toLowerCase());
		} else {
			locale = new Locale("en_US");
		}

		Class amnClass = Class.forName("android.app.ActivityManagerNative");
		Object amn = null;
		Configuration config = null;

		// amn = ActivityManagerNative.getDefault();
		Method methodGetDefault = amnClass.getMethod("getDefault");
		methodGetDefault.setAccessible(true);
		amn = methodGetDefault.invoke(amnClass);

		// config = amn.getConfiguration();
		Method methodGetConfiguration = amnClass.getMethod("getConfiguration");
		methodGetConfiguration.setAccessible(true);
		config = (Configuration) methodGetConfiguration.invoke(amn);

		// config.userSetLocale = true;
		Class configClass = config.getClass();
		Field f = configClass.getField("userSetLocale");
		f.setBoolean(config, true);

		// set the locale to the new value
		config.locale = locale;

		// amn.updateConfiguration(config);
		Method methodUpdateConfiguration = amnClass.getMethod(
				"updateConfiguration", Configuration.class);
		methodUpdateConfiguration.setAccessible(true);
		methodUpdateConfiguration.invoke(amn, config);
	}

	public void setLoggingPath() {
		File sdcard = Environment.getExternalStorageDirectory();

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		sdf.format(calendar.getTime());

		FileAppender fileAppender = new FileAppender();
		fileAppender.setContext(WaiterPadApplication.loggerContext);
		fileAppender.setName("timestamp");
		// set the file name
		fileAppender.setFile(sdcard + "/waiterpad/files/log/"
				+ sdf.format(calendar.getTime()) + ".log");

		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setContext(WaiterPadApplication.loggerContext);
		encoder.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
		encoder.start();

		fileAppender.setEncoder(encoder);
		fileAppender.start();

		// attach the rolling file appender to the logger of your choice
		WaiterPadApplication.LOG = WaiterPadApplication.loggerContext
				.getLogger(BaseActivity.class);
		WaiterPadApplication.LOG.addAppender(fileAppender);
	}

	private void writeToLog(String message) {
		if (WaiterPadApplication.LOG != null) {
			WaiterPadApplication.LOG.debug(message);
		}
	}

	public int getCount() {
		return mCounter;
	}

	public void setCount(int counter) {
		mCounter = counter;
	}

	public static Object copy(Object orig) {
		Object obj = null;
		try {
			// Write the object out to a byte array
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(orig);
			out.flush();
			out.close();

			// Make an input stream from the byte array and read
			// a copy of the object back in.
			ObjectInputStream in = new ObjectInputStream(
					new ByteArrayInputStream(bos.toByteArray()));
			obj = in.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
		return obj;
	}
	
	public void startActivityAfterHomePress(Intent intentToStart) {
		BaseActivity.startActivityAfterHomePress(intentToStart);
	}
	
	public void clearCache() {
		File[] dir = WaiterPadApplication.getAppContext().getCacheDir().listFiles();
		if(dir != null){
			for (File f : dir){
				f.delete();
			}
		}
	}
}
