package org.microemu.android.device.ui;

import org.microemu.device.ui.CanvasUI;

import android.app.Activity;

public class AndroidCanvasUI extends AndroidDisplayableUI implements CanvasUI {

	private Activity activity;
	
	public AndroidCanvasUI(Activity activity) {
		this.activity = activity;
	}

	public void hideNotify() {
	}

	public void showNotify() {
		// TODO Auto-generated method stub
	}

}
