package com.azilen.waiterpad.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.NotificationManager;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.adapters.NotificationAdapter;
import com.azilen.waiterpad.asynctask.LogoutAsyncTask;
import com.azilen.waiterpad.controls.CommonDialog;
import com.azilen.waiterpad.data.Order;
import com.azilen.waiterpad.data.WSWaiterPin;
import com.azilen.waiterpad.data.WaiterPadResponse;
import com.azilen.waiterpad.managers.font.FontManager;
import com.azilen.waiterpad.managers.language.LanguageManager;
import com.azilen.waiterpad.managers.order.OrderManager;
import com.azilen.waiterpad.utils.Global;
import com.azilen.waiterpad.utils.Prefs;
import com.azilen.waiterpad.utils.Utils;
import com.azilen.waiterpad.utils.Validator;
import com.azilen.waiterpad.utils.search.SearchForOrder;
import com.google.common.collect.Iterables;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class NotificationListActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {
	private ListView mListView;
	private TextView mTxtHeader;
	private List<Order> mOrders;
	private LanguageManager mLanguageManager;
	public static NotificationAdapter notificationAdapter;
	private LruCache<String, Object> mMemCache;
	private View view;

	private Button mBtnPlaceOrderLeftMenu;
	private TextView mTxtLeftMenuSettings;
	private TextView mTxtLeftMenuTables;
	private TextView mTxtLeftMenuOrders;
	private TextView mTxtLeftMenuNotifications;
	private TextView mTxtLeftMenuWaiterName;
	private TextView mTxtLeftMenuLink;
	private Button mBtnLockLeftMenu;
	private Button mBtnWaiterChangeLeftMenu;
	private ImageButton imageMenuIcon;

	private String mWaiterName;

	private EditText editEnterPinToExit;
	private Dialog builderEnterPin;
	private Button btnCancel;
	private LogoutAsyncTask logoutAsyncTask;
	
	// changes as on 28th November 2013
	// adding these because of requirements
	private BroadcastReceiver updateReciver = null;
	private TextView mTxtNotificationCount;
	private RelativeLayout mRelNotificationCentre;
	// changes end here

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// activity out animation
		Global.activityFinishAnimationLeftToRight(NotificationListActivity.this);
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
	
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notificationlist);
		
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

		SlidingMenu menu = new SlidingMenu(this);
		menu.setShadowWidthRes(R.dimen.shadow_res);
		menu.setFadeDegree(0.35f);

		mLanguageManager = LanguageManager.getInstance();

		mMemCache = com.azilen.waiterpad.managers.notification.NotificationManager
				.getInstance().getNotificationCache();

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
		getSlidingMenu().setBehindOffset((int) getWidth() / 3);
		getSlidingMenu().setShadowDrawable(R.drawable.drop_shadow_for_menu);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

		mListView = (ListView) findViewById(android.R.id.list);
		mTxtHeader = (TextView) findViewById(R.id.txtHeader);

		mTxtHeader.setText(getString(R.string.notification_center));

		mTxtLeftMenuWaiterName = (TextView) view
				.findViewById(R.id.txtWaiterName);
		mTxtLeftMenuOrders = (TextView) view
				.findViewById(R.id.txtOrdersLeftMenu);
		mTxtLeftMenuSettings = (TextView) view
				.findViewById(R.id.txtSettingsLeftMenu);
		mTxtLeftMenuNotifications = (TextView) view
				.findViewById(R.id.txtNotificationsLeftMenu);
		mTxtLeftMenuTables = (TextView) view
				.findViewById(R.id.txtTablesLeftMenu);
		mTxtLeftMenuLink = (TextView) view.findViewById(R.id.txtLeftMenuLink);
		mBtnPlaceOrderLeftMenu = (Button) view
				.findViewById(R.id.btnPlaceOrderLeftMenu);
		mBtnLockLeftMenu = (Button) view.findViewById(R.id.btnLeftMenuLock);
		mBtnWaiterChangeLeftMenu = (Button) view
				.findViewById(R.id.btnLeftMenuWaiterChange);
		imageMenuIcon = (ImageButton) findViewById(R.id.menuIcon);

		mTxtLeftMenuLink.setMovementMethod(LinkMovementMethod.getInstance());
		mTxtLeftMenuLink.setLinksClickable(true);
		
		mTxtNotificationCount = (TextView)findViewById(R.id.txtNotificationNumber);
		mRelNotificationCentre = (RelativeLayout)findViewById(R.id.relNotificationCentre);
		mRelNotificationCentre.setClickable(false);

		mTxtLeftMenuOrders.setOnClickListener(this);
		mTxtLeftMenuSettings.setOnClickListener(this);
		mTxtLeftMenuNotifications.setOnClickListener(this);
		mTxtLeftMenuTables.setOnClickListener(this);
		mBtnPlaceOrderLeftMenu.setOnClickListener(this);
		mBtnLockLeftMenu.setOnClickListener(this);
		mBtnWaiterChangeLeftMenu.setOnClickListener(this);
		imageMenuIcon.setOnClickListener(this);
		mTxtLeftMenuLink.setOnClickListener(this);

		mTxtLeftMenuNotifications.setPressed(true);

		Prefs.addKey(NotificationListActivity.this, Prefs.MENU_SELECTED,
				Global.NOTIFICATIONS);

		mWaiterName = Prefs.getKey(Prefs.WAITER_NAME);

		setGui();

		mOrders = (List<Order>) mMemCache.get(Global.NOTIFICATION_ORDERS);

		if (mOrders != null) {
			mTxtHeader.append(" ( " + mOrders.size() + " ) ");
		} else {
			mTxtHeader.append(" ( 0 )");
		}

		if (mOrders != null && mOrders.size() > 0) {
			notificationAdapter = new NotificationAdapter(
					NotificationListActivity.this,
					R.layout.individual_notification_list, mOrders);
			mListView.setAdapter(notificationAdapter);
		}

		mTxtLeftMenuWaiterName.setText(mWaiterName);

		mListView.setOnItemClickListener(this);
	}
	
	private void refreshList() {
		mOrders = (List<Order>) mMemCache.get(Global.NOTIFICATION_ORDERS);
		if ((mOrders != null && mOrders.size() <= 0) || mOrders == null) {
			mOrders = new ArrayList<Order>();
		}
		
		notificationAdapter = new NotificationAdapter(
				NotificationListActivity.this,
				R.layout.individual_notification_list, mOrders);
		mListView.setAdapter(notificationAdapter);
	}

	private void setButtonWidth() {
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

	private void setGui() {
		mTxtHeader.setText(mLanguageManager.getNotificationCenter());
		mBtnPlaceOrderLeftMenu.setText(mLanguageManager.getPlaceOrder());
		mTxtLeftMenuNotifications.setText(mLanguageManager.getNotifications());
		mTxtLeftMenuOrders.setText(mLanguageManager.getOrders());
		mTxtLeftMenuSettings.setText(mLanguageManager.getSettings());
		mTxtLeftMenuTables.setText(mLanguageManager.getTables());
		mBtnLockLeftMenu.setText(mLanguageManager.getLock());
		mBtnWaiterChangeLeftMenu.setText(mLanguageManager.getChangeWaiter());
		
		mTxtHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mBtnPlaceOrderLeftMenu.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtLeftMenuNotifications.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtLeftMenuOrders.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtLeftMenuSettings.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtLeftMenuTables.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mBtnLockLeftMenu.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mBtnWaiterChangeLeftMenu.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtLeftMenuWaiterName.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		
		setButtonWidth();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getSlidingMenu() != null) {
			if (getSlidingMenu().isMenuShowing()) {
				toggle();
			}
		}
		
		// changes as on 2nd Jan 2014
		StartActivity.currentIntent = getIntent();
		// changes end here
		
		Prefs.addKey(NotificationListActivity.this, Prefs.MENU_SELECTED,
				Global.NOTIFICATIONS);
		setSelectedView();
		
		// changes as on 28th November 2013
		if(mTxtNotificationCount != null) {
			mTxtNotificationCount.setText(String.valueOf(com.azilen.waiterpad.managers.notification.NotificationManager.getInstance().getNotificationOrderCount()));
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
						
						mTxtHeader.setText(getString(R.string.notification_center));
						mTxtHeader.append(" (" + counter + ") ");
						
						refreshList();
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
			Global.activityStartAnimationRightToLeft(NotificationListActivity.this);
			break;

		case R.id.btnPlaceOrderLeftMenu:
			// lead to the table selection list
			Prefs.addKey(NotificationListActivity.this, Prefs.MENU_SELECTED,
					Global.TABLES);
			setSelectedView();
			Intent intent = new Intent(this, TableListActivity.class);
			startActivity(intent);
			Global.activityStartAnimationRightToLeft(NotificationListActivity.this);
			break;

		case R.id.txtSettingsLeftMenu:
			// onClick(findViewById(R.id.menuIcon));
			Prefs.addKey(NotificationListActivity.this, Prefs.MENU_SELECTED,
					Global.SETTINGS);
			setSelectedView();
			intent = new Intent(NotificationListActivity.this,
					SettingsActivity.class);
			startActivity(intent);
			Global.activityStartAnimationRightToLeft(NotificationListActivity.this);
			break;

		case R.id.txtOrdersLeftMenu:
			// do nothing
			Prefs.addKey(NotificationListActivity.this, Prefs.MENU_SELECTED,
					Global.ORDERS);
			setSelectedView();
			finish();
			intent = new Intent(NotificationListActivity.this,
					MyOrderActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			// activity out animation
			Global.activityStartAnimationRightToLeft(NotificationListActivity.this);
			break;

		case R.id.txtNotificationsLeftMenu:
			// take to the notification screen
			toggle();
			Prefs.addKey(NotificationListActivity.this, Prefs.MENU_SELECTED,
					Global.NOTIFICATIONS);
			setSelectedView();
			break;

		case R.id.txtTablesLeftMenu:
			// same page
			// do nothing
			Prefs.addKey(NotificationListActivity.this, Prefs.MENU_SELECTED,
					Global.TABLES);
			setSelectedView();
			intent = new Intent(this, TableListActivity.class);
			startActivity(intent);
			Global.activityStartAnimationRightToLeft(NotificationListActivity.this);
			break;

		case R.id.menuIcon:
			toggle();
			setSelectedView();
			break;

		case R.id.btnLeftMenuLock:
			mBtnLockLeftMenu.setPressed(true);
			intent = new Intent(this, LoginActivity.class);
			intent.putExtra(Global.ACTION, Global.FROM_LOCK);
			startActivity(intent);
			Global.activityStartAnimationRightToLeft(NotificationListActivity.this);
			break;

		case R.id.btnLeftMenuWaiterChange:
			callDialog();
			break;

		default:
			break;
		}
	}

	private void callDialog() {
		builderEnterPin = new Dialog(NotificationListActivity.this);

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

		editEnterPinToExit = (EditText) view.findViewById(R.id.editPinToExit);
		btnCancel = (Button) view.findViewById(R.id.btnCloseLayout);

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editEnterPinToExit.getWindowToken(), 0);

		String enterPin = "";
		String cancel = "";

		enterPin = mLanguageManager.getEnterPin();
		cancel = mLanguageManager.getCancel();
		
		txtEnterPinToLogout.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		editEnterPinToExit.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		btnCancel.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());

		editEnterPinToExit.setHint(enterPin);
		btnCancel.setText(cancel);

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(
						editEnterPinToExit.getWindowToken(), 0);
				builderEnterPin.dismiss();
				setSelectedView();
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
									NotificationListActivity.this, wsWaiterPin,
									Global.FROM_NOTIFICATIONS);
							logoutAsyncTask.execute();
						}else {
							String message = mLanguageManager.getEnterValidPin();
							showMessageBox(message);
						}
					} else {
						String message = mLanguageManager.getEnterValidPin();
						showMessageBox(message);
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

				showMessageBox(message);

				if (editEnterPinToExit != null) {
					editEnterPinToExit.setText("");
				}
			} else {
				if (builderEnterPin != null) {
					builderEnterPin.dismiss();
				}
				Intent intent = new Intent(NotificationListActivity.this,
						LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_CLEAR_TASK
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("from", "splash");
				startActivity(intent);
				finish();
				Global.activityStartAnimationRightToLeft(NotificationListActivity.this);
			}
		} else {
			String message = mLanguageManager.getServerUnreachable();
			showMessageBox(message);

			if (editEnterPinToExit != null) {
				editEnterPinToExit.setText("");
			}
		}
	}

	private void showMessageBox(String message) {
		if (message != null) {
			String appName = mLanguageManager.getAppName();
			String ok = mLanguageManager.getOk();

			CommonDialog dialog = new CommonDialog(
					NotificationListActivity.this, appName, ok, message);
			dialog.show();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// open the order screen with the order
		Order order = mOrders.get(position);

		if (order != null) {
			// remove the order from the list
			// send to the next screen
			List<Order> orders = (List<Order>) mMemCache
					.get(Global.NOTIFICATION_ORDERS);
			orders.remove(order);

			clearNotification(order.getOrderNumber());

			mMemCache.put(Global.NOTIFICATION_ORDERS, orders);
			notificationAdapter.notifyDataSetChanged();
			int index = mTxtHeader.getText().toString().indexOf('(');
			mTxtHeader.setText(mTxtHeader.getText().toString()
					.subSequence(0, index - 1));
			if (orders != null) {
				mTxtHeader.append(" (" + orders.size() + ") ");
			} else {
				mTxtHeader.append(" (0) ");
			}
			
			if(mTxtNotificationCount != null) {
				mTxtNotificationCount.setText(String.valueOf(com.azilen.waiterpad.managers.notification.NotificationManager.getInstance().getNotificationOrderCount()));
			}

			LruCache<String, Object> lruRunningOrders = OrderManager
					.getInstance().getOrderCache();

			// Get all the orders related to the table
			List<Order> lstRunningOrders = (List<Order>) lruRunningOrders
					.get(Global.RUNNING_ORDERS);

			if (lstRunningOrders != null && lstRunningOrders.size() > 0) {
				if (Iterables.find(lstRunningOrders,
						new SearchForOrder(order.getOrderId()), null) == null) {
					return;
				} else {
					Intent intent = new Intent(NotificationListActivity.this,
							OrderRelatedActivity.class);
					intent.putExtra(Global.TABLE_ID, order.getTable()
							.getTableId());
					intent.putExtra(Global.TABLE_NUMBER,
							String.valueOf(order.getTable().getTableNumber()));
					intent.putExtra(Global.ORDER_ID, order.getOrderId());
					intent.putExtra(Global.ORDER_NUMBER, order.toString());
					intent.putExtra(Global.FROM_ACTIVITY,
							Global.FROM_NOTIFICATIONS);
					intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
					startActivity(intent);

					finish();
					Global.activityStartAnimationRightToLeft(NotificationListActivity.this);
				}
			}
		}
	}

	private void clearNotification(int orderNumber) {
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(orderNumber);
	}
}
