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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
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
import org.microemu.MIDletContext;
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
import org.microemu.app.util.MIDletThread;
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

public class Common implements MicroEmulator, CommonInterface {
	
	private static Common instance;

	private static Launcher launcher;

	private static StatusBarListener statusBarListener = null;

	protected EmulatorContext emulatorContext;

	protected JadProperties jad = new JadProperties();

	protected JadProperties manifest = new JadProperties();

	private RecordStoreManager recordStoreManager;

	private ResponseInterfaceListener responseInterfaceListener = null;

	private ExtensionsClassLoader extensionsClassLoader;

	private boolean useSystemClassLoader = false;
	
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
		} else if (key.equals("microedition.profiles")) {
			return "MIDP-2.0";
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

	public void notifyDestroyed(MIDletContext midletContext) {
		startLauncher(midletContext);
	}

	public void destroyMIDletContext(MIDletContext midletContext) {
		MIDletThread.contextDestroyed(midletContext);
	}
	
	public Launcher getLauncher() {
		return launcher;
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
			setStatusBar("Loading...");
			getInstance().jad.clear();
			getInstance().jad = loadJadProperties(urlString);
			
			getInstance().loadFromJad(urlString, midletClassLoader);
			
			Config.getUrlsMRU().push(new MidletURLReference(getInstance().jad.getSuiteName(), urlString));
			
		} catch (MalformedURLException ex) {
			throw ex;
		} catch (ClassNotFoundException ex) {
			Logger.error(ex);
			throw new IOException(ex.getMessage());
		} catch (FileNotFoundException ex) {
			Message.error("File Not found", urlString, ex);
		} catch (NullPointerException ex) {
			Logger.error("Cannot open jad", urlString, ex);
		} catch (IllegalArgumentException ex) {
			Logger.error("Cannot open jad", urlString, ex);
		}
	}

	public void startMidlet(Class midletClass, MIDletAccess previousMidletAccess) {
		try {
			if (previousMidletAccess != null) {
				previousMidletAccess.destroyApp(true);
			}
		} catch (Throwable e) {
			Message.error("Unable to destroy MIDlet, " + Message.getCauseMessage(e), e);
		}
		
		MIDletContext context = new MIDletContext();
		MIDletBridge.setThreadMIDletContext(context);
		try {
			MIDlet m;
			
			final String errorTitle = "Error starting MIDlet";
			
			try {
				Object object = midletClass.newInstance();
				if (!(object instanceof MIDlet)) {
					Message.error(errorTitle, "Class " + midletClass.getName() + " should extend MIDlet");
					return;
				}
				m = (MIDlet) object;
			} catch (Throwable e) {
				Message.error(errorTitle, "Unable to create MIDlet, " + Message.getCauseMessage(e), e);
				MIDletBridge.destroyMIDletContext(context);
				return;
			}
			
			try {
				if (context.getMIDlet() != m) {
					throw new Error("MIDlet Context corrupted");
				}
				context.getMIDletAccess().startApp();
				
				launcher.setCurrentMIDlet(m);
			} catch (Throwable e) {
				Message.error(errorTitle, "Unable to start MIDlet, " + Message.getCauseMessage(e), e);
				MIDletBridge.destroyMIDletContext(context);
			}

		} finally {
			MIDletBridge.setThreadMIDletContext(null);
		}

	}

	protected void startLauncher(MIDletContext midletContext) {
		if (midletContext != null && !midletContext.isLauncher()) {
			try {
				MIDletAccess previousMidletAccess = midletContext.getMIDletAccess();
				if (previousMidletAccess != null) {
					previousMidletAccess.destroyApp(true);
				}
			} catch (Throwable e) {
				Logger.error("destroyApp error", e);
			}
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
		
		// Close Current MIDlet before oppening new one.
		MIDletContext previousMidletContext = MIDletBridge.getMIDletContext();
		if (previousMidletContext != null && !previousMidletContext.isLauncher()) {
			 MIDletBridge.destroyMIDletContext(previousMidletContext);
		}
		MIDletBridge.clear();
		
		setResponseInterface(false);
		try {
			URL url = null;
			try {
				url = new URL(jad.getJarURL());
			} catch (MalformedURLException ex) {
				try {
					url = new URL(jadUrl.substring(0, jadUrl.lastIndexOf('/') + 1) + jad.getJarURL());
					// TODO check if IOUtils.getCanonicalFileURL is needed
					jad.setCorrectedJarURL(url.toExternalForm());
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
				launcher.addMIDletEntry(new MIDletEntry(jadEntry.getName(), midletClass));
			}
			startLauncher(null);
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
			Class implClass = getExtensionsClassLoader().loadClass(implClassName);
			if (ImplementationInitialization.class.isAssignableFrom(implClass)) {
				Object inst = implClass.newInstance();
				((ImplementationInitialization)inst).registerImplementation();
				Logger.debug("implementation registered", implClassName);
			} else {
				Logger.debug("initialize implementation", implClassName);
				boolean isStatic = true;
				try {
					// Create and object or call static initializer instance();
					Constructor c = implClass.getConstructor(null);
					if (Modifier.isPublic(c.getModifiers())) {
						isStatic = false;
						implClass.newInstance();
					}
				} catch (NoSuchMethodException e) {
				}
				
				if (isStatic) {
					try {
						Method getinst = implClass.getMethod("instance", null);
						if (Modifier.isStatic(getinst.getModifiers())) {
							getinst.invoke(implClass, null);
						} else {
							Logger.debug("No known way to initialize implementation class");	
						}
					} catch (NoSuchMethodException e) {
						Logger.debug("No known way to initialize implementation class");
					} catch (InvocationTargetException e) {
						Logger.debug("Unable to initialize Implementation", e.getCause());
					}
				}
			}
		} catch (ClassNotFoundException e) {
			Logger.error(errorText, e);
		} catch (InstantiationException e) {
			Logger.error(errorText, e);
		} catch (IllegalAccessException e) {
			Logger.error(errorText, e);
		}
	}

	public static void initParams(List params) {
		Iterator it = params.iterator();
		while (it.hasNext()) {
			String tmp = (String) it.next();
			if (tmp.equals("--id")) {
				it.remove();
				Config.setEmulatorID((String)it.next());
				it.remove();
			} else if ((tmp.equals("--help")) || (tmp.equals("-help"))) {
				System.out.println(usage());
				System.exit(0);
			}
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
			} else if ((tmp.equals("--classpath")) || (tmp.equals("-classpath"))  || (tmp.equals("-cp"))) {
				it.remove();
				getExtensionsClassLoader().addClasspath((String)it.next());
				it.remove();
			} else if (tmp.equals("--impl")) {
				it.remove();
				registerImplementation((String)it.next());
				it.remove();
			}
		}

		//TODO registerImplementations by reading jar files in classpath.

		ClassLoader classLoader = getExtensionsClassLoader();
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

	private static ExtensionsClassLoader getExtensionsClassLoader() {
		if (instance.extensionsClassLoader == null) {
			instance.extensionsClassLoader = new ExtensionsClassLoader(new URL[]{}, instance.getClass().getClassLoader());
		}
		return instance.extensionsClassLoader;
	}
	
	private static MIDletClassLoader createMIDletClassLoader() {
		MIDletClassLoader mcl = new MIDletClassLoader(getExtensionsClassLoader());
		if (!Serializable.class.isAssignableFrom(Injected.class)) {
			Logger.error("classpath configuration error, Wrong Injected class detected. microemu-injected module should be after microemu-javase in eclipse");
		}
		mcl.disableClassPreporcessing(Injected.class);
		MIDletResourceLoader.classLoader = mcl;
		return mcl;
	}
	
	public static ClassLoader createExtensionsClassLoader(final URL[] urls) {
		return new ExtensionsClassLoader(urls, getExtensionsClassLoader());
	}
	
	private static JadProperties loadJadProperties(String urlString) throws IOException {
		JadProperties properties = new JadProperties();
		
		URL url = new URL(urlString);
		if (url.getUserInfo() == null) {
			properties.load(url.openStream());
		} else {
			URLConnection cn = url.openConnection();
			String userInfo = new String(Base64Coder.encode(url.getUserInfo().getBytes("UTF-8")));
			cn.setRequestProperty("Authorization", "Basic " + userInfo);
			properties.load(cn.getInputStream());
		}		
		
		return properties;
	}

	public void initMIDlet(List params, boolean startMidlet) {
		Class midletClass = null;
		Vector appclasses = new Vector();
		Vector appclasspath = new Vector();
		String propertiesJad = null;
		
		Iterator it = params.iterator();
		
		while (it.hasNext()) {
			String test = (String) it.next();
			it.remove();
			if ((test.equals("--appclasspath")) || (test.equals("-appclasspath"))  || (test.equals("-appcp"))) {
				appclasspath.add(it.next());
				it.remove();
			} else if (test.equals("--appclass")) {
				appclasses.add(it.next());
				it.remove();
			} else if (test.equals("--propertiesjad")) {
				File file = new File((String) it.next());
				propertiesJad = file.exists() ? IOUtils.getCanonicalFileURL(file) : test;
				it.remove();
			} else if (midletClass == null && test.endsWith(".jad")) {
				try {
					File file = new File(test);
					String url = file.exists() ? IOUtils.getCanonicalFileURL(file) : test;
					openJadUrl(url);
				} catch (IOException exception) {
					Logger.error("Cannot load " + test + " URL", exception);
				}
			} else if (test.equals("--usesystemclassloader")) {				
				useSystemClassLoader = true;
			} else {				
				if (!useSystemClassLoader) {
    				MIDletClassLoader classLoader = createMIDletClassLoader();
    				try {
    					for (Iterator iter = appclasspath.iterator(); iter.hasNext();) {
    						String path = (String)iter.next();
    						StringTokenizer st = new StringTokenizer(path, File.pathSeparator);
    						while (st.hasMoreTokens()) {
    							classLoader.addURL(new URL(IOUtils.getCanonicalFileClassLoaderURL(new File(st.nextToken()))));
    						}
    					}	
    					classLoader.addClassURL(test);
    					for (Iterator iter = appclasses.iterator(); iter.hasNext();) {
    						classLoader.addClassURL((String) iter.next());
    						
    					}
    					
    					midletClass = classLoader.loadClass(test);
    				} catch (MalformedURLException e) {
    					Message.error("Error", "Unable to find MIDlet class, " + Message.getCauseMessage(e), e);
    				} catch (ClassNotFoundException e) {
    					Message.error("Error", "Unable to find MIDlet class, " + Message.getCauseMessage(e), e);
    				}
				} else {					
					try {
						midletClass = instance.getClass().getClassLoader().loadClass(test);
					} catch (ClassNotFoundException e) {
						Message.error("Error", "Unable to find MIDlet class, " + Message.getCauseMessage(e), e);
					}
				}
			}
		}
		
		if (midletClass != null && propertiesJad != null) {
			try {
				jad = loadJadProperties(propertiesJad);
			} catch (IOException e) {
				Logger.error("Cannot load " + propertiesJad + " URL", e);
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
			"[(-d | --device) ({device descriptor} | {device class name}) ] \n" +
			"[--rms (file | memory)] \n" +
			"[--id EmulatorID ] \n" +
			"[--impl {JSR implementation class name}]\n" +
			"[(--classpath|-cp) <JSR CLASSPATH>]\n" +
			"[(--appclasspath|--appcp) <MIDlet CLASSPATH>]\n" +
			"[--appclass <library class name>]\n" +
			"[--usesystemclassloader] \n" +
			"(({MIDlet class name} [--propertiesjad {jad file location}]) | {jad file location})";
	}

}
