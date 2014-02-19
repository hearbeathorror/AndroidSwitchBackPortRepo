package com.azilen.waiterpad.activities;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.WaiterPadApplication;
import com.azilen.waiterpad.asynctask.GetAllDataAsyncTask;
import com.azilen.waiterpad.asynctask.GetLanguageXmlAsyncTask;
import com.azilen.waiterpad.asynctask.GetOnlyMenuAsyncTask;
import com.azilen.waiterpad.asynctask.LogoutAsyncTask;
import com.azilen.waiterpad.asynctask.SendLogAsyncTask;
import com.azilen.waiterpad.controls.CommonDialog;
import com.azilen.waiterpad.data.Section;
import com.azilen.waiterpad.data.WSWaiterPin;
import com.azilen.waiterpad.data.WaiterPadResponse;
import com.azilen.waiterpad.managers.font.FontManager;
import com.azilen.waiterpad.managers.language.LanguageManager;
import com.azilen.waiterpad.managers.notification.NotificationManager;
import com.azilen.waiterpad.managers.section.SectionManager;
import com.azilen.waiterpad.managers.settings.SettingsManager;
import com.azilen.waiterpad.utils.Global;
import com.azilen.waiterpad.utils.Prefs;
import com.azilen.waiterpad.utils.Utils;
import com.azilen.waiterpad.utils.Validator;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import de.ankri.views.Switch;

public class SettingsActivity extends BaseActivity implements
		OnCheckedChangeListener,
		android.content.DialogInterface.OnClickListener, OnClickListener {
	private String TAG = this.getClass().getSimpleName();

	private LinearLayout lnrSectionBox;
	private LinearLayout lnrLanguageBox;
	private TextView txtHeader;
	private TextView txtSectionSelection;
	private TextView txtLanguage;
	private TextView txtConfigureSettings;
	private TextView txtForceSync;
	private TextView txtSendLog;
	private TextView txtSyncMenu;
	private TextView txtLogout;
	private Button btnCancel;
	private EditText editEnterPinToExit;
	private Dialog builderEnterPin;

	private Button mBtnPlaceOrderLeftMenu;
	private TextView mTxtLeftMenuSettings;
	private TextView mTxtLeftMenuTables;
	private TextView mTxtLeftMenuOrders;
	private TextView mTxtLeftMenuNotifications;
	private TextView mTxtLeftMenuWaiterName;
	private Button mBtnLockLeftMenu;
	private Button mBtnWaiterChangeLeftMenu;
	private TextView mTxtLeftMenuLink;
	private ImageButton imageMenuIcon;

	private Utils mUtils;
	private String langSelection;
	private LanguageManager mLanguageManager;
	private List<Section> lstSection;
	private String sectionId;
	private String sectionName;
	private String waiterName;
	private String previousValue;
	private int selectionVal = 0;
	private int langSelectionVal = 0;
	private List<String> mLanguageList;
	private LruCache<String, Object> sectionCache;
	private String mFrom;
	private String mTimestamp;
	private String mTableId;
	private String mTableNumber;
	private static boolean isSectionSelectionChanged;
	private LogoutAsyncTask logoutAsyncTask;
	private GetLanguageXmlAsyncTask getLanguageXmlAsyncTask;
	private SendLogAsyncTask sendLogAsyncTask;
	private View view;
	
	// changes as on 28th November 2013
	private TextView mTxtNotificationCount;
	private BroadcastReceiver updateReciver = null;
	private RelativeLayout mRelNotificationCentre;
	// changes end here

	// changes as on 6th December 2013
	private Switch mSwitchForNotifications;
	private TextView mTxtNotificationsEnabled;
	// changes end here
	
	@Override
	public void onBackPressed() {
		// unregisterReceiver(SettingsFragment.this);
		if (isSectionSelectionChanged != true) {
			if (mFrom != null
					&& mFrom.equalsIgnoreCase(Global.FROM_HOME_ACTIVITY)) {
				finish();
				Intent intent = new Intent(SettingsActivity.this,
						MyOrderActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				// activity out animation
				Global.activityFinishAnimationLeftToRight(SettingsActivity.this);
			} else if (mFrom != null
					&& mFrom.equalsIgnoreCase(Global.FROM_TABLE_LIST)) {

				if (sectionId != null && sectionId.trim().length() > 0) {
					Prefs.addKey(SettingsActivity.this, Prefs.SECTION_ID,
							sectionId);
					Prefs.addKey(SettingsActivity.this, Prefs.SECTION_NAME,
							sectionName);
				}

				Intent intent = new Intent(SettingsActivity.this,
						TableListActivity.class);
				startActivity(intent);

				// activity out animation
				Global.activityFinishAnimationLeftToRight(SettingsActivity.this);
				finish();
			} else if (mFrom != null
					&& mFrom.equalsIgnoreCase(Global.FROM_ORDER_RELATED)) {
				Intent intent = new Intent(SettingsActivity.this,
						OrderRelatedActivity.class);
				intent.putExtra(Global.TIME_STAMP, mTimestamp);

				if (previousValue != null && previousValue.trim().length() > 0) {
					intent.putExtra(Global.FROM_ACTIVITY, previousValue);
				} else {
					intent.putExtra(Global.FROM_ACTIVITY,
							Global.FROM_TABLE_LIST);
				}
				startActivity(intent);

				// activity out animation
				Global.activityFinishAnimationLeftToRight(SettingsActivity.this);
				finish();
			}

			else if (mFrom != null
					&& mFrom.equalsIgnoreCase(Global.FROM_TABLE_ORDER_LIST)) {
				if (previousValue != null) {
					if (previousValue.equalsIgnoreCase(Global.FROM_TABLE_LIST)) {
						Intent intent = new Intent(SettingsActivity.this,
								TableOrderListActivity.class);
						intent.putExtra(Global.TABLE_ID, mTableId);
						intent.putExtra(Global.TABLE_NUMBER, mTableNumber);
						intent.putExtra(Global.FROM_ACTIVITY, previousValue);
						startActivity(intent);

						// activity out animation
						Global.activityFinishAnimationLeftToRight(SettingsActivity.this);
						finish();
					}
				} else {
					Intent intent = new Intent(SettingsActivity.this,
							TableOrderListActivity.class);
					intent.putExtra(Global.TABLE_ID, mTableId);
					intent.putExtra(Global.TABLE_NUMBER, mTableNumber);
					intent.putExtra(Global.FROM_ACTIVITY,
							Global.FROM_TABLE_LIST);
					startActivity(intent);

					// activity out animation
					Global.activityFinishAnimationLeftToRight(SettingsActivity.this);
					finish();
				}
			} else if (mFrom == null) {
				// activity out animation
				finish();
				Global.activityFinishAnimationLeftToRight(SettingsActivity.this);
				super.onBackPressed();
			}
		} else {
			// section has been changed
			Intent intent = new Intent(SettingsActivity.this,
					TableListActivity.class);
			startActivity(intent);
			// activity out animation
			Global.activityFinishAnimationLeftToRight(SettingsActivity.this);
			finish();
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
	public void onResume() {
		super.onResume();
		Prefs.addKey(SettingsActivity.this, Prefs.MENU_SELECTED,
				Global.SETTINGS);
		setSelectedView();
		
		// changes as on 2nd Jan 2014
		StartActivity.currentIntent = getIntent();
		// changes end here

		if (getSlidingMenu() != null) {
			if (getSlidingMenu().isMenuShowing()) {
				toggle();
			}
		}

		if(mTxtNotificationCount != null) {
			mTxtNotificationCount.setText(String.valueOf(NotificationManager.getInstance().getNotificationOrderCount()));
		}
		
		if (updateReciver == null) {
			IntentFilter notification_filter = new IntentFilter();
			notification_filter.addAction(getString(R.string.broadcast_notification_update));
			notification_filter.addAction(getString(R.string.kill));

			updateReciver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					int counter = 0;
					if(intent.getAction().equalsIgnoreCase(
							getString(R.string.broadcast_notification_update))) {
						if(intent.getExtras() != null) {
							counter = intent.getExtras().getInt(Global.NOTIFICATION_COUNT);
						}
						
						if(mTxtNotificationCount != null) {
							mTxtNotificationCount.setText(String.valueOf(counter));
						}
					}
					
					if(intent.getAction().equalsIgnoreCase(getString(R.string.kill))) {
						finish();
					}
				}
			};
			registerReceiver(updateReciver, notification_filter);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				onBackPressed();
				return true;
				
			case KeyEvent.KEYCODE_HOME:
				
			}
		}
		return false;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return super.onKeyUp(keyCode, event);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Global.logd(TAG + " on create called ");

		setContentView(R.layout.activity_settings);
		mLanguageManager = LanguageManager.getInstance();

		SlidingMenu menu = new SlidingMenu(this);
		menu.setShadowWidthRes(R.dimen.shadow_res);
		menu.setFadeDegree(0.35f);

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.layout_left_menu, null);

		setBehindContentView(view);
		
		// changes as on 2nd Jan 2014
		StartActivity.currentIntent = getIntent();
		// changes end here

		getSlidingMenu().setFadeEnabled(true);
		getSlidingMenu().setFadeDegree(0.35f);
		getSlidingMenu().setSlidingEnabled(true);
		getSlidingMenu().setSelectorEnabled(true);
		getSlidingMenu().setSelected(true);
		getSlidingMenu().setShadowDrawable(R.drawable.drop_shadow_for_menu);
		getSlidingMenu().setBehindOffset((int) getWidth() / 3);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

		// registerReceiver(SettingsFragment.this);

		if (getIntent().getExtras() != null) {
			if (getIntent().getStringExtra(Global.FROM_ACTIVITY) != null) {
				mFrom = getIntent().getStringExtra(Global.FROM_ACTIVITY);
			}

			if (getIntent().getStringExtra(Global.TIME_STAMP) != null) {
				mTimestamp = getIntent().getStringExtra(Global.TIME_STAMP);
			}

			if (getIntent().getStringExtra(Global.PREVIOUS) != null) {
				previousValue = getIntent().getStringExtra(Global.PREVIOUS);
			}

			if (getIntent().getStringExtra(Global.TABLE_ID) != null) {
				mTableId = getIntent().getStringExtra(Global.TABLE_ID);
			}

			if (getIntent().getStringExtra(Global.TABLE_NUMBER) != null) {
				mTableNumber = getIntent().getStringExtra(Global.TABLE_NUMBER);
			}
		}

		txtHeader = (TextView) findViewById(R.id.txtHeader);
		txtSectionSelection = (TextView) findViewById(R.id.txtSectionSelection);
		txtLanguage = (TextView) findViewById(R.id.txtLanguage);
		txtConfigureSettings = (TextView) findViewById(R.id.txtConfigureSettings);
		txtForceSync = (TextView) findViewById(R.id.txtForceSync);
		txtSyncMenu = (TextView) findViewById(R.id.txtSyncMenu);
		txtSendLog = (TextView) findViewById(R.id.txtSendLog);
		txtLogout = (TextView) findViewById(R.id.txtLogout);
		lnrSectionBox = (LinearLayout) findViewById(R.id.lnrSectionSelection);
		mTxtNotificationsEnabled = (TextView)findViewById(R.id.txtNotificationSettings);
		lnrLanguageBox = (LinearLayout) findViewById(R.id.lnrLanguage);
		mTxtNotificationCount = (TextView)findViewById(R.id.txtNotificationNumber);
		mRelNotificationCentre = (RelativeLayout)findViewById(R.id.relNotificationCentre);

		// changes as on 16th july 2013
		mTxtLeftMenuWaiterName = (TextView) view
				.findViewById(R.id.txtWaiterName);
		mTxtLeftMenuOrders = (TextView) view
				.findViewById(R.id.txtOrdersLeftMenu);
		mTxtLeftMenuSettings = (TextView) view
				.findViewById(R.id.txtSettingsLeftMenu);
		mTxtLeftMenuLink = (TextView) view.findViewById(R.id.txtLeftMenuLink);
		mTxtLeftMenuNotifications = (TextView) view
				.findViewById(R.id.txtNotificationsLeftMenu);
		mTxtLeftMenuTables = (TextView) view
				.findViewById(R.id.txtTablesLeftMenu);
		mBtnLockLeftMenu = (Button) view.findViewById(R.id.btnLeftMenuLock);
		mBtnWaiterChangeLeftMenu = (Button) view
				.findViewById(R.id.btnLeftMenuWaiterChange);
		mBtnPlaceOrderLeftMenu = (Button) view
				.findViewById(R.id.btnPlaceOrderLeftMenu);
		imageMenuIcon = (ImageButton) findViewById(R.id.menuIcon);
		
		// changes as on 6th December 2013
		mSwitchForNotifications = (Switch)findViewById(R.id.switchForNotifications);
		
		if(Prefs.getKey_boolean_with_default_true(this, Prefs.ARE_NOTIFICATIONS_ENABLED)) {
			mSwitchForNotifications.setChecked(true);
		}else {
			mSwitchForNotifications.setChecked(false);
		}

		mSwitchForNotifications.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.i("dhara", "isChecked : " + isChecked);
				
				Prefs.addKey(SettingsActivity.this, Prefs.ARE_NOTIFICATIONS_ENABLED, isChecked);
			}
		});
		
		mSwitchForNotifications.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// do nothing
			}
		});
		// changes end here

		mTxtLeftMenuLink.setMovementMethod(LinkMovementMethod.getInstance());
		mTxtLeftMenuLink.setLinksClickable(true);

		mTxtLeftMenuOrders.setOnClickListener(this);
		mTxtLeftMenuSettings.setOnClickListener(this);
		mTxtLeftMenuNotifications.setOnClickListener(this);
		mTxtLeftMenuTables.setOnClickListener(this);
		mBtnPlaceOrderLeftMenu.setOnClickListener(this);
		mBtnLockLeftMenu.setOnClickListener(this);
		mBtnWaiterChangeLeftMenu.setOnClickListener(this);
		imageMenuIcon.setOnClickListener(this);
		mTxtLeftMenuLink.setOnClickListener(this);
		mRelNotificationCentre.setOnClickListener(this);

		sectionCache = SectionManager.getInstance().getSectionCache();
		mUtils = new Utils(SettingsActivity.this);
		sectionId = Prefs.getKey(Prefs.SECTION_ID);
		sectionName = Prefs.getKey(Prefs.SECTION_NAME);
		waiterName = Prefs.getKey(Prefs.WAITER_NAME);

		mTxtLeftMenuWaiterName.setText(waiterName);

		// Get the language selection value from shared preferences
		// show it on the screen
		langSelection = Prefs.getKey(Prefs.LANGUAGE_SELECTED);

		// Gets the list of languages
		mLanguageList = (List<String>) LanguageManager.getInstance()
				.getLanguageCache().get(Global.LANGUAGES);

		Log.i(TAG, "value of mLanguageList " + mLanguageList);

		if (mLanguageList != null && mLanguageList.size() > 0) {
			// set the first value
			if (langSelection != null && langSelection.trim().length() > 0) {
				// Get the language xml file to set the values of the GUI
				String language = mLanguageManager.getStartNode();

				if (!langSelection.equalsIgnoreCase(language)) {
					getLanguageXmlAsyncTask = new GetLanguageXmlAsyncTask(
							SettingsActivity.this, langSelection);
					getLanguageXmlAsyncTask.execute();
				} else {
					setGuiLabels();
				}
			}
		}

		if (sectionId != null && sectionId.trim().length() > 0) {
			if (Prefs.getKey(Prefs.SECTION_NAME) != null
					&& Prefs.getKey(Prefs.SECTION_NAME)
							.trim().length() > 0) {
				txtSectionSelection.setText(mLanguageManager
						.getSelectedSection() + ": " + sectionName);
			}
		}

		txtHeader.setText(mLanguageManager.getSettings());
		
		txtSectionSelection.setOnClickListener(SettingsActivity.this);
		txtLanguage.setOnClickListener(SettingsActivity.this);
		txtConfigureSettings.setOnClickListener(SettingsActivity.this);
		txtForceSync.setOnClickListener(SettingsActivity.this);
		txtSyncMenu.setOnClickListener(SettingsActivity.this);
		txtSendLog.setOnClickListener(SettingsActivity.this);
		txtLogout.setOnClickListener(SettingsActivity.this);
		lnrLanguageBox.setOnClickListener(SettingsActivity.this);
		lnrSectionBox.setOnClickListener(SettingsActivity.this);

		Prefs.addKey(SettingsActivity.this, Prefs.MENU_SELECTED,
				Global.SETTINGS);
		mTxtLeftMenuSettings.setPressed(true);

		setGuiLabels();
	}

	public void setButtonWidth() {
		final int offset = getWidth() / 3;
		Global.logd(TAG + " offset " + offset);

		mBtnLockLeftMenu.post(new Runnable() {

			@Override
			public void run() {
				mBtnLockLeftMenu.setWidth(offset);
				Log.i("Dhara",
						"mBtnLockLeftMenu " + mBtnLockLeftMenu.getWidth());
			}
		});

		mBtnWaiterChangeLeftMenu.post(new Runnable() {

			@Override
			public void run() {
				mBtnWaiterChangeLeftMenu.setWidth(offset);
				Log.i("Dhara", "mBtnChangeWaiterLeftMenu "
						+ mBtnWaiterChangeLeftMenu.getWidth());
			}
		});

	}

	@Override
	public void finish() {
		super.finish();
	}

	/**
	 * Opens a dialog box for language selection
	 */
	private void showLanguageBox() {

		AlertDialog.Builder builder = new AlertDialog.Builder(
				SettingsActivity.this);
		builder.setTitle(SettingsActivity.this
				.getString(R.string.language_selection));

		// Get the languages from the memcache

		if (mLanguageList != null) {
			for (int i = 0; i < mLanguageList.size(); i++) {
				if (langSelection != null && langSelection.trim().length() > 0) {
					if (mLanguageList.get(i).equalsIgnoreCase(langSelection)) {
						langSelectionVal = i;
						break;
					}
				} else if ((langSelection != null && langSelection.trim()
						.length() <= 0) || langSelection == null) {
					/*if (mLanguageList.get(i).equalsIgnoreCase("English")) {
						langSelectionVal = i;
					}*/
					if (mLanguageList.get(i).equalsIgnoreCase("R")) {
						langSelectionVal = i;
					}
				}
			}
		}

		builder.setSingleChoiceItems((String[]) mLanguageList
				.toArray(new String[mLanguageList.size()]), langSelectionVal,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// identify which language was selected
						// then get the xml file for the language
						if (which != -1) {
							String selectedLanguageName = mLanguageList.get(which);
							String currentLanguageInManager = mLanguageManager.getStartNode();
							
							langSelection = selectedLanguageName;

							// store in the shared preferences
							Prefs.addKey(SettingsActivity.this,
									Prefs.LANGUAGE_SELECTED, langSelection);

							if (!currentLanguageInManager.equalsIgnoreCase(selectedLanguageName)) {
								getLanguageXmlAsyncTask = new GetLanguageXmlAsyncTask(
										SettingsActivity.this, langSelection);
								getLanguageXmlAsyncTask.execute();
							} else {
								txtLanguage.setText(mLanguageManager.getSelectedLanguage()
										+ ": "
										+ langSelection);
								refreshGUI(langSelection);
							}

							dialog.dismiss();
						}
					}
				});

		builder.show();
	}

	/**
	 * Shows the dialog box from which to select the sections
	 */
	@SuppressWarnings("unchecked")
	private void showSectionBox() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				SettingsActivity.this);

		String title = mLanguageManager.getSelectSection();
		builder.setTitle(title);

		// get it from the cache
		lstSection = (List<Section>) sectionCache.get(Global.SECTIONS);

		if (lstSection != null) {
			String[] sections = new String[lstSection.size()];

			int i = 0;
			for (Section section : lstSection) {
				// stores the values into the array
				sections[i] = section.getSectionName().toString();
				// checks if the section id is equal to the section id selected
				if (section.getSectionName().toString()
						.equalsIgnoreCase(Prefs.getKey(Prefs.SECTION_NAME))) {
					selectionVal = i;
				}
				i++;
			}

			builder.setSingleChoiceItems(sections, selectionVal, this);
			builder.show();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton button, boolean isChecked) {
		switch (button.getId()) {

		default:
			break;
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// checks if this button is the ok button or not
		if (which == -1) {
			// if just ok is pressed then get the selected first value
			Section section = lstSection.get(selectionVal);
			sectionId = String.valueOf(section.getSectionId());
			sectionName = section.getSectionName().toString();

			Prefs.addKey(SettingsActivity.this, Prefs.SECTION_ID, sectionId);
			Prefs.addKey(SettingsActivity.this, Prefs.SECTION_NAME, sectionName);

			txtSectionSelection.setText(mLanguageManager.getSelectedSection()
					+ ": " + sectionName);

			Global.logd(TAG + " section name: " + sectionName);

			txtSectionSelection.setTextColor(getResources().getColor(
					R.color.black));

			dialog.dismiss();
		} else if (which != -1) {
			isSectionSelectionChanged = true;
			selectionVal = which;
			Section section = lstSection.get(which);
			sectionId = String.valueOf(section.getSectionId());
			sectionName = section.getSectionName().toString();
			Log.i(TAG, "value of sectionId : " + sectionId);

			Prefs.addKey(SettingsActivity.this, Prefs.SECTION_ID, sectionId);
			Prefs.addKey(SettingsActivity.this, Prefs.SECTION_NAME, sectionName);

			refreshData(dialog);
		}
	}

	public void refreshData(DialogInterface dialog) {
		txtSectionSelection.setText(mLanguageManager.getSelectedSection() + ": "
				+ Prefs.getKey(Prefs.SECTION_NAME));

		WaiterPadApplication.LOG.debug("menu obtained .. ");
		dialog.dismiss();
		Intent intent = new Intent(SettingsActivity.this,
				TableListActivity.class);
		startActivity(intent);
		// activity out animation
		Global.activityFinishAnimationLeftToRight(SettingsActivity.this);
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.txtLeftMenuLink:
			Intent browserIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://" + getString(R.string.site)));
			
			// changes as on 2nd Jan 2014
			StartActivity.currentIntent = browserIntent;
			// changes end here
			
			startActivity(browserIntent);
			Global.activityStartAnimationRightToLeft(SettingsActivity.this);
			break;

		// changes as on 15th July 2013
		// added this because of design changes
		case R.id.menuIcon:
			toggle();
			setSelectedView();
			break;

		case R.id.txtLanguage:
			setSelectedView();
			languageCall();
			break;

		case R.id.txtSectionSelection:
			setSelectedView();
			showSectionBox();
			break;

		case R.id.txtConfigureSettings:
			setSelectedView();
			Intent intent = new Intent(SettingsActivity.this,
					ConfigureServiceActivity.class);
			intent.putExtra(Global.FROM_ACTIVITY, TAG);
			startActivity(intent);
			// activity in animation
			Global.activityStartAnimationRightToLeft(SettingsActivity.this);
			break;

		case R.id.txtForceSync:
			setSelectedView();
			// sync all data
			GetAllDataAsyncTask getAllDataAsyncTask = new GetAllDataAsyncTask(
					SettingsActivity.this, Global.SETTINGS);
			getAllDataAsyncTask.execute();
			break;

		case R.id.txtSendLog:
			setSelectedView();
			InputStream is = mUtils.readFromLogFile();

			if (is != null) {
				sendLogAsyncTask = new SendLogAsyncTask(SettingsActivity.this,
						is);
				sendLogAsyncTask.execute();
			}

			break;

		case R.id.txtLogout:
			setSelectedView();
			callDialog(Global.FROM_EXIT);
			break;

		case R.id.lnrSectionSelection:
			showSectionBox();
			setSelectedView();
			break;

		case R.id.lnrLanguage:
			languageCall();
			setSelectedView();
			break;

		case R.id.txtSyncMenu:
			// sync just the menu
			showSyncDialogBox();
			setSelectedView();
			break;

		case R.id.btnPlaceOrderLeftMenu:
			// lead to the table selection list
			Prefs.addKey(Prefs.MENU_SELECTED, Global.TABLES);
			setSelectedView();
			intent = new Intent(this, TableListActivity.class);
			startActivity(intent);
			Global.activityStartAnimationRightToLeft(SettingsActivity.this);
			break;

		case R.id.txtSettingsLeftMenu:
			toggle();
			Prefs.addKey(Prefs.MENU_SELECTED, Global.SETTINGS);
			setSelectedView();
			break;

		case R.id.txtOrdersLeftMenu:
			Prefs.addKey(Prefs.MENU_SELECTED, Global.ORDERS);
			setSelectedView();
			finish();
			intent = new Intent(this, MyOrderActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			Global.activityStartAnimationRightToLeft(SettingsActivity.this);
			break;

		case R.id.txtNotificationsLeftMenu:
			// take to the notification screen
			Prefs.addKey(Prefs.MENU_SELECTED, Global.NOTIFICATIONS);
			setSelectedView();
			intent = new Intent(this, NotificationListActivity.class);
			startActivity(intent);
			Global.activityStartAnimationRightToLeft(SettingsActivity.this);
			break;
			
		case R.id.relNotificationCentre:
			Prefs.addKey(Prefs.MENU_SELECTED, Global.NOTIFICATIONS);
			setSelectedView();
			intent = new Intent(this, NotificationListActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.activity_in_anim,R.anim.activity_out_anim); 
			break;

		case R.id.txtTablesLeftMenu:
			Prefs.addKey(Prefs.MENU_SELECTED, Global.TABLES);
			setSelectedView();
			intent = new Intent(this, TableListActivity.class);
			startActivity(intent);
			Global.activityStartAnimationRightToLeft(SettingsActivity.this);
			break;

		case R.id.btnLeftMenuLock:
			intent = new Intent(this, LoginActivity.class);
			intent.putExtra(Global.ACTION, Global.FROM_LOCK);
			startActivity(intent);
			Global.activityStartAnimationRightToLeft(SettingsActivity.this);
			break;

		case R.id.btnLeftMenuWaiterChange:
			callDialog(Global.FROM_CHANGE_WAITER_CODE);
			break;

		default:
			break;
		}
	}

	private void setSelectedView() {
		String selected = Prefs.getKey(Prefs.MENU_SELECTED);

		if (selected.trim().length() > 0) {
			if (selected.equalsIgnoreCase(Global.NOTIFICATIONS)) {
				mTxtLeftMenuNotifications.setPressed(true);
			} else if (selected.equalsIgnoreCase(Global.TABLES)) {
				mTxtLeftMenuTables.setPressed(true);
			} else if (selected.equalsIgnoreCase(Global.ORDERS)) {
				mTxtLeftMenuOrders.setPressed(true);
			} else if (selected.equalsIgnoreCase(Global.SETTINGS)) {
				mTxtLeftMenuSettings.setPressed(true);
			}
		}
	}

	private void showSyncDialogBox() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				SettingsActivity.this);
		builder.setCancelable(false);

		String appName = mLanguageManager.getAppName();
		String yes = mLanguageManager.getYes();
		String no = mLanguageManager.getNo();
		String message = mLanguageManager.getSyncMenuMessage();

		builder.setTitle(appName);
		builder.setMessage(message);
		builder.setPositiveButton(yes, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				GetOnlyMenuAsyncTask getOnlyMenuAsyncTask = new GetOnlyMenuAsyncTask(
						SettingsActivity.this);
				getOnlyMenuAsyncTask.execute();
			}
		});

		builder.setNegativeButton(no, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.show();

	}

	private void languageCall() {
		if (mLanguageList != null) {
			showLanguageBox();
		} else {
			showMessage("No language files found!");
		}
	}

	public void logoutMessage(WaiterPadResponse result, String fromWhere) {
		if (result != null) {
			if (result.isError()
					&& result.getErrorMessage().toLowerCase()
							.contains("invalid")) {
				String message = mLanguageManager.getInvalidUser();
				showMessage(message);

				if (editEnterPinToExit != null) {
					editEnterPinToExit.setText("");
				}
			} else if(result.isError()
					&& result.getErrorMessage().toLowerCase()
					.contains("device not registred")){
				
				/*String message = mLanguageManager.getDeviceNotRegistered();
				showMessage(message);

				if (editEnterPinToExit != null) {
					editEnterPinToExit.setText("");
				}*/
				
				// only when the valid pin is entered
				// this will be reached
				// so exit user, since device not registered will only happen
				// when iiko is restarted.
				
				// changes as on 22nd Jan 2014
				Intent intent = new Intent(SettingsActivity.this,
						ExitActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_CLEAR_TASK
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				// intent.putExtra(Global.FROM_ACTIVITY,
				// Global.FROM_SPLASH);
				startActivity(intent);
				finish();
				Global.activityStartAnimationRightToLeft(SettingsActivity.this);
				// changes end here
				
			}else {
				if (builderEnterPin != null) {
					builderEnterPin.dismiss();
				}

				// Remove from sharedpreferences
				String[] keys = new String[] { Prefs.WAITER_CODE,
						Prefs.WAITER_ID, Prefs.WAITER_NAME, Prefs.USER_PIN };

				Prefs.removeKeys(SettingsActivity.this, keys);
				Prefs.addKey(SettingsActivity.this, Prefs.LANGUAGE_SELECTED,
						langSelection);

				stopService(SettingsActivity.this);

				Global.logd(TAG + " From : " + fromWhere);
				if (fromWhere != null) {
					if (fromWhere
							.equalsIgnoreCase(Global.FROM_CHANGE_WAITER_CODE)) {
						Intent intent = new Intent(SettingsActivity.this,
								LoginActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
								| Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra(Global.FROM_ACTIVITY,
								Global.FROM_SPLASH);
						startActivity(intent);
						finish();
						Global.activityStartAnimationRightToLeft(SettingsActivity.this);
					} else if (fromWhere.equalsIgnoreCase(Global.FROM_EXIT)) {
						Log.d("Home", "exit else if condition");
						Intent intent = new Intent(SettingsActivity.this,
								ExitActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
								| Intent.FLAG_ACTIVITY_CLEAR_TASK
								| Intent.FLAG_ACTIVITY_NEW_TASK);
						// intent.putExtra(Global.FROM_ACTIVITY,
						// Global.FROM_SPLASH);
						startActivity(intent);
						finish();
						Global.activityStartAnimationRightToLeft(SettingsActivity.this);
					}
				}
			}
		} else {
			// commenting this, as Nick wants the ability to logout when 
			// the server is not reachable
			/*String message = mLanguageManager.getServerUnreachable();
			showMessage(message);

			if (editEnterPinToExit != null) {
				editEnterPinToExit.setText("");
			}*/
			
			// exiting the app since it would have been called only when the correct pin is entered
			// so instead of displaying the message the app will exit normally
			Intent intent = new Intent(SettingsActivity.this,
					ExitActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_CLEAR_TASK
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			// intent.putExtra(Global.FROM_ACTIVITY,
			// Global.FROM_SPLASH);
			startActivity(intent);
			finish();
			Global.activityStartAnimationRightToLeft(SettingsActivity.this);
		}
	}

	private void showMessage(String message) {
		if (message != null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					SettingsActivity.this);
			builder.setCancelable(false);

			String appName = mLanguageManager.getAppName();
			String ok = mLanguageManager.getOk();

			CommonDialog dialog = new CommonDialog(SettingsActivity.this,
					appName, ok, message);
			dialog.show();
		}
	}

	public void refreshGUI(String name) {

		try {
			mUtils.changeKeyBoardSettings();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}

		Global.logd(TAG + " name of string to be passed ! ");

		
		langSelection = name;
		setGuiLabels();
	}

	private void setGuiLabels() {
		// set the values from the xml file
		txtHeader.setText(mLanguageManager.getSettings());
		txtLanguage.setText(mLanguageManager.getSelectedLanguage() + ": "
				+ Prefs.getKey(Prefs.LANGUAGE_SELECTED));
		txtConfigureSettings.setText(mLanguageManager.getBackEndSettings());
		txtForceSync.setText(mLanguageManager.getForceSync());
		txtSendLog.setText(mLanguageManager.getSendLog());
		txtLogout.setText(mLanguageManager.getExit());
		txtSyncMenu.setText(mLanguageManager.getSyncMenu());

		// changes as on 16th july 2013
		mBtnPlaceOrderLeftMenu.setText(mLanguageManager.getPlaceOrder());
		mTxtLeftMenuNotifications.setText(mLanguageManager.getNotifications());
		mTxtLeftMenuOrders.setText(mLanguageManager.getOrders());
		mTxtLeftMenuSettings.setText(mLanguageManager.getSettings());
		mTxtLeftMenuTables.setText(mLanguageManager.getTables());
		mBtnLockLeftMenu.setText(mLanguageManager.getLock());
		mBtnWaiterChangeLeftMenu.setText(mLanguageManager.getChangeWaiter());
		mTxtNotificationsEnabled.setText(mLanguageManager.getNotificationsEnabled());

		txtSectionSelection.setText(mLanguageManager.getSelectedSection() + ": "
				+ sectionName);
		
		
		txtLanguage.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mBtnPlaceOrderLeftMenu.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtLeftMenuNotifications.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtLeftMenuOrders.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtLeftMenuSettings.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtLeftMenuTables.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mBtnLockLeftMenu.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mBtnWaiterChangeLeftMenu.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtNotificationsEnabled.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		
		txtSectionSelection.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		txtHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		txtConfigureSettings.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		txtForceSync.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		txtSendLog.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		txtLogout.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		txtSyncMenu.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtLeftMenuWaiterName.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		
		// changes as on 9th December 2013
		// related to the switchs' on and off text
		mSwitchForNotifications.setTextOn(mLanguageManager.getOn());
		mSwitchForNotifications.setTextOff(mLanguageManager.getOff());
		// changes end here
		
		setButtonWidth();
	}

	private void callDialog(final String fromWhere) {
		builderEnterPin = new Dialog(SettingsActivity.this);

		builderEnterPin.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		builderEnterPin.requestWindowFeature(Window.FEATURE_NO_TITLE);

		builderEnterPin.setCancelable(false);
		builderEnterPin.setTitle(getString(R.string.app_name));
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.pin_screen, null);
		builderEnterPin.setContentView(view);

		TextView txtEnterPinToLogout = (TextView) view
				.findViewById(R.id.txtEnterPinToLogout);

		txtEnterPinToLogout.setText(mLanguageManager.getEnterPinToLogout());
		
		txtEnterPinToLogout.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());

		editEnterPinToExit = (EditText) view.findViewById(R.id.editPinToExit);
		btnCancel = (Button) view.findViewById(R.id.btnCloseLayout);
		
		editEnterPinToExit.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		btnCancel.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editEnterPinToExit.getWindowToken(), 0);

		String 	enterPin = mLanguageManager.getEnterPin();
		String cancel = mLanguageManager.getCancel();

		editEnterPinToExit.setHint(enterPin);
		btnCancel.setText(cancel);

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(
						editEnterPinToExit.getWindowToken(), 0);
				builderEnterPin.dismiss();
			}
		});

		editEnterPinToExit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.toString().length() >= 4) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(
							editEnterPinToExit.getWindowToken(), 0);
					
					String userPin = Prefs.getKey(Prefs.USER_PIN);
					String adminPin = Prefs.getKey(Prefs.EXIT_PIN);
					
					// changes as on 10th December 2013
					if (Validator.isValidNumber(s.toString())) {
						if(fromWhere.equals(Global.FROM_EXIT)) {
							if(s.toString().equalsIgnoreCase(adminPin)) {
								WSWaiterPin wsWaiterPin = new WSWaiterPin();
								wsWaiterPin.setWaiterCode(Prefs
										.getKey(Prefs.WAITER_CODE));
								wsWaiterPin.setMacAddress(Utils.getMacAddress());
								//wsWaiterPin.setMacAddress("20");
								wsWaiterPin.setWaiterPin(userPin);

								if(mUtils.isConnected()) {
									// connected, then only send 
									// logout request to the server
									logoutAsyncTask = new LogoutAsyncTask(
											SettingsActivity.this, wsWaiterPin,
											Global.FROM_SETTINGS,
											fromWhere);
									logoutAsyncTask.execute();
								}else {
									// no internet connectivity
									// exit from the system
									WaiterPadResponse result =new WaiterPadResponse();
									result.setError(false);
									logoutMessage(result, fromWhere);
								}
							}else {
								editEnterPinToExit.setText("");
								String message = mLanguageManager.getEnterValidExitPin();
								showMessage(message);
							}
						}else {
							boolean isNumber = Validator.containsOnlyNumbers(s.toString());
							if(isNumber) {
								WSWaiterPin wsWaiterPin = new WSWaiterPin();
								wsWaiterPin.setWaiterCode(Prefs
										.getKey(Prefs.WAITER_CODE));
								wsWaiterPin.setMacAddress(Utils.getMacAddress());
								wsWaiterPin.setWaiterPin(s.toString());

								logoutAsyncTask = new LogoutAsyncTask(
										SettingsActivity.this, wsWaiterPin,
										Global.FROM_SETTINGS,
										fromWhere);
								logoutAsyncTask.execute();
							}else {
								editEnterPinToExit.setText("");
								String message = mLanguageManager.getEnterValidPin();
								showMessage(message);
							}
						}
					} else {
						String message = mLanguageManager.getEnterValidPin();
						editEnterPinToExit.setText("");
						showMessage(message);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		builderEnterPin.show();
	}
}