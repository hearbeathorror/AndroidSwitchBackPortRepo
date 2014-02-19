package com.azilen.waiterpad.activities;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.FilterQueryProvider;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.WaiterPadApplication;
import com.azilen.waiterpad.adapters.CategoryListAdapter;
import com.azilen.waiterpad.adapters.ItemAdapter;
import com.azilen.waiterpad.adapters.ModifierCursorAdapter;
import com.azilen.waiterpad.adapters.ModifierCursorTreeAdapter;
import com.azilen.waiterpad.adapters.OrderRelatedAdapter;
import com.azilen.waiterpad.asynctask.CheckoutListOrderAsyncTask;
import com.azilen.waiterpad.asynctask.CheckoutOrderAsyncTask;
import com.azilen.waiterpad.asynctask.GetCurrentOrderListAsyncTask;
import com.azilen.waiterpad.asynctask.LogoutAsyncTask;
import com.azilen.waiterpad.asynctask.SendOrderToAsyncTask;
import com.azilen.waiterpad.asynctask.UpdateOrderAsyncTask;
import com.azilen.waiterpad.asynctask.UpdateOrderListAsyncTask;
import com.azilen.waiterpad.controls.CommonDialog;
import com.azilen.waiterpad.data.CategoryMaster;
import com.azilen.waiterpad.data.CheckOutOrderDetails;
import com.azilen.waiterpad.data.CheckoutData;
import com.azilen.waiterpad.data.CheckoutDataList;
import com.azilen.waiterpad.data.CheckoutDataToSend;
import com.azilen.waiterpad.data.Guest;
import com.azilen.waiterpad.data.ItemAndGuestRelatedTemp;
import com.azilen.waiterpad.data.ItemMaster;
import com.azilen.waiterpad.data.ListOfOrders;
import com.azilen.waiterpad.data.ModifierMaster;
import com.azilen.waiterpad.data.Order;
import com.azilen.waiterpad.data.OrderList;
import com.azilen.waiterpad.data.OrderedItem;
import com.azilen.waiterpad.data.OrderedItem.OrderedItemStatus;
import com.azilen.waiterpad.data.Tables;
import com.azilen.waiterpad.data.Tables.TableType;
import com.azilen.waiterpad.data.WSWaiterPin;
import com.azilen.waiterpad.data.WaiterPadResponse;
import com.azilen.waiterpad.db.DBHelper;
import com.azilen.waiterpad.managers.font.FontManager;
import com.azilen.waiterpad.managers.language.LanguageManager;
import com.azilen.waiterpad.managers.menu.MenuManager;
import com.azilen.waiterpad.managers.network.RequestType;
import com.azilen.waiterpad.managers.notification.NotificationManager;
import com.azilen.waiterpad.managers.order.OrderManager;
import com.azilen.waiterpad.utils.Global;
import com.azilen.waiterpad.utils.Prefs;
import com.azilen.waiterpad.utils.Utils;
import com.azilen.waiterpad.utils.Validator;
import com.azilen.waiterpad.utils.search.SearchForItemAdded;
import com.azilen.waiterpad.utils.search.SearchForModifier;
import com.azilen.waiterpad.utils.search.SearchForOrder;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.slidinglayer.SlidingLayer;

/**
 * Fragment Activity that displays an order for editing purposes
 * or a new order screen
 * @author dharashah
 *
 */
public class OrderRelatedActivity extends BaseActivity implements OnClickListener, OnItemSelectedListener, OnItemClickListener {
	private static boolean isSave;
	private String timeStamp;

	private RelativeLayout relativeButtons;
	private TextView txtHeader;
	private Button btnSendOrderFooter;

	private Button btnAddFooter;
	private Button btnMinusFooter;
	private ImageButton btnDeleteFooter;
	private Button btnQuantityFractionFooter;
	private Button btnAddNewGuestFooter;
	private ImageButton imageLogo;
	private ImageView imageMenuIcon;

	public static int viewId;

	private ExpandableListView guestListView;
	private static List<Guest> guests;
	private Guest guest;
	private static int guestCounter = 1;

	private String sectionId;
	private String waiterId;
	private String waiterCode;
	private String waiterName;
	private String tableId;
	private String orderId;
	private String tableNumber;
	private String guestSelected;
	private String guestSelectedToExpand;
	private String sectionName;
	private String orderNumber;
	private String billSplitChecked;
	private String mKitchenNote;
	private String mFrom;
	public static int mChildPositionSelected = -1;

	private LanguageManager mLanguageManager;

	private boolean placeOrderOnDummyTable;

	private boolean mGroupSelected;
	private boolean mChildSelected;

	private double mQuantityValue;
	private double mFractionalValue;
	private double totalPrice;
	private double mQtyPerDish;

	private int loopSize;

	private DecimalFormat mDecimalFormat; 

	private List<Order> lstRunningOrders;
	private List<ModifierMaster> lstOfModifiers;

	private Order orderToDisplay;
	private OrderRelatedAdapter orderRelatedAdapter;

	private ItemAndGuestRelatedTemp itemAndGuestRelatedTemp;
	private String TAG = this.getClass().getSimpleName();

	private static boolean msgShownForBillSplitOrder;
	private static boolean msgShownForBillSplitUpdate;
	private static boolean msgShownForBillSplitCheckout;

	// all the variables below are required for the add item slider window
	// picked from additemfragment
	private Button btnBack;
	private ListView itemListView;
	private AutoCompleteTextView mEditSearch;
	private Spinner spinnerCategoryList;
	private Spinner spinnerSubCategoryList;
	private List<ModifierMaster> modifiers;
	private boolean isMenuOrganized;
	private List<OrderedItem> orderedItemsForSliderWindow;
	private List<OrderedItem> lstOfOrderedItemsForSliderWindow;
	private List<CategoryMaster> lstCategoryWithSelect;

	// changes as on 15th June 2013
	// new variables added for submenu items
	private String mMainCategorySelected;
	private String mCurrentSelection;
	private String mCurrentSelectionString;

	private LinkedList<String> userSelectionPathIds;
	private LinkedList<String> userSelectionName;

	private TextView mTxtTableNumberHeader;
	private Button mBtnPlaceOrderLeftMenu;
	private TextView mTxtLeftMenuSettings;
	private TextView mTxtLeftMenuTables;
	private TextView mTxtLeftMenuOrders;
	private TextView mTxtLeftMenuNotifications;
	private TextView mTxtWaiterNameLeftMenu;
	private Button mBtnWaiterChangeLeftMenu;
	private TextView mTxtLeftMenuLink;
	private Button mBtnLockLeftMenu;
	private Button mBtnModifiers;

	// Picking from ItemDescActivity
	private DecimalFormat decimalFormat;
	private TextView txtQuantityLabel;
	private TextView txtFractionLabel;
	private TextView txtAllowFractions;
	private TextView txtItemPriceIndividual;
	private TextView txtItemDesc;

	private LinearLayout lnrFractionalDishes;
	private CheckBox checkFractional;

	private Button btnAddOrUpdate;
	private Button btnRemove;
	private Button btnAddQuantity;
	private Button btnRemoveQuantity;
	private Button btnAddFractionalDish ;
	private Button btnRemoveFractionalDish;
	private View footerView;

	private EditText editKitchenNote;
	private EditText editQuantity;
	private EditText editFractional;

	private RelativeLayout mRelOrderRelatedFooter;

	// to send the orders for update
	// or checkout.. 
	// needed to change the button text of the order
	// from send order to checkout order
	private List<Order> ordersToSend;
	private List<Guest> guestsToSend;
	private Order orderToSend;

	private View view;
	private SlidingMenu menu;
	private SlidingLayer slidingLayer;
	private LayoutParams rlp;

	private EditText editEnterPinToExit;
	private Dialog builderEnterPin;
	private Button btnCancel;

	private LogoutAsyncTask logoutAsyncTask;
	private RelativeLayout lnrListLayout; 
	private RelativeLayout relFooterOrderRelated;
	private LinearLayout lnrLayoutWithList;
	private static View childRowView;

	// changes as on 1st August 2013
	private ItemAdapter mItemAdapter;
	private Cursor cursorWithAllItems;
	private Cursor cursorWithAllItemsPerCategory;
	private List<CategoryMaster> categories;
	private List<ModifierMaster> modifiersSelected;
	private List<ModifierMaster> modifiersSelectedCopy;

	private Object orderDisplayEditObj;
	private List<OrderedItem> orderedItemsForSliderWindowCopy;

	// changes as on 26th Sept 2013
	// adding these new fields for the modifiers
	private double maxAmount;
	private double minAmount;
	private Cursor groupCursorModifiers;
	private HashMap<String, Integer> countOfModifiersMap;
	private boolean errorFlag = false;
	private boolean clickedModifiersButton = false;

	private Drawable clearText;
	private ImageButton mBtnForceSync;
	private GetCurrentOrderListAsyncTask mGetCurrentOrderListAsyncTask;

	private MenuManager mMenuManager;

	private LruCache<String, Object> orderCache;

	// changes as on 28th November 2013
	private TextView mTxtNotificationCount;
	private RelativeLayout mRelNotificationCentre;
	private int counter = 0;

	// receiver that will update the order
	private BroadcastReceiver updateReciver = null;
	// changes end here

	@Override
	public void onBackPressed() {
		if(orderRelatedAdapter != null) {
			OrderRelatedAdapter.isNewlyAdded = false;
		}
		saveOrder(Global.BACK);	
	}

	public boolean onKeyDown(int keyCode,KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_DOWN) {
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

	@Override
	protected void onPause() {
		super.onPause();

		if (updateReciver != null) {
			unregisterReceiver(updateReciver);
			Prefs.addKey(Global.ORDER_ID, "");
			updateReciver = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (updateReciver != null) {
			Prefs.addKey(Global.ORDER_ID, "");
			unregisterReceiver(updateReciver);
			updateReciver = null;
		}
	}


	@Override
	public void onResume() {
		super.onResume();

		// changes as on 2nd Jan 2014
		StartActivity.currentIntent = getIntent();
		// changes end here

		if(getSlidingMenu() != null) {
			if(getSlidingMenu().isMenuShowing()) {
				toggle();
			}
		}

		if(slidingLayer != null && slidingLayer.isOpened()) {
			slidingLayer.closeLayer(true);
			if(userSelectionPathIds.size() <= 0) {
				btnBack.setEnabled(false);
				btnBack.setBackgroundResource(R.drawable.up_arrow_grayed);
			}else {
				btnBack.setEnabled(true);
				btnBack.setBackgroundResource(R.drawable.up_arrow);
			}
		}

		isMenuOrganized = Prefs.getKey_boolean(OrderRelatedActivity.this, Prefs.IS_MENU_ORGANIZED);

		if(userSelectionPathIds == null || (userSelectionPathIds != null && 
				userSelectionPathIds.size() <= 0)) { 
			btnBack.setEnabled(false);
			btnBack.setBackgroundResource(R.drawable.up_arrow_grayed);
		}

		if(btnBack != null) {
			btnBack.setVisibility(View.VISIBLE);
		}

		if(mTxtNotificationCount != null) {
			mTxtNotificationCount.setText(String.valueOf(NotificationManager.getInstance().getNotificationOrderCount()));
		}

		// Changes as on 28th November 2013
		// broadcast receiver that will update the layout from the service
		if (updateReciver == null) {
			
			if(orderId != null) {
				Prefs.addKey(Global.ORDER_ID, orderId);
			}
			
			IntentFilter notification_filter = new IntentFilter();
			notification_filter.addAction(getString(R.string.broadcast_order_update));
			notification_filter.addAction(getString(R.string.broadcast_notification_update));
			notification_filter.addAction(getString(R.string.kill));

			updateReciver = new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {
					if(intent.getAction().equalsIgnoreCase(
							getString(R.string.broadcast_notification_update))) {
						if(intent.getExtras() != null) {
							counter = intent.getExtras().getInt(Global.NOTIFICATION_COUNT);
						}
					}

					if(mTxtNotificationCount != null) {
						mTxtNotificationCount.setText(String.valueOf(counter));
					}

					if(intent.getAction().equalsIgnoreCase(getString(R.string.kill))) {
						finish();
					}

					if(intent.getAction().equalsIgnoreCase(
							getString(R.string.broadcast_order_update))) {
						if(intent.getExtras() != null) {
							String orderIdFromService = intent.getExtras().getString(Global.ORDER_ID);
							Order order = null;
							// get the order from the cache, since it would have been replaced

							// checks if the order id is returned from the service or not
							// BLANK : The order is removed from IIKO
							// ORDERID : The order is still present and updated
							if(!orderIdFromService.equals("")) {
								orderId = orderIdFromService;

								lstRunningOrders = (List<Order>)orderCache.get(Global.RUNNING_ORDERS);

								if(lstRunningOrders != null && lstRunningOrders.size() > 0) {
									order = Iterables.find(lstRunningOrders, 
											new SearchForOrder(orderId),null);
								}

								order = 
										OrderManager.getInstance()
											.performOperationsOnOrderJustForOrderStatusChange(order, orderToDisplay);

								refreshGuiWithRefreshedOrder(order);						
								orderRelatedAdapter.notifyDataSetChanged();
								/*if(orderToDisplay != null && orderToDisplay.getOrderId() != null) {
									// perform the update only if the current order has the same id
									// as that of the id from the service
									if(orderIdFromService.equals(orderToDisplay.getOrderId())) {
										orderId = orderIdFromService;

										lstRunningOrders = (List<Order>)orderCache.get(Global.RUNNING_ORDERS);

										if(lstRunningOrders != null && lstRunningOrders.size() > 0) {
											order = Iterables.find(lstRunningOrders, 
													new SearchForOrder(orderId),null);
										}

										refreshGuiWithRefreshedOrder(order);						
										orderRelatedAdapter.notifyDataSetChanged();
									}
								}*/
							}
						}
					}
				}
			};
			registerReceiver(updateReciver, notification_filter);
		}
		// changes end here
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_order_related);

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

		mLanguageManager = LanguageManager.getInstance();

		mMenuManager = MenuManager.getInstance();
		orderCache = OrderManager.getInstance().getOrderCache();

		menu = new SlidingMenu(this);
		menu.setShadowWidthRes(R.dimen.shadow_res);
		menu.setFadeDegree(0.35f);

		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.layout_left_menu,null);

		setBehindContentView(view);

		// changes as on 2nd Jan 2014
		StartActivity.currentIntent = getIntent();
		// changes end here

		getSlidingMenu().setSelectorEnabled(true);
		getSlidingMenu().setSlidingEnabled(true);
		getSlidingMenu().setSelectorEnabled(true);
		getSlidingMenu().setSelected(true);
		getSlidingMenu().setBehindOffset((int)getWidth()/3);
		getSlidingMenu().setFadeEnabled(true);
		getSlidingMenu().setShadowDrawable(R.drawable.drop_shadow_for_menu);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		getSlidingMenu().setMode(SlidingMenu.LEFT);

		itemAndGuestRelatedTemp = new ItemAndGuestRelatedTemp();
		countOfModifiersMap = new HashMap<String, Integer>();	

		slidingLayer = (SlidingLayer)findViewById(R.id.slidingLayer1);
		rlp = (LayoutParams) slidingLayer.getLayoutParams();
		rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);    
		slidingLayer.setLayoutParams(rlp);
		slidingLayer.setShadowWidth(15);
		slidingLayer.setOffsetWidth(0);
		
		slidingLayer.setCloseOnTapEnabled(false);

		guestListView = (ExpandableListView)findViewById(R.id.guestListView);
		guestListView.setIndicatorBounds(getWidth() - GetPixelFromDips(75), getWidth() - GetPixelFromDips(10));
		guestListView.setGroupIndicator(null);

		mRelNotificationCentre = (RelativeLayout)findViewById(R.id.relNotificationCentre);
		mRelNotificationCentre.setClickable(true);
		mRelNotificationCentre.setOnClickListener(this);

		mTxtNotificationCount = (TextView)findViewById(R.id.txtNotificationNumber);

		setFlags();

		LinearLayout layout=(LinearLayout) findViewById(R.id.lnrLayoutWithList);

		layout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if(slidingLayer != null && slidingLayer.isOpened()) {
					InputMethodManager imm = (InputMethodManager)getSystemService(
							Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(mEditSearch.getWindowToken(), 0);
					imm.hideSoftInputFromWindow(slidingLayer.getWindowToken(), 0);

					slidingLayer.closeLayer(true);
				}

				OrderRelatedAdapter.isNewlyAdded = false;
				OrderRelatedAdapter.mGroupPositionSelected = Integer.parseInt(guestSelected);

				if(orderToDisplay != null) {
					if(orderToDisplay.getGuests() != null) {
						Guest guest = orderToDisplay.getGuests().get(OrderRelatedAdapter.mGroupPositionSelected);
						if(guest.getOrderedItems() != null) {
							OrderRelatedAdapter.mChildPositionSelected = guest.getOrderedItems().size() - 1;
						}
					}
				}
				orderRelatedAdapter.notifyDataSetChanged();

				guestListView.requestFocusFromTouch();
				guestListView.setSelected(true);
				guestListView.setSelectedGroup(OrderRelatedAdapter.mGroupPositionSelected);

				if(OrderRelatedAdapter.mChildPositionSelected != -1) {
					guestListView.setSelectedChild(OrderRelatedAdapter.mGroupPositionSelected, 
							OrderRelatedAdapter.mChildPositionSelected, 
							true);
				}

				return false;
			}
		});

		relFooterOrderRelated = (RelativeLayout)findViewById(R.id.relFooterOrderRelated);
		relFooterOrderRelated.setOnClickListener(this);

		lnrLayoutWithList = (LinearLayout)findViewById(R.id.lnrLayoutWithList);
		lnrLayoutWithList.setOnClickListener(this);

		relativeButtons = (RelativeLayout)findViewById(R.id.relButtonsFooter);
		relativeButtons.setVisibility(View.VISIBLE);

		mTxtTableNumberHeader = (TextView)findViewById(R.id.txtTableNumberHeader);
		mTxtTableNumberHeader.setVisibility(View.VISIBLE);
		txtHeader = (TextView)findViewById(R.id.txtHeader);

		imageMenuIcon = (ImageView)findViewById(R.id.menuIcon);
		imageLogo = (ImageButton)findViewById(R.id.logo);
		imageLogo.setClickable(true);
		imageLogo.setBackgroundResource(R.drawable.image_selector_order);
		imageLogo.setVisibility(View.VISIBLE);

		lnrListLayout = (RelativeLayout)findViewById(R.id.lnrListLayout);
		lnrListLayout.setOnClickListener(this);

		btnAddFooter = (Button)findViewById(R.id.btnAddFooter);
		btnMinusFooter = (Button)findViewById(R.id.btnMinusFooter);
		btnDeleteFooter = (ImageButton)findViewById(R.id.btnDeleteFooter);
		btnQuantityFractionFooter = (Button)findViewById(R.id.btnQuantityFraction);
		btnSendOrderFooter = (Button)findViewById(R.id.btnSendOrderFooter);
		mBtnForceSync = (ImageButton)findViewById(R.id.imgBtnRefresh);

		// changes as on 16th July 2013
		// for the left menu
		mTxtLeftMenuOrders = (TextView)view.findViewById(R.id.txtOrdersLeftMenu);
		mTxtLeftMenuSettings = (TextView)view.findViewById(R.id.txtSettingsLeftMenu);
		mTxtLeftMenuNotifications = (TextView)view.findViewById(R.id.txtNotificationsLeftMenu);
		mTxtLeftMenuTables = (TextView)view.findViewById(R.id.txtTablesLeftMenu);
		mTxtLeftMenuLink  = (TextView)view.findViewById(R.id.txtLeftMenuLink);
		mBtnPlaceOrderLeftMenu = (Button)view.findViewById(R.id.btnPlaceOrderLeftMenu);
		mTxtWaiterNameLeftMenu = (TextView)view.findViewById(R.id.txtWaiterName);
		mBtnLockLeftMenu = (Button)view.findViewById(R.id.btnLeftMenuLock);
		mBtnWaiterChangeLeftMenu = (Button)view.findViewById(R.id.btnLeftMenuWaiterChange);
		mBtnModifiers = (Button)findViewById(R.id.btnModifiers);

		mBtnModifiers.setVisibility(View.GONE);

		mTxtLeftMenuLink.setMovementMethod(LinkMovementMethod.getInstance());
		mTxtLeftMenuLink.setLinksClickable(true);

		mTxtLeftMenuOrders.setOnClickListener(this);
		mTxtLeftMenuSettings.setOnClickListener(this);
		mTxtLeftMenuNotifications.setOnClickListener(this);
		mTxtLeftMenuTables.setOnClickListener(this);
		mBtnPlaceOrderLeftMenu.setOnClickListener(this);
		mBtnModifiers.setOnClickListener(this);
		mBtnLockLeftMenu.setOnClickListener(this);
		mBtnWaiterChangeLeftMenu.setOnClickListener(this);
		mTxtLeftMenuLink.setOnClickListener(this);
		imageMenuIcon.setOnClickListener(this);
		imageLogo.setOnClickListener(this);
		mBtnForceSync.setOnClickListener(this);

		// For the layout that will display the items to add
		// picked from additemfragment
		mDecimalFormat = new DecimalFormat("#.##");
		btnBack = (Button)findViewById(R.id.btnBack);
		mEditSearch = (AutoCompleteTextView)findViewById(R.id.autoSearchItem);
		spinnerCategoryList = (Spinner)findViewById(R.id.spinnerCategoryList);
		spinnerSubCategoryList = (Spinner)findViewById(R.id.spinnerSubCategoryList);
		itemListView = (ListView)findViewById(android.R.id.list);
		mRelOrderRelatedFooter = (RelativeLayout)findViewById(R.id.relFooterOrderRelated);

		userSelectionName = new LinkedList<String>();
		userSelectionPathIds = new LinkedList<String>();
		itemListView.setOnItemClickListener(this);
		itemListView.setEmptyView(findViewById(android.R.id.empty));

		// a text watcher 
		mEditSearch.addTextChangedListener(new MyTextWatcher(mEditSearch));

		// changes as on 7th November 2013
		// adding the ability to clear edit text
		clearText = getResources().getDrawable(R.drawable.clear_text);
		clearText.setBounds(0, 0, clearText.getIntrinsicWidth(), clearText.getIntrinsicHeight());
		mEditSearch.setCompoundDrawables(null, null, mEditSearch.getText().toString().equals("") ? null : clearText, null);

		mEditSearch.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mEditSearch.getCompoundDrawables()[2] == null) {
					return false;
				}
				if (event.getAction() != MotionEvent.ACTION_UP) {
					return false;
				}
				if (event.getX() > mEditSearch.getWidth() - mEditSearch.getPaddingRight() - clearText.getIntrinsicWidth()) {
					mEditSearch.setText("");
					mEditSearch.setCompoundDrawables(null, null, null, null);


					// filter the request
					if(mItemAdapter != null) {
						mItemAdapter.getFilter().filter("");
					}
				}
				return false;
			}
		});

		// changes end here

		spinnerCategoryList.setOnItemSelectedListener(this);
		spinnerSubCategoryList.setOnItemSelectedListener(this);

		isMenuOrganized = Prefs.getKey_boolean(OrderRelatedActivity.this, Prefs.IS_MENU_ORGANIZED);
		waiterId =Prefs.getKey(Prefs.WAITER_ID);
		sectionId =Prefs.getKey( Prefs.SECTION_ID);
		waiterCode =Prefs.getKey( Prefs.WAITER_CODE);
		sectionName = Prefs.getKey( Prefs.SECTION_NAME);
		waiterName =Prefs.getKey( Prefs.WAITER_NAME);
		billSplitChecked = Prefs.getKey( Prefs.BILL_SPLIT_CHECKED);

		//TODO: section id added manually to be removed
		// Dhara 30/10/2013
		// sectionId = "1959a1ec-ce3f-44de-8ef5-a9ba4aa54385";

		mTxtWaiterNameLeftMenu.setText(waiterName);

		setButtonWidth(5);
		setButtonWidthForLeftMenu();

		// Get the values passed from the previous activity
		if(getIntent().getExtras() != null) {
			if(getIntent().getStringExtra(Global.TABLE_ID) != null) {
				tableId = getIntent().getStringExtra(Global.TABLE_ID);
			}

			if(getIntent().getStringExtra(Global.TABLE_NUMBER) != null) {
				tableNumber = getIntent().getStringExtra(Global.TABLE_NUMBER);
			}

			if(getIntent().getStringExtra(Global.ORDER_ID) != null) {
				orderId = getIntent().getStringExtra(Global.ORDER_ID);
			}

			if(getIntent().getStringExtra(Global.TIME_STAMP) != null) {
				timeStamp = getIntent().getStringExtra(Global.TIME_STAMP);
			}

			if(getIntent().getStringExtra(Global.ORDER_NUMBER) != null) {
				orderNumber = getIntent().getStringExtra(Global.ORDER_NUMBER);
			}

			if(getIntent().getStringExtra(Global.GUEST_NUMBER) != null) {
				guestSelectedToExpand = getIntent().getStringExtra(Global.GUEST_NUMBER);
			}

			if(getIntent().getStringExtra(Global.FROM_ACTIVITY) != null) {
				mFrom = getIntent().getStringExtra(Global.FROM_ACTIVITY);
			}
		}

		mDecimalFormat = new DecimalFormat("#.##");

		mTxtTableNumberHeader.setText(tableNumber);

		// Get all the orders related to the table
		lstRunningOrders = (List<Order>)orderCache.get(Global.RUNNING_ORDERS);

		Prefs.addKey(Prefs.ORDER_ID, "");

		if(orderId != null && orderId.trim().length() > 0) {
			getOrderFromRunningOrders();
		}

		if(timeStamp != null && 
				timeStamp.trim().length() > 0 && 
				mFrom.equalsIgnoreCase(Global.FROM_TABLE_ORDER_LIST)) {
			// get the order from cache
			HashMap<String, Order> orderMapCache = (HashMap<String, Order>)orderCache.get(Global.CURRENT_ORDER);
			if(orderMapCache != null) {
				orderToDisplay = orderMapCache.get(timeStamp);
			}
		}else if((orderId == null && timeStamp == null) || 
				(orderId != null && orderId.trim().length() <= 0 && 
				timeStamp != null && 
				timeStamp.trim().length() <= 0)){
			// it is not a running order and 
			// so does not have an order id
			// or a timestamp either
			// NEW ORDER
			guestCounter = 1;
			orderToDisplay = new Order();
			guests = new ArrayList<Guest>();
			setOrderTable();
			setNewGuest();
			orderToDisplay.setWaiterId(waiterId);
		}

		orderDisplayEditObj = Utils.copy((Object)orderToDisplay);

		// set the adapter to show this current order in the list
		orderRelatedAdapter = new OrderRelatedAdapter(OrderRelatedActivity.this, 
				R.layout.individual_guest_name, 
				orderToDisplay,
				guestSelected,
				mChildPositionSelected,
				this);

		OrderRelatedAdapter.mGroupPositionSelected = 0;
		orderRelatedAdapter.notifyDataSetChanged();

		btnSendOrderFooter.setOnClickListener(this);

		btnBack.setOnClickListener(this);
		imageLogo.setOnClickListener(this);
		btnAddFooter.setOnClickListener(this);
		btnDeleteFooter.setOnClickListener(this);
		btnMinusFooter.setOnClickListener(this);
		btnQuantityFractionFooter.setOnClickListener(this);

		guestListView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				Log.i(TAG, "group click !!!!");
				OrderRelatedAdapter.isNewlyAdded = false;
				guestListView.expandGroup(groupPosition);

				guestListView.setSelected(true);
				guestListView.setSelectedGroup(groupPosition);
				return true;
			}
		});

		decimalFormat = new DecimalFormat("#.##");

		guestListView.setOnGroupExpandListener(new OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int groupPosition) {

			}
		});

		guestListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
			@Override
			public void onGroupCollapse(int groupPosition) {

			}
		});

		guestListView.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {


				if(orderRelatedAdapter != null) {
					OrderRelatedAdapter.isNewlyAdded = false;
				}

				if(slidingLayer.isOpened()) {
					InputMethodManager imm = (InputMethodManager)getSystemService(
							Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(mEditSearch.getWindowToken(), 0);
					imm.hideSoftInputFromWindow(slidingLayer.getWindowToken(), 0);

					slidingLayer.closeLayer(true);

				}

				mGroupSelected = false;
				Log.i(TAG,"child clicked : " );
				List<Guest> guests = orderToDisplay.getGuests();
				Guest guest = guests.get(groupPosition);
				List<OrderedItem> items = guest.getOrderedItems();
				OrderedItem orderedItem = items.get(childPosition);

				guestListView.requestFocusFromTouch();
				guestListView.setSelected(true);
				guestListView.setSelectedGroup(groupPosition);
				guestListView.setSelectedChild(groupPosition, 
						childPosition, 
						true);

				if(orderToDisplay.getOrderStatus() == null || 
						(orderToDisplay.getOrderStatus() != null && 
						orderToDisplay.getOrderStatus().ordinal() != 2)) {

					// Get the item with the order id;

					ItemMaster item = MenuManager.getInstance().getItem(orderedItem.getId());

					if(item != null) {
						mChildSelected = true;

						if(orderedItem.isEditable() || 
								(orderedItem.getOrderedItemStatus() == 0 && 
								orderedItem.isEditable() == false)) {
							btnAddFooter.setEnabled(true);

							btnAddFooter.setTextColor(getResources().getColor(R.color.order_footer_textcolor));
							btnQuantityFractionFooter.setEnabled(true);
							btnQuantityFractionFooter.setTextColor(getResources().getColor(R.color.order_footer_textcolor));

							if(orderedItem.getQuantity() > 1) {
								btnMinusFooter.setEnabled(true);
								btnMinusFooter.setTextColor(getResources().getColor(R.color.order_footer_textcolor));
							}else {
								btnMinusFooter.setEnabled(false);
								btnMinusFooter.setTextColor(getResources().getColor(android.R.color.darker_gray));
							}
						}else {
							btnMinusFooter.setEnabled(false);
							btnQuantityFractionFooter.setEnabled(false);
							btnAddFooter.setEnabled(true);

							btnAddFooter.setTextColor(getResources().getColor(R.color.order_footer_textcolor));
							btnMinusFooter.setTextColor(getResources().getColor(android.R.color.darker_gray));
							btnQuantityFractionFooter.setTextColor(getResources().getColor(android.R.color.darker_gray));
						}

						// store the values selected
						itemAndGuestRelatedTemp = new ItemAndGuestRelatedTemp();
						itemAndGuestRelatedTemp.setGroupPosition(groupPosition);
						itemAndGuestRelatedTemp.setChildPosition(childPosition);
						itemAndGuestRelatedTemp.setGuests(guests);
						itemAndGuestRelatedTemp.setGuest(guest);
						itemAndGuestRelatedTemp.setOrderedItems(items);
						itemAndGuestRelatedTemp.setOrderedItem(orderedItem);
						itemAndGuestRelatedTemp.setItem(item);

						modifiersSelectedCopy = new ArrayList<ModifierMaster>();

						if(orderedItem.getModifiers() != null) {
							modifiersSelectedCopy.addAll(orderedItem.getModifiers());
						}

						//if(item.isHasModifiers()) {
						int countOfNormalModifiers =
								MenuManager.getInstance().getCountOfItemsModifiers(sectionId, item.getItemId());
						int countOfGroupModifiers = 
								MenuManager.getInstance().getCountOfGroupModifiers(item.getItemId());

						Log.i("dhara", "total count of modifiers: ");
						Log.i("dhara"," countOfNormalModifiers : " + countOfNormalModifiers);
						Log.i("dhara"," countOfGroupModifiers : "  + countOfGroupModifiers);

						// changes as on 24th September 2013
						// compare if the item has normal modifiers 
						// or group modifiers (a total of them)
						if(orderedItem.isEditable() || 
								(orderedItem.getOrderedItemStatus() == 0 && 
								orderedItem.isEditable() == false)) {
							if(countOfGroupModifiers + countOfNormalModifiers > 0) {
								// make the modifiers buttons visible
								// else invisible                         
								Log.i(TAG,"has modifiers");
								mBtnModifiers.setVisibility(View.VISIBLE);
							}else {
								mBtnModifiers.setVisibility(View.INVISIBLE);
							}
						}else {
							mBtnModifiers.setVisibility(View.INVISIBLE);
						}

						guestSelected = String.valueOf(groupPosition);
						mChildPositionSelected = childPosition;

						Log.i("tag","mChildPositionSelected : " +mChildPositionSelected );

						if(orderRelatedAdapter != null) {
							OrderRelatedAdapter.mChildPositionSelected = childPosition;
							OrderRelatedAdapter.mGroupPositionSelected = groupPosition;
						}

						orderRelatedAdapter.notifyDataSetChanged();
					}else {
						setButtonsWhenNoItemSelected();
					}

				}
				//TODO: Need to make changes here
				
				if(orderToDisplay.getOrderStatus() == null || 
						(orderToDisplay.getOrderStatus() != null && 
						orderToDisplay.getOrderStatus().ordinal() != 2)) {
					v.setBackgroundResource(R.drawable.child_item_selector);
				}
				
				v.setSelected(true);
				childRowView = v;

				return false;
			}
		});

		setGuiLabels();

		if(orderToDisplay != null) {
			if(orderToDisplay.getOrderStatus() == null || (orderToDisplay.getOrderStatus() != null && 
					orderToDisplay.getOrderStatus().ordinal() != 2) 
					) {
				footerView = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listview_footer, 
						null, 
						false);

				btnAddNewGuestFooter = (Button)footerView.findViewById(R.id.btnAddGuestFooter);
				btnAddNewGuestFooter.setText(mLanguageManager.getAddNewGuest());
				btnAddNewGuestFooter.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
				btnAddNewGuestFooter.setOnClickListener(this);

				guestListView.addFooterView(footerView);
			}

			guestListView.setAdapter(orderRelatedAdapter);
			guestListView.invalidateViews();

			// expand that group that has been edited last
			// for the user to see it
			if(guestSelectedToExpand != null) {
				guestListView.expandGroup(Integer.parseInt(guestSelectedToExpand));
			}	

			if(orderToDisplay != null) {
				if(orderToDisplay.getOrderStatus() != null && orderToDisplay.getOrderStatus().ordinal() == 2) {
					imageLogo.setEnabled(false);
					imageLogo.setBackgroundResource(R.drawable.logo_header);
				}

				expandList();
			}

			inflateListAndAlsoSpinner();
		}


		// changes as on 11th November 2013
		// check if the order created has an order number or not
		// if no order number (or order number greater than 0), show the refresh button
		// else hide the refresh button
		if(orderToDisplay != null && 
				orderToDisplay.getOrderNumber() > 0 && 
				orderToDisplay.getOrderStatus() != null &&
				orderToDisplay.getOrderStatus().ordinal() != 2) {
			mBtnForceSync.setVisibility(View.VISIBLE);
		}else {
			mBtnForceSync.setVisibility(View.GONE);
		}
	}

	public void setButtonWidthForLeftMenu() {
		final int offset = getWidth()/3;
		Log.i("Dhara", "offset " + offset);

		mBtnLockLeftMenu.post(new Runnable() {

			@Override
			public void run() {
				mBtnLockLeftMenu.setWidth(offset);
				Log.i("Dhara", "mBtnLockLeftMenu " + mBtnLockLeftMenu.getWidth());
			}
		});

		mBtnWaiterChangeLeftMenu.post(new Runnable() {

			@Override
			public void run() {
				mBtnWaiterChangeLeftMenu.setWidth(offset);
				Log.i("Dhara", "mBtnChangeWaiterLeftMenu " + mBtnWaiterChangeLeftMenu.getWidth());
			}
		});	

	}

	public void refreshGUI(List<CategoryMaster> result) {
		categories = result;
		inflateListAndAlsoSpinner();

		OrderRelatedAdapter.mChildPositionSelected = -1;
		orderRelatedAdapter.notifyDataSetChanged();
		slidingLayer.openLayer(true);
	}

	private void setSelectedView() {
		String selected = Prefs.getKey(Prefs.MENU_SELECTED);

		if(selected.trim().length() > 0) {
			if(selected.equalsIgnoreCase(Global.NOTIFICATIONS)) {
				mTxtLeftMenuNotifications.setPressed(true);
			}else if(selected.equalsIgnoreCase(Global.TABLES)) {
				mTxtLeftMenuTables.setPressed(true);
			}else if(selected.equalsIgnoreCase(Global.ORDERS)) {
				mTxtLeftMenuOrders.setPressed(true);
			}else if(selected.equalsIgnoreCase(Global.SETTINGS)) {
				mTxtLeftMenuSettings.setPressed(true);
			}
		}
	}

	/**
	 * Sets the button width depending on 
	 * the number of buttons available
	 * @param divideBy
	 */
	private void setButtonWidth(int divideBy) {
		btnAddFooter.setWidth(getWidth()/divideBy);
		btnMinusFooter.setWidth(getWidth()/divideBy);
		btnQuantityFractionFooter.setWidth(getWidth()/divideBy);
		btnDeleteFooter.setMinimumWidth(getWidth()/divideBy);
		btnSendOrderFooter.setWidth(getWidth()/divideBy);
	}


	@Override
	public void finish() {
		super.finish();
	}

	/**
	 * Gets an order from the set of running orders
	 */
	private void getOrderFromRunningOrders() {
		if(lstRunningOrders != null && lstRunningOrders.size() > 0) {
			orderToDisplay = Iterables.find(lstRunningOrders, new SearchForOrder(orderId),null);
			if(orderToDisplay != null) {


				// changes as on 28th November 2013
				// changes as per the client's requirement
				// need to auto-update the order screen
				// storing the value of orderId into shared preferences
				// so as to fetch the value from @{GetAllOrdersService}
				Prefs.addKey(Prefs.ORDER_ID, orderId);
				// changes end here

				List<Guest> guestsObj = orderToDisplay.getGuests();
				if(guestsObj != null) {
					guestCounter = guestsObj.size();
					guests= orderToDisplay.getGuests();

					String strItemPrice = String.valueOf(orderToDisplay.getTotalAmount());
					String currencySymbol = LanguageManager.getInstance()
							.getCurrancySymbol();

					String currencyLocation = LanguageManager.getInstance()
							.IS_AFTER();

					if (!currencyLocation.equalsIgnoreCase("TRUE")) {
						strItemPrice = currencySymbol + " " + strItemPrice;
					} else {
						strItemPrice = strItemPrice + " " + currencySymbol;
					}

					txtHeader.setText(mLanguageManager.getTotal() + ": " + strItemPrice);
				}else {
					// if the guest object returned by iiko is null
					guestCounter = 1;
					guests = new ArrayList<Guest>();
					setOrderTable();
					setNewGuest();
					orderToDisplay.setWaiterId(waiterId);
				}

				orderToDisplay =(Order) Utils.copy(orderToDisplay);

				orderRelatedAdapter = new OrderRelatedAdapter(OrderRelatedActivity.this, 
						R.layout.individual_guest_name, 
						orderToDisplay,
						guestSelected,
						mChildPositionSelected,
						this);
				guestListView.setAdapter(orderRelatedAdapter);
				guestListView.invalidateViews();
			}
		}
	}

	/**
	 * Returns the pixel values
	 * @param pixels
	 * @return
	 */
	public int GetPixelFromDips(float pixels) {
		// Get the screen's density scale 
		final float scale = getResources().getDisplayMetrics().density;
		// Convert the dps to pixels, based on density scale
		return (int) (pixels * scale + 0.5f);
	}

	/**
	 * Calculates the total amount for display
	 * @param order
	 */
	private void calculateTotalAmount(Order order) {
		if(order != null) {
			List<Guest> guests = order.getGuests();
			totalPrice = 0.0;
			Log.i(TAG,"calculateTotalAmount called ") ;
			for(Guest guest : guests) {
				List<OrderedItem> lst = guest.getOrderedItems();				
				if(lst != null) {
					for(OrderedItem orderedItem : lst) {
						double modifiersCost = mMenuManager.getModifiersCost(orderedItem.getModifiers());

						Log.i(TAG, "value of modifiers cost : " + modifiersCost);

						double price = (modifiersCost + orderedItem.getPrice())  * orderedItem.getQuantity();
						totalPrice += price;
					}
				}
			}

			String totalText = "";

			// need to add ways of changing the format of the price
			totalText = mLanguageManager.getTotal();

			DecimalFormat dcf = new DecimalFormat("#.##");
			String strTotalPrice = dcf.format(totalPrice);

			txtHeader.setTextColor(getResources().getColor(R.color.white));
			Log.i(TAG,"total is : " + strTotalPrice);

			String currencySymbol = LanguageManager.getInstance()
					.getCurrancySymbol();

			String currencyLocation = LanguageManager.getInstance()
					.IS_AFTER();

			if (!currencyLocation.equalsIgnoreCase("TRUE")) {
				strTotalPrice = currencySymbol + " " + strTotalPrice;
			} else {
				strTotalPrice = strTotalPrice + " " + currencySymbol;
			}

			txtHeader.setText(mLanguageManager.getTotal() + ": " + strTotalPrice);

			orderToDisplay.setTotalAmount(totalPrice);
		}
	}

	/**
	 * Sets the table for the order taken
	 */
	private void setOrderTable() {
		if(placeOrderOnDummyTable) {
			orderToDisplay.setTable(null);
		}else {
			Tables table = new Tables();
			table.setSectionId(sectionId);
			table.setTableId(tableId);

			if(tableNumber != null) {
				table.setTableNumber(Integer.parseInt(tableNumber));
			}else {
				table.setTableNumber(Integer.parseInt("1"));
			}
			table.setTableTypeOrdinal(TableType.TABLE.ordinal());
			table.setSectionName(sectionName);
			orderToDisplay.setTable(table);
		}
	}

	/**
	 * Sets a new guest for the order
	 */
	private void setNewGuest(){
		guest = new Guest();

		guest.setGuestName(mLanguageManager.getGuest() + 
				" " + 
				guestCounter);

		if(orderToDisplay.getGuests() != null) {
			guests = orderToDisplay.getGuests();
		}

		guests.add(guest);
		orderToDisplay.setGuests(guests);

		if(orderDisplayEditObj != null) {
			Order orderDummy = (Order)orderDisplayEditObj;

			if(orderDummy.getGuests() == null) {
				orderDummy.setGuests(new ArrayList<Guest>());
			}
			orderDummy.getGuests().add(guest);
			orderDisplayEditObj = (Object)orderDummy;
		}
	}


	/** 
	 * Refreshes the orderitems and also other variables needed
	 * from the add item fragment or any other fragment that needs it
	 */
	@SuppressWarnings("unchecked")
	public void getUpdatedOrderFromFragment(String timestamp,String guestGroupSelected) {
		Log.i(TAG, "getUpdatedOrderFromFragment called!!! ");

		HashMap<String, Order> currentOrder = 
				(HashMap<String, Order>)orderCache.get(Global.CURRENT_ORDER);

		orderToDisplay = currentOrder.get(timestamp);

		orderRelatedAdapter = new OrderRelatedAdapter(OrderRelatedActivity.this, 
				R.layout.individual_guest_name, 
				orderToDisplay,
				guestSelected,
				mChildPositionSelected,
				this);

		guestListView.setAdapter(orderRelatedAdapter);

		guestCounter = orderToDisplay.getGuests().size();

		calculateTotalAmount(orderToDisplay);

		guestListView.expandGroup(Integer.parseInt(guestGroupSelected));
	}

	public void showMessage(String message, boolean isCheckout) {
		if(message != null && message.trim().length() > 0 ){

			AlertDialog.Builder builder = 
					new AlertDialog.Builder(OrderRelatedActivity.this);
			builder.setCancelable(false);

			String appName = mLanguageManager.getAppName();
			String ok = mLanguageManager.getOk();

			builder.setTitle(appName);
			builder.setMessage(message);
			builder.setPositiveButton(ok,new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();

					Intent intent = new Intent(OrderRelatedActivity.this, MyOrderActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | 
							Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(intent);
					//activity out animation
					Global.activityStartAnimationRightToLeft(OrderRelatedActivity.this);
				}
			});

			builder.show();
		}
	}

	public void showMessage(String message) {
		if(message != null && message.trim().length() > 0 ){
			String appName = mLanguageManager.getAppName();
			String ok = mLanguageManager.getOk();

			CommonDialog dialog =
					new CommonDialog(OrderRelatedActivity.this, appName, ok, message);
			dialog.show();
		}
	}

	/**
	 * Changes as on 11th November 2013
	 * refresh the current order with the new order(or refreshed order)
	 * @param result
	 */
	public void refreshOrders(OrderList result) {
		if(result != null && result.getOrders() !=null) {
			// seach for orders having the same order id
			Order orderRefreshed = Iterables.find(result.getOrders(),
					new SearchForOrder(orderToDisplay.getOrderId()), null);
			// changes as on 28th November 2013
			// clubbed the operations into one method
			// so that they can be re-used
			refreshGuiWithRefreshedOrder(orderRefreshed);
			// changes end here
		}else {
			// the order is not present in the order list
			// this means that it has been cleared
			// redirect to the my orders screen then
			Intent intent = new Intent(OrderRelatedActivity.this, MyOrderActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | 
					Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			//activity out animation
			Global.activityStartAnimationRightToLeft(OrderRelatedActivity.this);
		}
	}

	// changes as on 28th november 2013
	// adding this method in order to refresh the gui 
	// based on the refreshed order
	// either received or got from the cache

	/**
	 * Added as on 28th November 2013
	 * Takes up an order that has been refreshed
	 * By pressing the force sync button or auto updated
	 * The order can be null also, meaning it has been cleared from IIKO
	 * @param orderRefreshed
	 */ 
	private void refreshGuiWithRefreshedOrder(Order orderRefreshed) {
		if(orderRefreshed != null) {
			orderToDisplay =(Order) Utils.copy(orderRefreshed);
			setButtons();

			if(orderRefreshed.getTable() != null) {
				mTxtTableNumberHeader.setText(String.valueOf(orderToDisplay.getTable().getTableNumber())); 
			}

			List<Guest> guestsObj = orderRefreshed.getGuests();
			if(guestsObj != null) {
				guestCounter = guestsObj.size();
				guests= orderToDisplay.getGuests();
			}else {
				// if the guest object returned by iiko is null
				guestCounter = 1;
				guests = new ArrayList<Guest>();
				setOrderTable();
				setNewGuest();
				orderToDisplay.setWaiterId(waiterId);
			}

			orderToDisplay =(Order) Utils.copy(orderToDisplay);
			orderDisplayEditObj = Utils.copy(orderToDisplay);			
			orderToDisplay.setModified(false);

			orderRelatedAdapter = new OrderRelatedAdapter(OrderRelatedActivity.this, 
					R.layout.individual_guest_name, 
					orderToDisplay,
					guestSelected,
					mChildPositionSelected,
					this);

			guestListView.setAdapter(orderRelatedAdapter);

			orderRelatedAdapter.notifyDataSetChanged();
			guestSelected = String.valueOf(guestCounter - 1);
			expandList();
			guestListView.requestFocusFromTouch();
			guestListView.setSelected(true);
			guestListView.setSelectedGroup(Integer.parseInt(guestSelected));
			guestListView.setSelection(Integer.parseInt(guestSelected));

			calculateTotalAmount(orderToDisplay);

			setButtons();

			if(orderToDisplay != null) {
				if(orderToDisplay.getOrderStatus() != null && 
						orderToDisplay.getOrderStatus().ordinal() == 2) {
					footerView.setVisibility(View.GONE);
				}
			}
		}else {
			// the order is not present in the order list
			// this means that it has been cleared
			// redirect to the my orders screen then
			Intent intent = new Intent(OrderRelatedActivity.this, MyOrderActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | 
					Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			//activity out animation
			overridePendingTransition(R.anim.activity_in_anim,R.anim.activity_out_anim);
		}
	}

	// changes end here

	/**
	 * Any changes to the order would notify the data set change
	 * @param order
	 * @param from
	 * @param action
	 */
	public void refreshList(Order order, String from, String action) {
		if(from.equalsIgnoreCase(Global.ORDER_FROM_ASYNC_SEND)) {
			orderToDisplay = (Order)Utils.copy(order);
			orderDisplayEditObj = Utils.copy(orderToDisplay);

			orderToDisplay.setModified(false);

			btnSendOrderFooter.setText(mLanguageManager.getCheckout());

			if(from.equalsIgnoreCase(Global.ORDER_FROM_ASYNC_SEND)) {
				setButtonsWhenNoItemSelected();
				if(action.equalsIgnoreCase(Global.ORDER_ACTION_NEW_ORDER))  {
					if(order.isBillSplit()) {
						if(!msgShownForBillSplitOrder) {
							showMessage(mLanguageManager.getOrderAdded());
							msgShownForBillSplitOrder = true;
						}
					}else {
						showMessage(mLanguageManager.getOrderAdded());
					}
				}else if(action.equalsIgnoreCase(Global.ORDER_ACTION_UPDATED)) {
					if(order.isBillSplit()) {
						// checks if the order updated message 
						// has been displayed or not
						if(!msgShownForBillSplitUpdate) {
							showMessage(mLanguageManager.getOrderUpdated());
							msgShownForBillSplitUpdate = true;
						}
					}else {
						showMessage(mLanguageManager.getOrderUpdated());
					}
				}else if(action.equalsIgnoreCase(Global.ORDER_ACTION_CHECKOUT)) {
					if(order.isBillSplit()) {
						if(!msgShownForBillSplitCheckout) {
							showMessage(mLanguageManager.getCheckoutSuccess(), true);
							msgShownForBillSplitCheckout = true;	
						}
					}else {
						showMessage(mLanguageManager.getCheckoutSuccess(), true);
					}
				}
			}

			if(order.getTable() != null) {
				mTxtTableNumberHeader.setText(String.valueOf(orderToDisplay.getTable().getTableNumber())); 
			}
			
			Global.isSendOrderCalled = false;

			// changes end here
		}

		if(from.equalsIgnoreCase(Global.ORDER_FROM_ADAPTER)) {
			orderToDisplay = order;
			orderToDisplay.setModified(true);

			btnSendOrderFooter.setText(mLanguageManager.getSendOrder());

			String strGuest = "";

			strGuest = mLanguageManager.getGuest();

			if(action.equalsIgnoreCase(Global.ORDER_ACTION_DELETE)) {
				List<Guest> guests = orderToDisplay.getGuests();
				if(guests != null) {
					for(int i=0 ;i<guests.size();i++) {
						Guest guest = guests.get(i);
						guest.setGuestName(strGuest + " " + String.valueOf((i + 1)));

						Log.i("dhara","action delete" + guest.getOrderedItems());
					}
				}

				orderToDisplay.setGuests(guests);
			}
		}

		if(from.equalsIgnoreCase(Global.ORDER_FROM_ASYNC_SEND)) {
			if(!action.equalsIgnoreCase(Global.ORDER_ACTION_CHECKOUT) && 
					!action.equalsIgnoreCase(Global.ORDER_ACTION_UPDATED)) {
				orderToDisplay = order;

				// set editable false for the new order
				if(order != null) {
					if(order.getGuests() != null) {
						List<Guest> guests = order.getGuests();

						if(guests != null && guests.size() > 0) {
							for(Guest guest : guests) {
								if(guest.getOrderedItems() != null) {
									List<OrderedItem> orderedItems = guest.getOrderedItems();
									if(orderedItems != null) {
										for(OrderedItem orderedItem : orderedItems) {
											orderedItem.setEditable(false);
											//orderedItem.setOrderedItemStatus(1);
										}
									}
								}
							}
						}
					}
				}

			}
		}

		if(action.equalsIgnoreCase(Global.ORDER_ACTION_EDIT_ITEM)) {
			if(orderRelatedAdapter != null) {
				OrderRelatedAdapter.mChildPositionSelected = itemAndGuestRelatedTemp.getChildPosition();
				OrderRelatedAdapter.mGroupPositionSelected = itemAndGuestRelatedTemp.getGroupPosition();
			}

			guestListView.setSelected(true);
			guestListView.setSelectedChild(itemAndGuestRelatedTemp.getGroupPosition(),
					itemAndGuestRelatedTemp.getChildPosition(), 
					true);

			orderRelatedAdapter.notifyDataSetChanged();
		}else {
			orderRelatedAdapter = new OrderRelatedAdapter(OrderRelatedActivity.this, 
					R.layout.individual_guest_name, 
					orderToDisplay,
					guestSelected,
					mChildPositionSelected,
					this);

			guestListView.setAdapter(orderRelatedAdapter);
			guestCounter = orderToDisplay.getGuests().size();

			orderRelatedAdapter.notifyDataSetChanged();

			if(action.equalsIgnoreCase(Global.ORDER_ACTION_ADDED_ITEM)) {
				expandList();
				guestListView.requestFocusFromTouch();
				guestListView.setSelected(true);
				guestListView.setSelectedGroup(Integer.parseInt(guestSelected));
				guestListView.setSelection(Integer.parseInt(guestSelected));
				guestListView.setSelectedChild(Integer.parseInt(guestSelected),
						OrderRelatedAdapter.mChildPositionSelected , 
						true);

				if(itemAndGuestRelatedTemp != null) {
					itemAndGuestRelatedTemp.setChildPosition(OrderRelatedAdapter.mChildPositionSelected);
				}
			}else{
				guestSelected = String.valueOf(guestCounter - 1);
				expandList();
				guestListView.requestFocusFromTouch();
				guestListView.setSelected(true);
				guestListView.setSelectedGroup(Integer.parseInt(guestSelected));
				guestListView.setSelection(Integer.parseInt(guestSelected));
			}
		}

		// changes as on 4th December 2013
		// checks if the order is not null
		if(orderToDisplay != null) {
			// checks if the order has an order number
			if(orderToDisplay.getOrderNumber() > 0 ) {
				// checks if it has an orderstatus
				// if yes, check its ordinal value
				// if != 2, then display the sync button
				// and the imageLogo button (add item button)
				if(orderToDisplay.getOrderStatus() != null) {
					if(orderToDisplay.getOrderStatus().ordinal() != 2) {
						mBtnForceSync.setVisibility(View.VISIBLE);
						imageLogo.setVisibility(View.VISIBLE);
					}else {
						mBtnForceSync.setVisibility(View.GONE);
						imageLogo.setVisibility(View.GONE);
					}
				}else {
					// it does not have a status so in that case display both the 
					// force sync button and also the add item button
					// display the force sync button since its an order 
					// generated by IIKO
					mBtnForceSync.setVisibility(View.VISIBLE);
					imageLogo.setVisibility(View.VISIBLE);
				}
			}else {
				// it does not have an order number so, 
				// it is a waiterpad generated order
				mBtnForceSync.setVisibility(View.GONE);
				imageLogo.setVisibility(View.VISIBLE);
			}
		}
		// changes end here

		/*if(orderToDisplay != null && 
				orderToDisplay.getOrderNumber() > 0 &&
				((orderToDisplay.getOrderStatus() != null && 
				orderToDisplay.getOrderStatus().ordinal() != 2) || 
				orderToDisplay.getOrderStatus() == null)) {
			mBtnForceSync.setVisibility(View.VISIBLE);
			imageLogo.setVisibility(View.VISIBLE);
		}else if(orderToDisplay != null && 
				orderToDisplay.getOrderNumber() > 0 &&
				(orderToDisplay.getOrderStatus() != null && 
				orderToDisplay.getOrderStatus().ordinal() == 2)){
			mBtnForceSync.setVisibility(View.GONE);
			imageLogo.setVisibility(View.GONE);
		}*/

		calculateTotalAmount(orderToDisplay);
		
		if(from.equalsIgnoreCase(Global.ORDER_FROM_ASYNC_SEND)) {
			if(orderCache.get(getString(R.string.order_from_waiterpad)) != null) {
				orderCache.remove(getString(R.string.order_from_waiterpad));
				Log.i("UpdateOrderAsyncTask","removed from cache id");
			}
		}
	}

	private void setFlags() {
		msgShownForBillSplitCheckout=false;
		msgShownForBillSplitOrder = false;
		msgShownForBillSplitUpdate = false;
	}

	/**
	 * Creates a new ordered item with values set
	 * @param item
	 * @param qty
	 * @return
	 */
	private OrderedItem createNewOrderedItem(ItemMaster item, double qty ) {
		Log.i(TAG, "add new order function called");
		OrderedItem orderedItem = new OrderedItem();
		orderedItem.setId(item.getItemId());

		if(mKitchenNote != null && 
				mKitchenNote.trim().length() > 0) {
			orderedItem.setComment(mKitchenNote);
		}
		orderedItem.setName(item.getItemName());
		orderedItem.setPrice(item.getPrice());
		orderedItem.setEditable(true);
		orderedItem.setPriceWithModifiers(item.getPrice() + mMenuManager.getModifiersCost(lstOfModifiers));
		orderedItem.setOrderedItemStatus(OrderedItemStatus.ADDED.ordinal());

		DecimalFormat dcf = new DecimalFormat("#.##");
		String strQty = dcf.format(qty);

		//TODO: Prevent addition of 0 qty
		if((int)qty <= 1) {
			orderedItem.setQuantity(1);
		}else {
			orderedItem.setQuantity((int)qty);
		}
		return orderedItem;
	}


	@Override
	public void onClick(View v) {
		String strQtyVal= "";
		String strFractionalVal = "";
		switch (v.getId()) {

		// changes as on 30th July 2013
		case R.id.txtLeftMenuLink:
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + getString(R.string.site)));
			startActivity(browserIntent);
			Global.activityStartAnimationRightToLeft(OrderRelatedActivity.this);
			break;

			// changes as on 26th july 2013
		case R.id.lnrListLayout:
			if(slidingLayer != null && slidingLayer.isOpened()) {
				InputMethodManager imm = (InputMethodManager)getSystemService(
						Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mEditSearch.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(slidingLayer.getWindowToken(), 0);

				slidingLayer.closeLayer(true);
			}
			OrderRelatedAdapter.isNewlyAdded = false;
			orderRelatedAdapter.notifyDataSetChanged();
			break;

		case R.id.lnrLayoutWithList:
			if(slidingLayer != null && slidingLayer.isOpened()) {
				InputMethodManager imm = (InputMethodManager)getSystemService(
						Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mEditSearch.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(slidingLayer.getWindowToken(), 0);

				slidingLayer.closeLayer(true);
			}
			OrderRelatedAdapter.isNewlyAdded = false;
			orderRelatedAdapter.notifyDataSetChanged();
			break;

		case R.id.relFooterOrderRelated:
			if(slidingLayer != null && slidingLayer.isOpened()) {
				InputMethodManager imm = (InputMethodManager)getSystemService(
						Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mEditSearch.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(slidingLayer.getWindowToken(), 0);

				slidingLayer.closeLayer(true);
			}
			OrderRelatedAdapter.isNewlyAdded = false;
			orderRelatedAdapter.notifyDataSetChanged();
			break;

			// changes as on 12th July 2013
		case R.id.logo:
			if(slidingLayer.isOpened()) {
				InputMethodManager imm = (InputMethodManager)getSystemService(
						Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mEditSearch.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(slidingLayer.getWindowToken(), 0);
				slidingLayer.closeLayer(true);
			}else {
				imageLogo.setClickable(true);
				OrderRelatedAdapter.mChildPositionSelected = -1;
				orderRelatedAdapter.notifyDataSetChanged();
				slidingLayer.openLayer(true);
			}

			break;

			// changes as on 13th july 2013
		case R.id.menuIcon:
			toggle();
			break;

		case R.id.btnModifiers:
			// open alert for modifiers list
			Log.i(TAG,"outside button click ");
			if(itemAndGuestRelatedTemp != null) {
				Log.i(TAG,"inside button click ");
				clickedModifiersButton = true;
				showModifiersBox();
			}
			break;

		case R.id.btnPlaceOrderLeftMenu:
			Prefs.addKey(OrderRelatedActivity.this, Prefs.MENU_SELECTED, Global.TABLES);
			setSelectedView();
			saveOrder(Global.PLACE_NEW_ORDER);
			break;

		case R.id.txtSettingsLeftMenu:
			Prefs.addKey(OrderRelatedActivity.this, Prefs.MENU_SELECTED, Global.SETTINGS);
			setSelectedView();
			Intent intent = new Intent(OrderRelatedActivity.this, SettingsActivity.class);
			startActivity(intent);
			Global.activityStartAnimationRightToLeft(OrderRelatedActivity.this);
			break;

		case R.id.txtOrdersLeftMenu:
			Prefs.addKey(OrderRelatedActivity.this, Prefs.MENU_SELECTED, Global.ORDERS);
			setSelectedView();
			saveOrder(Global.HOME);
			// do nothing
			break;

		case R.id.txtNotificationsLeftMenu:
			// send to the notification activity
			Prefs.addKey(OrderRelatedActivity.this, Prefs.MENU_SELECTED,Global.NOTIFICATIONS);
			setSelectedView();
			intent = new Intent(this, NotificationListActivity.class);
			startActivity(intent);
			Global.activityStartAnimationRightToLeft(OrderRelatedActivity.this);
			break;

		case R.id.relNotificationCentre:
			Prefs.addKey(OrderRelatedActivity.this, Prefs.MENU_SELECTED,Global.NOTIFICATIONS);
			setSelectedView();
			intent = new Intent(this, NotificationListActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.activity_in_anim,R.anim.activity_out_anim); 
			break;

		case R.id.txtTablesLeftMenu:
			Prefs.addKey(OrderRelatedActivity.this, Prefs.MENU_SELECTED, Global.TABLES);
			setSelectedView();
			saveOrder(Global.TABLE_LIST);
			break;
			// changes as on 19th july 2013
		case R.id.btnAddGuestFooter:
			if(orderToDisplay != null) {

				if(orderRelatedAdapter != null) {
					OrderRelatedAdapter.isNewlyAdded = false;
				}

				guestCounter ++;
				setNewGuest();
				orderRelatedAdapter.notifyDataSetChanged();

				// TODO: 28th sept
				// ask for bill split only if the order has not been sent
				if(orderToDisplay.getOrderId() == null) {
					askForBillSplit();
				}else {
					if(!Prefs.getKey_boolean(OrderRelatedActivity.this, Prefs.IS_BILL_SPLIT)) {
						orderToDisplay.setBillSplit(false);
					}
				}

				guestListView.setSelection(guestListView.getCount() - 1);
				expandList();

				guestSelected = String.valueOf(guestCounter - 1);
				OrderRelatedAdapter.mGroupPositionSelected = guestCounter - 1;
				Guest guest =orderToDisplay.getGuests().get(Integer.valueOf(guestSelected));
				if(guest.getOrderedItems() != null) {
					orderedItemsForSliderWindow = 
							orderToDisplay.getGuests().get(Integer.valueOf(guestSelected)).getOrderedItems();

					if(!isOrderSentToKitchen()) {
						Order orderDummy = (Order)orderDisplayEditObj;
						orderedItemsForSliderWindowCopy = (List<OrderedItem>)Utils.copy(orderDummy.getGuests().get(Integer.valueOf(guestSelected)).getOrderedItems());
					}
				}else {
					orderedItemsForSliderWindow = new ArrayList<OrderedItem>();

					if(!isOrderSentToKitchen()) {
						Order orderDummy = (Order)orderDisplayEditObj;
						orderedItemsForSliderWindowCopy = (List<OrderedItem>)Utils.copy(orderDummy.getGuests().get(Integer.valueOf(guestSelected)).getOrderedItems());

						if(orderedItemsForSliderWindowCopy == null) {
							orderedItemsForSliderWindowCopy = new ArrayList<OrderedItem>();
						}
					}
				}

				// set into tempObject
				if(itemAndGuestRelatedTemp == null) {
					itemAndGuestRelatedTemp = new ItemAndGuestRelatedTemp();
				}

				itemAndGuestRelatedTemp.setGroupPosition(guestCounter - 1);
				itemAndGuestRelatedTemp.setGuests(orderToDisplay.getGuests());
				itemAndGuestRelatedTemp.setGuest(orderToDisplay.getGuests().get(guestCounter - 1));
			}

			btnAddFooter.setEnabled(false);
			btnMinusFooter.setEnabled(false);
			btnQuantityFractionFooter.setEnabled(false);

			btnAddFooter.setTextColor(getResources().getColor(android.R.color.darker_gray));
			btnMinusFooter.setTextColor(getResources().getColor(android.R.color.darker_gray));
			btnQuantityFractionFooter.setTextColor(getResources().getColor(android.R.color.darker_gray));

			mBtnModifiers.setVisibility(View.INVISIBLE);
			mGroupSelected = true;
			orderRelatedAdapter.notifyDataSetChanged();

			String text = mLanguageManager.getSendOrder();
			setFlags();
			btnSendOrderFooter.setText(text);
			break;

		case R.id.btnAddFooter:
			if(mChildSelected) {
				mKitchenNote = "";
				addItemToList();

				if(orderRelatedAdapter != null)  {
					OrderRelatedAdapter.mChildPositionSelected = 
							itemAndGuestRelatedTemp.getChildPosition();
					OrderRelatedAdapter.mGroupPositionSelected = 
							itemAndGuestRelatedTemp.getGroupPosition();
				}

				if(childRowView != null) {
					childRowView.setSelected(true);
				}

				guestListView.requestFocusFromTouch();
				guestListView.setSelected(true);
				guestListView.setSelectedGroup(itemAndGuestRelatedTemp.getGroupPosition());
				guestListView.setSelectedChild(itemAndGuestRelatedTemp.getGroupPosition(), 
						itemAndGuestRelatedTemp.getChildPosition(), 
						true);								
			}
			String btnText = mLanguageManager.getSendOrder();

			btnSendOrderFooter.setText(btnText);
			setFlags();
			break;

		case R.id.btnMinusFooter:
			if(mChildSelected) {
				// reduce the quantity of the item
				List<OrderedItem> orderedItemsInner = itemAndGuestRelatedTemp.getOrderedItems();
				OrderedItem orderedItemInner = itemAndGuestRelatedTemp.getOrderedItem();

				if(orderedItemsInner != null) {
					// search for items with the same value and increase qty
					if(orderedItemInner.isEditable() || 
							(orderedItemInner.isEditable() == false && 
							orderedItemInner.getOrderedItemStatus() == 0)) {

						DecimalFormat dcf = new DecimalFormat("#.##");
						if(!isOrderSentToKitchen()) {
							OrderedItem orderedItemCopy= (OrderedItem)Utils.copy(orderedItemInner);
							Order orderDummy = (Order)orderDisplayEditObj;
							Guest guestDummy = orderDummy.getGuests().get(itemAndGuestRelatedTemp.getGroupPosition());
							List<OrderedItem> orderedItemsDummy = (List<OrderedItem>)Utils.copy( guestDummy.getOrderedItems());

							for(int i=0;i<orderedItemsDummy.size();i++) {
								OrderedItem oItemInner  = orderedItemsDummy.get(i);
								if(OrderManager.getInstance().compareOrderedItems(oItemInner,orderedItemCopy)) {
									String strQtyDummy = "";
									
									if((int)(orderedItemCopy.getQuantity()) > 0 && 
											(orderedItemCopy.getQuantity() - 1) >= 1) {
										strQtyDummy = dcf.format(orderedItemCopy.getQuantity() - 1);
									}else {
										strQtyDummy = dcf.format(orderedItemCopy.getQuantity());
									}
									
									double qtyDummy = Double.parseDouble(strQtyDummy);
									orderedItemCopy.setQuantity(qtyDummy);
									orderedItemCopy.setPriceWithModifiers(orderedItemCopy.getPriceWithModifiers() - orderedItemCopy.getPrice());
									orderedItemsDummy.set(i, orderedItemCopy);
									break;
								}else {
									continue;
								}
							}

							guestDummy.setOrderedItems(orderedItemsDummy);
							orderDummy.getGuests().set(itemAndGuestRelatedTemp.getGroupPosition(), guestDummy);
							orderDisplayEditObj = (Object)orderDummy;
						}

						String strQty = dcf.format(orderedItemInner.getQuantity() - 1);

						double qty = orderedItemInner.getQuantity() - 1;

						if((int)orderedItemInner.getQuantity() > 0 
								&& qty >= 1) {
							orderedItemInner.setQuantity(qty);
						}else {
							orderedItemInner.setQuantity(orderedItemInner.getQuantity());
						}
						
						double priceWithModifiers = orderedItemInner.getPriceWithModifiers();
						orderedItemInner.setPriceWithModifiers(priceWithModifiers - orderedItemInner.getPrice()); 

						if(orderedItemInner.getQuantity() > 1 && 
								((int)orderedItemInner.getQuantity() - orderedItemInner.getQuantity() == 0)) {
							btnMinusFooter.setEnabled(true);
							btnMinusFooter.setTextColor(getResources().getColor(R.color.order_footer_textcolor));
						}else {
							btnMinusFooter.setEnabled(false);
							btnMinusFooter.setTextColor(getResources().getColor(android.R.color.darker_gray));
						}
					}
				}

				itemAndGuestRelatedTemp.setOrderedItems(orderedItemsInner);
				itemAndGuestRelatedTemp.getGuest().setOrderedItems(itemAndGuestRelatedTemp.getOrderedItems());
				itemAndGuestRelatedTemp.setGuest(itemAndGuestRelatedTemp.getGuest());
				orderToDisplay.setGuests(itemAndGuestRelatedTemp.getGuests());

				if(orderRelatedAdapter != null)  {
					OrderRelatedAdapter.mChildPositionSelected = 
							itemAndGuestRelatedTemp.getChildPosition();
					OrderRelatedAdapter.mGroupPositionSelected = 
							itemAndGuestRelatedTemp.getGroupPosition();
				}
				refreshList(orderToDisplay, Global.ORDER_FROM_ADAPTER,Global.ORDER_ACTION_EDIT_ITEM);

				if(childRowView != null) {
					childRowView.setSelected(true);
				}

				guestListView.requestFocusFromTouch();
				guestListView.setSelected(true);
				guestListView.setSelectedGroup(itemAndGuestRelatedTemp.getGroupPosition());
				guestListView.setSelectedChild(itemAndGuestRelatedTemp.getGroupPosition(), 
						itemAndGuestRelatedTemp.getChildPosition(), 
						true);
			}
			setButtonText();
			break;

		case R.id.btnQuantityFraction:
			if(mChildSelected) {
				Log.i(TAG, " itemAndGuestTemp : " + itemAndGuestRelatedTemp.getOrderedItem());
				// show the item desc
				showItemDesc();
			}

			break;

		case R.id.btnDeleteFooter:
			if(mGroupSelected) {
				// disable the buttons related to an item
				setButtonsWhenNoItemSelected();
				
				// delete the guest
				if(itemAndGuestRelatedTemp != null) {
					if(itemAndGuestRelatedTemp.getGuests().size() - 1 == itemAndGuestRelatedTemp.getGroupPosition() && 
							itemAndGuestRelatedTemp.getGroupPosition() == 0) {
						if(itemAndGuestRelatedTemp.getGuest().getGuestId() != null) {
							// cannot delete the guest as the guest has already been sent
							// with the order
							if(isOrderSentToKitchen()) {
								String message = mLanguageManager.getCannotDeleteGuest();
								showMessageBox(message);
							}else {
								showMessage(mLanguageManager.getOneGuestMessage());
							}
						}else {
							showMessage(mLanguageManager.getOneGuestMessage());
						}
					}else {
						boolean messageShown = false;
						if(itemAndGuestRelatedTemp.getGuest().getGuestId() != null) {
							if(isOrderSentToKitchen()) {
								// cannot delete the guest as the guest has already been sent
								// with the order
								String message = mLanguageManager.getCannotDeleteGuest();
								showMessageBox(message);
								messageShown = true;
							}
						}

						if(!messageShown) {
							// Get the guest that has been selected
							// or tapped on
							int position = itemAndGuestRelatedTemp.getGroupPosition();
							Guest guest = itemAndGuestRelatedTemp.getGuests().get(position);

							if(checkIfGuestHasEditableItems(position)) {
								Iterator<Guest> guestIterator =  itemAndGuestRelatedTemp.getGuests().iterator();

								int counter = 0;
								// Go through the set of guests
								while(guestIterator.hasNext()) {
									Guest guestObj = guestIterator.next();
									if(guestObj.equals(guest)) {
										guestIterator.remove();
										Log.i("Dhara","value of counter for guest deleted : " + counter);

										if(!isOrderSentToKitchen()) {
											Order orderDummy = (Order)orderDisplayEditObj;
											Guest guestDummy =orderDummy.getGuests().get(counter);
											guestDummy.setDeleted(true);
											orderDummy.getGuests().set(counter, guestDummy);

											orderDisplayEditObj = (Object)orderDummy;

											Log.i("Dhara","value of guest deleted or not : " + orderDummy.getGuests().get(counter).isDeleted());
										}
									}
									counter ++;
								}
								orderToDisplay.setGuests(itemAndGuestRelatedTemp.getGuests());
								
								orderToDisplay.setBillSplit(false);
								Prefs.addKey(OrderRelatedActivity.this, Prefs.IS_BILL_SPLIT, false);
								Prefs.removeKey(OrderRelatedActivity.this, Prefs.BILL_SPLIT_CHECKED);
								
								refreshList(orderToDisplay, Global.ORDER_FROM_ADAPTER,Global.ORDER_ACTION_DELETE);
							}
						}
					}
				}
			}else if(mChildSelected) {
				// delete the item
				int childPos = itemAndGuestRelatedTemp.getChildPosition();
				int groupPos = itemAndGuestRelatedTemp.getGroupPosition();

				if(itemAndGuestRelatedTemp.getOrderedItem().isEditable() || 
						(itemAndGuestRelatedTemp.getOrderedItem().getOrderedItemStatus() == 0 && 
						itemAndGuestRelatedTemp.getOrderedItem().isEditable() == false)) {
					if(itemAndGuestRelatedTemp.getOrderedItems() != null) {
						if(!isOrderSentToKitchen()) {
							OrderedItem orderedItemCopy = (OrderedItem)Utils.copy(itemAndGuestRelatedTemp.getOrderedItem());

							Order orderDummy = (Order)orderDisplayEditObj;
							Guest guestDummy = orderDummy.getGuests().get(groupPos);
							List<OrderedItem> orderedItemsDummy = (List<OrderedItem>)Utils.copy(guestDummy.getOrderedItems());

							//OrderedItem orderedItemToReplace = Iterables.f

							// changes as on 11th September 2013
							// problems when deleting the first fractional dish
							// first item was replaced which would cause a deleted item to be placed in the order
							// hence going through the items present
							// in order to replace the one that correctly matches the item 
							if(orderedItemsDummy != null && orderedItemsDummy.size() > 0) {
								for(int i=0;i<orderedItemsDummy.size();i++) {
									OrderedItem oItemInner = orderedItemsDummy.get(i);
									if(OrderManager.getInstance().compareOrderedItems(oItemInner, orderedItemCopy)) {
										/*orderedItemCopy.setQuantity(0);
										OrderedItem orderedItemToPlace = (OrderedItem)Utils.copy(orderedItemCopy);*/
										oItemInner.setQuantity(0);
										orderedItemsDummy.set(i, oItemInner);
										break;
									}else {
										continue;
									}
								}
							}

							guestDummy.setOrderedItems(orderedItemsDummy);
							orderDummy.getGuests().set(groupPos, guestDummy);

							orderDisplayEditObj = (Object)orderDummy;
						}

						itemAndGuestRelatedTemp.getOrderedItems().remove(childPos);
						itemAndGuestRelatedTemp.getGuest().setOrderedItems(itemAndGuestRelatedTemp.getOrderedItems());
						itemAndGuestRelatedTemp.getGuests().set(groupPos, itemAndGuestRelatedTemp.getGuest());
					}

					orderToDisplay.setGuests(itemAndGuestRelatedTemp.getGuests());
					mChildSelected = false;

					refreshList(orderToDisplay, Global.ORDER_FROM_ADAPTER,Global.ORDER_ACTION_DELETE);
				}else {
					String message = "";
					message = mLanguageManager.getItemCannotBeDeleted();
					showMessageBox(message);
				}
			}
			setButtonText(); 
			mBtnModifiers.setVisibility(View.INVISIBLE);
			setButtonsWhenNoItemSelected();
			break;

		case R.id.btnAddQuantityDesc:
			if(editQuantity.getText() != null) {
				strQtyVal = editQuantity.getText().toString().trim();
			}

			// value present
			if(strQtyVal.trim().length() > 0) {

				// if the quantity is one
				// then disable the reduction button
				double qty = Double.parseDouble(strQtyVal) + 1;
				mQuantityValue = qty;

				if(qty == 1.0) {
					btnRemoveQuantity.setEnabled(false);
					btnRemoveQuantity.setBackgroundColor(getResources().getColor(R.color.silver));
				}else {
					btnRemoveQuantity.setEnabled(true);
					btnRemoveQuantity.setBackgroundColor(getResources().getColor(R.color.header_end_color));
				}
			}else {
				mQuantityValue = 1.0;
			}
			editQuantity.setText(String.valueOf(mQuantityValue));
			break;

		case R.id.btnAddFractionalDesc:
			if(editFractional.getText() != null) {
				strFractionalVal =editFractional.getText().toString().trim();
			}

			if(strFractionalVal.length() > 0) {
				try {
					//double fractionalVal = Double.parseDouble(strFractionalVal) + 1;
					DecimalFormat dcf = new DecimalFormat("#.##");
					double fractionalVal = dcf.parse(strFractionalVal).doubleValue() + 1; 

					if(fractionalVal <= 0.0) {
						fractionalVal = 1.0;
						btnRemoveFractionalDish.setEnabled(false);
						btnRemoveFractionalDish.setBackgroundColor(getResources().getColor(R.color.silver));
					}else {
						btnRemoveFractionalDish.setEnabled(true);
						btnRemoveFractionalDish.setBackgroundColor(getResources().getColor(R.color.header_end_color));
					}
					mFractionalValue = fractionalVal;
				}catch(java.text.ParseException e) {
					e.printStackTrace();
				}
			}else {
				mFractionalValue = 1.0;
				btnRemoveFractionalDish.setEnabled(false);
				btnRemoveFractionalDish.setBackgroundColor(getResources().getColor(R.color.silver));
			}
			editFractional.setText(String.valueOf(mFractionalValue));

			break;

		case R.id.btnRemoveQuantityDesc:
			if(editQuantity.getText() != null) {
				strQtyVal = editQuantity.getText().toString().trim();
			}

			if(strQtyVal.length() > 0) {
				double qty = Double.parseDouble(strQtyVal) - 1;

				if(qty <= 0) {
					btnRemoveQuantity.setEnabled(false);
					btnRemoveQuantity.setBackgroundColor(getResources().getColor(R.color.silver));
					mQuantityValue = 1.0;
				}else {
					mQuantityValue = qty;
					btnRemoveQuantity.setEnabled(true);
					btnRemoveQuantity.setBackgroundColor(getResources().getColor(R.color.header_end_color));
				}
				editQuantity.setText(String.valueOf(mQuantityValue));
			}

			break;

		case R.id.btnRemoveFractionalDesc:
			if(editFractional.getText() != null) {
				strFractionalVal =editFractional.getText().toString().trim();
			}

			if(strFractionalVal.length() > 0) {
				try {
					//double fractionalVal = Double.parseDouble(strFractionalVal) - 1;

					DecimalFormat dcf = new DecimalFormat("#.##");
					double fractionalVal = dcf.parse(strFractionalVal).doubleValue() -1;

					if(fractionalVal <= 0.0) {
						btnRemoveFractionalDish.setEnabled(false);
						btnRemoveFractionalDish.setBackgroundColor(getResources().getColor(R.color.silver));
						mFractionalValue = 1.0;
					}else {
						btnRemoveFractionalDish.setEnabled(true);
						btnRemoveFractionalDish.setBackgroundColor(getResources().getColor(R.color.header_end_color));
						mFractionalValue = fractionalVal;
					}
				}catch(java.text.ParseException e) {
					e.printStackTrace();
				}

				editFractional.setText(String.valueOf(mFractionalValue));
			}

			break;


		case R.id.btnSendOrderFooter:
			// send the order to the system

			if(orderRelatedAdapter != null) {
				OrderRelatedAdapter.isNewlyAdded = false;
			}

			Prefs.removeKey(OrderRelatedActivity.this, Prefs.BILL_SPLIT_CHECKED);

			String checkout = mLanguageManager.getCheckout();
			String sendOrder = mLanguageManager.getSendOrder();

			if(orderToDisplay != null) {
				if(mBtnModifiers.getVisibility() == View.VISIBLE) {
					mBtnModifiers.setVisibility(View.GONE);
				}

				if(btnSendOrderFooter.getText().toString().equalsIgnoreCase(checkout)) {
					// send a check out request 
					if(orderToDisplay.isBillSplit()) {
						// send a list of orders
						CheckoutDataList checkoutDataList = new CheckoutDataList();
						List<CheckoutDataToSend> lstCheckoutData = 
								new ArrayList<CheckoutDataToSend>();

						List<Guest> guests = orderToDisplay.getGuests();

						if(guests !=null) {
							for(Guest guest : guests) {
								CheckoutDataToSend checkoutData = new CheckoutDataToSend();
								checkoutData.setOrderId(guest.getOrderId());
								checkoutData.setMacAddress(Utils.getMacAddress());
								checkoutData.setWaiterCode(Prefs.getKey(Prefs.WAITER_CODE));
								lstCheckoutData.add(checkoutData);
							}
							checkoutDataList.setLstCheckoutData(lstCheckoutData);
						}

						if(lstCheckoutData != null && lstCheckoutData.size() > 0) {
							new CheckoutListOrderAsyncTask(OrderRelatedActivity.this,
									checkoutDataList,
									RequestType.CHECKOUT_BILL_SPLIT_ORDER,
									orderToDisplay).execute();
						}	
					}else {
						// send the checkout request
						CheckoutData checkoutData = new CheckoutData();
						CheckOutOrderDetails checkOutOrderDetails = new CheckOutOrderDetails();
						checkOutOrderDetails.setOrderId(orderToDisplay.getOrderId());
						checkOutOrderDetails.setMacAddress(Utils.getMacAddress());
						checkOutOrderDetails.setWaiterCode(Prefs.getKey(Prefs.WAITER_CODE));

						checkoutData.setCheckOutOrderDetails(checkOutOrderDetails);

						new CheckoutOrderAsyncTask(OrderRelatedActivity.this,
								checkoutData,
								RequestType.CHECKOUT_ORDER,
								Global.ORDER_RELATED).execute();
					}
				}else if(btnSendOrderFooter.getText().toString().equalsIgnoreCase(sendOrder)) {
					setFlags();
					if(orderToDisplay.getGuests() != null && 
							orderToDisplay.getGuests().size() > 0) {
						HashMap<String, Integer> countOfItemsPerGuest = 
								OrderManager.getInstance().getCountOfItemsPerGuest(orderToDisplay);
						
						// there are guests who have no items
						if(countOfItemsPerGuest.size() > 0) {
							// all the guests have no items
							if(countOfItemsPerGuest.size() == orderToDisplay.getGuests().size()) {
								String message = mLanguageManager.getNoOrderedItems();
								showMessage(message);
							}else {
								String message = mLanguageManager.getNoOrderedItemsForAllGuests();
								showMessage(message);
								return;
							}
						}else {
							if(orderToDisplay.getOrderId() == null) {
								orderToDisplay.setMacAddress(Utils.getMacAddress());
								//orderToDisplay.setMacAddress("20");
								orderToDisplay.setWaiterCode(waiterCode);

								if(orderToDisplay.getTable() != null) {
									orderToDisplay.getTable().setNoOfPeopleOnTheTable(orderToDisplay.getGuests().size());
								}

								if(orderToDisplay.isBillSplit()) {
									boolean showMessageFlag = OrderManager.getInstance().getCountOfItemsForSplitOrders(guests);
									if(showMessageFlag) {
										String message = mLanguageManager.getNoOrderedItemsForAllGuests();
										showMessage(message);
										return;
									}
								}

								new SendOrderToAsyncTask(OrderRelatedActivity.this,
										orderToDisplay,
										RequestType.SEND_NEW_ORDER).execute();	

							}else {
								// checks if the order is sent to the kitchen or not
								// false : the order is not sent to the kitchen
								// true : the order is sent to the kitchen
								// changes as on 19th August 2013
								// dhara

								if(!isOrderSentToKitchen()) {
									Order orderToSendDummy = (Order)orderDisplayEditObj;
									orderToSendDummy.setMacAddress(Utils.getMacAddress());
									//orderToSendDummy.setMacAddress("20");
									orderToSendDummy.setWaiterCode(waiterCode);
									orderToSendDummy.setOrderStatus(null);
									orderToSendDummy.getTable().setTableType(null);
									
									orderToSendDummy = OrderManager.getInstance().setModifiersQtyToOne(orderToSendDummy);

									// changes as on 7th Sept 2013
									// send order with edit order method
									// allows the items to be modified and printed
									new SendOrderToAsyncTask(OrderRelatedActivity.this,
											orderToSendDummy,
											RequestType.EDIT_ORDER).execute();

								}else {
									// check if the order is bill split
									// check for order changes and new orders
									checkForOrderChange();

									// if the order is bill split send the list of orders
									if(orderToDisplay.isBillSplit()) {	
										setFlags();
										// needs to be implemented in the service
										ListOfOrders listOfOrders = new ListOfOrders();
										listOfOrders.setOrders(ordersToSend);

										new UpdateOrderListAsyncTask(OrderRelatedActivity.this,
												listOfOrders).execute();
									}else if(!orderToDisplay.isBillSplit()){
										// send the order as one
										if(guestsToSend.size() > 0) {
											// send the order
											orderToSend.setMacAddress(Utils.getMacAddress());
											//orderToSend.setMacAddress("20");
											orderToSend.setWaiterCode(waiterCode);
											orderToDisplay.getTable().setTableType(null);
											orderToSend.setTable(orderToDisplay.getTable());
											orderToSend.setOrderId(orderToDisplay.getOrderId());
											orderToSend.setGuests(guestsToSend);

											new UpdateOrderAsyncTask(OrderRelatedActivity.this,
													orderToSend).execute();
										}else {
											// there are no new things
											text = mLanguageManager.getCheckout();
											btnSendOrderFooter.setText(text);
										}
									}
								}
							}
						}
					}
				}
			}

			break;


		case R.id.btnBack:
			// picked from the additemfragment
			// changes as on 13th July 2013
			// changes because of design changes
			// when the back button is pressed
			// only when the menu will be organized
			// follow the bread crumbs of the user

			// changes as on 7th December 2013
			// clubbing the functionality into a method
			performBackTracking();
			// changes end here

			break;

		case R.id.btnLeftMenuLock:
			intent = new Intent(this,LoginActivity.class);
			intent.putExtra(Global.ACTION,Global.FROM_LOCK);
			startActivity(intent);
			Global.activityStartAnimationRightToLeft(OrderRelatedActivity.this);
			break;

		case R.id.btnLeftMenuWaiterChange:
			callDialog();
			break;

			// case for force sync button
		case R.id.imgBtnRefresh:
			// when the user presses force sync
			// get all the orders and replace the current order with the new order
			// changes as on 11th November 2013
			mGetCurrentOrderListAsyncTask = 
			new GetCurrentOrderListAsyncTask(OrderRelatedActivity.this, Global.ORDER_RELATED);
			mGetCurrentOrderListAsyncTask.execute();
			break;

		default:
			break;
		}
	}

	/**
	 * Clubbed operations that performs back tracking
	 * when the user presses the back button
	 * Changes as on 7th December 2013, 4:07pm
	 */
	private void performBackTracking() {
		// changes as on 7th November 2013
		// organized menu required but with change in implementation
		// not changing the flag to isMenuOrganized since it is not needed
		if(userSelectionPathIds != null && userSelectionPathIds.size() > 0) {
			// remove the category id and set cursor to display older level
			String categoryId  = userSelectionPathIds.removeLast();

			if(userSelectionPathIds.size() <= 0) {
				cursorWithAllItemsPerCategory = MenuManager.getInstance().getCategoriesForList(sectionId);
				fillListWithData(cursorWithAllItemsPerCategory);
				btnBack.setEnabled(false);
				btnBack.setBackgroundResource(R.drawable.up_arrow_grayed);
			}else {
				cursorWithAllItemsPerCategory = MenuManager.getInstance().getSubcategoriesAndItems(categoryId, sectionId);
				fillListWithData(cursorWithAllItemsPerCategory);
			}
		} 
		// changes end here
	}

	/**
	 * Show the item desc box
	 */
	private void showItemDesc() {
		AlertDialog.Builder builder = new AlertDialog.Builder(OrderRelatedActivity.this);
		builder.setTitle(itemAndGuestRelatedTemp.getOrderedItem().getName());

		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.layout_dialog_item_desc, null);

		txtQuantityLabel = (TextView)view.findViewById(R.id.txtQuantity);
		txtFractionLabel = (TextView)view.findViewById(R.id.txtSize);
		txtAllowFractions = (TextView)view.findViewById(R.id.txtAllowFractionalDishes);
		txtItemPriceIndividual = (TextView)view.findViewById(R.id.txtTotalDescFooter);
		txtItemDesc = (TextView)view.findViewById(R.id.txtItemDesc);

		lnrFractionalDishes = (LinearLayout)view.findViewById(R.id.lnrFractionalDishes);
		checkFractional = (CheckBox)view.findViewById(R.id.checkboxFractionAllowed);

		btnAddOrUpdate = (Button)view.findViewById(R.id.btnAddOrUpdate);
		btnRemove = (Button)view.findViewById(R.id.btnRemoveModifier);
		btnAddQuantity = (Button)view.findViewById(R.id.btnAddQuantityDesc);
		btnRemoveQuantity = (Button)view.findViewById(R.id.btnRemoveQuantityDesc);
		btnAddFractionalDish = (Button)view.findViewById(R.id.btnAddFractionalDesc);
		btnRemoveFractionalDish = (Button)view.findViewById(R.id.btnRemoveFractionalDesc);

		editKitchenNote = (EditText)view.findViewById(R.id.kitchenNote);
		editQuantity = (EditText)view.findViewById(R.id.editQuantityDesc);
		editFractional = (EditText)view.findViewById(R.id.editFractionalDesc);

		InputMethodManager imm = (InputMethodManager)getSystemService(
				Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editKitchenNote.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(editQuantity.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(editFractional.getWindowToken(), 0);

		btnAddQuantity.setOnClickListener(this);
		btnRemoveQuantity.setOnClickListener(this);
		btnAddFractionalDish.setOnClickListener(this);
		btnRemoveFractionalDish.setOnClickListener(this);

		builder.setView(view);

		String message = mLanguageManager.getCancel();

		builder.setNegativeButton(message, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		final AlertDialog dialog = builder.create();
		Button b = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
		if(b != null) {
			b.setBackgroundResource(R.drawable.button_shape_selector);
		}

		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		// picking the code for item desc activity
		// changes as on 18th July 2013

		// set the gui labels as per the language
		txtQuantityLabel.setText(mLanguageManager.getQuantity());
		txtFractionLabel.setText(mLanguageManager.getFraction());
		txtAllowFractions.setText(mLanguageManager.getAllowFractions());
		editKitchenNote.setHint(mLanguageManager.getEnterKitchenNote());
		btnAddOrUpdate.setText(mLanguageManager.getUpdate());
		btnRemove.setText(mLanguageManager.getRemove());

		txtAllowFractions.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		txtQuantityLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		txtFractionLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		editKitchenNote.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		btnAddOrUpdate.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		btnRemove.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		txtItemDesc.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		txtItemPriceIndividual.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());

		if(btnAddNewGuestFooter != null) {
			btnAddNewGuestFooter.setText(mLanguageManager.getAddNewGuest());
		}

		checkFractional.setChecked(false);
		lnrFractionalDishes.setVisibility(View.GONE);

		checkFractional.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(checkFractional.isChecked()) {
					lnrFractionalDishes.setVisibility(View.VISIBLE);
					btnRemoveFractionalDish.setEnabled(false);
					btnRemoveFractionalDish.setBackgroundColor(getResources().getColor(R.color.silver));
				}else {
					lnrFractionalDishes.setVisibility(View.GONE);
				}
			}
		});

		final ItemMaster item = itemAndGuestRelatedTemp.getItem();
		final OrderedItem orderedItem = itemAndGuestRelatedTemp.getOrderedItem();

		// hide the description portion since it is not there
		if(item.getItemDescription() != null && 
				item.getItemDescription().trim().length() > 0) {
			txtItemDesc.setText(item.getItemDescription());
		}else {
			txtItemDesc.setVisibility(View.GONE);
		}

		editQuantity.setText(String.valueOf(orderedItem.getQuantity()));
		editFractional.setText("1.0");
		mFractionalValue = 1.0;
		mQuantityValue = orderedItem.getQuantity();

		if(orderedItem.getComment() != null && 
				orderedItem.getComment().trim().length()  >0) {
			editKitchenNote.setText(orderedItem.getComment());
		}

		txtItemPriceIndividual.setText(decimalFormat.format(item.getPrice()));

		if(mQuantityValue <= 1.0) {
			btnRemoveQuantity.setEnabled(false);
			btnRemoveQuantity.setBackgroundColor(getResources().getColor(R.color.silver));
		}

		editQuantity.addTextChangedListener(new MyTextWatcher("Quantity",item.getPrice()));
		editFractional.addTextChangedListener(new MyTextWatcher("Fractional", item.getPrice()));


		btnAddOrUpdate.setOnClickListener(new  OnClickListener() {
			@Override
			public void onClick(View v) {
				OrderedItem orderedItemInner = null;

				if(editQuantity != null) {
					String qty = editQuantity.getText().toString();

					if(qty.trim().length() > 0) {
						double dblQty =  Double.parseDouble(qty);

						if(dblQty <= 0.0) {
							showMessage(mLanguageManager.getPleaseEnterAValidQuantity());
							return;       
						}
					}else if(qty.trim().length() <= 0) {
						showMessage(mLanguageManager.getPleaseEnterAQuantity());
						return;
					}
				}

				if(editFractional != null) {
					String fractionalDish = editFractional.getText().toString();

					if(fractionalDish.trim().length() > 0 ) {
						DecimalFormat dcf = new DecimalFormat("#.##");
						try {
							double dblFractional = dcf.parse(fractionalDish).doubleValue();

							if(dblFractional > 100.0) {
								showMessage(mLanguageManager.getFractionalLimitValue());
								return;
							}
						}catch(java.text.ParseException e) {
							e.printStackTrace();
							mFractionalValue = 1.0;
						}
					}
				}

				if(editKitchenNote.getText() != null) {
					mKitchenNote = editKitchenNote.getText().toString();
					Log.i(TAG, " kitchen note : " + mKitchenNote);
				}

				// Get all the modifiers
				// by default now all the modifiers 
				// will be added to all the items added
				List<ModifierMaster> modifiersAdded = itemAndGuestRelatedTemp.getOrderedItem().getModifiers();

				//Toast.makeText(OrderRelatedFragment.this, "value modifiersAdded : " + modifiersAdded.size(), 2500).show();

				if(checkFractional.isChecked()) {
					// if the fractional value is more than one
					// denoting that there are no fractional dishes
					Log.i("dhara","value of mFractionalValue : " + mFractionalValue);
					if(mFractionalValue > 1.0) {
						itemAndGuestRelatedTemp.getOrderedItems().remove(orderedItem);
						mQtyPerDish = mQuantityValue / mFractionalValue;

						// fractional dish value will determine 
						// how many dishes have to be entered or added
						loopSize = (int)mFractionalValue;

						Order orderDummy = null;
						Guest guestDummy = null;
						List<OrderedItem> orderedItemsDummy = null;

						DecimalFormat dcf = new DecimalFormat("#.##");
						String strQty = dcf.format(mQtyPerDish);

						BigDecimal roundfinalPrice = new BigDecimal(mQtyPerDish).setScale(2,BigDecimal.ROUND_HALF_UP);
						mQtyPerDish = roundfinalPrice.doubleValue();

						//mQtyPerDish = Double.parseDouble(strQty);


						Log.i("Dhara","qty : " + mQtyPerDish);

						if(!isOrderSentToKitchen()) {
							// changes as on 7th sept 2013
							orderDummy = (Order)orderDisplayEditObj;
							guestDummy = orderDummy.getGuests().get(itemAndGuestRelatedTemp.getGroupPosition());
							orderedItemsDummy = guestDummy.getOrderedItems();
							orderedItemsDummy.get(itemAndGuestRelatedTemp.getChildPosition()).setQuantity(0);
						}

						// create new ordered items and
						// add the modifiers to them
						for(int i=0;i<loopSize;i++) {

							orderedItemInner = addNewOrder(item, mQtyPerDish);
							orderedItemInner.setModifiers((List<ModifierMaster>)Utils.copy(modifiersAdded));
							itemAndGuestRelatedTemp.getOrderedItems().add(orderedItemInner);

							if(!isOrderSentToKitchen()) {
								if(orderedItemsDummy != null) {
									// changes as on 7th sept 2013
									OrderedItem orderedItemCopy = (OrderedItem)Utils.copy(orderedItemInner);
									orderedItemsDummy.add(orderedItemCopy);
								}
							}
						}
						if(!isOrderSentToKitchen()) {
							guestDummy.setOrderedItems(orderedItemsDummy);
							orderDummy.getGuests().set(itemAndGuestRelatedTemp.getGroupPosition(), guestDummy);
							orderDisplayEditObj = orderDummy;
						}
					}else {
						// case when the fraction dish is just one
						// meaning the qty by 1 = qty added
						addOrderedItemsToList(mQuantityValue);
					}
				}else {
					// case when the quantity alone is considered
					addOrderedItemsToList(mQuantityValue);
				}

				// its already added
				// only modifications are taking place

				itemAndGuestRelatedTemp.getGuest().setOrderedItems(itemAndGuestRelatedTemp.getOrderedItems());
				itemAndGuestRelatedTemp.getGuests().set(itemAndGuestRelatedTemp.getGroupPosition(), itemAndGuestRelatedTemp.getGuest());
				orderToDisplay.setGuests(itemAndGuestRelatedTemp.getGuests());

				guestListView.requestFocusFromTouch();
				guestListView.setSelected(true);
				guestListView.setSelectedChild(itemAndGuestRelatedTemp.getGroupPosition(),
						itemAndGuestRelatedTemp.getChildPosition(), 
						true);

				itemAndGuestRelatedTemp.setOrderedItem(itemAndGuestRelatedTemp.getOrderedItems().get(itemAndGuestRelatedTemp.getChildPosition()));
				orderRelatedAdapter.notifyDataSetChanged();
				calculateTotalAmount(orderToDisplay);
				dialog.dismiss();
				
				setButtonsWhenNoItemSelected();
			}
		});

		btnRemove.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mChildSelected = false;

				OrderedItem orderedItemCopy = (OrderedItem)Utils.copy(orderedItem);

				if(!isOrderSentToKitchen()) {
					// changes as on 7th sept 2013
					Order orderDummy = (Order)orderDisplayEditObj;
					Guest guestDummy = (Guest)Utils.copy(orderDummy.getGuests().get(itemAndGuestRelatedTemp.getGroupPosition()));
					List<OrderedItem> orderedItemsDummy = (List<OrderedItem>)Utils.copy(guestDummy.getOrderedItems());

					if(orderedItemsDummy != null && orderedItemsDummy.size() > 0) {
						for(int i=0;i<orderedItemsDummy.size();i++) {
							OrderedItem oItemInner = orderedItemsDummy.get(i);
							if(OrderManager.getInstance().compareOrderedItems(oItemInner, orderedItemCopy)) {
								orderedItemCopy.setQuantity(0);
								OrderedItem orderedItemToPlace = (OrderedItem)Utils.copy(orderedItemCopy);
								orderedItemsDummy.set(i, orderedItemToPlace);
								break;
							}else {
								continue;
							}
						}
					}

					guestDummy.setOrderedItems(orderedItemsDummy);
					orderDummy.getGuests().set(itemAndGuestRelatedTemp.getGroupPosition(), guestDummy);
					orderDisplayEditObj = orderDummy;
					// changes end here
				}

				// remove the ordered item from the list
				List<OrderedItem> lstOrderedItems = itemAndGuestRelatedTemp.getGuest().getOrderedItems();
				lstOrderedItems.remove(orderedItem);
				itemAndGuestRelatedTemp.getGuest().setOrderedItems(lstOrderedItems);

				mBtnModifiers.setVisibility(View.INVISIBLE);

				if(lstOrderedItems != null && lstOrderedItems.size() > 0) {
					// add the guest to the list at the group position
					itemAndGuestRelatedTemp.getGuests().set(itemAndGuestRelatedTemp.getGroupPosition(),
							itemAndGuestRelatedTemp.getGuest());

					// set the list of guests again
					orderToDisplay.setGuests(itemAndGuestRelatedTemp.getGuests());

					if(itemAndGuestRelatedTemp.getGuests().get(itemAndGuestRelatedTemp.getGroupPosition()).getOrderedItems() != null) {
						mChildSelected = true;
						guestListView.setSelectedChild(itemAndGuestRelatedTemp.getGroupPosition(),
								0, 
								true);
					}
				}

				mGroupSelected = true;

				btnMinusFooter.setEnabled(false);
				btnAddFooter.setEnabled(false);
				btnQuantityFractionFooter.setEnabled(false);

				btnMinusFooter.setTextColor(getResources().getColor(android.R.color.darker_gray));
				btnAddFooter.setTextColor(getResources().getColor(android.R.color.darker_gray));
				btnQuantityFractionFooter.setTextColor(getResources().getColor(android.R.color.darker_gray));

				orderRelatedAdapter.notifyDataSetChanged();
				calculateTotalAmount(orderToDisplay);
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	/**
	 * Adds an item to the list present 
	 * if the item is already ordered
	 * the quantity will be increased
	 */
	@SuppressWarnings("unchecked")
	private void addItemToList() {
		List<OrderedItem> orderedItemsInner = itemAndGuestRelatedTemp.getOrderedItems();
		OrderedItem orderedItemInner = itemAndGuestRelatedTemp.getOrderedItem();
		ItemMaster item = itemAndGuestRelatedTemp.getItem();

		boolean flagPresent = false;

		if(orderedItemsInner != null && orderedItemsInner.size() > 0) {
			if(orderedItemInner.isEditable() || 
					(orderedItemInner.getOrderedItemStatus() == 0 && 
					orderedItemInner.isEditable() == false)) {
				double qty = orderedItemInner.getQuantity();
				int intQty = (int)qty;

				double diff = qty - intQty;

				if(diff <= 0) {
					if(!isOrderSentToKitchen()) {
						// code for adding item to the copy of the order
						// changes as on 7th Sept 2013
						// dhara
						OrderedItem orderedItemCopy = (OrderedItem)Utils.copy(orderedItemInner);
						Order orderDummy = (Order)orderDisplayEditObj;
						Guest guestDummy = orderDummy.getGuests().get(itemAndGuestRelatedTemp.getGroupPosition());
						List<OrderedItem> orderedItemsDummy = (List<OrderedItem>)Utils.copy(guestDummy.getOrderedItems());

						for(int i=0;i<orderedItemsDummy.size();i++) {
							OrderedItem oItemInner = orderedItemsDummy.get(i);
							if(OrderManager.getInstance().compareOrderedItems(oItemInner, orderedItemCopy)) {
								orderedItemCopy.setQuantity(orderedItemCopy.getQuantity() + 1);
								orderedItemCopy.setPriceWithModifiers(orderedItemCopy.getPriceWithModifiers() + orderedItemCopy.getPrice());
								orderedItemsDummy.set(i, orderedItemCopy);
								break;
							}else {
								continue;
							}
						}

						guestDummy.setOrderedItems(orderedItemsDummy);
						orderDummy.getGuests().set(itemAndGuestRelatedTemp.getGroupPosition(), guestDummy);

						orderDisplayEditObj = (Object)orderDummy;
						// changes end here
					}

					orderedItemInner.setQuantity(orderedItemInner.getQuantity() + 1);
					double priceWithModifiers = orderedItemInner.getPriceWithModifiers();
					orderedItemInner.setPriceWithModifiers(priceWithModifiers + orderedItemInner.getPrice()); 

					if(mChildSelected) {
						if(orderedItemInner.getQuantity() > 1) {
							btnMinusFooter.setEnabled(true);
							//btnMinusFooter.setBackgroundResource(R.drawable.minus_footer_red);
							btnMinusFooter.setTextColor(getResources().getColor(R.color.order_footer_textcolor));
						}
					}
					orderedItemsInner.set(itemAndGuestRelatedTemp.getChildPosition(),orderedItemInner);
					flagPresent = true;
				}else {
					flagPresent = false;
				}
			}else {
				flagPresent = false;
			}
		}

		if(flagPresent == false) {
			// check for another item with the same contents
			// if present update else create new item
			// changes as on 23rd July 2013
			OrderedItem orderedItemTemp = null;
			Collection<OrderedItem> collection = Collections2.filter(orderedItemsInner, 
					new SearchForItemAdded(item.getItemId()));
			// Get a list of items with the item id

			Log.i(TAG, "value of item id: " + item.getItemId());
			List<OrderedItem> lstOfOrderedItemsFromCollection;
			if(collection != null) {
				lstOfOrderedItemsFromCollection = new ArrayList<OrderedItem>(collection);
			}else {
				lstOfOrderedItemsFromCollection = null;
			}

			if(lstOfOrderedItemsFromCollection != null) {
				for(OrderedItem oObj : lstOfOrderedItemsFromCollection) {
					// not a fractional dish
					if(oObj != null) {
						double qty = oObj.getQuantity();
						int intQty = (int)qty;

						double diff = qty - intQty;
						// it is an integer qty
						// it doesnt have modifiers with it

						//orderedItemInner.setEditable(true);
						// get an ordered item that is editable
						if(diff <= 0 && (oObj.isEditable() ||
								(oObj.isEditable() == false && oObj.getOrderedItemStatus() == 0)) && 
								oObj.getModifiers() == null) {
							Order orderDummy = null;
							Guest guestDummy = null;
							List<OrderedItem> orderedItemsDummy = null;

							if(!isOrderSentToKitchen()) {
								// changes as on 7th September 2013
								// adding the quantity to an order already placed with whole qty
								orderDummy = (Order)orderDisplayEditObj;
								guestDummy = orderDummy.getGuests().get(itemAndGuestRelatedTemp.getGroupPosition());
								orderedItemsDummy = guestDummy.getOrderedItems();

								orderedItemsForSliderWindowCopy = (List<OrderedItem>)Utils.copy(guestDummy.getOrderedItems());
								if(orderedItemsForSliderWindowCopy.size() > 0) {
									OrderedItem orderedItemCopy = (OrderedItem)Utils.copy(oObj);

									Iterator<OrderedItem> it = orderedItemsForSliderWindowCopy.iterator();
									while (it.hasNext()) {
										OrderedItem orderedItemObj = it.next();
										if (orderedItemObj.getId().equals(orderedItemCopy.getId()) && 
												orderedItemObj.isEditable() && 
												orderedItemObj.getQuantity() == orderedItemCopy.getQuantity()) {
											it.remove();
										}
									}
								}
							}


							orderedItemsInner.remove(oObj);


							oObj.setQuantity(oObj.getQuantity() + 1);

							double priceWithModifiers = oObj.getPriceWithModifiers();
							oObj.setPriceWithModifiers(priceWithModifiers + oObj.getPrice()); 

							// related to the copy of the orderedItem removed

							if(!isOrderSentToKitchen()) {
								if(orderedItemsForSliderWindowCopy!= null) {
									OrderedItem orderedItemCopy = (OrderedItem)Utils.copy(oObj);
									orderedItemsForSliderWindowCopy.add(orderedItemCopy);
								}else {
									orderedItemsForSliderWindowCopy = new ArrayList<OrderedItem>();
									OrderedItem orderedItemCopy = (OrderedItem)Utils.copy(oObj);
									orderedItemsForSliderWindowCopy.add(orderedItemCopy);
								}
							}
							orderedItemsInner.add(oObj);
							
							if(!isOrderSentToKitchen()) {
								// changes as on 7th sept 2013						
								List<OrderedItem> orderedItemsCopy = (List<OrderedItem>)Utils.copy(orderedItemsForSliderWindowCopy);
								guestDummy.setOrderedItems(orderedItemsCopy);
								orderDummy.getGuests().set(Integer.parseInt(guestSelected), guestDummy);
								orderDisplayEditObj = orderDummy;
								
								orderDisplayEditObj = 
										OrderManager.getInstance().setModifiersQtyToOne((Order)orderDisplayEditObj);

								Log.i("Dhara"," items into the order : " + 
										orderDummy.getGuests().get(Integer.parseInt(guestSelected)).getOrderedItems().size());
								// changes end here
							}

							if(oObj.getQuantity() > 1) {
								btnMinusFooter.setEnabled(true);
								btnMinusFooter.setTextColor(getResources().getColor(R.color.order_footer_textcolor));
							}
							orderedItemTemp = oObj;
							break;


						}else {
							continue;
						}
					}
					// this is incase out of all the items identified, 
					// none of them has an integer quantity
					// then a new order item is generated
					if(orderedItemTemp == null) {
						OrderedItem orderedItemNew = new OrderedItem();
						//TODO: Removed the comment that gets copied
						orderedItemNew.setComment(null);
						orderedItemNew.setEditable(true);
						orderedItemNew.setId(orderedItemInner.getId());

						List<ModifierMaster> modifiersObj = (List<ModifierMaster>)Utils.copy(orderedItemInner.getModifiers());

						if(modifiersObj != null) {
							for(ModifierMaster modifiers : modifiersObj) {
								modifiers.setQuantity(1.0);
							}
						}

						orderedItemNew.setModifiers(modifiersObj);

						orderedItemNew.setName(orderedItemInner.getName());
						orderedItemNew.setOrderedItemStatus(OrderedItemStatus.ADDED.ordinal());
						orderedItemNew.setPrice(orderedItemInner.getPrice());

						double modifiersCost = mMenuManager.getModifiersCost(orderedItemInner.getModifiers());
						if(modifiersCost == 0.0) {
							orderedItemNew.setPriceWithModifiers(orderedItemInner.getPrice());
						}else {
							orderedItemNew.setPriceWithModifiers((modifiersCost + orderedItemInner.getPrice()));
						}
						orderedItemNew.setQuantity(1.0);
						orderedItemTemp = orderedItemNew;
						orderedItemsInner.add(orderedItemsInner.size(),orderedItemNew);

						if(!isOrderSentToKitchen()) {
							// changes as on 7th Sept 2013
							Order orderDummy = (Order)orderDisplayEditObj;
							Guest guestDummy = orderDummy.getGuests().get(itemAndGuestRelatedTemp.getGroupPosition());
							List<OrderedItem> orderedItemsDummy = guestDummy.getOrderedItems();		
							OrderedItem orderedItemCopy = (OrderedItem)Utils.copy(orderedItemNew);
							orderedItemsDummy.add(orderedItemsDummy.size(), orderedItemCopy);
							guestDummy.setOrderedItems(orderedItemsDummy);
							orderDummy.getGuests().set(itemAndGuestRelatedTemp.getGroupPosition(), guestDummy);

							orderDisplayEditObj = (Object)orderDummy;
							// changes end here
						}
					}
				}
				// Incase there are no items in the list
				if(orderedItemTemp == null) {
					OrderedItem orderedItemNew = new OrderedItem();
					orderedItemNew.setComment(null);
					orderedItemNew.setEditable(true);
					orderedItemNew.setId(orderedItemInner.getId());

					List<ModifierMaster> modifiersObj = (List<ModifierMaster>)Utils.copy(orderedItemInner.getModifiers());

					if(modifiersObj != null) {
						for(ModifierMaster modifiers : modifiersObj) {
							modifiers.setQuantity(1.0);
						}
					}

					orderedItemNew.setModifiers(modifiersObj);

					//orderedItemNew.setModifiers((List<ModifierMaster>)Utils.copy(orderedItemInner.getModifiers()));

					orderedItemNew.setName(orderedItemInner.getName());
					orderedItemNew.setOrderedItemStatus(OrderedItemStatus.ADDED.ordinal());
					orderedItemNew.setPrice(orderedItemInner.getPrice());

					double modifiersCost = mMenuManager.getModifiersCost(orderedItemInner.getModifiers());
					if(modifiersCost == 0.0) {
						orderedItemNew.setPriceWithModifiers(orderedItemInner.getPrice());
					}else {
						orderedItemNew.setPriceWithModifiers((modifiersCost + orderedItemInner.getPrice()));
					}
					orderedItemNew.setQuantity(1.0);
					orderedItemTemp = orderedItemNew;
					orderedItemsInner.add(orderedItemsInner.size(),orderedItemNew);

					if(!isOrderSentToKitchen()) {
						// changes as on 7th Sept 2013
						Order orderDummy = (Order)orderDisplayEditObj;
						Guest guestDummy = orderDummy.getGuests().get(itemAndGuestRelatedTemp.getGroupPosition());
						List<OrderedItem> orderedItemsDummy = guestDummy.getOrderedItems();		
						OrderedItem orderedItemCopy = (OrderedItem)Utils.copy(orderedItemNew);
						orderedItemsDummy.add(orderedItemsDummy.size(), orderedItemCopy);
						guestDummy.setOrderedItems(orderedItemsDummy);
						orderDummy.getGuests().set(itemAndGuestRelatedTemp.getGroupPosition(), guestDummy);

						orderDisplayEditObj = (Object)orderDummy;
						// changes end here
					}
				}
			}

			Log.i(TAG,"inside the false loop");


			if(mChildSelected) {
				btnMinusFooter.setEnabled(false);
				btnMinusFooter.setTextColor(getResources().getColor(android.R.color.darker_gray));
			}
		}

		itemAndGuestRelatedTemp.getGuest().setOrderedItems(orderedItemsInner);
		itemAndGuestRelatedTemp.getGuests().set(itemAndGuestRelatedTemp.getGroupPosition(), 
				itemAndGuestRelatedTemp.getGuest());
		orderToDisplay.setGuests(itemAndGuestRelatedTemp.getGuests());

		OrderRelatedAdapter.mGroupPositionSelected = itemAndGuestRelatedTemp.getGroupPosition();
		OrderRelatedAdapter.mChildPositionSelected = itemAndGuestRelatedTemp.getChildPosition();

		refreshList(orderToDisplay, Global.ORDER_FROM_ADAPTER,Global.ORDER_ACTION_EDIT_ITEM);
	}

	/**
	 * Keeps track of the quantity that has been entered 
	 * and accordingly change the price (total)
	 * Changes to fractional dishes will not cause a change 
	 * in the item total price
	 * @author dhara.shah
	 *
	 */
	class MyTextWatcher implements TextWatcher {
		private String mWhat;
		private double mItemPrice;
		private AutoCompleteTextView mEditSearch;

		public MyTextWatcher(String what, double itemPrice) {
			mWhat  =what;
			mItemPrice = itemPrice;
		}

		public MyTextWatcher(AutoCompleteTextView editSearch) {
			mEditSearch = editSearch;
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
			if(mWhat == null) {
				// changes as on 1st august 2013

				if(mItemAdapter != null) {
					mItemAdapter.getFilter().filter(s.toString());
				}

				mEditSearch.setCompoundDrawables(null, null,
						mEditSearch.getText().toString().equals("") ? null : clearText, null);
			}else {	
				if(mWhat.equalsIgnoreCase("Quantity")) {
					// Get the value from the editbox

					if(s.toString().trim().length() <= 0) {
						btnRemoveQuantity.setEnabled(false);
						btnRemoveQuantity.setBackgroundColor(getResources().getColor(R.color.silver));
						mQuantityValue = 1.0;
					}else {
						if(Double.parseDouble(s.toString().trim()) <= 1.0) {
							btnRemoveQuantity.setEnabled(false);
							btnRemoveQuantity.setBackgroundColor(getResources().getColor(R.color.silver));
						}else {
							btnRemoveQuantity.setEnabled(true);
							btnRemoveQuantity.setBackgroundColor(getResources().getColor(R.color.header_end_color));
						}
						mQuantityValue = Double.parseDouble(editQuantity.getText().toString());
					}
				}else if(mWhat.equalsIgnoreCase("Fractional")) {
					// Get the value from the edit box

					if(s.toString().trim().length() <= 0) {
						btnRemoveFractionalDish.setEnabled(false);
						btnRemoveFractionalDish.setBackgroundColor(getResources().getColor(R.color.silver));
					}else {
						// added this to prevent minus button clicks
						if(Double.parseDouble(s.toString().trim()) <= 1.0) {
							btnRemoveFractionalDish.setEnabled(false);
							btnRemoveFractionalDish.setBackgroundColor(getResources().getColor(R.color.silver));
						}else {
							btnRemoveFractionalDish.setEnabled(true);
							btnRemoveFractionalDish.setBackgroundColor(getResources().getColor(R.color.header_end_color));
						}
					}

					if(editFractional.getText().toString().trim().length() > 0) {
						try {
							String fractionalValue = editFractional.getText().toString();
							DecimalFormat dcf = new DecimalFormat("#.##");
							double dblFractionalValue = dcf.parse(fractionalValue).doubleValue();
							mFractionalValue = dblFractionalValue;
						}catch (java.text.ParseException e) {
							e.printStackTrace();
						}
					}else {
						mFractionalValue = 0.0;
					}
				}
			}
		}
	}

	private void showModifiersBox() {
		itemListView.setEnabled(false);
		String save = mLanguageManager.getSave();
		String cancel = mLanguageManager.getCancel();
		String nonGroupModifiers = mLanguageManager.getNonGroupModifiers();

		final AlertDialog builder = new AlertDialog.Builder(OrderRelatedActivity.this)
		.setNegativeButton(cancel, null)
		.setPositiveButton(save, null)
		.create();

		builder.setTitle(itemAndGuestRelatedTemp.getOrderedItem().getName());

		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.list_for_modifiers, null);

		builder.setView(view);
		builder.setCancelable(false);

		final ItemMaster item = itemAndGuestRelatedTemp.getItem();
		final Cursor c = MenuManager.getInstance().getModifiersPerItem(item.getItemId(),sectionId);
		countOfModifiersMap.clear();
		// changes as on 23rd September 2013
		groupCursorModifiers = MenuManager.getInstance().getGroupModifiers(item.getItemId());
		
		//fe0d42ff-9076-4600-b617-45ef1ca99dd9
		
		if(itemAndGuestRelatedTemp.getOrderedItem().getModifiers() != null) {
			modifiersSelected = itemAndGuestRelatedTemp.getOrderedItem().getModifiers();
			countOfModifiersMap.clear();
			for(ModifierMaster modifierMaster : modifiersSelected) {
				if(modifierMaster.getGroupId() != null) {
					if(countOfModifiersMap.containsKey(modifierMaster.getGroupId())) {
						// incr counter
						countOfModifiersMap.put(modifierMaster.getGroupId(), 
								countOfModifiersMap.get(modifierMaster.getGroupId()) + 1);
					}else {
						countOfModifiersMap.put(modifierMaster.getGroupId(), 1);
					}
				}
			}
		}else {
			modifiersSelected = new ArrayList<ModifierMaster>();
		}

		// listview for group modifiers
		final ExpandableListView lstOfModifiersExpandable = (ExpandableListView)view.findViewById(R.id.expandableModifiersList);

		// listview for nongroup modifiers
		final ListView lstviewModifiersWithoutGroup = (ListView)view.findViewById(android.R.id.list);

		// modifiers without a group
		final ModifierCursorAdapter modifierAdapters = new ModifierCursorAdapter(OrderRelatedActivity.this, 
				c,
				false,
				itemAndGuestRelatedTemp.getOrderedItem().getModifiers());
		
		//TODO: need to check this
		lstviewModifiersWithoutGroup.setAdapter(modifierAdapters);

		// changes as on 6th November, 2013
		// displaying optional modifiers under the group modifiers
		final RelativeLayout relHeaderMain = (RelativeLayout)view.findViewById(R.id.relOptionalModifiersWithoutGroup);
		final RelativeLayout relClick = (RelativeLayout)view.findViewById(R.id.relHeaderForList);
		final ImageView imgArrow = (ImageView)view.findViewById(R.id.imgArrow);
		final TextView txtHeaderName = (TextView)view.findViewById(R.id.txtHeaderName);

		txtHeaderName.setText(nonGroupModifiers);

		lstviewModifiersWithoutGroup.setVisibility(View.GONE);

		if(c != null && c.getCount() > 0) {
			relHeaderMain.setVisibility(View.VISIBLE);

			if((groupCursorModifiers != null && 
					groupCursorModifiers.getCount() <= 0) ||
					groupCursorModifiers == null) {
				lstviewModifiersWithoutGroup.setVisibility(View.VISIBLE);
				imgArrow.setBackgroundResource(R.drawable.arrow_down);
			}
		}

		relClick.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(lstviewModifiersWithoutGroup.getVisibility() == View.VISIBLE) {
					lstviewModifiersWithoutGroup.setVisibility(View.GONE);
					imgArrow.setBackgroundResource(R.drawable.arrow_right);

					// Commenting this as on 7th December 2013
					// since Nick wants collapsible groups
					//if(groupCursorModifiers != null) {
					//	lstviewModifiersWithoutGroup.setVisibility(View.VISIBLE);
					//	imgArrow.setBackgroundResource(R.drawable.arrow_down);
					//}
					// changes end here
				}else {
					lstviewModifiersWithoutGroup.setVisibility(View.VISIBLE);
					imgArrow.setBackgroundResource(R.drawable.arrow_down);

					// changes as on 11th November 2013
					// collapse the group modifiers since
					// non-group modifiers has been displayed
					if(groupCursorModifiers != null) {
						for(int i=0;i<groupCursorModifiers.getCount();i++) {
							lstOfModifiersExpandable.collapseGroup(i);
						}
					}
				}
			}
		});
		// changes end here

		// group modifiers
		final ModifierCursorTreeAdapter modifierTreeAdapter =
				new ModifierCursorTreeAdapter(OrderRelatedActivity.this, 
						groupCursorModifiers,
						false,
						modifiersSelected);

		final DisplayMetrics outMetrics = new DisplayMetrics();
		builder.getWindow().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

		lstOfModifiersExpandable.setAdapter(modifierTreeAdapter);

		ViewTreeObserver vto = lstOfModifiersExpandable.getViewTreeObserver();

		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
			@Override
			public void onGlobalLayout() {
				if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
					lstOfModifiersExpandable.setIndicatorBounds(0, 40);
				} else {
					lstOfModifiersExpandable.setIndicatorBoundsRelative(0,40);
				}
			}
		});

		if(groupCursorModifiers != null) {
			// only the first group is selected
			lstOfModifiersExpandable.expandGroup(0);
			// changes end here
		}

		lstOfModifiersExpandable.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {

				// Changes as on 7th December 2013
				// Nick now wants all groups to be collapsible

				if(lstOfModifiersExpandable.isGroupExpanded(groupPosition)) {
					lstOfModifiersExpandable.collapseGroup(groupPosition);
				}else {
					lstOfModifiersExpandable.expandGroup(groupPosition);

					// changes as on 6th Novemeber, 2013
					// collapsing all other groups except the one expanded
					for(int i=0;i<groupCursorModifiers.getCount();i++) {
						if(i != groupPosition) {
							lstOfModifiersExpandable.collapseGroup(i);
						}
					}
					// changes end here
				}

				// collapse the non-group modifiers also
				if(c != null && c.getCount() > 0) {
					relHeaderMain.setVisibility(View.VISIBLE);

					lstviewModifiersWithoutGroup.setVisibility(View.GONE);
					imgArrow.setBackgroundResource(R.drawable.arrow_right);
				}
				// changes end here

				return true;
			}
		});

		lstOfModifiersExpandable.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent,final View v,
					int groupPosition, int childPosition, long id) {

				groupCursorModifiers.moveToPosition(groupPosition);
				String groupId = groupCursorModifiers.getString(groupCursorModifiers.getColumnIndex(DBHelper.GROUP_ID));
				maxAmount = groupCursorModifiers.getDouble(groupCursorModifiers.getColumnIndex(DBHelper.MAX_AMOUNT));
				minAmount = groupCursorModifiers.getDouble(groupCursorModifiers.getColumnIndex(DBHelper.MIN_AMOUNT));
				String gName = groupCursorModifiers.getString(groupCursorModifiers.getColumnIndex(DBHelper.GROUP_NAME));
				Cursor childCursor = MenuManager.getInstance().getModifiersUnderCursor(groupId);

				Log.i("dhara","max : " + maxAmount);

				if(childCursor != null) {
					childCursor.moveToPosition(childPosition);

					ModifierMaster modifierMaster = new ModifierMaster();
					modifierMaster.setDescription(childCursor.getString(childCursor.getColumnIndex(DBHelper.MODIFIER_DESC)));
					modifierMaster.setId(childCursor.getString(childCursor.getColumnIndex(DBHelper.MODIFIER_ID)));
					modifierMaster.setModifierName(childCursor.getString(childCursor.getColumnIndex(DBHelper.MODIFIER_NAME)));
					modifierMaster.setPrice(childCursor.getDouble(childCursor.getColumnIndex(DBHelper.MODIFIER_PRICE)));
					modifierMaster.setGroupId(groupId);
					modifierMaster.setQuantity(1);

					int isActive = childCursor.getInt(childCursor.getColumnIndex(DBHelper.MODIFIER_IS_ACTIVE));

					Log.i(TAG, " value of isActive : " + isActive);

					if(isActive == 1) {
						CheckBox chckSelected = (CheckBox)v.findViewById(R.id.checkBoxModifier);
						chckSelected.setFocusable(false);
						chckSelected.toggle();
						Log.i(TAG,"value: " + chckSelected.isChecked());

						//lstOfModifiers.setItemChecked(position, chckSelected.isChecked());

						if(!chckSelected.isChecked()) {
							ModifierMaster modifierPresent = Iterables.find(modifiersSelected, 
									new SearchForModifier(modifierMaster.getId()), null);

							Log.i(TAG, "modifiersSelected : " + modifiersSelected);
							Log.i(TAG, "modifiersSelectedCopy : " + modifiersSelectedCopy);

							if(modifierPresent != null) {
								Log.i(TAG, " removed the modifier ");

								if(countOfModifiersMap.containsKey(groupId)) {
									int counter = countOfModifiersMap.get(groupId);

									counter--;

									if(counter <= 0) {
										countOfModifiersMap.put(groupId, 0);
									}else {
										countOfModifiersMap.put(groupId, counter);
									}

									modifiersSelected.remove(modifierPresent);

									if(modifierAdapters != null) {
										modifierAdapters.setList(modifiersSelected);
									}

								}else {
									modifiersSelected.remove(modifierPresent);

									if(modifierAdapters != null) {
										modifierAdapters.setList(modifiersSelected);
									}
								}
							}
						}else {
							if(countOfModifiersMap.containsKey(groupId)) {
								int counter = countOfModifiersMap.get(groupId);

								if(counter >= maxAmount) {
									// show message box
									showMessageBox(mLanguageManager.getYouCanOnlySelect() + 
											" " + 
											(int)maxAmount + 
											" " +
											mLanguageManager.getModifiersLast() +
											" " + 
											mLanguageManager.getUnder() + 
											" " +
											gName + 
											" " + 
											mLanguageManager.getGroup());
									chckSelected.toggle();
								}else {
									counter ++;
									countOfModifiersMap.put(groupId, counter);
									modifiersSelected.add(modifierMaster);
									Log.i("dhara", "size of array : " + modifiersSelected.size());
								}
							}else {
								countOfModifiersMap.put(groupId, 1);
								modifiersSelected.add(modifierMaster);
							}

							if(modifierAdapters != null) {
								modifierAdapters.setList(modifiersSelected);
							}
						}
					}
				}
				return true;
			}
		});
		
		//TODO: new
		if(modifiersSelected != null && modifiersSelected.size() <= 0) {
			if(c != null && c.getCount() > 0) {
				for(int i=0;i<c.getCount();i++) {
					c.moveToPosition(i);
					if(Integer.parseInt(c.getString(c.getColumnIndex(DBHelper.MIN_AMOUNT))) == 1) {
						// need to be there
						// set them as checked by default
						
						ModifierMaster mObj = new ModifierMaster();
						mObj.setDescription(c.getString(c.getColumnIndex(DBHelper.MODIFIER_DESC)));
						mObj.setId(c.getString(c.getColumnIndex(DBHelper.MODIFIER_ID)));
						mObj.setModifierName(c.getString(c.getColumnIndex(DBHelper.MODIFIER_NAME)));
						mObj.setPrice(c.getDouble(c.getColumnIndex(DBHelper.MODIFIER_PRICE)));
						mObj.setQuantity(1);
						
						if(modifiersSelected != null) {
							modifiersSelected.add(mObj);
						}
					}
				}
			}
		}
		
		if(modifierAdapters != null) {
			modifierAdapters.setList(modifiersSelected);
		}

		// Changes in the name of the listview that holds a groupless modifier list
		lstviewModifiersWithoutGroup.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				ModifierCursorAdapter adapter = (ModifierCursorAdapter)lstviewModifiersWithoutGroup.getAdapter();
				Cursor cModifier = adapter.getCursor();

				if(cModifier != null) {
					cModifier.moveToPosition(position);

					ModifierMaster modifierMaster = new ModifierMaster();
					modifierMaster.setDescription(cModifier.getString(cModifier.getColumnIndex(DBHelper.MODIFIER_DESC)));
					modifierMaster.setId(cModifier.getString(cModifier.getColumnIndex(DBHelper.MODIFIER_ID)));
					modifierMaster.setModifierName(cModifier.getString(cModifier.getColumnIndex(DBHelper.MODIFIER_NAME)));
					modifierMaster.setPrice(cModifier.getDouble(cModifier.getColumnIndex(DBHelper.MODIFIER_PRICE)));
					modifierMaster.setQuantity(1);

					int isActive = cModifier.getInt(cModifier.getColumnIndex(DBHelper.MODIFIER_IS_ACTIVE));

					Log.i(TAG, " value of isActive : " + isActive);

					if(isActive == 1) {
						CheckBox chckSelected = (CheckBox)view.findViewById(R.id.checkBoxModifier);
						chckSelected.setFocusable(false);
						chckSelected.toggle();
						Log.i(TAG,"value: " + chckSelected.isChecked());

						//lstOfModifiers.setItemChecked(position, chckSelected.isChecked());

						if(!chckSelected.isChecked()) {
							// changes in the group id, pass null or nothing
							Log.i(TAG, " inside the if condition of modifiers");
							ModifierMaster modifierPresent = Iterables.find(modifiersSelected, 
									new SearchForModifier(modifierMaster.getId()), null);

							Log.i(TAG, "modifiersSelected : " + modifiersSelected);
							Log.i(TAG, "modifiersSelectedCopy : " + modifiersSelectedCopy);

							if(modifierPresent != null) {
								Log.i(TAG, " removed the modifier ");
								
								//TODO: needed
								if(Integer.parseInt(cModifier.getString(cModifier.getColumnIndex(DBHelper.MIN_AMOUNT))) < 1) {
									modifiersSelected.remove(modifierPresent);
								}else {
									// min is one hence it has to remain selected
									chckSelected.toggle();
								}

								if(modifierAdapters != null) {
									modifierAdapters.setList(modifiersSelected);
								}
							}
						}else { 
							// cancelled.. added group modifiers and also non-group modifiers
							// changes as on 20th September 2013 - dhara
							// changes to support only one modifier selection
							//modifiersSelected.add(modifierMaster);

							//modifiersSelected = new ArrayList<ModifierMaster>();
							
							modifiersSelected.add(modifierMaster);

							if(modifierAdapters != null) {
								modifierAdapters.setList(modifiersSelected);
							}
						}

						mMenuManager.setQuantityForModifiers(modifiersSelected);

						if(modifierAdapters != null) {
							modifierAdapters.setList(modifiersSelected);
						}
					}
				}

			}
		});

		builder.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if(!errorFlag) {
					dialog.dismiss();
				}
			}
		});

		builder.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				Button b = builder.getButton(AlertDialog.BUTTON_POSITIVE);
				b.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						itemListView.setEnabled(true);
						// set the modifier... 
						Log.i(TAG, " pos called !!! ");

						Log.i(TAG, "modifiersSelected : " + modifiersSelected);
						Log.i(TAG, "modifiersSelectedCopy : " + modifiersSelectedCopy);

						Log.i(TAG,"size of modifiers : " + (modifiersSelectedCopy != null ? modifiersSelectedCopy.size() : "null"));

						if(groupCursorModifiers != null) {
							for(int i=0;i<groupCursorModifiers.getCount();i++) {
								groupCursorModifiers.moveToPosition(i);
								String gId = groupCursorModifiers.getString(groupCursorModifiers.getColumnIndex(DBHelper.GROUP_ID));
								String gName = groupCursorModifiers.getString(groupCursorModifiers.getColumnIndex(DBHelper.GROUP_NAME));
								double min = groupCursorModifiers.getDouble(groupCursorModifiers.getColumnIndex(DBHelper.MIN_AMOUNT));

								if(countOfModifiersMap.containsKey(gId)) {
									if(countOfModifiersMap.get(gId) < min) {
										errorFlag = true;
										// show message
										showMessageBox(getString(R.string.min_modifiers_to_select) + 
												" " + 
												(int)min + 
												" " +
												getString(R.string.modifiers_last) +
												" " + 
												getString(R.string.under) + 
												" " +
												gName + 
												" " + 
												getString(R.string.group));
										
										modifiersSelected = new ArrayList<ModifierMaster>();
										modifiersSelected.addAll(modifiersSelectedCopy);
										break;
									}else {
										errorFlag = false;
										continue;
									}
								}else {
									if(min > 0) {
										// break here
										errorFlag = true;
										showMessageBox(getString(R.string.min_modifiers_to_select) + 
												" " + 
												(int)min + 
												" " +
												getString(R.string.modifiers_last) +
												" " + 
												getString(R.string.under) + 
												" " +
												gName + 
												" " + 
												getString(R.string.group));
										
										modifiersSelected = new ArrayList<ModifierMaster>();
										modifiersSelected.addAll(modifiersSelectedCopy);
										break;
									}else{
										errorFlag = false;
										continue;
									}
								}
							}
						}

						if(!errorFlag) {
							modifierAdapters.setList(itemAndGuestRelatedTemp.getOrderedItem().getModifiers());
							if(itemAndGuestRelatedTemp.getOrderedItem().getModifiers() != null) {
								modifiersSelectedCopy = new ArrayList<ModifierMaster>();
								modifiersSelectedCopy.addAll(itemAndGuestRelatedTemp.getOrderedItem().getModifiers());
							}else {
								modifiersSelectedCopy = null;
							}
							makeChangesToSelectedItem();
							builder.dismiss();
						}
					}
				});

				Button n = builder.getButton(AlertDialog.BUTTON_NEGATIVE);
				n.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						itemListView.setEnabled(true);
						Log.i(TAG, " neg called !!! ");
						// If the item has compulsory modifiers then 
						// in that case the item will be deleted
						// when the user taps on cancel
						if(item.isHasModifiers() && !clickedModifiersButton) {
							// delete the item
							int childPos = itemAndGuestRelatedTemp.getChildPosition();
							int groupPos = itemAndGuestRelatedTemp.getGroupPosition();

							if(itemAndGuestRelatedTemp.getOrderedItem().isEditable() || 
									(itemAndGuestRelatedTemp.getOrderedItem().getOrderedItemStatus() == 0 && 
									itemAndGuestRelatedTemp.getOrderedItem().isEditable() == false)) {
								if(itemAndGuestRelatedTemp.getOrderedItems() != null) {
									//Order orderDummy = (Order)orderDisplayEditObj;
									//Order orderDummy = singleOrderEdit.get(0);

									if(!isOrderSentToKitchen()) {
										OrderedItem orderedItemCopy = (OrderedItem)Utils.copy(itemAndGuestRelatedTemp.getOrderedItem());

										Order orderDummy = (Order)orderDisplayEditObj;
										Guest guestDummy = orderDummy.getGuests().get(groupPos);
										@SuppressWarnings("unchecked")
										List<OrderedItem> orderedItemsDummy = (List<OrderedItem>)Utils.copy(guestDummy.getOrderedItems());

										//OrderedItem orderedItemToReplace = Iterables.f

										// changes as on 11th September 2013
										// problems when deleting the first fractional dish
										// first item was replaced which would cause a deleted item to be placed in the order
										// hence going throught the items present
										// in order to replace the one that correctly matches the item 
										if(orderedItemsDummy != null && orderedItemsDummy.size() > 0) {
											for(int i=0;i<orderedItemsDummy.size();i++) {
												OrderedItem oItemInner = orderedItemsDummy.get(i);
												if(OrderManager.getInstance().compareOrderedItems(oItemInner, orderedItemCopy)) {
													//Toast.makeText(OrderRelatedFragment.this, "order to be deleted is the same",2500).show();

													orderedItemCopy.setQuantity(0);
													OrderedItem orderedItemToPlace = (OrderedItem)Utils.copy(orderedItemCopy);
													oItemInner.setQuantity(0);
													orderedItemsDummy.set(i, oItemInner);
													Log.i("Dhara","item qty from obj: " 
															+ orderDummy.getGuests().get(groupPos).getOrderedItems().get(childPos).getQuantity());
													break;
												}else {
													//Toast.makeText(OrderRelatedFragment.this, "order is the same",2500).show();
													continue;
												}
											}
										}

										guestDummy.setOrderedItems(orderedItemsDummy);
										orderDummy.getGuests().set(groupPos, guestDummy);

										orderDisplayEditObj = (Object)orderDummy;
									}

									if(childPos >= 0) {
										itemAndGuestRelatedTemp.getOrderedItems().remove(childPos);
									}

									itemAndGuestRelatedTemp.getGuest().setOrderedItems(itemAndGuestRelatedTemp.getOrderedItems());
									itemAndGuestRelatedTemp.getGuests().set(groupPos, itemAndGuestRelatedTemp.getGuest());
								}

								orderToDisplay.setGuests(itemAndGuestRelatedTemp.getGuests());
								mChildSelected = false;

								refreshList(orderToDisplay, Global.ORDER_FROM_ADAPTER,Global.ORDER_ACTION_DELETE);
								mBtnModifiers.setVisibility(View.GONE);
							}
							setButtons();
							builder.dismiss();
						}else {
							WaiterPadApplication.LOG.debug("flag value " + errorFlag + " ----- " + TAG);
							WaiterPadApplication.LOG.debug("The modifiers have been selected properly ----- " + TAG);
							
							modifiersSelected = new ArrayList<ModifierMaster>();
							if(modifiersSelectedCopy != null) {
								if(modifiersSelectedCopy.size() > 0) {
									modifiersSelected.addAll(modifiersSelectedCopy);
								}else {
									if(itemAndGuestRelatedTemp.getOrderedItem().getModifiers() != null) {
										modifiersSelected.addAll(itemAndGuestRelatedTemp.getOrderedItem().getModifiers());
									}
								}
							}else {
								if(itemAndGuestRelatedTemp.getOrderedItem().getModifiers() != null) {
									modifiersSelected.addAll(itemAndGuestRelatedTemp.getOrderedItem().getModifiers());
								}else {
									modifiersSelected = null;
								}
							}

							makeChangesToSelectedItem();
							builder.dismiss();
						}
					}
				});
			}
		});


		builder.show();
	}

	/**
	 * Added as on 6th August 2013
	 * Function adds the modifiers accordingly
	 * if not present it removes the modifiers
	 * used by the dialog cancel and save button
	 */
	private void makeChangesToSelectedItem() {

		//patch to solve array index
		if(itemAndGuestRelatedTemp.getChildPosition() > itemAndGuestRelatedTemp.getGuest().getOrderedItems().size())
			return;
		//patch end


		int childPos = itemAndGuestRelatedTemp.getChildPosition();
		List<OrderedItem> orderedItems = itemAndGuestRelatedTemp.getOrderedItems();

		if(modifiersSelected != null && modifiersSelected.size() > 0) {
			OrderedItem orderedItem = itemAndGuestRelatedTemp.getOrderedItem();
			setQuantityForModifiers();

			if(orderedItem.isEditable() || 
					(orderedItem.isEditable() == false && orderedItem.getOrderedItemStatus() == 0)) {
				orderedItem.setModifiers(modifiersSelected);
				itemAndGuestRelatedTemp.setOrderedItem(orderedItem);
				itemAndGuestRelatedTemp.getGuest().getOrderedItems().set(itemAndGuestRelatedTemp.getChildPosition(), orderedItem);

				if(!isOrderSentToKitchen()) {
					// changes as on 7th sept 2013
					Order orderDummy = (Order)orderDisplayEditObj;
					Guest guestDummy = orderDummy.getGuests().get(itemAndGuestRelatedTemp.getGroupPosition());
					List<OrderedItem> orderedItemsDummy = guestDummy.getOrderedItems();
					OrderedItem orderedItemDummy = orderedItemsDummy.get(itemAndGuestRelatedTemp.getChildPosition());
					orderedItemDummy.setModifiers(modifiersSelected);
					orderedItemsDummy.set(itemAndGuestRelatedTemp.getChildPosition(),orderedItemDummy);

					guestDummy.setOrderedItems(orderedItemsDummy);
					orderDummy.getGuests().set(itemAndGuestRelatedTemp.getGroupPosition(), guestDummy);
					orderDisplayEditObj = orderDummy;
					// changes end here
				}
			}else {
				// add new item and then set the modifiers
				addItemToListWithModifiers();
				int position = itemAndGuestRelatedTemp.getGuest().getOrderedItems().size() - 1;
				OrderedItem orderedItemInner = itemAndGuestRelatedTemp.getGuest().getOrderedItems().get(position);
				orderedItemInner.setModifiers(modifiersSelected);
				itemAndGuestRelatedTemp.getGuest().getOrderedItems().set(position, orderedItemInner);

				if(!isOrderSentToKitchen()) {
					// changes as on 7th sept 2013
					Order orderDummy = (Order)orderDisplayEditObj;
					Guest guestDummy = orderDummy.getGuests().get(itemAndGuestRelatedTemp.getGroupPosition());
					List<OrderedItem> orderedItemsDummy = guestDummy.getOrderedItems();
					OrderedItem orderedItemDummy = orderedItemsDummy.get(position);
					orderedItemDummy.setModifiers(modifiersSelected);
					orderedItemsDummy.set(position,orderedItemDummy);

					guestDummy.setOrderedItems(orderedItemsDummy);
					orderDummy.getGuests().set(itemAndGuestRelatedTemp.getGroupPosition(), guestDummy);
					orderDisplayEditObj = orderDummy;
					// changes end here
				}
			}
		}else {
			OrderedItem orderedItem = itemAndGuestRelatedTemp.getOrderedItem();
			orderedItem.setModifiers(null);
			itemAndGuestRelatedTemp.setOrderedItem(orderedItem);
			itemAndGuestRelatedTemp.getGuest().getOrderedItems().set(itemAndGuestRelatedTemp.getChildPosition(), orderedItem);

			if(!isOrderSentToKitchen()) {
				// changes as on 7th sept 2013
				Order orderDummy = (Order)orderDisplayEditObj;
				Guest guestDummy = orderDummy.getGuests().get(itemAndGuestRelatedTemp.getGroupPosition());
				List<OrderedItem> orderedItemsDummy = guestDummy.getOrderedItems();
				OrderedItem orderedItemDummy = orderedItemsDummy.get(itemAndGuestRelatedTemp.getChildPosition());
				orderedItemDummy.setModifiers(null);
				orderedItemsDummy.set(itemAndGuestRelatedTemp.getChildPosition(),orderedItemDummy);

				guestDummy.setOrderedItems(orderedItemsDummy);
				orderDummy.getGuests().set(itemAndGuestRelatedTemp.getGroupPosition(), guestDummy);
				orderDisplayEditObj = orderDummy;
				// changes end here
			}
		}

		orderedItems.set(childPos,itemAndGuestRelatedTemp.getOrderedItem());
		itemAndGuestRelatedTemp.setOrderedItems(orderedItems);
		itemAndGuestRelatedTemp.getGuest().setOrderedItems(itemAndGuestRelatedTemp.getOrderedItems());
		itemAndGuestRelatedTemp.getGuests().set(itemAndGuestRelatedTemp.getGroupPosition(), itemAndGuestRelatedTemp.getGuest());

		orderToDisplay.setGuests(itemAndGuestRelatedTemp.getGuests());

		OrderRelatedAdapter.mChildPositionSelected= itemAndGuestRelatedTemp.getChildPosition();
		OrderRelatedAdapter.mGroupPositionSelected = itemAndGuestRelatedTemp.getGroupPosition();

		guestListView.setSelectedChild(itemAndGuestRelatedTemp.getGroupPosition(),
				itemAndGuestRelatedTemp.getChildPosition(), 
				true);
		orderRelatedAdapter.notifyDataSetChanged();
	}

	private void setQuantityForModifiers() {
		if(modifiersSelected != null) {
			for(ModifierMaster modifierMaster :  modifiersSelected) {
				modifierMaster.setQuantity(1);
			}
		}
	}

	private void addItemToListWithModifiers() {
		List<OrderedItem> orderedItemsInner = itemAndGuestRelatedTemp.getOrderedItems();
		OrderedItem orderedItemInner = itemAndGuestRelatedTemp.getOrderedItem();
		ItemMaster item = itemAndGuestRelatedTemp.getItem();


		// check for another item with the same contents
		// if present update else create new item
		// changes as on 23rd July 2013
		OrderedItem orderedItemTemp = null;
		Collection<OrderedItem> collection = Collections2.filter(orderedItemsInner, 
				new SearchForItemAdded(item.getItemId()));
		// Get a list of items with the item id

		Log.i(TAG, "value of item id: " + item.getItemId());
		List<OrderedItem> lstOfOrderedItemsFromCollection;
		if(collection != null) {
			lstOfOrderedItemsFromCollection = new ArrayList<OrderedItem>(collection);
		}else {
			lstOfOrderedItemsFromCollection = null;
		}

		if(lstOfOrderedItemsFromCollection != null) {
			for(OrderedItem oObj : lstOfOrderedItemsFromCollection) {
				// not a fractional dish
				if(oObj != null) {
					double qty = oObj.getQuantity();
					int intQty = (int)qty;

					double diff = qty - intQty;
					// it is an integer qty
					// it doesnt have modifiers with it

					//orderedItemInner.setEditable(true);
					// get an ordered item that is editable
					if(diff <= 0 && oObj.isEditable() || (oObj.getOrderedItemStatus() == 0 && 
							oObj.isEditable() == false) && (oObj.getModifiers() == null)) {						
						orderedItemsInner.remove(oObj);


						Order orderDummy = null;
						Guest guestDummy = null;
						List<OrderedItem> orderedItemsDummy = null;

						if(!isOrderSentToKitchen()) {
							// changes as on 7th sept 2013
							orderDummy = (Order)orderDisplayEditObj;
							guestDummy = orderDummy.getGuests().get(itemAndGuestRelatedTemp.getGroupPosition());
							orderedItemsDummy = guestDummy.getOrderedItems();
							orderedItemsDummy.remove(oObj);
						}

						double priceWithModifiers = oObj.getPriceWithModifiers();
						oObj.setPriceWithModifiers(priceWithModifiers + oObj.getPrice()); 

						orderedItemsInner.add(oObj);
						if(!isOrderSentToKitchen()) {
							orderedItemsDummy.add(oObj);

							guestDummy.setOrderedItems(orderedItemsDummy);
							orderDummy.getGuests().set(itemAndGuestRelatedTemp.getGroupPosition(), guestDummy);
							orderDisplayEditObj = orderDummy;
							// changes end here
						}

						if(oObj.getQuantity() > 1) {
							btnMinusFooter.setEnabled(true);
							btnMinusFooter.setTextColor(getResources().getColor(R.color.order_footer_textcolor));
						}
						orderedItemTemp = oObj;
						break;
					}else {
						continue;
					}
				}
				// this is incase out of all the items identified, 
				// none of them has an integer quantity
				// then a new order item is generated
				if(orderedItemTemp == null) {
					OrderedItem orderedItemNew = new OrderedItem();
					orderedItemNew.setComment(orderedItemInner.getComment());
					orderedItemNew.setEditable(true);
					orderedItemNew.setId(orderedItemInner.getId());
					orderedItemNew.setModifiers(orderedItemInner.getModifiers());
					orderedItemNew.setName(orderedItemInner.getName());
					orderedItemNew.setOrderedItemStatus(OrderedItemStatus.ADDED.ordinal());
					orderedItemNew.setPrice(orderedItemInner.getPrice());

					double modifiersCost = mMenuManager.getModifiersCost(orderedItemInner.getModifiers());
					if(modifiersCost == 0.0) {
						orderedItemNew.setPriceWithModifiers(orderedItemInner.getPrice());
					}else {
						orderedItemNew.setPriceWithModifiers((modifiersCost + orderedItemInner.getPrice()));
					}
					orderedItemNew.setQuantity(1.0);
					orderedItemTemp = orderedItemNew;
					orderedItemsInner.add(orderedItemsInner.size(),orderedItemNew);

					if(!isOrderSentToKitchen()) {
						// changes as on 7th sept 2013
						Order orderDummy = (Order)orderDisplayEditObj;
						Guest guestDummy = orderDummy.getGuests().get(itemAndGuestRelatedTemp.getGroupPosition());
						List<OrderedItem> orderedItemsDummy = guestDummy.getOrderedItems();
						orderedItemsDummy.add(orderedItemsDummy.size(), orderedItemNew);

						guestDummy.setOrderedItems(orderedItemsDummy);
						orderDummy.getGuests().set(itemAndGuestRelatedTemp.getGroupPosition(), guestDummy);
						orderDisplayEditObj = orderDummy;
						// changes end here
					}
				}
			}
			// Incase there are no items in the list
			if(orderedItemTemp == null) {
				OrderedItem orderedItemNew = new OrderedItem();
				orderedItemNew.setComment(orderedItemInner.getComment());
				orderedItemNew.setEditable(true);
				orderedItemNew.setId(orderedItemInner.getId());
				orderedItemNew.setModifiers(orderedItemInner.getModifiers());
				orderedItemNew.setName(orderedItemInner.getName());
				orderedItemNew.setOrderedItemStatus(OrderedItemStatus.ADDED.ordinal());
				orderedItemNew.setPrice(orderedItemInner.getPrice());

				double modifiersCost = mMenuManager.getModifiersCost(orderedItemInner.getModifiers());
				if(modifiersCost == 0.0) {
					orderedItemNew.setPriceWithModifiers(orderedItemInner.getPrice());
				}else {
					orderedItemNew.setPriceWithModifiers((modifiersCost + orderedItemInner.getPrice()));
				}
				orderedItemNew.setQuantity(1.0);
				orderedItemTemp = orderedItemNew;
				orderedItemsInner.add(orderedItemsInner.size(),orderedItemNew);

				if(!isOrderSentToKitchen()) {
					// changes as on 7th sept 2013
					Order orderDummy = (Order)orderDisplayEditObj;
					Guest guestDummy = orderDummy.getGuests().get(itemAndGuestRelatedTemp.getGroupPosition());
					List<OrderedItem> orderedItemsDummy = guestDummy.getOrderedItems();
					orderedItemsDummy.add(orderedItemsDummy.size(), orderedItemNew);

					guestDummy.setOrderedItems(orderedItemsDummy);
					orderDummy.getGuests().set(itemAndGuestRelatedTemp.getGroupPosition(), guestDummy);
					orderDisplayEditObj = orderDummy;
					// changes end here
				}
			}
		}

		Log.i(TAG,"inside the false loop");


		if(mChildSelected) {
			btnMinusFooter.setEnabled(false);
			btnMinusFooter.setTextColor(getResources().getColor(android.R.color.darker_gray));
		}

		itemAndGuestRelatedTemp.getGuest().setOrderedItems(orderedItemsInner);
		itemAndGuestRelatedTemp.getGuests().set(itemAndGuestRelatedTemp.getGroupPosition(), 
				itemAndGuestRelatedTemp.getGuest());
		orderToDisplay.setGuests(itemAndGuestRelatedTemp.getGuests());

		OrderRelatedAdapter.mGroupPositionSelected = itemAndGuestRelatedTemp.getGroupPosition();
		OrderRelatedAdapter.mChildPositionSelected = itemAndGuestRelatedTemp.getChildPosition();

		refreshList(orderToDisplay, Global.ORDER_FROM_ADAPTER,Global.ORDER_ACTION_DELETE);
	}

	/**
	 * Updation of an already added item
	 * @param remainingDishes
	 */
	private void addOrderedItemsToList(double remainingDishes) {
		OrderedItem orderedItem = null;
		getCollection(itemAndGuestRelatedTemp.getItem());
		if(lstOfOrderedItemsForSliderWindow != null) {
			if(mKitchenNote != null && 
					mKitchenNote.trim().length() > 0) {
				itemAndGuestRelatedTemp.getOrderedItem().setComment(mKitchenNote);
			}else {
				// set the kitchen note to null in-case its updated
				itemAndGuestRelatedTemp.getOrderedItem().setComment(null);
			}

			// check even for float values
			if((remainingDishes - (int)remainingDishes <= 0) || ((int)remainingDishes) <= 0) {
				orderedItem = itemAndGuestRelatedTemp.getOrderedItem();

				if(!isOrderSentToKitchen()) {
					OrderedItem orderedItemCopy = (OrderedItem)Utils.copy(orderedItem);
					// changes as on 7th sept 2013
					Order orderDummy = (Order)orderDisplayEditObj;
					Guest guestDummy = orderDummy.getGuests().get(itemAndGuestRelatedTemp.getGroupPosition());
					List<OrderedItem> orderedItemsDummy = guestDummy.getOrderedItems();

					for(int i=0;i<orderedItemsDummy.size();i++) {
						OrderedItem oItemInner = orderedItemsDummy.get(i);
						if(OrderManager.getInstance().compareOrderedItems(oItemInner, orderedItemCopy)) {
							orderedItemCopy.setQuantity(remainingDishes);
							orderedItemsDummy.set(i, orderedItemCopy);
							break;
						}else {
							continue;
						}
					}

					guestDummy.setOrderedItems(orderedItemsDummy);
					orderDummy.getGuests().set(itemAndGuestRelatedTemp.getGroupPosition(), guestDummy);
					orderDisplayEditObj = orderDummy;
					// changes end here
				}
				itemAndGuestRelatedTemp.getOrderedItem().setQuantity(remainingDishes);
				itemAndGuestRelatedTemp.getOrderedItems().set(itemAndGuestRelatedTemp.getChildPosition(),
						itemAndGuestRelatedTemp.getOrderedItem());
			}
			
			// Incase there are no items in the list
			if(orderedItem == null) {
				orderedItem = createNewOrderedItem(itemAndGuestRelatedTemp.getItem(),remainingDishes);
				orderedItem.setModifiers(null);
				itemAndGuestRelatedTemp.getOrderedItems().add(orderedItem);
				Log.i(TAG, "Incase there are no items in the list  ");

				if(!isOrderSentToKitchen()) {
					// changes as on 7th sept 2013
					Order orderDummy = (Order)orderDisplayEditObj;
					Guest guestDummy = orderDummy.getGuests().get(itemAndGuestRelatedTemp.getGroupPosition());
					List<OrderedItem> orderedItemsDummy = guestDummy.getOrderedItems();
					orderedItemsDummy.add(orderedItem);
					guestDummy.setOrderedItems(orderedItemsDummy);
					orderDummy.getGuests().set(itemAndGuestRelatedTemp.getGroupPosition(), guestDummy);
					orderDisplayEditObj = orderDummy;
					// changes end here
				}
			}
		}else {
			// if the list is null then in that case 
			// its a complete new item order
			orderedItem = createNewOrderedItem(itemAndGuestRelatedTemp.getItem(),remainingDishes);
			Log.i(TAG, "added to the list  ");
			itemAndGuestRelatedTemp.getOrderedItems().add(orderedItem);

			if(!isOrderSentToKitchen()) {
				// changes as on 7th sept 2013
				Order orderDummy = (Order)orderDisplayEditObj;
				Guest guestDummy = orderDummy.getGuests().get(itemAndGuestRelatedTemp.getGroupPosition());
				List<OrderedItem> orderedItemsDummy = guestDummy.getOrderedItems();
				orderedItemsDummy.add(orderedItem);
				guestDummy.setOrderedItems(orderedItemsDummy);
				orderDummy.getGuests().set(itemAndGuestRelatedTemp.getGroupPosition(), guestDummy);
				orderDisplayEditObj = orderDummy;
				// changes end here
			}
		}
	}

	/**
	 * Sets the text on the labels
	 */
	private void setGuiLabels() {

		// changes as on 16th July 2013
		mBtnPlaceOrderLeftMenu.setText(mLanguageManager.getPlaceOrder());
		mTxtLeftMenuNotifications.setText(mLanguageManager.getNotifications());
		mTxtLeftMenuOrders.setText(mLanguageManager.getOrders());
		mTxtLeftMenuSettings.setText(mLanguageManager.getSettings());
		mTxtLeftMenuTables.setText(mLanguageManager.getTables());
		btnSendOrderFooter.setText(mLanguageManager.getSendOrder());
		mBtnLockLeftMenu.setText(mLanguageManager.getLock());
		mBtnWaiterChangeLeftMenu.setText(mLanguageManager.getChangeWaiter());

		if(btnAddNewGuestFooter != null) {
			btnAddNewGuestFooter.setText(mLanguageManager.getAddNewGuest());
		}

		if(mEditSearch != null) {
			mEditSearch.setHint(mLanguageManager.getSearchItem());
			mEditSearch.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		}

		txtHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mBtnPlaceOrderLeftMenu.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtLeftMenuNotifications.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtLeftMenuOrders.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtLeftMenuSettings.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtLeftMenuTables.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mBtnLockLeftMenu.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mBtnWaiterChangeLeftMenu.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		btnSendOrderFooter.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		mTxtWaiterNameLeftMenu.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());

		setButtons();
	}

	/**
	 * 
	 * Enables and disables the buttons
	 */
	private void setButtons() {
		if(orderToDisplay != null) {
			if(orderToDisplay.getOrderStatus() == null || 
					(orderToDisplay.getOrderStatus() != null && 
					orderToDisplay.getOrderStatus().ordinal() != 2 )){
				btnQuantityFractionFooter.setEnabled(false);
				btnMinusFooter.setEnabled(false);
				btnAddFooter.setEnabled(false);

				btnAddFooter.setTextColor(getResources().getColor(android.R.color.darker_gray));
				btnQuantityFractionFooter.setTextColor(getResources().getColor(android.R.color.darker_gray));
				btnMinusFooter.setTextColor(getResources().getColor(android.R.color.darker_gray));
			}else if(orderToDisplay.getOrderId() != null && 
					orderToDisplay.getOrderStatus() != null && 
					orderToDisplay.getOrderStatus().ordinal() == 2){

				mRelOrderRelatedFooter.setVisibility(View.GONE);
				mBtnForceSync.setVisibility(View.GONE);
				imageLogo.setVisibility(View.GONE);
			}

			if(orderToDisplay.getOrderId() != null && 
					orderToDisplay.getOrderStatus() != null && 
					orderToDisplay.getOrderStatus().ordinal() != 2 ) {
				String text = "";
				if(isOrderSentToKitchen()) {
					// The order has been sent to the kitchen
					// check if the order has items really
					if(OrderManager.getInstance().getCountOfItems(orderToDisplay) > 0) {
						if(OrderManager.getInstance().hasEditableItems(orderToDisplay)) {
							text = mLanguageManager.getSendOrder();
						}else {
							text = mLanguageManager.getCheckout();
						}
					}else {
						text = mLanguageManager.getSendOrder();
					}
				}else {
					orderDisplayEditObj = Utils.copy((Object)orderToDisplay);
					text = mLanguageManager.getSendOrder();
				}
				btnSendOrderFooter.setText(text);

				mBtnForceSync.setVisibility(View.VISIBLE);
				imageLogo.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * Asks the user if he/she wishes to save the order
	 * called when the user presses home, or when the user
	 * navigates to the previous screen by pressing back
	 */
	private void saveOrder(final String dialogFrom) {
		if(orderRelatedAdapter != null) {
			OrderRelatedAdapter.isNewlyAdded = false;
			OrderRelatedAdapter.mChildPositionSelected = -1;
			OrderRelatedAdapter.mGroupPositionSelected = -1;
		}

		// ask the user for save
		AlertDialog.Builder builder = new AlertDialog.Builder(OrderRelatedActivity.this);

		String title= mLanguageManager.getOrderLossMessage();

		builder.setTitle(title);
		builder.setCancelable(false);

		String yes = mLanguageManager.getOk();
		String no = mLanguageManager.getCancel();

		builder.setPositiveButton(yes, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				isSave = true;
				dialog.dismiss();

				if(dialogFrom.equalsIgnoreCase(Global.BACK)) {
					navigation();
				}else if(dialogFrom.equalsIgnoreCase(Global.HOME)) {
					finish();
					Intent intent = new Intent(OrderRelatedActivity.this, MyOrderActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | 
							Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(intent);
					//activity out animation
					Global.activityStartAnimationRightToLeft(OrderRelatedActivity.this);
				}else if(dialogFrom.equalsIgnoreCase(Global.PLACE_NEW_ORDER) || 
						dialogFrom.equalsIgnoreCase(Global.TABLE_LIST)) {
					Intent intent = new Intent(OrderRelatedActivity.this, TableListActivity.class);
					startActivity(intent);
					finish();
					//activity out animation
					Global.activityStartAnimationRightToLeft(OrderRelatedActivity.this);
				}
			}
		});

		builder.setNegativeButton(no, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				isSave = false;
				dialog.dismiss();
			}
		});

		if(orderToDisplay != null && (orderToDisplay.getOrderId() == null || 
				orderToDisplay.isModified())) {
			builder.show();
		}else {
			if(dialogFrom.equalsIgnoreCase(Global.BACK)) {
				navigation();
			}else if(dialogFrom.equalsIgnoreCase(Global.HOME)) {
				finish();
				Intent intent = new Intent(OrderRelatedActivity.this, MyOrderActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | 
						Intent.FLAG_ACTIVITY_CLEAR_TASK );
				startActivity(intent);

				//activity out animation
				Global.activityStartAnimationRightToLeft(OrderRelatedActivity.this);
			}else if(dialogFrom.equalsIgnoreCase(Global.PLACE_NEW_ORDER) || 
					dialogFrom.equalsIgnoreCase(Global.TABLE_LIST)) {
				Intent intent = new Intent(OrderRelatedActivity.this, TableListActivity.class);
				startActivity(intent);
				finish();
				//activity out animation
				Global.activityStartAnimationRightToLeft(OrderRelatedActivity.this);
			}
		}
	}

	private void navigation() {
		if(mFrom != null && mFrom.equalsIgnoreCase(Global.FROM_TABLE_LIST)) {
			Intent intent = new Intent(OrderRelatedActivity.this, TableListActivity.class);	
			startActivity(intent);
			finish();


			//activity out animation
			Global.activityFinishAnimationLeftToRight(OrderRelatedActivity.this);
		}else if(mFrom!= null && mFrom.equalsIgnoreCase(Global.FROM_TABLE_ORDER_LIST)){
			Intent intent = new Intent(OrderRelatedActivity.this, TableOrderListActivity.class);
			intent.putExtra(Global.TABLE_ID,tableId);
			intent.putExtra(Global.TABLE_NUMBER, tableNumber);
			intent.putExtra(Global.FROM_ACTIVITY, Global.FROM_TABLE_LIST);
			startActivity(intent);
			finish();


			//activity out animation
			Global.activityFinishAnimationLeftToRight(OrderRelatedActivity.this);
		}else if(mFrom != null && mFrom.equalsIgnoreCase(Global.FROM_HOME_ACTIVITY)) {
			finish();
			Intent intent = new Intent(OrderRelatedActivity.this, MyOrderActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | 
					Intent.FLAG_ACTIVITY_CLEAR_TASK );
			startActivity(intent);
			//activity out animation
			Global.activityFinishAnimationLeftToRight(OrderRelatedActivity.this);
		}else if(mFrom != null && mFrom.equalsIgnoreCase(Global.FROM_NOTIFICATIONS)) {
			finish();
			Intent intent = new Intent(OrderRelatedActivity.this, NotificationListActivity.class);
			startActivity(intent);
			//activity out animation
			Global.activityFinishAnimationLeftToRight(OrderRelatedActivity.this);
		}
	}

	private void expandList() {
		if(orderToDisplay != null) {
			if(orderToDisplay.getGuests() != null) {
				for(int i=0;i<orderToDisplay.getGuests().size();i++) {
					guestListView.expandGroup(i);
				}

				if(guestSelected != null && guestSelected.trim().length() > 0) {
					guestListView.setSelection(Integer.parseInt(guestSelected));
				}else {
					guestSelected = "0";
				}

				// set itemAndGuestTempObj
				if(itemAndGuestRelatedTemp == null) {
					itemAndGuestRelatedTemp  = new ItemAndGuestRelatedTemp();
				}

				mGroupSelected = true;
				OrderRelatedAdapter.mGroupPositionSelected = Integer.parseInt(guestSelected);
				itemAndGuestRelatedTemp.setGroupPosition(Integer.parseInt(guestSelected));
				itemAndGuestRelatedTemp.setGuests(orderToDisplay.getGuests());
				itemAndGuestRelatedTemp.setGuest(orderToDisplay.getGuests().get(Integer.parseInt(guestSelected)));

				guestListView.requestFocusFromTouch();
				guestListView.setSelected(true);
				guestListView.setSelectedGroup(Integer.parseInt(guestSelected));
			}
		}
	}

	public void changeGroupStatus(boolean isExpanded, int groupPosition) {
		Log.i(TAG, "value of isExpanded " + isExpanded);
		if(isExpanded) {
			Log.i(TAG, "group collapsed");
			guestListView.collapseGroup(groupPosition);
			//relativeButtons.setVisibility(View.GONE);
		}else {
			Log.i(TAG, "group expanded");
			guestListView.expandGroup(groupPosition);
		}
	}


	public void performOperations(boolean isExpanded, int groupPosition, boolean isSelected) {
		if(isExpanded) {
			if(!isSelected) {
				mGroupSelected = false;
			}else {
				if(orderToDisplay.getOrderStatus() != null && 
						orderToDisplay.getOrderStatus().ordinal() == 2) {
					// hide the footer view..
				}else {
					mGroupSelected = true;
					mChildPositionSelected = -1;
					btnQuantityFractionFooter.setEnabled(false);
					btnMinusFooter.setEnabled(false);
					btnAddFooter.setEnabled(false);

					btnAddFooter.setTextColor(getResources().getColor(android.R.color.darker_gray));
					btnQuantityFractionFooter.setTextColor(getResources().getColor(android.R.color.darker_gray));
					btnMinusFooter.setTextColor(getResources().getColor(android.R.color.darker_gray));

					itemAndGuestRelatedTemp.setGroupPosition(groupPosition);
					itemAndGuestRelatedTemp.setGuests(orderToDisplay.getGuests());
					itemAndGuestRelatedTemp.setGuest(orderToDisplay.getGuests().get(groupPosition));

					guestSelected = String.valueOf(groupPosition);

					OrderRelatedAdapter.mChildPositionSelected = 0;
					OrderRelatedAdapter.mGroupPositionSelected = groupPosition;

					guestListView.requestFocusFromTouch();
					guestListView.setSelected(true);
					guestListView.setSelectedGroup(groupPosition);
				}
			}
		}
		orderRelatedAdapter.notifyDataSetChanged();
	}

	private void askForBillSplit() {
		if(guestCounter == 2 && 
				billSplitChecked.trim().length() <= 0 && 
				orderId == null) {

			AlertDialog.Builder builder = new AlertDialog.Builder(OrderRelatedActivity.this);

			String noForDialogButton = mLanguageManager.getNo();
			String yesForDialogButton = mLanguageManager.getYes();
			String messageForDialog = mLanguageManager.getBillSplitMessage();

			builder.setMessage(messageForDialog);
			builder.setNegativeButton(noForDialogButton, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					orderToDisplay.setBillSplit(false);

					Prefs.addKey(OrderRelatedActivity.this, Prefs.IS_BILL_SPLIT, false);
					Prefs.addKey(OrderRelatedActivity.this, Prefs.BILL_SPLIT_CHECKED, Global.YES);

					dialog.dismiss();
				}
			});

			builder.setPositiveButton(yesForDialogButton, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					orderToDisplay.setBillSplit(true);
					Prefs.addKey(OrderRelatedActivity.this, Prefs.IS_BILL_SPLIT, true);
					Prefs.addKey(OrderRelatedActivity.this, Prefs.BILL_SPLIT_CHECKED, Global.YES);
					dialog.dismiss();
				}
			});

			builder.show();
		}
	}

	// Add item fragment related code will come here
	// so as to add the item directly under the guest selected
	// and into the expandable listview directly

	// changes as on 13th July 2013
	// changes due to design changes
	private void inflateListAndAlsoSpinner() {
		lstCategoryWithSelect = new ArrayList<CategoryMaster>();
		//categoryHashMap = new HashMap<String, List<Category>>();

		if(guestSelected == null || (guestSelected != null && guestSelected.trim().length() <= 0)) {
			guestSelected = String.valueOf((guestCounter - 1));
			Log.i(TAG, " value of guestSelected " + guestSelected);
		}

		// get the items of the guest
		Guest guest =orderToDisplay.getGuests().get(Integer.valueOf(guestSelected));
		if(guest.getOrderedItems() != null) {
			orderedItemsForSliderWindow = orderToDisplay.getGuests().get(Integer.valueOf(guestSelected)).getOrderedItems();

			if(!isOrderSentToKitchen()) {
				Order orderDummy = (Order)orderDisplayEditObj;
				orderedItemsForSliderWindowCopy = (List<OrderedItem>)Utils.copy(orderDummy.getGuests().get(Integer.valueOf(guestSelected)).getOrderedItems());
			}

		}else {
			orderedItemsForSliderWindow = new ArrayList<OrderedItem>();

			if(!isOrderSentToKitchen()) {
				Order orderDummy = (Order)orderDisplayEditObj;
				orderedItemsForSliderWindowCopy = (List<OrderedItem>)Utils.copy(orderDummy.getGuests().get(Integer.valueOf(guestSelected)).getOrderedItems());

				if(orderedItemsForSliderWindowCopy == null) {
					orderedItemsForSliderWindowCopy = new ArrayList<OrderedItem>();
				}
			}
		}


		// add the category list to the list with the select
		lstCategoryWithSelect.add(createCategoryWithJustSelect());

		if(categories != null) {
			lstCategoryWithSelect.addAll(categories);
		}


		cursorWithAllItemsPerCategory = MenuManager.getInstance().getCategoriesForList(sectionId);
		fillListWithData(cursorWithAllItemsPerCategory);

		// first level will not have any parent id
		// so null, and place the entire categorial list into the map
		//categoryHashMap.put(null, categoryList);

		spinnerCategoryList.setVisibility(View.GONE);

		if(isMenuOrganized) {
			btnBack.setVisibility(View.VISIBLE);
		}else {
			btnBack.setVisibility(View.GONE);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		switch (parent.getId()) {
		case R.id.spinnerCategoryList:
			if(view != null) {
				((TextView)view).setText(null); 

				CategoryListAdapter categoryListAdapter = (CategoryListAdapter)spinnerCategoryList.getAdapter();
				Cursor c = categoryListAdapter.getCursor();

				CategoryMaster category = null;
				if(c != null) {
					c.moveToPosition(position);
					category = new CategoryMaster();
					category.setCategoryId(c.getString(c.getColumnIndex(DBHelper.CATEGORY_ID)));
					category.setCategoryName(c.getString(c.getColumnIndex(DBHelper.CATEGORY_NAME)));
					category.setCategoryParentId(c.getString(c.getColumnIndex(DBHelper.CATEGORY_PARENT_ID)));
				}


				//cate = lstCategoryWithSelect.get(position);

				mMainCategorySelected = category.getCategoryName();
				mCurrentSelection = category.getCategoryId();
				mCurrentSelectionString = category.getCategoryName();

				String strSelect = mLanguageManager.getSelectCategory();

				if(!mMainCategorySelected.toLowerCase().contains(strSelect.toLowerCase())) {
					//txtHeader.setText(category.getCategoryName());

					if(!isMenuOrganized) {
						// if the menu is not organized then
						// just display the items under the category

						cursorWithAllItemsPerCategory = MenuManager.getInstance().getSubcategoriesAndItems(category.getCategoryId(),
								sectionId);
						fillListWithData(cursorWithAllItemsPerCategory);

						Log.i("dhara"," userSelectionPathIds : spinner  " + userSelectionPathIds);

						if(userSelectionPathIds.size() <= 0){
							userSelectionPathIds.add("No Id Here");

							btnBack.setEnabled(true);
							btnBack.setBackgroundResource(R.drawable.selector_up_arrow);
						}else {
							if(!btnBack.isEnabled()) {
								btnBack.setEnabled(true);
								btnBack.setBackgroundResource(R.drawable.selector_up_arrow);
							}
						}
					}
				}else {
					// if it is a select then
					// display all the items of the categories
					cursorWithAllItems = MenuManager.getInstance().getAllItemFromDB(
							sectionId);
					userSelectionPathIds.add("No id here");
					fillListWithData(cursorWithAllItems);
				}
			}
			break;

		case R.id.spinnerSubCategoryList:

			break;

		default:
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	/**
	 * OnItemClick:
	 * Click for listview items present in the sliding layer
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, 
			int position, long id) {

		Log.i(TAG, "value of position : " + position);

		// changes will occur here
		// 31st july 2013
		// get the item from the cursor
		ItemMaster item = new ItemMaster();
		ItemAdapter itemAdapter = (ItemAdapter)itemListView.getAdapter();
		Cursor cursor = itemAdapter.getCursor();

		if(cursor != null && cursor.getCount() > 0) {
			String itemName = cursor.getString(cursor.getColumnIndex(DBHelper.ITEM_NAME));
			String categoryId = cursor.getString(cursor.getColumnIndex(DBHelper.CATEGORY_ID));

			if(itemName != null && itemName.trim().length() >0) {

				cursor.moveToPosition(position);

				int isActivated = cursor.getInt(cursor.getColumnIndex(DBHelper.ITEM_IS_ACTIVATED)); 
				int isRestricted = cursor.getInt(cursor.getColumnIndex(DBHelper.ITEM_IS_RESTRICTED)); 
				int hasModifiers = cursor.getInt(cursor.getColumnIndex(DBHelper.HAVE_MODIFIERS));

				switch (isActivated) {
				case 0:
					item.setActivated(false);
					break;

				case 1:
					item.setActivated(true);
					break;

				default:
					break;

				}

				switch (isRestricted) {
				case 0:
					item.setRestricted(false);
					break;

				case 1:
					item.setRestricted(true);
					break;

				default:
					break;	

				}

				switch (hasModifiers) {
				case 0:
					item.setHasModifiers(false);
					break;

				case 1:
					item.setHasModifiers(true);
					break;

				default:
					break;	

				}

				// get the values of the item here
				item.setItemCode(cursor.getString(cursor.getColumnIndex(DBHelper.ITEM_CODE)));
				item.setItemDescription(cursor.getString(cursor.getColumnIndex(DBHelper.ITEM_DESC)));
				item.setFullName(cursor.getString(cursor.getColumnIndex(DBHelper.ITEM_FULLNAME)));
				item.setItemId(cursor.getString(cursor.getColumnIndex(DBHelper.ITEM_ID)));
				item.setItemName(itemName);
				item.setPrice(cursor.getDouble(cursor.getColumnIndex(DBHelper.ITEM_PRICE)));
				item.setCategoryId(cursor.getString(cursor.getColumnIndex(DBHelper.CATEGORY_ID)));
				item.setSectionId(sectionId);

				Log.i(TAG, "value of item clicked on : " + item.getItemName());

				// tapping on the item will open a dialog box
				if(orderToDisplay != null &&
						(orderToDisplay.getOrderStatus() == null || 
						(orderToDisplay.getOrderStatus() != null && 
						orderToDisplay.getOrderStatus().ordinal() != 2))) {

					// Add item to the list of ordered items or 
					// increase the value of the item already present

					// get the items of the guest
					Guest guest =orderToDisplay.getGuests().get(Integer.valueOf(guestSelected));
					if(guest.getOrderedItems() != null) {
						orderedItemsForSliderWindow = orderToDisplay.getGuests().get(Integer.valueOf(guestSelected)).getOrderedItems();

						if(!isOrderSentToKitchen()) {
							Order orderDummy = (Order)orderDisplayEditObj;
							orderedItemsForSliderWindowCopy = (List<OrderedItem>)Utils.copy(orderDummy.getGuests().get(Integer.valueOf(guestSelected)).getOrderedItems());
						}

					}else {
						orderedItemsForSliderWindow = new ArrayList<OrderedItem>();

						if(!isOrderSentToKitchen()) {
							Order orderDummy = (Order)orderDisplayEditObj;
							orderedItemsForSliderWindowCopy = (List<OrderedItem>)Utils.copy(orderDummy.getGuests().get(Integer.valueOf(guestSelected)).getOrderedItems());

							if(orderedItemsForSliderWindowCopy == null) {
								orderedItemsForSliderWindowCopy = (List<OrderedItem>)Utils.copy(orderDummy.getGuests().get(Integer.valueOf(guestSelected)).getOrderedItems());
							}
						}
					}

					if(item != null && (item.isActivated() && !item.isRestricted())) {
						if(editKitchenNote != null) {
							editKitchenNote.setText("");
						}

						addItemToOrder(item,"");
						if(item.isHasModifiers()) {
							clickedModifiersButton = false;
							itemAndGuestRelatedTemp = new ItemAndGuestRelatedTemp();
							itemAndGuestRelatedTemp.setGroupPosition(Integer.valueOf(guestSelected));
							itemAndGuestRelatedTemp.setChildPosition(guest.getOrderedItems().size()-1);
							itemAndGuestRelatedTemp.setGuests(guests);
							itemAndGuestRelatedTemp.setGuest(guest);
							itemAndGuestRelatedTemp.setOrderedItems(guest.getOrderedItems());
							itemAndGuestRelatedTemp.setOrderedItem(guest.getOrderedItems().get(guest.getOrderedItems().size()-1));
							itemAndGuestRelatedTemp.setItem(item);

							modifiersSelectedCopy = new ArrayList<ModifierMaster>();

							if(guest.getOrderedItems().get(guest.getOrderedItems().size()-1).getModifiers() != null) {
								modifiersSelectedCopy.addAll(guest.getOrderedItems().get(guest.getOrderedItems().size()-1).getModifiers());
							}


							if(itemAndGuestRelatedTemp != null) {
								Log.i(TAG,"inside button click ");
								showModifiersBox();
							}
						}
					}else {
						if(orderRelatedAdapter != null) {
							OrderRelatedAdapter.isNewlyAdded = false;
						}
					}
				}

			}else {
				// changes as on 7th November 2013
				// change the subcategories and items displayed, and store the values 
				// of the category previously selected for back tracking
				if(!btnBack.isEnabled()) {
					btnBack.setEnabled(true);
					btnBack.setBackgroundResource(R.drawable.selector_up_arrow);
				}

				String parentId = cursor.getString(cursor.getColumnIndex(DBHelper.CATEGORY_PARENT_ID));
				userSelectionPathIds.add(parentId);
				cursorWithAllItemsPerCategory = MenuManager.getInstance().getSubcategoriesAndItems(categoryId, sectionId);
				fillListWithData(cursorWithAllItemsPerCategory);
			}
		}

	}

	public void updateModifiers(List<ModifierMaster> lstModifiers) {
		modifiers = lstModifiers;
	}

	/**
	 * Creates a new order to be added to the list
	 * @param item
	 * @return
	 */
	private OrderedItem addNewOrder(ItemMaster item, double qty) {
		Log.i(TAG, "add new order function called");
		OrderedItem orderedItem = new OrderedItem();
		orderedItem.setId(item.getItemId());
		orderedItem.setModifiers(null);
		orderedItem.setName(item.getItemName());
		orderedItem.setPrice(item.getPrice());
		orderedItem.setEditable(true);

		if(orderedItem.getModifiers() != null) {
			orderedItem.setPriceWithModifiers(item.getPrice() + mMenuManager.getModifiersCost(modifiers));
		}else {
			orderedItem.setPriceWithModifiers(item.getPrice());
		}

		orderedItem.setOrderedItemStatus(OrderedItemStatus.ADDED.ordinal());

		DecimalFormat formatter = new DecimalFormat("#.##");
		String strQty = formatter.format(qty);

		orderedItem.setQuantity(qty);

		// Changes as on 7th June 2013
		// Adding kitchen note comment
		if(mKitchenNote != null && 
				mKitchenNote.trim().length() > 0) {
			orderedItem.setComment(mKitchenNote);
		}

		return orderedItem;
	}

	// changes as on 1st August 2013

	private void fillListWithData(Cursor c) {
		mItemAdapter = new ItemAdapter(OrderRelatedActivity.this, c, false);
		itemListView.setAdapter(mItemAdapter);
		mItemAdapter.notifyDataSetChanged();

		TextView txtEmptyView = (TextView)findViewById(android.R.id.empty);

		if(c != null && c.getCount() > 0) {
			txtEmptyView.setVisibility(View.GONE);
		}else {
			txtEmptyView.setVisibility(View.VISIBLE);
			txtEmptyView.setText(mLanguageManager.getNoResults());
			txtEmptyView.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		}

		itemListView.setEmptyView(txtEmptyView);

		mItemAdapter.setFilterQueryProvider(new FilterQueryProvider() {

			@Override
			public Cursor runQuery(CharSequence constraint) {
				if(constraint.toString().trim().length() <= 0) {
					// changes as on 16th November 2013
					// commenting this line as there is no spinner now
					// on clear text move the user to the category listing

					final Cursor c = MenuManager.getInstance().getCategoriesForList(sectionId);
					cursorWithAllItemsPerCategory = c;

					OrderRelatedActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mItemAdapter.swapCursor(c);
							mItemAdapter.notifyDataSetChanged();
						}
					});

					return c;
					// changes end here
				}else {
					final Cursor c = MenuManager.getInstance().getItemDataSearchedFor(constraint.toString(), sectionId);

					cursorWithAllItemsPerCategory = c;

					OrderRelatedActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mItemAdapter.swapCursor(c);
							mItemAdapter.notifyDataSetChanged();
						}
					});

					return c;
				}
			}
		});
	}
	
	private void setButtonsWhenNoItemSelected() {
		mChildSelected = false;
		mChildPositionSelected = -1;

		btnAddFooter.setEnabled(false);
		btnMinusFooter.setEnabled(false);
		btnQuantityFractionFooter.setEnabled(false);

		btnAddFooter.setTextColor(getResources().getColor(android.R.color.darker_gray));
		btnMinusFooter.setTextColor(getResources().getColor(android.R.color.darker_gray));
		btnQuantityFractionFooter.setTextColor(getResources().getColor(android.R.color.darker_gray));
	}

	/**
	 * increases the quantity of the item to the list if present 
	 * or updates it 
	 * or adds a new item
	 * @param item
	 * @param remainingDishes
	 * @param from
	 * @return
	 */
	private boolean addOrderedItemsToList(ItemMaster item, int remainingDishes, String from) {
		//TODO: set mChildSelected to false
		// need to see what happens
		OrderedItem orderedItem = null;
		
		mGroupSelected = false;
		setButtonsWhenNoItemSelected();
		
		if(orderToDisplay.getOrderStatus() == null ||
				(orderToDisplay.getOrderStatus() != null && 
				orderToDisplay.getOrderStatus().ordinal() == 0)) {
			if(lstOfOrderedItemsForSliderWindow != null) {
				int index=0;
				for(OrderedItem oObj : lstOfOrderedItemsForSliderWindow) {
					// not a fractional dish
					if(oObj != null) {
						double qty = oObj.getQuantity();
						int intQty = (int)qty;

						double diff = qty - intQty;
						// it is an integer qty
						// it doesnt have modifiers with it
						if(diff <= 0 && 
								(oObj.isEditable() || (oObj.isEditable() == false && 
								oObj.getOrderedItemStatus() == 0)) &&
								(oObj.getModifiers() == null || 
								oObj.getModifiers().size() <= 0)) {

							if(!isOrderSentToKitchen()) {
								if(orderedItemsForSliderWindowCopy.size() > 0) {
									OrderedItem orderedItemCopy = (OrderedItem)Utils.copy(oObj);

									Iterator<OrderedItem> it = orderedItemsForSliderWindowCopy.iterator();
									while (it.hasNext()) {
										OrderedItem orderedItemObj = it.next();
										if (orderedItemObj.getId().equals(orderedItemCopy.getId()) && 
												orderedItemObj.isEditable() && 
												orderedItemObj.getQuantity() == orderedItemCopy.getQuantity()) {
											it.remove();
											Log.i("Dhara","remove it");
										}
									}
								}
							}

							if(from.equalsIgnoreCase("update")) {
								oObj.setQuantity(remainingDishes);
							}else {
								oObj.setQuantity(oObj.getQuantity() + remainingDishes);
							}

							orderedItemsForSliderWindow.remove(oObj);

							oObj.setModifiers(null);
							orderedItemsForSliderWindow.add(oObj);

							if(!isOrderSentToKitchen()) {
								if(orderedItemsForSliderWindowCopy!= null) {
									OrderedItem orderedItemCopy = (OrderedItem)Utils.copy(oObj);
									orderedItemsForSliderWindowCopy.add(orderedItemCopy);
								}else {
									orderedItemsForSliderWindowCopy = new ArrayList<OrderedItem>();
									OrderedItem orderedItemCopy = (OrderedItem)Utils.copy(orderedItem);
									orderedItemsForSliderWindowCopy.add(orderedItemCopy);
								}
							}

							orderedItem = oObj;


							if(!isOrderSentToKitchen()) {
								// changes as on 7th sept 2013
								Order orderDummy = (Order)orderDisplayEditObj;
								Guest guestDummy = orderDummy.getGuests().get(Integer.parseInt(guestSelected));

								List<OrderedItem> orderedItemsCopy = (List<OrderedItem>)Utils.copy(orderedItemsForSliderWindowCopy);
								guestDummy.setOrderedItems(orderedItemsCopy);
								orderDummy.getGuests().set(Integer.parseInt(guestSelected), guestDummy);
								orderDisplayEditObj = orderDummy;

								Log.i("Dhara"," items into the order : " + 
										orderDummy.getGuests().get(Integer.parseInt(guestSelected)).getOrderedItems().size());
								// changes end here
							}

							if(mChildSelected) {
								if(orderedItem.getQuantity() > 1) {
									btnMinusFooter.setEnabled(true);
									btnMinusFooter.setTextColor(getResources().getColor(R.color.order_footer_textcolor));
								}
							}

							break;
						}else {
							continue;
						}
					}
					// this is incase out of all the items identified, 
					// none of them has an integer quantity
					// then a new order item is generated
					if(orderedItem == null) {
						orderedItem = addNewOrder(item, remainingDishes);
						orderedItem.setModifiers(null);
						btnMinusFooter.setEnabled(false);
						btnMinusFooter.setTextColor(getResources().getColor(android.R.color.darker_gray));
						orderedItemsForSliderWindow.add(orderedItem);
						OrderRelatedAdapter.mGroupPositionSelected = Integer.parseInt(guestSelected);
						OrderRelatedAdapter.mChildPositionSelected = orderedItemsForSliderWindow.size() - 1;

						if(!isOrderSentToKitchen()) {
							// changes as on 7th sept 2013
							Order orderDummy = (Order)orderDisplayEditObj;
							Guest guestDummy = orderDummy.getGuests().get(Integer.parseInt(guestSelected));

							if(orderedItemsForSliderWindowCopy!=null) {
								OrderedItem orderedItemCopy = (OrderedItem)Utils.copy(orderedItem);
								orderedItemsForSliderWindowCopy.add(orderedItemCopy);
							}else {
								orderedItemsForSliderWindowCopy = new ArrayList<OrderedItem>();
								OrderedItem orderedItemCopy = (OrderedItem)Utils.copy(orderedItem);
								orderedItemsForSliderWindowCopy.add(orderedItemCopy);
							}

							List<OrderedItem> orderedItemsCopy = (List<OrderedItem>)Utils.copy(orderedItemsForSliderWindowCopy);
							guestDummy.setOrderedItems(orderedItemsCopy);
							orderDummy.getGuests().set(Integer.parseInt(guestSelected), guestDummy);
							orderDisplayEditObj = orderDummy;

							Log.i("Dhara"," items into the order : " + 
									(orderDummy.getGuests().get(Integer.parseInt(guestSelected)).getOrderedItems() != null ? 
											orderDummy.getGuests().get(Integer.parseInt(guestSelected)).getOrderedItems().size() : "null"));
							// changes end here
						}
					}

					index ++;
				}
				// Incase there are no items in the list
				if(orderedItem == null) {
					orderedItem = addNewOrder(item,remainingDishes);
					orderedItem.setModifiers(null);
					btnMinusFooter.setEnabled(false);

					btnMinusFooter.setTextColor(getResources().getColor(android.R.color.darker_gray));
					orderedItemsForSliderWindow.add(orderedItem);
					OrderRelatedAdapter.mGroupPositionSelected = Integer.parseInt(guestSelected);
					OrderRelatedAdapter.mChildPositionSelected = orderedItemsForSliderWindow.size() - 1;
					Log.i(TAG, "Incase there are no items in the list  ");

					if(!isOrderSentToKitchen()) {
						// changes as on 7th sept 2013
						Order orderDummy = (Order)orderDisplayEditObj;
						Guest guestDummy = orderDummy.getGuests().get(Integer.parseInt(guestSelected));

						if(orderedItemsForSliderWindowCopy!=null) {
							OrderedItem orderedItemCopy = (OrderedItem)Utils.copy(orderedItem);
							orderedItemsForSliderWindowCopy.add(orderedItemCopy);
						}else {
							orderedItemsForSliderWindowCopy = new ArrayList<OrderedItem>();
							OrderedItem orderedItemCopy = (OrderedItem)Utils.copy(orderedItem);
							orderedItemsForSliderWindowCopy.add(orderedItemCopy);
						}

						List<OrderedItem> orderedItemsCopy = (List<OrderedItem>)Utils.copy(orderedItemsForSliderWindowCopy);
						guestDummy.setOrderedItems(orderedItemsCopy);
						orderDummy.getGuests().set(Integer.parseInt(guestSelected), guestDummy);
						orderDisplayEditObj = orderDummy;

						Log.i("Dhara"," items into the order : " + 
								(orderDummy.getGuests().get(Integer.parseInt(guestSelected)).getOrderedItems() != null ?
										orderDummy.getGuests().get(Integer.parseInt(guestSelected)).getOrderedItems().size() :
										"null"));
						// changes end here
					}
				}
			}else {
				// if the list is null then in that case 
				// its a complete new item order
				orderedItem = addNewOrder(item,remainingDishes);
				OrderRelatedAdapter.mGroupPositionSelected = Integer.parseInt(guestSelected);
				orderedItemsForSliderWindow.add(orderedItem);
				OrderRelatedAdapter.mChildPositionSelected = orderedItemsForSliderWindow.size() - 1;
				btnMinusFooter.setEnabled(false);
				//btnMinusFooter.setBackgroundResource(R.drawable.minus_footer_grey);
				btnMinusFooter.setTextColor(getResources().getColor(android.R.color.darker_gray));
				Log.i(TAG, "added to the list  ");

				if(!isOrderSentToKitchen()) {
					// changes as on 7th sept 2013
					Order orderDummy = (Order)orderDisplayEditObj;
					Guest guestDummy = orderDummy.getGuests().get(Integer.parseInt(guestSelected));
					if(orderedItemsForSliderWindowCopy != null) {
						OrderedItem orderedItemCopy = (OrderedItem)Utils.copy(orderedItem);
						orderedItemsForSliderWindowCopy.add(orderedItemCopy);
					}

					List<OrderedItem> orderedItemsCopy = (List<OrderedItem>)Utils.copy(orderedItemsForSliderWindowCopy);
					guestDummy.setOrderedItems(orderedItemsCopy);
					orderDummy.getGuests().set(Integer.parseInt(guestSelected), guestDummy);
					orderDisplayEditObj = orderDummy;

					Log.i("Dhara"," items into the order : " + 
							(orderDummy.getGuests().get(Integer.parseInt(guestSelected)).getOrderedItems() != null ?
									orderDummy.getGuests().get(Integer.parseInt(guestSelected)).getOrderedItems().size() 
									: "null"));
					// changes end here
				}
			}
			return true;
		}
		return false;
	}


	/**
	 * Adds the item that was added from the list into the order
	 * @param item
	 * changes as on 13th July 2013
	 */
	public boolean addItemToOrder(ItemMaster item, String from) {
		getCollection(item);
		boolean flag = addOrderedItemsToList(item, 1, from);
		setIntoOrder();

		return flag;
	}

	/**
	 * Gets the collection  of items added in the ordereditems
	 * @param item
	 * @return
	 */
	public Collection<OrderedItem> getCollection(ItemMaster item) {
		Collection<OrderedItem> collection = Collections2.filter(orderedItemsForSliderWindow, 
				new SearchForItemAdded(item.getItemId()));
		// Get a list of items with the item id

		Log.i(TAG, "value of item id: " + item.getItemId());

		if(collection != null) {
			lstOfOrderedItemsForSliderWindow = new ArrayList<OrderedItem>(collection);
		}else {
			lstOfOrderedItemsForSliderWindow = null;
		}

		return collection;
	}

	/**
	 * Sets the ordered items into the order
	 * used by the function called from the adapter
	 * and also the dialog box
	 */
	private void setIntoOrder() {
		List<Guest> guests = orderToDisplay.getGuests();
		Log.i(TAG, "guest selected : " + guestSelected);
		Guest guest = guests.get(Integer.parseInt(guestSelected));
		guest.setOrderedItems(orderedItemsForSliderWindow);
		guests.set(Integer.parseInt(guestSelected),guest);
		orderToDisplay.setGuests(guests);

		OrderRelatedAdapter.mGroupPositionSelected = Integer.parseInt(guestSelected);
		OrderRelatedAdapter.mChildPositionSelected = orderToDisplay.getGuests().get(Integer.valueOf(guestSelected)).getOrderedItems().size() - 1;

		orderRelatedAdapter.notifyDataSetChanged();

		refreshList(orderToDisplay, Global.ORDER_FROM_ADAPTER, Global.ORDER_ACTION_ADDED_ITEM);

		guestListView.requestFocusFromTouch();
		guestListView.setSelected(true);
		guestListView.setSelection(Integer.parseInt(guestSelected));
		guestListView.setSelectedChild(Integer.parseInt(guestSelected),
				OrderRelatedAdapter.mChildPositionSelected , 
				true);
	}

	/**
	 * Returns a category instance that just has Select
	 * Added on 15th June 2013
	 * changes as on 13th July 2013
	 * @return
	 */
	private CategoryMaster createCategoryWithJustSelect() {
		CategoryMaster category =  new CategoryMaster();
		category.setCategoryName(mLanguageManager.getSelectCategory());
		category.setCategoryId(mLanguageManager.getSelectCategory());
		return category;
	}

	private void checkForOrderChange() {
		ordersToSend = new ArrayList<Order>();
		List<Guest> guestsPresent = orderToDisplay.getGuests();
		guestsToSend = new ArrayList<Guest>();
		orderToSend = new Order();

		for(Guest guest : guestsPresent) {
			if(orderToDisplay.isBillSplit()) {
				// if its a bill split order, 
				// each order will be treated as new
				orderToSend = new Order();
				guestsToSend = new ArrayList<Guest>();
			}

			List<OrderedItem> orderedItemsToSend = new ArrayList<OrderedItem>();
			if(guest.getGuestId() != null) {
				List<OrderedItem> orderedItems = guest.getOrderedItems();

				if(orderedItems != null) {
					// Get all the items that are newly added
					for(OrderedItem orderedItem : orderedItems) {
						if(orderedItem.isEditable()) {
							Log.i(TAG, "is item editable : " + orderedItem.isEditable());
							orderedItemsToSend.add(orderedItem);
						}
					}

					// if there are items added then set the guest.
					if(orderedItemsToSend.size() > 0) {
						Log.i(TAG, "ordered items to send if loop");
						Guest guestToAdd = new Guest();
						guestToAdd.setGuestId(guest.getGuestId());
						guestToAdd.setOrderedItems(orderedItemsToSend);
						guestsToSend.add(guestToAdd);
					}
				}
			}else {
				// if the guest is a new guest
				Log.i(TAG, "ordered items to send else loop");
				guestsToSend.add(guest);
			}

			if(orderToDisplay.isBillSplit()) {
				orderToSend.setMacAddress(Utils.getMacAddress());
				//orderToSend.setMacAddress("20");
				orderToSend.setWaiterCode(waiterCode);
				orderToSend.setTable(orderToDisplay.getTable());

				orderToSend.setOrderId(guest.getOrderId());
				orderToSend.setGuests(guestsToSend);
				ordersToSend.add(orderToSend);
			}
		}
	}

	/**
	 * Added as on 19th August 2013
	 * Checks if the order received or displayed 
	 * has been sent to the kitchen or not
	 * returns false if not sent
	 * return true if sent
	 * @return
	 */
	private boolean isOrderSentToKitchen() {
		Order orderFOrdeDummy = (Order)orderDisplayEditObj;
		if(orderFOrdeDummy != null) {
			List<Guest> guestsPresent = orderFOrdeDummy.getGuests();

			if(guestsPresent != null) {
				for(Guest guest : guestsPresent) {
					List<OrderedItem> orderedItems = guest.getOrderedItems();

					if(orderedItems != null) {
						for(OrderedItem orderedItem : orderedItems) {
							if(orderedItem.getOrderedItemStatus() == 0 && 
									!orderedItem.isEditable()) {
								return false;
							}else {
								continue;
							}
						}
					}
				}
			}
		}

		return true;
	}

	private void setButtonText() {
		String text = "";
		if(!isOrderSentToKitchen()) {
			text = mLanguageManager.getSendOrder();
		}else {
			checkForOrderChange();
			if(guestsToSend == null || guestsToSend.size() <= 0) {
				text = mLanguageManager.getCheckout();
			}else {
				text = mLanguageManager.getSendOrder();
			}
		}
		btnSendOrderFooter.setText(text);
	}

	private void callDialog() {
		builderEnterPin = 
				new Dialog(OrderRelatedActivity.this);

		builderEnterPin.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		builderEnterPin.requestWindowFeature(Window.FEATURE_NO_TITLE);

		builderEnterPin.setCancelable(false);
		builderEnterPin.setTitle(getString(R.string.app_name));
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.pin_screen, null);
		builderEnterPin.setContentView(view);

		TextView txtEnterPinToLogout = (TextView)view.findViewById(R.id.txtEnterPinToLogout);
		txtEnterPinToLogout.setText(mLanguageManager.getEnterPinToLogout());

		txtEnterPinToLogout.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());

		editEnterPinToExit = (EditText)view.findViewById(R.id.editPinToExit);
		btnCancel = (Button)view.findViewById(R.id.btnCloseLayout);

		editEnterPinToExit.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());
		btnCancel.setTextSize(TypedValue.COMPLEX_UNIT_PX, FontManager.getInstance().getFontSize());

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editEnterPinToExit.getWindowToken(), 0);

		String enterPin = mLanguageManager.getEnterPin();
		String cancel = mLanguageManager.getCancel();

		editEnterPinToExit.setHint(enterPin);
		btnCancel.setText(cancel);

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager)getSystemService(
						Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editEnterPinToExit.getWindowToken(), 0);
				builderEnterPin.dismiss();
			}
		});

		editEnterPinToExit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.toString().length() >= 4) {

					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(editEnterPinToExit.getWindowToken(), 0);

					if(Validator.isValidNumber(s.toString())) {
						boolean isNumber = Validator.containsOnlyNumbers(s.toString());
						
						if(isNumber) {
							WSWaiterPin wsWaiterPin = new WSWaiterPin();
							wsWaiterPin.setWaiterCode(Prefs.getKey(Prefs.WAITER_CODE));
							wsWaiterPin.setMacAddress(Utils.getMacAddress());
							wsWaiterPin.setWaiterPin(s.toString());

							logoutAsyncTask = new LogoutAsyncTask(OrderRelatedActivity.this, 
									wsWaiterPin, 
									Global.ORDER_RELATED);
							logoutAsyncTask.execute();
						}else {
							String message = mLanguageManager.getEnterValidPin();
							showMessageBox(message);
						}
					}else {
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
		if(result != null) {
			if(result.isError() && 
					result.getErrorMessage().toLowerCase().contains("invalid")) {

				String message = mLanguageManager.getInvalidUser();
				showMessageBox(message);

				if(editEnterPinToExit != null) {
					editEnterPinToExit.setText("");
				}
			}else {
				if(builderEnterPin != null) {
					builderEnterPin.dismiss();
				}
				toggle();
				Intent intent = new Intent (OrderRelatedActivity.this, LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | 
						Intent.FLAG_ACTIVITY_CLEAR_TASK |
						Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(Global.FROM_ACTIVITY, Global.FROM_SPLASH);
				startActivity(intent);
				Global.activityStartAnimationRightToLeft(OrderRelatedActivity.this);
				finish();
			}
		}else {
			String message = mLanguageManager.getServerUnreachable();
			showMessageBox(message);

			if(editEnterPinToExit != null) {
				editEnterPinToExit.setText("");
			}
		}
	}

	private void showMessageBox(String message) {
		if(message != null) {
			String appName = mLanguageManager.getAppName();
			String ok = mLanguageManager.getOk();

			CommonDialog dialog = 
					new CommonDialog(OrderRelatedActivity.this, appName, ok, message);
			dialog.show();
		}
	}

	private boolean checkIfGuestHasEditableItems(int position) {
		boolean flag = true;
		if(orderDisplayEditObj != null) {
			Order orderDummy = (Order)orderDisplayEditObj;

			if(orderDummy != null) {
				Guest guest = orderDummy.getGuests().get(position);

				if(guest != null) {
					if(guest.getOrderedItems() != null){
						for(OrderedItem orderedItem : guest.getOrderedItems()) {
							if(orderedItem.isEditable() == false && orderedItem.getOrderedItemStatus() != 0) {
								flag = false;
								break;
							}else {
								continue;
							}
						}
					}
				}
			}
		}

		return flag;
	}
}
