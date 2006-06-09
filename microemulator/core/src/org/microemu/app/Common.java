/*
 *  MicroEmulator
 *  Copyright (C) 2001-2003 Bartek Teodorczyk <barteo@barteo.net>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.microemu.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.microemu.DisplayComponent;
import org.microemu.EmulatorContext;
import org.microemu.MIDletBridge;
import org.microemu.MIDletEntry;
import org.microemu.MicroEmulator;
import org.microemu.RecordStoreManager;
import org.microemu.app.launcher.Launcher;
import org.microemu.app.ui.ResponseInterfaceListener;
import org.microemu.app.ui.StatusBarListener;
import org.microemu.app.ui.noui.NoUiDisplayComponent;
import org.microemu.app.util.Base64Coder;
import org.microemu.app.util.DeviceEntry;
import org.microemu.app.util.FileRecordStoreManager;
import org.microemu.app.util.MIDletClassLoader;
import org.microemu.app.util.MemoryRecordStoreManager;
import org.microemu.device.Device;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.DeviceFactory;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;
import org.microemu.device.j2se.J2SEDeviceDisplay;
import org.microemu.device.j2se.J2SEFontManager;
import org.microemu.device.j2se.J2SEInputMethod;
import org.microemu.util.JadMidletEntry;
import org.microemu.util.JadProperties;

//import com.barteo.emulator.app.capture.Capturer;

public class Common implements MicroEmulator {
	private static Common instance;

	private static Launcher launcher;

	private static StatusBarListener statusBarListener = null;

	protected EmulatorContext emulatorContext;

	protected JadProperties jad = new JadProperties();

	protected JadProperties manifest = new JadProperties();

	protected String captureFile = null;

	//  	private Capturer capturer = null;

	private RecordStoreManager recordStoreManager;

	private ResponseInterfaceListener responseInterfaceListener = null;

	public Common(EmulatorContext context) {
		instance = this;
		this.emulatorContext = context;

		launcher = new Launcher();
		launcher.setCurrentMIDlet(launcher);

		MIDletBridge.setMicroEmulator(this);
	}

	public RecordStoreManager getRecordStoreManager() {
		return recordStoreManager;
	}
    
    public void setRecordStoreManager(RecordStoreManager manager) {
        this.recordStoreManager = manager; 
    }

    public String getAppProperty(String key) {
		if (key.equals("microedition.platform")) {
			return "MicroEmulator";
		} else if (key.equals("microedition.profile")) {
			return "MIDP-1.0";
		} else if (key.equals("microedition.configuration")) {
			return "CLDC-1.0";
		} else if (key.equals("microedition.locale")) {
			return Locale.getDefault().getLanguage();
		} else if (key.equals("microedition.encoding")) {
			return System.getProperty("file.encoding");
		}

		String result = jad.getProperty(key);
		if (result == null) {
			result = manifest.getProperty(key);
		}
        
        return result; 
	}

	public void notifyDestroyed() {
		startMidlet(launcher);
	}

	public void notifySoftkeyLabelsChanged() {
	}

	public Launcher getLauncher() {
		return launcher;
	}

	public MIDlet loadMidlet(String name, Class midletClass) {
		MIDlet result;

		try {
			result = (MIDlet) midletClass.newInstance();
			launcher.addMIDletEntry(new MIDletEntry(name, result));
		} catch (Exception ex) {
			System.out.println("Cannot initialize " + midletClass
					+ " MIDlet class");
			System.out.println(ex);
			ex.printStackTrace();
			return null;
		}

		return result;
	}

	public static void openJadUrl(String urlString)
			throws MalformedURLException {
		try {
			URL url = new URL(urlString);
			setStatusBar("Loading...");
			getInstance().jad.clear();
			if (url.getUserInfo() == null) {
				getInstance().jad.load(url.openStream());
			} else {
				URLConnection cn = url.openConnection();
				String userInfo = new String(Base64Coder.encode(url
						.getUserInfo().getBytes("UTF-8")));
				cn.setRequestProperty("Authorization", "Basic " + userInfo);
				getInstance().jad.load(cn.getInputStream());
			}
			getInstance().loadFromJad(url);
		} catch (MalformedURLException ex) {
			throw ex;
		} catch (FileNotFoundException ex) {
			System.err.println("Cannot found " + urlString);
		} catch (NullPointerException ex) {
			ex.printStackTrace();
			System.err.println("Cannot open jad " + urlString);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			System.err.println("Cannot open jad " + urlString);
		} catch (IOException ex) {
			ex.printStackTrace();
			System.err.println("Cannot open jad " + urlString);
		}
	}

	public void startMidlet(MIDlet m) {
		try {
			launcher.setCurrentMIDlet(m);
			MIDletBridge.getMIDletAccess(m).startApp();
		} catch (MIDletStateChangeException ex) {
			System.err.println(ex);
		}
	}

	public void setStatusBarListener(StatusBarListener listener) {
		statusBarListener = listener;
	}

	public boolean platformRequest(String URL) {
		return false;
	}

	public void setResponseInterfaceListener(ResponseInterfaceListener listener) {
		responseInterfaceListener = listener;
	}

	protected void close() {
		if (captureFile != null) {
			//			if (capturer != null) {
			//				capturer.stopCapture(emulatorContext.getDisplayComponent());
			//			}
		}
	}

	protected void loadFromJad(URL jadUrl) {
		setResponseInterface(false);
		URL url = null;
		try {
			if (jadUrl.getProtocol().equals("file")) {
				String tmp = jadUrl.getFile();
				File f = new File(tmp.substring(0, tmp.lastIndexOf('/')), jad
						.getJarURL());
				url = f.toURL();
			} else {
				url = new URL(jad.getJarURL());
			}
		} catch (MalformedURLException ex) {
			System.err.println(ex);
			setResponseInterface(true);
		}

		final MIDletClassLoader loader = new MIDletClassLoader(url, getClass().getClassLoader());
		launcher.removeMIDletEntries();

		manifest.clear();
		try {
			manifest.load(loader.getResourceAsStream("/META-INF/MANIFEST.MF"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		Thread task = new Thread() {

			public void run() {
				launcher.setSuiteName(jad.getSuiteName());
				try {
					for (Enumeration e = jad.getMidletEntries().elements(); e
							.hasMoreElements();) {
						JadMidletEntry jadEntry = (JadMidletEntry) e
								.nextElement();
						Class midletClass = loader.loadClass(jadEntry
								.getClassName());
						loadMidlet(jadEntry.getName(), midletClass);
					}
					notifyDestroyed();
				} catch (ClassNotFoundException ex) {
					System.err.println(ex);
				}
				setStatusBar("");
				setResponseInterface(true);
			}

		};

		task.start();
	}
	
	protected Device getDevice() {
		return DeviceFactory.getDevice();
	}

	protected void setDevice(Device device) {
		if (captureFile != null) {
			//			if (capturer != null) {
			//				capturer.stopCapture(emulatorContext.getDisplayComponent());
			//			}
		}

		DeviceFactory.setDevice(device);

		if (captureFile != null) {
			//			if (capturer == null) {
			//				capturer = new Capturer();
			//			}
			//			capturer.startCapture(emulatorContext.getDisplayComponent(), captureFile);
		}
		
		device.init(emulatorContext);
	}

	private static Common getInstance() {
		return instance;
	}

	private static void setStatusBar(String text) {
		if (statusBarListener != null) {
			statusBarListener.statusBarChanged(text);
		}
	}

	private void setResponseInterface(boolean state) {
		if (responseInterfaceListener != null) {
			responseInterfaceListener.stateChanged(state);
		}
	}

	public static void main(String args[]) {
		Common app = new Common(new EmulatorContext() {
			private DisplayComponent displayComponent = new NoUiDisplayComponent();

			private InputMethod inputMethod = new J2SEInputMethod();

			private DeviceDisplay deviceDisplay = new J2SEDeviceDisplay(this);

			private FontManager fontManager = new J2SEFontManager();

			public DisplayComponent getDisplayComponent() {
				return displayComponent;
			}

			public Launcher getLauncher() {
				return getLauncher();
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
		});

		List params = new ArrayList();
		for (int i = 0; i < args.length; i++) {
			params.add(args[i]);
		}
		app.initDevice(params);
		if (app.getDevice() == null) {
			DeviceEntry entry = new DeviceEntry("Default device", null, "org.microemu.device.Device", true, false);
			List deviceParams = new ArrayList();
			deviceParams.add("-d");
			deviceParams.add(entry.getClassName());
			app.initDevice(deviceParams);
		}
		app.initMIDlet(params);
	}

	protected void initDevice(List params) {
		RecordStoreManager paramRecordStoreManager = null;
		
		Iterator it = params.iterator();
		while (it.hasNext()) {
			String tmp = (String) it.next();
			if (tmp.equals("-d") || tmp.equals("--device")) {
				it.remove();
				if (it.hasNext()) {
					try {
						Class deviceClass = Class.forName((String) it.next());
						setDevice((Device) deviceClass.newInstance());
					} catch (ClassNotFoundException ex) {
						ex.printStackTrace();
					} catch (InstantiationException ex) {
						ex.printStackTrace();
					} catch (IllegalAccessException ex) {
						ex.printStackTrace();
					} finally {
						it.remove();
					}
				}
			} else if (tmp.equals("--rms")) {
				it.remove();
				if (it.hasNext()) {
					String tmpRms = (String) it.next();
					it.remove();
					if (tmpRms.equals("file")) {
						paramRecordStoreManager = new FileRecordStoreManager(launcher);
					} else if (tmpRms.equals("memory")) {
						paramRecordStoreManager = new MemoryRecordStoreManager();
					}
				}
			}
		}

		if (getRecordStoreManager() == null) {
			if (paramRecordStoreManager == null) {
				setRecordStoreManager(new FileRecordStoreManager(launcher));
			} else {
				setRecordStoreManager(paramRecordStoreManager);
			}
		}		
	}
	
	protected void initMIDlet(List params) {
		MIDlet m = null;
		Iterator it = params.iterator();
		while (it.hasNext()) {
			String test = (String) it.next();
			it.remove();
			if (m == null && test.endsWith(".jad")) {
				try {
					File file = new File(test);
					String url = file.exists() ? file.toURL().toString()
							: test;
					openJadUrl(url);
				} catch (MalformedURLException exception) {
					System.out.println("Cannot parse " + test + " URL");
				}
			} else {
				Class midletClass;
				try {
					midletClass = Class.forName(test);
					m = loadMidlet("MIDlet", midletClass);
				} catch (ClassNotFoundException ex) {
					System.out.println("Cannot find " + test
							+ " MIDlet class");
				}
			}
		}

		if (m == null) {
			m = getLauncher();
		}

		if (m != null) {
			startMidlet(m);
		}

	}
	
	
	public static String usage()
	{
		return
			"[(-d | --device) {device class name} ] " +
			"[--rms (file | memory)] " +
			"{midlet class name | jad file location}";
	}

}
