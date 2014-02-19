package com.azilen.waiterpad.adapters;

import java.util.List;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.data.Section;
import com.azilen.waiterpad.managers.font.FontManager;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SectionSpinnerAdapter extends ArrayAdapter<Section> {
	private Context mContext;
	private int mResource;
	private List<Section> mSectionList;
	private LayoutInflater inflater;
	
	private String TAG  = this.getClass().getSimpleName();
	
	public SectionSpinnerAdapter(Context context, int resource,
			List<Section> objects) {
		super(context, resource, objects);
		mContext = context;
		mResource = resource;
		mSectionList = objects;
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		View view = convertView;
		
		if(view == null) {
			inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(mResource, null);
			vh = new ViewHolder();
			vh.txtSectionName = (TextView)view.findViewById(R.id.txtSectionNameSectionSpinner);
			view.setTag(vh);
		}else {
			vh = (ViewHolder) view.getTag();
		}
		
		Section section = mSectionList.get(position);
				
		if(section != null) {
			Log.i(TAG, "inside the adapter : " + section.getSectionName());
			vh.txtSectionName.setText(section.getSectionName().toString());
		}
		
		vh.txtSectionName.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		
		return view;
	}
	
	static class ViewHolder {
		private TextView txtSectionName;
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getCount()
	 */
	@Override
	public int getCount() {
		if(mSectionList != null) {
			return mSectionList.size();
		}else {
			return 0;
		}
	}
	
	
}
