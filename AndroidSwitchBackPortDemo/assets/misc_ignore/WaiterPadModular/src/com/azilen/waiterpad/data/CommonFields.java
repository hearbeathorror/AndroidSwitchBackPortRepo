package com.azilen.waiterpad.data;

import com.google.gson.annotations.SerializedName;

/**
 * Holds the common fields that would be inherited 
 * by all the other entities. Also used while receiving 
 * and sending json data
 * @author dhara.shah
 *
 */
public class CommonFields {
	@SerializedName(value="MACAddress")
	public String macAddress;
	@SerializedName(value="WaiterCode")
	public String waiterCode;
	/**
	 * @return the macAddress
	 */
	public String getMacAddress() {
		return macAddress;
	}
	/**
	 * @param macAddress the macAddress to set
	 */
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	/**
	 * @return the waiterCode
	 */
	public String getWaiterCode() {
		return waiterCode;
	}
	/**
	 * @param waiterCode the waiterCode to set
	 */
	public void setWaiterCode(String waiterCode) {
		this.waiterCode = waiterCode;
	}
}
