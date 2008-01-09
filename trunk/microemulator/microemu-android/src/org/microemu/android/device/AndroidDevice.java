package org.microemu.android.device;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.microedition.android.lcdui.Image;

import org.microemu.EmulatorContext;
import org.microemu.android.device.ui.AndroidTextBoxUI;
import org.microemu.device.Device;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;
import org.microemu.device.ui.TextBoxUI;
import org.microemu.device.ui.UIFactory;

import android.app.Activity;

public class AndroidDevice implements Device {

	private EmulatorContext emulatorContext;
	
	private Activity activity;
	
	private UIFactory ui = new UIFactory() {

		public TextBoxUI createTextBoxUI() {
			return new AndroidTextBoxUI(activity);
		}
		
	};
	
	private Map systemProperties = new HashMap();
	
	private Vector softButtons = new Vector();
	
	public AndroidDevice(EmulatorContext emulatorContext, Activity activity) {
		this.emulatorContext = emulatorContext;
		this.activity = activity;
	}

	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public Vector getButtons() {
		// TODO Auto-generated method stub
		return null;
	}

	public DeviceDisplay getDeviceDisplay() {
		return emulatorContext.getDeviceDisplay();
	}

	public FontManager getFontManager() {
		return emulatorContext.getDeviceFontManager();
	}

	public InputMethod getInputMethod() {
		return emulatorContext.getDeviceInputMethod();
	}
	
	public UIFactory getUIFactory() {
		return ui;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Image getNormalImage() {
		// TODO Auto-generated method stub
		return null;
	}

	public Image getOverImage() {
		// TODO Auto-generated method stub
		return null;
	}

	public Image getPressedImage() {
		// TODO Auto-generated method stub
		return null;
	}

	public Vector getSoftButtons() {
		return softButtons;
	}

	public Map getSystemProperties() {
		return systemProperties;
	}

	public boolean hasPointerEvents() {
		return true;
	}

	public boolean hasPointerMotionEvents() {
		return true;
	}

	public boolean hasRepeatEvents() {
		return true;
	}

	public void init() {
		// TODO Auto-generated method stub
		
	}

	public boolean vibrate(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
