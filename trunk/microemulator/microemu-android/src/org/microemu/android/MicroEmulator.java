/**
 *  MicroEmulator
 *  Copyright (C) 2008 Bartek Teodorczyk <barteo@barteo.net>
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
 *  @version $Id$
 */

package org.microemu.android;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;

import org.microemu.DisplayAccess;
import org.microemu.DisplayComponent;
import org.microemu.EmulatorContext;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.android.device.AndroidDevice;
import org.microemu.android.device.AndroidDeviceDisplay;
import org.microemu.android.device.AndroidFontManager;
import org.microemu.android.device.AndroidInputMethod;
import org.microemu.android.device.ui.AndroidCommandUI;
import org.microemu.android.device.ui.AndroidDisplayableUI;
import org.microemu.android.util.AndroidLoggerAppender;
import org.microemu.android.util.AndroidRecordStoreManager;
import org.microemu.app.Common;
import org.microemu.app.util.MIDletSystemProperties;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;
import org.microemu.device.ui.CommandUI;
import org.microemu.log.Logger;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.Window;

public class MicroEmulator extends MicroEmulatorActivity {
	
	public static final String LOG_TAG = "MicroEmulator";
		
	protected Common common;

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
					return MicroEmulator.this.getAssets().open(name.substring(1));
				} else {
					return MicroEmulator.this.getAssets().open(name);
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
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
        Logger.removeAllAppenders();
        Logger.setLocationEnabled(false);
        Logger.addAppender(new AndroidLoggerAppender());
        
        System.setOut(new PrintStream(new OutputStream() {
        	
        	StringBuffer line = new StringBuffer();

			@Override
			public void write(int oneByte) throws IOException {
				if (((char) oneByte) == '\n') {
					Logger.debug(line.toString());
					line.delete(0, line.length() - 1);
				} else {
					line.append((char) oneByte);
				}
			}
        	
        }));
        
        System.setErr(new PrintStream(new OutputStream() {
        	
        	StringBuffer line = new StringBuffer();

			@Override
			public void write(int oneByte) throws IOException {
				if (((char) oneByte) == '\n') {
					Logger.debug(line.toString());
					line.delete(0, line.length() - 1);
				} else {
					line.append((char) oneByte);
				}
			}
        	
        }));
        
        java.util.List params = new ArrayList();
        params.add("--usesystemclassloader");
        params.add("--quit");
        
        String midletClassName;
		try {
			Class r = Class.forName(getComponentName().getPackageName() + ".R$string");
			Field[] fields = r.getFields();
			Class[] classes = r.getClasses();
	        midletClassName = getResources().getString(r.getField("class_name").getInt(null));

	        params.add(midletClassName);	       
		} catch (Exception e) {
			Logger.error(e);
			return;
		}

        Display display = getWindowManager().getDefaultDisplay();
        ((AndroidDeviceDisplay) emulatorContext.getDeviceDisplay()).displayRectangleWidth = display.getWidth();
        ((AndroidDeviceDisplay) emulatorContext.getDeviceDisplay()).displayRectangleHeight = display.getHeight() - 25;
        
        common = new Common(emulatorContext);
        common.setRecordStoreManager(new AndroidRecordStoreManager(this));
        common.setDevice(new AndroidDevice(emulatorContext, this));        
        common.initParams(params, null, AndroidDevice.class);
               
        System.setProperty("microedition.platform", "microemulator-android");
        System.setProperty("microedition.locale", Locale.getDefault().toString());
        
        /* JSR-75 */
        Map properties = new HashMap();
        properties.put("fsRoot", "/");
        properties.put("fsSingle", "sdcard");
        common.registerImplementation("org.microemu.cldc.file.FileSystem", properties, false);
        MIDletSystemProperties.setPermission("javax.microedition.io.Connector.file.read", 1);
        MIDletSystemProperties.setPermission("javax.microedition.io.Connector.file.write", 1);
        
        common.getLauncher().setSuiteName(midletClassName);
        common.initMIDlet(true);
    }
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			MIDletAccess ma = MIDletBridge.getMIDletAccess();
			if (ma == null) {
				return false;
			}
			final DisplayAccess da = ma.getDisplayAccess();
			if (da == null) {
				return false;
			}
			AndroidDisplayableUI ui = (AndroidDisplayableUI) da.getCurrentUI();
			if (ui == null) {
				return false;
			}		

			List<AndroidCommandUI> commands = ui.getCommandsUI();
			for (int i = 0; i < commands.size(); i++) {
				CommandUI cmd = commands.get(i);
				if (cmd.getCommand().getCommandType() == Command.BACK) {
					CommandListener l = ui.getCommandListener();
					l.commandAction(cmd.getCommand(), da.getCurrent());
					return true;
				}
			}			

			for (int i = 0; i < commands.size(); i++) {
				CommandUI cmd = commands.get(i);
				if (cmd.getCommand().getCommandType() == Command.EXIT) {
					moveTaskToBack(true);
					return true;
				}
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	private final static float TRACKBALL_THRESHOLD = 0.4f; 
	
	private float accumulatedTrackballX = 0;
	
	private float accumulatedTrackballY = 0;
	
	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			float x = event.getX();
			float y = event.getY();
			if ((x > 0 && accumulatedTrackballX < 0) || (x < 0 && accumulatedTrackballX > 0)) {
				accumulatedTrackballX = 0;
			}
			if ((y > 0 && accumulatedTrackballY < 0) || (y < 0 && accumulatedTrackballY > 0)) {
				accumulatedTrackballY = 0;
			}
			if (accumulatedTrackballX + x > TRACKBALL_THRESHOLD) {
				accumulatedTrackballX -= TRACKBALL_THRESHOLD;
				new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT).dispatch(getContentView());
				new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_RIGHT).dispatch(getContentView());
			} else if (accumulatedTrackballX + x < -TRACKBALL_THRESHOLD) {
				accumulatedTrackballX += TRACKBALL_THRESHOLD;
				new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT).dispatch(getContentView());
				new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_LEFT).dispatch(getContentView());
			}
			if (accumulatedTrackballY + y > TRACKBALL_THRESHOLD) {
				accumulatedTrackballY -= TRACKBALL_THRESHOLD;
				new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN).dispatch(getContentView());
				new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_DOWN).dispatch(getContentView());
			} else if (accumulatedTrackballY + y < -TRACKBALL_THRESHOLD) {
				accumulatedTrackballY += TRACKBALL_THRESHOLD;
				new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP).dispatch(getContentView());
				new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_UP).dispatch(getContentView());
			}
			accumulatedTrackballX += x;
			accumulatedTrackballY += y;
			
			return true;
		} else {
			return super.onTrackballEvent(event);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MIDletAccess ma = MIDletBridge.getMIDletAccess();
		if (ma == null) {
			return false;
		}
		final DisplayAccess da = ma.getDisplayAccess();
		if (da == null) {
			return false;
		}
		AndroidDisplayableUI ui = (AndroidDisplayableUI) da.getCurrentUI();
		if (ui == null) {
			return false;
		}		
		
		menu.clear();	
		boolean result = false;
		List<AndroidCommandUI> commands = ui.getCommandsUI();
		for (int i = 0; i < commands.size(); i++) {
			result = true;
			AndroidCommandUI cmd = commands.get(i);
			if (cmd.getCommand().getCommandType() != Command.BACK && cmd.getCommand().getCommandType() != Command.EXIT) {
				SubMenu item = menu.addSubMenu(Menu.NONE, i + Menu.FIRST, Menu.NONE, cmd.getCommand().getLabel());
				item.setIcon(cmd.getDrawable());
			}
		}

		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		MIDletAccess ma = MIDletBridge.getMIDletAccess();
		if (ma == null) {
			return false;
		}
		final DisplayAccess da = ma.getDisplayAccess();
		if (da == null) {
			return false;
		}
		AndroidDisplayableUI ui = (AndroidDisplayableUI) da.getCurrentUI();
		if (ui == null) {
			return false;
		}

		CommandListener l = ui.getCommandListener();
		int commandIndex = item.getItemId() - Menu.FIRST;
		List<AndroidCommandUI> commands = ui.getCommandsUI();
		CommandUI c = commands.get(commandIndex);

		if (c != null) {
			l.commandAction(c.getCommand(), da.getCurrent());
			return true;
		}

		return false;
	}

}