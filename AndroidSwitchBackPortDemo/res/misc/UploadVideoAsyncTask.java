/***
 * Copyright (c) 2012 readyState Software Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.azilen.insuranceapp.asynctasks;

import java.io.File;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.azilen.insuranceapp.R;
import com.azilen.insuranceapp.entities.VideosToUpload;
import com.azilen.insuranceapp.service.UploadVideoService;
import com.azilen.insuranceapp.utils.CommonUtility;
import com.azilen.insuranceapp.utils.Global;
import com.readystatesoftware.simpl3r.UploadIterruptedException;
import com.readystatesoftware.simpl3r.Uploader;

public class UploadVideoAsyncTask extends AsyncTask<Void, Boolean, Boolean> {
	public static String ARG_FILE_PATH = "";
	public static final String UPLOAD_STATE_CHANGED_ACTION = "com.readystatesoftware.simpl3r.example.UPLOAD_STATE_CHANGED_ACTION";
	public static final String UPLOAD_CANCELLED_ACTION = "com.readystatesoftware.simpl3r.example.UPLOAD_CANCELLED_ACTION";
	public static final String S3KEY_EXTRA = "s3key";
	public static final String PERCENT_EXTRA = "percent";
	public static final String MSG_EXTRA = "msg";
	
	private VideosToUpload mVideosToUpload;
	private String mFrom;
	private String mVideoPath;
	private static boolean isVideoUploaded = false;

	private Context context;

	private AmazonS3Client s3Client;
	private Uploader uploader;

	public UploadVideoAsyncTask(Context _context, VideosToUpload videosToUpload,
			String from) {
		context = _context;
		mVideosToUpload = videosToUpload;
		mFrom = from;
		mVideoPath = videosToUpload.getVideoPath();
		ARG_FILE_PATH = mVideoPath;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		s3Client = new AmazonS3Client(new BasicAWSCredentials(
				context.getString(R.string.s3_access_key),
				context.getString(R.string.s3_secret)));

		String filePath = ARG_FILE_PATH; // intent.getStringExtra(ARG_FILE_PATH);
		File fileToUpload = new File(filePath);
		final String s3ObjectKey = mVideosToUpload.getActivityId() +".mp4";
		String s3BucketName = context.getString(R.string.s3_bucket);

		final String msg = "Uploading " + s3ObjectKey + "...";

		// create a new uploader for this file
		uploader = new Uploader(context, s3Client, s3BucketName, s3ObjectKey,
				fileToUpload);
		try {
			String s3Location = uploader.start(); // initiate the upload
			Log.e("Upload Service ---> ", "File successfully uploaded to "
					+ s3Location);
			isVideoUploaded = true;
		} catch (UploadIterruptedException uie) {
			Log.e("Upload Service ---> ", "User interrupted");
			isVideoUploaded = false;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("Upload Service ---> ", "Error: " + e.getMessage());
			isVideoUploaded = false;
		}
		return isVideoUploaded;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		if (mFrom.equalsIgnoreCase(Global.FROM_SERVICE)) {
			if (result) {
				// video is uploaded
				// therefore set the video
				((UploadVideoService) context).setVideo(mVideosToUpload,
						mVideoPath);
			} else {
				// video is not uploaded
				// make this the last entry
				CommonUtility.writeToXmlFile(mVideosToUpload.getActivityId(),
						mVideoPath, mVideosToUpload.getVideoSize(),
						mVideosToUpload.getVideoDuration());

				// try for the next video
				((UploadVideoService) context).uploadVid();
			}
		}
	}
}
