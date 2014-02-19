package com.azilen.waiterpad.activities;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.azilen.waiterpad.WaiterPadApplication;
import com.azilen.waiterpad.service.GetAllOrdersService;
import com.azilen.waiterpad.service.GetDataAlarmService;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class BaseActivity extends SlidingActivity {
	public static Intent waiterPadDataServiceIntent;
	private static int width;
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		try {
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			if (currentapiVersion < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH){
				this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD); 
			}
		}catch(IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD); 
		super.onCreate(savedInstanceState);
		
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		KeyguardManager keyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
		KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
		lock.disableKeyguard();
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		width = metrics.widthPixels;
		
		Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(
				this));
	}

	public void stopService(Context context) {
		final PackageManager packageManager = context.getPackageManager();

		ComponentName componentName = new ComponentName(
				BaseActivity.this.getPackageName(),
				GetAllOrdersService.class.getName());
		
		packageManager.setComponentEnabledSetting(componentName,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);
		
		componentName = new ComponentName(
				BaseActivity.this.getPackageName(),
				GetDataAlarmService.class.getName());

		packageManager.setComponentEnabledSetting(componentName,
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				PackageManager.DONT_KILL_APP);

		if (GetDataAlarmService.getAllOrdersIntent != null) {
			stopService(GetDataAlarmService.getAllOrdersIntent);
		}
	}

	public void startServices(Context context) {
		final PackageManager packageManager = context.getPackageManager();

		ComponentName componentName = new ComponentName(
				BaseActivity.this.getPackageName(),
				GetAllOrdersService.class.getName());
		
		packageManager.setComponentEnabledSetting(componentName,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);
		
		componentName = new ComponentName(
				BaseActivity.this.getPackageName(),
				GetDataAlarmService.class.getName());
		
		packageManager.setComponentEnabledSetting(componentName,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);
	}

	public int getWidth() {
		return width;
	}
	
	public class DefaultExceptionHandler implements UncaughtExceptionHandler {
		private Thread.UncaughtExceptionHandler defaultUEH;
		
		public DefaultExceptionHandler(Context context) {
			this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		}
		
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			StackTraceElement[] arr = ex.getStackTrace();
			String threadString =thread.toString();
			StringBuffer strReportBuffer = new StringBuffer();
			strReportBuffer.append(ex.toString()+"\n\n");
			strReportBuffer.append("--------- Stack trace ---------\n\n"+ threadString);
			for (int i=0; i<arr.length; i++) {
				strReportBuffer.append("    "+arr[i].toString()+"\n");
			}
			strReportBuffer.append("-------------------------------\n\n");

			// If the exception was thrown in a background thread inside
			// AsyncTask, then the actual exception can be found with getCause
			strReportBuffer.append("--------- Cause ---------\n\n");
			Throwable cause = ex.getCause();
			if(cause != null) {
				strReportBuffer.append(cause.toString() + "\n\n");
				arr = cause.getStackTrace();
				for (int i=0; i<arr.length; i++){
					strReportBuffer.append( "    "+arr[i].toString()+"\n");
				}
			}
			strReportBuffer.append("-------------------------------\n\n");

			WaiterPadApplication.LOG.debug(strReportBuffer.toString());

			defaultUEH.uncaughtException(thread, ex);
			
			Intent intent = new Intent(BaseActivity.this, ExitActivity.class);
			startActivity(intent);
		}
	}
	
	public static void startActivityAfterHomePress(Intent intentToStart) {
		WaiterPadApplication.getAppContext().startActivity(intentToStart);
	}
}
