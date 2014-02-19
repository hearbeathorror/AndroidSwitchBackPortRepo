package com.azilen.waiterpad.utils;

import java.lang.Thread.UncaughtExceptionHandler;

import org.slf4j.Logger;

import android.content.Context;
import android.content.Intent;

import com.azilen.waiterpad.R;
import com.azilen.waiterpad.activities.BaseActivity;

public class DefaultExceptionHandler implements UncaughtExceptionHandler {
	private Context mContext;
	private Thread.UncaughtExceptionHandler defaultUEH;
	Logger LOG;

	public DefaultExceptionHandler(Context context) {
		mContext = context;
		this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		StackTraceElement[] arr = ex.getStackTrace();
		String threadString = thread.toString();
		StringBuffer strReportBuffer = new StringBuffer();
		strReportBuffer.append(ex.toString() + "\n\n");
		strReportBuffer.append("--------- Stack trace ---------\n\n"
				+ threadString);
		for (int i = 0; i < arr.length; i++) {
			strReportBuffer.append("    " + arr[i].toString() + "\n");
		}
		strReportBuffer.append("-------------------------------\n\n");

		// If the exception was thrown in a background thread inside
		// AsyncTask, then the actual exception can be found with getCause
		strReportBuffer.append("--------- Cause ---------\n\n");
		Throwable cause = ex.getCause();
		if (cause != null) {
			strReportBuffer.append(cause.toString() + "\n\n");
			arr = cause.getStackTrace();
			for (int i = 0; i < arr.length; i++) {
				strReportBuffer.append("    " + arr[i].toString() + "\n");
			}
		}
		strReportBuffer.append("-------------------------------\n\n");

		/*
		 * try { File file = mUtils.getFile(); FileOutputStream trace =
		 * mContext.openFileOutput( file.getPath(), Context.MODE_PRIVATE);
		 * trace.write(report.getBytes()); trace.close(); } catch(IOException
		 * ioe) { // ... }
		 */

		LOG.debug(strReportBuffer.toString());

		defaultUEH.uncaughtException(thread, ex);

		Intent intent = new Intent(mContext.getString(R.string.kill));
		mContext.sendBroadcast(intent);
		((BaseActivity)mContext).finish();
	}
}