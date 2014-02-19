package com.azilen.waiterpad.adapters;

import java.util.List;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.data.Guest;
import com.azilen.waiterpad.managers.font.FontManager;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GuestAdapter extends ArrayAdapter<Guest>{
	private Context mContext;
	private List<Guest> mGuests;
	private int mResource;
	private LayoutInflater mInflater;
	
	public GuestAdapter(Context context, int resource,
			List<Guest> objects) {
		super(context, resource, objects);
		mContext = context;
		mGuests = objects;
		mResource = resource;
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getCount()
	 */
	@Override
	public int getCount() {
		if(mGuests != null) {
			return mGuests.size();
		}else {
			return 0;
		}
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder vh = null;
		
		if(view == null) {
			mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = mInflater.inflate(mResource, parent, false);
			
			vh = new ViewHolder();
			vh.txtGuestNameAddItem = (TextView)view.findViewById(R.id.txtGuestNameAddItem);
			view.setTag(vh);
		}else {
			vh = (ViewHolder)view.getTag();
		}
		
		Guest guest = mGuests.get(position);
		
		vh.txtGuestNameAddItem.setText(guest.getGuestName().toString());
		vh.txtGuestNameAddItem.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		
		return view;
	}
	
	static class ViewHolder {
		private TextView txtGuestNameAddItem;
	}
}
