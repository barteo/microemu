package org.microemu.android;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

public abstract class MicroEmulatorActivity extends Activity {

	private Handler handler = new Handler();
	
	private Thread activityThread;
	
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
	
}
