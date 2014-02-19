package com.azilen.waiterpad.asynctask;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.azilen.waiterpad.WaiterPadApplication;
import com.azilen.waiterpad.data.Item;
import com.azilen.waiterpad.data.WholeMenu;
import com.azilen.waiterpad.managers.language.LanguageManager;
import com.azilen.waiterpad.managers.menu.MenuManager;
import com.azilen.waiterpad.managers.network.NetworkManager;
import com.azilen.waiterpad.managers.network.RequestType;
import com.azilen.waiterpad.managers.network.ServiceUrlManager;
import com.azilen.waiterpad.utils.Prefs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GetOnlyMenuAsyncTask extends AsyncTask<Void, Void, WholeMenu>{
	private ProgressDialog mProgressDialog;
	private Context mContext;
	private String mSectionId;
	private WholeMenu mWholeMenu;
	private String TAG = this.getClass().getSimpleName();
	
	public GetOnlyMenuAsyncTask(Context context) {
		mContext = context;
		mSectionId =Prefs.getKey(context, Prefs.SECTION_ID);
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		String message = LanguageManager.getInstance().getDownloadingMenu();
		mProgressDialog = ProgressDialog.show(mContext,null, message);
	}
	
	@Override
	protected WholeMenu doInBackground(Void... params) {
		getMenuData();
		return mWholeMenu;
	}
	
	@Override
	protected void onPostExecute(WholeMenu result) {
		if(result != null) {
			// store it in the database
			MenuManager.getInstance().deleteFromAllTables();
			MenuManager.getInstance().storeDataIntoDatabase(mWholeMenu);
		}
		
		mProgressDialog.dismiss();
	}
	
	private WholeMenu getMenuData() {
		String url = ServiceUrlManager.getInstance().getServiceUrlByType(RequestType.GET_MENU_ALL);
		String menuResponse = NetworkManager.getInstance().performGetRequest(url);
		
		//InputStream is = NetworkManager.getInstance().getInsputStreamGetRequest(url);
		
		if(menuResponse == null) {
			return null;
		}else {
			try {
				/*GsonBuilder gsonBuilder = new GsonBuilder();
				gsonBuilder.registerTypeAdapter(Item.ItemType.class, new MenuManager.MenuDeserializer());
				Gson gson = gsonBuilder.create();
				
				Reader reader = new InputStreamReader(is);
				
				mWholeMenu = gson.fromJson(reader,WholeMenu.class);*/
				mWholeMenu = getDataFromGson(menuResponse);
			}catch(Exception e) {
				e.printStackTrace();
				WaiterPadApplication.LOG.debug(TAG + " \n\n" + e.getMessage() + "\n\n" + String.valueOf(e.getCause()));
			}
			return mWholeMenu;
		}
	
	}
	
	private WholeMenu getDataFromGson(String response) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Item.ItemType.class, new MenuManager.MenuDeserializer());
		Gson gson = gsonBuilder.create();
		mWholeMenu = gson.fromJson(response, WholeMenu.class);
		
		WaiterPadApplication.LOG.debug(TAG + " menu list obtained !!! ");
		WaiterPadApplication.LOG.debug(TAG + " menu list value : " + mWholeMenu.toString());
		
		return mWholeMenu;
	}
}
