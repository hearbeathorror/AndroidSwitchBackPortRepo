package com.azilen.waiterpad.activities;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.WaiterPadApplication;
import com.azilen.waiterpad.managers.network.ServiceUrlManager;
import com.azilen.waiterpad.utils.Global;
import com.azilen.waiterpad.utils.Prefs;
import com.azilen.waiterpad.utils.Utils;

/**
 * Displays the splash screen to the user
 * 
 * @author dhara.shah
 * 
 */
public class SplashActivity extends BaseActivity {
	private Thread mSplashThread;
	private int mCounter = 0;

	private String TAG = this.getClass().getSimpleName();

	@Override
	public void onBackPressed() {
		// do nothing as the user does not have to exit the application
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			// do nothing
		}
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		setBehindContentView(R.layout.layout_left_menu);
		getSlidingMenu().setSlidingEnabled(false);

		/*
		 * Added by Chintan Rathod Check whether any language is set or not. If
		 * not, make default language Russian
		 */
		if (Prefs.getKey(Prefs.LANGUAGE_SELECTED).equals("")) {
			Prefs.addKey(Prefs.LANGUAGE_SELECTED, Prefs.RUSSIAN_LANGUAGE);

			try {
				Class amnClass = Class
						.forName("android.app.ActivityManagerNative");
				Object amn = null;
				Configuration config = null;

				// amn = ActivityManagerNative.getDefault();
				Method methodGetDefault = amnClass.getMethod("getDefault");
				methodGetDefault.setAccessible(true);
				amn = methodGetDefault.invoke(amnClass);

				// config = amn.getConfiguration();
				Method methodGetConfiguration = amnClass
						.getMethod("getConfiguration");
				methodGetConfiguration.setAccessible(true);
				config = (Configuration) methodGetConfiguration.invoke(amn);

				// config.userSetLocale = true;
				Class configClass = config.getClass();
				Field f = configClass.getField("userSetLocale");
				f.setBoolean(config, true);

				// set the locale to the new value
				config.locale = new Locale("ru");

				// amn.updateConfiguration(config);
				Method methodUpdateConfiguration = amnClass.getMethod(
						"updateConfiguration", Configuration.class);
				methodUpdateConfiguration.setAccessible(true);
				methodUpdateConfiguration.invoke(amn, config);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		/* End of modification */

		try {
			mSplashThread = new Thread() {
				@Override
				public void run() {
					while (mCounter < 1500) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						mCounter += 100;
					}

					if (Utils.getMacAddress() == null
							|| Utils.getMacAddress().trim().length() <= 0) {
						// There is no wifi connected
						SplashActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(SplashActivity.this,
										"You are not connected to wifi",
										Toast.LENGTH_LONG).show();
								return;
							}
						});
					}

					if (Prefs.getKey(Prefs.IP_ADDRESS).trim().length() > 0
							&& Prefs.getKey(Prefs.PORT_ADDRESS).trim().length() > 0) {

						ServiceUrlManager.getInstance().setBaseURL(
								Prefs.getKey(Prefs.IP_ADDRESS).trim(),
								Prefs.getKey(Prefs.PORT_ADDRESS).trim());

						if (Prefs.getKey(Prefs.WAITER_ID).trim().length() > 0) {
							// redirect to the dashboard activity
							// user has already logged in and not logged out
							Intent intent = new Intent(SplashActivity.this,
									LoginActivity.class);
							intent.putExtra(Global.FROM_ACTIVITY,
									Global.FROM_SPLASH);
							startActivity(intent);
							WaiterPadApplication.LOG
									.debug("END -- splash activity");
							// logger.debug("END -- splash activity");
							finish();
							Global.activityStartAnimationRightToLeft(SplashActivity.this);
						} else {
							// redirect to the login screen
							// user has logged out or cleared the memory
							Intent intent = new Intent(SplashActivity.this,
									LoginActivity.class);
							intent.putExtra(Global.FROM_ACTIVITY,
									Global.FROM_SPLASH);
							startActivity(intent);
							WaiterPadApplication.LOG
									.debug("END -- splash activity");
							finish();
							Global.activityStartAnimationRightToLeft(SplashActivity.this);
						}
					} else {
						// configuration not present so redirect to the config
						// screen
						Intent intent = new Intent(SplashActivity.this,
								ConfigureServiceActivity.class);
						intent.putExtra(Global.FROM_ACTIVITY, TAG);
						startActivity(intent);
						WaiterPadApplication.LOG
								.debug("END -- splash activity");
						finish();
						Global.activityStartAnimationRightToLeft(SplashActivity.this);
					}
				}
			};

			mSplashThread.start();
		} catch (Exception e) {
			e.printStackTrace();
			WaiterPadApplication.LOG.error("ERROR -- splash activity : "
					+ e.getMessage());
		}
	}
}
