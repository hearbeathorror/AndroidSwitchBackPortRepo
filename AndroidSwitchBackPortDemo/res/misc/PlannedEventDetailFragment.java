package com.azilen.insuranceapp.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.azilen.insuranceapp.R;
import com.azilen.insuranceapp.activities.MainTabFragmentActivity;
import com.azilen.insuranceapp.activities.MapFragmentActivity;
import com.azilen.insuranceapp.activities.VideoViewFragmentActivity;
import com.azilen.insuranceapp.entities.network.response.PlannedEvents;
import com.azilen.insuranceapp.utils.Global;
import com.azilen.insuranceapp.utils.Prefs;
import com.azilen.insuranceapp.videostreaming.DroidActivity;
import com.azilen.insuranceapp.views.CustomScrollView;
import com.azilen.insuranceapp.views.CustomScrollView.OnScrollViewListener;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Fragment that is loaded when a planned event is clicked on
 * 
 * @author dhara.shah
 * 
 */
public class PlannedEventDetailFragment extends Fragment implements
		OnClickListener {
	private TextView mTxtGroupTitle;
	private TextView mTxtEventTitle;
	private TextView mTxtUsername;
	private TextView mTxtVideoDate;
	private TextView mTxtVideoTime;
	private TextView mTxtCityName;
	private TextView mTxtPlaceLbl;
	private TextView mTxtStreetName;
	private ImageView mImageZoom;
	private ImageButton mImgBtnRecord;
	private ImageButton mImgBtnPlay;
	private Button mBtnReady;
	private CustomScrollView mScrollView;

	private View mView;
	private static PlannedEvents mPlannedEvent;
	private static boolean isMapShown = false;
	private static boolean isVideoShown = false;
	private static PlannedEventDetailFragment mPlannedEventDetailFragment;

	// related to the map
	private MapView mMapView;
	private GoogleMap mGoogleMap;
	private LatLng mLocationPosition;
	private Handler handler;
	private String path;

	/**
	 * instance of PlannedEventDetailFragment
	 * 
	 * @param plannedEvent
	 * @return
	 */
	public static PlannedEventDetailFragment newInstance(
			PlannedEvents plannedEvent) {
		mPlannedEventDetailFragment = new PlannedEventDetailFragment();
		mPlannedEvent = plannedEvent;
		return mPlannedEventDetailFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.planned_event_item, container, false);
		mTxtCityName = (TextView) mView.findViewById(R.id.txtCityName);
		mTxtEventTitle = (TextView) mView.findViewById(R.id.txtTitleEvent);
		mTxtGroupTitle = (TextView) mView.findViewById(R.id.txtTitleOfGroup);
		mTxtStreetName = (TextView) mView.findViewById(R.id.txtStreetName);
		mTxtUsername = (TextView) mView.findViewById(R.id.txtUserName);
		mTxtVideoDate = (TextView) mView.findViewById(R.id.txtDate);
		mTxtVideoTime = (TextView) mView.findViewById(R.id.txtTime);
		mImageZoom = (ImageView) mView.findViewById(R.id.imgZoom);
		mTxtPlaceLbl = (TextView)mView.findViewById(R.id.txtPlaceLbl);
		mScrollView = (CustomScrollView)mView.findViewById(R.id.scrollView);
		mImgBtnPlay = (ImageButton)mView.findViewById(R.id.imgBtnPlay);

		mBtnReady = (Button) mView.findViewById(R.id.btnReady);
		mImgBtnRecord = (ImageButton) mView.findViewById(R.id.imgRecord);

		mMapView = (MapView) mView.findViewById(R.id.mapview);
		mMapView.onCreate(savedInstanceState);

		if(mPlannedEvent != null) {
			if (mPlannedEvent.getLatitude() != null
					&& mPlannedEvent.getLongitude() != null) {
				mLocationPosition = new LatLng(Double.parseDouble(mPlannedEvent
						.getLatitude()), Double.parseDouble(mPlannedEvent
						.getLongitude()));
			} else {
				mLocationPosition = new LatLng(0.0, 0.0);
			}
		}

		handler = new Handler();
		handler.postDelayed(r, 1500);

		mMapView.setOnClickListener(this);
		mImgBtnRecord.setOnClickListener(this);
		mImageZoom.setOnClickListener(this);
		mBtnReady.setOnClickListener(this);
		mImgBtnPlay.setOnClickListener(this);
		
		mScrollView.setOnScrollViewListener(new OnScrollViewListener() {
			@Override
			public void onScrollChanged(CustomScrollView v, int l, int t, int oldl,
					int oldt) {
				// the view has not scrolled
				handler.removeCallbacks(resumeRunnable);
				mMapView.setVisibility(View.INVISIBLE);
				handler.postDelayed(resumeRunnable, 1500);
			}
		});
		return mView;
	}

	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();

		mMapView.setVisibility(View.INVISIBLE);

		handler = new Handler();
		handler.postDelayed(resumeRunnable, 1500);

		if (isMapShown || isVideoShown) {
			
			if(Prefs.getKey(Prefs.CURRENT_TAB_SELECTED).trim().length() > 0) {
				((MainTabFragmentActivity) getActivity()).
					setCurrentTab(Integer.parseInt(Prefs.getKey(Prefs.CURRENT_TAB_SELECTED)));
			}else {
				((MainTabFragmentActivity) getActivity()).setCurrentTab(0);
			}
			
			((MainTabFragmentActivity) getActivity())
					.changeTitle(getString(R.string.events));
			isMapShown = false;
			isVideoShown = false;
		}
	}

	private Runnable r = new Runnable() {
		@Override
		public void run() {
			if (mMapView != null) {
				try {
					mMapView.onResume();
					mMapView.setVisibility(View.INVISIBLE);
					initializeMap();
					mMapView.setVisibility(View.VISIBLE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};

	private Runnable resumeRunnable = new Runnable() {
		@Override
		public void run() {
			if (mMapView != null) {
				try {
					mMapView.onResume();
					mMapView.setVisibility(View.VISIBLE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};

	@Override
	public void onPause() {
		super.onPause();
		try {
			if(mMapView != null) {
				mMapView.onPause();
				mMapView.setVisibility(View.INVISIBLE);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
		handler.removeCallbacks(r);
		handler.removeCallbacks(resumeRunnable);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		((MainTabFragmentActivity) getActivity())
				.changeTitle(getString(R.string.events));

		// store the value of the current tab in shared preferences
		Prefs.addKey(Prefs.CURRENT_TAB_SELECTED, 
				String.valueOf(((MainTabFragmentActivity)getActivity()).getCurrentTab()));
		
		if(mPlannedEvent != null) {
			mTxtCityName.setText(mPlannedEvent.getCity());
			
			if(mPlannedEvent.getEventTitle() != null && 
					mPlannedEvent.getEventTitle().trim().length() >0 ) {
				mTxtEventTitle.setText(mPlannedEvent.getEventTitle());
			}else {
				mTxtEventTitle.setText(getString(R.string.event_title));
			}
			
			if(mPlannedEvent.getGroupTitle() != null && 
					mPlannedEvent.getGroupTitle().trim().length() >0 ) {
				mTxtGroupTitle.setText(mPlannedEvent.getGroupTitle());
			}else {
				mTxtGroupTitle.setText(getString(R.string.title_of_group));
			}
			
			mTxtStreetName.setText(mPlannedEvent.getStreet());
			mTxtUsername.setText(mPlannedEvent.getUserName());

			if (mPlannedEvent.getDateTime() != null) {
				mTxtVideoDate.setText(mPlannedEvent.getDateTime().substring(0,
						mPlannedEvent.getDateTime().indexOf(" ") + 1));
				mTxtVideoTime.setText(mPlannedEvent.getDateTime().split(" ")[1]
						.trim());
			}
		}
		

		switch (Prefs.getKey_int(getActivity(), Prefs.USER_TYPE)) {
		// inspector
		case 0:
			mTxtGroupTitle.setBackgroundResource(R.drawable.text_title_drawable_pink);
			mTxtPlaceLbl.setTextColor(getResources().getColor(R.color.pink));
			mTxtEventTitle.setTextColor(getResources().getColor(R.color.pink));
			mBtnReady.setVisibility(View.VISIBLE);
			mImgBtnRecord.setEnabled(true);
			mImgBtnRecord
					.setBackgroundResource(R.drawable.record_button_selector);
			mImgBtnPlay.setVisibility(View.GONE);
			break;

		// analyst
		case 1:
			mTxtPlaceLbl.setTextColor(getResources().getColor(R.color.blue));
			mTxtGroupTitle.setBackgroundResource(R.drawable.text_title_drawable_blue);
			mTxtEventTitle.setTextColor(getResources().getColor(R.color.blue));
			mBtnReady.setVisibility(View.GONE);
			mImgBtnRecord.setVisibility(View.GONE); 
			mImgBtnPlay.setVisibility(View.VISIBLE);
			break;
		}
	}

	/**
	 * MapView initialization
	 */
	private void initializeMap() {
		try {
			// Gets to GoogleMap from the MapView and does initialization stuff
			mGoogleMap = mMapView.getMap();
			mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
			mGoogleMap.setMyLocationEnabled(false);

			// Needs to call MapsInitializer before doing any
			// CameraUpdateFactory calls
			try {
				MapsInitializer.initialize(this.getActivity());
			} catch (GooglePlayServicesNotAvailableException e) {
				e.printStackTrace();
			}

			// Updates the location and zoom of the MapView
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
					mLocationPosition, 10);
			mGoogleMap.animateCamera(cameraUpdate);

			Marker locationPosition = mGoogleMap.addMarker(new MarkerOptions()
					.position(mLocationPosition)
					.title("")
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.marker)));

			mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
					mLocationPosition, 15));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.imgZoom:
			openFullMapView();
			break;

		case R.id.mapview:
			openFullMapView();
			break;

		case R.id.imgRecord:
			Intent intent = new Intent(getActivity(), DroidActivity.class);
			intent.putExtra(Global.ACTIVITY_ID, mPlannedEvent.getActivityId());
			startActivity(intent);
			break;
			
		case R.id.btnReady:
			// push notification logic here
			// and timer to make the record button visible
			break;
			
		case R.id.imgBtnPlay:
			// this is for the inspector to be able to play the video
			// on his end
			path = "rtsp://myusername:mypassword@192.168.3.20:1935/vod/mp4:" + mPlannedEvent.getActivityId() +".mp4";
			showVideoFullView();
			break;

		default:
			break;
		}
	}
	
	/**
	 * Opens a full view of the video 
	 * A new activity is opened
	 */
	private void showVideoFullView() {
		// full view of the video
		isVideoShown = true;
		Intent intent = new Intent(getActivity(),VideoViewFragmentActivity.class);
		intent.putExtra(Global.VIDEO_LINK, path);
		startActivityForResult(intent,3);
		((MainTabFragmentActivity)getActivity()).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
	}

	/**
	 * Opens the map in a new activity
	 */
	private void openFullMapView() {
		isMapShown = true;
		Intent intent = new Intent(getActivity(), MapFragmentActivity.class);

		if (mPlannedEvent.getLatitude() != null
				&& mPlannedEvent.getLongitude() != null) {
			intent.putExtra(Global.LATITUDE,
					Double.parseDouble(mPlannedEvent.getLatitude()));
			intent.putExtra(Global.LONGITUDE,
					Double.parseDouble(mPlannedEvent.getLongitude()));
		} else {
			intent.putExtra(Global.LATITUDE, Double.parseDouble("0.0"));
			intent.putExtra(Global.LONGITUDE, Double.parseDouble("0.0"));
		}

		intent.putExtra(Global.DESCRIPTION, mPlannedEvent.getDescription());
		startActivity(intent);
		((MainTabFragmentActivity) getActivity()).overridePendingTransition(
				R.anim.slide_in_left, R.anim.slide_out_left);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	class InitializeMapAsyncTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			initializeMap();
			return null;
		}
	}
}
