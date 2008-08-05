package org.microemu.app;

import java.io.InputStream;
import java.util.ArrayList;

import org.microemu.DisplayComponent;
import org.microemu.EmulatorContext;
import org.microemu.MIDletBridge;
import org.microemu.app.ui.noui.NoUiDisplayComponent;
import org.microemu.app.util.DeviceEntry;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;
import org.microemu.device.j2se.J2SEDevice;
import org.microemu.device.j2se.J2SEDeviceDisplay;
import org.microemu.device.j2se.J2SEFontManager;
import org.microemu.device.j2se.J2SEInputMethod;

public class Headless {
	
	private Common emulator; 

	private EmulatorContext context = new EmulatorContext() {
		private DisplayComponent displayComponent = new NoUiDisplayComponent();

		private InputMethod inputMethod = new J2SEInputMethod();

		private DeviceDisplay deviceDisplay = new J2SEDeviceDisplay(this);

		private FontManager fontManager = new J2SEFontManager();

		public DisplayComponent getDisplayComponent() {
			return displayComponent;
		}

		public InputMethod getDeviceInputMethod() {
			return inputMethod;
		}

		public DeviceDisplay getDeviceDisplay() {
			return deviceDisplay;
		}

		public FontManager getDeviceFontManager() {
			return fontManager;
		}

		public InputStream getResourceAsStream(String name) {
			return MIDletBridge.getCurrentMIDlet().getClass().getResourceAsStream(name);
		}
	};
	
	public Headless() {
		emulator = new Common(context);
	}

	public static void main(String[] args) {
		ArrayList params = new ArrayList();
		for (int i = 0; i < args.length; i++) {
			params.add(args[i]);
		}
		
		// Non-persistent RMS
		params.add("--rms");
		params.add("memory");
		
		Headless app = new Headless();
		
		DeviceEntry defaultDevice = new DeviceEntry(
	    		"Default device", 
	    		null, 
	    		"org/microemu/device/default/device.xml", 
	    		true, 
	    		false);
		
		app.emulator.initParams(params, defaultDevice, J2SEDevice.class);
	    app.emulator.initMIDlet(true);
	}

}
