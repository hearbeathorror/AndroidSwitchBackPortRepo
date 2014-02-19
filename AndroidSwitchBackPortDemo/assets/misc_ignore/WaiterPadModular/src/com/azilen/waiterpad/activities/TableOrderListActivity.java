package com.azilen.waiterpad.activities;

import java.util.ArrayList;
import java.util.Collection;
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
import com.azilen.waiterpad.WaiterPadApplication;
import com.azilen.waiterpad.adapters.PerTableOrderAdapter;
import com.azilen.waiterpad.asynctask.CheckoutOrderAsyncTask;
import com.azilen.waiterpad.asynctask.GetCurrentOrderListAsyncTask;
import com.azilen.waiterpad.asynctask.LogoutAsyncTask;
import com.azilen.waiterpad.controls.CommonDialog;
import com.azilen.waiterpad.data.CheckOutOrderDetails;
import com.azilen.waiterpad.data.CheckoutData;
import com.azilen.waiterpad.data.Guest;
import com.azilen.waiterpad.data.Order;
import com.azilen.waiterpad.data.Order.OrderStatus;
import com.azilen.waiterpad.data.Tables;
import com.azilen.waiterpad.data.WSWaiterPin;
import com.azilen.waiterpad.data.WaiterPadResponse;
import com.azilen.waiterpad.managers.font.FontManager;
import com.azilen.waiterpad.managers.language.LanguageManager;
import com.azilen.waiterpad.managers.network.RequestType;
import com.azilen.waiterpad.managers.notification.NotificationManager;
import com.azilen.waiterpad.managers.order.OrderManager;
import com.azilen.waiterpad.utils.Global;
import com.azilen.waiterpad.utils.Prefs;
import com.azilen.waiterpad.utils.Utils;
import com.azilen.waiterpad.utils.Validator;
import com.azilen.waiterpad.utils.search.SearchForGuestWithName;
import com.azilen.waiterpad.utils.search.SearchForOrder;
import com.azilen.waiterpad.utils.search.SearchForOrdersWithOneGuest;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class TableOrderListActivity extends BaseActivity implements
		OnItemClickListener, OnClickListener {
	private TextView mTxtHeader;
	private Button mBtnNewOrder;
	private ListView mTableWiseOrderListView;
	private String mTableId;
	private String mTableNumber;
	private String mWaiterName;
	private String mTimestamp;

	private String mSectionId;

	private MultiKeyMap mTableOrdersSectionwiseMap;
	private LruCache<String, Object> orderCache;
	private List<Order> lstRunningOrders;

	private PerTableOrderAdapter mPerTableOrderAdapter;
	private List<Order> orderWithGuestOne;
	private Collection<Order> guestCollection;
	private Collection<Order> collection;
	private String mFrom;

	private Button mBtnPlaceOrderLeftMenu;
	private TextView mTxtLeftMenuSettings;
	private TextView mTxtLeftMenuTables;
	private TextView mTxtLeftMenuOrders;
	private TextView mTxtLeftMenuNotifications;
	private TextView mTxtLeftMenuWaiterName;
	private Button mBtnLeftMenuLock;
	private Button mBtnLeftMenuWaiterChange;
	private TextView mTxtLeftMenuLink;
	private ImageButton imageMenuIcon;
	private View view;

	private Button btnCancel;
	private EditText editEnterPinToExit;
	private Dialog builderEnterPin;
	private LogoutAsyncTask logoutAsyncTask;

	private GetCurrentOrderListAsyncTask mGetCurrentOrderListAsyncTask;
	private ImageButton mBtnForceSync;

	// changes as on 28th November 2013
	private TextView mTxtNotificationCount;
	private BroadcastReceiver updateReciver = null;
	private RelativeLayout mRelNotificationCentre;
	// changes end here
	
	private String TAG = this.getClass().getSimpleName();

	@Override
	public void onBackPressed() {
		if (mFrom != null && mFrom.equalsIgnoreCase("tablelist")) {
			Intent intent = new Intent(TableOrderListActivity.this,
					TableListActivity.class);
			startActivity(intent);

			// activity out animation
			Global.activityFinishAnimationLeftToRight(TableOrderListActivity.this);
			finish();
		} else {
			super.onBackPressed();
		}
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
	public void onResume() {
		super.onResume();
		Prefs.addKey(TableOrderListActivity.this, Prefs.MENU_SELECTED, "tables");
		setSelectedView();

		if (getSlidingMenu() != null) {
			if (getSlidingMenu().isMenuShowing()) {
				toggle();
			}
		}
		
		// changes as on 2nd Jan 2014
		StartActivity.currentIntent = getIntent();
		// changes end here
		
		// changes as on 28th November 2013
		if(mTxtNotificationCount != null) {
			mTxtNotificationCount.setText(String.valueOf(NotificationManager.getInstance().getNotificationOrderCount()));
		}
		
		if (updateReciver == null) {
			IntentFilter notification_filter = new IntentFilter();
			notification_filter.addAction(getString(R.string.broadcast_notification_update));
			notification_filter.addAction(getString(R.string.kill));
			notification_filter.addAction(getString(R.string.broadcast_tableorders_update));

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
					
					if(intent.getAction()
							.equalsIgnoreCase(getString(R.string.broadcast_tableorders_update))) {
						refreshOrders();
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
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_table_orderlist);
		
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

		SlidingMenu menu = new SlidingMenu(this);
		menu.setShadowWidthRes(R.dimen.shadow_res);
		menu.setFadeDegree(0.35f);

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

		orderCache = OrderManager.getInstance().getOrderCache();
				
		mBtnNewOrder = (Button) findViewById(R.id.btnNewOrderFooter);
		mBtnNewOrder.setOnClickListener(this);

		mBtnForceSync = (ImageButton) findViewById(R.id.imgBtnRefresh);
		mBtnForceSync.setOnClickListener(this);
		mBtnForceSync.setVisibility(View.VISIBLE);
		
		mTxtNotificationCount = (TextView)findViewById(R.id.txtNotificationNumber);
		mRelNotificationCentre = (RelativeLayout)findViewById(R.id.relNotificationCentre);

		mSectionId = Prefs.getKey(Prefs.SECTION_ID);
		mWaiterName = Prefs.getKey(Prefs.WAITER_NAME);

		if (getIntent().getExtras() != null) {
			mTableId = getIntent().getStringExtra(Global.TABLE_ID);
			mTableNumber = getIntent().getStringExtra(Global.TABLE_NUMBER);

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
		mBtnLeftMenuLock = (Button) view.findViewById(R.id.btnLeftMenuLock);
		mBtnLeftMenuWaiterChange = (Button) view
				.findViewById(R.id.btnLeftMenuWaiterChange);
		mTxtLeftMenuWaiterName = (TextView) view
				.findViewById(R.id.txtWaiterName);
		imageMenuIcon = (ImageButton) findViewById(R.id.menuIcon);
		mTxtLeftMenuLink = (TextView) view.findViewById(R.id.txtLeftMenuLink);

		mTxtLeftMenuLink.setMovementMethod(LinkMovementMethod.getInstance());
		mTxtLeftMenuLink.setLinksClickable(true);

		mTxtLeftMenuOrders.setOnClickListener(this);
		mTxtLeftMenuSettings.setOnClickListener(this);
		mTxtLeftMenuNotifications.setOnClickListener(this);
		mTxtLeftMenuTables.setOnClickListener(this);
		mBtnPlaceOrderLeftMenu.setOnClickListener(this);
		mBtnLeftMenuLock.setOnClickListener(this);
		mBtnLeftMenuWaiterChange.setOnClickListener(this);
		imageMenuIcon.setOnClickListener(this);
		mTxtLeftMenuLink.setOnClickListener(this);
		mRelNotificationCentre.setOnClickListener(this);

		Prefs.addKey(Prefs.MENU_SELECTED, Global.TABLES);
		setSelectedView();

		mTxtLeftMenuWaiterName.setText(mWaiterName);

		// if (mLangSelection != null) {
		// mLanguageXml = (LanguageXml) LanguageManager.getInstance()
		// .getLanguageCache().get(mLangSelection);
		// }

		mTxtHeader = (TextView) findViewById(R.id.txtHeader);
		setGuiLabels();
		mTableWiseOrderListView = (ListView) findViewById(android.R.id.list);

		// Get all the orders related to the table
		// changes as on 11th November 2013
		lstRunningOrders = new ArrayList<Order>();
		getAllTheOrdersRelatedToTheTable();
		// changes end here

		mTableWiseOrderListView.setOnItemClickListener(this);

		setButtonWidth();
	}

	/**
	 * Changes as on 11th November 2013 Clubbed all methods into one function
	 * Gets all the orders related to the table clicked on
	 */
	private void getAllTheOrdersRelatedToTheTable() {
		mTableOrdersSectionwiseMap = (MultiKeyMap) orderCache.get(Global.PER_TABLE_ORDERS);

		if (mTableOrdersSectionwiseMap != null) {
			if (mTableId != null && mSectionId != null) {
				if (mTableOrdersSectionwiseMap.get(mSectionId, mTableId) != null) {
					// gets the list of running orders under a section id and
					// table id
					lstRunningOrders = (List<Order>) mTableOrdersSectionwiseMap
							.get(mSectionId, mTableId);
				}else {
					lstRunningOrders = new ArrayList<Order>();
				}
			}
		} else {
			lstRunningOrders = new ArrayList<Order>();
		}

		String orderLabel = LanguageManager.getInstance().getOrder() + " : ";

		mPerTableOrderAdapter = new PerTableOrderAdapter(this,
				R.layout.individual_pertableorder, lstRunningOrders, orderLabel);

		Global.logd(TAG + " lst of running orders " + lstRunningOrders.size());

		mTableWiseOrderListView.setAdapter(mPerTableOrderAdapter);
		mPerTableOrderAdapter.notifyDataSetChanged();
	}

	public void refreshOrders() {
		getAllTheOrdersRelatedToTheTable();
	}

	public void setButtonWidth() {
		final int offset = getWidth() / 3;
		Global.logd(TAG + " offset " + offset);

		mBtnLeftMenuLock.post(new Runnable() {

			@Override
			public void run() {
				mBtnLeftMenuLock.setWidth(offset);
				Global.logd(TAG + " mBtnLockLeftMenu "
						+ mBtnLeftMenuLock.getWidth());
			}
		});

		mBtnLeftMenuWaiterChange.post(new Runnable() {

			@Override
			public void run() {
				mBtnLeftMenuWaiterChange.setWidth(offset);
				Global.logd(TAG + " mBtnChangeWaiterLeftMenu "
						+ mBtnLeftMenuWaiterChange.getWidth());
			}
		});

	}

	@Override
	public void finish() {
		super.finish();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Order order = lstRunningOrders.get(position);

		lstRunningOrders = (List<Order>) 
				orderCache.get(Global.RUNNING_ORDERS);

		if (lstRunningOrders != null && lstRunningOrders.size() > 0) {
			Order orderToDisplay = Iterables.find(lstRunningOrders,
					new SearchForOrder(order.getOrderId()), null);
			if (orderToDisplay != null) {
				if (order.getOrderId() != null) {
					Global.logd(TAG + " orderId : " + order.getOrderId());
				}

				// changes as on 12th June 2013
				// considering those orders that were bill split
				if (lstRunningOrders != null) {
					if (order.getOrderStatus() != null
							&& order.getOrderStatus().ordinal() != 2) {
						collection = Collections2.filter(lstRunningOrders,
								new SearchForOrdersWithOneGuest());

						// convert the collection to a list of orders
						// orders having one guest only
						Order orderNew = new Order();
						List<Guest> guests = new ArrayList<Guest>();
						List<Order> ordersWithOneGuest = new ArrayList<Order>(
								collection);

						// Get the list of guests with the name "Guest 1" or
						// language specific value

						if (ordersWithOneGuest != null) {
							guestCollection = Collections2.filter(
									ordersWithOneGuest,
									new SearchForOrdersWithOneGuest(
											LanguageManager.getInstance()
													.getGuest() + " 1"));
						}

						if (guestCollection != null) {
							// order list with one guest having Guest 1 name
							orderWithGuestOne = new ArrayList<Order>(
									guestCollection);

							if (orderWithGuestOne != null) {
								// all the orders are one guest orders
								// they cannot be merged
								if (orderWithGuestOne.size() == ordersWithOneGuest
										.size()) {
									// do nothing
								}

								// there is just one order with the name Guest 1
								// and many orders that may have just one guest
								// name is not known in that case
								List<Guest> lstGuests = new ArrayList<Guest>();

								if (orderWithGuestOne.size() == 1
										&& ordersWithOneGuest.size() > 1) {
									for (int i = 0; i < ordersWithOneGuest
											.size(); i++) {
										if (ordersWithOneGuest.get(i)
												.getGuests() != null) {
											List<Guest> guestList = ordersWithOneGuest
													.get(i).getGuests();

											for (int j = 0; j < guestList
													.size(); j++) {
												Guest guest = guestList.get(j);
												guest.setOrderId(ordersWithOneGuest
														.get(i).getOrderId());
											}

											lstGuests.addAll(ordersWithOneGuest
													.get(i).getGuests());
										}
									}

									if (lstGuests.size() > 1) {
										for (int j = 0; j < lstGuests.size(); j++) {
											String guestname = "Guest "
													+ (j + 1);

											Guest guest = Iterables.find(
													lstGuests,
													new SearchForGuestWithName(
															guestname), null);

											if (guest != null) {
												// add the guest to the list of
												// guests
												guests.add(guest);
											} else {
												continue;
											}
										}
									}
								}
							}

						}

						// send the waiter to the order taking screen
						// OrderRelatedFragment screen
						Intent intent = new Intent(TableOrderListActivity.this,
								OrderRelatedActivity.class);

						intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

						intent.putExtra(Global.TABLE_NUMBER, mTableNumber);
						intent.putExtra(Global.TABLE_ID, mTableId);
						intent.putExtra(Global.FROM_ACTIVITY,
								Global.FROM_TABLE_ORDER_LIST);

						// there is a set of guests available
						if (guests.size() > 0) {
							orderNew.setGuests(guests);
							orderNew.setBillSplit(true);
							orderNew.setModified(false);
							orderNew.setTable(setNewTable());
							orderNew.setOrderId(orderWithGuestOne.get(0)
									.getOrderId());
							orderNew.setOrderNumber(orderWithGuestOne.get(0)
									.getOrderNumber());

							// set the order into the memcache
							saveOrderIntoCache(orderNew);
							intent.putExtra(Global.TIME_STAMP, mTimestamp);
						} else {
							// its not a split order
							// Get the orderid and order number

							if (order.getOrderId() != null) {
								String orderId = order.getOrderId();
								String orderNumber = String.valueOf(order
										.getOrderNumber());

								intent.putExtra(Global.ORDER_ID, orderId);
								intent.putExtra(Global.ORDER_NUMBER,
										orderNumber);
								// intent.putExtra("timestamp","");
							}
						}

						// changes end here
						startActivity(intent);
						// activity in animation
						Global.activityStartAnimationRightToLeft(TableOrderListActivity.this);
						finish();
					} else {
						// send the waiter to the order taking screen
						// OrderRelatedFragment screen
						Intent intent = new Intent(TableOrderListActivity.this,
								OrderRelatedActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
						intent.putExtra(Global.TABLE_NUMBER, mTableNumber);
						intent.putExtra(Global.TABLE_ID, mTableId);
						intent.putExtra(Global.FROM_ACTIVITY,
								Global.FROM_TABLE_ORDER_LIST);

						if (order.getOrderId() != null) {
							String orderId = order.getOrderId();
							String orderNumber = String.valueOf(order
									.getOrderNumber());

							intent.putExtra(Global.ORDER_ID, orderId);
							intent.putExtra(Global.ORDER_NUMBER, orderNumber);
							// intent.putExtra("timestamp","");
						}
						startActivity(intent);

						// activity in animation
						Global.activityStartAnimationRightToLeft(TableOrderListActivity.this);
						finish();
					}
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// changes as on 11th November 2013
		// adding this in order to refresh the order listing present
		case R.id.imgBtnRefresh:
			mGetCurrentOrderListAsyncTask = new GetCurrentOrderListAsyncTask(
					TableOrderListActivity.this, Global.FROM_TABLE_ORDER_LIST);
			mGetCurrentOrderListAsyncTask.execute();
			break;

		case R.id.txtLeftMenuLink:
			Intent browserIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://" + getString(R.string.site)));
			
			// changes as on 2nd Jan 2014
			StartActivity.currentIntent = browserIntent;
			// changes end here
			
			startActivity(browserIntent);
			Global.activityStartAnimationRightToLeft(TableOrderListActivity.this);
			break;

		case R.id.btnNewOrderFooter:
			Intent intent = new Intent(TableOrderListActivity.this,
					OrderRelatedActivity.class);
			intent.putExtra(Global.TABLE_NUMBER, mTableNumber);
			intent.putExtra(Global.TABLE_ID, mTableId);
			intent.putExtra(Global.ORDER_ID, "");
			intent.putExtra(Global.ORDER_NUMBER, "");
			intent.putExtra(Global.TIME_STAMP, "");
			intent.putExtra(Global.FROM_ACTIVITY, Global.FROM_TABLE_ORDER_LIST);
			startActivity(intent);
			// activity in animation
			Global.activityStartAnimationRightToLeft(TableOrderListActivity.this);
			finish();
			break;

		case R.id.btnPlaceOrderLeftMenu:
			Prefs.addKey(Prefs.MENU_SELECTED, Global.TABLES);
			setSelectedView();
			// lead to the table selection list
			intent = new Intent(this, TableListActivity.class);
			startActivity(intent);
			Global.activityStartAnimationRightToLeft(TableOrderListActivity.this);
			break;

		case R.id.txtSettingsLeftMenu:
			Prefs.addKey(Prefs.MENU_SELECTED, Global.SETTINGS);
			setSelectedView();
			// onClick(findViewById(R.id.menuIcon));
			intent = new Intent(TableOrderListActivity.this,
					SettingsActivity.class);
			startActivity(intent);
			Global.activityStartAnimationRightToLeft(TableOrderListActivity.this);
			break;

		case R.id.txtOrdersLeftMenu:
			Prefs.addKey(Prefs.MENU_SELECTED, Global.ORDERS);
			setSelectedView();
			finish();
			intent = new Intent(TableOrderListActivity.this,
					MyOrderActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			Global.activityStartAnimationRightToLeft(TableOrderListActivity.this);
			break;

		case R.id.txtNotificationsLeftMenu:
			// take to the notification screen
			Prefs.addKey(Prefs.MENU_SELECTED, Global.NOTIFICATIONS);
			setSelectedView();
			intent = new Intent(this, NotificationListActivity.class);
			startActivity(intent);
			Global.activityStartAnimationRightToLeft(TableOrderListActivity.this);
			break;
			
		case R.id.relNotificationCentre:
			// take to the notification screen
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
			Global.activityStartAnimationRightToLeft(TableOrderListActivity.this);
			break;

		case R.id.menuIcon:
			// super.onClick(v);
			toggle();
			setSelectedView();
			break;

		case R.id.btnLeftMenuLock:
			intent = new Intent(this, LoginActivity.class);
			intent.putExtra(Global.ACTION, Global.FROM_LOCK);
			startActivity(intent);
			Global.activityStartAnimationRightToLeft(TableOrderListActivity.this);
			break;

		case R.id.btnLeftMenuWaiterChange:
			callDialog();
			break;

		default:
			break;
		}
	}

	/**
	 * Sets the table details
	 * 
	 * @return
	 */
	private Tables setNewTable() {
		Tables table = new Tables();
		table.setSectionId(mSectionId);
		table.setTableId(mTableId);
		table.setTableNumber(Integer.parseInt(mTableNumber));
		return table;
	}

	/**
	 * Saves the order into cache
	 * 
	 * @param orderToSave
	 */
	private void saveOrderIntoCache(Order orderToSave) {
		HashMap<String, Order> orderMapCached = new HashMap<String, Order>();
		mTimestamp = String.valueOf(System.currentTimeMillis());
		orderMapCached.put(mTimestamp, orderToSave);
		orderCache.put(Global.CURRENT_ORDER, orderMapCached);
	}

	/**
	 * Sets the labels on the gui
	 */
	private void setGuiLabels() {
		mTxtHeader.setText(LanguageManager.getInstance().getOrderList());
		mBtnNewOrder.setText(LanguageManager.getInstance().getNewOrder());
		mTxtLeftMenuNotifications.setText(LanguageManager.getInstance()
				.getNotifications());
		mTxtLeftMenuOrders.setText(LanguageManager.getInstance().getOrders());
		mTxtLeftMenuSettings.setText(LanguageManager.getInstance()
				.getSettings());
		mTxtLeftMenuTables.setText(LanguageManager.getInstance().getTables());
		mBtnPlaceOrderLeftMenu.setText(LanguageManager.getInstance()
				.getPlaceOrder());
		mBtnLeftMenuWaiterChange.setText(LanguageManager.getInstance()
				.getChangeWaiter());
		mBtnLeftMenuLock.setText(LanguageManager.getInstance().getLock());
		
		mBtnPlaceOrderLeftMenu.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtLeftMenuNotifications.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtLeftMenuOrders.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtLeftMenuSettings.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtLeftMenuTables.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mBtnNewOrder.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mBtnLeftMenuLock.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mBtnLeftMenuWaiterChange.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		
		mTxtLeftMenuWaiterName.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		
		setButtonWidth();
	}

	public void sendCheckoutRequest(String orderId) {
		CheckoutData checkoutData = new CheckoutData();

		CheckOutOrderDetails checkOutOrderDetails = new CheckOutOrderDetails();
		checkOutOrderDetails.setOrderId(orderId);
		checkOutOrderDetails.setMacAddress(Utils.getMacAddress());
		checkOutOrderDetails.setWaiterCode(Prefs.getKey(Prefs.WAITER_CODE));

		checkoutData.setCheckOutOrderDetails(checkOutOrderDetails);

		new CheckoutOrderAsyncTask(TableOrderListActivity.this, checkoutData,
				RequestType.CHECKOUT_ORDER, Global.FROM_TABLE_ORDER_LIST)
				.execute();
	}

	public void showMessage(String message) {
		if (message != null) {
			String appName = LanguageManager.getInstance().getAppName();
			String ok = LanguageManager.getInstance().getOk();

			CommonDialog dialog = new CommonDialog(TableOrderListActivity.this,
					appName, ok, message);
			dialog.show();
		}
	}

	public void refresh(String orderId) {
		showMessage(LanguageManager.getInstance().getCheckoutSuccess());

		if (lstRunningOrders != null) {
			Order order = Iterables.find(lstRunningOrders, new SearchForOrder(
					orderId), null);

			if (order != null) {
				lstRunningOrders.remove(order);
				order.setOrderStatus(OrderStatus.BILL);
				lstRunningOrders.add(order);
			}
		}

		if (mPerTableOrderAdapter != null) {
			mPerTableOrderAdapter.notifyDataSetChanged();
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

	private void callDialog() {
		builderEnterPin = new Dialog(TableOrderListActivity.this);

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

		txtEnterPinToLogout.setText(LanguageManager.getInstance()
				.getEnterPinToLogout());

		editEnterPinToExit = (EditText) view.findViewById(R.id.editPinToExit);
		btnCancel = (Button) view.findViewById(R.id.btnCloseLayout);

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editEnterPinToExit.getWindowToken(), 0);

		String enterPin = LanguageManager.getInstance().getEnterPin();
		String cancel = LanguageManager.getInstance().getCancel();

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
									TableOrderListActivity.this, wsWaiterPin,
									Global.FROM_TABLE_ORDER_LIST);
							logoutAsyncTask.execute();
						}else {
							String message = LanguageManager.getInstance()
									.getEnterValidPin();

							showMessage(message);
						}
					} else {
						String message = LanguageManager.getInstance()
								.getEnterValidPin();

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

				String message = LanguageManager.getInstance().getInvalidUser();

				showMessage(message);

				if (editEnterPinToExit != null) {
					editEnterPinToExit.setText("");
				}
			} else {
				if (builderEnterPin != null) {
					builderEnterPin.dismiss();
				}
				Intent intent = new Intent(TableOrderListActivity.this,
						LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_CLEAR_TASK
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(Global.FROM_ACTIVITY, Global.FROM_SPLASH);
				startActivity(intent);
				finish();
				Global.activityStartAnimationRightToLeft(TableOrderListActivity.this);
			}
		} else {
			String message = LanguageManager.getInstance()
					.getServerUnreachable();

			showMessage(message);

			if (editEnterPinToExit != null) {
				editEnterPinToExit.setText("");
			}
		}
	}
}
