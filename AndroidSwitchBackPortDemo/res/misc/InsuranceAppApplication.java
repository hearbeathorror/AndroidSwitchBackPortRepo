package com.azilen.insuranceapp.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.azilen.insuranceapp.managers.doneactivities.DoneActivitiesManager;
import com.azilen.insuranceapp.managers.network.NetworkManager;
import com.azilen.insuranceapp.managers.network.ServiceUrlManager;
import com.azilen.insuranceapp.managers.plannedevents.PlannedEventsManager;
import com.azilen.insuranceapp.managers.staticdata.StaticDataManager;
import com.azilen.insuranceapp.service.UploadServiceAlarmManager;
import com.azilen.insuranceapp.utils.ExternalStorageManager;
import com.azilen.insuranceapp.utils.Logger;
import com.azilen.insuranceapp.utils.Logger.modules;
import com.azilen.insuranceapp.utils.Prefs;

/**
 * Application class for the app
 * creates and sends the application context
 * and initializes the managers
 * @author dhara.shah
 *
 */
public class InsuranceAppApplication extends Application{
	private static Context context;
	private String TAG = this.getClass().getSimpleName();
	
	@Override
	public void onCreate() {
		super.onCreate();
		InsuranceAppApplication.context = getApplicationContext();
		initLogManager();
		initStaticDataManager();
		initNetworkManager();
		initServiceUrlManager();
		initDoneActivitiesManager();
		initPlannedEventsManager();
		startAppService();
		//refreshSettings();
		
		Logger.i(modules.INSURANCE_APP, TAG, "Application class called!");
	}
	
	/**
	 * Refreshes all logged in information
	 */
	public void refreshSettings() {
		Prefs.addKey(context, Prefs.IS_LOGGED_IN,false);
		Prefs.addKey(context, Prefs.IS_SUPER_LOGIN_CLICKED, false);
		Prefs.addKey(context, Prefs.USER_TYPE, -1);
		Prefs.addKey(context, Prefs.IS_SUPER_LOGIN_CLICKED,false);
	}
	
	private void startAppService() {
		startService(new Intent(context, UploadServiceAlarmManager.class));
	}
	
	/**
	 * Initializes static data manager
	 */
	public void initStaticDataManager() {
		StaticDataManager.getInstance();
	}
	
	/**
	 * Initializes the network manager
	 */
	public void initNetworkManager() {
		NetworkManager.getInstance().init();
	}
	
	/**
	 * Initializes the service url manager
	 */
	public void initServiceUrlManager() {
		ServiceUrlManager.getInstance().init();
	}
	
	/**
	 * Initializes the external storage for logging purposes
	 */
	public void initLogManager() {
		ExternalStorageManager.startWatchingExternalStorage();
	}
	
	/**
	 * Initializes Planned Events Manager 
	 * to perform operations on planned events
	 */
	public void initPlannedEventsManager() {
		PlannedEventsManager.getInstance();
	}
	
	/**
	 * Initializes Done Activities Manager 
	 * to perform operations on done activities
	 */
	public void initDoneActivitiesManager() {
		DoneActivitiesManager.getInstance();
	}
	
	/**
	 * Return Application context
	 * @return
	 */
	public static Context getAppContext() {
		return InsuranceAppApplication.context;
	}

	@Override
	public Context getApplicationContext() {
		return super.getApplicationContext();
	}
}
