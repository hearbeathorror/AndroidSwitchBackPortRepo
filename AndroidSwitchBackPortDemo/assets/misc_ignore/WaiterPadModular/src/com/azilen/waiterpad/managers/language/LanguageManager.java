package com.azilen.waiterpad.managers.language;

import java.util.List;

import android.support.v4.util.LruCache;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.WaiterPadApplication;
import com.azilen.waiterpad.data.Languages;
import com.azilen.waiterpad.managers.network.NetworkManager;
import com.azilen.waiterpad.managers.network.RequestType;
import com.azilen.waiterpad.managers.network.ServiceUrlManager;
import com.azilen.waiterpad.utils.Global;
import com.azilen.waiterpad.utils.Prefs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LanguageManager {
	private static LanguageManager instance = new LanguageManager();
	private Languages mLanguages;

	private LruCache<String, Object> languageCache;

	// language string keys
	private String nonGroupModifiers = "nonGroupModifiers";
	private String refresh = "refresh";
	private String max = "max";
	private String min = "min";
	private String modifiersLast = "modifiersLast";
	private String minModifiersToSelect = "minModifiersToSelect";
	private String youCanOnlySelect = "youCanOnlySelect";
	private String under = "under";
	private String group = "group";
	private String choose = "choose";
	private String outOf = "outOf";
	private String syncMenuMessage = "syncMenuMessage";
	private String selectCategory = "selectCategory";
	private String pleaseEnterAValidQuantity = "pleaseEnterAValidQuantity";
	private String pleaseEnterAQuantity = "pleaseEnterAQuantity";
	private String fractionalLimitValue = "fractionalLimitValue";
	private String noResults = "noResults";
	private String tableSingle = "tableSingle";
	private String orderLossMessage = "orderLossMessage";
	private String startNode = "startNode";
	private String isoCode = "isoCode";
	private String appName = "appName";
	private String loading = "loading";
	private String pleasePressBackAgain = "pleasePressBackAgain";
	private String downloadingMenu = "downloadingMenu";
	private String refreshingData = "refreshingData";
	private String notificationCenter = "notificationCenter";
	private String syncMenu = "syncMenu";
	private String notifications = "notifications";
	private String orders = "orders";
	private String tables = "tables";
	private String initializing = "initializing";
	private String lock = "lock";
	private String changeWaiter = "changeWaiter";
	private String openOrders = "openOrders";
	private String itemCannotBeDeleted = "itemCannotBeDeleted";
	private String cannotDeleteGuest = "cannotDeleteGuest";
	private String selectedSection = "selectedSection";
	private String selectedLanguage = "selectedLanguage";
	private String memoryMessage = "memoryMessage";
	private String oneGuestMessage = "oneGuestMessage";
	private String orderAdded = "orderAdded";
	private String orderUpdated = "orderUpdated";
	private String checkoutSuccess = "checkoutSuccess";
	private String noOrderedItems = "noOrderedItems";
	
	private String deviceNotRegistered = "deviceNotRegistered";

	private String goToOrder = "goToOrder";
	private String searchItem = "searchItem";

	private String cancel = "cancel";
	private String save = "save";
	private String reset = "reset";

	// related to the configure settings screen
	private String ipAddress = "ipAddress";
	private String portAddress = "portAddress";
	private String configureSettingsLabel = "configureSettingsLabel";

	// related to the login screen
	private String enterPin = "enterPin";
	private String changeConfigSettings = "changeConfigSettings";

	// related to the order screen
	private String order = "order";
	private String yes = "yes";
	private String no = "no";
	private String ok = "ok";
	private String addNewItem = "addNewItem";
	private String checkout = "checkout";
	private String sendOrder = "sendOrder";
	private String addNewGuest = "addNewGuest";
	private String billSplitMessage = "billSplitMessage";
	private String total = "total";
	private String guest = "guest";

	// related to the item desc box
	private String remove = "remove";
	private String add = "add";
	private String update = "update";
	private String quantity = "quantity";
	private String fraction = "fraction";
	private String enterKitchenNote = "enterKitchenNote";
	private String modifiers = "modifiers";
	private String allowFractions = "allowFractions";

	// related to the modifiers selection
	private String applyModifiers = "applyModifiers";

	// related to the settings screen
	private String organizeMenu = "organizeMenu";
	private String selectSection = "selectSection";
	private String logout = "logout";
	private String notificationsEnabled = "notificationsEnabled";
	private String languageSelection = "languageSelection";
	private String backEndSettings = "backEndSettings";
	private String forceSync = "forceSync";
	private String sendLog = "sendLog";
	private String exit = "exit";

	// related to the table list screen
	private String pending = "pending";
	private String placeOrder = "placeOrder";

	// related to the per table orders screen
	private String orderList = "orderList";
	private String newOrder = "newOrder";

	// related to validations
	private String enterPort = "enterPort";
	private String enterIp = "enterIp";
	private String enterAllValues = "enterAllValues";
	private String enterValidIp = "enterValidIp";
	private String serverUnreachable = "serverUnreachable";
	private String incorrectPinSize = "incorrectPinSize";
	private String invalidUser = "invalidUser";
	private String licenseLimit = "licenseLimit";
	private String enterValidPin = "enterValidPin";
	private String enterValidExitPin = "enterValidExitPin";
	private String enterValidPort = "enterValidPort";

	// related to the notification
	private String orderNumberLabel = "orderNumberLabel";
	private String hasBeenUpdated = "hasBeenUpdated";

	// related to the menu items (action bar)
	private String menu = "menu";
	private String myOrders = "myOrders";
	private String myTables = "myTables";
	private String allTables = "allTables";
	private String activeTables = "activeTables";
	private String billRequestedTables = "billRequestedTables";
	private String settings = "settings";
	private String featuredItems = "featuredItems";
	private String userProfile = "userProfile";
	private String noOrderedItemsForAllGuests = "noOrderedItemsForAllGuests";

	private String enterPinToLogout = "enterPinToLogout";

	private String currancySymbol = "currancySymbol";
	private String currancyLocation = "currancyLocation";
	
	// changes as on 9th December 2013
	// related to the switch on and off text
	private String on = "ON";
	private String off = "OFF";
	// changes end here
	
	// changes as on 10th December 2013
	// related to the headers
	private String totalHeaderKey = "totalHeader";
	private String orderHeaderKey = "orderHeader";
	private String sectionHeaderKey = "sectionHeader";
	private String tableHeaderKey = "tableHeader";
	// changes end here

	// max cache size 1 MB = 1024 KB
	private int maxSize = 1 * 1024 * 1024;

	public LanguageManager() {
		languageCache = new LruCache<String, Object>(maxSize);
	}

	/* singleton object */
	public static LanguageManager getInstance() {
		return instance;
	}

	public LruCache<String, Object> getLanguageCache() {
		return languageCache;
	}

	/**
	 * Returns the list of languages that are present
	 * 
	 * @return
	 */
	public Languages getLanguages(String parameterSent) {
		String url = ServiceUrlManager.getInstance().getServiceUrlByType(
				RequestType.GET_LANGUAGES)
				+ ServiceUrlManager.SEPARATOR + parameterSent;

		Global.logd("URL : " + url);

		String languageResponse = NetworkManager.getInstance()
				.performGetRequest(url);

		if (languageResponse == null) {
			return null;
		} else {
			try {
				GsonBuilder gsonBuilder = new GsonBuilder();
				Gson gson = gsonBuilder.create();
				mLanguages = gson.fromJson(languageResponse, Languages.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return mLanguages;
		}
	}

	/**
	 * Stores all the languages obtained into the memcache of the device. Used
	 * by @GetAllDataAsyncTask and @SettingsFragment
	 * 
	 * @param languages
	 * @param memCache
	 */
	public void storeLangugagesIntoCache(Languages result, String parameterSent) {
		if (result != null) {
			switch (result.getResponseCode()) {
			case 1000:
				// There's an error that takes place
				Prefs.addKey(WaiterPadApplication.getAppContext(),
						Prefs.IS_LANGUAGE_PRESENT, false);
				break;

			case 1004:
				// store the languages into the cache
				if (result.getLanguages() != null) {

					Prefs.addKey(WaiterPadApplication.getAppContext(),
							Prefs.IS_LANGUAGE_PRESENT, true);

					List<String> languages = result.getLanguages();
					languageCache.put(Global.LANGUAGES, languages);

					Global.logd("inside if of languages");
				} else {
					// no language list obtained
					// use english by default
					Global.logd("inside else of languages");
				}
				break;

			case 1005:

				// store the xml into the cache
				if (result.getLanguageXml() != null) {
					// the language xml file is there
					LanguageParser languageParser = new LanguageParser(
							WaiterPadApplication.getAppContext(),
							result.getLanguageXml());
					boolean isParsed = languageParser.parseDocument();

					if (isParsed) {
						// store it in the memory
						// languageCache.put(parameterSent, languageXml);
						Prefs.addKey(parameterSent, result.getLanguageXml());
					}
				} else {
					// use english by default
					// and display a message to the user
				}
				break;

			default:
				break;
			}
		}
	}

	public String getValueFromKey(String keyParam) {
		return (languageCache.get(keyParam) == null ? "" : languageCache.get(
				keyParam).toString());
	}

	public String getCurrancySymbol() {
		String temp = getValueFromKey(currancySymbol);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.currancy_symbol) : temp;
	}

	public void setCurrancySymbol(String currancySymbol) {
		languageCache.put(this.currancySymbol, currancySymbol);
	}

	public String IS_AFTER() {
		String temp = getValueFromKey(currancyLocation);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.currancy_location) : temp;
	}

	public void IS_AFTER(String currancyLocation) {
		languageCache.put(this.currancyLocation, currancyLocation);
	}

	/**
	 * @return the menu
	 */
	public String getMenu() {
		String temp = getValueFromKey(menu);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.menu) : temp;
	}

	/**
	 * @param menu
	 *            the menu to set
	 */
	public void setMenu(String menu) {
		languageCache.put(this.menu, menu);
	}

	/**
	 * @return the myTables
	 */
	public String getMyTables() {
		String temp = getValueFromKey(myTables);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.my_tables) : temp;
	}

	/**
	 * @param myTables
	 *            the myTables to set
	 */
	public void setMyTables(String myTables) {
		languageCache.put(this.myTables, myTables);
	}

	/**
	 * @return the allTables
	 */
	public String getAllTables() {
		String temp = getValueFromKey(allTables);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.all_tables) : temp;
	}

	/**
	 * @param allTables
	 *            the allTables to set
	 */
	public void setAllTables(String allTables) {
		languageCache.put(this.allTables, allTables);
	}

	/**
	 * @return the activeTables
	 */
	public String getActiveTables() {
		String temp = getValueFromKey(activeTables);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.active_tables) : temp;
	}

	/**
	 * @param activeTables
	 *            the activeTables to set
	 */
	public void setActiveTables(String activeTables) {
		languageCache.put(this.activeTables, activeTables);
	}

	/**
	 * @return the billRequestedTables
	 */
	public String getBillRequestedTables() {
		String temp = getValueFromKey(billRequestedTables);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.bill_request_tables) : temp;
	}

	/**
	 * @param billRequestedTables
	 *            the billRequestedTables to set
	 */
	public void setBillRequestedTables(String billRequestedTables) {
		languageCache.put(this.billRequestedTables, billRequestedTables);
	}

	public String getNonGroupModifiers() {
		String temp = getValueFromKey(nonGroupModifiers);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.non_group_modifiers) : temp;
	}

	public void setNonGroupModifiers(String nonGroupModifiers) {
		languageCache.put(this.nonGroupModifiers, nonGroupModifiers);
	}

	public String getRefresh() {
		String temp = getValueFromKey(refresh);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.refresh) : temp;
	}

	public void setRefresh(String refresh) {
		languageCache.put(this.refresh, refresh);
	}

	public String getMax() {
		String temp = getValueFromKey(max);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.max) : temp;
	}

	public void setMax(String max) {
		languageCache.put(this.max, max);
	}

	public String getMin() {
		String temp = getValueFromKey(min);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.min) : temp;
	}

	public void setMin(String min) {
		languageCache.put(this.min, min);
	}

	public String getModifiersLast() {
		String temp = getValueFromKey(modifiersLast);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.modifiers_last) : temp;
	}

	public void setModifiersLast(String modifiersLast) {
		languageCache.put(this.modifiersLast, modifiersLast);
	}

	public String getMinModifiersToSelect() {
		String temp = getValueFromKey(minModifiersToSelect);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.min_modifiers_to_select) : temp;
	}

	public void setMinModifiersToSelect(String minModifiersToSelect) {
		languageCache.put(this.minModifiersToSelect, minModifiersToSelect);
	}

	public String getYouCanOnlySelect() {
		String temp = getValueFromKey(youCanOnlySelect);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.modifiers_to_select) : temp;
	}

	public void setYouCanOnlySelect(String youCanOnlySelect) {
		languageCache.put(this.youCanOnlySelect, youCanOnlySelect);
	}

	public String getUnder() {
		String temp = getValueFromKey(under);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.under) : temp;
	}

	public void setUnder(String under) {
		languageCache.put(this.under, under);
	}

	public String getGroup() {
		String temp = getValueFromKey(group);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.group) : temp;
	}

	public void setGroup(String group) {
		languageCache.put(this.group, group);
	}

	public String getChoose() {
		String temp = getValueFromKey(choose);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.choose) : temp;
	}

	public void setChoose(String choose) {
		languageCache.put(this.choose, choose);
	}

	public String getOutOf() {
		String temp = getValueFromKey(outOf);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.out_of) : temp;
	}

	public void setOutOf(String outOf) {
		languageCache.put(this.outOf, outOf);
	}

	public String getSyncMenuMessage() {
		String temp = getValueFromKey(syncMenuMessage);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.sync_menu_ask) : temp;
	}

	public void setSyncMenuMessage(String syncMenuMessage) {
		languageCache.put(this.syncMenuMessage, syncMenuMessage);
	}

	public String getSelectCategory() {
		String temp = getValueFromKey(selectCategory);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.select_category) : temp;
	}

	public void setSelectCategory(String selectCategory) {
		languageCache.put(this.selectCategory, selectCategory);
	}

	public String getPleaseEnterAValidQuantity() {
		String temp = getValueFromKey(pleaseEnterAValidQuantity);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.please_enter_a_valid_quantity) : temp;
	}

	public void setPleaseEnterAValidQuantity(String pleaseEnterAValidQuantity) {
		languageCache.put(this.pleaseEnterAValidQuantity,
				pleaseEnterAValidQuantity);
	}

	public String getPleaseEnterAQuantity() {
		String temp = getValueFromKey(pleaseEnterAQuantity);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.please_enter_a_quantity) : temp;
	}

	public void setPleaseEnterAQuantity(String pleaseEnterAQuantity) {
		languageCache.put(this.pleaseEnterAQuantity, pleaseEnterAQuantity);
	}

	public String getFractionalLimitValue() {
		String temp = getValueFromKey(fractionalLimitValue);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.fractional_limit_value) : temp;
	}

	public void setFractionalLimitValue(String fractionalLimitValue) {
		languageCache.put(this.fractionalLimitValue, fractionalLimitValue);
	}

	public String getNoResults() {
		String temp = getValueFromKey(noResults);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.no_results) : temp;
	}

	public void setNoResults(String noResults) {
		languageCache.put(this.noResults, noResults);
	}

	public String getTableSingle() {
		String temp = getValueFromKey(tableSingle);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.table_title) : temp;
	}

	public void setTableSingle(String tableSingle) {
		languageCache.put(this.tableSingle, tableSingle);
	}

	public String getOrderLossMessage() {
		String temp = getValueFromKey(orderLossMessage);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.order_loss_message) : temp;
	}

	public void setOrderLossMessage(String orderLossMessage) {
		languageCache.put(this.orderLossMessage, orderLossMessage);
	}

	public String getStartNode() {
		String temp = getValueFromKey(startNode);
		return temp;
	}

	public void setStartNode(String startNode) {
		languageCache.put(this.startNode, startNode);
	}

	public String getIsoCode() {
		String temp = getValueFromKey(isoCode);
		return temp;
	}

	public void setIsoCode(String isoCode) {
		languageCache.put(this.isoCode, isoCode);
	}

	public String getAppName() {
		String temp = getValueFromKey(appName);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.app_name) : temp;
	}

	public void setAppName(String appName) {
		languageCache.put(this.appName, appName);
	}

	public String getLoading() {
		String temp = getValueFromKey(loading);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.loading) : temp;
	}

	public void setLoading(String loading) {
		languageCache.put(this.loading, loading);
	}

	public String getPleasePressBackAgain() {
		String temp = getValueFromKey(pleasePressBackAgain);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.please_press_back_again) : temp;
	}

	public void setPleasePressBackAgain(String pleasePressBackAgain) {
		languageCache.put(this.pleasePressBackAgain, pleasePressBackAgain);
	}

	public String getDownloadingMenu() {
		String temp = getValueFromKey(downloadingMenu);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.downloading_menu) : temp;
	}

	public void setDownloadingMenu(String downloadingMenu) {
		languageCache.put(this.downloadingMenu, downloadingMenu);
	}

	public String getRefreshingData() {
		String temp = getValueFromKey(refreshingData);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.refreshing_data) : temp;
	}

	public void setRefreshingData(String refreshingData) {
		languageCache.put(this.refreshingData, refreshingData);
	}

	public String getNotificationCenter() {
		String temp = getValueFromKey(notificationCenter);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.notification_center) : temp;
	}

	public void setNotificationCenter(String notificationCenter) {
		languageCache.put(this.notificationCenter, notificationCenter);
	}

	public String getSyncMenu() {
		String temp = getValueFromKey(syncMenu);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.sync_menu) : temp;
	}

	public void setSyncMenu(String syncMenu) {
		languageCache.put(this.syncMenu, syncMenu);
	}

	public String getNotifications() {
		String temp = getValueFromKey(notifications);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.notifications) : temp;
	}

	public void setNotifications(String notifications) {
		languageCache.put(this.notifications, notifications);
	}

	public String getOrders() {
		String temp = getValueFromKey(orders);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.orders) : temp;
	}

	public void setOrders(String orders) {
		languageCache.put(this.orders, orders);
	}

	public String getTables() {
		String temp = getValueFromKey(tables);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.tables) : temp;
	}

	public void setTables(String tables) {
		languageCache.put(this.tables, tables);
	}

	public String getInitializing() {
		String temp = getValueFromKey(initializing);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.initializing) : temp;
	}

	public void setInitializing(String initializing) {
		languageCache.put(this.initializing, initializing);
	}

	public String getLock() {
		String temp = getValueFromKey(lock);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.lock) : temp;
	}

	public void setLock(String lock) {
		languageCache.put(this.lock, lock);
	}

	public String getChangeWaiter() {
		String temp = getValueFromKey(changeWaiter);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.change_waiter) : temp;
	}

	public void setChangeWaiter(String changeWaiter) {
		languageCache.put(this.changeWaiter, changeWaiter);
	}

	public String getOpenOrders() {
		String temp = getValueFromKey(openOrders);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.open_orders) : temp;
	}

	public void setOpenOrders(String openOrders) {
		languageCache.put(this.openOrders, openOrders);
	}

	public String getItemCannotBeDeleted() {
		String temp = getValueFromKey(itemCannotBeDeleted);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.item_cannot_be_deleted) : temp;
	}

	public void setItemCannotBeDeleted(String itemCannotBeDeleted) {
		languageCache.put(this.itemCannotBeDeleted, itemCannotBeDeleted);
	}

	public String getCannotDeleteGuest() {
		String temp = getValueFromKey(cannotDeleteGuest);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.cannot_delete_guest) : temp;
	}

	public void setCannotDeleteGuest(String cannotDeleteGuest) {
		languageCache.put(this.cannotDeleteGuest, cannotDeleteGuest);
	}

	public String getSelectedSection() {
		String temp = getValueFromKey(selectedSection);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.section_selected) : temp;
	}

	public void setSelectedSection(String selectedSection) {
		languageCache.put(this.selectedSection, selectedSection);
	}

	public String getSelectedLanguage() {
		String temp = getValueFromKey(selectedLanguage);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.language_selected) : temp;
	}

	public void setSelectedLanguage(String selectedLanguage) {
		languageCache.put(this.selectedLanguage, selectedLanguage);
	}

	public String getMemoryMessage() {
		String temp = getValueFromKey(memoryMessage);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.memory_message) : temp;
	}

	public void setMemoryMessage(String memoryMessage) {
		languageCache.put(this.memoryMessage, memoryMessage);
	}

	public String getOneGuestMessage() {
		String temp = getValueFromKey(oneGuestMessage);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.guest_one_only) : temp;
	}

	public void setOneGuestMessage(String oneGuestMessage) {
		languageCache.put(this.oneGuestMessage, oneGuestMessage);
	}

	public String getOrderAdded() {
		String temp = getValueFromKey(orderAdded);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.order_added) : temp;
	}

	public void setOrderAdded(String orderAdded) {
		languageCache.put(this.orderAdded, orderAdded);
	}

	public String getOrderUpdated() {
		String temp = getValueFromKey(orderUpdated);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.order_updated) : temp;
	}

	public void setOrderUpdated(String orderUpdated) {
		languageCache.put(this.orderUpdated, orderUpdated);
	}

	public String getCheckoutSuccess() {
		String temp = getValueFromKey(checkoutSuccess);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.order_checked_out) : temp;
	}

	public void setCheckoutSuccess(String checkoutSuccess) {
		languageCache.put(this.checkoutSuccess, checkoutSuccess);
	}

	public String getNoOrderedItems() {
		String temp = getValueFromKey(noOrderedItems);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.no_ordered_items) : temp;
	}

	public void setNoOrderedItems(String noOrderedItems) {
		languageCache.put(this.noOrderedItems, noOrderedItems);
	}

	public String getGoToOrder() {
		String temp = getValueFromKey(goToOrder);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.go_to_order) : temp;
	}

	public void setGoToOrder(String goToOrder) {
		languageCache.put(this.goToOrder, goToOrder);
	}

	public String getSearchItem() {
		String temp = getValueFromKey(searchItem);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.search_item) : temp;
	}

	public void setSearchItem(String searchItem) {
		languageCache.put(this.searchItem, searchItem);
	}

	public String getCancel() {
		String temp = getValueFromKey(cancel);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.cancel) : temp;
	}

	public void setCancel(String cancel) {
		languageCache.put(this.cancel, cancel);
	}

	public String getSave() {
		String temp = getValueFromKey(save);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.save) : temp;
	}

	public void setSave(String save) {
		languageCache.put(this.save, save);
	}

	public String getReset() {
		String temp = getValueFromKey(reset);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.reset) : temp;
	}

	public void setReset(String reset) {
		languageCache.put(this.reset, reset);
	}

	public String getIpAddress() {
		String temp = getValueFromKey(ipAddress);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.ip_address) : temp;
	}

	public void setIpAddress(String ipAddress) {
		languageCache.put(this.ipAddress, ipAddress);
	}

	public String getPortAddress() {
		String temp = getValueFromKey(portAddress);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.port) : temp;
	}

	public void setPortAddress(String portAddress) {
		languageCache.put(this.portAddress, portAddress);
	}

	public String getConfigureSettingsLabel() {
		String temp = getValueFromKey(configureSettingsLabel);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.settings_label) : temp;
	}

	public void setConfigureSettingsLabel(String configureSettingsLabel) {
		languageCache.put(this.configureSettingsLabel, configureSettingsLabel);
	}

	public String getEnterPin() {
		String temp = getValueFromKey(enterPin);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.enter_pin) : temp;
	}

	public void setEnterPin(String enterPin) {
		languageCache.put(this.enterPin, enterPin);
	}

	public String getChangeConfigSettings() {
		String temp = getValueFromKey(changeConfigSettings);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.change_config_settings) : temp;
	}

	public void setChangeConfigSettings(String changeConfigSettings) {
		languageCache.put(this.changeConfigSettings, changeConfigSettings);
	}

	public String getOrder() {
		String temp = getValueFromKey(order);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.order) : temp;
	}

	public void setOrder(String order) {
		languageCache.put(this.order, order);
	}

	public String getYes() {
		String temp = getValueFromKey(yes);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.YES) : temp;
	}

	public void setYes(String yes) {
		languageCache.put(this.yes, yes);
	}

	public String getNo() {
		String temp = getValueFromKey(no);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.NO) : temp;
	}

	public void setNo(String no) {
		languageCache.put(this.no, no);
	}

	public String getOk() {
		String temp = getValueFromKey(ok);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.OK) : temp;
	}

	public void setOk(String ok) {
		languageCache.put(this.ok, ok);
	}

	public String getAddNewItem() {
		String temp = getValueFromKey(addNewItem);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.add_item) : temp;
	}

	public void setAddNewItem(String addNewItem) {
		languageCache.put(this.addNewItem, addNewItem);
	}

	public String getCheckout() {
		String temp = getValueFromKey(checkout);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.checkout) : temp;
	}

	public void setCheckout(String checkout) {
		languageCache.put(this.checkout, checkout);
	}

	public String getSendOrder() {
		String temp = getValueFromKey(sendOrder);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.send_order) : temp;
	}

	public void setSendOrder(String sendOrder) {
		languageCache.put(this.sendOrder, sendOrder);
	}

	public String getAddNewGuest() {
		String temp = getValueFromKey(addNewGuest);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.add_guest) : temp;
	}

	public void setAddNewGuest(String addNewGuest) {
		languageCache.put(this.addNewGuest, addNewGuest);
	}

	public String getBillSplitMessage() {
		String temp = getValueFromKey(billSplitMessage);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.bill_split_message) : temp;
	}

	public void setBillSplitMessage(String billSplitMessage) {
		languageCache.put(this.billSplitMessage, billSplitMessage);
	}

	public String getTotal() {
		String temp = getValueFromKey(total);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.total) : temp;
	}

	public void setTotal(String total) {
		languageCache.put(this.total, total);
	}

	public String getGuest() {
		String temp = getValueFromKey(guest);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.guest) : temp;
	}

	public void setGuest(String guest) {
		languageCache.put(this.guest, guest);
	}

	public String getRemove() {
		String temp = getValueFromKey(remove);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.remove) : temp;
	}

	public void setRemove(String remove) {
		languageCache.put(this.remove, remove);
	}

	public String getAdd() {
		String temp = getValueFromKey(add);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.add) : temp;
	}

	public void setAdd(String add) {
		languageCache.put(this.add, add);
	}

	public String getUpdate() {
		String temp = getValueFromKey(update);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.update) : temp;
	}

	public void setUpdate(String update) {
		languageCache.put(this.update, update);
	}

	public String getQuantity() {
		String temp = getValueFromKey(quantity);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.quantity) : temp;
	}

	public void setQuantity(String quantity) {
		languageCache.put(this.quantity, quantity);
	}

	public String getFraction() {
		String temp = getValueFromKey(fraction);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.fraction) : temp;
	}

	public void setFraction(String fraction) {
		languageCache.put(this.fraction, fraction);
	}

	public String getEnterKitchenNote() {
		String temp = getValueFromKey(enterKitchenNote);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.enter_kitchen_note) : temp;
	}

	public void setEnterKitchenNote(String enterKitchenNote) {
		languageCache.put(this.enterKitchenNote, enterKitchenNote);
	}

	public String getModifiers() {
		String temp = getValueFromKey(modifiers);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.modifier) : temp;
	}

	public void setModifiers(String modifiers) {
		languageCache.put(this.modifiers, modifiers);
	}

	public String getAllowFractions() {
		String temp = getValueFromKey(allowFractions);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.allow_fractions) : temp;
	}

	public void setAllowFractions(String allowFractions) {
		languageCache.put(this.allowFractions, allowFractions);
	}

	public String getApplyModifiers() {
		String temp = getValueFromKey(applyModifiers);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.apply_modifiers_on) : temp;
	}

	public void setApplyModifiers(String applyModifiers) {
		languageCache.put(this.applyModifiers, applyModifiers);
	}

	public String getLogout() {
		String temp = getValueFromKey(logout);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.logout) : temp;
	}

	public void setLogout(String logout) {
		languageCache.put(this.logout, logout);
	}

	public String getNotificationsEnabled() {
		String temp = getValueFromKey(notificationsEnabled);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.notifications_enabled) : temp;
	}

	public void setNotificationsEnabled(String notificationsEnabled) {
		languageCache.put(this.notificationsEnabled, notificationsEnabled);
	}

	public String getLanguageSelection() {
		String temp = getValueFromKey(languageSelection);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.language_selection) : temp;
	}

	public void setLanguageSelection(String languageSelection) {
		languageCache.put(this.languageSelection, languageSelection);
	}

	public String getBackEndSettings() {
		String temp = getValueFromKey(backEndSettings);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.back_end_settings) : temp;
	}

	public void setBackEndSettings(String backEndSettings) {
		languageCache.put(this.backEndSettings, backEndSettings);
	}

	public String getForceSync() {
		String temp = getValueFromKey(forceSync);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.force_sync) : temp;
	}

	public void setForceSync(String forceSync) {
		languageCache.put(this.forceSync, forceSync);
	}

	public String getSendLog() {
		String temp = getValueFromKey(sendLog);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.send_log) : temp;
	}

	public void setSendLog(String sendLog) {
		languageCache.put(this.sendLog, sendLog);
	}

	public String getExit() {
		String temp = getValueFromKey(exit);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.exit) : temp;
	}

	public void setExit(String exit) {
		languageCache.put(this.exit, exit);
	}

	public String getOrganizeMenu() {
		String temp = getValueFromKey(organizeMenu);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.organize_menu) : temp;
	}

	public void setOrganizeMenu(String organizeMenu) {
		languageCache.put(this.organizeMenu, organizeMenu);
	}

	public String getSelectSection() {
		String temp = getValueFromKey(selectSection);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.select_section) : temp;
	}

	public void setSelectSection(String selectSection) {
		languageCache.put(this.selectSection, selectSection);
	}

	public String getPending() {
		String temp = getValueFromKey(pending);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.pending) : temp;
	}

	public void setPending(String pending) {
		languageCache.put(this.pending, pending);
	}

	public String getPlaceOrder() {
		String temp = getValueFromKey(placeOrder);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.place_order) : temp;
	}

	public void setPlaceOrder(String placeOrder) {
		languageCache.put(this.placeOrder, placeOrder);
	}

	public String getOrderList() {
		String temp = getValueFromKey(orderList);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.order_list) : temp;
	}

	public void setOrderList(String orderList) {
		languageCache.put(this.orderList, orderList);
	}

	public String getNewOrder() {
		String temp = getValueFromKey(newOrder);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.new_order) : temp;
	}

	public void setNewOrder(String newOrder) {
		languageCache.put(this.newOrder, newOrder);
	}

	public String getEnterPort() {
		String temp = getValueFromKey(enterPort);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.enter_port) : temp;
	}

	public void setEnterPort(String enterPort) {
		languageCache.put(this.enterPort, enterPort);
	}

	public String getEnterIp() {
		String temp = getValueFromKey(enterIp);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.enter_ip) : temp;
	}

	public void setEnterIp(String enterIp) {
		languageCache.put(this.enterIp, enterIp);
	}

	public String getEnterAllValues() {
		String temp = getValueFromKey(enterAllValues);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.enter_values) : temp;
	}

	public void setEnterAllValues(String enterAllValues) {
		languageCache.put(this.enterAllValues, enterAllValues);
	}

	public String getEnterValidIp() {
		String temp = getValueFromKey(enterValidIp);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.valid_ip) : temp;
	}

	public void setEnterValidIp(String enterValidIp) {
		languageCache.put(this.enterValidIp, enterValidIp);
	}

	public String getServerUnreachable() {
		String temp = getValueFromKey(serverUnreachable);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.server_unreachable) : temp;
	}

	public void setServerUnreachable(String serverUnreachable) {
		languageCache.put(this.serverUnreachable, serverUnreachable);
	}

	public String getIncorrectPinSize() {
		String temp = getValueFromKey(incorrectPinSize);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.incorrect_pin_size) : temp;
	}

	public void setIncorrectPinSize(String incorrectPinSize) {
		languageCache.put(this.incorrectPinSize, incorrectPinSize);
	}

	public String getInvalidUser() {
		String temp = getValueFromKey(invalidUser);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.invalid_user) : temp;
	}

	public void setInvalidUser(String invalidUser) {
		languageCache.put(this.invalidUser, invalidUser);
	}
	
	public String getDeviceNotRegistered() {
		String temp = getValueFromKey(deviceNotRegistered);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.device_not_registered) : temp;
	}
	
	public void setDeviceNotRegistered(String deviceNotRegistered) {
		languageCache.put(this.deviceNotRegistered, deviceNotRegistered);
	}

	public String getLicenseLimit() {
		String temp = getValueFromKey(licenseLimit);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.LICENCE_LIMIT_EXCEEDED) : temp;
	}

	public void setLicenseLimit(String licenseLimit) {
		languageCache.put(this.licenseLimit, licenseLimit);
	}

	public String getEnterValidPin() {
		String temp = getValueFromKey(enterValidPin);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.enter_valid_pin) : temp;
	}

	public void setEnterValidPin(String enterValidPin) {
		languageCache.put(this.enterValidPin, enterValidPin);
	}
	
	public String getEnterValidExitPin() {
		String temp = getValueFromKey(enterValidExitPin);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.enter_valid_exit_pin) : temp;
	}

	public void setEnterValidExitPin(String enterValidExitPin) {
		languageCache.put(this.enterValidExitPin, enterValidExitPin);
	}

	public String getEnterValidPort() {
		String temp = getValueFromKey(enterValidPort);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.enter_valid_port) : temp;
	}

	public void setEnterValidPort(String enterValidPort) {
		languageCache.put(this.enterValidPort, enterValidPort);
	}

	public String getOrderNumberLabel() {
		String temp = getValueFromKey(orderNumberLabel);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.order_number) : temp;
	}

	public void setOrderNumberLabel(String orderNumberLabel) {
		languageCache.put(this.orderNumberLabel, orderNumberLabel);
	}

	public String getHasBeenUpdated() {
		String temp = getValueFromKey(hasBeenUpdated);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.order_status_message) : temp;
	}

	public void setHasBeenUpdated(String hasBeenUpdated) {
		languageCache.put(this.hasBeenUpdated, hasBeenUpdated);
	}

	public String getMyOrders() {
		String temp = getValueFromKey(myOrders);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.myorders) : temp;
	}

	public void setMyOrders(String myOrders) {
		languageCache.put(this.myOrders, myOrders);
	}

	public String getSettings() {
		String temp = getValueFromKey(settings);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.settings) : temp;
	}

	public void setSettings(String settings) {
		languageCache.put(this.settings, settings);
	}

	public String getFeaturedItems() {
		String temp = getValueFromKey(featuredItems);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.featured_items) : temp;
	}

	public void setFeaturedItems(String featuredItems) {
		languageCache.put(this.featuredItems, featuredItems);
	}

	public String getUserProfile() {
		String temp = getValueFromKey(userProfile);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.user_profile) : temp;
	}

	public void setUserProfile(String userProfile) {
		languageCache.put(this.userProfile, userProfile);
	}

	public String getNoOrderedItemsForAllGuests() {
		String temp = getValueFromKey(noOrderedItemsForAllGuests);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.no_ordered_items_for_all_guests) : temp;
	}

	public void setNoOrderedItemsForAllGuests(String noOrderedItemsForAllGuests) {
		languageCache.put(this.noOrderedItemsForAllGuests,
				noOrderedItemsForAllGuests);
	}

	public String getEnterPinToLogout() {
		String temp = getValueFromKey(enterPinToLogout);
		return temp.equals("") ? WaiterPadApplication.getAppContext()
				.getString(R.string.enter_pin_to_logout) : temp;
	}

	public void setEnterPinToLogout(String enterPinToLogout) {
		languageCache.put(this.enterPinToLogout, enterPinToLogout);
	}
	
	// changes as on 9th December 2013
	public void setOn(String on) {
		languageCache.put(this.on, on);
	}
	
	public String getOn() {
		String temp = getValueFromKey(on);
		return temp.equals("") ? WaiterPadApplication
				.getAppContext().getString(
						R.string.on) : temp;
	}
	
	public void setOff(String off) {
		languageCache.put(this.off, off);
	}
	
	public String getOff() {
		String temp = getValueFromKey(off);
		return temp.equals("") ? WaiterPadApplication
				.getAppContext().getString(
						R.string.off) : temp;
	}
	// changes end here
	
	// changes as on 10th December 2013
	public void setOrderHeader(String orderHeader) {
		languageCache.put(this.orderHeaderKey, orderHeader);
	}
	
	public String getOrderHeader() {
		String temp = getValueFromKey(orderHeaderKey);
		return temp.equals("") ? WaiterPadApplication
				.getAppContext().getString(
						R.string.order_header) : temp;
	}
	
	public void setTableHeader(String tableHeader) {
		languageCache.put(this.tableHeaderKey, tableHeader);
	}
	
	public String getTableHeader() {
		String temp = getValueFromKey(tableHeaderKey);
		return temp.equals("") ? WaiterPadApplication
				.getAppContext().getString(
						R.string.table_header) : temp;
	}
	
	public void setTotalHeader(String totalHeader) {
		languageCache.put(this.totalHeaderKey, totalHeader);
	}
	
	public String getTotalHeader() {
		String temp = getValueFromKey(totalHeaderKey);
		return temp.equals("") ? WaiterPadApplication
				.getAppContext().getString(
						R.string.total_header) : temp;
	}
	
	public void setSectionHeader(String sectionHeader) {
		languageCache.put(this.sectionHeaderKey, sectionHeader);
	}
	
	public String getSectionHeader() {
		String temp = getValueFromKey(sectionHeaderKey);
		return temp.equals("") ? WaiterPadApplication
				.getAppContext().getString(
						R.string.section_header) : temp;
	}
	// changes end here
}
