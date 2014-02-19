package com.azilen.waiterpad.activities;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.utils.Global;
import com.azilen.waiterpad.utils.Prefs;

/**
 * Added as on 2nd January 2014, 4:45 PM
 * As a hack to override the home button
 * Such that the activity comes back to the front when the user presses the home button
 * @author dhara.shah
 *
 */
public class StartActivity extends Activity{
	public static Intent currentIntent = null;
	private BroadcastReceiver updateReciver = null;
	
	@Override
	public void onBackPressed() {
		// do nothing as the user does not have to exit the application
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_HOME) {
			// do nothing
		}
		return false;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializeTimer();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (updateReciver == null) {
			IntentFilter notification_filter = new IntentFilter();
			notification_filter.addAction("kill");
			
			updateReciver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					if(intent.getAction().equalsIgnoreCase(getString(R.string.kill))) {
						finish();
					}
				}
			};
			registerReceiver(updateReciver, notification_filter);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();

		if (updateReciver != null) {
			unregisterReceiver(updateReciver);
			updateReciver = null;
		}
	}
	
	public void onDestroy() {
		super.onDestroy();
		
		if (updateReciver != null) {
			unregisterReceiver(updateReciver);
			updateReciver = null;
		}
	}
	
	private void initializeTimer() {
		int timerValue = 1000;
		final Timer currentTimerObj = new Timer();
		currentTimerObj.schedule(new TimerTask() {
			public void run() {

				/*try {
					// waiter pad activity is not the top most activity
					// so then move the current activity or intent to the top
					if(!isActivityOnTop()) {
						if(currentIntent != null) {
							Utils utils = new Utils(context);
							utils.startActivityAfterHomePress(currentIntent);
						}else {
							Intent intent = new Intent(context, SplashActivity.class);
							startActivity(intent);
						}
					}
				} catch (Exception e) {
					LOG.debug("BEGIN -- splash activity");
				}*/
				
				// waiter pad activity is not the top most activity
				// so then move the current activity or intent to the top
				// exit is called so cancel timer and finish activity
				if(Prefs.getKey_boolean(StartActivity.this,Prefs.IS_EXIT_CALLED)) {
					Prefs.addKey(StartActivity.this, Prefs.IS_EXIT_CALLED, false);
					currentIntent = null;
					currentTimerObj.cancel();
					finish();
				}else {
					// exit is not called
					if(!isActivityOnTop()) {
						if(currentIntent != null) {
							currentIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(currentIntent);
						}else {
							Intent intent = new Intent(StartActivity.this,
									SplashActivity.class);
							startActivity(intent);
							Global.activityStartAnimationRightToLeft(StartActivity.this);
						}
					}else {
						if(currentIntent == null) {
							Intent intent = new Intent(StartActivity.this, 
									SplashActivity.class);
							startActivity(intent);
							Global.activityStartAnimationRightToLeft(StartActivity.this);
						}
					}
				}
			}
		}, timerValue, 1000);
	}
	
	private boolean isActivityOnTop() {
		ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskInfo = am.getRunningTasks(15);
		ComponentName componenInfo = taskInfo.get(0).topActivity;

		// simply get the package name
		String componentPackageName = componenInfo.getPackageName();
		
		if(componentPackageName.contains(getPackageName())) {
			return true;
		}else {
			return false;
		}
	}
}
