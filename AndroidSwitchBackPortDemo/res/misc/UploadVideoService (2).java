package com.azilen.insuranceapp.service;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.azilen.insuranceapp.asynctasks.SetVideoAsyncTask;
import com.azilen.insuranceapp.asynctasks.UploadVideoAsyncTask;
import com.azilen.insuranceapp.entities.VideosToUpload;
import com.azilen.insuranceapp.entities.network.request.SetVideoRequest;
import com.azilen.insuranceapp.utils.CommonUtility;
import com.azilen.insuranceapp.utils.Global;
import com.azilen.insuranceapp.utils.NetworkStatus;
import com.azilen.insuranceapp.utils.Prefs;

/**
 * Service that uploads the video (if any is pending)
 * When there is internet connection in the background
 * Created as on 16th January 2014
 * @author dhara.shah
 *
 */
public class UploadVideoService extends Service {
	private int connectedTo;
	private String TAG =this.getClass().getSimpleName();
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		connectedTo = NetworkStatus.getConnectionType();
		switch (connectedTo) {
		case 1:
			// wifi connected
			// upload videos only if the user wants to download the videos over wifi
			if(Prefs.getKey_boolean(UploadVideoService.this, Prefs.SYNC_VIDEOS_OVER_WIFI) ||
					Prefs.getKey_boolean(UploadVideoService.this, Prefs.SYNC_VIDEOS)) {
				// upload vid here
				uploadVid();
			}
			break;
			
		case 2:
			// internet connected
			// upload videos only if the user wants to download videos over regular network
			if(Prefs.getKey_boolean(UploadVideoService.this, Prefs.SYNC_VIDEOS)) {
				// upload vid here
				uploadVid();
			}
			break;

		default:
			break;
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	public void uploadVid() {
		// changes as on 4th Feb 2014
		// the video will first be uploaded and then set.
		// get a list of videos
		List<VideosToUpload> vids = CommonUtility.getVideosToUpload();
		
		if(vids != null && vids.size() > 0) {
			// get the first video always
			// since only one video will be uploaded at a time
			VideosToUpload vid = vids.get(0);
			new UploadVideoAsyncTask(UploadVideoService.this, vid, Global.FROM_SERVICE).execute();
		}
		// changes end here
		
		
	}
	
	public void setVideo(VideosToUpload vidToUpload, String videoPath) {
		// video is not set
		SetVideoRequest setVideoRequest = new SetVideoRequest();
		setVideoRequest.setActivityId(vidToUpload.getActivityId());
		setVideoRequest.setImage(vidToUpload.getActivityId() + ".png");
		setVideoRequest.setIsClaim(String.valueOf(true));
		setVideoRequest.setVideo(vidToUpload.getActivityId() + ".mp4");
		setVideoRequest.setVideoSize(vidToUpload.getVideoSize());
		setVideoRequest.setVideoDuration(vidToUpload.getVideoDuration());
		
		new SetVideoAsyncTask(UploadVideoService.this, 
				setVideoRequest, Global.FROM_SERVICE, videoPath).execute();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
