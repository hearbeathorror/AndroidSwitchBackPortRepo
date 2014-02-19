package com.azilen.waiterpad.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.WaiterPadApplication;
import com.azilen.waiterpad.asynctask.CheckServerCommunicationAsyncTask;
import com.azilen.waiterpad.controls.CommonDialog;
import com.azilen.waiterpad.data.RegistrationStatus;
import com.azilen.waiterpad.data.ServiceConfig;
import com.azilen.waiterpad.data.WSRegisterDevice;
import com.azilen.waiterpad.data.WSRegisterDevice.OSType;
import com.azilen.waiterpad.managers.font.FontManager;
import com.azilen.waiterpad.managers.language.LanguageManager;
import com.azilen.waiterpad.managers.language.LanguageParser;
import com.azilen.waiterpad.utils.Global;
import com.azilen.waiterpad.utils.Prefs;
import com.azilen.waiterpad.utils.Utils;
import com.azilen.waiterpad.utils.Validator;

/**
 * Displays a GUI for the waiter to configure the IP Address and also the port
 * address
 * 
 * @author dhara.shah
 * 
 */
public class ConfigureServiceActivity extends BaseActivity {
	private TextView mTxtHeader;
	private EditText mEditIpAddress;
	private EditText mEditPortAddress;
	private TextView mTxtSettingsLabel;
	private TextView mTxtIPLabel;
	private TextView mTxtPortLabel;
	private Button mBtnCancelService;
	private Button mBtnSaveService;
	private RelativeLayout mRelativeTopHeader;
	private RelativeLayout mRelNotificationCount;
	private ImageView mLogo;

	private String mIpAddress;
	private String mPortAddress;

	private String from;

	private CheckServerCommunicationAsyncTask checkServerCommunicationAsyncTask;
	private String TAG = this.getClass().getSimpleName();

	private String mLangSelection;

	@Override
	public void onBackPressed() {
		if (from.equalsIgnoreCase(Global.FROM_LOGIN_ACTIVITY)) {
			Intent intent = new Intent(ConfigureServiceActivity.this,
					LoginActivity.class);
			startActivity(intent);
			finish();
		} else if (from.equalsIgnoreCase(Global.FROM_SETTINGS_FRAGMENT)) {
			super.onBackPressed();
		} else {
			// do nothing
		}

		Global.activityFinishAnimationLeftToRight(ConfigureServiceActivity.this);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_HOME) {
			// do nothing
		}else if(keyCode == KeyEvent.KEYCODE_BACK) {
			onBackPressed();
			return true;
		}
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		setContentView(R.layout.activity_configure_service);
		setBehindContentView(R.layout.layout_left_menu);

		getSlidingMenu().setSlidingEnabled(false);

		WaiterPadApplication.LOG.debug("BEGIN -- " + TAG);

		if (getIntent().getExtras() != null) {
			from = getIntent().getStringExtra(Global.FROM_ACTIVITY);
		}
		
		// changes as on 2nd Jan 2014
		StartActivity.currentIntent = getIntent();
		// changes end here

		mTxtHeader = (TextView) findViewById(R.id.txtHeader);
		mTxtHeader.setText(ConfigureServiceActivity.this
				.getString(R.string.app_name));

		mRelativeTopHeader = (RelativeLayout) findViewById(R.id.relativeTopHeader);
		mEditIpAddress = (EditText) findViewById(R.id.editIpAddress);
		mEditPortAddress = (EditText) findViewById(R.id.editPortNumber);
		mRelNotificationCount = (RelativeLayout)findViewById(R.id.relNotificationCentre);
		mRelNotificationCount.setVisibility(View.GONE);

		mLogo = (ImageView) findViewById(R.id.logo);

		mTxtIPLabel = (TextView) findViewById(R.id.lblIP);
		mTxtPortLabel = (TextView) findViewById(R.id.lblPort);
		mTxtSettingsLabel = (TextView) findViewById(R.id.lblSettingsLabel);
		mBtnCancelService = (Button) findViewById(R.id.btnCancelService);
		mBtnSaveService = (Button) findViewById(R.id.btnSaveService);

		// mUtils = new Utils(ConfigureServiceActivity.this);
		// memCache = Utils.getInstance();
		// memCache = LanguageManager.getInstance().getLanguageCache();

		mLangSelection = Prefs.getKey(Prefs.LANGUAGE_SELECTED);

		if (mLangSelection != null && mLangSelection.trim().length() > 0) {
			// mLanguageXml = (LanguageXml) memCache.get(mLangSelection);
		}

		mIpAddress = Prefs.getKey(Prefs.IP_ADDRESS);
		mPortAddress = Prefs.getKey(Prefs.PORT_ADDRESS);

		if (mIpAddress != null && mIpAddress.trim().length() > 0) {
			mEditIpAddress.setText(mIpAddress);
		}

		if (mPortAddress != null && mPortAddress.trim().length() > 0) {
			mEditPortAddress.setText(mPortAddress);
		}

		mLogo.setVisibility(View.GONE);
		mRelativeTopHeader.setVisibility(View.GONE);

		setGuiLabels();
	}

	@Override
	public void finish() {
		super.finish();
	}

	public void buttonClicked(View v) {
		switch (v.getId()) {
		case R.id.btnSaveService:
			mIpAddress = mEditIpAddress.getText().toString();
			mPortAddress = mEditPortAddress.getText().toString();

			String message = validateInput();

			if (message.trim().length() > 0) {

				showMessageBox(ConfigureServiceActivity.this, message);
			} else {
				if (Validator.isValidIPAddress(mIpAddress)) {
					// save the values in the config file once
					// it is tested that the connection has been established.
					ServiceConfig serviceConfig = new ServiceConfig();
					serviceConfig.setIpAddress(mIpAddress);
					serviceConfig.setPortAddress(mPortAddress);

					WSRegisterDevice wsRegisterDevice = new WSRegisterDevice();
					wsRegisterDevice.setDeviceName(Utils.getDeviceName());
					wsRegisterDevice.setMacAddress(Utils.getMacAddress());

					// wsRegisterDevice.setDeviceName("Samsung Galaxy Tab");
					//wsRegisterDevice.setMacAddress("20");
					wsRegisterDevice.setOsType(OSType.ANDROID.ordinal());

					checkServerCommunicationAsyncTask = new CheckServerCommunicationAsyncTask(
							ConfigureServiceActivity.this, serviceConfig,
							wsRegisterDevice);
					checkServerCommunicationAsyncTask.execute();
					
					/*Prefs.addKey(Prefs.IP_ADDRESS, mIpAddress);
					Prefs.addKey(Prefs.PORT_ADDRESS, mPortAddress);
					
					Intent intent = new Intent(this, LoginActivity.class);
					startActivity(intent);
					finish();*/

					WaiterPadApplication.LOG.debug("ASYNC TASK CALLED -- " + TAG);
				} else {
					String errMessage = LanguageManager.getInstance()
							.getEnterValidIp();

					/*
					 * if (mLanguageXml != null) { errMessage =
					 * mLanguageXml.getEnterValidIp(); } else { errMessage =
					 * ConfigureServiceActivity.this
					 * .getString(R.string.valid_ip); }
					 */
					showMessageBox(ConfigureServiceActivity.this, errMessage);
				}
			}

			break;

		case R.id.btnCancelService:
			// if from the splash screen meaning that there was no config found
			if (from.equalsIgnoreCase(Global.FROM_SPLASH_ACTIVITY)) {
				mEditIpAddress.setText("");
				mEditIpAddress.setHint(ConfigureServiceActivity.this
						.getString(R.string.ip_address_hint));

				mEditPortAddress.setText("");
			} else if (from.equalsIgnoreCase(Global.FROM_LOGIN_ACTIVITY)) {
				Intent intent = new Intent(ConfigureServiceActivity.this,
						LoginActivity.class);
				startActivity(intent);
				Global.activityFinishAnimationLeftToRight(ConfigureServiceActivity.this);
				finish();
			} else {
				// finish
				finish();
				Global.activityFinishAnimationLeftToRight(ConfigureServiceActivity.this);
			}

			break;

		default:
			break;
		}
	}

	private void showMessageBox(
			ConfigureServiceActivity configureServiceActivity, String message) {
		CommonDialog dialog = new CommonDialog(ConfigureServiceActivity.this,
				LanguageManager.getInstance().getAppName(), LanguageManager
						.getInstance().getOk(), message);
		dialog.show();
	}

	/**
	 * Checks if the ip address and port address are valid or empty :- Performs
	 * validation
	 * 
	 * @return
	 */
	private String validateInput() {
		if (mIpAddress.trim().length() <= 0
				&& mPortAddress.trim().length() <= 0) {
			return LanguageManager.getInstance().getEnterAllValues();
		}

		if (mIpAddress.trim().length() <= 0 && mPortAddress.trim().length() > 0) {
			return LanguageManager.getInstance().getEnterIp();
		}

		// added to check the ip is valid or not before port validation checking
		if (mIpAddress.trim().length() > 0) {
			if (!Validator.isValidIPAddress(mIpAddress)) {
				return LanguageManager.getInstance().getEnterValidIp();
			}
		}

		if (mIpAddress.trim().length() > 0 && mPortAddress.trim().length() <= 0) {
			return LanguageManager.getInstance().getEnterPort();
		}

		if (mPortAddress != null && mPortAddress.trim().length() > 0
				&& mPortAddress.length() <= 5) {
			if (!Validator.isValidPort(mPortAddress)) {
				return LanguageManager.getInstance().getEnterValidPort();
			}
		}

		return "";
	}

	/**
	 * Processes data received from inputStream.
	 * 
	 * @param inputStream
	 */
	public void processNext(RegistrationStatus registrationStatus) {
		if (registrationStatus == null) {
			WaiterPadApplication.LOG.debug(TAG + " server cannot be reached ");

			showMessageBox(ConfigureServiceActivity.this, LanguageManager
					.getInstance().getServerUnreachable());
		} else {
			// parse the data by converting the inputStream to a reader
			// if registered then redirect to the login screen else
			// register and then redirect
			Prefs.addKey(Prefs.IP_ADDRESS, mIpAddress);
			Prefs.addKey(Prefs.PORT_ADDRESS, mPortAddress);

			if (registrationStatus.isRegisteredSuccessfully()) {
				// unregisterReceiver(ConfigureServiceActivity.this);
				// redirect the user to the login page since the user is
				// registered
				Intent intent = new Intent(ConfigureServiceActivity.this,
						LoginActivity.class);
				startActivity(intent);
				Global.activityFinishAnimationLeftToRight(ConfigureServiceActivity.this);
				finish();
			} else if (!registrationStatus.isRegisteredSuccessfully()) {
				switch (registrationStatus.getResponseCode()) {
				case 100:
					showMessageBox(ConfigureServiceActivity.this,
							registrationStatus.getResponseErrorMessage());
					break;

				case 102:
					// device is already registered
					// redirect to the login page
					// unregisterReceiver(ConfigureServiceActivity.this);
					Intent intent = new Intent(ConfigureServiceActivity.this,
							LoginActivity.class);
					startActivity(intent);
					Global.activityFinishAnimationLeftToRight(ConfigureServiceActivity.this);
					finish();
					break;

				case 103:
					showMessageBox(ConfigureServiceActivity.this,
							ConfigureServiceActivity.this
									.getString(R.string.LICENCE_LIMIT_EXCEEDED));
					break;

				case 104:
					showMessageBox(ConfigureServiceActivity.this,
							registrationStatus.getResponseErrorMessage());
					break;

				default:
					break;
				}
			}
		}
	}

	/**
	 * Sets the text on the labels and other views from the language xml in the
	 * memory else default will be displayed
	 */
	private void setGuiLabels() {
		// if the language file exists then
		// only set the label texts
		// else leave default

		if (from != null) {
			if (Prefs.getKey(mLangSelection).trim().length() > 0) {
				LanguageParser languageParser = new LanguageParser(
						ConfigureServiceActivity.this,
						Prefs.getKey(mLangSelection));
				languageParser.parseDocument();
			}
		}

		mTxtIPLabel.setText(LanguageManager.getInstance().getIpAddress()
				+ " *:");
		mTxtPortLabel.setText(LanguageManager.getInstance().getPortAddress()
				+ " *:");
		mTxtSettingsLabel.setText(LanguageManager.getInstance()
				.getConfigureSettingsLabel());
		mBtnCancelService.setText(LanguageManager.getInstance().getCancel());
		mBtnSaveService.setText(LanguageManager.getInstance().getSave());

		mTxtIPLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtPortLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mEditPortAddress.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mEditIpAddress.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtSettingsLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mBtnCancelService.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mBtnSaveService.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
	}
}
