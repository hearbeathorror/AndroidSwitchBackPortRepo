package com.azilen.waiterpad;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.LoggerFactory;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

import com.azilen.waiterpad.activities.SplashActivity;
import com.azilen.waiterpad.db.DbOperations;
import com.azilen.waiterpad.managers.menu.MenuManager;
import com.azilen.waiterpad.managers.network.NetworkManager;
import com.azilen.waiterpad.managers.network.ServiceUrlManager;
import com.azilen.waiterpad.managers.section.SectionManager;
import com.azilen.waiterpad.utils.Utils;

public class WaiterPadApplication extends Application {
	private static Context context;
	public static LoggerContext loggerContext = (LoggerContext) LoggerFactory
			.getILoggerFactory();
	public static Logger LOG;
	//public static Intent currentIntent = null;

	@Override
	public void onCreate() {
		super.onCreate();
		WaiterPadApplication.context = getApplicationContext();
		initLoggingManager();
		initNetworkManager();
		initServiceUrlManager();
		initDatabaseManager();
		initMenuManager();
		initSectionManager();
	}
	
	/**
	 * Return Application context
	 * 
	 * @return
	 */
	public static Context getAppContext() {
		return WaiterPadApplication.context;
	}

	@Override
	public Context getApplicationContext() {
		return super.getApplicationContext();
	}

	/* initialize application manager */
	public void initNetworkManager() {
		NetworkManager.getInstance().init();
	}

	/* initialize Service URL Manager */
	public void initServiceUrlManager() {
		ServiceUrlManager.getInstance().init();
	}

	/* initialize Database Manager */
	public void initDatabaseManager() {
		new DbOperations(context);
	}

	/* initialize Menu Manager */
	public void initMenuManager() {
		MenuManager.getInstance();
	}

	public void initSectionManager() {
		SectionManager.getInstance();
	}
	
	private void initLoggingManager() {
		Utils utils = new Utils(context);
		utils.setLoggingPath();
		LOG.debug("BEGIN -- splash activity");
	}

/*	private void initializeTimer() {
		int timerValue = 5000;
		Timer currentTimerObj = new Timer();
		currentTimerObj.schedule(new TimerTask() {
			public void run() {

				try {
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
				}
				
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
			Log.i("dhara","waiter pad is on ");
			return true;
		}else {
			Log.i("dhara", "waiter pad is not in the front");
			return false;
		}
	}*/
}
