package com.azilen.waiterpad.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.WaiterPadApplication;
import com.azilen.waiterpad.adapters.MyOrdersAdapter;
import com.azilen.waiterpad.asynctask.GetCurrentOrderListAsyncTask;
import com.azilen.waiterpad.asynctask.LogoutAsyncTask;
import com.azilen.waiterpad.controls.CommonDialog;
import com.azilen.waiterpad.data.Order;
import com.azilen.waiterpad.data.OrderList;
import com.azilen.waiterpad.data.WSWaiterPin;
import com.azilen.waiterpad.data.WaiterPadResponse;
import com.azilen.waiterpad.managers.font.FontManager;
import com.azilen.waiterpad.managers.language.LanguageManager;
import com.azilen.waiterpad.managers.notification.NotificationManager;
import com.azilen.waiterpad.managers.order.OrderManager;
import com.azilen.waiterpad.utils.Global;
import com.azilen.waiterpad.utils.Prefs;
import com.azilen.waiterpad.utils.Utils;
import com.azilen.waiterpad.utils.Validator;
import com.azilen.waiterpad.utils.search.SearchForOrder;
import com.google.common.collect.Iterables;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MyOrderActivity extends BaseActivity implements
		OnItemClickListener, OnClickListener {
	private ListView myOrderList;
	private TextView mTxtHeader;
	private ImageButton imageMenuIcon;

	private Button mBtnPlaceOrderLeftMenu;
	private TextView mTxtLeftMenuSettings;
	private TextView mTxtLeftMenuTables;
	private TextView mTxtLeftMenuOrders;
	private TextView mTxtLeftMenuNotifications;
	private Button mBtnLockLeftMenu;
	private Button mBtnChangeWaiterLeftMenu;
	private TextView mTxtLeftMenuWaiterName;
	private TextView mTxtLeftMenuLink;

	// changes as on 6th November, 2013
	// force sync orders button
	private ImageButton mBtnForceSyncOrders;
	// changes end here

	private String mWaiterId;
	private String mSectionId;
	private HashMap<String, List<Order>> mOrderPerWaiter;
	private List<Order> mOrderList;
	private List<Order> mOrderListToUse;
	public static MyOrdersAdapter mOpenOrdersAdapter;
	private String mFrom;
	private String mWaiterName;
	private static int counter = 0;
	private View view;

	private EditText editEnterPinToExit;
	private Dialog builderEnterPin;
	private Button btnCancel;

	private LanguageManager mLanguageManager;
	private LogoutAsyncTask logoutAsyncTask;

	// changes as on 6th November 2013
	// fetches the orders and refreshes the GUI
	private GetCurrentOrderListAsyncTask mGetCurrentOrderListAsyncTask;
	// changes end here

	private BroadcastReceiver updateReciver = null;
	private TextView mTxtNotificationCount;
	private RelativeLayout mRelNotificationCentre;
	
	// changes as on 10th December 2013
	private TextView txtTableHeader;
	private TextView txtSectionHeader;
	private TextView txtOrderHeader;
	private TextView txtTotalHeader;
	// changes end here
	
	private Utils mUtils;
	
	private String TAG = this.getClass().getSimpleName();
	private static Logger logger = LoggerFactory
			.getLogger(MyOrderActivity.class);

	@Override
	public void onResume() {
		super.onResume();
		counter = 0;
		refresh();
		Prefs.addKey(MyOrderActivity.this, Prefs.MENU_SELECTED, Global.ORDERS);
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
		
		// changes as on 28th November 2013
		// adding the receiver for notification updates
		if (updateReciver == null) {
			IntentFilter notification_filter = new IntentFilter();
			notification_filter.addAction("kill");
			notification_filter.addAction(getString(R.string.broadcast_notification_update));
			notification_filter.addAction(getString(R.string.broadcast_myorders_update));
			//		new IntentFilter(getString(R.string.broadcast_notification_update));

			updateReciver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					if(intent.getAction().equalsIgnoreCase(getString(R.string.kill))) {
						finish();
					}
					
					if(intent.getAction()
							.equalsIgnoreCase(getString(R.string.broadcast_notification_update))) {
						int count = 0;
						if(intent.getExtras() != null) {
							count = intent.getExtras().getInt(Global.NOTIFICATION_COUNT);
						}
						if(mTxtNotificationCount != null) {
							mTxtNotificationCount.setText(String.valueOf(count));
						}
					}
					
					// changes as on 10th December 2013
					if(intent.getAction()
							.equalsIgnoreCase(getString(R.string.broadcast_myorders_update))) {
						refresh();
					}
					// changes end here
				}
			};
			registerReceiver(updateReciver, notification_filter);
		}
		// changes end here
	}
	
	@Override
	public void onBackPressed() {
		counter++;
		String message = "";

		message = mLanguageManager.getPleasePressBackAgain();

		if (counter < 2) {
			final Toast toast = Toast.makeText(MyOrderActivity.this, message,
					Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();

			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					toast.cancel();
				}
			}, 500);
		} else {
			WSWaiterPin wsWaiterPin = new WSWaiterPin();
			wsWaiterPin.setMacAddress(Utils.getMacAddress());
			//wsWaiterPin.setMacAddress("20");
			wsWaiterPin.setWaiterCode(Prefs.getKey(Prefs.WAITER_ID));
			wsWaiterPin.setWaiterPin(Prefs.getKey(Prefs.USER_PIN));

			String[] keys = new String[] { Prefs.TAB_SELECTED, Prefs.WAITER_ID,
					Prefs.WAITER_NAME, Prefs.WAITER_CODE, Prefs.USER_PIN };
			Prefs.removeKeys(MyOrderActivity.this, keys);

			new LogoutAsyncTask(MyOrderActivity.this, wsWaiterPin, Global.HOME)
					.execute();

			stopService(MyOrderActivity.this);
			// activity out animation
			Global.activityFinishAnimationLeftToRight(MyOrderActivity.this);
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
	protected void onPause() {
		super.onPause();

		if (updateReciver != null) {
			unregisterReceiver(updateReciver);
			updateReciver = null;
		}
	}


	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return super.onKeyUp(keyCode, event);
	}
	
	public void onDestroy() {
		super.onDestroy();
		
		if (updateReciver != null) {
			unregisterReceiver(updateReciver);
			updateReciver = null;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myorders);
		
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion <= android.os.Build.VERSION_CODES.GINGERBREAD_MR1 || 
				currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN){
		    // Do something for froyo and above versions
			if(Prefs.getKey(Prefs.WAITER_ID) != null && 
					Prefs.getKey(Prefs.WAITER_ID).trim().length() <= 0 || 
					Prefs.getKey(Prefs.WAITER_ID) == null) {
				finish();
			}
		}

		mUtils = new Utils(MyOrderActivity.this);
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
		getSlidingMenu().setShadowDrawable(R.drawable.drop_shadow_for_menu);
		getSlidingMenu().setFadeDegree(0.35f);
		getSlidingMenu().setSlidingEnabled(true);
		getSlidingMenu().setSelectorEnabled(true);
		getSlidingMenu().setSelected(true);
		getSlidingMenu().setBehindOffset((int) getWidth() / 3);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

		logger.debug(TAG + " ---- BEGINS HERE ---- ");

		if (getIntent().getExtras() != null) {
			if (getIntent().getStringExtra(Global.FROM_ACTIVITY) != null) {
				mFrom = getIntent().getStringExtra(Global.FROM_ACTIVITY);
			}
		}

		myOrderList = (ListView) findViewById(android.R.id.list);
		mTxtHeader = (TextView) findViewById(R.id.txtHeader);
		mBtnForceSyncOrders = (ImageButton) findViewById(R.id.imgBtnRefresh);
		mTxtNotificationCount = (TextView)findViewById(R.id.txtNotificationNumber);
		mRelNotificationCentre = (RelativeLayout)findViewById(R.id.relNotificationCentre);
		
		txtOrderHeader = (TextView)findViewById(R.id.txtOrderHeader);
		txtSectionHeader = (TextView)findViewById(R.id.txtSectionHeader);
		txtTableHeader = (TextView)findViewById(R.id.txtTableHeader);
		txtTotalHeader = (TextView)findViewById(R.id.txtTotalHeader);

		mTxtLeftMenuOrders = (TextView) view
				.findViewById(R.id.txtOrdersLeftMenu);
		mTxtLeftMenuSettings = (TextView) view
				.findViewById(R.id.txtSettingsLeftMenu);
		mTxtLeftMenuNotifications = (TextView) view
				.findViewById(R.id.txtNotificationsLeftMenu);
		mTxtLeftMenuTables = (TextView) view
				.findViewById(R.id.txtTablesLeftMenu);
		mTxtLeftMenuWaiterName = (TextView) view
				.findViewById(R.id.txtWaiterName);
		mTxtLeftMenuLink = (TextView) view.findViewById(R.id.txtLeftMenuLink);
		mBtnPlaceOrderLeftMenu = (Button) view
				.findViewById(R.id.btnPlaceOrderLeftMenu);
		imageMenuIcon = (ImageButton) findViewById(R.id.menuIcon);
		mBtnLockLeftMenu = (Button) view.findViewById(R.id.btnLeftMenuLock);
		mBtnChangeWaiterLeftMenu = (Button) view
				.findViewById(R.id.btnLeftMenuWaiterChange);

		mTxtLeftMenuLink.setMovementMethod(LinkMovementMethod.getInstance());
		mTxtLeftMenuLink.setLinksClickable(true);

		mTxtLeftMenuOrders.setOnClickListener(this);
		mTxtLeftMenuSettings.setOnClickListener(this);
		mTxtLeftMenuNotifications.setOnClickListener(this);
		mTxtLeftMenuTables.setOnClickListener(this);
		mBtnPlaceOrderLeftMenu.setOnClickListener(this);
		imageMenuIcon.setOnClickListener(this);
		mBtnLockLeftMenu.setOnClickListener(this);
		mBtnChangeWaiterLeftMenu.setOnClickListener(this);
		mTxtLeftMenuLink.setOnClickListener(this);
		mBtnForceSyncOrders.setOnClickListener(this);
		mRelNotificationCentre.setOnClickListener(this);

		mTxtLeftMenuOrders.setPressed(true);
		Prefs.addKey(MyOrderActivity.this, Prefs.MENU_SELECTED, Global.ORDERS);

		mWaiterId = Prefs.getKey(Prefs.WAITER_ID);
		mSectionId = Prefs.getKey(Prefs.SECTION_ID);
		mWaiterName = Prefs.getKey(Prefs.WAITER_NAME);

		mTxtLeftMenuWaiterName.setText(mWaiterName);
		// setSliderWindow();

		mBtnForceSyncOrders.setVisibility(View.VISIBLE);

		setGui();

		getOrders();

		mOpenOrdersAdapter = new MyOrdersAdapter(MyOrderActivity.this,
				R.layout.individual_my_orders, mOrderListToUse);

		myOrderList.setAdapter(mOpenOrdersAdapter);
		myOrderList.setOnItemClickListener(this);
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

		mBtnChangeWaiterLeftMenu.post(new Runnable() {

			@Override
			public void run() {
				mBtnChangeWaiterLeftMenu.setWidth(offset);
				Log.i("Dhara", "mBtnChangeWaiterLeftMenu "
						+ mBtnChangeWaiterLeftMenu.getWidth());
			}
		});
	}

	private void setGui() {
		mBtnPlaceOrderLeftMenu.setText(mLanguageManager.getPlaceOrder());
		mTxtLeftMenuNotifications.setText(mLanguageManager.getNotifications());
		mTxtLeftMenuOrders.setText(mLanguageManager.getOrders());
		mTxtLeftMenuSettings.setText(mLanguageManager.getSettings());
		mTxtLeftMenuTables.setText(mLanguageManager.getTables());
		mBtnLockLeftMenu.setText(mLanguageManager.getLock());
		mBtnChangeWaiterLeftMenu.setText(mLanguageManager.getChangeWaiter());
		mTxtHeader.setText(mLanguageManager.getOpenOrders());
		
		mBtnPlaceOrderLeftMenu.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtLeftMenuNotifications.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtLeftMenuOrders.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtLeftMenuSettings.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtLeftMenuTables.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mBtnLockLeftMenu.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mBtnChangeWaiterLeftMenu.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtLeftMenuWaiterName.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		
		txtTotalHeader.setText(mLanguageManager.getTotalHeader());
		txtOrderHeader.setText(mLanguageManager.getOrderHeader());
		txtSectionHeader.setText(mLanguageManager.getSectionHeader());
		txtTableHeader.setText(mLanguageManager.getTableHeader());
		
		txtTotalHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		txtOrderHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		txtSectionHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		txtTableHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		
		setButtonWidth();
	}

	public void finish() {
		super.finish();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// clicking on an order should lead to the order related screen
		Order order = mOrderListToUse.get(position);

		if (order != null && order.getOrderId() != null) {
			List<Order> runningOrders = (List<Order>) OrderManager
					.getInstance().getOrderCache().get(Global.RUNNING_ORDERS);
			if (runningOrders != null && runningOrders.size() > 0) {
				Order orderToDisplay = Iterables.find(runningOrders,
						new SearchForOrder(order.getOrderId()), null);
				if (orderToDisplay != null) {
					Intent intent = new Intent(MyOrderActivity.this,
							OrderRelatedActivity.class);
					intent.putExtra(Global.TABLE_ID, order.getTable()
							.getTableId());
					intent.putExtra(Global.TABLE_NUMBER,
							String.valueOf(order.getTable().getTableNumber()));
					intent.putExtra(Global.ORDER_ID, order.getOrderId());
					intent.putExtra(Global.ORDER_NUMBER, order.toString());
					intent.putExtra(Global.FROM_ACTIVITY, Global.HOME);

					intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
					startActivity(intent);
					// activity in animation
					Global.activityStartAnimationRightToLeft(MyOrderActivity.this);
					logger.debug(TAG + " ---- ENDS HERE ---- ");
				}
			}
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
			Global.activityStartAnimationRightToLeft(MyOrderActivity.this);
			break;

		case R.id.btnPlaceOrderLeftMenu:
			// lead to the table selection list
			Prefs.addKey(MyOrderActivity.this, Prefs.MENU_SELECTED,
					Global.TABLES);
			mTxtLeftMenuTables.setPressed(true);
			Intent intent = new Intent(this, TableListActivity.class);
			startActivity(intent);
			Global.activityStartAnimationRightToLeft(MyOrderActivity.this);
			break;

		case R.id.txtSettingsLeftMenu:
			mTxtLeftMenuSettings.setPressed(true);
			Prefs.addKey(MyOrderActivity.this, Prefs.MENU_SELECTED,
					Global.SETTINGS);
			mTxtLeftMenuSettings.setSelected(true);
			intent = new Intent(MyOrderActivity.this, SettingsActivity.class);
			intent.putExtra(Global.FROM_ACTIVITY, Global.FROM_HOME_ACTIVITY);
			startActivity(intent);
			Global.activityStartAnimationRightToLeft(MyOrderActivity.this);
			finish();
			break;

		case R.id.txtOrdersLeftMenu:
			// do nothing
			toggle();
			Prefs.addKey(MyOrderActivity.this, Prefs.MENU_SELECTED,
					Global.ORDERS);
			mTxtLeftMenuOrders.setPressed(true);
			break;

		case R.id.txtNotificationsLeftMenu:
			// take to the notification screen
			Prefs.addKey(MyOrderActivity.this, Prefs.MENU_SELECTED,
					Global.NOTIFICATIONS);
			mTxtLeftMenuNotifications.setPressed(true);
			intent = new Intent(this, NotificationListActivity.class);
			startActivity(intent);
			Global.activityStartAnimationRightToLeft(MyOrderActivity.this);
			break;
			
		case R.id.relNotificationCentre:
			Prefs.addKey(MyOrderActivity.this, Prefs.MENU_SELECTED,
					Global.NOTIFICATIONS);
			mTxtLeftMenuNotifications.setPressed(true);
			intent = new Intent(this, NotificationListActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.activity_in_anim,R.anim.activity_out_anim); 
			break;

		case R.id.txtTablesLeftMenu:
			// same page
			// do nothing
			Prefs.addKey(MyOrderActivity.this, Prefs.MENU_SELECTED,
					Global.TABLES);
			mTxtLeftMenuTables.setSelected(true);
			intent = new Intent(this, TableListActivity.class);
			startActivity(intent);
			Global.activityStartAnimationRightToLeft(MyOrderActivity.this);
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
			Global.activityStartAnimationRightToLeft(MyOrderActivity.this);
			break;

		case R.id.btnLeftMenuWaiterChange:
			callDialog();
			break;

		case R.id.imgBtnRefresh:
			mGetCurrentOrderListAsyncTask = new GetCurrentOrderListAsyncTask(
					MyOrderActivity.this, Global.MY_ORDERS);
			mGetCurrentOrderListAsyncTask.execute();
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

	// changes as on 6th November 2013
	// replace the orders and refresh the current screen
	public void refreshOrders(OrderList result) {
		OrderManager.getInstance().storeCurrentOrdersInMemory(result);
		refresh();
	}
	
	private void refresh() {
		getOrders();
		mOpenOrdersAdapter = new MyOrdersAdapter(MyOrderActivity.this,
				R.layout.individual_my_orders, mOrderListToUse);

		myOrderList.setAdapter(mOpenOrdersAdapter);
	}

	/**
	 * Get all the open orders and set adapter Clubbed all into one method as on
	 * 6th July 2013
	 */
	private void getOrders() {
		mOrderPerWaiter = (HashMap<String, List<Order>>) OrderManager
				.getInstance().getOrderCache().get(Global.ORDER_PER_WAITER);

		if (mOrderPerWaiter != null) {
			mOrderList = mOrderPerWaiter.get(mWaiterId);
		}else {
			mOrderList = new ArrayList<Order>();
		}

		mOrderListToUse = mOrderList;

		// ******
		// Changes as on 28th November 2013
		// Commenting this section, 
		// as all orders of the waiter are required

		// Requirement changed as on 28th November 2013
		// changes as on 19th July 2013
		// get the orders that are open
		/* if(mOrderList != null && mOrderList.size() > 0) {
					Collection<Order> collectionOrder = Collections2.filter(mOrderList,
							new SearchForRunningOrders());
					mOrderListToUse = new ArrayList<Order>(collectionOrder);
				} */

		// changed end here
	}

	// changes end here

	private void callDialog() {
		builderEnterPin = new Dialog(MyOrderActivity.this);
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
		
		txtEnterPinToLogout.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		editEnterPinToExit.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		btnCancel.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());

		String enterPin = "";
		String cancel = "";

		enterPin = mLanguageManager.getEnterPin();
		cancel = mLanguageManager.getCancel();

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
									MyOrderActivity.this, wsWaiterPin, Global.HOME);
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

				String message = "";
				message = mLanguageManager.getInvalidUser();

				showMessageBox(message);

				if (editEnterPinToExit != null) {
					editEnterPinToExit.setText("");
				}
			} else {
				if (builderEnterPin != null) {
					builderEnterPin.dismiss();
				}
				
				String[] keys = new String[] { Prefs.WAITER_CODE,
						Prefs.WAITER_ID, Prefs.WAITER_NAME, Prefs.USER_PIN };
				Prefs.removeKeys(MyOrderActivity.this, keys);

				Intent intent = new Intent(MyOrderActivity.this,
						LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_CLEAR_TASK
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(Global.FROM_ACTIVITY, Global.FROM_SPLASH);
				startActivity(intent);
				Global.activityStartAnimationRightToLeft(MyOrderActivity.this);
				
				Log.i("dhara", "user logged out ");
			}
		} else {
			String message = "";
			message = mLanguageManager.getServerUnreachable();

			if(mUtils.isConnected()) {
				String[] keys = new String[] { Prefs.WAITER_CODE,
						Prefs.WAITER_ID, Prefs.WAITER_NAME, Prefs.USER_PIN };
				Prefs.removeKeys(MyOrderActivity.this, keys);
				
				Intent intent = new Intent(MyOrderActivity.this,
						LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_CLEAR_TASK
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(Global.FROM_ACTIVITY, Global.FROM_SPLASH);
				startActivity(intent);
				Global.activityStartAnimationRightToLeft(MyOrderActivity.this);
			}else {
				showMessageBox(message);

				if (editEnterPinToExit != null) {
					editEnterPinToExit.setText("");
				}
			}
		}
	}

	private void showMessageBox(String message) {
		if (message != null) {

			String appName = mLanguageManager.getAppName();
			String ok = mLanguageManager.getOk();

			CommonDialog dialog = new CommonDialog(MyOrderActivity.this,
					appName, ok, message);
			dialog.show();
		}
	}
}
