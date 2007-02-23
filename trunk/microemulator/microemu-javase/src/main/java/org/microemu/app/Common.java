/**
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
 *  
 *  @version $Id$
 */
package org.microemu.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.zip.ZipException;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.microemu.EmulatorContext;
import org.microemu.Injected;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.MIDletEntry;
import org.microemu.MicroEmulator;
import org.microemu.RecordStoreManager;
import org.microemu.app.classloader.ExtensionsClassLoader;
import org.microemu.app.classloader.MIDletClassLoader;
import org.microemu.app.launcher.Launcher;
import org.microemu.app.ui.Message;
import org.microemu.app.ui.ResponseInterfaceListener;
import org.microemu.app.ui.StatusBarListener;
import org.microemu.app.util.DeviceEntry;
import org.microemu.app.util.FileRecordStoreManager;
import org.microemu.app.util.IOUtils;
import org.microemu.app.util.MIDletResourceLoader;
import org.microemu.app.util.MIDletSystemProperties;
import org.microemu.app.util.MidletURLReference;
import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.device.impl.DeviceImpl;
import org.microemu.log.Logger;
import org.microemu.microedition.ImplFactory;
import org.microemu.microedition.ImplementationInitialization;
import org.microemu.microedition.io.ConnectorImpl;
import org.microemu.util.Base64Coder;
import org.microemu.util.JadMidletEntry;
import org.microemu.util.JadProperties;
import org.microemu.util.MemoryRecordStoreManager;

//import com.barteo.emulator.app.capture.Capturer;

public class Common implements MicroEmulator, CommonInterface {
	
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

		/* Initialize secutity context for implemenations, May be there are better place for this call */
		ImplFactory.instance();
		ImplFactory.registerGCF(ImplFactory.DEFAULT, new ConnectorImpl());

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
			Logger.error(ex);
		}
		// TODO to be removed when event dispatcher will run input method task
		DeviceFactory.getDevice().getInputMethod().dispose();
	}


	public static boolean isJadExtension(String nameString) {
		if (nameString == null) {
			return false;
		}
		int end = nameString.lastIndexOf('.');
		if (end == -1) {
			return false;
		}
		return nameString.substring(end + 1, nameString.length()).toLowerCase(Locale.ENGLISH).equals("jad");
	}
	
	/**
	 * TODO add proper Error handling and display in this function. 
	 */
	public static void openJadUrlSafe(String urlString) {
		try {
			openJadUrl(urlString);
		} catch (IOException e) {
			Message.error("Unable to open jad " + urlString, e);
		}
	}
	
	public static void openJadUrl(String urlString)
			throws IOException {
		openJadUrl(urlString, createMIDletClassLoader());
	}

	public static void openJadUrl(String urlString, MIDletClassLoader midletClassLoader)
			throws IOException {
		try {
			Logger.debug("openJad", urlString);
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
			getInstance().loadFromJad(urlString, midletClassLoader);
			
			Config.getUrlsMRU().push(new MidletURLReference(getInstance().jad.getSuiteName(), urlString));
			
		} catch (MalformedURLException ex) {
			throw ex;
		} catch (ClassNotFoundException ex) {
			Logger.error(ex);
			throw new IOException(ex.getMessage());
		} catch (FileNotFoundException ex) {
			Logger.error("Not found", urlString, ex);
		} catch (NullPointerException ex) {
			Logger.error("Cannot open jad", urlString, ex);
		} catch (IllegalArgumentException ex) {
			Logger.error("Cannot open jad", urlString, ex);
		}
	}

	public void startMidlet(Class midletClass, MIDletAccess previousMidletAccess) {
		MIDlet m;
		final String errorTitle = "Error starting MIDlet";
		try {
			Object object = midletClass.newInstance();
			if (!(object instanceof MIDlet)) {
				Message.error(errorTitle, "Class " + midletClass.getName() + " should extend MIDlet");
				return;
			}
			m = (MIDlet)object;
		} catch (Throwable e) {
			Message.error(errorTitle, "Unable to create MIDlet, " + Message.getCauseMessage(e), e);
			return;
		}

		try {
			if (previousMidletAccess != null) {
				previousMidletAccess.destroyApp(true);
			}
			MIDletBridge.getMIDletAccess(m).startApp();
			launcher.setCurrentMIDlet(m);
		} catch (Throwable e) {
			Message.error(errorTitle, "Unable to start MIDlet, " + Message.getCauseMessage(e), e);
		}
	}

	protected void startLauncher(MIDletAccess previousMidletAccess) {
		try {
			if (previousMidletAccess != null) {
				previousMidletAccess.destroyApp(true);
			}
		} catch (Throwable e) {
		    Logger.error("destroyApp error", e);
		}
		
		try {
			MIDletBridge.getMIDletAccess(launcher).startApp();
			launcher.setCurrentMIDlet(launcher);
		} catch (Throwable e) {
			Message.error("Unable to start launcher MIDlet, " + Message.getCauseMessage(e), e);
			handleStartMidletException(e);
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

	protected void handleStartMidletException(Throwable e) {

	}

	/**
	 * Show message describing problem with jar if any
	 */
	protected boolean describeJarProblem(URL jarUrl , MIDletClassLoader midletClassLoader) {
		InputStream is = null;
		JarInputStream jis = null;
		try {
			final String message = "Unable to open jar " + jarUrl;
			URLConnection conn;
			try {
				conn = jarUrl.openConnection();
			} catch (IOException e) {
				Message.error(message, e);
				return true;
			}
			try {
				is = conn.getInputStream();
			} catch (FileNotFoundException e) {
				Message.error("The system cannot find the jar file " + jarUrl, e);
				return true;
			} catch (IOException e) {
				Message.error(message, e);
				return true;
			}
			try {
				jis = new JarInputStream(is);
			} catch (IOException e) {
				Message.error(message, e);
				return true;
			}
			try {
				JarEntry entry = jis.getNextJarEntry();
				if (entry == null) {
					Message.error("Empty jar " + jarUrl);
					return true;
				}
				// Read till the end
				while (jis.getNextJarEntry() != null);
			} catch (ZipException e) {
				Message.error("Problem reading jar " + jarUrl, e);
				return true;
			} catch (IOException e) {
				Message.error("Problem reading jar " + jarUrl, e);
				return true;
			}
			// There seems to be no poblem with jar
			return false;
		} finally {
			IOUtils.closeQuietly(jis);
			IOUtils.closeQuietly(is);
		}		
	}
	
	protected void loadFromJad(String jadUrl, MIDletClassLoader midletClassLoader) throws ClassNotFoundException {
		if (jad.getJarURL() == null) {
			throw new ClassNotFoundException("Cannot find MIDlet-Jar-URL property in jad");
		}
		Logger.debug("openJar", jad.getJarURL());
		MIDletAccess previousMidletAccess = MIDletBridge.getMIDletAccess();
		MIDletBridge.clear();
		setResponseInterface(false);
		try {
			URL url = null;
			try {
				url = new URL(jad.getJarURL());
			} catch (MalformedURLException ex) {
				try {
					url = new URL(jadUrl.substring(0, jadUrl.lastIndexOf('/') + 1) + jad.getJarURL());
					Logger.debug("openJar url", url);
				} catch (MalformedURLException ex1) {
					Logger.error("Unable to find jar url", ex1);
					setResponseInterface(true);
				}
			}

			midletClassLoader.addURL(url);

			launcher.removeMIDletEntries();

			manifest.clear();
			InputStream is = null;
			try {
				is = midletClassLoader.getResourceAsStream("META-INF/MANIFEST.MF");
				if (is == null) {
					if (!describeJarProblem(url, midletClassLoader)) {
						Message.error("Unable to find MANIFEST in MIDlet jar");
					}
					return;
				}
				manifest.load(is);
			} catch (IOException e) {
				Message.error("Unable to read MANIFEST", e);
			} finally {
				IOUtils.closeQuietly(is);
			}

			launcher.setSuiteName(jad.getSuiteName());

			for (Enumeration e = jad.getMidletEntries().elements(); e.hasMoreElements();) {
				JadMidletEntry jadEntry = (JadMidletEntry) e.nextElement();
				Class midletClass = midletClassLoader.loadClass(jadEntry.getClassName());
				loadMidlet(jadEntry.getName(), midletClass);
			}
			notifyDestroyed(previousMidletAccess);

			setStatusBar("");
		} finally {
			setResponseInterface(true);
		}
	}

	public Device getDevice() {
		return DeviceFactory.getDevice();
	}

	protected void setDevice(Device device) {
		MIDletSystemProperties.setDevice(device);
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

	private void registerImplementation(String implClassName) {
		final String errorText = "Implementation initialization";
		try {
			Class implClass = getClass().getClassLoader().loadClass(implClassName);
			Object inst = implClass.newInstance();
			if (inst instanceof ImplementationInitialization) {
				((ImplementationInitialization)inst).registerImplementation();
				Logger.debug("Implementation registered", implClassName);
			} else {
				Logger.debug("Implementation does not implement", ImplementationInitialization.class.getName());
			}
		} catch (ClassNotFoundException e) {
			Logger.error(errorText, e);
		} catch (InstantiationException e) {
			Logger.error(errorText, e);
		} catch (IllegalAccessException e) {
			Logger.error(errorText, e);
		}
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
			} else if (tmp.equals("--impl")) {
				it.remove();
				registerImplementation((String)it.next());
				it.remove();
			}
		}

		//TODO registerImplementations by reading jar files in classpath.

		ClassLoader classLoader = getClass().getClassLoader();
		if (classLoader == null) {
			classLoader = ClassLoader.getSystemClassLoader();
		}
		if (deviceDescriptorLocation != null) {
			try {
				setDevice(DeviceImpl.create(
						emulatorContext,
						classLoader,
						deviceDescriptorLocation));
			} catch (IOException ex) {
				Logger.error(ex);
			}
		}
		if (DeviceFactory.getDevice() == null) {
			try {
				if (deviceClass == null) {
					if (defaultDevice.getFileName() != null) {
						URL[] urls = new URL[1];
						urls[0] = new File(Config.getConfigPath(), defaultDevice.getFileName()).toURI().toURL();
						classLoader = createExtensionsClassLoader(urls);
					}
					setDevice(DeviceImpl.create(
							emulatorContext,
							classLoader,
							defaultDevice.getDescriptorLocation()));
				} else {
					DeviceImpl device = (DeviceImpl) deviceClass.newInstance();
					device.init(emulatorContext);
					setDevice(device);
				}
			} catch (InstantiationException ex) {
				Logger.error(ex);
			} catch (IllegalAccessException ex) {
				Logger.error(ex);
			} catch (IOException ex) {
				Logger.error(ex);
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

	private static MIDletClassLoader createMIDletClassLoader() {
		MIDletClassLoader mcl = new MIDletClassLoader(instance.getClass().getClassLoader());
		if (!Serializable.class.isAssignableFrom(Injected.class)) {
			Logger.error("classpath configuration error, Wrong Injected class detected. microemu-injected module should be after microemu-javase in eclipse");
		}
		mcl.disableClassPreporcessing(Injected.class);
		MIDletResourceLoader.classLoader = mcl;
		return mcl;
	}
	
	public static ClassLoader createExtensionsClassLoader(final URL[] urls) {
		return new ExtensionsClassLoader(urls, instance.getClass().getClassLoader());
	}

	public void initMIDlet(List params, boolean startMidlet) {
		Class midletClass = null;
		Vector appclasses = new Vector();
		Vector appclasspath = new Vector();
		
		Iterator it = params.iterator();
		while (it.hasNext()) {
			String test = (String) it.next();
			it.remove();
			if (test.equals("--appclasspath")) {
				appclasspath.add(it.next());
				it.remove();
			} else if (test.equals("--appclass")) {
				appclasses.add(it.next());
				it.remove();
			} else if (midletClass == null && test.endsWith(".jad")) {
				try {
					File file = new File(test);
					String url = file.exists() ? IOUtils.getCanonicalFileURL(file) : test;
					openJadUrl(url);
				} catch (IOException exception) {
					Logger.error("Cannot load " + test + " URL", exception);
				}
			} else {
				MIDletClassLoader classLoader = createMIDletClassLoader();
				try {
					classLoader.addClassURL(test);
					for (Iterator iter = appclasses.iterator(); iter.hasNext();) {
						classLoader.addClassURL((String) iter.next());
						
					}
					for (Iterator iter = appclasspath.iterator(); iter.hasNext();) {
						// TODO add appclasspath parts separators and path with spaces ''
						classLoader.addURL(new URL(IOUtils.getCanonicalFileURL(new File((String)iter.next()))));
					}					
					
					midletClass = classLoader.loadClass(test);
				} catch (MalformedURLException e) {
					Message.error("Error", "Unable to find MIDlet class, " + Message.getCauseMessage(e), e);
				} catch (ClassNotFoundException e) {
					Message.error("Error", "Unable to find MIDlet class, " + Message.getCauseMessage(e), e);
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


	public static String usage() {
		return
			"[(-d | --device) ({device descriptor} | {device class name}) ] " +
			"[--rms (file | memory)] " +
			"[--impl {JSR implementation class name}]" +
			"[--appclasspath <MIDlet CLASSPATH>]" +
			"[--appclass <library class name>]" +
			"({MIDlet class name} | {jad file location})";
	}

}
