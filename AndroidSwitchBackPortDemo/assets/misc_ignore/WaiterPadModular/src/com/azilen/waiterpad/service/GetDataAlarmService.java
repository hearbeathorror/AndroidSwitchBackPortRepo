package com.azilen.waiterpad.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.managers.settings.SettingsManager;
import com.azilen.waiterpad.utils.Prefs;

/**
 * Alarm service that will schedule fetching of :
 * the data (ordered items updated) and also
 * the menu items and item statuses
 * @author dhara.shah
 *
 */
public class GetDataAlarmService extends Service{
	
	public static boolean isServiceStarted = false;
	public static Intent getAllOrdersIntent;
	private String mWaiterId;
	
	private String TAG = this.getClass().getSimpleName();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int id = (int)System.currentTimeMillis();
		String longMilliSeconds = String.valueOf(SettingsManager.getInstance().getServiceCallTime());
		
		mWaiterId = Prefs.getKey(GetDataAlarmService.this, Prefs.WAITER_ID);
		
		if(mWaiterId != null && mWaiterId.trim().length() > 0) {
			isServiceStarted=true;
			
			if((longMilliSeconds != null && 
					longMilliSeconds.trim().length() <= 0) || 
					longMilliSeconds == null) {
				longMilliSeconds = GetDataAlarmService.this.getString(R.string.time_for_gettings_all_orders);
			}
			
	        // ---- For fetching all the running orders ---- //
	        // Intent created to fetch all the running orders on iiko 
	        getAllOrdersIntent = new Intent(GetDataAlarmService.this, com.azilen.waiterpad.service.GetAllOrdersService.class);
	        getAllOrdersIntent.setAction("GetAllOrdersService");
	        getAllOrdersIntent.setPackage("com.azilen.waiterpad");
	        
	        Log.i(TAG, "alarm for all orders");
	        PendingIntent pendingIntentForAllOrders = PendingIntent.getService(GetDataAlarmService.this, 
	        		id, 
	        		getAllOrdersIntent, 
	        		PendingIntent.FLAG_UPDATE_CURRENT);
	        
	        AlarmManager amForAllOrders = (AlarmManager)getSystemService(ALARM_SERVICE);
	        amForAllOrders.setRepeating(AlarmManager.RTC_WAKEUP, 
	        		System.currentTimeMillis(), 
	        		Long.parseLong(longMilliSeconds), 
	        		pendingIntentForAllOrders);
	        // Everything related to the running orders ends here
	        // Changes as on 6th June 2013
		}
        
        return START_STICKY;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isServiceStarted=false;
	}
}
