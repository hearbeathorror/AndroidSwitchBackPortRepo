package com.azilen.waiterpad.data;

import com.google.gson.annotations.SerializedName;

/**
 * Stores the registration response to identify
 * successful registration 
 * @author dhara.shah
 *
 */
public class RegistrationStatus extends ResponseEntity{
	public enum OSType {
		ANDROID(0), IOS(1);
		
		private int key;
		
		private OSType(int key) {
			this.key = key;
		}
		
		public static OSType getOsType(int key) {
			for(OSType type : values()) {
				if(type.key == key) {
					return type;
				}
			}
			return null;
		}
	}
	@SerializedName(value="MAC")
	private String macAddress;
	@SerializedName(value="OSType")
	private OSType OSType;
	@SerializedName(value="Status")
	private boolean registeredSuccessfully;
	
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
	 * @return the oSType
	 */
	public OSType getOSType() {
		return OSType;
	}
	/**
	 * @param oSType the oSType to set
	 */
	public void setOSType(OSType oSType) {
		OSType = oSType;
	}
	/**
	 * @return the registeredSuccessfully
	 */
	public boolean isRegisteredSuccessfully() {
		return registeredSuccessfully;
	}
	/**
	 * @param registeredSuccessfully the registeredSuccessfully to set
	 */
	public void setRegisteredSuccessfully(boolean registeredSuccessfully) {
		this.registeredSuccessfully = registeredSuccessfully;
	}
}
