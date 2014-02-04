package com.azilen.insuranceapp.videostreaming;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;

import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ShortBuffer;
import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.azilen.insuranceapp.R;
import com.azilen.insuranceapp.asynctasks.SetVideoAsyncTask;
import com.azilen.insuranceapp.entities.network.request.SetVideoRequest;
import com.azilen.insuranceapp.managers.plannedevents.PlannedEventsManager;
import com.azilen.insuranceapp.utils.CommonUtility;
import com.azilen.insuranceapp.utils.ExternalStorageManager;
import com.azilen.insuranceapp.utils.Global;
import com.azilen.insuranceapp.utils.NetworkStatus;
import com.googlecode.javacv.FFmpegFrameRecorder;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

public class DroidActivity extends Activity implements OnClickListener {

	private final String LOG_TAG = this.getClass().getSimpleName();

	private PowerManager.WakeLock mWakeLock;
	private String ffmpeg_link = "rtmp://myusername:mypassword@192.168.3.20:1935/live/I";
	// private String ffmpeg_link = "rtmp://54.229.196.152/livecf/"+vidName;
	private String ffmpeg_link1 = ExternalStorageManager.InsuranceAppVideosDIR + "appVid" + ".mp4";

	private volatile FFmpegFrameRecorder recorder, localRecorder;
	boolean recording = false;
	long startTime = 0;

	private int sampleAudioRateInHz = 44100;
	private int imageWidth = 640;
	private int imageHeight = 480;
	private int frameRate = 24;
	private static int counter=0;
	
	private int width;
	private int height;

	private Thread audioThread;
	volatile boolean runAudioThread = true;
	private AudioRecord audioRecord;
	private AudioRecordRunnable audioRecordRunnable;

	private CameraView cameraView;
	private IplImage yuvIplimage = null;

	private Button myButton;
	private Chronometer meter;
	private RelativeLayout mainLayout;
	
	private String mActivityId;
	private File file;
	
	private static boolean serverRecordFailed = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.videostreaming);
		
		if(getIntent().getExtras() != null) {
			mActivityId = getIntent().getStringExtra(Global.ACTIVITY_ID);
		}
		
		// this will not be null
		// but just incase
		if(mActivityId != null) {
			ffmpeg_link1 = ExternalStorageManager.InsuranceAppVideosDIR + mActivityId + ".mp4";
			ffmpeg_link = "rtmp://myusername:mypassword@192.168.3.20:1935/live/" + mActivityId;
			//ffmpeg_link = "rtmp://54.229.196.152/livecf/"+ mActivityId;
		}

		
		Display display = getWindowManager().getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();
		imageWidth = width;
		imageHeight = height;
		initLayout();
		initRecorder();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mWakeLock == null) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
					LOG_TAG);
			mWakeLock.acquire();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mWakeLock != null) {
			mWakeLock.release();
			mWakeLock = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		recording = false;
	}

	private void initLayout() {
		mainLayout = (RelativeLayout) this.findViewById(R.id.record_layout);
		ImageView redImage = new ImageView(this);
		redImage.setImageResource(R.drawable.redrecordsmall);

		myButton = new Button(this);
		myButton.setOnClickListener(this);
		myButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_record_stop));

		meter = new Chronometer(this);
		LinearLayout.LayoutParams chronometerLayoutParams = (LinearLayout.LayoutParams) new LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		chronometerLayoutParams.setMargins(0, 0, 10, 0);

		LinearLayout ll = new LinearLayout(this);
		ll.setBackgroundColor(Color.BLACK);
		ll.getBackground().setAlpha(50);
		ll.addView(redImage);
		ll.addView(meter, chronometerLayoutParams);

		cameraView = new CameraView(this);

		RelativeLayout.LayoutParams layoutParam = new RelativeLayout.LayoutParams(width, height);
		mainLayout.addView(cameraView, layoutParam);

		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		mainLayout.addView(myButton, params);

		LinearLayout.LayoutParams linearLayoutParams = (LinearLayout.LayoutParams) new LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		linearLayoutParams.setMargins(20, 20, 20, 20);
		mainLayout.addView(ll, linearLayoutParams);
		Log.v(LOG_TAG, "added cameraView to mainLayout");
	}

	private void initRecorder() {
		Log.w(LOG_TAG, "initRecorder");

		if (yuvIplimage == null) {
			// Recreated after frame size is set in surface change method
			yuvIplimage = IplImage.create(imageWidth, imageHeight,
					IPL_DEPTH_8U, 2);
			// yuvIplimage = IplImage.create(imageWidth,
			// imageHeight,IPL_DEPTH_32S, 2);

			Log.v(LOG_TAG, "IplImage.create");
		}

		recorder = new FFmpegFrameRecorder(ffmpeg_link, imageWidth,
				imageHeight, 1);
		Log.v(LOG_TAG, "FFmpegFrameRecorder: " + ffmpeg_link + " imageWidth: "
				+ imageWidth + " imageHeight " + imageHeight);

		recorder.setFormat("rtsp");
        recorder.setVideoCodec(28);
		Log.v(LOG_TAG, "recorder.setFormat(\"flv\")");

		recorder.setSampleRate(sampleAudioRateInHz);
		Log.v(LOG_TAG, "recorder.setSampleRate(sampleAudioRateInHz)");

		// re-set in the surface changed method as well
		recorder.setFrameRate(frameRate);
		Log.v(LOG_TAG, "recorder.setFrameRate(frameRate)");

		localRecorder = new FFmpegFrameRecorder(ffmpeg_link1, imageWidth,
				imageHeight, 1);
		Log.v(LOG_TAG, "FFmpegFrameRecorder: " + ffmpeg_link1 + " imageWidth: "
				+ imageWidth + " imageHeight " + imageHeight);

		localRecorder.setFormat("mp4");
		Log.v(LOG_TAG, "recorder.setFormat(\"flv\")");

		localRecorder.setSampleRate(sampleAudioRateInHz);
		Log.v(LOG_TAG, "recorder.setSampleRate(sampleAudioRateInHz)");

		// re-set in the surface changed method as well
		localRecorder.setFrameRate(frameRate);
		Log.v(LOG_TAG, "recorder.setFrameRate(frameRate)");

		// Create audio recording thread
		audioRecordRunnable = new AudioRecordRunnable();
		audioThread = new Thread(audioRecordRunnable);
	}

	// Start the capture
	public void startRecording() {
		try {
			localRecorder.start();
		}catch(FFmpegFrameRecorder.Exception e) {
			e.printStackTrace();
		}

		try {
			recorder.start();
		}catch(FFmpegFrameRecorder.Exception e) {
			e.printStackTrace();
			// set flag here
			// server did not receive packets
			serverRecordFailed = true;
		}

		startTime = System.currentTimeMillis();
		meter.setBase(SystemClock.elapsedRealtime());
		meter.start();
		recording = true;
		audioThread.start();
	}

	public void startRecordingLocal() {
		try {
			localRecorder.start();
			startTime = System.currentTimeMillis();
			meter.setBase(SystemClock.elapsedRealtime());
            meter.start();
			recording = true;
			audioThread.start();
		} catch (FFmpegFrameRecorder.Exception e) {
			e.printStackTrace();
		}
	}

	public void stopRecording() {
		// This should stop the audio thread from running
		runAudioThread = false;

		if (recorder != null && recording) {
			recording = false;
			Log.v(LOG_TAG,
					"Finishing recording, calling stop and release on recorder");
			try {
				recorder.stop();
				recorder.release();
				localRecorder.stop();
				localRecorder.release();
				meter.stop();
			} catch (FFmpegFrameRecorder.Exception e) {
				e.printStackTrace();
			}
			recorder = null;
			localRecorder = null;
		}
	}

	public void stopRecordingLocal() {
		// This should stop the audio thread from running
		runAudioThread = false;

		if (localRecorder != null && recording) {
			recording = false;
			Log.v(LOG_TAG,
					"Finishing recording, calling stop and release on recorder");
			try {
				localRecorder.stop();
				localRecorder.release();
				meter.stop();
			} catch (FFmpegFrameRecorder.Exception e) {
				e.printStackTrace();
			}
			localRecorder = null;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Quit when back button is pushed
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (recording) {
				stopRecording();
				stopRecordingLocal();
				
				// set video
				setVideo(Global.BACK_PRESSED);
				return false;
			}else {
				return super.onKeyDown(keyCode, event);
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void setVideo(String backPressed) {
		SetVideoRequest setVideoRequest = new SetVideoRequest();
		setVideoRequest.setVideoDuration(meter.getText().toString());
		setVideoRequest.setActivityId(mActivityId);
		setVideoRequest.setImage(mActivityId + ".png");
		setVideoRequest.setVideo(mActivityId + ".mp4");
		setVideoRequest.setIsClaim(String.valueOf(true));
		setVideoRequest.setVideoSize(getFileSize());
		
		new SetVideoAsyncTask(DroidActivity.this, setVideoRequest, 
				Global.FROM_DROID_ACTIVITY, backPressed).execute();
	}

	@Override
	public void onClick(View v) {
		if(v.isSelected()) {
			v.setSelected(false);
		}else {
			v.setSelected(true);
		}
		
		if (!NetworkStatus.isConnected()) {
			if (!recording) {
				startRecordingLocal();
				Log.w(LOG_TAG, "Start Button Pushed");
			} else {
				stopRecordingLocal();
				Log.w(LOG_TAG, "Stop Button Pushed");
				
				// store the id in the xml
				CommonUtility.writeToXmlFile(mActivityId, 
						ffmpeg_link1,
						getFileSize(),
						meter.getText().toString());
			}
		} else {
			if (!recording) {
				startRecording();
				Log.w(LOG_TAG, "Start Button Pushed");
			} else {
				stopRecording();
				
				if(!serverRecordFailed) {
					// server record did not fail
					// so set video
					setVideo(Global.BACK_PRESSED);
				}else {
					// store the value in the xml
					CommonUtility.writeToXmlFile(mActivityId, 
							ffmpeg_link1,
							getFileSize(),
							meter.getText().toString());
					serverRecordFailed = false;
				}
				
				Log.w(LOG_TAG, "Stop Button Pushed");
			}
		}
	}

	// ---------------------------------------------
	// audio thread, gets and encodes audio data
	// ---------------------------------------------
	class AudioRecordRunnable implements Runnable {

		@Override
		public void run() {
			// Set the thread priority
			android.os.Process
					.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

			// Audio
			int bufferSize;
			short[] audioData;
			int bufferReadResult;

			bufferSize = AudioRecord.getMinBufferSize(sampleAudioRateInHz,
					AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT);
			audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
					sampleAudioRateInHz,
					AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT, bufferSize);

			audioData = new short[bufferSize];

			Log.d(LOG_TAG, "audioRecord.startRecording()");
			audioRecord.startRecording();

			// Audio Capture/Encoding Loop
			while (runAudioThread) {
				// Read from audioRecord
				bufferReadResult = audioRecord.read(audioData, 0,
						audioData.length);
				if (bufferReadResult > 0) {
					// Log.v(LOG_TAG,"audioRecord bufferReadResult: " +
					// bufferReadResult);

					// Changes in this variable may not be picked up despite it
					// being "volatile"
					if (recording) {
						try {
							// Write to FFmpegFrameRecorder

							Buffer[] buffer = { ShortBuffer.wrap(audioData, 0,
									bufferReadResult) };
							localRecorder.record(buffer);
							recorder.record(buffer);
						} catch (FFmpegFrameRecorder.Exception e) {
							Log.v(LOG_TAG, e.getMessage());
							e.printStackTrace();
						}
					}
				}
			}
			Log.v(LOG_TAG, "AudioThread Finished");

			/* Capture/Encoding finished, release recorder */
			if (audioRecord != null) {
				audioRecord.stop();
				audioRecord.release();
				audioRecord = null;
				Log.v(LOG_TAG, "audioRecord released");
			}
		}
	}

	class CameraView extends SurfaceView implements SurfaceHolder.Callback,
			PreviewCallback {

		private boolean previewRunning = false;

		private SurfaceHolder holder;
		private Camera camera;

		private byte[] previewBuffer;

		long videoTimestamp = 0;

		Bitmap bitmap;
		Canvas canvas;

		public CameraView(Context _context) {
			super(_context);

			holder = this.getHolder();
			holder.addCallback(this);
			holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			camera = Camera.open();

			try {
				camera.setPreviewDisplay(holder);
				camera.setPreviewCallback(this);

				Camera.Parameters currentParams = camera.getParameters();
				Log.v(LOG_TAG,
						"Preview Framerate: "
								+ currentParams.getPreviewFrameRate());
				Log.v(LOG_TAG,
						"Preview imageWidth: "
								+ currentParams.getPreviewSize().width
								+ " imageHeight: "
								+ currentParams.getPreviewSize().height);

				// Use these values
				imageWidth = currentParams.getPreviewSize().width;
				imageHeight = currentParams.getPreviewSize().height;
				frameRate = currentParams.getPreviewFrameRate();

				bitmap = Bitmap.createBitmap(imageWidth, imageHeight,
						Bitmap.Config.ALPHA_8);

				/*
				 * Log.v(LOG_TAG,"Creating previewBuffer size: " + imageWidth *
				 * imageHeight *
				 * ImageFormat.getBitsPerPixel(currentParams.getPreviewFormat
				 * ())/8); previewBuffer = new byte[imageWidth * imageHeight *
				 * ImageFormat
				 * .getBitsPerPixel(currentParams.getPreviewFormat())/8];
				 * camera.addCallbackBuffer(previewBuffer);
				 * camera.setPreviewCallbackWithBuffer(this);
				 */

				camera.startPreview();
				previewRunning = true;
			} catch (IOException e) {
				Log.v(LOG_TAG, e.getMessage());
				e.printStackTrace();
			}
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			Log.v(LOG_TAG, "Surface Changed: width " + width + " height: "
					+ height);

			// We would do this if we want to reset the camera parameters
			/*
			 * if (!recording) { if (previewRunning){ camera.stopPreview(); }
			 * 
			 * try { //Camera.Parameters cameraParameters =
			 * camera.getParameters(); //p.setPreviewSize(imageWidth,
			 * imageHeight); //p.setPreviewFrameRate(frameRate);
			 * //camera.setParameters(cameraParameters);
			 * 
			 * camera.setPreviewDisplay(holder); camera.startPreview();
			 * previewRunning = true; } catch (IOException e) {
			 * Log.e(LOG_TAG,e.getMessage()); e.printStackTrace(); } }
			 */

			// Get the current parameters
			Camera.Parameters currentParams = camera.getParameters();
			Log.v(LOG_TAG,
					"Preview Framerate: " + currentParams.getPreviewFrameRate());
			Log.v(LOG_TAG,
					"Preview imageWidth: "
							+ currentParams.getPreviewSize().width
							+ " imageHeight: "
							+ currentParams.getPreviewSize().height);

			// Use these values
			imageWidth = currentParams.getPreviewSize().width;
			imageHeight = currentParams.getPreviewSize().height;
			frameRate = currentParams.getPreviewFrameRate();

			// Create the yuvIplimage if needed
			yuvIplimage = IplImage.create(imageWidth, imageHeight,
					IPL_DEPTH_8U, 2);
			// yuvIplimage = IplImage.create(imageWidth, imageHeight,
			// IPL_DEPTH_32S, 2);
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			try {
				camera.setPreviewCallback(null);

				previewRunning = false;
				camera.release();

			} catch (RuntimeException e) {
				Log.v(LOG_TAG, e.getMessage());
				e.printStackTrace();
			}
		}

		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {

			if (yuvIplimage != null && recording) {
				videoTimestamp = 1000 * (System.currentTimeMillis() - startTime);

				// Put the camera preview frame right into the yuvIplimage
				// object
				yuvIplimage.getByteBuffer().put(data);

				// FAQ about IplImage:
				// - For custom raw processing of data, getByteBuffer() returns
				// an NIO direct
				// buffer wrapped around the memory pointed by imageData, and
				// under Android we can
				// also use that Buffer with Bitmap.copyPixelsFromBuffer() and
				// copyPixelsToBuffer().
				// - To get a BufferedImage from an IplImage, we may call
				// getBufferedImage().
				// - The createFrom() factory method can construct an IplImage
				// from a BufferedImage.
				// - There are also a few copy*() methods for
				// BufferedImage<->IplImage data transfers.

				// Let's try it..
				// This works but only on transparency
				// Need to find the right Bitmap and IplImage matching types

				/*
				 * bitmap.copyPixelsFromBuffer(yuvIplimage.getByteBuffer());
				 * //bitmap.setPixel(10,10,Color.MAGENTA);
				 * 
				 * canvas = new Canvas(bitmap); Paint paint = new Paint();
				 * paint.setColor(Color.GREEN); float leftx = 20; float topy =
				 * 20; float rightx = 50; float bottomy = 100; RectF rectangle =
				 * new RectF(leftx,topy,rightx,bottomy);
				 * canvas.drawRect(rectangle, paint);
				 * 
				 * bitmap.copyPixelsToBuffer(yuvIplimage.getByteBuffer());
				 */
				// Log.v(LOG_TAG,"Writing Frame");

				try {
					// Get the correct time
					localRecorder.setTimestamp(videoTimestamp);
					//
					// // Record the image into FFmpegFrameRecorder
					localRecorder.record(yuvIplimage);

					// Get the correct time
					recorder.setTimestamp(videoTimestamp);

					// Record the image into FFmpegFrameRecorder
					recorder.record(yuvIplimage);

				} catch (FFmpegFrameRecorder.Exception e) {
					Log.v(LOG_TAG, e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Update the value in the xml for the video
	 */
	public void updateValues(String response, String isBackPressed) {
		if(response != null) {
			CommonUtility.modifyIsVideoSetValue(mActivityId, "1");
		}else {
			CommonUtility.modifyIsVideoSetValue(mActivityId, "0");
			Log.e("dhara","vid not set");
		}
		
		if(file != null && file.exists()) {
			// remove this event from planned events
			PlannedEventsManager.getInstance().removeVideoFromPlannedEvents(mActivityId);
		}
		
		if(isBackPressed != null) {
			// back has been pressed
			// now finish this activity
			super.onBackPressed();
			overridePendingTransition(R.anim.activity_finish_in_anim, R.anim.activity_finish_out_anim);
		}
	}
	
	private String getFileSize() {
		DecimalFormat dcf = new DecimalFormat("#.##");
		file = new File(ExternalStorageManager.InsuranceAppVideosDIR + mActivityId + ".mp4");
		
		if (file.exists()) {
			long fileSize = file.length();
		    // converting to mb
		    double dblFileSize = fileSize / 1000000.00;
		    return dcf.format(dblFileSize);
		}
		return dcf.format(0);
	}
}