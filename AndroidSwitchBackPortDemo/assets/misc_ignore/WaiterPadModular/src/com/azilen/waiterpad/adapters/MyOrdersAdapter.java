package com.azilen.waiterpad.adapters;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.data.Order;
import com.azilen.waiterpad.data.Tables;
import com.azilen.waiterpad.managers.font.FontManager;
import com.azilen.waiterpad.managers.language.LanguageManager;

/**
 * MyOrdersAdapter - Inflates the list view to display the current orders per
 * waiter and also the orders per table used by {@MyOrderActivity}
 * @author dhara.shah
 * 
 */
public class MyOrdersAdapter extends ArrayAdapter<Order> {
	private Context mContext;
	private int mResource;
	private List<Order> mOrders;
	private DecimalFormat dcf;

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param resource
	 * @param objects
	 */
	public MyOrdersAdapter(Context context, int resource, List<Order> objects) {
		super(context, resource, objects);
		mContext = context;
		mResource = resource;
		mOrders = objects;

		dcf = new DecimalFormat("#.##");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder vh;

		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(mResource, parent, false);

			vh = new ViewHolder();
			vh.txtSectionName = (TextView) view
					.findViewById(R.id.txtSectionName);
			vh.txtTableNumber = (TextView) view
					.findViewById(R.id.txtTableNumberMyOrder);
			vh.txtOrderNumber = (TextView) view
					.findViewById(R.id.txtOrderNumberMyOrder);
			vh.txtTotalAmount = (TextView) view
					.findViewById(R.id.txtTotalMyOrderScreen);
			vh.relRowLayout = (RelativeLayout)view
					.findViewById(R.id.relLayerMenuScreenIndi);

			view.setTag(vh);
		} else {
			vh = (ViewHolder) view.getTag();
		}
		
		vh.txtSectionName.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		vh.txtTableNumber.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		vh.txtOrderNumber.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		vh.txtTotalAmount.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());

		Order order = mOrders.get(position);

		if (order != null) {
			Tables table = order.getTable();
			double totalAmount = order.getTotalAmount();

			vh.txtSectionName.setText(String.valueOf(table.getSectionName()));

			String strTotalAmount = dcf.format(totalAmount);

			String currancySymbol = LanguageManager.getInstance()
					.getCurrancySymbol();

			String currancyLocation = LanguageManager.getInstance().IS_AFTER();

			if (!currancyLocation.equalsIgnoreCase("TRUE")) {
				strTotalAmount = currancySymbol + " " + strTotalAmount;
			} else {
				strTotalAmount = strTotalAmount + " " + currancySymbol;
			}

			vh.txtTableNumber.setText(String.valueOf(table.getTableNumber()));
			vh.txtOrderNumber.setText(order.toString());
			vh.txtTotalAmount.setText(strTotalAmount);
			
			if(order.getOrderStatus() != null && 
					order.getOrderStatus().ordinal() == 2) {
				vh.relRowLayout.setBackgroundColor(mContext.getResources().getColor(R.color.list_select_color));
			}else {
				vh.relRowLayout.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
			}
		}

		return view;
	}

	static class ViewHolder {
		private TextView txtTableNumber;
		private TextView txtTotalAmount;
		private TextView txtSectionName;
		private TextView txtOrderNumber;
		private RelativeLayout relRowLayout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getCount()
	 */
	@Override
	public int getCount() {
		if (mOrders != null) {
			return mOrders.size();
		} else {
			return 0;
		}
	}
}
