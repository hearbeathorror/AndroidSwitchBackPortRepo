package com.azilen.waiterpad.data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

public class Order extends CommonFields implements Cloneable, Serializable{
	public enum OrderStatus {
		NEWORDER(0), CLOSED(1), BILL(2), DELETED(3);
		
		private int key;
		
		private OrderStatus(int key) {
			this.key = key;
		}
		
		public static OrderStatus getOrderStatus(int key) {
			for(OrderStatus o : values()) {
				if(o.key == key) {
					return o;
				}
			}
			return null;
		}
	}
	
	@SerializedName(value="Id")
	private String orderId;
	@SerializedName(value="FullSum")
	private double totalAmount;
	@SerializedName(value="Guests")
	private List<Guest> guests;
	@SerializedName(value="Number")
	private int orderNumber;
	@SerializedName(value="ProcessedPayment")
	private double processedPayment;
	@SerializedName(value="ResultSum")
	private double amountPayable;
	@SerializedName(value="WaiterId")
	private String waiterId;
	@SerializedName(value="Status")
	private OrderStatus orderStatus;
	@SerializedName(value="Table")
	private Tables table;
	@SerializedName(value="OpenTime")
	private Date openOrderTime;
	@SerializedName(value="CloseTime")
	private Date closeOrderTime;
	@SerializedName(value="IsBillSplit")
	private boolean isBillSplit;
	
	private boolean isModified;
	private int quantityToBeDeleted;

	/**
	 * @return the totalAmount
	 */
	public double getTotalAmount() {
		return totalAmount;
	}
	/**
	 * @param totalAmount the totalAmount to set
	 */
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	/**
	 * @return the guests
	 */
	public List<Guest> getGuests() {
		return guests;
	}
	/**
	 * @param guests the guests to set
	 */
	public void setGuests(List<Guest> guests) {
		this.guests = guests;
	}
	/**
	 * @return the number
	 */
	public int getOrderNumber() {
		return orderNumber;
	}
	/**
	 * @param number the number to set
	 */
	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}
	/**
	 * @return the processedPayment
	 */
	public double getProcessedPayment() {
		return processedPayment;
	}
	/**
	 * @param processedPayment the processedPayment to set
	 */
	public void setProcessedPayment(double processedPayment) {
		this.processedPayment = processedPayment;
	}
	/**
	 * @return the amountPayable
	 */
	public double getAmountPayable() {
		return amountPayable;
	}
	/**
	 * @param amountPayable the amountPayable to set
	 */
	public void setAmountPayable(double amountPayable) {
		this.amountPayable = amountPayable;
	}
	/**
	 * @return the waiterId
	 */
	public String getWaiterId() {
		return waiterId;
	}
	/**
	 * @param waiterId the waiterId to set
	 */
	public void setWaiterId(String waiterId) {
		this.waiterId = waiterId;
	}
	/**
	 * @return the orderStatus
	 */
	public OrderStatus getOrderStatus() {
		return orderStatus;
	}
	/**
	 * @param orderStatus the orderStatus to set
	 */
	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}
	/**
	 * @return the table
	 */
	public Tables getTable() {
		return table;
	}
	/**
	 * @param table the table to set
	 */
	public void setTable(Tables table) {
		this.table = table;
	}
	/**
	 * @return the orderId
	 */
	public String getOrderId() {
		return orderId;
	}
	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	/**
	 * @return the openOrderTime
	 */
	public Date getOpenOrderTime() {
		return openOrderTime;
	}
	/**
	 * @param openOrderTime the openOrderTime to set
	 */
	public void setOpenOrderTime(Date openOrderTime) {
		this.openOrderTime = openOrderTime;
	}
	/**
	 * @return the closeOrderTime
	 */
	public Date getCloseOrderTime() {
		return closeOrderTime;
	}
	/**
	 * @param closeOrderTime the closeOrderTime to set
	 */
	public void setCloseOrderTime(Date closeOrderTime) {
		this.closeOrderTime = closeOrderTime;
	}
	
	public String toString() {
		return String.valueOf(orderNumber);
	}
	/**
	 * @return the isBillSplit
	 */
	public boolean isBillSplit() {
		return isBillSplit;
	}
	/**
	 * @param isBillSplit the isBillSplit to set
	 */
	public void setBillSplit(boolean isBillSplit) {
		this.isBillSplit = isBillSplit;
	}
	/**
	 * @return the isModified
	 */
	public boolean isModified() {
		return isModified;
	}
	/**
	 * @param isModified the isModified to set
	 */
	public void setModified(boolean isModified) {
		this.isModified = isModified;
	}
	/**
	 * @return the quantityToBeDeleted
	 */
	public int getQuantityToBeDeleted() {
		return quantityToBeDeleted;
	}
	/**
	 * @param quantityToBeDeleted the quantityToBeDeleted to set
	 */
	public void setQuantityToBeDeleted(int quantityToBeDeleted) {
		this.quantityToBeDeleted = quantityToBeDeleted;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
