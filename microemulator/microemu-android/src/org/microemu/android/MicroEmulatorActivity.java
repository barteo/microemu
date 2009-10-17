/**
 *  MicroEmulator
 *  Copyright (C) 2009 Bartek Teodorczyk <barteo@barteo.net>
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 *
 *  @version $Id: MicroEmulatorActivity.java 1918 2009-01-21 12:56:43Z barteo $
 */

package org.microemu.android;

import java.io.IOException;
import java.io.InputStream;

import org.microemu.DisplayAccess;
import org.microemu.DisplayComponent;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.android.device.AndroidDeviceDisplay;
import org.microemu.android.device.AndroidFontManager;
import org.microemu.android.device.AndroidInputMethod;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.DeviceFactory;
import org.microemu.device.EmulatorContext;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;
import org.microemu.log.Logger;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;

public abstract class MicroEmulatorActivity extends Activity {

	private Handler handler = new Handler();
	
	private Thread activityThread;
	
	protected View contentView;

	private Dialog dialog;
	
	protected EmulatorContext emulatorContext = new EmulatorContext() {

        private InputMethod inputMethod = new AndroidInputMethod();

        private DeviceDisplay deviceDisplay = new AndroidDeviceDisplay(this);
        
        private FontManager fontManager = new AndroidFontManager();

        public DisplayComponent getDisplayComponent() {
            // TODO consider removal of EmulatorContext.getDisplayComponent()
            System.out.println("MicroEmulator.emulatorContext::getDisplayComponent()");
            return null;
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
            try {
                if (name.startsWith("/")) {
                    return MicroEmulatorActivity.this.getAssets().open(name.substring(1));
                } else {
                    return MicroEmulatorActivity.this.getAssets().open(name);
                }
            } catch (IOException e) {
                Logger.debug(e);
                return null;
            }
        }

        public boolean platformRequest(String url) 
        {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

            return true;
        }
                
    };
    
    public EmulatorContext getEmulatorContext() {
        return emulatorContext;
    }

	public boolean post(Runnable r) {
		if (activityThread == Thread.currentThread()) {
			r.run();
			return true;
		} else {
			return handler.post(r);
		}
	}
	
	public boolean isActivityThread() {
		return (activityThread == Thread.currentThread());
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
			da.sizeChanged();
			deviceDisplay.repaint(0, 0, deviceDisplay.getFullWidth(), deviceDisplay.getFullHeight());
		}
	}
	
	public void setDialog(Dialog dialog) {
		this.dialog = dialog;
		if (dialog != null) {
			showDialog(0);
		} else {
			removeDialog(0);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		return dialog;
	}
}
