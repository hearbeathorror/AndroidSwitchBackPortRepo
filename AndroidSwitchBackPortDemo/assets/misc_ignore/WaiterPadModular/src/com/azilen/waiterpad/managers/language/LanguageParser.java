package com.azilen.waiterpad.managers.language;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;

import com.azilen.waiterpad.utils.Prefs;

public class LanguageParser extends DefaultHandler {
	private String mResponse;
	private String mTempVal = "";
	private String mLangSelection;
	private LanguageManager languageManager;

	/**
	 * Constructor that takes the response as a string
	 * 
	 * @param response
	 */
	public LanguageParser(Context context, String response) {
		mResponse = response;
		mLangSelection = Prefs.getKey(Prefs.LANGUAGE_SELECTED);
		languageManager = LanguageManager.getInstance();
	}

	/**
	 * Constructor that takes the response as a string and selected language
	 * 
	 * @param response
	 *            : Response from web service
	 * @param selectedLanguage
	 *            : Selected language by user
	 */
	public LanguageParser(String response, String selectedLanguage) {
		mResponse = response;
		mLangSelection = selectedLanguage;
		languageManager = LanguageManager.getInstance();
	}

	/**
	 * Parses the document initializes the SAXParser
	 */
	public boolean parseDocument() {
		try {
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();
			saxParser.parse(new InputSource(new StringReader(mResponse)), this);
			return true;
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		mTempVal = "";

		// if no language has been selected and by chance
		// this method is called then do not create any new object instance
		// consider the default language
		if (mLangSelection != null && mLangSelection.trim().length() > 0) {
			if (qName.equalsIgnoreCase(mLangSelection)) {
				// its the first node
				// create a new object
				// languageCache = new LanguageXml();
				languageManager.setStartNode(mLangSelection);
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		mTempVal = new String(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		if (qName.equalsIgnoreCase("ISOCODE")) {
			languageManager.setIsoCode(mTempVal);
		} else if (qName.equalsIgnoreCase("APP_NAME")) {
			languageManager.setAppName(mTempVal);
		} else if (qName.equalsIgnoreCase("BACK_END_SETTINGS")) {
			languageManager.setBackEndSettings(mTempVal);
		} else if (qName.equalsIgnoreCase("FORCE_SYNC")) {
			languageManager.setForceSync(mTempVal);
		} else if (qName.equalsIgnoreCase("SEND_LOG")) {
			languageManager.setSendLog(mTempVal);
		} else if (qName.equalsIgnoreCase("ORGANIZE_MENU")) {
			languageManager.setOrganizeMenu(mTempVal);
		} else if (qName.equalsIgnoreCase("SELECT_SECTION")) {
			languageManager.setSelectSection(mTempVal);
		} else if (qName.equalsIgnoreCase("ENTER_PIN")) {
			languageManager.setEnterPin(mTempVal);
		} else if (qName.equalsIgnoreCase("IP_ADDRESS")) {
			languageManager.setIpAddress(mTempVal);
		} else if (qName.equalsIgnoreCase("PORT")) {
			languageManager.setPortAddress(mTempVal);
		} else if (qName.equalsIgnoreCase("SAVE")) {
			languageManager.setSave(mTempVal);
		} else if (qName.equalsIgnoreCase("CANCEL")) {
			languageManager.setCancel(mTempVal);
		} else if (qName.equalsIgnoreCase("LOGOUT")) {
			languageManager.setLogout(mTempVal);
		} else if (qName.equalsIgnoreCase("RESET")) {
			languageManager.setReset(mTempVal);
		} else if (qName.equalsIgnoreCase("SEND_ORDER")) {
			languageManager.setSendOrder(mTempVal);
		} else if (qName.equalsIgnoreCase("ADD")) {
			languageManager.setAdd(mTempVal);
		} else if (qName.equalsIgnoreCase("TOTAL")) {
			languageManager.setTotal(mTempVal);
		} else if (qName.equalsIgnoreCase("OK")) {
			languageManager.setOk(mTempVal);
		} else if (qName.equalsIgnoreCase("SEARCH_ITEM")) {
			languageManager.setSearchItem(mTempVal);
		} else if (qName.equalsIgnoreCase("ENTER_PORT")) {
			languageManager.setEnterPort(mTempVal);
		} else if (qName.equalsIgnoreCase("ENTER_IP")) {
			languageManager.setEnterIp(mTempVal);
		} else if (qName.equalsIgnoreCase("ENTER_VALUES")) {
			languageManager.setEnterAllValues(mTempVal);
		} else if (qName.equalsIgnoreCase("VALID_IP")) {
			languageManager.setEnterValidIp(mTempVal);
		} else if (qName.equalsIgnoreCase("SERVER_UNREACHABLE")) {
			languageManager.setServerUnreachable(mTempVal);
		} else if (qName.equalsIgnoreCase("INVALID_USER")) {
			languageManager.setInvalidUser(mTempVal);
		} else if (qName.equalsIgnoreCase("INCORRECT_PIN_SIZE")) {
			languageManager.setIncorrectPinSize(mTempVal);
		} else if (qName.equalsIgnoreCase("LICENCE_LIMIT_EXCEEDED")) {
			languageManager.setLicenseLimit(mTempVal);
		} else if (qName.equalsIgnoreCase("ORDER_LIST")) {
			languageManager.setOrderList(mTempVal);
		} else if (qName.equalsIgnoreCase("USER_PROFILE")) {
			languageManager.setUserProfile(mTempVal);
		} else if (qName.equalsIgnoreCase("LOADING")) {
			languageManager.setLoading(mTempVal);
		} else if (qName.equalsIgnoreCase("SETTINGS")) {
			languageManager.setSettings(mTempVal);
		} else if (qName.equalsIgnoreCase("ADD_GUEST")) {
			languageManager.setAddNewGuest(mTempVal);
		} else if (qName.equalsIgnoreCase("NEW_ORDER")) {
			languageManager.setNewOrder(mTempVal);
		} else if (qName.equalsIgnoreCase("CHECKOUT")) {
			languageManager.setCheckout(mTempVal);
		} else if (qName.equalsIgnoreCase("ADD_ITEM")) {
			languageManager.setAddNewItem(mTempVal);
		} else if (qName.equalsIgnoreCase("GUEST")) {
			languageManager.setGuest(mTempVal);
		} else if (qName.equalsIgnoreCase("REMOVE")) {
			languageManager.setRemove(mTempVal);
		} else if (qName.equalsIgnoreCase("UPDATE")) {
			languageManager.setUpdate(mTempVal);
		} else if (qName.equalsIgnoreCase("MY_TABLES")) {
			languageManager.setMyTables(mTempVal);
		} else if (qName.equalsIgnoreCase("SETTINGS_LABEL")) {
			languageManager.setConfigureSettingsLabel(mTempVal);
		} else if (qName.equalsIgnoreCase("CHANGE_CONFIG_SETTINGS")) {
			languageManager.setChangeConfigSettings(mTempVal);
		} else if (qName.equalsIgnoreCase("QUANTITY")) {
			languageManager.setQuantity(mTempVal);
		} else if (qName.equalsIgnoreCase("FRACTION")) {
			languageManager.setFraction(mTempVal);
		} else if (qName.equalsIgnoreCase("MENU")) {
			languageManager.setMenu(mTempVal);
		} else if (qName.equalsIgnoreCase("FEATURED_ITEMS")) {
			languageManager.setFeaturedItems(mTempVal);
		} else if (qName.equalsIgnoreCase("YES")) {
			languageManager.setYes(mTempVal);
		} else if (qName.equalsIgnoreCase("NO")) {
			languageManager.setNo(mTempVal);
		} else if (qName.equalsIgnoreCase("BILL_SPLIT_MESSAGE")) {
			languageManager.setBillSplitMessage(mTempVal);
		} else if (qName.equalsIgnoreCase("PLACE_ORDER")) {
			languageManager.setPlaceOrder(mTempVal);
		} else if (qName.equalsIgnoreCase("PENDING")) {
			languageManager.setPending(mTempVal);
		} else if (qName.equalsIgnoreCase("ORDER_NUMBER")) {
			languageManager.setOrderNumberLabel(mTempVal);
		} else if (qName.equalsIgnoreCase("ORDER_STATUS_MESSAGE")) {
			languageManager.setHasBeenUpdated(mTempVal);
		} else if (qName.equalsIgnoreCase("MY_ORDERS")) {
			languageManager.setMyOrders(mTempVal);
		} else if (qName.equalsIgnoreCase("ALL_TABLES")) {
			languageManager.setAllTables(mTempVal);
		} else if (qName.equalsIgnoreCase("ACTIVE_TABLES")) {
			languageManager.setActiveTables(mTempVal);
		} else if (qName.equalsIgnoreCase("BILL_REQUESTED_TABLES")) {
			languageManager.setBillRequestedTables(mTempVal);
		} else if (qName.equalsIgnoreCase("NOTIFICATIONS_ENABLED")) {
			languageManager.setNotificationsEnabled(mTempVal);
		} else if (qName.equalsIgnoreCase("ORDER")) {
			languageManager.setOrder(mTempVal);
		} else if (qName.equalsIgnoreCase("ENTER_VALID_PORT")) {
			languageManager.setEnterValidPort(mTempVal);
		} else if (qName.equalsIgnoreCase("GO_TO_ORDER")) {
			languageManager.setGoToOrder(mTempVal);
		} else if (qName.equalsIgnoreCase("EXIT")) {
			languageManager.setExit(mTempVal);
		} else if (qName.equalsIgnoreCase("ENTER_PIN_TO_LOGOUT")) {
			languageManager.setEnterPinToLogout(mTempVal);
		} else if (qName.equalsIgnoreCase("ENTER_KITCHEN_NOTE")) {
			languageManager.setEnterKitchenNote(mTempVal);
		} else if (qName.equalsIgnoreCase("MODIFIERS")) {
			languageManager.setModifiers(mTempVal);
		} else if (qName.equalsIgnoreCase("ALLOW_FRACTIONS")) {
			languageManager.setAllowFractions(mTempVal);
		} else if (qName.equalsIgnoreCase("PLEASE_PRESS_BACK_AGAIN")) {
			languageManager.setPleasePressBackAgain(mTempVal);
		} else if (qName.equalsIgnoreCase("DOWNLOADING_MENU")) {
			languageManager.setDownloadingMenu(mTempVal);
		} else if (qName.equalsIgnoreCase("SYNC_MENU")) {
			languageManager.setSyncMenu(mTempVal);
		} else if (qName.equalsIgnoreCase("NOTIFICATION_CENTER")) {
			languageManager.setNotificationCenter(mTempVal);
		} else if (qName.equalsIgnoreCase("REFRESHING_DATA")) {
			languageManager.setRefreshingData(mTempVal);
		} else if (qName.equalsIgnoreCase("NOTIFICATIONS")) {
			languageManager.setNotifications(mTempVal);
		} else if (qName.equalsIgnoreCase("ORDERS")) {
			languageManager.setOrders(mTempVal);
		} else if (qName.equalsIgnoreCase("TABLES")) {
			languageManager.setTables(mTempVal);
		} else if (qName.equalsIgnoreCase("INITIALIZING")) {
			languageManager.setInitializing(mTempVal);
		} else if (qName.equalsIgnoreCase("CHANGE_WAITER")) {
			languageManager.setChangeWaiter(mTempVal);
		} else if (qName.equalsIgnoreCase("LOCK")) {
			languageManager.setLock(mTempVal);
		} else if (qName.equalsIgnoreCase("OPEN_ORDERS")) {
			languageManager.setOpenOrders(mTempVal);
		} else if (qName.equalsIgnoreCase("CANNOT_DELETE_GUEST")) {
			languageManager.setCannotDeleteGuest(mTempVal);
		} else if (qName.equalsIgnoreCase("ITEM_CANNOT_BE_DELETED")) {
			languageManager.setItemCannotBeDeleted(mTempVal);
		} else if (qName.equalsIgnoreCase("SELECTED_SECTION")) {
			languageManager.setSelectedSection(mTempVal);
		} else if (qName.equalsIgnoreCase("SELECTED_LANGUAGE")) {
			languageManager.setSelectedLanguage(mTempVal);
		} else if (qName.equalsIgnoreCase("MEMORY_MESSAGE")) {
			languageManager.setMemoryMessage(mTempVal);
		} else if (qName.equalsIgnoreCase("ORDER_ADDED_SUCCESSFULLY")) {
			languageManager.setOrderAdded(mTempVal);
		} else if (qName.equalsIgnoreCase("ORDER_UPDATED_SUCCESSFULLY")) {
			languageManager.setOrderUpdated(mTempVal);
		} else if (qName.equalsIgnoreCase("ORDER_CHECKED_OUT_SUCCESSFULLY")) {
			languageManager.setCheckoutSuccess(mTempVal);
		} else if (qName.equalsIgnoreCase("GUEST_ONE_ONLY")) {
			languageManager.setOneGuestMessage(mTempVal);
		} else if (qName.equalsIgnoreCase("NO_ORDERED_ITEMS")) {
			languageManager.setNoOrderedItems(mTempVal);
		} else if (qName.equalsIgnoreCase("NO_ORDERED_ITEMS_FOR_ALL_GUESTS")) {
			languageManager.setNoOrderedItemsForAllGuests(mTempVal);
		} else if (qName.equalsIgnoreCase("ORDER_LOSS_MESSAGE")) {
			languageManager.setOrderLossMessage(mTempVal);
		} else if (qName.equalsIgnoreCase("TABLE_SINGLE")) {
			languageManager.setTableSingle(mTempVal);
		} else if (qName.equalsIgnoreCase("NO_RESULTS")) {
			languageManager.setNoResults(mTempVal);
		} else if (qName.equalsIgnoreCase("PLEASE_ENTER_A_VALID_QUANTITY")) {
			languageManager.setPleaseEnterAValidQuantity(mTempVal);
		} else if (qName.equalsIgnoreCase("PLEASE_ENTER_A_QUANTITY")) {
			languageManager.setPleaseEnterAQuantity(mTempVal);
		} else if (qName.equalsIgnoreCase("FRACTIONAL_LIMIT_VALUE")) {
			languageManager.setFractionalLimitValue(mTempVal);
		} else if (qName.equalsIgnoreCase("SELECT_CATEGORY")) {
			languageManager.setSelectCategory(mTempVal);
		} else if (qName.equalsIgnoreCase("SYNC_MENU_MESSAGE")) {
			languageManager.setSyncMenuMessage(mTempVal);
		} else if (qName.equalsIgnoreCase("CURRENCY_SYMBOL")) {
			languageManager.setCurrancySymbol(mTempVal);
		} else if (qName.equalsIgnoreCase("IS_AFTER")) {
			languageManager.IS_AFTER(mTempVal);
		} else if (qName.equalsIgnoreCase("CHOOSE")) {
			languageManager.setChoose(mTempVal);
		} else if (qName.equalsIgnoreCase("OUT_OF")) {
			languageManager.setOutOf(mTempVal);
		} else if (qName.equalsIgnoreCase("UNDER")) {
			languageManager.setUnder(mTempVal);
		} else if (qName.equalsIgnoreCase("GROUP")) {
			languageManager.setGroup(mTempVal);
		} else if (qName.equalsIgnoreCase("MIN_MODIFIERS_TO_SELECT")) {
			languageManager.setMinModifiersToSelect(mTempVal);
		} else if (qName.equalsIgnoreCase("YOU_CAN_ONLY_SELECT")) {
			languageManager.setYouCanOnlySelect(mTempVal);
		} else if (qName.equalsIgnoreCase("MODIFIERS_LAST")) {
			languageManager.setModifiersLast(mTempVal);
		} else if (qName.equalsIgnoreCase("MAX")) {
			languageManager.setMax(mTempVal);
		} else if (qName.equalsIgnoreCase("MIN")) {
			languageManager.setMin(mTempVal);
		} else if (qName.equalsIgnoreCase("REFRESH")) {
			languageManager.setRefresh(mTempVal);
		} else if (qName.equalsIgnoreCase("NON_GROUP_MODIFIERS")) {
			languageManager.setNonGroupModifiers(mTempVal);
		} else if(qName.equalsIgnoreCase("DEVICE_NOT_REGISTERED")) {
			languageManager.setDeviceNotRegistered(mTempVal);
		} else if(qName.equalsIgnoreCase("NOTIFICATIONS_ENABLED")){
			languageManager.setNotificationsEnabled(mTempVal);
		} else if(qName.equalsIgnoreCase("ON")){
			languageManager.setOn(mTempVal);
		} else if(qName.equalsIgnoreCase("OFF")){
			languageManager.setOff(mTempVal);
		} else if(qName.equalsIgnoreCase("TOTAL_HEADER")){
			languageManager.setTotalHeader(mTempVal);
		} else if(qName.equalsIgnoreCase("ORDER_HEADER")){
			languageManager.setOrderHeader(mTempVal);
		} else if(qName.equalsIgnoreCase("TABLE_HEADER")){
			languageManager.setTableHeader(mTempVal);
		} else if(qName.equalsIgnoreCase("SECTION_HEADER")){
			languageManager.setSectionHeader(mTempVal);
		} else if(qName.equalsIgnoreCase("ENTER_VALID_EXIT_PIN")) {
			languageManager.setEnterValidExitPin(mTempVal);
		}
	}
}
