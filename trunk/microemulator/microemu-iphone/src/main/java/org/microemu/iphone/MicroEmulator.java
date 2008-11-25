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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import joc.Message;
import joc.Scope;
import obc.CGRect;
import obc.UIApplication;
import obc.UIHardware;
import obc.UIWindow;

import org.microemu.DisplayComponent;
import org.microemu.EmulatorContext;
import org.microemu.app.Common;
import org.microemu.app.launcher.Launcher;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;
import org.microemu.iphone.device.IPhoneDevice;
import org.microemu.iphone.device.IPhoneDeviceDisplay;
import org.microemu.iphone.device.IPhoneFontManager;
import org.microemu.iphone.device.IPhoneInputMethod;
import org.microemu.iphone.device.IPhoneRecordStoreManager;
import org.microemu.iphone.device.ui.AbstractUI;
import org.microemu.midp.examples.simpledemo.SimpleDemoMIDlet;

import com.saurik.uicaboodle.Main;

public class MicroEmulator extends UIApplication {
	private static final class ExceptionHandler implements Thread.UncaughtExceptionHandler {
		public void uncaughtException(Thread arg0, Throwable arg1) {
			System.err.println("Uncaught exception in thread: " + arg0.getName());
			arg1.printStackTrace();
			System.exit(-1);
		}
	}

	public static void main(String[] args) throws Exception {
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
//		Main.main(new String[] { HelloJava.class.getName() });
		Main.main(new String[] { MicroEmulator.class.getName() });
	}

	@Message
	public void applicationDidFinishLaunching$(Object unused) throws Exception {
		CGRect outer = UIHardware.$fullScreenApplicationContentRect();
		window = new UIWindow().initWithContentRect$(outer);

		window.orderFront$(this);
		window.makeKeyAndVisible();
	
		init(SimpleDemoMIDlet.class.getName(), Arrays.asList("--usesystemclassloader", SimpleDemoMIDlet.class.getName()));
	}
	
	protected EmulatorContext emulatorContext = new EmulatorContext() {
		private InputMethod inputMethod = new IPhoneInputMethod();

		private DeviceDisplay deviceDisplay = new IPhoneDeviceDisplay(this);

		private FontManager fontManager = new IPhoneFontManager();

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
			if (name.startsWith("/")) {
				return getClass().getResourceAsStream(name);
			} else {
				return getClass().getResourceAsStream("/" + name);
			}
		}

	};

	private UIWindow window;
	private Common common;


	public void init(String midletClassName, List<String> params) {
		((IPhoneDeviceDisplay) emulatorContext.getDeviceDisplay()).displayRectangleWidth = (int) getWindow().bounds().size.width;
		((IPhoneDeviceDisplay) emulatorContext.getDeviceDisplay()).displayRectangleHeight = (int) getWindow().bounds().size.height-AbstractUI.TOOLBAR_HEIGHT;

		common = new Common(emulatorContext);
		common.setRecordStoreManager(new IPhoneRecordStoreManager(this));
		common.setDevice(new IPhoneDevice(emulatorContext, this));
		common.initParams(new ArrayList<String>(params), null, IPhoneDevice.class);

		System.setProperty("microedition.platform", "microemulator-iphone");
		System.setProperty("microedition.locale", Locale.getDefault().toString());

		Launcher.setSuiteName(midletClassName);
		
		//don't know why this is needed...
		new Thread(new Runnable() {
			public void run() {
				new Scope();
				post(new Runnable() {
					public void run() {
						new Scope();
						common.initMIDlet(true);
					}
				});
			}
		}).start();
	}

	private Runnable runnable;

	public synchronized boolean post(Runnable r) {
		runnable = r;
		this.performSelectorOnMainThread$withObject$waitUntilDone$(new joc.Selector("handle"), null, YES);
		return true;
	}

	@Message
	public void handle() {
		runnable.run();
	}
	
	public UIWindow getWindow() {
		return window;
	}

}