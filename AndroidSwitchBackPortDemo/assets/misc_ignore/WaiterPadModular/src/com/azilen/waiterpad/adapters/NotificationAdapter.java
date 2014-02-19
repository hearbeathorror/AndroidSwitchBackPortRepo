package com.azilen.waiterpad.adapters;

import java.util.List;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.data.Order;
import com.azilen.waiterpad.managers.font.FontManager;
import com.azilen.waiterpad.managers.language.LanguageManager;

public class NotificationAdapter extends ArrayAdapter<Order> {
	private List<Order> mOrders;
	private Context mContext;
	private int mResource;
	private LayoutInflater mInflater;

	public NotificationAdapter(Context context, int resource,
			List<Order> objects) {
		super(context, resource, objects);
		mContext = context;
		mOrders = objects;
		mResource = resource;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder vh = null;

		if (view == null) {
			mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = mInflater.inflate(mResource, parent, false);
			vh = new ViewHolder();
			vh.txtOrderText = (TextView) view.findViewById(R.id.txtOrderText);
			view.setTag(vh);
		} else {
			vh = (ViewHolder) view.getTag();
		}

		Order order = mOrders.get(position);
		String message = "";
		message = LanguageManager.getInstance().getOrderNumberLabel() + " "
				+ order.getTable().getTableNumber() + " . "
				+ order.getOrderNumber() + "   "
				+ LanguageManager.getInstance().getHasBeenUpdated();

		vh.txtOrderText.setText(message);
		
		vh.txtOrderText.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		return view;
	}

	static class ViewHolder {
		private TextView txtOrderText;
	}
}
