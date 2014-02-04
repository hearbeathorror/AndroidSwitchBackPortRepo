package com.azilen.insuranceapp.asynctasks;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.azilen.insuranceapp.entities.VideosToUpload;
import com.azilen.insuranceapp.service.UploadVideoService;
import com.azilen.insuranceapp.utils.CommonUtility;
import com.azilen.insuranceapp.utils.ExternalStorageManager;
import com.azilen.insuranceapp.utils.Global;
import com.azilen.insuranceapp.utils.Logger;
import com.azilen.insuranceapp.utils.Logger.modules;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class DemoSftp extends AsyncTask<Void, Void, Boolean> {
	private VideosToUpload mVideosToUpload;
	private Context mContext;
	private String mVideoPath;
	private String mFrom;
	private static boolean isVideoUploaded = false;
	private String TAG = this.getClass().getSimpleName();
	
	String SFTPHOST = "demo.wftpserver.com";
	int SFTPPORT = 2222;
	String SFTPUSER = "demo-user";
	String SFTPPASS = "demo-user";
	
	public DemoSftp(Context context, VideosToUpload videosToUpload, String from) {
		mVideosToUpload = videosToUpload;
		mFrom = from;
		mContext = context;
		mVideoPath = videosToUpload.getVideoPath();
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {

		File uploadFilePath;
		Session session;
		Channel channel = null;
		ChannelSftp sftp;
		uploadFilePath = new File(ExternalStorageManager.InsuranceAppVideosDIR + "eshan.mp4");
		//uploadFilePath = new File(mVideoPath);
		
		try {
			byte[] bufr = new byte[(int) uploadFilePath.length()];
			FileInputStream fis = new FileInputStream(uploadFilePath);
			fis.read(bufr);
			JSch ssh = new JSch();

			session = ssh.getSession(SFTPUSER,SFTPHOST, SFTPPORT);
			System.out.println("JSch JSch JSch Session created.");
			session.setPassword(SFTPPASS);
			
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			System.out.println("JSch JSch Session connected.");
			System.out.println("Opening Channel.");
			channel = session.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
			// server path where you want to upload file
			sftp.cd("/upload/");
			
			ByteArrayInputStream in = new ByteArrayInputStream(bufr);
			sftp.put(in, uploadFilePath.getName(), null);
			in.close();

			if (sftp.getExitStatus() == -1) {
				System.out.println("file uploaded");
				Log.v("upload result", "succeeded");
				
				// delete from xml if the videoid is present 
				// in the list of videos to upload
				isVideoUploaded = true;
			} else {
				Log.v("upload faild ", "faild");
				Logger.e(modules.INSURANCE_APP, TAG, 
						" file upload failed for video name " + mVideosToUpload.getActivityId() + ".mp4");
				
				// since the video is not uploaded 
				// add it to the xml so that it can be uploaded later on
				
				isVideoUploaded = false;
			}
		} catch (Exception e) {
			Logger.e(modules.INSURANCE_APP, TAG, CommonUtility.getExceptionMSG(e));
			
			isVideoUploaded = false;
		}
		
		return isVideoUploaded;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		
		if(mFrom.equalsIgnoreCase(Global.FROM_SERVICE)) {
			if(result) {
				// video is uploaded
				// therefore set the video
				((UploadVideoService)mContext).setVideo(mVideosToUpload, mVideoPath);
			}else {
				// video is not uploaded
				// make this the last entry
				CommonUtility.writeToXmlFile(mVideosToUpload.getActivityId(),
					mVideoPath, 
					mVideosToUpload.getVideoSize(), 
					mVideosToUpload.getVideoDuration());
				
				// try for the next video
				((UploadVideoService)mContext).uploadVid();
			}
		}
	}
}