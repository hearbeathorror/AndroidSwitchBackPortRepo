package com.azilen.waiterpad.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class SectionTable {
	@SerializedName(value="Id")
	private String id;
	@SerializedName(value="Name")
	private String sectionName;
	@SerializedName(value="TableInSection")
	private List<Tables> tableList;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the sectionName
	 */
	public String getSectionName() {
		return sectionName;
	}
	/**
	 * @param sectionName the sectionName to set
	 */
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	/**
	 * @return the tableList
	 */
	public List<Tables> getTableList() {
		return tableList;
	}
	/**
	 * @param tableList the tableList to set
	 */
	public void setTableList(List<Tables> tableList) {
		this.tableList = tableList;
	}
}
