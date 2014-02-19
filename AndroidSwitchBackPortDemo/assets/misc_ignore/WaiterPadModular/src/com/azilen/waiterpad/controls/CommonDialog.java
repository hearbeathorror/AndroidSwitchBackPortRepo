package com.azilen.waiterpad.controls;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Used to show dialog with Single Button
 * 
 * @author chintan.rathod
 * 
 */
public class CommonDialog extends AlertDialog {
	String strMessage, strOkString, strAppName;

	public CommonDialog(Context context, String appName, String okString,
			String message) {
		super(context);
		this.strAppName = appName;
		this.strMessage = message;
		this.strOkString = okString;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setCancelable(false);

		setTitle(strAppName);
		setMessage(strMessage);

		setButton(DialogInterface.BUTTON_POSITIVE, strOkString,
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		super.onCreate(savedInstanceState);
	}
}
