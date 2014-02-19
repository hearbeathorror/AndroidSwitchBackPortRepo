package com.azilen.waiterpad.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.azilen.waiterpad.activities.SettingsActivity;
import com.azilen.waiterpad.data.Languages;
import com.azilen.waiterpad.managers.language.LanguageManager;

public class GetLanguageXmlAsyncTask extends AsyncTask<Void, Void, Languages>{
	private Context mContext;
	private String mLanguageName;
	private Languages mLanguages;
	private ProgressDialog mProgressDialog;
	private String mLoading;
	
	public GetLanguageXmlAsyncTask(Context context, 
			String languageName) {
		mContext = context;
		mLanguageName = languageName;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mLoading = LanguageManager.getInstance().getLoading();
		mProgressDialog = ProgressDialog.show(mContext,null, mLoading);	
	}
	
	@Override
	protected Languages doInBackground(Void... params) {
		mLanguages = LanguageManager.getInstance().getLanguages(mLanguageName);
		return mLanguages;
	}
	
	@Override
	protected void onPostExecute(Languages result) {
		super.onPostExecute(result);
		
		if(result != null) {
			switch (result.getResponseCode()) {
			case 1000:
				
				break;
				
			case 1004:
				
				break;
				
			case 1005:
				// store the language xml into cache
				LanguageManager.getInstance().storeLangugagesIntoCache(result, 
						mLanguageName);
				
				((SettingsActivity)mContext).refreshGUI(mLanguageName);
				break;

			default:
				break;
			}
		}
		
		mProgressDialog.dismiss();
	}
}
