package com.azilen.waiterpad.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.db.DBHelper;
import com.azilen.waiterpad.managers.font.FontManager;

public class CategoryListAdapter extends CursorAdapter {
	private Context mContext;
	private LayoutInflater inflater;
	
	public CategoryListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		mContext = context;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView txtCategoryName = (TextView)view.findViewById(R.id.txtCategoryNameAddItem);
		
		if(cursor != null) {
			String categoryName = cursor.getString(cursor.getColumnIndex(DBHelper.CATEGORY_NAME));
			
			if(categoryName.contains(mContext.getString(R.string.select_category))) {
				txtCategoryName.setTextColor(mContext.getResources().getColor(R.color.header));
			}else {
				txtCategoryName.setTextColor(mContext.getResources().getColor(R.color.black));
			}
			
			txtCategoryName.setText(categoryName);
		}
		
		txtCategoryName.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.individual_category_name, null);
		return view;
	}
	
}

