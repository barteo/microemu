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
		super(activity, alert);
		
		activity.post(new Runnable() {
			public void run() {
				view = new LinearLayout(activity);
				((LinearLayout) view).setOrientation(LinearLayout.VERTICAL);
				view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
				
				titleView = new TextView(activity);
				titleView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				TypedArray a = titleView.getContext().obtainStyledAttributes(android.R.styleable.Theme);
				titleView.setTextAppearance(titleView.getContext(), a.getResourceId(android.R.styleable.Theme_textAppearanceLarge, -1));
				((LinearLayout) view).addView(titleView);				
		
				alertTextView = new TextView(activity);
				alertTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				a = alertTextView.getContext().obtainStyledAttributes(android.R.styleable.Theme);
				alertTextView.setTextAppearance(alertTextView.getContext(), a.getResourceId(android.R.styleable.Theme_textAppearanceLarge, -1));
				((LinearLayout) view).addView(alertTextView);				

				invalidate();
			}
		});		
	}

	//
	// AlertUI
	//		
	
	public void setString(String str) {
		alertTextView.setText(str);
	}
		
}
