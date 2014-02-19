package com.azilen.waiterpad.adapters;

import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.data.Item;
import com.azilen.waiterpad.managers.font.FontManager;

public class MenuExpandableAdapter extends ArrayAdapter<Item> {
	private List<Item> mItemList;
	private String mSearchedFor;
	private String mFrom;
	private LayoutInflater mInflater;
	private Context mContext;
	private Pattern mPattern;
	private int mResource;
	private DecimalFormat dcf;
	private String TAG  = this.getClass().getSimpleName();
	
	/**
	 * Constructor
	 * @param context
	 * @param resource
	 * @param objects
	 * @param searchedFor
	 * @param from
	 */
	public MenuExpandableAdapter(Context context, int resource,
			List<Item> objects, String  searchedFor,String from) {
		super(context, resource, objects);
		mContext = context;
		mResource = resource;
		mItemList = objects;
		mSearchedFor = searchedFor;
		mFrom = from;
		dcf = new DecimalFormat("#.##");
	}

	@Override
	public int getCount() {
		if(mItemList != null) {
			return mItemList.size();
		}else {
			return 0;
		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder vh;
		
		if(view == null) {
			mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = mInflater.inflate(mResource, null);
			vh = new ViewHolder();
			vh.txtItemName = (TextView)view.findViewById(R.id.txtMenuItemName);
			vh.txtItemPrice = (TextView)view.findViewById(R.id.txtMenuItemPrice);
			vh.lnrItems = (LinearLayout)view.findViewById(R.id.lnrItems);
			view.setTag(vh);
		}else {
			vh = (ViewHolder)view.getTag();
		}
		
		if(mItemList != null) {
			Item item = mItemList.get(position);
			String itemName = item.getItemName().toString();

			// hightlight the word that was searched for
			if(mSearchedFor != null && mSearchedFor.trim().length() > 0) {
				setPattern(mSearchedFor,itemName, vh);
			}else {
				vh.txtItemName.setText(itemName.toString());
			}
			
			
			if(!item.isActivated()) {
				vh.txtItemPrice.setTextColor(mContext.getResources().getColor(android.R.color.darker_gray));
				vh.txtItemName.setTextColor(mContext.getResources().getColor(android.R.color.darker_gray));
			}
			
			String strItemPrice = dcf.format(item.getPrice());
			vh.txtItemPrice.setText(strItemPrice);
		}
		
		vh.txtItemName.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		vh.txtItemPrice.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		
		return view;
	}
	
	static class ViewHolder {
		private TextView txtItemName;
		private TextView txtItemPrice;
		private LinearLayout lnrItems;
	}
	
	private void setPattern( String pattern, String wholeText, ViewHolder vh) {
		mPattern = pattern == null ? null : Pattern.compile( pattern );
		updateText(wholeText,vh);
	}
	
	private void updateText(String itemName, ViewHolder vh) {
		Spannable spannable = new SpannableString(itemName);
		Log.i(TAG, "update text called");
		if( mPattern != null ){
			Matcher matcher = mPattern.matcher(itemName);
			while( matcher.find() )
			{
				int start = matcher.start();
				int end = matcher.end();
				ForegroundColorSpan span = new ForegroundColorSpan(mContext.getResources().getColor(R.color.header_end_color));
				spannable.setSpan(span, start, end, Spannable.SPAN_COMPOSING );
			}
		}
		
		vh.txtItemName.setText(spannable.toString());
	}
	
}
