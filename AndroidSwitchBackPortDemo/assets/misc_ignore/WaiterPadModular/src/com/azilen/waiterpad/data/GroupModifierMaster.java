package com.azilen.waiterpad.data;

import com.google.gson.annotations.SerializedName;

public class GroupModifierMaster {
	@SerializedName(value="Id")
	private String groupId;
	@SerializedName(value="GroupName")
	private String groupName;
	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}
	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}
	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}
