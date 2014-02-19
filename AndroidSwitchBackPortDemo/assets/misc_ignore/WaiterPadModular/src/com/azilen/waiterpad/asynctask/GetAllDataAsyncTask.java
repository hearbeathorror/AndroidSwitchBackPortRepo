package com.azilen.waiterpad.asynctask;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.azilen.waiterpad.WaiterPadApplication;
import com.azilen.waiterpad.activities.BaseActivity;
import com.azilen.waiterpad.activities.LoginActivity;
import com.azilen.waiterpad.activities.MyOrderActivity;
import com.azilen.waiterpad.data.Item;
import com.azilen.waiterpad.data.Languages;
import com.azilen.waiterpad.data.Order;
import com.azilen.waiterpad.data.OrderList;
import com.azilen.waiterpad.data.OrderedItem;
import com.azilen.waiterpad.data.SectionTable;
import com.azilen.waiterpad.data.SectionWiseTable;
import com.azilen.waiterpad.data.Tables;
import com.azilen.waiterpad.data.WholeMenu;
import com.azilen.waiterpad.managers.language.LanguageManager;
import com.azilen.waiterpad.managers.menu.MenuManager;
import com.azilen.waiterpad.managers.network.NetworkManager;
import com.azilen.waiterpad.managers.network.RequestType;
import com.azilen.waiterpad.managers.network.ServiceUrlManager;
import com.azilen.waiterpad.managers.order.OrderManager;
import com.azilen.waiterpad.managers.section.SectionManager;
import com.azilen.waiterpad.managers.settings.SettingsParser;
import com.azilen.waiterpad.service.GetDataAlarmService;
import com.azilen.waiterpad.utils.Global;
import com.azilen.waiterpad.utils.Prefs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GetAllDataAsyncTask extends AsyncTask<Void, Void, Void>{
	private ProgressDialog mProgressDialog;
	private Context mContext;
	private String mFrom;
	
	private OrderList mOrderList;
	private SectionWiseTable mSectionWiseTable;
	private Languages mLanguages;
	private Languages mLanguageXml;
	private String mParameterSent;
	private String language;
	
	private WholeMenu mWholeMenu;
	
	private boolean networkProblem = false;
	
	private String TAG = this.getClass().getSimpleName();
	
	public GetAllDataAsyncTask(Context context, String from){
		mContext = context;
		mFrom = from;
		mParameterSent = "List";
		language =Prefs.getKey(context, Prefs.LANGUAGE_SELECTED);
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		String sectionId = "";
		int countOfItems = 0;
		
		// store all the datas
		getSettings();
		getTableList();
		getOrderData();
		
		if(mSectionWiseTable != null) {
			if(Prefs.getKey(mContext, Prefs.SECTION_ID) != null && 
					Prefs.getKey(mContext, Prefs.SECTION_ID).trim().length() > 0) {
				// do not do anything
				sectionId =Prefs.getKey(mContext, Prefs.SECTION_ID);
				Log.i("dhara","sectionId from getAllDataAsyncTask : " + sectionId);
			}else {
				List<SectionTable> lst = mSectionWiseTable.getSectionTables();
				SectionTable sectionTable = null;
				if(lst != null && lst.size() > 0) {
					sectionTable = lst.get(0);
					Prefs.addKey(mContext, Prefs.SECTION_ID, sectionTable.getId());
					Prefs.addKey(mContext, Prefs.SECTION_NAME, sectionTable.getSectionName());
				}
			}
		}
		
		
		if(mFrom != null && mFrom.equalsIgnoreCase("settings")) {
			// dont get the data if its from the settings 
		}else {
			if(sectionId != null && sectionId.trim().length() > 0) {
				// section id there stored
				// get the count of items from the db
				countOfItems = MenuManager.getInstance().getCountOfItems(sectionId);
			}
			
			if(countOfItems <= 0) {
				try{
					Log.i(TAG, "time when it is called !" + new Date());
					getAllMenu();
					Log.i(TAG, "time when it gets all the data !" + new Date());
				}catch(OutOfMemoryError e) {
					WaiterPadApplication.LOG.debug(e.getMessage() + "\n" + e.getStackTrace());
				}
			}
		}
		
		mLanguages = LanguageManager.getInstance().getLanguages(mParameterSent);
		
		// indication that the language is not there
		int position = -1;
		
		if(mLanguages != null && mLanguages.getResponseCode() == 1004) {
			if(language != null 
					&& language.trim().length() >0) {
				for(int i=0;i<mLanguages.getLanguages().size();i++) {
					if(language.equals(mLanguages.getLanguages().get(i))) {
						position = i;
						break;
					}
				}
				
				if(position != -1) {
					mLanguageXml = LanguageManager.getInstance().getLanguages(language);
				}
			}
		}
		
		return null;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		String message = "";
		
		if(mFrom != null && mFrom.equalsIgnoreCase("settings")) {
			message = LanguageManager.getInstance().getRefreshingData();
		}else {
			message = LanguageManager.getInstance().getDownloadingMenu();
		}
		
		if(mFrom.equalsIgnoreCase("LoginActivity"))
		{
			if(((LoginActivity)mContext).getProgressDialog()!=null)
				mProgressDialog=((LoginActivity)mContext).getProgressDialog();	
				mProgressDialog.setMessage(message);
						
		}else
		{
			mProgressDialog = ProgressDialog.show(mContext, null,message);
		}
	}
	
	/**
	 * Gets the order data
	 * @return
	 */
	private OrderList getOrderData() {
		String url = ServiceUrlManager.getInstance().getServiceUrlByType(RequestType.GET_CURRENT_ORDERS);
		String orderResponse = NetworkManager.getInstance().performGetRequest(url);
		
		Log.i(TAG, "url called!!! " + url);
		
		if(orderResponse == null) {
			networkProblem=true;
			return null;
		}else {
			try {
				networkProblem = false;
				GsonBuilder gsonBuilder = new GsonBuilder();
				
				// deserializers for the enum values
				gsonBuilder.registerTypeAdapter(Order.OrderStatus.class, new OrderManager.OrderStatusDeserializer());
				gsonBuilder.registerTypeAdapter(OrderedItem.OrderedItemStatus.class,new OrderManager.OrderedItemStatusDeserializer());
				gsonBuilder.registerTypeAdapter(Tables.TableType.class, new SectionManager.TableTypeDeserializer());
				
				Gson gson = gsonBuilder.create();
				mOrderList = gson.fromJson(orderResponse, OrderList.class);
				
				Log.i(TAG, "value : " + mOrderList.toString());
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return mOrderList;
	}
	
	/**
	 * Added on: 11th December 2013, 5:43pm
	 * gets the settings from the server
	 */
	private void getSettings() {
		String url = ServiceUrlManager.getInstance().getServiceUrlByType(RequestType.GET_SETTINGS);
		String settingsResponse = NetworkManager.getInstance().performGetRequest(url);
		
		if(settingsResponse != null) {
			String responseToParse = null;
			
			// that means that the string returned is a
			// settings xml
			if(settingsResponse.contains("XML: ")) {
				responseToParse = settingsResponse.replace("XML: ","");
				responseToParse = responseToParse.replace("\\/", "/");
				if(responseToParse != null) {
					SettingsParser settingsParser = 
							new SettingsParser(mContext,responseToParse.trim());
					settingsParser.parseDocument();
				}
			}
		}
	}
	
	

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		
		// Checks if the current order list is empty or not
		if(mOrderList != null) {
			OrderManager.getInstance().storeCurrentOrdersInMemory(mOrderList);
		}else {
			if(networkProblem) {
				// there was a network problem
			}else {
				// clear the orders present already
				OrderManager.getInstance().getOrderCache().remove(Global.ORDER_PER_WAITER);
				OrderManager.getInstance().getOrderCache().remove(Global.PER_TABLE_ORDERS);
				OrderManager.getInstance().getOrderCache().remove(Global.BILL_REQUEST);
				OrderManager.getInstance().getOrderCache().remove(Global.RUNNING_ORDERS);
			}
		}
		
		/*// checks if the menu list obtained is empty or not
		if(mSectionMenuList != null) {
			if(mSectionMenuList.size() > 0) {
				if(mFrom.equalsIgnoreCase(Global.FROM_LOGIN_ACTIVITY)) {
					Prefs.addKey(mContext, Prefs.SECTION_ID, mSectionMenuList.get(0).getId());
					Prefs.addKey(mContext, Prefs.SECTION_NAME,mSectionMenuList.get(0).getName());
				}
				memCache.put(Global.SECTION_WISE_MENU, mSectionMenuList);
				mUtils.getMenuFromCache(mSectionMenuList);
			}
		}*/
		
		// checks if the section table is empty or not
		if(mSectionWiseTable != null) {
			SectionManager.getInstance().storeTableDataIntoCache(mSectionWiseTable);
		}
		
		if(mWholeMenu != null) {
			MenuManager.getInstance().storeDataIntoDatabase(mWholeMenu);
		}

		// checks if the languages model is empty or not
		if(mLanguages != null) {
			Log.i(TAG, "value of mLanguages inside async: " + 
					mLanguages.getLanguages() + 
					mLanguages.getResponseCode());
			LanguageManager.getInstance().storeLangugagesIntoCache(mLanguages, mParameterSent);
		}
		
		if(mLanguageXml != null) {
			LanguageManager.getInstance().storeLangugagesIntoCache(mLanguageXml,language);
		}
		
		/*if(!Prefs.getKey_boolean_with_default_true(mContext,Prefs.ARE_NOTIFICATIONS_ENABLED)) {
			Prefs.addKey(mContext, Prefs.ARE_NOTIFICATIONS_ENABLED,true);
		}*/
		
		if(mProgressDialog != null) {
			mProgressDialog.dismiss();
			
			if(mFrom.equalsIgnoreCase(Global.FROM_LOGIN_ACTIVITY)){
				((LoginActivity)mContext).setProgressDialog(null);	
							
			}
			
		}
		
		if(!GetDataAlarmService.isServiceStarted) {
			((BaseActivity)mContext).startServices(mContext);
			mContext.startService(new Intent(mContext, GetDataAlarmService.class));
		}
		
		if(mFrom.equalsIgnoreCase(Global.FROM_SETTINGS)) {
			// do nothing its from the settings activity
		}else if(mFrom.equalsIgnoreCase(Global.FROM_LOGIN_ACTIVITY)) {
			// redirect the user to the ,my orders activity
			Intent intent = new Intent(mContext,MyOrderActivity.class);
			intent.putExtra(Global.FROM_ACTIVITY, Global.FROM_LOGIN);
			mContext.startActivity(intent);
			
			Global.activityStartAnimationRightToLeft(mContext);
			((Activity)mContext).finish();
		}
	}
	
	private WholeMenu getAllMenu() {
		String url = ServiceUrlManager.getInstance().getServiceUrlByType(RequestType.GET_MENU_ALL);
		String menuResponse = NetworkManager.getInstance().performGetRequest(url);
		
		if(menuResponse == null) {
			return null;
		}else {
			try {
				mWholeMenu = getAllDataFromGson(menuResponse);
				return mWholeMenu;
			}catch(Exception e) {
				e.printStackTrace();
				WaiterPadApplication.LOG.debug(TAG + " \n\n" + e.getMessage() + "\n\n" + String.valueOf(e.getCause()));
			}
			return mWholeMenu;
		}
	}
	
	/**
	 * Gets the section wise table data
	 * @return
	 */
	private SectionWiseTable getTableList() {
		String url = ServiceUrlManager.getInstance().getServiceUrlByType(RequestType.GET_SECTIONWISE_TABLE);
		String sectionWiseResponse = NetworkManager.getInstance().performGetRequest(url);
		
		if(sectionWiseResponse == null) {
			return null;
		}else {
			try {
				GsonBuilder gsonBuilder = new GsonBuilder();
				gsonBuilder.registerTypeAdapter(Tables.TableType.class, new SectionManager.TableTypeDeserializer());
				Gson gson = gsonBuilder.create();
				mSectionWiseTable = gson.fromJson(sectionWiseResponse, SectionWiseTable.class);
			}catch(Exception e) {
				e.printStackTrace();
			}
			return mSectionWiseTable;
		}
	}
	
	private WholeMenu getAllDataFromGson(String response) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Item.ItemType.class, new MenuManager.MenuDeserializer());
		Gson gson = gsonBuilder.create();
		mWholeMenu = gson.fromJson(response,WholeMenu.class);
		return mWholeMenu;
	}
}
