package com.azilen.waiterpad.adapters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.map.MultiKeyMap;

import android.content.Context;
import android.support.v4.util.LruCache;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.data.Order;
import com.azilen.waiterpad.data.Tables;
import com.azilen.waiterpad.managers.font.FontManager;
import com.azilen.waiterpad.managers.order.OrderManager;
import com.azilen.waiterpad.utils.Global;
import com.azilen.waiterpad.utils.search.SearchForTableOrders;
import com.google.common.collect.Collections2;

public class TableListAdapter extends ArrayAdapter<Tables> {
	private Context mContext;
	private int mResource;
	private boolean mFlag;
	private List<Tables> tableList;
	private MultiKeyMap orderPerTableMap;
	private List<Order> orderListPerTable;
	private List<Order> billRequestedOrders;
	private List<Order> billRequestOrdersPerTable;
	private LruCache<String, Object> memCache;

	private LayoutInflater inflater;

	public TableListAdapter(Context context, int resource, List<Tables> objects) {
		super(context, resource, objects);
		mContext = context;
		mResource = resource;
		tableList = objects;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		memCache = OrderManager.getInstance().getOrderCache();

		if (tableList != null) {
			Collections.sort(tableList, new Comparator<Tables>() {
				public int compare(Tables t1, Tables t2) {
					return t1.getTableNumber() - t2.getTableNumber();
				}
			});
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder vh;
		if (view == null) {
			view = inflater.inflate(mResource, null);
			vh = new ViewHolder();
			vh.txtTableNumber = (TextView) view
					.findViewById(R.id.txtTableNumberTableList);

			view.setTag(vh);
		} else {
			vh = (ViewHolder) view.getTag();
		}
		
		vh.txtTableNumber.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());

		Tables table = tableList.get(position);
		orderPerTableMap = (MultiKeyMap) memCache.get(Global.PER_TABLE_ORDERS);

		// get the bill requested orders
		billRequestedOrders = (List<Order>) memCache.get(Global.BILL_REQUEST);

		if (orderPerTableMap != null) {
			Tables innerTable = tableList.get(position);
			if (orderPerTableMap.containsKey(innerTable.getSectionId(),
					innerTable.getTableId())) {
				orderListPerTable = (List<Order>) orderPerTableMap.get(
						innerTable.getSectionId(), innerTable.getTableId());

				// Checks if the list of order is not null and that there's
				// atleast one order
				if (orderListPerTable != null && orderListPerTable.size() > 0) {

					Collection<Order> collection = Collections2.filter(
							billRequestedOrders, new SearchForTableOrders(
									innerTable.getTableId()));

					if (collection != null) {
						billRequestOrdersPerTable = new ArrayList<Order>(
								collection);
					}

					boolean areAllBillRequestedOrders = checkForBillOrders();

					// if true then all the orders are bill requested
					if (areAllBillRequestedOrders) {
						vh.txtTableNumber
								.setBackgroundResource(R.drawable.checkout_table_selector);
						vh.txtTableNumber.setTextColor(mContext.getResources()
								.getColor(R.color.silver));
					} else {
						vh.txtTableNumber
								.setBackgroundResource(R.drawable.ongoing_table_selector);
						vh.txtTableNumber.setTextColor(mContext.getResources()
								.getColor(R.color.black));
					}
				}
			} else {
				vh.txtTableNumber
						.setBackgroundResource(R.drawable.normal_table_selector);
				vh.txtTableNumber.setTextColor(mContext.getResources()
						.getColor(R.color.black));
			}
		} else {
			vh.txtTableNumber
					.setBackgroundResource(R.drawable.normal_table_selector);
			vh.txtTableNumber.setTextColor(mContext.getResources().getColor(
					R.color.black));
		}

		vh.txtTableNumber.setText(String.valueOf(table.getTableNumber()));

		return view;
	}

	static class ViewHolder {
		private TextView txtTableNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getCount()
	 */
	@Override
	public int getCount() {
		if (tableList != null) {
			return tableList.size();
		} else {
			return 0;
		}
	}

	private boolean checkForBillOrders() {
		mFlag = false;
		if (orderListPerTable != null) {
			if (billRequestOrdersPerTable != null
					&& billRequestOrdersPerTable.size() > 0) {
				if (orderListPerTable.size() > billRequestOrdersPerTable.size()) {
					for (int i = 0; i < orderListPerTable.size(); i++) {
						for (int j = 0; j < billRequestOrdersPerTable.size(); j++) {
							Order billedOrder = billRequestOrdersPerTable
									.get(j);

							Order order = orderListPerTable.get(i);
							if (order.getOrderId().equals(
									billedOrder.getOrderId())) {
								mFlag = true;
							} else {
								mFlag = false;
								return mFlag;
							}
						}
					}
				} else {
					for (int i = 0; i < orderListPerTable.size(); i++) {
						Order billedOrder = billRequestOrdersPerTable.get(i);

						Order order = orderListPerTable.get(i);
						if (order.getOrderId().equals(billedOrder.getOrderId())) {
							mFlag = true;
						} else {
							mFlag = false;
							return mFlag;
						}
					}
				}
			}
		}

		return mFlag;
	}

}