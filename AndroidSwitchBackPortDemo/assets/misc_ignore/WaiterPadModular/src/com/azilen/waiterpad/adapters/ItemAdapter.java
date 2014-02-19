package com.azilen.waiterpad.adapters;

import java.text.DecimalFormat;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.db.DBHelper;
import com.azilen.waiterpad.managers.font.FontManager;
import com.azilen.waiterpad.managers.language.LanguageManager;

public class ItemAdapter extends CursorAdapter {
	private Context mContext;
	private DecimalFormat dcf;
	private String TAG = this.getClass().getSimpleName();

	public ItemAdapter(Context context, Cursor c, boolean autoRecovery) {
		super(context, c, autoRecovery);
		mContext = context;
		mCursor = c;

		dcf = new DecimalFormat("#.##");
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView txtMenuItemName = (TextView) view
				.findViewById(R.id.txtMenuItemName);
		TextView txtMenuItemPrice = (TextView) view
				.findViewById(R.id.txtMenuItemPrice);
		LinearLayout lnrRowForItems = (LinearLayout)view.findViewById(R.id.lnrItems);
		
		txtMenuItemName.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		txtMenuItemPrice.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());

		if (cursor != null) {
			String itemName = cursor.getString(cursor
					.getColumnIndex(DBHelper.ITEM_NAME));
			String categoryName = cursor.getString(cursor
					.getColumnIndex(DBHelper.CATEGORY_NAME));
			double itemPrice = cursor.getDouble(cursor
					.getColumnIndex(DBHelper.ITEM_PRICE));
			int isActive = cursor.getInt(cursor
					.getColumnIndex(DBHelper.ITEM_IS_ACTIVATED));
			int isRestricted = cursor.getInt(cursor
					.getColumnIndex(DBHelper.ITEM_IS_RESTRICTED));

			// only an item will have price
			// thus moved all price related processing inside the if condition
			// this condition checks if the row has an item name, if yes, then
			// the row represents an item, else a category
			// changes as on 7th Novemeber 2013
			if (itemName != null && itemName.trim().length() > 0) {
				//TODO: Adding the flag isRestricted
				if (isActive == 0 || isRestricted == 1) {
					txtMenuItemName.setTextColor(mContext.getResources()
							.getColor(android.R.color.darker_gray));
					txtMenuItemPrice.setTextColor(mContext.getResources()
							.getColor(android.R.color.darker_gray));
				} else {
					txtMenuItemName.setTextColor(mContext.getResources()
							.getColor(android.R.color.black));
					txtMenuItemPrice.setTextColor(mContext.getResources()
							.getColor(android.R.color.black));
				}
				
				lnrRowForItems.setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
				lnrRowForItems.setBackgroundResource(R.drawable.list_item_selector);
				txtMenuItemName.setText(itemName);
				txtMenuItemPrice.setVisibility(View.VISIBLE);

				String strItemPrice = dcf.format(itemPrice);

				String currancySymbol = LanguageManager.getInstance()
						.getCurrancySymbol();

				String currancyLocation = LanguageManager.getInstance()
						.IS_AFTER();

				if (!currancyLocation.equalsIgnoreCase("TRUE")) {
					strItemPrice = currancySymbol + " " + strItemPrice;
				} else {
					strItemPrice = strItemPrice + " " + currancySymbol;
				}

				txtMenuItemPrice.setText(strItemPrice);
			} else {
				lnrRowForItems.setBackgroundColor(mContext.getResources().getColor(R.color.category_color));
				lnrRowForItems.setBackgroundResource(R.drawable.list_item_selector_for_categories);
				txtMenuItemName.setTextColor(mContext.getResources()
						.getColor(android.R.color.black));
				txtMenuItemName.setText(categoryName);
				txtMenuItemPrice.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.individual_item_menuitems, parent,
				false);
		bindView(v, context, cursor);
		return v;
	}
}
