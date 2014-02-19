package com.azilen.waiterpad.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.map.MultiKeyMap;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.WaiterPadApplication;
import com.azilen.waiterpad.adapters.SectionSpinnerAdapter;
import com.azilen.waiterpad.adapters.TableListAdapter;
import com.azilen.waiterpad.asynctask.GetCurrentOrderListAsyncTask;
import com.azilen.waiterpad.asynctask.LogoutAsyncTask;
import com.azilen.waiterpad.controls.CommonDialog;
import com.azilen.waiterpad.data.Order;
import com.azilen.waiterpad.data.OrderList;
import com.azilen.waiterpad.data.Section;
import com.azilen.waiterpad.data.Tables;
import com.azilen.waiterpad.data.WSWaiterPin;
import com.azilen.waiterpad.data.WaiterPadResponse;
import com.azilen.waiterpad.managers.font.FontManager;
import com.azilen.waiterpad.managers.language.LanguageManager;
import com.azilen.waiterpad.managers.notification.NotificationManager;
import com.azilen.waiterpad.managers.order.OrderManager;
import com.azilen.waiterpad.managers.section.SectionManager;
import com.azilen.waiterpad.utils.Global;
import com.azilen.waiterpad.utils.Prefs;
import com.azilen.waiterpad.utils.Utils;
import com.azilen.waiterpad.utils.Validator;
import com.azilen.waiterpad.utils.search.SearchForSection;
import com.google.common.collect.Iterables;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class TableListActivity extends BaseActivity implements
		OnItemClickListener, OnItemSelectedListener, OnClickListener {
	private GridView gridView;
	private TextView mTxtHeader;
	private TextView mTxtSectionName;

	private Button mBtnPlaceOrderLeftMenu;
	private TextView mTxtLeftMenuSettings;
	private TextView mTxtLeftMenuTables;
	private TextView mTxtLeftMenuOrders;
	private TextView mTxtLeftMenuNotifications;
	private TextView mTxtLeftMenuWaiterName;
	private Button mBtnLockLeftMenu;
	private Button mBtnWaiterChangeLeftMenu;
	private TextView mTxtLeftMenuLink;
	private ImageButton mBtnForceSyncTables;

	private Spinner mSectionSpinner;

	private List<Tables> tableList;
	private String sectionId;
	private String waiterId;
	private String waiterName;
	private String mTabSelected;
	private HashMap<String, List<Tables>> tableHashMap;
	private List<Section> mLstSections;
	private LruCache<String, Object> sectionCache;
	private LruCache<String, Object> orderCache;
	private MultiKeyMap orderPerTableMap;
	private LanguageManager mLanguageManager;

	private MultiKeyMap mOrderPerTable;
	private SectionSpinnerAdapter sectionSpinnerAdapter;
	private String mFrom;
	private int mSelectedSectionPos;
	private ImageButton imageMenuIcon;
	private View view;

	private LogoutAsyncTask logoutAsyncTask;

	private EditText editEnterPinToExit;
	private Dialog builderEnterPin;
	private Button btnCancel;

	private String TAG = this.getClass().getSimpleName();

	private BroadcastReceiver updateReciver = null;
	TableListAdapter tableListAdapter = null;

	private GetCurrentOrderListAsyncTask mGetCurrentOrderListAsyncTask;
	
	// changes as on 28th November 2013
	private TextView mTxtNotificationCount;
	private RelativeLayout mRelNotificationCentre;
	// changes end here

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Global.activityFinishAnimationLeftToRight(TableListActivity.this);
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
		setContentView(R.layout.activity_table_list);
		
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1 || 
				currentapiVersion > android.os.Build.VERSION_CODES.JELLY_BEAN){
		    // Do something for froyo and above versions
			if(Prefs.getKey(Prefs.WAITER_ID) != null && 
					Prefs.getKey(Prefs.WAITER_ID).trim().length() <= 0 || 
					Prefs.getKey(Prefs.WAITER_ID) == null) {
				finish();
			}
		}
		
		// changes as on 2nd Jan 2014
		StartActivity.currentIntent = getIntent();
		// changes end here

		mLanguageManager = LanguageManager.getInstance();

		SlidingMenu menu = new SlidingMenu(this);
		menu.setShadowWidthRes(R.dimen.shadow_res);
		menu.setFadeDegree(0.35f);

		sectionCache = SectionManager.getInstance().getSectionCache();

		orderCache = OrderManager.getInstance().getOrderCache();

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.layout_left_menu, null);

		setBehindContentView(view);

		getSlidingMenu().setFadeEnabled(true);
		getSlidingMenu().setFadeDegree(0.35f);
		getSlidingMenu().setSlidingEnabled(true);
		getSlidingMenu().setSelectorEnabled(true);
		getSlidingMenu().setSelected(true);
		getSlidingMenu().setShadowDrawable(R.drawable.drop_shadow_for_menu);
		getSlidingMenu().setBehindOffset((int) getWidth() / 3);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

		mTxtHeader = (TextView) findViewById(R.id.txtHeader);
		mTxtSectionName = (TextView) findViewById(R.id.txtSectionNameTableList);
		mBtnForceSyncTables = (ImageButton) findViewById(R.id.imgBtnRefresh);

		if (getIntent().getExtras() != null) {
			if (getIntent().getStringExtra(Global.FROM_ACTIVITY) != null) {
				mFrom = getIntent().getStringExtra(Global.FROM_ACTIVITY);
			}
		}

		mTxtLeftMenuOrders = (TextView) view
				.findViewById(R.id.txtOrdersLeftMenu);
		mTxtLeftMenuSettings = (TextView) view
				.findViewById(R.id.txtSettingsLeftMenu);
		mTxtLeftMenuNotifications = (TextView) view
				.findViewById(R.id.txtNotificationsLeftMenu);
		mTxtLeftMenuTables = (TextView) view
				.findViewById(R.id.txtTablesLeftMenu);
		mBtnPlaceOrderLeftMenu = (Button) view
				.findViewById(R.id.btnPlaceOrderLeftMenu);
		mBtnLockLeftMenu = (Button) view.findViewById(R.id.btnLeftMenuLock);
		mBtnWaiterChangeLeftMenu = (Button) view
				.findViewById(R.id.btnLeftMenuWaiterChange);
		mTxtLeftMenuWaiterName = (TextView) view
				.findViewById(R.id.txtWaiterName);
		imageMenuIcon = (ImageButton) findViewById(R.id.menuIcon);
		mTxtLeftMenuLink = (TextView) view.findViewById(R.id.txtLeftMenuLink);

		mTxtLeftMenuLink.setMovementMethod(LinkMovementMethod.getInstance());
		mTxtLeftMenuLink.setLinksClickable(true);
		
		mTxtNotificationCount = (TextView)findViewById(R.id.txtNotificationNumber);
		mRelNotificationCentre = (RelativeLayout)findViewById(R.id.relNotificationCentre);

		mTxtLeftMenuOrders.setOnClickListener(this);
		mTxtLeftMenuSettings.setOnClickListener(this);
		mTxtLeftMenuNotifications.setOnClickListener(this);
		mTxtLeftMenuTables.setOnClickListener(this);
		mBtnPlaceOrderLeftMenu.setOnClickListener(this);
		mBtnLockLeftMenu.setOnClickListener(this);
		mBtnWaiterChangeLeftMenu.setOnClickListener(this);
		imageMenuIcon.setOnClickListener(this);
		mTxtLeftMenuLink.setOnClickListener(this);
		mBtnForceSyncTables.setOnClickListener(this);
		mRelNotificationCentre.setOnClickListener(this);

		mTxtLeftMenuTables.requestFocus();
		mTxtLeftMenuTables.setFocusable(true);
		mTxtLeftMenuTables.setPressed(true);
		Prefs.addKey(Prefs.MENU_SELECTED, Global.TABLES);
		setSelectedView();

		mSectionSpinner = (Spinner) findViewById(R.id.sectionSelectorSpinner);
		mSectionSpinner.setOnItemSelectedListener(this);

		gridView = (GridView) findViewById(R.id.gridViewTableList);
		gridView.setOnItemClickListener(this);

		sectionId = Prefs.getKey(Prefs.SECTION_ID);
		waiterId = Prefs.getKey(Prefs.WAITER_ID);
		waiterName = Prefs.getKey(Prefs.WAITER_NAME);
		mTabSelected = Prefs.getKey(Prefs.TAB_SELECTED);

		mTxtLeftMenuWaiterName.setText(waiterName);

		mBtnForceSyncTables.setVisibility(View.VISIBLE);

		initializeData();
		setGuiLabels();
		setSelectedView();

		setButtonWidth();
	}

	public void setButtonWidth() {
		final int offset = getWidth() / 3;
		Log.i("Dhara", "offset " + offset);

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
	 * Gets all the tables per sections
	 * 
	 * @param sectionId
	 */
	@SuppressWarnings("unchecked")
	private void getAllTableData(String sectionId) {
		if (sectionCache != null) {
			Log.i(TAG, "lru cache has data");
			tableHashMap = (HashMap<String, List<Tables>>) sectionCache
					.get(Global.TABLE_MAP);

			if (tableHashMap != null) {
				tableList = tableHashMap.get(sectionId);
			} else {
				tableList = new ArrayList<Tables>();
			}

			tableListAdapter = new TableListAdapter(TableListActivity.this,
					R.layout.individual_tablelist_item, tableList);
			gridView.setAdapter(tableListAdapter);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Prefs.addKey(Prefs.MENU_SELECTED, Global.TABLES);
		setSelectedView();
		
		// changes as on 2nd Jan 2014
		StartActivity.currentIntent = getIntent();
		// changes end here

		if (getSlidingMenu() != null) {
			if (getSlidingMenu().isMenuShowing()) {
				toggle();
			}
		}
		
		// changes as on 28th November 2013

		if(mTxtNotificationCount != null) {
			mTxtNotificationCount.setText(String.valueOf(NotificationManager.getInstance().getNotificationOrderCount()));
		}
		
		if (updateReciver == null) {
			IntentFilter notification_filter = new IntentFilter();
			notification_filter.addAction(getString(R.string.broadcast_table_update));
			notification_filter.addAction(getString(R.string.broadcast_notification_update));
			notification_filter.addAction(getString(R.string.kill));

			updateReciver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					if(intent.getAction().equalsIgnoreCase(
							getString(R.string.broadcast_table_update))) {
						tableListAdapter.notifyDataSetChanged();
					}
					
					int counter = 0;
					if(intent.getAction().equalsIgnoreCase(
							getString(R.string.broadcast_notification_update))) {
						if(intent.getExtras() != null) {
							counter = intent.getExtras().getInt("notificationCount");
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
		// changes end here
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txtLeftMenuLink:
			Intent browserIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://" + getString(R.string.site)));
			
			// changes as on 2nd Jan 2014
			StartActivity.currentIntent = browserIntent;
			// changes end here
			
			startActivity(browserIntent);
			Global.activityStartAnimationRightToLeft(TableListActivity.this);
			break;

		case R.id.txtSettingsLeftMenu:
			Prefs.addKey(Prefs.MENU_SELECTED, Global.SETTINGS);
			setSelectedView();
			Intent intent = new Intent(TableListActivity.this,
					SettingsActivity.class);
			startActivity(intent);
			Global.activityStartAnimationRightToLeft(TableListActivity.this);
			break;

		case R.id.txtOrdersLeftMenu:
			Prefs.addKey(Prefs.MENU_SELECTED, Global.ORDERS);
			setSelectedView();
			finish();
			intent = new Intent(TableListActivity.this, MyOrderActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			Global.activityStartAnimationRightToLeft(TableListActivity.this);
			break;

		case R.id.txtNotificationsLeftMenu:
			// send to the notification screen
			Prefs.addKey(Prefs.MENU_SELECTED, Global.NOTIFICATIONS);
			setSelectedView();
			intent = new Intent(this, NotificationListActivity.class);
			startActivity(intent);
			Global.activityStartAnimationRightToLeft(TableListActivity.this);
			break;
			
		case R.id.relNotificationCentre:
			Prefs.addKey(Prefs.MENU_SELECTED, Global.NOTIFICATIONS);
			setSelectedView();
			intent = new Intent(this, NotificationListActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.activity_in_anim,
					R.anim.activity_out_anim);
			break;

		case R.id.txtTablesLeftMenu:
			// same page
			// do nothing
			toggle();
			Prefs.addKey(Prefs.MENU_SELECTED, Global.TABLES);
			setSelectedView();
			break;

		case R.id.btnPlaceOrderLeftMenu:
			// same page
			// do nothing
			toggle();
			Prefs.addKey(Prefs.MENU_SELECTED, Global.TABLES);
			setSelectedView();
			break;

		case R.id.menuIcon:
			toggle();
			setSelectedView();
			break;

		case R.id.btnLeftMenuLock:
			intent = new Intent(this, LoginActivity.class);
			intent.putExtra(Global.ACTION, Global.FROM_LOCK);
			startActivity(intent);
			Global.activityStartAnimationRightToLeft(TableListActivity.this);
			break;

		case R.id.btnLeftMenuWaiterChange:
			callDialog();
			break;

		case R.id.imgBtnRefresh:
			mGetCurrentOrderListAsyncTask = new GetCurrentOrderListAsyncTask(
					TableListActivity.this, Global.TABLE_LIST);
			mGetCurrentOrderListAsyncTask.execute();
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// Remove billSplitChecked from sp
		// used to display the bill slit dialog
		Prefs.removeKey(TableListActivity.this, Prefs.BILL_SPLIT_CHECKED);

		String textViewText = ((TextView) (view)
				.findViewById(R.id.txtTableNumberTableList)).getText()
				.toString();
		Log.i(TAG, "table number : " + textViewText + " clicked");

		processOnTableClick(position);
	}

	private void processOnTableClick(int position) {
		String tableId = "";
		String tableNumber = "";

		Tables table = tableList.get(position);

		tableId = table.getTableId();
		tableNumber = String.valueOf(table.getTableNumber());

		// Check if the table clicked on is having orders or not
		orderPerTableMap = (MultiKeyMap) orderCache
				.get(Global.PER_TABLE_ORDERS);

		if (orderPerTableMap != null) {
			Log.i(TAG,
					"value inside  on item click: "
							+ orderPerTableMap.containsKey(
									table.getSectionId(), table.getTableId()));
			if (orderPerTableMap.containsKey(table.getSectionId(),
					table.getTableId())) {
				List<Order> orderListPerTable = (List<Order>) orderPerTableMap
						.get(table.getSectionId(), table.getTableId());

				// Checks if the list of order is not null and that there's
				// atleast one order
				if (orderListPerTable != null && orderListPerTable.size() > 0) {
					// after clicking on a table, move to the order related
					// activity
					// where the table id and tableNumber would be sent to the
					// activity
					Intent intent = new Intent(TableListActivity.this,
							TableOrderListActivity.class);
					intent.putExtra(Global.TABLE_NUMBER, tableNumber);
					intent.putExtra(Global.TABLE_ID, tableId);
					intent.putExtra(Global.FROM_ACTIVITY,
							Global.FROM_TABLE_LIST);
					startActivity(intent);

					// activity in animation
					Global.activityStartAnimationRightToLeft(TableListActivity.this);
					finish();
				} else {
					callOrderRelatedIntent(tableNumber, tableId);
				}
			} else {
				// replace current fragment activity with the new order fragment
				// activity
				callOrderRelatedIntent(tableNumber, tableId);

			}
		} else {
			// replace current fragment activity with the new order fragment
			// activtiy
			callOrderRelatedIntent(tableNumber, tableId);
		}

	}

	private void callOrderRelatedIntent(String tableNumber, String tableId) {
		Intent intent = new Intent(TableListActivity.this,
				OrderRelatedActivity.class);
		intent.putExtra(Global.TABLE_NUMBER, tableNumber);
		intent.putExtra(Global.TABLE_ID, tableId);
		intent.putExtra(Global.FROM_ACTIVITY, Global.FROM_TABLE_LIST);

		intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		startActivity(intent);
		finish();
		// activity in animation
		Global.activityStartAnimationRightToLeft(TableListActivity.this);
	}

	/**
	 * Gets the count of tables without orders Used to set the number in the
	 * pending section
	 */
	public void getTotalCountOfFreeTables() {
		Log.i(TAG, "inside getTotalCount of free tables ");
		orderPerTableMap = (MultiKeyMap) orderCache
				.get(Global.PER_TABLE_ORDERS);

		int counter = 0;
		if (tableList != null) {
			for (Tables table : tableList) {
				// if the order map contains the table
				// if the table has orders it will be available in the
				// collection
				// else no
				if (orderPerTableMap != null) {
					if (!orderPerTableMap.containsKey(table.getSectionId(),
							table.getTableId())) {
						counter++;
					}
				} else {
					counter++;
				}
			}
		}

		/* String pending = mLanguageManager.getPending(); */
	}

	/**
	 * Set the labels on the gui
	 */
	private void setGuiLabels() {
		mTxtHeader.setText(mLanguageManager.getTables());
		mTxtLeftMenuNotifications.setText(mLanguageManager.getNotifications());
		mTxtLeftMenuOrders.setText(mLanguageManager.getOrders());
		mTxtLeftMenuSettings.setText(mLanguageManager.getSettings());
		mTxtLeftMenuTables.setText(mLanguageManager.getTables());
		mBtnPlaceOrderLeftMenu.setText(mLanguageManager.getPlaceOrder());
		mBtnLockLeftMenu.setText(mLanguageManager.getLock());
		mBtnWaiterChangeLeftMenu.setText(mLanguageManager.getChangeWaiter());
		
		mBtnPlaceOrderLeftMenu.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtLeftMenuNotifications.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtLeftMenuOrders.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtLeftMenuSettings.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtLeftMenuTables.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mBtnLockLeftMenu.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mBtnWaiterChangeLeftMenu.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		
		mTxtLeftMenuWaiterName.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		
		setButtonWidth();
	}

	/**
	 * Called by the async task
	 */
	public void dataReceived(boolean flag) {
		if (flag) {
			initializeData();
		} else {
			Toast toast = Toast.makeText(TableListActivity.this,
					getString(R.string.server_unreachable), Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
	}

	/**
	 * initializes the data
	 */
	private void initializeData() {
		// forcefully asking android to call on create options menu
		// needed to ensure that oncreatemenu options is called so that
		// the language of the menu can be changed
		getSectionsForSpinner();

		mTxtHeader.setText(Prefs.getKey(Prefs.WAITER_NAME));
		mTxtSectionName.setText(Prefs.getKey(Prefs.SECTION_NAME));
		Log.i(TAG, "section id selected : " + sectionId);

		setGuiLabels();

		mOrderPerTable = (MultiKeyMap) orderCache.get(Global.PER_TABLE_ORDERS);

		getAllTableData(sectionId);
	}

	/**
	 * Get the sections for the spinner for the user to be able to select
	 */
	private void getSectionsForSpinner() {
		// get it from the cache
		mLstSections = (List<Section>) sectionCache.get(Global.SECTIONS);

		// set the spinner adapter
		sectionSpinnerAdapter = new SectionSpinnerAdapter(
				TableListActivity.this,
				R.layout.individual_sectionspinner_item, mLstSections);
		mSectionSpinner.setAdapter(sectionSpinnerAdapter);
		

		if (mLstSections != null) {
			mSelectedSectionPos = 0;
			int i = 0;
			for (Section section : mLstSections) {
				// checks if the section id is equal to the section id selected
				if (section.getSectionId().equals(sectionId)) {
					mSelectedSectionPos = i;
					Log.i(TAG, "value of section id previously selected : "
							+ sectionId);
					Log.i(TAG, " value of position " + mSelectedSectionPos);
					break;
				}
				i++;
			}
		}
		
		mSectionSpinner.setSelected(true);
		mSectionSpinner.setSelection(mSelectedSectionPos);
	}

	/*
	 * public WaiterPadResponse logoutUser(String userPin) { WaiterPadResponse
	 * waiterPadResponse = null; if (userPin != null && userPin.trim().length()
	 * >= 0) { String url = mUtils.getURLString(getString(R.string.LOGOUT)) +
	 * getString(R.string.delimiter) + userPin + getString(R.string.delimiter) +
	 * mUtils.getMacAddress();
	 * 
	 * Log.i(TAG, "url : " + url);
	 * 
	 * InputStream inputStream = mUtils.retrieveStream(url); if (inputStream ==
	 * null) { return waiterPadResponse; } else { try { GsonBuilder gsonBuilder
	 * = new GsonBuilder(); Gson gson = gsonBuilder.create(); Reader reader =
	 * new InputStreamReader(inputStream);
	 * 
	 * waiterPadResponse = gson.fromJson(reader, WaiterPadResponse.class); }
	 * catch (Exception e) { e.printStackTrace(); } return waiterPadResponse; }
	 * } else { return null; } }
	 */

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if (((TextView) view) != null) {
			((TextView) view).setText(null);
		}
		
		// change the table list based on the section selection
		Section section = mLstSections.get(position);
		if (section != null) {
			Log.i(TAG, "value of section id currently selected : " + sectionId);

			String sectionIdSelected = section.getSectionId();

			Prefs.addKey(Prefs.SECTION_ID, sectionIdSelected);
			Prefs.addKey(Prefs.SECTION_NAME, section.getSectionName()
					.toString());

			sectionId = sectionIdSelected;
			getAllTableData(sectionIdSelected);
			mTxtSectionName.setText(section.getSectionName().toString());
		}
		
		Prefs.addKey(Prefs.MENU_SELECTED, Global.TABLES);
		setSelectedView();
	}

	public void refreshList(String sectionIdFromService) {
		Section section = Iterables.find(mLstSections, new SearchForSection(
				sectionIdFromService), null);

		if (section != null) {
			mTxtSectionName.setText(section.getSectionName().toString());

			Prefs.addKey(Prefs.SECTION_ID, sectionIdFromService);
			Prefs.addKey(Prefs.SECTION_NAME, section.getSectionName()
					.toString());
		} else {
			sectionIdFromService = Prefs.getKey(Prefs.SECTION_ID);
			sectionId = sectionIdFromService;
			mTxtSectionName.setText(Prefs.getKey(Prefs.SECTION_NAME));
		}

		getAllTableData(sectionIdFromService);

	}

	public void showMessage(String message) {
		if (message != null) {
			String appName = mLanguageManager.getAppName();
			String ok = mLanguageManager.getOk();
			CommonDialog dialog = new CommonDialog(TableListActivity.this,
					appName, ok, message);
			dialog.show();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	private void callDialog() {
		builderEnterPin = new Dialog(TableListActivity.this);
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
		editEnterPinToExit = (EditText) view.findViewById(R.id.editPinToExit);
		btnCancel = (Button) view.findViewById(R.id.btnCloseLayout);
		
		txtEnterPinToLogout.setText(mLanguageManager.getEnterPinToLogout());

		txtEnterPinToLogout.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		editEnterPinToExit.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		btnCancel.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		
		String enterPin = mLanguageManager.getEnterPin();
		String cancel = mLanguageManager.getCancel();

		editEnterPinToExit.setHint(enterPin);
		btnCancel.setText(cancel);

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(
						editEnterPinToExit.getWindowToken(), 0);
				setSelectedView();
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

					if (Validator.isValidNumber(s.toString())) {
						
						boolean isNumber = Validator.containsOnlyNumbers(s.toString());
						
						if(isNumber) {
							WSWaiterPin wsWaiterPin = new WSWaiterPin();
							wsWaiterPin.setWaiterCode(Prefs
									.getKey(Prefs.WAITER_CODE));
							wsWaiterPin.setMacAddress(Utils.getMacAddress());
							wsWaiterPin.setWaiterPin(s.toString());

							logoutAsyncTask = new LogoutAsyncTask(
									TableListActivity.this, wsWaiterPin,
									Global.TABLE_LIST);
							logoutAsyncTask.execute();
						}else {
							String message = mLanguageManager.getEnterValidPin();
							showMessage(message);
						}
					} else {
						String message = mLanguageManager.getEnterValidPin();
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

	public void logoutMessage(WaiterPadResponse result) {
		if (result != null) {
			if (result.isError()
					&& result.getErrorMessage().toLowerCase()
							.contains("invalid")) {

				String message = mLanguageManager.getInvalidUser();
				showMessage(message);

				if (editEnterPinToExit != null) {
					editEnterPinToExit.setText("");
				}
			} else {
				if (builderEnterPin != null) {
					builderEnterPin.dismiss();
				}
				Intent intent = new Intent(TableListActivity.this,
						LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_CLEAR_TASK
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(Global.FROM_ACTIVITY, Global.FROM_SPLASH);
				startActivity(intent);
				finish();
				Global.activityStartAnimationRightToLeft(TableListActivity.this);
			}
		} else {
			String message = mLanguageManager.getServerUnreachable();
			showMessage(message);

			if (editEnterPinToExit != null) {
				editEnterPinToExit.setText("");
			}
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

	// changes as on 6th Novemeber 2013
	// sync the tables
	public void refreshTables(OrderList result) {
		OrderManager.getInstance().storeCurrentOrdersInMemory(result);
		tableListAdapter.notifyDataSetChanged();
	}
	// changes end here
}