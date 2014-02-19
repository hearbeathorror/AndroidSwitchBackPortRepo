package com.azilen.waiterpad.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.azilen.waiterpad.activities.StartActivity;

public class BootUpReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Thread.sleep(1000);
		}catch(InterruptedException e) {
			e.printStackTrace();
		}
		
		Intent intentStart = new Intent(context, StartActivity.class);
		intentStart.setAction("android.intent.action.MAIN");
		intentStart.addCategory("android.intent.category.HOME");
		intentStart.addCategory("android.intent.category.LAUNCHER");
		intentStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intentStart);
	}
}