package org.microemu.android.device.ui;

import org.microemu.device.ui.TextBoxUI;

import android.app.Activity;
import android.widget.TextView;

public class AndroidTextBoxUI extends AndroidDisplayableUI implements TextBoxUI {
	
	private Activity activity;
	
	private TextView view;
	
	public AndroidTextBoxUI(Activity activity) {
		this.activity = activity;
		
		this.view = new TextView(activity);
		this.view.setText("AndroidTextBoxUI");
	}

	public void hideNotify() {
	}

	public void showNotify() {
		activity.setContentView(view);
	}

}
