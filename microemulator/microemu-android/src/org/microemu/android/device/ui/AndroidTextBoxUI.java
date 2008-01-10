package org.microemu.android.device.ui;

import org.microemu.device.ui.TextBoxUI;

import android.app.Activity;
import android.widget.TextView;

public class AndroidTextBoxUI extends AndroidDisplayableUI implements TextBoxUI {
	
	private Activity activity;
	
	public AndroidTextBoxUI(Activity activity) {
		this.activity = activity;
	}

	public void hideNotify() {
System.out.println("AndroidTextBoxUI::hideNotify");
		// TODO Auto-generated method stub
		
	}

	public void showNotify() {
System.out.println("AndroidTextBoxUI::showNotify");
		activity.setContentView(new TextView(activity));
	}

}
