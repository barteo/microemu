/*
 *  MicroEmulator
 *  Copyright (C) 2001-2003 Bartek Teodorczyk <barteo@it.pl>
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
 
package com.barteo.emulator.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import com.barteo.emulator.EmulatorContext;
import com.barteo.emulator.MIDletBridge;
import com.barteo.emulator.MIDletEntry;
import com.barteo.emulator.MicroEmulator;
//import com.barteo.emulator.app.capture.Capturer;
import com.barteo.emulator.app.launcher.Launcher;
import com.barteo.emulator.app.ui.ResponseInterfaceListener;
import com.barteo.emulator.app.ui.StatusBarListener;
import com.barteo.emulator.app.util.ProgressEvent;
import com.barteo.emulator.app.util.ProgressJarClassLoader;
import com.barteo.emulator.app.util.ProgressListener;
import com.barteo.emulator.device.Device;
import com.barteo.emulator.device.DeviceFactory;
import com.barteo.emulator.util.JadMidletEntry;
import com.barteo.emulator.util.JadProperties;


public class Common implements MicroEmulator 
{
	private static Launcher launcher;
  
  	protected EmulatorContext emulatorContext;
  
  	protected String captureFile = null;
//  	private Capturer capturer = null;
  	
	protected JadProperties jad = new JadProperties();
	
	private StatusBarListener statusBarListener = null; 
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
		emulatorContext = context;

		launcher = new Launcher();
		launcher.setCurrentMIDlet(launcher);     

		MIDletBridge.setMicroEmulator(this);
	}
	
	
	public String getAppProperty(String key)
	{
		return jad.getProperty(key);
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
			launcher.setCurrentMIDlet(result);
		} catch (Exception ex) {
			System.out.println("Cannot initialize " + midletClass + " MIDlet class");
			System.out.println(ex);
			ex.printStackTrace();
			return null;
		}  
    
		return result;
	}


	public void openJadFile(URL url)
	{
		try {
			setStatusBar("Loading...");
			jad.clear();
			jad.load(url.openStream());
			loadFromJad(url);
		} catch (FileNotFoundException ex) {
			System.err.println("Cannot found " + url.getPath());
		} catch (NullPointerException ex) {
			ex.printStackTrace();
			System.err.println("Cannot open jad " + url.getPath());
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
			System.err.println("Cannot open jad " + url.getPath());
		} catch (IOException ex) {
			ex.printStackTrace();
			System.err.println("Cannot open jad " + url.getPath());
		}
	}


	public void startMidlet(MIDlet m)
	{
		try {
			MIDletBridge.getMIDletAccess(m).startApp();
		} catch (MIDletStateChangeException ex) {
			System.err.println(ex);
		}
	}
	
	
	public void setStatusBarListener(StatusBarListener listener)
	{
		statusBarListener = listener;
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
		final ProgressJarClassLoader loader = (ProgressJarClassLoader) emulatorContext.getClassLoader();
		
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
    
		Thread task = new Thread() 
		{
      
			public void run()
			{
				loader.setProgressListener(progressListener);
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


	private void setStatusBar(String text)
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
