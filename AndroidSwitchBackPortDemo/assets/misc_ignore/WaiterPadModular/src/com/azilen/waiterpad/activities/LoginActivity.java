package com.azilen.waiterpad.activities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.asynctask.GetAllDataAsyncTask;
import com.azilen.waiterpad.asynctask.LoginAsyncTask;
import com.azilen.waiterpad.asynctask.LogoutAsyncTask;
import com.azilen.waiterpad.asynctask.RegisterDeviceAsyncTask;
import com.azilen.waiterpad.controls.CommonDialog;
import com.azilen.waiterpad.data.User;
import com.azilen.waiterpad.data.WSRegisterDevice;
import com.azilen.waiterpad.data.WSRegisterDevice.OSType;
import com.azilen.waiterpad.data.WSWaiterPin;
import com.azilen.waiterpad.data.WaiterPadResponse;
import com.azilen.waiterpad.managers.font.FontManager;
import com.azilen.waiterpad.managers.language.LanguageManager;
import com.azilen.waiterpad.managers.language.LanguageParser;
import com.azilen.waiterpad.managers.notification.NotificationManager;
import com.azilen.waiterpad.managers.order.OrderManager;
import com.azilen.waiterpad.utils.Global;
import com.azilen.waiterpad.utils.Prefs;
import com.azilen.waiterpad.utils.Utils;
import com.azilen.waiterpad.utils.Validator;

public class LoginActivity extends BaseActivity {
	private TextView mTxtHeader;
	private TextView mTxtChangeConfig;
	private ImageView mLogo;
	private EditText mEditPin;
	private WSWaiterPin wsWaiterPin;
	private RelativeLayout relativeTopHeader;
	private LoginAsyncTask loginAsyncTask;
	private RegisterDeviceAsyncTask registerDeviceAsyncTask;
	private LogoutAsyncTask logoutAsyncTask;
	private RelativeLayout mRelNotificationCenter;
	private String TAG = this.getClass().getSimpleName();

	private String mFrom;
	private String mAction;

	private String mLangSelection;
	private BroadcastReceiver updateReciver;

	private static Logger logger = LoggerFactory.getLogger(LoginActivity.class);

	private ProgressDialog progressDialog = null;

	public void setProgressDialog(ProgressDialog pg) {
		progressDialog = pg;
	}

	public ProgressDialog getProgressDialog() {
		return progressDialog;
	}

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
	public void onResume() {
		super.onResume();
		
		// changes as on 2nd Jan 2014
		StartActivity.currentIntent = getIntent();
		// changes end here
		
		// changes as on 28th November 2013
		// adding the receiver for notification updates
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
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (updateReciver != null) {
			unregisterReceiver(updateReciver);
			updateReciver = null;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_login);
		setBehindContentView(R.layout.layout_left_menu);

		getSlidingMenu().setSlidingEnabled(false);
		
		// changes as on 2nd Jan 2014
		StartActivity.currentIntent = getIntent();
		// changes end here

		logger.debug("BEGIN -- " + TAG);
		
		if (getIntent().getExtras() != null) {
			if (getIntent().getStringExtra(Global.FROM_ACTIVITY) != null) {
				mFrom = getIntent().getStringExtra(Global.FROM_ACTIVITY);
			}

			if (getIntent().getStringExtra(Global.ACTION) != null) {
				mAction = getIntent().getStringExtra(Global.ACTION);
			}
		}

		mTxtChangeConfig = (TextView) findViewById(R.id.txtConfigSettingsScreen);
		mTxtHeader = (TextView) findViewById(R.id.txtHeader);
		mEditPin = (EditText) findViewById(R.id.editWaiterPin);
		mLogo = (ImageView) findViewById(R.id.logo);
		relativeTopHeader = (RelativeLayout) findViewById(R.id.relativeTopHeader);
		mRelNotificationCenter = (RelativeLayout)findViewById(R.id.relNotificationCentre);

		mTxtHeader.setText(LoginActivity.this.getString(R.string.app_name));
		mTxtHeader.setVisibility(View.GONE);
		mLogo.setVisibility(View.GONE);
		relativeTopHeader.setVisibility(View.GONE);
		mRelNotificationCenter.setVisibility(View.GONE);

		// For setting the underline effect on text in the textview
		SpannableString spanString = new SpannableString(
				LoginActivity.this.getString(R.string.change_config_settings));
		spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
		mTxtChangeConfig.setText(spanString);

		if (mAction != null && mAction.equalsIgnoreCase(Global.FROM_LOCK)) {
			mTxtChangeConfig.setVisibility(View.GONE);
		}

		mLangSelection = Prefs.getKey(Prefs.LANGUAGE_SELECTED);

		if (mAction == null
				|| (mAction != null && !mAction
						.equalsIgnoreCase(Global.FROM_LOCK))) {
			OrderManager.getInstance().clearAllOrders();
		}

		if (mLangSelection != null && mLangSelection.trim().length() > 0) {
			if (Prefs.getKey(mLangSelection).trim().length() > 0) {
				LanguageParser languageParser = new LanguageParser(
						LoginActivity.this, Prefs.getKey(mLangSelection));
				languageParser.parseDocument();
			}
		}

		mEditPin.addTextChangedListener(new MyTextWatcher(LoginActivity.this,
				mEditPin));

		setGuiLabels();
		removeNotificationsOfPreviousUser();
	}
	
	/**
	 * Added on 17th February 2014
	 * Removing all notifications of previous user
	 */
	private void removeNotificationsOfPreviousUser() {
		NotificationManager.getInstance().clearAll();
	}

	@Override
	public void finish() {
		super.finish();
	}

	/**
	 * Receives the response from the login async task And decides if the device
	 * has to be registered or not
	 * 
	 * @param waiterPadResponse
	 */
	public void refreshResponse(WaiterPadResponse waiterPadResponse) {
		try {
			logger.debug(TAG + " -- received response from async task");
			if (waiterPadResponse != null) {
				if (waiterPadResponse.isError()) {

					if (waiterPadResponse.getErrorMessage().equalsIgnoreCase(
							"Invalid pin")) {
						logger.debug(TAG + " -- invalid user pin");
						progressDialog.dismiss();
						progressDialog = null;
						// registerReceiver(LoginActivity.this);

						String message = "";

						message = LanguageManager.getInstance()
								.getInvalidUser();

						showMessageBox(message);

						mEditPin.setText("");

						return;
					} else if (waiterPadResponse.getErrorMessage()
							.equalsIgnoreCase("Device is not registred")) {
						// call registration async task
						WSRegisterDevice wsRegisterDevice = new WSRegisterDevice();
						wsRegisterDevice.setDeviceName(Utils.getDeviceName());
						wsRegisterDevice.setMacAddress(Utils.getMacAddress());
						//wsRegisterDevice.setMacAddress("20");

						wsRegisterDevice.setOsType(OSType.ANDROID.ordinal());

						wsWaiterPin = new WSWaiterPin();
						wsWaiterPin.setWaiterCode(Prefs
								.getKey(Prefs.WAITER_CODE));
						wsWaiterPin.setMacAddress(Utils.getMacAddress());
						//wsWaiterPin.setMacAddress("20");
						wsWaiterPin.setWaiterPin(mEditPin.getText().toString());

						registerDeviceAsyncTask = new RegisterDeviceAsyncTask(
								LoginActivity.this, wsRegisterDevice,
								wsWaiterPin);
						registerDeviceAsyncTask.execute();
					} else {

						progressDialog.dismiss();
						progressDialog = null;
						showMessageBox(waiterPadResponse.getErrorMessage());
						mEditPin.setText("");
					}
					return;
				} else if (!waiterPadResponse.isError()) {
					if (waiterPadResponse.getUser() != null) {
						if (mFrom == null
								|| (mFrom
										.equalsIgnoreCase(Global.FROM_CHANGE_WAITER_CODE) || mFrom
										.equalsIgnoreCase(Global.FROM_SPLASH))) {
							User user = waiterPadResponse.getUser();

							logger.debug(TAG + " -- storing id in sp");

							Prefs.removeKey(LoginActivity.this,
									Prefs.BILL_SPLIT_CHECKED);
							Prefs.addKey(LoginActivity.this, Prefs.WAITER_ID,
									user.getUserId());
							Prefs.addKey(LoginActivity.this, Prefs.WAITER_NAME,
									user.getUserName());
							Prefs.addKey(LoginActivity.this, Prefs.WAITER_CODE,
									user.getUserId());

							logger.debug("END -- " + TAG);

							new GetAllDataAsyncTask(LoginActivity.this, TAG)
									.execute();
						}
					}
				}
			} else {
				logger.debug(TAG + " -- no connection");

				progressDialog.dismiss();
				progressDialog = null;
				// registerReceiver(LoginActivity.this);

				String message = "";

				message = LanguageManager.getInstance().getServerUnreachable();

				showMessageBox(message);
				mEditPin.setText("");
			}
		} catch (Exception e) {
			progressDialog.dismiss();
			progressDialog = null;
			e.printStackTrace();
			logger.debug("ERROR -- " + TAG + " -- " + e.getMessage());
		}
	}

	public void buttonClicked(View v) {
		switch (v.getId()) {
		case R.id.txtConfigSettingsScreen:
			Intent intent = new Intent(LoginActivity.this,
					ConfigureServiceActivity.class);
			intent.putExtra(Global.FROM_ACTIVITY, TAG);
			startActivity(intent);
			// in animation effect
			Global.activityStartAnimationRightToLeft(LoginActivity.this);
			finish();
			break;

		default:
			break;
		}
	}

	/**
	 * Shows the message to the user
	 * 
	 * @param message
	 */
	public void showMessageBox(String message) {
		if (getProgressDialog() != null) {
			getProgressDialog().dismiss();
			setProgressDialog(null);
		}

		CommonDialog dialog = new CommonDialog(LoginActivity.this,
				LanguageManager.getInstance().getAppName(), LanguageManager
						.getInstance().getOk(), message);
		dialog.show();

		mEditPin.setText("");
	}

	class MyTextWatcher implements TextWatcher {
		private EditText mEditText;
		private Context mContext;

		public MyTextWatcher(Context context, EditText editText) {
			mContext = context;
			mEditText = editText;
		}

		@Override
		public void afterTextChanged(Editable s) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			logger.debug("text entered : " + count);

			// Checks if the string entered is more than four characters or not
			if (s.toString().length() >= 4) {

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);

				boolean flagValid = Validator.isValidNumber(s.toString());

				if (flagValid) {
					if (mAction != null
							&& mAction.equalsIgnoreCase(Global.FROM_LOCK)) {
						String userPin = Prefs.getKey(Prefs.USER_PIN);
						if (userPin.trim().length() > 0) {
							if (s.toString().equals(userPin)) {
								finish();
								Global.activityFinishAnimationLeftToRight(LoginActivity.this);
							} else {
								String message = "";

								message = LanguageManager.getInstance()
										.getInvalidUser();

								showMessageBox(message);
							}
						}
					} else {
						String adminPin = "";
						
						if(Prefs.getKey(Prefs.EXIT_PIN).trim().length() > 0) {
							adminPin = Prefs.getKey(Prefs.EXIT_PIN);
						}else {
							adminPin = getString(R.string.exit_pin);
						}
						
						if(s.toString().equals(adminPin)) {
							// changes as on 24th Jan 2014
							// exit WP
							Intent intent = new Intent(LoginActivity.this,
									ExitActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
									| Intent.FLAG_ACTIVITY_CLEAR_TASK
									| Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
							finish();
							Global.activityStartAnimationRightToLeft(LoginActivity.this);
							// changes end here
						}else {
							// changes as on 27th Jan 2014
							// check if the value is a string or an integer
							// then only pass the pin
							
							boolean isNumber = Validator.containsOnlyNumbers(s.toString());						
							
							if(isNumber) {
								WSWaiterPin wsWaiterPin = new WSWaiterPin();
								wsWaiterPin.setWaiterCode(Prefs
										.getKey(Prefs.WAITER_CODE));
								wsWaiterPin.setMacAddress(Utils.getMacAddress());
								// wsWaiterPin.setMacAddress("20");

								logger.debug("value of string :" + s.toString());

								wsWaiterPin.setWaiterPin(s.toString());

								if (mFrom != null) {
									if (mFrom.equalsIgnoreCase(Global.FROM_LOGOUT)) {
										logoutAsyncTask = new LogoutAsyncTask(
												LoginActivity.this, wsWaiterPin,
												Global.FROM_LOGIN);
										logoutAsyncTask.execute();
									} else if (mFrom
											.equalsIgnoreCase(Global.FROM_SPLASH)) {
										// call made to the async task
										loginAsyncTask = new LoginAsyncTask(mContext,
												wsWaiterPin);
										loginAsyncTask.execute();
										
										/*Intent intent = new Intent(LoginActivity.this, 
												OrderRelatedActivity.class);
										startActivity(intent);
										finish();*/
									}
								} else {
									// call made to the async task
									loginAsyncTask = new LoginAsyncTask(mContext,
											wsWaiterPin);
									loginAsyncTask.execute();
									
									/*Intent intent = new Intent(LoginActivity.this, 
											OrderRelatedActivity.class);
									startActivity(intent);
									finish();*/
								}

								logger.debug("Textwatcher async called -- " + TAG
										+ " -- ");

							}else {
								mEditPin.setText("");
								showMessageBox(LanguageManager.getInstance().getEnterValidExitPin());
							}
							// changes end here
						}
					}
				} else {
					String message = "";
					message = LanguageManager.getInstance().getEnterValidPin();
					mEditPin.setText("");
					showMessageBox(message);
				}
			}
		}
	}

	/**
	 * Sets the text on the labels
	 */
	private void setGuiLabels() {
		mEditPin.setHint(LanguageManager.getInstance().getEnterPin());
		mTxtChangeConfig.setText(LanguageManager.getInstance()
				.getChangeConfigSettings());
		
		mEditPin.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtChangeConfig.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
	}

	public void logoutMessage(WaiterPadResponse waiterPadResponse) {
		if (mFrom != null && mFrom.equalsIgnoreCase(Global.FROM_LOGOUT)) {
			String[] keys = new String[] { Prefs.WAITER_ID, Prefs.WAITER_NAME,
					Prefs.WAITER_CODE };
			Prefs.removeKeys(LoginActivity.this, keys);
			finish();
		}

		System.exit(1);
	}
}
