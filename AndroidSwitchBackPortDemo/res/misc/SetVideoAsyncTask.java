package com.azilen.insuranceapp.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.azilen.insuranceapp.R;
import com.azilen.insuranceapp.entities.network.request.SetVideoRequest;
import com.azilen.insuranceapp.managers.network.NetworkManager;
import com.azilen.insuranceapp.managers.network.RequestType;
import com.azilen.insuranceapp.service.UploadVideoService;
import com.azilen.insuranceapp.utils.CommonUtility;
import com.azilen.insuranceapp.utils.Global;
import com.azilen.insuranceapp.videostreaming.DroidActivity;

/**
 * Async task that sets the video
 * @author dhara.shah
 *
 */
public class SetVideoAsyncTask extends AsyncTask<Void, Void, String>{
	private Context mContext;
	private SetVideoRequest mSetVideoRequest;
	private String mFrom;
	private ProgressDialog mProgressDialog;
	private String mIsBackPressed;
	private String mVideoPath;

	/**
	 * Constructor
	 * @param context
	 * @param setVideoRequest
	 * @param from
	 * @param videoPath
	 */
	public SetVideoAsyncTask(Context context, 
			SetVideoRequest setVideoRequest, String from, String videoPath) {
		mContext = context;
		mSetVideoRequest = setVideoRequest;
		mFrom = from;
		mVideoPath = videoPath;
	}
	
	/**
	 * Constructor
	 * @param context
	 * @param setVideoRequest
	 * @param from
	 * @param isBackPressed
	 * @param videoPath
	 */
	public SetVideoAsyncTask(Context context, 
			SetVideoRequest setVideoRequest, String from, String isBackPressed, String videoPath) {
		mContext = context;
		mSetVideoRequest = setVideoRequest;
		mFrom = from;
		mIsBackPressed = isBackPressed;
		mVideoPath = videoPath;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		if(mFrom.equalsIgnoreCase(Global.FROM_DROID_ACTIVITY)) {
			mProgressDialog = ProgressDialog.show(mContext, null, mContext.getString(R.string.please_wait));
		}
	}

	@Override
	protected String doInBackground(Void... params) {
		String response =NetworkManager.getInstance().
				performPostRequest(RequestType.SET_VIDEO, mSetVideoRequest);

		// output is just ["true"] or ["false"]
		if(response != null) {
		
		}
		return response;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		
		if(mFrom.equalsIgnoreCase(Global.FROM_DROID_ACTIVITY)) {
			mProgressDialog.dismiss();
			((DroidActivity)mContext).updateValues(result, mIsBackPressed);
		}else if(mFrom.equalsIgnoreCase(Global.FROM_SERVICE)) {
			if(result != null) {
				// delete the value from xml
				// as it has been uploaded and also set
				CommonUtility.deleteFromXmlFile(mSetVideoRequest.getActivityId());
			}else {
				// write the node again such that it becomes the last node. 
				CommonUtility.writeToXmlFile(mSetVideoRequest.getActivityId(),
						mVideoPath, 
						mSetVideoRequest.getVideoSize(), 
						mSetVideoRequest.getVideoDuration());
			}
			
			((UploadVideoService)mContext).uploadVid();
		}
	}
}
