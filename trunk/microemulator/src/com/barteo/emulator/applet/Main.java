/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@it.pl>
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
 
package com.barteo.emulator.applet;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import com.barteo.emulator.DisplayComponent;
import com.barteo.emulator.EmulatorContext;
import com.barteo.emulator.MIDletBridge;
import com.barteo.emulator.MicroEmulator;
import com.barteo.emulator.RecordStoreManager;
import com.barteo.emulator.app.ui.awt.AwtDeviceComponent;
import com.barteo.emulator.device.DeviceFactory;
import com.barteo.emulator.device.applet.AppletDevice;
import com.barteo.emulator.util.JadProperties;


public class Main extends Applet implements MicroEmulator
{
  MIDlet midlet = null;
  
  private RecordStoreManager recordStoreManager;
  
  private JadProperties manifest = new JadProperties();

  Font defaultFont;
  
  AwtDeviceComponent devicePanel;

    
  EmulatorContext emulatorContext = new EmulatorContext()
  {    
    public ClassLoader getClassLoader()
    {
      return getClass().getClassLoader();
    }
    
    public DisplayComponent getDisplayComponent()
    {
      return devicePanel.getDisplayComponent();
    }    
  };
  
  
  public Main()
  {
  	recordStoreManager = new AppletRecordStoreManager();
	devicePanel = new AwtDeviceComponent();
  }
  
  
  public void init()
  {
System.out.println("Applet::init()");
	if (midlet != null) {
		return;
	}

    MIDletBridge.setMicroEmulator(this);
      
    setLayout(new BorderLayout());
	add(devicePanel, "Center");	

    AppletDevice device;
    String deviceClassName = getParameter("device");
    if (deviceClassName == null) {
      device = new AppletDevice();    
    } else {
      try {
      	Class cl = Class.forName(deviceClassName);
        device = (AppletDevice) cl.newInstance();
      } catch (ClassNotFoundException ex) {
        System.out.println(ex);
        return;
      } catch (IllegalAccessException ex) {
        System.out.println(ex);
        return;
      } catch (InstantiationException ex) {
        System.out.println(ex);
        return;
      }
    }

    DeviceFactory.setDevice(device);
    device.init(emulatorContext);
	devicePanel.init();

    String midletName = getParameter("midlet");
		if (midletName == null) {
			System.out.println("There is no midlet parameter");
			return;
		}
    
    Class midletClass;
		try {
			midletClass = Class.forName(midletName);
		} catch (ClassNotFoundException ex) {
			System.out.println("Cannot find " + midletName + " MIDlet class");
			return;
		}

    try {
      midlet = (MIDlet) midletClass.newInstance();
    } catch (Exception ex) {
      System.out.println("Cannot initialize " + midletClass + " MIDlet class");
      System.out.println(ex);        
      ex.printStackTrace();
      return;
    }
    
	manifest.clear();
	try {
		URL url = emulatorContext.getClassLoader().getResource("META-INF/MANIFEST.MF");
		manifest.load(url.openStream());
		if (manifest.getProperty("MIDlet-Name") == null) {
			manifest.clear();
		}
	} catch (IOException e) {
		e.printStackTrace();
	}
	
    Image tmpImg = ((AppletDevice) DeviceFactory.getDevice()).getNormalImage();
    resize(tmpImg.getWidth(null), tmpImg.getHeight(null));
    
    return;
  }


  public void start()
	{
System.out.println("Applet::start()");  	
    try {
      MIDletBridge.getMIDletAccess(midlet).startApp();
		} catch (MIDletStateChangeException ex) {
      System.err.println(ex);
		}
    requestFocus();
  }


	public void stop() 
  {
System.out.println("Applet::stop()");  	
    MIDletBridge.getMIDletAccess(midlet).pauseApp();
  }


	public void destroy()
	{
System.out.println("Applet::destroy()");  	
    try {
			MIDletBridge.getMIDletAccess(midlet).destroyApp(true);
		} catch (MIDletStateChangeException ex) {
  		System.err.println(ex);
		}
	}

	
	public RecordStoreManager getRecordStoreManager()
	{
		return recordStoreManager;
	}
	
	

  public String getAppProperty(String key)
  {
    String property = getParameter(key);
    if (property==null) {
        property = manifest.getProperty(key);
    }
    
    return property;
  }

  
  public void notifyDestroyed()
	{
  }
  
  
  public void notifySoftkeyLabelsChanged()
  {
  }
  
  
  public String getAppletInfo()
	{
    return "Title: MicroEmulator \nAuthor: Bartek Teodorczyk, 2001";
  }


  public String[][] getParameterInfo()
	{
    String[][] info = {
		    {"midlet", "MIDlet class name", "The MIDlet class name. This field is mandatory."},
    };

		return info;
  }
                     
}
