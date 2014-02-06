package com.azilen.insuranceapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.azilen.insuranceapp.R;
import com.azilen.insuranceapp.activities.MainTabFragmentActivity;
import com.azilen.insuranceapp.app.InsuranceAppApplication;
import com.azilen.insuranceapp.utils.Global;
import com.azilen.insuranceapp.utils.Prefs;

import de.ankri.views.Switch;

/**
 * Fragment loaded when the user is logged in and even when not
 * For the settings tab
 * @author dhara.shah
 *
 */
public class SettingsFragment extends Fragment implements OnClickListener, OnItemSelectedListener, OnCheckedChangeListener{
	private TextView mTxtUserName;
	TextView login ; 
	Spinner spinnerNoOfEvents; 
	private String userName;
	private Integer[] numberOfEventsArray;
	private Switch mSyncVideosSwitch;
	private Switch mSyncVideosOverWifi;
	private boolean isLoggedIn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View setting_view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_settings,
				null);
		login = (TextView) setting_view.findViewById(R.id.txtLogin);
		mTxtUserName = (TextView)setting_view.findViewById(R.id.txtNameSettings);
		spinnerNoOfEvents = (Spinner) setting_view.findViewById(R.id.spinnerNumbOfEventsToShow);
		mSyncVideosOverWifi = (Switch)setting_view.findViewById(R.id.switchSyncVidOverWifi);
		mSyncVideosSwitch = (Switch)setting_view.findViewById(R.id.switchSyncVideos);
		
		numberOfEventsArray = new Integer[10];
		 for(int i=0;i<10;i++) {
			 numberOfEventsArray[i]=  i+1;
		 }
	        
		 ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getActivity(),
				 android.R.layout.simple_spinner_item, numberOfEventsArray);
		 spinnerNoOfEvents.setAdapter(adapter);
		
		isLoggedIn = getArguments().getBoolean("login");
		
		if(getArguments() != null) {
			userName = getArguments().getString(Global.USERNAME);
		}
		
		setGui();
		
		login.setOnClickListener(this);
		((MainTabFragmentActivity)getActivity()).changeTitle(getString(R.string.settings));
		
		spinnerNoOfEvents.setOnItemSelectedListener(this);
		mSyncVideosOverWifi.setOnCheckedChangeListener(this);
		mSyncVideosSwitch.setOnCheckedChangeListener(this);
		
		return setting_view;
	}
	
	private void setGui() {
		int position = getSelectedValuePosition();
		if(position == -1) {
			spinnerNoOfEvents.setSelection(0);
		}else {
			spinnerNoOfEvents.setSelected(true);
			spinnerNoOfEvents.setSelection(position);
		}
		
		if(userName != null) {
			mTxtUserName.setText(userName.toUpperCase());
		}
		
		if(isLoggedIn) {
			login.setText(getActivity().getString(R.string.logout));
			((MainTabFragmentActivity)getActivity()).setTabsAccessibility(true);
		}
		else
			login.setTag("Log in");
		
		switch (Prefs.getKey_int(getActivity(), Prefs.USER_TYPE)) {
		// pink
		case 0:
			mSyncVideosSwitch.setThumbResource(R.drawable.switch_inner_holo_dark_pink);
			mSyncVideosOverWifi.setThumbResource(R.drawable.switch_inner_holo_dark_pink);
			
			mSyncVideosSwitch.setTrackResource(R.drawable.switch_track);
			mSyncVideosOverWifi.setTrackResource(R.drawable.switch_track);
			break;
		
		// blue
		case 1:
			mSyncVideosSwitch.setThumbResource(R.drawable.switch_inner_holo_dark_blue);
			mSyncVideosOverWifi.setThumbResource(R.drawable.switch_inner_holo_dark_blue);
			
			mSyncVideosSwitch.setTrackResource(R.drawable.switch_track);
			mSyncVideosOverWifi.setTrackResource(R.drawable.switch_track);
			break;
			
		default:
			mSyncVideosSwitch.setThumbResource(R.drawable.switch_inner_holo_dark_black);
			mSyncVideosOverWifi.setThumbResource(R.drawable.switch_inner_holo_dark_black);
			
			mSyncVideosSwitch.setTrackResource(R.drawable.switch_track_holo_dark_black);
			mSyncVideosOverWifi.setTrackResource(R.drawable.switch_track_holo_dark_black);
			break;
		}
		
		mSyncVideosOverWifi.setChecked(Prefs.getKey_boolean(getActivity(), Prefs.SYNC_VIDEOS_OVER_WIFI));
		mSyncVideosSwitch.setChecked(Prefs.getKey_boolean(getActivity(), Prefs.SYNC_VIDEOS));
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(getActivity() != null) {
			if(Prefs.getKey_boolean(getActivity(), Prefs.IS_LOGGED_IN)) {
				login.setText(getActivity().getString(R.string.logout));
				mTxtUserName.setVisibility(View.VISIBLE);
				
				// set name here
				if(userName != null) {
					mTxtUserName.setText(userName.toUpperCase());
				}
			}else {
				login.setText(getActivity().getString(R.string.log_in));
				mTxtUserName.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txtLogin:
			if(login.getText().toString().equalsIgnoreCase(getActivity().getString(R.string.log_in))) {
				// log in
				((MainTabFragmentActivity)getActivity()).loadFragment(new LogInFragment());
			}
			else {
				// log Out
				((MainTabFragmentActivity)getActivity()).loadFragment(new HomeFragment());
				Prefs.addKey(getActivity(), Prefs.IS_LOGGED_IN, false);
				
				Prefs.removeKey(getActivity(), Prefs.USER_ID);
				Prefs.removeKey(getActivity(), Prefs.USER_NAME);
				Prefs.removeKey(getActivity(), Prefs.USER_PASSWORD);
				
				((MainTabFragmentActivity)getActivity()).updateTopLoginPress(false);
				((InsuranceAppApplication)InsuranceAppApplication.getAppContext()).refreshSettings();
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// these settings are device based and not user based
		Prefs.addKey(getActivity(), Prefs.NO_OF_EVENTS_TO_SHOW, numberOfEventsArray[position]);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}
	
	private int getSelectedValuePosition() {
		if(numberOfEventsArray != null) {
			for(int i=0;i<numberOfEventsArray.length;i++) {
				if(Prefs.getKey_int(getActivity(), Prefs.NO_OF_EVENTS_TO_SHOW) == 
						numberOfEventsArray[i]) {
					return i;
				}else {
					continue;
				}
			}
		}
		return -1;
	}

	@Override
	public void onCheckedChanged(CompoundButton button, boolean isChecked) {
		switch (button.getId()) {
		case R.id.switchSyncVideos:
			mSyncVideosSwitch.setChecked(isChecked);
			Prefs.addKey(getActivity(), Prefs.SYNC_VIDEOS, isChecked);
			break;
			
		case R.id.switchSyncVidOverWifi:
			mSyncVideosOverWifi.setChecked(isChecked);
			Prefs.addKey(getActivity(), Prefs.SYNC_VIDEOS_OVER_WIFI, isChecked);
			break;

		default:
			break;
		}
	}
}
