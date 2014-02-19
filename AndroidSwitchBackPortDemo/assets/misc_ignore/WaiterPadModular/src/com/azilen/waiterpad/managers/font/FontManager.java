package com.azilen.waiterpad.managers.font;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.WaiterPadApplication;
import com.azilen.waiterpad.managers.settings.SettingsManager;

public class FontManager {
	private static FontManager instance = new FontManager();
	private float floatFontSize;
	
	public FontManager() {
		floatFontSize = WaiterPadApplication.getAppContext().getResources().getDimension(R.dimen.normal_font_size);
	}
	
	/* singleton object */
	public static FontManager getInstance() {
		return instance;
	}
	
	public float getFontSize() {
		String fontSize = SettingsManager.getInstance().getFontSize();
		
		if(fontSize.equals(WaiterPadApplication.getAppContext().getString(R.string.small_font))) {
			floatFontSize = 
					WaiterPadApplication.getAppContext().getResources().getDimension(R.dimen.small_font_size);
		}else if(fontSize.equals(WaiterPadApplication.getAppContext().getString(R.string.medium_font))) {
			floatFontSize = 
					WaiterPadApplication.getAppContext().getResources().getDimension(R.dimen.normal_font_size);
		}else if(fontSize.equals(WaiterPadApplication.getAppContext().getString(R.string.large_font))) {
			floatFontSize = 
					WaiterPadApplication.getAppContext().getResources().getDimension(R.dimen.large_font_size);
		}
		
		return floatFontSize;
	}
}
