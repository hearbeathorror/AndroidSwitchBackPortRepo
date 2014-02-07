package com.azilen.insuranceapp.activities;

import java.util.HashMap;
import java.util.Stack;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.azilen.insuranceapp.R;
import com.azilen.insuranceapp.app.InsuranceAppApplication;
import com.azilen.insuranceapp.fragments.HomeFragment;
import com.azilen.insuranceapp.fragments.LogInFragment;
import com.azilen.insuranceapp.fragments.MyTeamDetailFragment;
import com.azilen.insuranceapp.fragments.MyTeamFragment;
import com.azilen.insuranceapp.fragments.MyVideoDetailFragment;
import com.azilen.insuranceapp.fragments.MyVideoFragment;
import com.azilen.insuranceapp.fragments.PlannedEventDetailFragment;
import com.azilen.insuranceapp.fragments.PlannedEventsFragment;
import com.azilen.insuranceapp.fragments.SettingsFragment;
import com.azilen.insuranceapp.utils.Global;
import com.azilen.insuranceapp.utils.Logger;
import com.azilen.insuranceapp.utils.Logger.modules;
import com.azilen.insuranceapp.utils.Prefs;

/**
 * Handles all the operations related to the fragments
 * Such as replace and pop of fragments on tab change or 
 * back press. (For the tabs at the bottom)
 * @author dhara.shah
 *
 */
public class MainTabFragmentActivity extends BaseFragmentActivity {
	public static TabHost mTabHost;
	public static TabManager mTabManager;
	public static int selectedTab = 3;
	private TextView txtHeader;
	private RelativeLayout mRelHeader;

	private PlannedEventsFragment mPlannedEventsFragment;
	private MyVideoFragment mVideoFragment;
	private MyTeamFragment mMyTeamFragment;
	private SettingsFragment mSettingsFragment;

	private static final String STACK_PLANNED_EVENTS = "PLANNED EVENTS";
	private static final String STACK_MY_VIDEOS = "MY VIDEOS";
	private static final String STACK_MY_TEAM = "MY TEAM";
	private static final String STACK_SETTINGS = "SETTINGS";

	private static Stack<Fragment> plannedEventsStack;
	private static Stack<Fragment> myVideosStack;
	private static Stack<Fragment> myTeamStack;
	private static Stack<Fragment> settingsStack;

	private static int userType = -1;
	private static boolean isFromResume = false;
	private static boolean isTopLoginPressed = false;
	
	private static final String APP_STATE = "AppState";

	private String TAG = this.getClass().getSimpleName();

	@Override
	public void onBackPressed() {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);

		// checks the current tab selected 
		// and accordingly pops the fragment from the stack
		switch (mTabHost.getCurrentTab()) {
		case 0:
			if (plannedEventsStack.size() > 1) {
				ft.remove(MainTabFragmentActivity.plannedEventsStack.pop());
				MainTabFragmentActivity.plannedEventsStack.lastElement().onResume();
				ft.show(MainTabFragmentActivity.plannedEventsStack.lastElement());
				changeTitle(STACK_PLANNED_EVENTS);
				ft.commit();
			} else {
				callBackPress();
			}
			break;

		case 1:
			if (myVideosStack.size() > 1) {
				ft.remove(MainTabFragmentActivity.myVideosStack.pop());
				MainTabFragmentActivity.myVideosStack.lastElement().onResume();
				ft.show(MainTabFragmentActivity.myVideosStack.lastElement());
				changeTitle(STACK_MY_VIDEOS);
				ft.commit();
			} else {
				callBackPress();
			}
			break;

		case 2:
			if (myTeamStack.size() > 1) {
				ft.remove(MainTabFragmentActivity.myTeamStack.pop());
				MainTabFragmentActivity.myTeamStack.lastElement().onResume();
				ft.show(MainTabFragmentActivity.myTeamStack.lastElement());
				changeTitle(STACK_MY_TEAM);
				ft.commit();
			} else {
				callBackPress();
			}
			break;

		case 3:
			if (settingsStack.size() > 1) {
				ft.remove(MainTabFragmentActivity.settingsStack.pop());
				MainTabFragmentActivity.settingsStack.lastElement().onResume();
				ft.show(MainTabFragmentActivity.settingsStack.lastElement());
				changeTitle(STACK_SETTINGS);
				ft.commit();

				if(settingsStack.size() == 1) {
					isTopLoginPressed = false;
					((InsuranceAppApplication)InsuranceAppApplication.getAppContext()).refreshSettings();
					userType = -1;
					hideActionBar();
					setActionBarColor();
					setTabsAccessibility(false);
				}
			} else {
				/*if(!Prefs.getKey_boolean(MainTabFragmentActivity.this, Prefs.IS_LOGGED_IN)) {
					super.onBackPressed();
					((InsuranceAppApplication)InsuranceAppApplication.getAppContext()).refreshSettings();
				}*/
				callBackPress();
			}
			break;
		}
	}
	
	private void callBackPress() {
		Prefs.removeKey(MainTabFragmentActivity.this, Prefs.CURRENT_TAB_SELECTED);
		super.onBackPressed();
		overridePendingTransition(R.anim.activity_finish_in_anim, R.anim.activity_finish_out_anim);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Prefs.addKey(Prefs.CURRENT_TAB_SELECTED, String.valueOf(getCurrentTab()));
		selectedTab = getCurrentTab();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(APP_STATE,getCurrentTab());
		Prefs.addKey(Prefs.CURRENT_TAB_SELECTED, String.valueOf(getCurrentTab()));
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab_layout);

		if(savedInstanceState != null) {
			isFromResume = true;
		}
		
		showActionbar();
		createTabHost();
		
		if(savedInstanceState != null) {
			mTabHost.setCurrentTab(savedInstanceState.getInt(APP_STATE));
			isFromResume = true;
		}else {
			mTabHost.setCurrentTab(3);
		}
		
		setTabSelectors();
		setTabsAccessibility(false);
		getSupportActionBar().hide();
		Prefs.addKey(MainTabFragmentActivity.this, Prefs.USER_TYPE, userType);
		Prefs.addKey(Prefs.LOW_BATTERY, "false");
		
		
	}
	
	/**
	 * Updates the topLogin flag to prevent the login screen
	 * to be opened from the bottom
	 * @param isPressed
	 */
	public void updateTopLoginPress(boolean isPressed) {
		isTopLoginPressed = isPressed;
	}

	/**
	 * Creates the tabs at the bottom of the screen
	 */
	private void createTabHost(){
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);

		plannedEventsStack = new Stack<Fragment>();
		myVideosStack = new Stack<Fragment>();
		myTeamStack = new Stack<Fragment>();
		settingsStack = new Stack<Fragment>();

		mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);
		mTabHost.setup();

		// the tab listener
		// loads the proper fragment when a tab is selected
		// and changes the title accordingly
		mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				FragmentManager fm = getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();

				hideTabs(ft);
				int index = 3;
				
				// Checks if the isFromResume flag is set
				// if true, then the title is set once again
				// flag set when the MapActivity or VideoViewActivity is called
				// to prevent the title from going blank
				
				if(isFromResume) {
					if(Prefs.getKey(Prefs.CURRENT_TAB_SELECTED).trim().length() > 0) {
						index = Integer.valueOf(Prefs.getKey(Prefs.CURRENT_TAB_SELECTED));
					}else {
						index = selectedTab;
					}

					switch (selectedTab) {
					case 0:
						tabId =STACK_PLANNED_EVENTS;
						break;

					case 1:
						tabId =STACK_MY_VIDEOS;
						break;

					case 2:
						tabId =STACK_MY_TEAM;
						break;

					case 3:
						tabId = STACK_SETTINGS;
						break;

					default:
						break;
					}
					isFromResume = false;
				} 
				
				// the cases below sets the fragment
				if (tabId.equals(STACK_PLANNED_EVENTS)) {
					if (plannedEventsStack.size() == 0) {
						mPlannedEventsFragment = new PlannedEventsFragment();
						ft.add(R.id.realtabcontent, mPlannedEventsFragment);
						plannedEventsStack.push(mPlannedEventsFragment);
					} else {
						Fragment fragment = plannedEventsStack.lastElement();
						
						if(fragment instanceof PlannedEventsFragment) {
							// call the webservice again
							((PlannedEventsFragment)fragment).callWebService();
						}
						
						fragment.onResume();
						ft.show(fragment);
					}
					index = 0;
				} else if (tabId.equals(STACK_MY_VIDEOS)) {
					if (myVideosStack.size() == 0) {
						mVideoFragment = new MyVideoFragment();
						ft.add(R.id.realtabcontent, mVideoFragment);
						myVideosStack.push(mVideoFragment);
					} else {
						Fragment fragment = myVideosStack.lastElement();
						
						if(fragment instanceof MyVideoFragment) {
							if(Prefs.getKey_boolean(MainTabFragmentActivity.this, Prefs.IS_LOGGED_IN)) {
								// call the webservice again
								((MyVideoFragment)fragment).callWebService();
							}
						}
						
						fragment.onResume();
						ft.show(fragment);
					}
					index = 1;
				} else if (tabId.equals(STACK_MY_TEAM)) {
					if (myTeamStack.size() == 0) {
						mMyTeamFragment = new MyTeamFragment();
						ft.add(R.id.realtabcontent, mMyTeamFragment);
						myTeamStack.push(mMyTeamFragment);
					} else {
						Fragment fragment = myTeamStack.lastElement();
						
						if(fragment instanceof MyTeamFragment) {
							if(Prefs.getKey_boolean(MainTabFragmentActivity.this, Prefs.IS_LOGGED_IN)) {
								// call the webservice again
								((MyTeamFragment)fragment).callWebService();
							}
						}
						
						fragment.onResume();
						ft.show(fragment);
					}
					index = 2;
				} else {
					Logger.i(modules.INSURANCE_APP, TAG, "tabId " + tabId);
					Logger.i(modules.INSURANCE_APP, TAG, "tabs tapped on");
					if (settingsStack.size() == 0) {
						// the settings fragment is not loaded
						// but the home fragment since that is the first screen to show
						if(Prefs.getKey(Prefs.USER_ID).trim().length() <= 0) {
							// not logged in
							HomeFragment homeFragment = new HomeFragment();

							ft.add(R.id.realtabcontent, homeFragment);
							settingsStack.push(homeFragment);
						}else {
							// logged in and not logged out
							// changes as on 17th January 2014
							mSettingsFragment = new SettingsFragment();
							
							Bundle bundle = new Bundle();
							bundle.putBoolean("login", true);
							bundle.putString(Global.USERNAME,Prefs.getKey(Prefs.USER_NAME));
							mSettingsFragment.setArguments(bundle);

							userType = Prefs.getKey_int(MainTabFragmentActivity.this, Prefs.USER_TYPE);

							ft.add(R.id.realtabcontent, mSettingsFragment);
							settingsStack.push(mSettingsFragment);
						}
					} else {
						Fragment fragment = settingsStack.lastElement();
						fragment.onResume();
						ft.show(fragment);
					}
					index = 3;
				}
				mTabHost.setCurrentTab(index);
				selectedTab = index;
				changeTitle(tabId);
				ft.commit();
			}
		});

		mTabManager.addTab(
				mTabHost.newTabSpec(STACK_PLANNED_EVENTS), 
				null, null,
				PlannedEventsFragment.class, null);

		mTabManager.addTab(
				mTabHost.newTabSpec(STACK_MY_VIDEOS),
				null, null, MyVideoFragment.class, null);

		mTabManager.addTab(
				mTabHost.newTabSpec(STACK_MY_TEAM),
				null, null, MyTeamFragment.class, null);

		mTabManager.addTab (
				mTabHost.newTabSpec(STACK_SETTINGS),
				null, null, HomeFragment.class, null);

		setTabsAccessibility(false);
		mTabHost.getTabWidget().getChildAt(2).setVisibility(View.GONE);
		mTabHost.setCurrentTab(3);

		// A tab can only be tapped on once
		// as per the requirement the bottom tab Settings can be clicked on again
		// to open the login screen for the analyst
		// Hence the click event
		ImageView imageIcon = (ImageView)mTabHost.getTabWidget().getChildAt(3).findViewById(R.id.icon);
		imageIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// it has been clicked before
				if(!Prefs.getKey_boolean(MainTabFragmentActivity.this, Prefs.IS_SUPER_LOGIN_CLICKED) &&
						!Prefs.getKey_boolean(MainTabFragmentActivity.this, Prefs.IS_LOGGED_IN)) {
					Prefs.addKey(MainTabFragmentActivity.this, Prefs.IS_SUPER_LOGIN_CLICKED, true);
					// the user has not tapped on the top login button
					if(!isTopLoginPressed) {
						loadLoginScreen(1);
						setTabsAccessibility(false);
					}
				}else {
					// just load the settings fragment
					mTabHost.setCurrentTab(3);
					selectedTab = 3;
				}
			}
		});
	}
	
	/**
	 * Hides all the fragments first of all, if incase any fragment is loaded
	 * This is so that fragments would not overlap each other
	 * @param ft
	 */
	private void hideTabs(FragmentTransaction ft) {
		if (!plannedEventsStack.isEmpty()){
			Fragment fragment = plannedEventsStack.lastElement();
			fragment.onPause();
			ft.hide(fragment);
		}

		if (!myVideosStack.isEmpty()){
			Fragment fragment = myVideosStack.lastElement();
			fragment.onPause();
			ft.hide(fragment);
		}

		if (!myTeamStack.isEmpty()){
			Fragment fragment = myTeamStack.lastElement();
			fragment.onPause();
			ft.hide(fragment);
		}

		if (!settingsStack.isEmpty()){
			Fragment fragment = settingsStack.lastElement();
			fragment.onPause();
			ft.hide(fragment);
		}
	}

	/**
	 * Displays the appropriate tabs based on the userType
	 * userType: -1 is default
	 * 0: The inspector
	 * 1: The analyst
	 * Resources also set for the tabs
	 * @param isEnabled
	 */
	public void setTabsAccessibility(boolean isEnabled) {
		mTabHost.getTabWidget().getChildAt(0).setEnabled(isEnabled);
		mTabHost.getTabWidget().getChildAt(1).setEnabled(isEnabled);
		mTabHost.getTabWidget().getChildAt(2).setEnabled(isEnabled);

		ImageView imgIcon = 
				(ImageView)mTabHost.getTabWidget().getChildAt(0).findViewById(R.id.icon);
		imgIcon.setClickable(false);

		imgIcon = 
				(ImageView)mTabHost.getTabWidget().getChildAt(1).findViewById(R.id.icon);
		imgIcon.setClickable(false);

		imgIcon = 
				(ImageView)mTabHost.getTabWidget().getChildAt(2).findViewById(R.id.icon);
		imgIcon.setClickable(false);

		if(Prefs.getKey_boolean(MainTabFragmentActivity.this, Prefs.IS_LOGGED_IN)) {
			setActionBarColor();
		}

		switch(Prefs.getKey_int(MainTabFragmentActivity.this, Prefs.USER_TYPE)) {
		case 0:
			imgIcon = 
				(ImageView)mTabHost.getTabWidget().getChildAt(0).findViewById(R.id.icon);
			if(!isEnabled) {
				imgIcon.setBackgroundResource(R.drawable.planned_events_non_active);
			}else {
				imgIcon.setBackgroundResource(R.drawable.planned_events_tab_selector_pink);
			}

			imgIcon = 
					(ImageView)mTabHost.getTabWidget().getChildAt(1).findViewById(R.id.icon);
			if(!isEnabled) {
				imgIcon.setBackgroundResource(R.drawable.my_videos_on_active);
			}else {
				imgIcon.setBackgroundResource(R.drawable.my_videos_tab_selector_pink);
			}

			mTabHost.getTabWidget().getChildAt(2).setVisibility(View.GONE);
			break;

		case 1:
			imgIcon = 
				(ImageView)mTabHost.getTabWidget().getChildAt(0).findViewById(R.id.icon);
			if(!isEnabled) {
				imgIcon.setBackgroundResource(R.drawable.planned_events_non_active);
			}else {
				imgIcon.setBackgroundResource(R.drawable.planned_events_tab_selector_blue);
			}

			imgIcon = 
				(ImageView)mTabHost.getTabWidget().getChildAt(1).findViewById(R.id.icon);
			if(!isEnabled) {
				imgIcon.setBackgroundResource(R.drawable.my_videos_on_active);
			}else {
				imgIcon.setBackgroundResource(R.drawable.my_videos_tab_selector_blue);
			} 
			
			imgIcon = 
				(ImageView)mTabHost.getTabWidget().getChildAt(2).findViewById(R.id.icon);
			if(!isEnabled) {
				imgIcon.setBackgroundResource(R.drawable.my_team_non_active);
			}else {
				imgIcon.setBackgroundResource(R.drawable.my_team_tab_selector_blue);
			}

			mTabHost.getTabWidget().getChildAt(2).setVisibility(View.VISIBLE);
			break;

		default:
			imgIcon = 
				(ImageView)mTabHost.getTabWidget().getChildAt(0).findViewById(R.id.icon);
			if(!isEnabled) {
				imgIcon.setBackgroundResource(R.drawable.planned_events_non_active);
			}else {
				imgIcon.setBackgroundResource(R.drawable.planned_events_tab_selector_black);
			}

			imgIcon = 
					(ImageView)mTabHost.getTabWidget().getChildAt(1).findViewById(R.id.icon);
			if(!isEnabled) {
				imgIcon.setBackgroundResource(R.drawable.my_videos_on_active);
			}else {
				imgIcon.setBackgroundResource(R.drawable.my_videos_tab_selector_black);
			}
		
			imgIcon = 
				(ImageView)mTabHost.getTabWidget().getChildAt(2).findViewById(R.id.icon);
			
			if(!isEnabled) {
				imgIcon.setBackgroundResource(R.drawable.my_team_non_active);
			}else {
				imgIcon.setBackgroundResource(R.drawable.my_team_tab_selector_black);
			}
			
			mTabHost.getTabWidget().getChildAt(2).setVisibility(View.GONE);
			break;
		}
	}

	/**
	 * Shows the action bar
	 */
	public void showActionbar() {
		getSupportActionBar().show();
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View actionBarView = inflater.inflate(R.layout.header, null);

		getSupportActionBar().setCustomView(actionBarView);

		txtHeader = (TextView) actionBarView.findViewById(R.id.txtHeading);
		txtHeader.setText("Set");

		mRelHeader = (RelativeLayout)actionBarView.findViewById(R.id.relHeader);

		ActionBar actionBar = this.getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setCustomView(actionBarView);
	}

	/** 
	 * Changes the header of the action bar
	 * @param title
	 */
	public void changeTitle(String title) {
		getSupportActionBar().show();
		txtHeader = (TextView)getSupportActionBar().getCustomView().findViewById(R.id.txtHeading);
		mRelHeader = (RelativeLayout)getSupportActionBar().getCustomView().findViewById(R.id.relHeader);
		txtHeader.setText(title);
	}

	/**
	 * Changes the title to space in the action bar
	 */
	public void hideActionBar() {
		changeTitle("");
	}

	/**
	 * Changes the color of the action bar as per the userType
	 */
	public void setActionBarColor() {
		switch (userType) {
		// pink color
		case 0:
			mRelHeader.setBackgroundResource(R.drawable.top_bar_pink);
			break;

			// blue color
		case 1:
			mRelHeader.setBackgroundResource(R.drawable.top_bar_blue);
			break;

		default:
			mRelHeader.setBackgroundResource(android.R.color.black);
			break;
		}
		
		invalidateOptionsMenu();
	}

	/**
	 * Sets the current tab selection
	 * @param position
	 */
	public void setCurrentTab(int position) {
		mTabHost.setCurrentTab(position);
	}

	/**
	 * Sets the tab selectors as per the userType
	 */
	public void setTabSelectors() {
		switch (userType) {
		// pink color
		case 0:
			ImageView imgIcon = 
			(ImageView)mTabHost.getTabWidget().getChildAt(0).findViewById(R.id.icon);
			imgIcon.setBackgroundResource(R.drawable.planned_events_tab_selector_pink);

			imgIcon = 
					(ImageView)mTabHost.getTabWidget().getChildAt(1).findViewById(R.id.icon);
			imgIcon.setBackgroundResource(R.drawable.my_videos_tab_selector_pink);

			imgIcon = 
					(ImageView)mTabHost.getTabWidget().getChildAt(3).findViewById(R.id.icon);
			imgIcon.setBackgroundResource(R.drawable.settings_tab_selector_pink);
			break;

			// blue color
		case 1:
			imgIcon = 
			(ImageView)mTabHost.getTabWidget().getChildAt(0).findViewById(R.id.icon);
			imgIcon.setBackgroundResource(R.drawable.planned_events_tab_selector_blue);

			imgIcon = 
					(ImageView)mTabHost.getTabWidget().getChildAt(1).findViewById(R.id.icon);
			imgIcon.setBackgroundResource(R.drawable.my_videos_tab_selector_blue);

			imgIcon = 
					(ImageView)mTabHost.getTabWidget().getChildAt(2).findViewById(R.id.icon);
			imgIcon.setBackgroundResource(R.drawable.my_team_tab_selector_blue);

			imgIcon = 
					(ImageView)mTabHost.getTabWidget().getChildAt(3).findViewById(R.id.icon);
			imgIcon.setBackgroundResource(R.drawable.settings_tab_selector_blue);
			break;

		default:
			imgIcon = 
			(ImageView)mTabHost.getTabWidget().getChildAt(0).findViewById(R.id.icon);
			imgIcon.setBackgroundResource(R.drawable.planned_events_tab_selector_black);

			imgIcon = 
					(ImageView)mTabHost.getTabWidget().getChildAt(1).findViewById(R.id.icon);
			imgIcon.setBackgroundResource(R.drawable.my_videos_tab_selector_black);

			imgIcon = 
					(ImageView)mTabHost.getTabWidget().getChildAt(2).findViewById(R.id.icon);
			imgIcon.setBackgroundResource(R.drawable.my_team_tab_selector_black);

			imgIcon = 
					(ImageView)mTabHost.getTabWidget().getChildAt(3).findViewById(R.id.icon);
			imgIcon.setBackgroundResource(R.drawable.settings_tab_selector_black);
			break;
		}
	}

	/**
	 * This is a helper class that implements a generic mechanism for
	 * associating fragments with the tabs in a tab host. It relies on a trick.
	 * Normally a tab host has a simple API for supplying a View or Intent that
	 * each tab will show. This is not sufficient for switching between
	 * fragments. So instead we make the content part of the tab host 0dp high
	 * (it is not shown) and the TabManager supplies its own dummy view to show
	 * as the tab content. It listens to changes in tabs, and takes care of
	 * switch to the correct fragment shown in a separate content area whenever
	 * the selected tab changes.
	 */
	public static class TabManager implements TabHost.OnTabChangeListener {

		private final FragmentActivity mActivity;
		private final TabHost mTabHost;
		private final int mContainerId;
		private String TAG  = "TabManager";

		private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();

		private TabInfo mLastTab;


		static final class TabInfo {
			private final String tag;
			private final Class<?> clss;
			private final Bundle args;
			private Fragment fragment;

			TabInfo(String _tag, Class<?> _class, Bundle _args) {
				tag = _tag;
				clss = _class;
				args = _args;
			}
		}

		static class DummyTabFactory implements TabHost.TabContentFactory {
			private final Context mContext;

			public DummyTabFactory(Context context) {
				mContext = context;
			}

			@Override
			public View createTabContent(String tag) {
				View v = new View(mContext);
				v.setMinimumWidth(0);
				v.setMinimumHeight(0);
				return v;
			}
		}

		public TabManager(FragmentActivity activity, TabHost tabHost,
				int mContainerId) {
			mActivity = activity;
			mTabHost = tabHost;
			this.mContainerId = mContainerId;
			mTabHost.setOnTabChangedListener(this);
		}

		public void addTab(TabHost.TabSpec tabSpec, String tabTitle, Drawable drawable, Class<?> clss, Bundle args) {
			tabSpec.setContent(new DummyTabFactory(mActivity));
			String tag = tabSpec.getTag();

			Logger.i(modules.INSURANCE_APP, TAG, tag);

			View tabIndicator = LayoutInflater.from(mActivity).inflate(R.layout.tab_indicator, mTabHost.getTabWidget(), false);

			//TextView txtTabTitle = ((TextView) tabIndicator.findViewById(R.id.title));
			//txtTabTitle.setText(tabTitle);

			ImageView imgIcon = ((ImageView) tabIndicator.findViewById(R.id.icon));
			imgIcon.setImageDrawable(drawable);
			imgIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);

			tabSpec.setIndicator(tabIndicator);

			TabInfo info = new TabInfo(tag, clss, args);

			info.fragment = mActivity.getSupportFragmentManager()
					.findFragmentByTag(tag);
			if (info.fragment != null && !info.fragment.isDetached()) {
				FragmentTransaction ft = mActivity.getSupportFragmentManager()
						.beginTransaction();
				ft.detach(info.fragment);
				ft.commit();
			}


			mTabs.put(tag, info);
			mTabHost.addTab(tabSpec);
		}

		public void addInvisibleTab(TabHost.TabSpec tabSpec, Class<?> clss,
				Bundle args, int childID) {
			tabSpec.setContent(new DummyTabFactory(mActivity));
			String tag = tabSpec.getTag();
			TabWidget tabWidget = mTabHost.getTabWidget();
			TabInfo info = new TabInfo(tag, clss, args);

			info.fragment = mActivity.getSupportFragmentManager()
					.findFragmentByTag(tag);
			if (info.fragment != null && !info.fragment.isDetached()) {
				FragmentTransaction ft = mActivity.getSupportFragmentManager()
						.beginTransaction();
				ft.detach(info.fragment);
				ft.commit();
			}

			mTabs.put(tag, info);
			mTabHost.addTab(tabSpec);

			// Makes tab invisible
			tabWidget.getChildAt(childID).setVisibility(View.GONE);
		}

		@Override
		public void onTabChanged(String tabId) {

		}
	}

	static class DummyTabFactory implements TabHost.TabContentFactory {
		private final Context mContext;

		public DummyTabFactory(Context context) {
			mContext = context;
		}

		@Override
		public View createTabContent(String tag) {
			View v = new View(mContext);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}
	}

	/**
	 * As per the instace of each fragment the fragments are loaded on the screen
	 * And also stored in the stack for back tracking purposes
	 * @param fragment
	 */
	public void loadFragment(Fragment fragment) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);

		// Fragment mFragment1 = new RestaurantMenuFragment_Child();
		ft.add(R.id.realtabcontent, fragment);

		if (fragment instanceof SettingsFragment) {
			if(Prefs.getKey_boolean(MainTabFragmentActivity.this, Prefs.IS_LOGGED_IN)) {

				((FrameLayout) findViewById(R.id.realtabcontent)).removeAllViews();

				popFromAllStacks();

				MainTabFragmentActivity.settingsStack.push(fragment);
			}else { 
				MainTabFragmentActivity.settingsStack.lastElement().onPause();
				ft.hide(MainTabFragmentActivity.settingsStack.lastElement());
				MainTabFragmentActivity.settingsStack.push(fragment);
			}
		} else if (fragment instanceof LogInFragment) {
			MainTabFragmentActivity.settingsStack.lastElement().onPause();
			ft.hide(MainTabFragmentActivity.settingsStack.lastElement());
			MainTabFragmentActivity.settingsStack.push(fragment);
		} else if (fragment instanceof HomeFragment) {
			userType = -1;
			Prefs.addKey(MainTabFragmentActivity.this, Prefs.IS_LOGGED_IN, false);
			Prefs.addKey(MainTabFragmentActivity.this, Prefs.IS_SUPER_LOGIN_CLICKED, false);

			((FrameLayout) findViewById(R.id.realtabcontent)).removeAllViews();

			popFromAllStacks();

			MainTabFragmentActivity.settingsStack.push(fragment);
			setTabsAccessibility(false);

			mRelHeader.setBackgroundResource(R.drawable.top_bar_black);
			setActionBarColor();
			setTabSelectors();

			Prefs.addKey(MainTabFragmentActivity.this, Prefs.USER_TYPE, userType);
		} else if(fragment instanceof PlannedEventsFragment) {

		} else if(fragment instanceof PlannedEventDetailFragment) {
			/*MainTabFragmentActivity.plannedEventsStack.lastElement().onPause();
			ft.hide(MainTabFragmentActivity.plannedEventsStack.lastElement());
			MainTabFragmentActivity.plannedEventsStack.push(fragment);*/
			switch (getCurrentTab()) {
			case 0:
				MainTabFragmentActivity.plannedEventsStack.lastElement().onPause();
				ft.hide(MainTabFragmentActivity.plannedEventsStack.lastElement());
				MainTabFragmentActivity.plannedEventsStack.push(fragment);
				break;
				
			case 2:
				MainTabFragmentActivity.myTeamStack.lastElement().onPause();
				ft.hide(MainTabFragmentActivity.myTeamStack.lastElement());
				MainTabFragmentActivity.myTeamStack.push(fragment);
				break;

			default:
				break;
			}
		} else if(fragment instanceof MyVideoDetailFragment) {
			switch (getCurrentTab()) {
			case 1:
				MainTabFragmentActivity.myVideosStack.lastElement().onPause();
				ft.hide(MainTabFragmentActivity.myVideosStack.lastElement());
				MainTabFragmentActivity.myVideosStack.push(fragment);
				break;
				
			case 2:
				MainTabFragmentActivity.myTeamStack.lastElement().onPause();
				ft.hide(MainTabFragmentActivity.myTeamStack.lastElement());
				MainTabFragmentActivity.myTeamStack.push(fragment);
				break;

			default:
				break;
			}
		} else if(fragment instanceof MyTeamDetailFragment) {
			MainTabFragmentActivity.myTeamStack.lastElement().onPause();
			ft.hide(MainTabFragmentActivity.myTeamStack.lastElement());
			MainTabFragmentActivity.myTeamStack.push(fragment);
		}

		ft.commit();
	}

	/**
	 * All fragments are removed such that on 
	 * back press only necessary fragments are covered
	 */
	private void popFromAllStacks() {
		while(!settingsStack.isEmpty()) {
			MainTabFragmentActivity.settingsStack.pop();
		}

		while(!plannedEventsStack.isEmpty()) {
			MainTabFragmentActivity.plannedEventsStack.pop();
		}

		while(!myVideosStack.isEmpty()) {
			MainTabFragmentActivity.myVideosStack.pop();
		}

		while(!myTeamStack.isEmpty()) {
			MainTabFragmentActivity.myTeamStack.pop();
		}
	}

	/**
	 * Load the login screen fragment based on the userType
	 * @param type
	 */
	public void loadLoginScreen(Integer type) {
		//mTabHost.setCurrentTabByTag("Settings");
		SettingsFragment settingsFragment = new SettingsFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean("login", false);
		settingsFragment.setArguments(bundle);
		loadFragment(settingsFragment);

		userType = type;
		Prefs.addKey(MainTabFragmentActivity.this, Prefs.USER_TYPE, userType);
	}
	
	public int getCurrentTab() {
		if(mTabHost != null) {
			return mTabHost.getCurrentTab();
		}else {
			// default settings tab position
			return 3;
		}
	}
	
	public int getHeight() {
		DisplayMetrics disp = getResources().getDisplayMetrics();
		int windowHeight = disp.heightPixels;
		return windowHeight;
	}
}
