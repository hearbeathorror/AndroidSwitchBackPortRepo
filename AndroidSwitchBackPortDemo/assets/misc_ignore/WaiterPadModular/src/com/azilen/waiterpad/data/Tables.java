package com.azilen.waiterpad.data;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Tables implements Serializable{
    public enum TableType {
		TABLE(0), VEHICLE(1), SEAT(2);
		
		private int value;
		
		private TableType(int value) {
			this.value = value;
		}
		
		public static TableType findByAbbr(int key) {
	        for (TableType c : values()) {
	            if (c.value == key) {
	                return c;
	            }
	        }
	        return null;
	    }
	}

    @SerializedName(value="Id")
	private String tableId;
    @SerializedName(value="NoOfPersonOnTable")
	private int noOfPeopleOnTheTable;
    @SerializedName(value="Number")
	private int tableNumber;
    @SerializedName(value="Type")
	private TableType tableType;
    @SerializedName(value="SectionId")
	private String sectionId;
    @SerializedName(value="SectionName")
	private String sectionName;
    
    private int tableTypeOrdinal;
    private int orderStatus;
	
	/**
	 * @return the tableId
	 */
	public String getTableId() {
		return tableId;
	}
	/**
	 * @param tableId the tableId to set
	 */
	public void setTableId(String tableId) {
		this.tableId = tableId;
	}
	/**
	 * @return the noOfPeopleOnTheTable
	 */
	public int getNoOfPeopleOnTheTable() {
		return noOfPeopleOnTheTable;
	}
	/**
	 * @param noOfPeopleOnTheTable the noOfPeopleOnTheTable to set
	 */
	public void setNoOfPeopleOnTheTable(int noOfPeopleOnTheTable) {
		this.noOfPeopleOnTheTable = noOfPeopleOnTheTable;
	}
	/**
	 * @return the tableNumber
	 */
	public int getTableNumber() {
		return tableNumber;
	}
	/**
	 * @param tableNumber the tableNumber to set
	 */
	public void setTableNumber(int tableNumber) {
		this.tableNumber = tableNumber;
	}
	/**
	 * @return the tableType
	 */
	public TableType getTableType() {
		return tableType;
	}
	/**
	 * @param tableType the tableType to set
	 */
	public void setTableType(TableType tableType) {
		this.tableType = tableType;
	}	
	/**
	 * @return the sectoionId
	 */
	public String getSectionId() {
		return sectionId;
	}
	/**
	 * @param sectoionId the sectoionId to set
	 */
	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
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
	 * @return the tableTypeOrdinal
	 */
	public int getTableTypeOrdinal() {
		return tableTypeOrdinal;
	}
	/**
	 * @param tableTypeOrdinal the tableTypeOrdinal to set
	 */
	public void setTableTypeOrdinal(int tableTypeOrdinal) {
		this.tableTypeOrdinal = tableTypeOrdinal;
	}
	/**
	 * @return the orderStatus
	 */
	public int getOrderStatus() {
		return orderStatus;
	}
	/**
	 * @param orderStatus the orderStatus to set
	 */
	public void setOrderStatus(int orderStatus) {
		this.orderStatus = orderStatus;
	}
}
