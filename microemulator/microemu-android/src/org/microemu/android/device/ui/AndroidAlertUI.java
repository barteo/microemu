package org.microemu.android.device.ui;

import javax.microedition.lcdui.Alert;

import org.microemu.android.MicroEmulatorActivity;
import org.microemu.device.ui.AlertUI;

import android.content.res.TypedArray;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AndroidAlertUI extends AndroidDisplayableUI implements AlertUI {

	protected TextView alertTextView;
	
	public AndroidAlertUI(final MicroEmulatorActivity activity, Alert alert) {
		super(activity, alert, true);
		
		activity.post(new Runnable() {
			public void run() {
				alertTextView = new TextView(activity);
				alertTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				TypedArray a = alertTextView.getContext().obtainStyledAttributes(android.R.styleable.Theme);
				alertTextView.setTextAppearance(alertTextView.getContext(), a.getResourceId(android.R.styleable.Theme_textAppearanceLarge, -1));
				((LinearLayout) view).addView(alertTextView);				

				invalidate();
			}
		});		
	}

	//
	// AlertUI
	//		
	
	public void setString(final String str) {
		activity.post(new Runnable() {
			public void run() {
				alertTextView.setText(str);
			}
		});
	}
		
}
