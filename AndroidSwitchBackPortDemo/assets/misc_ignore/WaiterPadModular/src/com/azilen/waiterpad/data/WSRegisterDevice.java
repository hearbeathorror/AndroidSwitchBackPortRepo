package com.azilen.waiterpad.data;

import com.google.gson.annotations.SerializedName;

public class WSRegisterDevice {
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
	@SerializedName(value="DeviceName")
	private String deviceName;
	@SerializedName(value="OSType")
	private int osType;
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
	 * @return the deviceName
	 */
	public String getDeviceName() {
		return deviceName;
	}
	/**
	 * @param deviceName the deviceName to set
	 */
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	/**
	 * @return the osType
	 */
	public int getOsType() {
		return osType;
	}
	/**
	 * @param osType the osType to set
	 */
	public void setOsType(int osType) {
		this.osType = osType;
	}
}
