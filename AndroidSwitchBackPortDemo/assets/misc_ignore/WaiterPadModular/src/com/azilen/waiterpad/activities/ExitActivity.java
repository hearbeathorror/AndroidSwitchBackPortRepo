package com.azilen.waiterpad.activities;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.utils.Prefs;

import android.content.Intent;
import android.os.Bundle;

public class ExitActivity extends BaseActivity{
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if(currentapiVersion <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
			android.os.Process.killProcess(android.os.Process.myPid());
		}
		
		Intent intent = new Intent(getString(R.string.kill));
		sendBroadcast(intent);
		finish();
		
		String[] keys = new String[] { Prefs.WAITER_CODE,
				Prefs.WAITER_ID, Prefs.WAITER_NAME, Prefs.USER_PIN };
		Prefs.removeKeys(ExitActivity.this, keys);
		
		// changes as on 2nd Jan 2014
		// adding this to finish the start Activity 
		// that was created first
		Prefs.addKey(this, Prefs.IS_EXIT_CALLED, true);
		// changes end here
	}
}
