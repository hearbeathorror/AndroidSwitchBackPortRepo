package com.azilen.waiterpad.data;

/**
 * Holds the Service configuration
 * @author dhara.shah
 *
 */
public class ServiceConfig {
	private String ipAddress;
	private String portAddress;
	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}
	/**
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	/**
	 * @return the portAddress
	 */
	public String getPortAddress() {
		return portAddress;
	}
	/**
	 * @param portAddress the portAddress to set
	 */
	public void setPortAddress(String portAddress) {
		this.portAddress = portAddress;
	}
}
