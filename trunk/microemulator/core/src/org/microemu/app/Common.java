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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
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
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.MIDletEntry;
import org.microemu.MicroEmulator;
import org.microemu.RecordStoreManager;
import org.microemu.app.launcher.Launcher;
import org.microemu.app.ui.Message;
import org.microemu.app.ui.ResponseInterfaceListener;
import org.microemu.app.ui.StatusBarListener;
import org.microemu.app.ui.noui.NoUiDisplayComponent;
import org.microemu.app.util.DeviceEntry;
import org.microemu.app.util.FileRecordStoreManager;
import org.microemu.app.util.IOUtils;
import org.microemu.app.util.MIDletClassLoader;
import org.microemu.device.Device;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.DeviceFactory;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;
import org.microemu.device.j2se.J2SEDeviceDisplay;
import org.microemu.device.j2se.J2SEFontManager;
import org.microemu.device.j2se.J2SEInputMethod;
import org.microemu.log.Logger;
import org.microemu.util.Base64Coder;
import org.microemu.util.JadMidletEntry;
import org.microemu.util.JadProperties;
import org.microemu.util.MemoryRecordStoreManager;

//import com.barteo.emulator.app.capture.Capturer;

public class Common implements MicroEmulator {
	private static Common instance;

	private static Launcher launcher;

	private static StatusBarListener statusBarListener = null;

	protected EmulatorContext emulatorContext;

	protected JadProperties jad = new JadProperties();

	protected JadProperties manifest = new JadProperties();

	private RecordStoreManager recordStoreManager;

	private ResponseInterfaceListener responseInterfaceListener = null;

	public Common(EmulatorContext context) {
		instance = this;
		this.emulatorContext = context;

		launcher = new Launcher(this);
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

	public void notifyDestroyed(MIDletAccess previousMidletAccess) {
		startLauncher(previousMidletAccess);
	}

	public Launcher getLauncher() {
		return launcher;
	}

	public void loadMidlet(String name, Class midletClass) {
		launcher.addMIDletEntry(new MIDletEntry(name, midletClass));
	}
	
	public static void dispose()
	{
		try {
			MIDletBridge.getMIDletAccess().destroyApp(true);
		} catch (MIDletStateChangeException ex) {
			ex.printStackTrace();
		}
		// TODO to be removed when event dispatcher will run input method task
		DeviceFactory.getDevice().getInputMethod().dispose();
	}
	
	
	public static void openJadUrl(String urlString)
			throws IOException {
		openJadUrl(urlString, new MIDletClassLoader(instance.getClass().getClassLoader()));
	}

	public static void openJadUrl(String urlString, MIDletClassLoader midletClassLoader)
			throws IOException {
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
			getInstance().loadFromJad(url, midletClassLoader);
		} catch (MalformedURLException ex) {
			throw ex;
		} catch (ClassNotFoundException ex) {
			Logger.error(ex);
			throw new IOException(ex.getMessage());
		} catch (FileNotFoundException ex) {
			System.err.println("Cannot found " + urlString);
		} catch (NullPointerException ex) {
			ex.printStackTrace();
			System.err.println("Cannot open jad " + urlString);
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			System.err.println("Cannot open jad " + urlString);
		}
	}

	public void startMidlet(Class midletClass, MIDletAccess previousMidletAccess) {
		try {
			MIDlet m = (MIDlet) midletClass.newInstance();
			if (previousMidletAccess != null) {
				previousMidletAccess.destroyApp(true);
			}
			MIDletBridge.getMIDletAccess(m).startApp();
			launcher.setCurrentMIDlet(m);
		} catch (Throwable e) {
			String text;
			if (e.getCause() != null) {
				text = e.getCause().toString();
			} else {
				text = e.toString();
			}
			Message.error("Error starting MIDlet", "Can't start MIDlet " + text, e);
		}
	}

	protected void startLauncher(MIDletAccess previousMidletAccess) {
		try {
			if (previousMidletAccess != null) {
				previousMidletAccess.destroyApp(true);
			}
			MIDletBridge.getMIDletAccess(launcher).startApp();
			launcher.setCurrentMIDlet(launcher);
		} catch (MIDletStateChangeException ex) {
			handleStartMidletException(ex);
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
	
	protected void handleStartMidletException(MIDletStateChangeException ex) {
		ex.printStackTrace();
	}

	protected void loadFromJad(URL jadUrl, MIDletClassLoader midletClassLoader) throws ClassNotFoundException{
		if (jad.getJarURL() == null) {
			throw new ClassNotFoundException("Cannot find MIDlet-Jar-URL property in jad");
		}
		
		MIDletAccess previousMidletAccess = MIDletBridge.getMIDletAccess();
		
		MIDletBridge.clear();

		setResponseInterface(false);
		URL url = null;
		try {
			url = new URL(jad.getJarURL());
		} catch (MalformedURLException ex) {
			try {
				String urlFullPath = jadUrl.toExternalForm();
				url = new URL(urlFullPath.substring(0, urlFullPath.lastIndexOf('/') + 1) + jad.getJarURL());
			} catch (MalformedURLException ex1) {
				ex1.printStackTrace();
				setResponseInterface(true);
			}
		}
		
		try {
			midletClassLoader.addURL(url);
		} catch (IOException ex) {
			throw new ClassNotFoundException(ex.getMessage());
		}
		launcher.removeMIDletEntries();

		manifest.clear();
		InputStream is = null;
		try {
			manifest.load(is = midletClassLoader.getResourceAsStream("/META-INF/MANIFEST.MF"));
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			IOUtils.closeQuietly(is);
		}

		launcher.setSuiteName(jad.getSuiteName());

		for (Enumeration e = jad.getMidletEntries().elements(); e
				.hasMoreElements();) {
			JadMidletEntry jadEntry = (JadMidletEntry) e.nextElement();
			Class midletClass = midletClassLoader.loadClass(jadEntry.getClassName());
			loadMidlet(jadEntry.getName(), midletClass);
		}
		notifyDestroyed(previousMidletAccess);

		setStatusBar("");
		setResponseInterface(true);
	}
	
	public Device getDevice() {
		return DeviceFactory.getDevice();
	}
	
	protected void setDevice(Device device) {
		DeviceFactory.setDevice(device);
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
		DeviceEntry defaultDevice = new DeviceEntry("Default device", null, Device.DEFAULT_LOCATION, true, false);
		app.initDevice(params, defaultDevice);

		app.initMIDlet(params, false);
	}

	public void initDevice(List params, DeviceEntry defaultDevice) {
		RecordStoreManager paramRecordStoreManager = null;		
		Class deviceClass = null;
		String deviceDescriptorLocation = null;		
		
		Iterator it = params.iterator();
		while (it.hasNext()) {
			String tmp = (String) it.next();
			if (tmp.equals("-d") || tmp.equals("--device")) {
				it.remove();
				if (it.hasNext()) {
					String tmpDevice = (String) it.next();
					it.remove();
					if (!tmpDevice.toLowerCase().endsWith(".xml")) {
						try {
							deviceClass = Class.forName(tmpDevice);
						} catch (ClassNotFoundException ex) {
						}
					}
					if (deviceClass == null) {
						deviceDescriptorLocation = tmpDevice;
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
		ClassLoader classLoader = getClass().getClassLoader();
		if (classLoader == null) {
			classLoader = ClassLoader.getSystemClassLoader();
		}
		if (deviceDescriptorLocation != null) {
			try {
				setDevice(Device.create(
						emulatorContext, 
						classLoader, 
						deviceDescriptorLocation));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		if (DeviceFactory.getDevice() == null) {
			try {
				if (deviceClass == null) {
					if (defaultDevice.getFileName() != null) {
						URL[] urls = new URL[1];					
						urls[0] = new File(Config.getConfigPath(), defaultDevice.getFileName()).toURL();
						classLoader = new URLClassLoader(urls);
					}
					setDevice(Device.create(
							emulatorContext, 
							classLoader, 
							defaultDevice.getDescriptorLocation()));
				} else {
					Device device = (Device) deviceClass.newInstance();
					device.init(emulatorContext);
					setDevice(device);
				}
			} catch (InstantiationException ex) {
				ex.printStackTrace();
			} catch (IllegalAccessException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
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
	
	public void initMIDlet(List params) {
		initMIDlet(params, false);
	}
	
	public void initMIDlet(List params, boolean startMidlet) {
		Class midletClass = null;
		Iterator it = params.iterator();
		while (it.hasNext()) {
			String test = (String) it.next();
			it.remove();
			if (midletClass == null && test.endsWith(".jad")) {
				try {
					File file = new File(test);
					String url = file.exists() ? file.toURL().toString()
							: test;
					openJadUrl(url);
				} catch (IOException exception) {
					System.out.println("Cannot load " + test + " URL");
				}
			} else {
				// Find if we can load Midlet using MIDletClassLoader
				String resource = "/" + test.replace('.', '/') + ".class";
				URL url = this.getClass().getResource(resource); 
			    if (url != null) {
			    	String path = url.toExternalForm();
			    	String basePath = path.substring(0, path.length() - resource.length());
			    	MIDletClassLoader classLoader = new MIDletClassLoader(instance.getClass().getClassLoader());
			    	try {
			    		classLoader.addURL(new URL(basePath));
				    	midletClass = classLoader.loadClass(test);
				    	if (MIDletClassLoader.debug) {
				    		System.out.println("Use " + basePath + " to load MIDlets");
				    	}
			    	} catch (ClassNotFoundException ignore) {
			    		if (MIDletClassLoader.debug) {
			    			ignore.printStackTrace();
			    		}
			    	} catch (MalformedURLException ignore) {
			    		if (MIDletClassLoader.debug) {
			    			ignore.printStackTrace();
			    		}
					} catch (IOException ignore) {
						if (MIDletClassLoader.debug) {
							ignore.printStackTrace();
						}
					}
			    }
			    
			    if (midletClass == null) {
					try {
						midletClass = Class.forName(test);
					} catch (ClassNotFoundException ex) {
						System.out.println("Cannot find " + test + " MIDlet class");
					}
				}
			}
		}

		if (midletClass == null) {
			MIDletEntry entry = launcher.getSelectedMidletEntry();
			if (startMidlet && entry != null) {
				startMidlet(entry.getMIDletClass(), null);
			} else {
				startLauncher(null);
			}
		} else {
			startMidlet(midletClass, null);
		}

	}
	
	
	public static String usage()
	{
		return
			"[(-d | --device) ({device descriptor} | {device class name}) ] " +
			"[--rms (file | memory)] " +
			"({midlet class name} | {jad file location})";
	}

}
