package org.microemu.app.ui;

import org.microemu.device.MutableImage;


public interface DisplayRepaintListener 
{
	
	public void repaintInvoked(MutableImage image);
	
}
