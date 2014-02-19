package com.azilen.waiterpad.adapters;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.data.ModifierMaster;
import com.azilen.waiterpad.managers.font.FontManager;

public class ModifierAdapters extends ArrayAdapter<ModifierMaster> {
	private Context mContext;
	private int mResource;
	private List<ModifierMaster> mModifiers;
	private LayoutInflater inflater;
	private DecimalFormat dcf;
	
	public ModifierAdapters(Context context, int resource,
			List<ModifierMaster> objects) {
		super(context, resource, objects);
		mContext = context;
		mResource = resource;
		mModifiers = objects;
		dcf = new DecimalFormat("#.##");
	}

	static class ViewHolder {
		private TextView txtModifierName;
		private CheckBox checkBoxSelected;
		private TextView txtModifirePrice;
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder vh;
		
		if(view == null) {
			inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(mResource, null);
			vh = new ViewHolder();
			vh.txtModifierName = (TextView)view.findViewById(R.id.txtModifierNameIndividual);
			vh.checkBoxSelected = (CheckBox)view.findViewById(R.id.checkBoxModifier);
			vh.txtModifirePrice=(TextView)view.findViewById(R.id.txtModifirePrice);
			view.setTag(vh);
		}else {
			vh = (ViewHolder)view.getTag();
		}
		
		ModifierMaster modifiers = mModifiers.get(position);
		vh.txtModifierName.setText(modifiers.toString());
		vh.txtModifirePrice.setText(dcf.format(modifiers.getPrice()));
		vh.checkBoxSelected.setChecked(modifiers.isSelected());
		vh.checkBoxSelected.setFocusable(false);
		
		vh.txtModifierName.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		vh.txtModifirePrice.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		
		return view; 
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getCount()
	 */
	@Override
	public int getCount() {
		if(mModifiers != null) {
			return mModifiers.size();
		}else {
			return 0;
		}
	}
}
