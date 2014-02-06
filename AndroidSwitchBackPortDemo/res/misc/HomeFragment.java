package com.azilen.insuranceapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.azilen.insuranceapp.R;
import com.azilen.insuranceapp.activities.MainTabFragmentActivity;
import com.azilen.insuranceapp.utils.Prefs;

/**
 * Displays the first screen with a single login button
 * The home screen, clicking on the login button set userType to 0
 * @author dhara.shah
 *
 */
public class HomeFragment extends Fragment implements OnClickListener{
	
	Button home_login ; 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View home_view = LayoutInflater.from(getActivity()).inflate(R.layout.home_page,
				null);
		home_login =  (Button) home_view.findViewById(R.id.buttonLogInHome);
		home_login.setOnClickListener(this);
			
		// Add values to the shared preferences
		Prefs.addKey(getActivity(),Prefs.IS_LOGGED_IN, false);
		Prefs.addKey(getActivity(), Prefs.IS_SUPER_LOGIN_CLICKED, false);
		Prefs.addKey(getActivity(), Prefs.USER_TYPE, -1);
		
		((MainTabFragmentActivity)getActivity()).setTabsAccessibility(false);
		
		return home_view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(getView() != null) {
			((MainTabFragmentActivity)getActivity()).changeTitle(getString(R.string.settings));
			((MainTabFragmentActivity)getActivity()).hideActionBar();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonLogInHome:
			// loads the login screen with the userType set to 0
			((MainTabFragmentActivity)getActivity()).loadLoginScreen(0);
			((MainTabFragmentActivity)getActivity()).updateTopLoginPress(true);
			break;

		default:
			break;
		}
		
	}

}
