package org.microemu.android;

import org.microemu.DisplayAccess;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.android.device.AndroidDeviceDisplay;
import org.microemu.device.DeviceFactory;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;

public abstract class MicroEmulatorActivity extends Activity {

	private Handler handler = new Handler();
	
	private Thread activityThread;
	
	private View contentView;
	
	public boolean post(Runnable r) {
		if (activityThread == Thread.currentThread()) {
			r.run();
			return true;
		} else {
			return handler.post(r);
		}
	}

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		activityThread = Thread.currentThread();
	}
	
	public View getContentView() {
		return contentView;
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(view);
		
		contentView = view;
	}
		
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
        Display display = getWindowManager().getDefaultDisplay();
		AndroidDeviceDisplay deviceDisplay = (AndroidDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay();
		deviceDisplay.displayRectangleWidth = display.getWidth();
		deviceDisplay.displayRectangleHeight = display.getHeight() - 25;
		MIDletAccess ma = MIDletBridge.getMIDletAccess();
		if (ma == null) {
			return;
		}
		DisplayAccess da = ma.getDisplayAccess();
		if (da != null) {
			da.sizeChanged(deviceDisplay.getFullWidth(), deviceDisplay.getFullHeight());
			deviceDisplay.repaint(0, 0, deviceDisplay.getFullWidth(), deviceDisplay.getFullHeight());
		}
	}
	
}
