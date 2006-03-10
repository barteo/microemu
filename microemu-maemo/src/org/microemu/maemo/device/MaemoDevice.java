package org.microemu.maemo.device;

import com.barteo.emulator.EmulatorContext;
import com.barteo.emulator.device.Device;

public class MaemoDevice extends Device
{

	  public void init(EmulatorContext context) 
	  {
	    super.init(context, "/org/microemu/maemo/device/device.xml");
	  }

}
