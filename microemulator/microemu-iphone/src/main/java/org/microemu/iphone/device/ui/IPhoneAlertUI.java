package org.microemu.iphone.device.ui;

import javax.microedition.lcdui.Alert;

import obc.UIAlertView;

import org.microemu.device.ui.AlertUI;
import org.microemu.iphone.MicroEmulator;

public class IPhoneAlertUI extends AbstractUI implements AlertUI {

	private Alert alert;
	
	public IPhoneAlertUI(MicroEmulator microEmulator, Alert alert) {
		super(microEmulator, alert);
		this.alert=alert;
	}

	public void setString(String str) {
		// TODO Auto-generated method stub

	}

	public void hideNotify() {
		// TODO Auto-generated method stub

	}

	public void invalidate() {
		// TODO Auto-generated method stub

	}

	public void showNotify() {
			UIAlertView alertView=new UIAlertView().initWithTitle$message$delegate$cancelButtonTitle$otherButtonTitles$(alert.getTitle(), alert.getString(), null, "Abbrechen", null);
	
	alertView.show();
	alertView.release();
	
	commandListener.commandAction(Alert.DISMISS_COMMAND, alert);
	}

}
