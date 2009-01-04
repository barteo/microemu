/**
 *  MicroEmulator
 *  Copyright (C) 2008 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2008 Markus Heberling <markus@heberling.net>
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
package org.microemu.iphone;

import static joc.Static.YES;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import joc.Message;
import joc.Scope;
import obc.CGRect;
import obc.UIApplication;
import obc.UIHardware;
import obc.UIWindow;

import org.microemu.DisplayComponent;
import org.microemu.MIDletBridge;
import org.microemu.MIDletContext;
import org.microemu.RecordStoreManager;
import org.microemu.app.CommonInterface;
import org.microemu.app.launcher.Launcher;
import org.microemu.device.Device;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.DeviceFactory;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;
import org.microemu.iphone.device.IPhoneDevice;
import org.microemu.iphone.device.IPhoneDeviceDisplay;
import org.microemu.iphone.device.IPhoneFontManager;
import org.microemu.iphone.device.IPhoneInputMethod;
import org.microemu.iphone.device.IPhoneRecordStoreManager;
import org.microemu.iphone.device.ui.AbstractUI;

import com.saurik.uicaboodle.Main;

public class MicroEmulator extends UIApplication {
	private final class IPhoneCommon implements CommonInterface, org.microemu.MicroEmulator {

		private IPhoneLauncher launcher;

		public IPhoneCommon(Device device) {
			DeviceFactory.setDevice(device);

			MIDletBridge.setMicroEmulator(this);
		}

		public void initMIDlet(boolean startMidlet) {
			if (launcher == null)
				launcher = new IPhoneLauncher(this);

			if (launcher.getSelectedMidletEntry() == null) {
				try {
					MIDletBridge.getMIDletAccess(launcher).startApp();
					launcher.setCurrentMIDlet(launcher);
				} catch (MIDletStateChangeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					MIDlet midlet = (MIDlet) launcher.getSelectedMidletEntry().getMIDletClass().newInstance();
					MIDletBridge.getMIDletAccess(midlet).startApp();
					launcher.setCurrentMIDlet(midlet);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		public void destroyMIDletContext(MIDletContext midletContext) {
			// TODO Auto-generated method stub

		}

		public String getAppProperty(String key) {
			// TODO Auto-generated method stub
			return null;
		}

		public Launcher getLauncher() {
			return launcher;
		}

		public RecordStoreManager getRecordStoreManager() {
			// TODO Auto-generated method stub
			return new IPhoneRecordStoreManager(MicroEmulator.this);
		}

		public InputStream getResourceAsStream(String name) {
			return MIDletBridge.getCurrentMIDlet().getClass().getResourceAsStream(name);
		}

		public void notifyDestroyed(MIDletContext midletContext) {
			System.out.println("IPhoneCommon.notifyDestroyed()");
			launcher = null;
			initMIDlet(true);
		}

		public int checkPermission(String permission) {
			// TODO

			return 0;
		}

		public boolean platformRequest(String URL) {
			// TODO Auto-generated method stub
			return false;
		}

	}

	private static final class ExceptionHandler implements Thread.UncaughtExceptionHandler {
		public void uncaughtException(Thread arg0, Throwable arg1) {
			System.err.println("Uncaught exception in thread: " + arg0.getName());
			arg1.printStackTrace();
			System.exit(-1);
		}
	}

	public static void main(String[] args) throws Exception {
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
		// Main.main(new String[] { HelloJava.class.getName() });
		Main.main(new String[] { MicroEmulator.class.getName() });
	}

	@Message
	public void applicationDidFinishLaunching$(Object unused) throws Exception {
		CGRect outer = UIHardware.$fullScreenApplicationContentRect();
		window = new UIWindow().initWithContentRect$(outer);

		window.orderFront$(this);
		window.makeKeyAndVisible();

		init(Arrays.asList("--usesystemclassloader"));
	}

	private IPhoneInputMethod inputMethod = new IPhoneInputMethod();

	private IPhoneDeviceDisplay deviceDisplay;

	private IPhoneFontManager fontManager = new IPhoneFontManager();

	private UIWindow window;
	private static IPhoneCommon common;

	public void init(List<String> params) {
		common = new IPhoneCommon(new IPhoneDevice(this));
		deviceDisplay = new IPhoneDeviceDisplay(common);
		deviceDisplay.displayRectangleWidth = (int) getWindow().bounds().size.width;
		deviceDisplay.displayRectangleHeight = (int) getWindow().bounds().size.height - AbstractUI.TOOLBAR_HEIGHT;

		System.setProperty("microedition.platform", "microemulator-iphone");
		System.setProperty("microedition.locale", Locale.getDefault().toString());

		Launcher.setSuiteName("MicroEmulator for iPhone");

		// don't know why this is needed...
		postFromNewTread(new Runnable() {
			public void run() {
				common.initMIDlet(true);
			}
		});

	}

	private Runnable runnable;

	public void postFromNewTread(final Runnable r) {
		Scope scope = new Scope();
		new Thread(new Runnable() {
			public void run() {
				Scope scope = new Scope();
				post(r);
				scope.close();
			}
		}).start();
		scope.close();
	}

	public synchronized boolean post(Runnable r) {
		runnable = r;
		this.performSelectorOnMainThread$withObject$waitUntilDone$(new joc.Selector("handle"), null, YES);
		return true;
	}

	public static CommonInterface getCommon() {
		return common;
	}

	@Message
	public void handle() {
		runnable.run();
	}

	public UIWindow getWindow() {
		return window;
	}

	public IPhoneFontManager getFontManager() {
		return fontManager;
	}
	public IPhoneInputMethod getInputMethod() {
		return inputMethod;
	}
	public IPhoneDeviceDisplay getDeviceDisplay() {
		return deviceDisplay;
	}
}