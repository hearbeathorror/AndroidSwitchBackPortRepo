package com.azilen.insuranceapp.fragments;

import java.io.File;
import java.util.List;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.azilen.insuranceapp.R;
import com.azilen.insuranceapp.activities.MainTabFragmentActivity;
import com.azilen.insuranceapp.activities.MapFragmentActivity;
import com.azilen.insuranceapp.activities.VideoViewFragmentActivity;
import com.azilen.insuranceapp.asynctasks.ChangeStatusAsyncTask;
import com.azilen.insuranceapp.entities.network.request.ChangeStatusRequest;
import com.azilen.insuranceapp.entities.network.response.DoneActivity;
import com.azilen.insuranceapp.managers.doneactivities.DoneActivitiesManager;
import com.azilen.insuranceapp.utils.ExternalStorageManager;
import com.azilen.insuranceapp.utils.Global;
import com.azilen.insuranceapp.utils.Prefs;
import com.azilen.insuranceapp.utils.search.SearchForDoneActivity;
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
import com.google.common.collect.Iterables;

/**
 * Fragment that displays the details of a video
 * from list of my videos
 * @author dhara.shah
 *
 */
public class MyVideoDetailFragment extends Fragment implements OnClickListener, OnSeekBarChangeListener, OnCheckedChangeListener{
	private View mView;
	
	private TextView mTxtUsername;
	private TextView mTxtGroupTitle;
	private TextView mTxtDuration;
	private TextView mTxtUploadStatus;
	private TextView mTxtIsVideoApproved;
	private TextView mTxtVideoName;
	private TextView mTxtDate;
	private TextView mTxtTime;
	private TextView mTxtSize;
	private TextView mTxtDesc;
	private TextView mTxtPlaceLbl;
	private MapView mMapView;
	private GoogleMap mMap;
	private ImageView mImgZoom;
	private RatingBar mRatingBar;
	private Button mBtnClaimValue;
	private TextView mTxtCityName;
	private TextView mTxtStreetName;
	private ImageView mImgVideoIsApproved;
	private ImageButton mImgBtnToggleDesc;
	private SeekBar mRateSeekBar;
	private ImageView mImgFullView;
	private RelativeLayout mRelVideoStatus;
	private ImageButton mImgBackBtn;
	private Button mBtnDone;
	
	private CheckBox mChckBoxApproveVideo;
	private RelativeLayout mRelSeekBarLayer;
	private RelativeLayout mRelApproveReject;
	private CustomScrollView mScrollView;
	
	private ImageView mImgThumbNail;
	private String path;
	private static boolean isMapShown = false;
	private static boolean isVideoShown = false;
	
	private static DoneActivity mMyVideoEvent;
	private LatLng mLocationPosition;
	private static MyVideoDetailFragment mMyVideoDetailFragment;
	private Handler handler; 
	
	private ChangeStatusAsyncTask mChangeStatusAsyncTask;
	
	/**
	 * instance of MyVideoDetailFragment
	 * @param myVideoEvent
	 * @return
	 */
	public static MyVideoDetailFragment newInstance(DoneActivity myVideoEvent) {
		mMyVideoDetailFragment = new MyVideoDetailFragment();
		mMyVideoEvent = myVideoEvent;
		return mMyVideoDetailFragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.my_video_item,container, false);
		mTxtUsername = (TextView)mView.findViewById(R.id.txtUserName);
		mTxtCityName = (TextView)mView.findViewById(R.id.txtCityName);
		mTxtDate = (TextView)mView.findViewById(R.id.txtDate);
		mTxtDuration = (TextView)mView.findViewById(R.id.txtDuration);
		mTxtGroupTitle = (TextView)mView.findViewById(R.id.txtTitleOfGroup);
		mTxtIsVideoApproved = (TextView)mView.findViewById(R.id.txtIsVideoApproved);
		mTxtSize = (TextView)mView.findViewById(R.id.txtSize);
		mTxtStreetName = (TextView)mView.findViewById(R.id.txtStreetName);
		mTxtTime = (TextView)mView.findViewById(R.id.txtTime);
		mTxtDesc = (TextView)mView.findViewById(R.id.txtDescription);
		mTxtUploadStatus = (TextView)mView.findViewById(R.id.txtUploadStatus);
		mTxtVideoName = (TextView)mView.findViewById(R.id.txtVideoName);
		mImgThumbNail = (ImageView)mView.findViewById(R.id.imgThumbNail);
		mBtnClaimValue = (Button)mView.findViewById(R.id.btnClaimValue);
		mImgZoom = (ImageView)mView.findViewById(R.id.imgZoom);
		mRatingBar = (RatingBar)mView.findViewById(R.id.ratingBar);
		mImgVideoIsApproved = (ImageView)mView.findViewById(R.id.imgIsApproved);
		mRateSeekBar = (SeekBar)mView.findViewById(R.id.rateSeekBar);
		mImgFullView = (ImageView)mView.findViewById(R.id.imgFullView);
		mRelVideoStatus = (RelativeLayout)mView.findViewById(R.id.relVideoStatus);
		mImgBackBtn = (ImageButton)mView.findViewById(R.id.imgBtnBack);
		mBtnDone = (Button)mView.findViewById(R.id.btnDone);
		mTxtPlaceLbl = (TextView)mView.findViewById(R.id.txtPlaceLbl);
		mScrollView = (CustomScrollView)mView.findViewById(R.id.scrollView);
		
		mImgBtnToggleDesc = (ImageButton)mView.findViewById(R.id.imgBtnToggleDesc);
		mChckBoxApproveVideo = (CheckBox)mView.findViewById(R.id.chckBoxApproveVideo);
		mRelSeekBarLayer = (RelativeLayout)mView.findViewById(R.id.relSlider);
		mRelApproveReject = (RelativeLayout)mView.findViewById(R.id.relApproveReject);
		
		mImgBtnToggleDesc.setOnClickListener(this);
		mImgFullView.setOnClickListener(this);
		mImgBackBtn.setOnClickListener(this);
		mBtnDone.setOnClickListener(this);
		
		mChckBoxApproveVideo.setOnCheckedChangeListener(this);
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
		
		mMapView = (MapView)mView.findViewById(R.id.mapview);
		mMapView.onCreate(savedInstanceState);
		
		// create a link to play the file locally
		// if the file does not exist
		// get the file from the server
		path = ExternalStorageManager.InsuranceAppVideosDIR + mMyVideoEvent.getVideoName();

		File file = new File(path);
		if(!file.exists()) {
			// the file does not exist on the device
			// hence fetch the video path on the server
			//TODO: needs to be done
			
			//path = "rtsp://184.72.239.149/vod/mp4:BigBuckBunny_115k.mov";
			path = "rtsp://myusername:mypassword@192.168.3.20:1935/vod/mp4:9.mp4";
			
			//path = "rtsp://media.smart-streaming.com/mytest/mp4:sample.mp4";
			//path="rtsp://54.229.196.152/vods3cf/flv:amazons3/mobile-client-storage/flv:10.flv";
		}
		
		//TODO: On final finishing touch, this will be removed
		// since it will be set in the above if and else
		path = "rtsp://myusername:mypassword@192.168.3.20:1935/vod/mp4:9.mp4";
		
		if(mMyVideoEvent.getLatitude() != null && 
				mMyVideoEvent.getLongitude() != null) {
			mLocationPosition = new LatLng(Double.parseDouble(mMyVideoEvent.getLatitude()),
					Double.parseDouble(mMyVideoEvent.getLongitude()));	
		}
		
		handler = new Handler();
		handler.postDelayed(r, 1000);
		
		mImgZoom.setOnClickListener(this);
		mImgThumbNail.setOnClickListener(this);
		
		mRateSeekBar.setOnSeekBarChangeListener(this);
		
		return mView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mMapView.setVisibility(View.INVISIBLE);
		
		handler = new Handler();
		handler.postDelayed(resumeRunnable, 1000);
		
		if(isMapShown || isVideoShown) {
			((MainTabFragmentActivity)getActivity()).changeTitle(getString(R.string.my_videos));
			
			if(Prefs.getKey(Prefs.CURRENT_TAB_SELECTED).trim().length() > 0) {
				((MainTabFragmentActivity) getActivity()).
					setCurrentTab(Integer.parseInt(Prefs.getKey(Prefs.CURRENT_TAB_SELECTED)));
			}else {
				((MainTabFragmentActivity) getActivity()).setCurrentTab(1);
			}
			
			isMapShown = false;
			isVideoShown = false;
		}
	}
	
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
	
	private Runnable r = new Runnable() {
		@Override
		public void run() {
			if(mMapView != null) {
				try {
					mMapView.onResume();
					mMapView.setVisibility(View.INVISIBLE);
					initializeMap();
					mMapView.setVisibility(View.VISIBLE);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	private Runnable resumeRunnable = new Runnable() {
		@Override
		public void run() {
			if(mMapView != null) {
				try {
					mMapView.onResume();
					mMapView.setVisibility(View.VISIBLE);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	/**
	 * Initialization of the mapview
	 */
	private void initializeMap() {
		try {
			// Gets to GoogleMap from the MapView and does initialization stuff
			mMap = mMapView.getMap(); 
			mMap.getUiSettings().setMyLocationButtonEnabled(false);
			mMap.setMyLocationEnabled(false);
			
			// Needs to call MapsInitializer before doing any CameraUpdateFactory calls
		    try {
		        MapsInitializer.initialize(this.getActivity());
		    } catch (GooglePlayServicesNotAvailableException e) {
		        e.printStackTrace();
		    }
		    
		    // Updates the location and zoom of the MapView
		    CameraUpdate cameraUpdate = CameraUpdateFactory
		    		.newLatLngZoom(mLocationPosition, 10);
		    mMap.animateCamera(cameraUpdate);
		    
		    Marker locationPosition = mMap.addMarker(new MarkerOptions().position(mLocationPosition)
		            .title("")
		            .icon(BitmapDescriptorFactory
		                    .fromResource(R.drawable.marker)));
		    
		    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocationPosition, 15));
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// store the value of the current tab in shared preferences
		Prefs.addKey(Prefs.CURRENT_TAB_SELECTED, 
				String.valueOf(((MainTabFragmentActivity)getActivity()).getCurrentTab()));
		
		// get the thumbnail of the video from the url
		//Bitmap thumb = ThumbnailUtils.createVideoThumbnail(path,
		//	    MediaStore.Images.Thumbnails.MINI_KIND);
		//mImgThumbNail.setImageBitmap(thumb);
		
		mTxtUsername.setText(mMyVideoEvent.getUserName());
		mTxtCityName.setText(mMyVideoEvent.getCity());
		
		if(mMyVideoEvent.getDateTime() != null) {
			mTxtDate.setText(mMyVideoEvent.getDateTime().substring(0, 
					mMyVideoEvent.getDateTime().indexOf(" ") + 1));
			
			mTxtTime.setText(mMyVideoEvent.getDateTime().split(" ")[1].trim());
		}
		
		mTxtDuration.setText(mMyVideoEvent.getVideoDuration());
		mTxtGroupTitle.setText(mMyVideoEvent.getGroupName());
		mTxtDesc.setText(mMyVideoEvent.getDescription());
		
		// sets the image and text
		// checks if the video is approved
		setVideoApproveStatus();
		
		mTxtSize.setText(mMyVideoEvent.getVideoSize());
		mTxtStreetName.setText(mMyVideoEvent.getStreet());
		
		// sets the image and text 
		// checks if the video is uploaded or not
		if(Boolean.parseBoolean(mMyVideoEvent.getIsUploaded())) {
			mTxtUploadStatus.setText(getString(R.string.upload_status));
		}else {
			mTxtUploadStatus.setText(getString(R.string.not_uploaded));
		}
		
		if(mMyVideoEvent.getEventTitle() != null && 
				mMyVideoEvent.getEventTitle().trim().length() > 0) {
			mTxtVideoName.setText(mMyVideoEvent.getEventTitle());
		}else {
			mTxtVideoName.setText(getString(R.string.event_title));
		}

		mBtnClaimValue.setText(mMyVideoEvent.getValue() +  
				" " + 
				getString(R.string.value_symbol));
		
		int rating = 0;
		
		if(mMyVideoEvent.getRating() != null &&
				mMyVideoEvent.getRating().trim().length() > 0) {
			mRatingBar.setRating(Float.parseFloat(mMyVideoEvent.getRating()));
			
			rating = (int)(Float.parseFloat(mMyVideoEvent.getRating()) * 2);
		}
		
		mRatingBar.setClickable(false);
		mRatingBar.setEnabled(false);
		
		// sets the rating value
		mRateSeekBar.setProgress(rating);
		
		// the video has a rating
		// it is not rejected
		if(rating > 0) {
			mRelSeekBarLayer.setVisibility(View.VISIBLE);
			mChckBoxApproveVideo.setChecked(true);
		}else {
			mChckBoxApproveVideo.setChecked(false);
			mRelSeekBarLayer.setVisibility(View.GONE);
			mRateSeekBar.setProgress(0);
			mRatingBar.setRating(0);
		}
		
		// sets the GUI resources
		setGui();
	}
	
	private void setVideoApproveStatus() {
		if(Boolean.parseBoolean(mMyVideoEvent.getIsApproved())) {
			mTxtIsVideoApproved.setText(getString(R.string.approved));
			mImgVideoIsApproved.setBackgroundResource(R.drawable.approved);
		}else {
			mTxtIsVideoApproved.setText(getString(R.string.not_approved));
			mImgVideoIsApproved.setBackgroundResource(R.drawable.rejected);
		}
	}

	private void setGui() {
		switch (Prefs.getKey_int(getActivity(), Prefs.USER_TYPE)) {
		// pink
		case 0:
			mTxtUsername.setTextColor(getResources().getColor(R.color.pink));
			mBtnClaimValue.setBackgroundResource(R.drawable.circular_view_pink);
			mTxtPlaceLbl.setTextColor(getResources().getColor(R.color.pink));
			mTxtGroupTitle.setBackgroundResource(R.drawable.text_title_drawable_pink);
			mTxtVideoName.setTextColor(getResources().getColor(R.color.pink));
			mImgBtnToggleDesc.setBackgroundResource(R.drawable.info_box_selector_pink);
			mImgBackBtn.setBackgroundResource(R.drawable.back_arrow_selector_pink);
			//mImgBtnToggleDesc.setImageDrawable(getResources().getDrawable(R.drawable.info_box_pink));
			mRateSeekBar.setVisibility(View.GONE);
			mRelVideoStatus.setVisibility(View.VISIBLE);
			mRelApproveReject.setVisibility(View.GONE);
			mBtnDone.setVisibility(View.GONE);
			break;

		case 1:
			mTxtUsername.setTextColor(getResources().getColor(R.color.blue));
			mBtnClaimValue.setBackgroundResource(R.drawable.circular_view);
			mTxtPlaceLbl.setTextColor(getResources().getColor(R.color.blue));
			mTxtGroupTitle.setBackgroundResource(R.drawable.text_title_drawable_blue);
			mTxtVideoName.setTextColor(getResources().getColor(R.color.blue));
			mImgBtnToggleDesc.setBackgroundResource(R.drawable.info_box_selector);
			mImgBackBtn.setBackgroundResource(R.drawable.back_arrow_selector);
			//mImgBtnToggleDesc.setImageDrawable(getResources().getDrawable(R.drawable.info_box));
			mRateSeekBar.setVisibility(View.VISIBLE);
			mRelVideoStatus.setVisibility(View.GONE);
			mRelApproveReject.setVisibility(View.VISIBLE);
			mBtnDone.setVisibility(View.VISIBLE);
			break;
			
		default:
			break;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.imgZoom:
			isMapShown = true;
			Intent intent = new Intent(getActivity(), MapFragmentActivity.class);
			
			if(mMyVideoEvent.getLatitude() != null && 
					mMyVideoEvent.getLongitude() != null) {
				intent.putExtra(Global.LATITUDE, Double.parseDouble(mMyVideoEvent.getLatitude()));
				intent.putExtra(Global.LONGITUDE, Double.parseDouble(mMyVideoEvent.getLongitude()));
			}else {
				intent.putExtra(Global.LATITUDE, Double.parseDouble("0.0"));
				intent.putExtra(Global.LONGITUDE, Double.parseDouble("0.0"));
			}
			
			intent.putExtra(Global.DESCRIPTION, mMyVideoEvent.getDescription());
			startActivityForResult(intent,2);
			((MainTabFragmentActivity)getActivity()).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
			break;
			
		case R.id.imgThumbNail:
			showVideoFullView();
			break;
			
		case R.id.imgBtnToggleDesc:
			if(mTxtDesc.getVisibility() == View.VISIBLE) {
				mTxtDesc.setVisibility(View.GONE);
			}else {
				mTxtDesc.setVisibility(View.VISIBLE);
			}
			break;
			
		case R.id.imgFullView:
			showVideoFullView();
			break;
			
		case R.id.imgBtnBack:
			((MainTabFragmentActivity)getActivity()).onBackPressed();
			break;
			
		case R.id.btnDone:
			// call to the webservice function
			sendChangeRequest();
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

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if((progress * 0.5) >= 1) {
			// sets the rating bar as per the seek bar position
			mRatingBar.setRating((float)(progress * 0.5));
		}else {
			// sets the rating bar to 1 by default and 
			// also seek bar position to 1 by default
			mRatingBar.setRating(1);
			mRateSeekBar.setProgress(1);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		
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

	@Override
	public void onCheckedChanged(CompoundButton button, boolean isChecked) {
		switch (button.getId()) {
		case R.id.chckBoxApproveVideo:
			mChckBoxApproveVideo.setChecked(isChecked);
			
			// if checked display the seek bar with a default rating of one
			if(isChecked) {
				if(mRatingBar.getRating() > 1) {
					mRelSeekBarLayer.setVisibility(View.VISIBLE);
					int rating = (int)(mRatingBar.getRating() * 2);
					mRateSeekBar.setProgress(rating);
				}else {
					mRelSeekBarLayer.setVisibility(View.VISIBLE);
					mRateSeekBar.setProgress((1));
					mRatingBar.setRating(1);
				}
			}else {
				mRateSeekBar.setProgress((0));
				mRatingBar.setRating(0);
				mRelSeekBarLayer.setVisibility(View.GONE);
			}
			break;

		default:
			break;
		}
	}
	
	public void setValues(String response) {
		// set the updated values for the video
		// get the id and replace the video element in cache
		String id = mMyVideoEvent.getActivityId();
		
		// check if there is any response received
		if(response != null) {
			mMyVideoEvent.setIsApproved(String.valueOf(mChckBoxApproveVideo.isChecked()));
			mMyVideoEvent.setRating(String.valueOf((int)mRatingBar.getRating()));
			setVideoApproveStatus();
		}
		
		List<DoneActivity> doneActivities = 
				DoneActivitiesManager.getInstance().getAllDoneActivities();
		DoneActivity mDoneActivity = null;
		
		if(doneActivities != null) {
			mDoneActivity = Iterables.find(doneActivities, 
					new SearchForDoneActivity(id), null);
			if(mDoneActivity != null) {
				// remove from the list and replace 
				// the same with the updated object
				doneActivities.remove(mDoneActivity);
				doneActivities.add(mMyVideoEvent);
			}
		}
	}
	
	/**
	 * Added as on 28th Jan 2014
	 * Function to send the changed status of the video
	 * that is, whether it is approved and whether its not
	 */
	private void sendChangeRequest() {
		ChangeStatusRequest changeStatusRequest = new ChangeStatusRequest();
		changeStatusRequest.setIsClaim(String.valueOf(mChckBoxApproveVideo.isChecked()));
		changeStatusRequest.setRating(String.valueOf((int)mRatingBar.getRating()));
		changeStatusRequest.setActivityId(mMyVideoEvent.getActivityId());
		
		mChangeStatusAsyncTask = 
				new ChangeStatusAsyncTask(getActivity(), 
						changeStatusRequest, MyVideoDetailFragment.this);
		mChangeStatusAsyncTask.execute();
	}
}
