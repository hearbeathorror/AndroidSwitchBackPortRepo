package com.azilen.waiterpad.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Chintan Rathod User: waiterpad Date: 21/11/13 Time: 11:50 AM
 */
public class Validator {
	/**
	 * Returns if the IP address entered is valid or not
	 * 
	 * @param ipAddress
	 * @return TRUE: valid IP address <br>
	 *         FALSE : invalid IP address
	 */
	public static boolean isValidIPAddress(String ipAddress) {
		String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
		Matcher matcher = pattern.matcher(ipAddress);
		return matcher.matches();
	}

	/**
	 * Returns if the PORT entered is valid or not
	 * 
	 * @param portAddress
	 * @return TRUE: valid Port address <br>
	 *         FALSE : invalid Port address
	 */
	public static boolean isValidPort(String portAddress) {
		String PORT_PATTERN = "(6553[0-5]|655[0-2]\\"
				+ "d|65[0-4]\\d{2}|6[0-4]\\" + "d{3}|[1-5]\\d{4}|[1-9]\\"
				+ "d{0,3})";
		Pattern portPattern = Pattern.compile(PORT_PATTERN);
		Matcher portMatcher = portPattern.matcher(portAddress);
		return portMatcher.matches();
	}

	public static boolean isValidNumber(String password) {
		//String NUMBER_PATTERN = "[0-9]*";
		String PIN_PATTERN = "^[\\S]*$";
		Pattern pattern = Pattern.compile(PIN_PATTERN);
		Matcher matcher = pattern.matcher(password);
		return matcher.matches();
	}
	
	public static boolean containsOnlyNumbers(String password) {
		String NUMBER_PATTERN = "[0-9]*";
		Pattern pattern = Pattern.compile(NUMBER_PATTERN);
		Matcher matcher = pattern.matcher(password);
		return matcher.matches();
	}
}
