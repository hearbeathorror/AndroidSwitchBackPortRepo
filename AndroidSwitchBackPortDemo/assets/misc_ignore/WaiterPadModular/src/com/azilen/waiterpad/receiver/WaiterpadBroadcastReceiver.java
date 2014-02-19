package com.azilen.waiterpad.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.widget.Toast;

public class WaiterpadBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)){ 
			boolean connected = intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false);
			if(!connected) {
				//Start service for disconnected state here
				//Toast.makeText(context, "disconnected ! ", Toast.LENGTH_LONG).show();
			}
		}
		else if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
			NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			if( netInfo.isConnected()){
				//Start service for connected state here.
				
				WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
				WifiInfo myWifiInfo = wifiManager.getConnectionInfo();
				
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
				String ipAddress = sp.getString("ipAddress", "");
				if(ipAddress != null && 
						ipAddress.trim().length() > 0) {
					@SuppressWarnings("deprecation")
					String ipOfDevice = Formatter.formatIpAddress(myWifiInfo.getIpAddress());
					
					int lastIndexOfDeviceIP = ipOfDevice.lastIndexOf(".");
					int lastIndexOfStoredIp = ipAddress.lastIndexOf(".");
					
					if(!ipOfDevice.substring(0,lastIndexOfDeviceIP).equalsIgnoreCase(ipAddress.substring(0,lastIndexOfStoredIp))) {
						Toast.makeText(context, "you are connected to a different network"  , 
								Toast.LENGTH_LONG).show();
					}else {
						//Toast.makeText(context, "connected ! ", 
							//	Toast.LENGTH_LONG).show();
					}
				}
				
//				Toast.makeText(context, "connected ! " + Formatter.formatIpAddress(myWifiInfo.getIpAddress()) , 
//						Toast.LENGTH_LONG).show();
			}else if(!netInfo.isConnectedOrConnecting()) {
				Toast.makeText(context, "trying to connect ! ", Toast.LENGTH_LONG).show();
			}else  if(!netInfo.isConnected()) {
				Toast.makeText(context, "disconnected ! ", Toast.LENGTH_LONG).show();
			}
		}
	}
}
