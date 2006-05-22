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
import java.util.Enumeration;
import java.util.Locale;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import org.microemu.EmulatorContext;
import org.microemu.MIDletBridge;
import org.microemu.MIDletEntry;
import org.microemu.MicroEmulator;
import org.microemu.RecordStoreManager;
import org.microemu.app.launcher.Launcher;
import org.microemu.app.ui.ResponseInterfaceListener;
import org.microemu.app.ui.StatusBarListener;
import org.microemu.app.util.Base64Coder;
import org.microemu.app.util.ProgressEvent;
import org.microemu.app.util.ProgressJarClassLoader;
import org.microemu.app.util.ProgressListener;
import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.util.JadMidletEntry;
import org.microemu.util.JadProperties;

//import com.barteo.emulator.app.capture.Capturer;


public class Common implements MicroEmulator 
{
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
  
	private ProgressListener progressListener = new ProgressListener()
	{
		int percent = -1;
    
		public void stateChanged(ProgressEvent event)
		{
			int newpercent = (int) ((float) event.getCurrent() / (float) event.getMax() * 100);
      
			if (newpercent != percent) {
				setStatusBar("Loading... (" + newpercent +" %)");
				percent = newpercent;
			}
		}    
	};

	
	public Common(EmulatorContext context)
	{
		instance = this;
		this.emulatorContext = context;
		
		launcher = new Launcher();
		launcher.setCurrentMIDlet(launcher);     

		this.recordStoreManager = new AppRecordStoreManager(launcher);

		MIDletBridge.setMicroEmulator(this);
	}
	
	
	public RecordStoreManager getRecordStoreManager()
	{
		return recordStoreManager;
	}
	
	
	public String getAppProperty(String key)
	{
        if (key.equals("microedition.platform")) {
            return  "MicroEmulator";
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

  
	public void notifyDestroyed()
	{
		startMidlet(launcher);
	}
  

	public void notifySoftkeyLabelsChanged() 
	{
	}
	
	
	public Launcher getLauncher()
	{
		return launcher;
	}
	
	
	public MIDlet loadMidlet(String name, Class midletClass)
	{
		MIDlet result;
    
		try {
			result = (MIDlet) midletClass.newInstance();
			launcher.addMIDletEntry(new MIDletEntry(name, result));
		} catch (Exception ex) {
			System.out.println("Cannot initialize " + midletClass + " MIDlet class");
			System.out.println(ex);
			ex.printStackTrace();
			return null;
		}  
    
		return result;
	}


	public static void openJadUrl(String urlString) throws MalformedURLException
	{
		try {
			URL url = new URL(urlString);
			setStatusBar("Loading...");
			getInstance().jad.clear();
			if (url.getUserInfo() == null) {
				getInstance().jad.load(url.openStream());
			} else {
				URLConnection cn = url.openConnection();
				String userInfo = new String(Base64Coder.encode(url.getUserInfo().getBytes("UTF-8")));
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


	public void startMidlet(MIDlet m)
	{
		try {
			launcher.setCurrentMIDlet(m);
			MIDletBridge.getMIDletAccess(m).startApp();
		} catch (MIDletStateChangeException ex) {
			System.err.println(ex);
		}
	}
	
	
	public void setStatusBarListener(StatusBarListener listener)
	{
		statusBarListener = listener;
	}
	
	
    public boolean platformRequest(String URL)
    {
    	return false;
    }
  

	public void setResponseInterfaceListener(ResponseInterfaceListener listener)
	{
		responseInterfaceListener = listener;
	}
	
	
	protected void close()
	{
		if (captureFile != null) {
//			if (capturer != null) {
//				capturer.stopCapture(emulatorContext.getDisplayComponent());
//			}
		}		
	}	
  

	protected void loadFromJad(URL jadUrl)
	{
		final ProgressJarClassLoader loader = new ProgressJarClassLoader(emulatorContext.getClassLoader());
		
		setResponseInterface(false);
		URL url = null;
		try {
			if (jadUrl.getProtocol().equals("file")) {
				String tmp = jadUrl.getFile();
				File f = new File(tmp.substring(0, tmp.lastIndexOf('/')), jad.getJarURL());
				url = f.toURL();
			} else {
				url = new URL(jad.getJarURL());
			}
		} catch (MalformedURLException ex) {
			System.err.println(ex);
			setResponseInterface(true);
		}
		
		loader.addRepository(url);
		launcher.removeMIDletEntries();
    

		manifest.clear();
		try {
			manifest.load(loader.getResourceAsStream("/META-INF/MANIFEST.MF"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}					

		Thread task = new Thread() 
		{
      
			public void run()
			{
				loader.setProgressListener(progressListener);
				
				launcher.setSuiteName(jad.getSuiteName());
				try {
					for (Enumeration e = jad.getMidletEntries().elements(); e.hasMoreElements(); ) {
						JadMidletEntry jadEntry = (JadMidletEntry) e.nextElement();
						Class midletClass = loader.loadClass(jadEntry.getClassName());
						loadMidlet(jadEntry.getName(), midletClass);
					}
					notifyDestroyed();
				} catch (ClassNotFoundException ex) {
					System.err.println(ex);
				}        
				loader.setProgressListener(null);
				setStatusBar("");
				setResponseInterface(true);
			}
      
		};
    
		task.start();
	}
	
	
	protected void setDevice(Device device)
	{
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
	}
	
	
	private static Common getInstance()
	{
		return instance;
	}


	private static void setStatusBar(String text)
	{
		if (statusBarListener != null) {
			statusBarListener.statusBarChanged(text);
		}
	}
	
	
	private void setResponseInterface(boolean state)
	{
		if (responseInterfaceListener != null) {
			responseInterfaceListener.stateChanged(state);
		}
	}
    
}
