package com.azilen.waiterpad.adapters;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.data.ModifierMaster;
import com.azilen.waiterpad.db.DBHelper;
import com.azilen.waiterpad.managers.font.FontManager;
import com.azilen.waiterpad.managers.language.LanguageManager;
import com.azilen.waiterpad.utils.Global;
import com.azilen.waiterpad.utils.search.SearchForModifier;
import com.google.common.collect.Iterables;

public class ModifierCursorAdapter extends CursorAdapter {
	private LayoutInflater inflater;
	private Context mContext;
	private List<ModifierMaster> mAppliedModifiers;
	private List<ModifierMaster> mModifiersList;
	private DecimalFormat dcf;
	private String TAG = this.getClass().getSimpleName();

	public ModifierCursorAdapter(Context context, Cursor c,
			boolean autoRequery, List<ModifierMaster> appliedModifiers) {
		super(context, c, autoRequery);
		mContext = context;
		mAppliedModifiers = appliedModifiers;
		dcf = new DecimalFormat("#.##");
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView txtModifierName = (TextView) view
				.findViewById(R.id.txtModifierNameIndividual);
		CheckBox checkBoxSelected = (CheckBox) view
				.findViewById(R.id.checkBoxModifier);
		TextView txtModifirePrice = (TextView) view
				.findViewById(R.id.txtModifirePrice);

		txtModifierName.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		txtModifirePrice.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		
		if (cursor != null) {
			txtModifierName.setText(cursor.getString(cursor
					.getColumnIndex(DBHelper.MODIFIER_NAME)));

			String strModifierPrice = dcf.format(cursor.getDouble(cursor
					.getColumnIndex(DBHelper.MODIFIER_PRICE)));

			String currancySymbol = LanguageManager.getInstance()
					.getCurrancySymbol();

			String currancyLocation = LanguageManager.getInstance().IS_AFTER();

			if (!currancyLocation.equalsIgnoreCase("TRUE")) {
				strModifierPrice = currancySymbol + " " + strModifierPrice;
			} else {
				strModifierPrice = strModifierPrice + " " + currancySymbol;
			}

			txtModifirePrice.setText(strModifierPrice);
			String id = cursor.getString(cursor
					.getColumnIndex(DBHelper.MODIFIER_ID));
			int isActive = cursor.getInt(cursor
					.getColumnIndex(DBHelper.MODIFIER_IS_ACTIVE));

			if (isActive == 0) {
				checkBoxSelected.setVisibility(View.INVISIBLE);
				checkBoxSelected.setEnabled(false);
				checkBoxSelected.setFocusable(false);
			} else {
				checkBoxSelected.setVisibility(View.VISIBLE);
				checkBoxSelected.setEnabled(true);
				if (mAppliedModifiers != null && mAppliedModifiers.size() > 0) {
					ModifierMaster modifierApplied = Iterables.find(
							mAppliedModifiers, new SearchForModifier(id), null);

					if (modifierApplied != null) {
						checkBoxSelected.setChecked(true);
						checkBoxSelected.setFocusable(false);
					} else {
						checkBoxSelected.setChecked(false);
						checkBoxSelected.setFocusable(false);
					}
				} else {
					checkBoxSelected.setChecked(false);
					checkBoxSelected.setFocusable(false);
				}

				if (mModifiersList != null) {
					ModifierMaster modifierApplied = Iterables.find(
							mModifiersList, new SearchForModifier(id), null);

					Global.logd(TAG + " modifiers if condition!");

					if (modifierApplied != null) {
						checkBoxSelected.setChecked(true);
						checkBoxSelected.setFocusable(false);
					} else {
						checkBoxSelected.setChecked(false);
						checkBoxSelected.setFocusable(false);
					}
				}
			}
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.item_individual_modifiers, null);
		return view;
	}

	public void setList(List<ModifierMaster> modifiersList) {
		mModifiersList = modifiersList;
	}
}
