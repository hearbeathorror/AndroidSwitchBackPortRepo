package com.azilen.waiterpad.adapters;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.activities.TableOrderListActivity;
import com.azilen.waiterpad.data.Guest;
import com.azilen.waiterpad.data.Order;
import com.azilen.waiterpad.managers.font.FontManager;
import com.azilen.waiterpad.managers.language.LanguageManager;
import com.azilen.waiterpad.utils.Global;

public class PerTableOrderAdapter extends ArrayAdapter<Order> {
	private Context mContext;
	private int mResource;
	private List<Order> mOrders;
	private LayoutInflater inflater;
	private String mOrderLabel;
	private DecimalFormat dcf;
	private String TAG = this.getClass().getSimpleName();

	public PerTableOrderAdapter(Context context, int resource,
			List<Order> objects, String orderLabel) {
		super(context, resource, objects);
		mContext = context;
		mResource = resource;
		mOrders = objects;
		mOrderLabel = orderLabel;
		dcf = new DecimalFormat("#.##");
	}

	static class ViewHolder {
		private TextView txtOrderLabel;
		private TextView txtOrderNumber;
		private TextView txtOrderAmount;
		private ImageButton checkout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder vh;

		if (view == null) {
			inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(mResource, null);
			vh = new ViewHolder();
			vh.txtOrderLabel = (TextView) view
					.findViewById(R.id.lblOrderNoTableOrders);
			vh.txtOrderNumber = (TextView) view
					.findViewById(R.id.txtOrderNoTableOrders);
			vh.txtOrderAmount = (TextView) view
					.findViewById(R.id.txtAmountPayableTableOrder);
			vh.checkout = (ImageButton) view
					.findViewById(R.id.btnCheckoutOrderTableOrders);
			view.setTag(vh);
		} else {
			vh = (ViewHolder) view.getTag();
		}
		
		vh.txtOrderLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		vh.txtOrderNumber.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		vh.txtOrderAmount.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());

		if (mOrders != null) {
			Order order = mOrders.get(position);
			// set the order number and table number
			vh.txtOrderLabel.setText(mOrderLabel);

			if (order.toString() != null
					&& !order.toString().equalsIgnoreCase("0")) {
				vh.txtOrderNumber.setText(order.toString());
			} else if (order.toString() != null
					&& order.toString().equalsIgnoreCase("0")) {
				vh.txtOrderNumber.setText(" - ");
			}

			if (order != null) {
				if (order.getOrderStatus() != null) {
					if (order.getOrderStatus().ordinal() == 2) {
						vh.checkout.setEnabled(false);
						vh.checkout
								.setBackgroundResource(android.R.color.transparent);

						// vh.checkout.setVisibility(View.GONE);
					} else {
						vh.checkout.setVisibility(View.VISIBLE);
						vh.checkout.setEnabled(true);
					}
				} else {
					vh.checkout.setVisibility(View.VISIBLE);
					vh.checkout.setEnabled(true);
				}
			}

			String strTotalAmount = dcf.format(order.getTotalAmount());

			String currancySymbol = LanguageManager.getInstance()
					.getCurrancySymbol();

			String currancyLocation = LanguageManager.getInstance().IS_AFTER();

			if (!currancyLocation.equalsIgnoreCase("TRUE")) {
				strTotalAmount = currancySymbol + " " + strTotalAmount;
			} else {
				strTotalAmount = strTotalAmount + " " + currancySymbol;
			}

			vh.txtOrderAmount.setText(LanguageManager.getInstance().getTotal()
					+ ": " + String.valueOf(strTotalAmount));

			vh.checkout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Global.logd(TAG + " button clicked");

					Order order = mOrders.get(position);

					if (getCountOfItems(order) > 0) {
						if (mContext != null) {
							((TableOrderListActivity) mContext)
									.sendCheckoutRequest(order.getOrderId());
						}
					} else {
						String message = LanguageManager.getInstance()
								.getNoOrderedItems();

						if (mContext != null) {
							((TableOrderListActivity) mContext)
									.showMessage(message);
						}
					}
				}
			});
			vh.checkout.setFocusable(false);
		}

		return view;
	}

	private int getCountOfItems(Order order) {
		int count = 0;
		if (order != null) {
			if (order.getGuests() != null) {
				for (Guest guest : order.getGuests()) {
					if (guest.getOrderedItems() != null) {
						count += guest.getOrderedItems().size();
					}
				}
			}
		}
		return count;
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
