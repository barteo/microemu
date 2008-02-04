package org.microemu.android;

import android.app.Activity;
import android.os.Handler;

public abstract class MicroEmulatorActivity extends Activity {

	private Handler handler = new Handler();
	
	public Handler getHandler() {
		return handler;
	}
	
}
