package com.barteo.emulator.app.ui;

import com.barteo.emulator.device.MutableImage;
import com.barteo.emulator.device.swt.SwtDeviceDisplay;


public interface DisplayRepaintListener 
{
	
	public void repaintInvoked(SwtDeviceDisplay dd);
	
}
