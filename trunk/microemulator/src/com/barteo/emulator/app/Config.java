/*
 *  MicroEmulator
 *  Copyright (C) 2002 Bartek Teodorczyk <barteo@it.pl>
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

import com.barteo.emulator.app.util.DeviceEntry;

import nanoxml.XMLElement;
import nanoxml.XMLParseException;


public class Config 
{
  private static File configPath = new File(System.getProperty("user.home") + "/.microemulator/");
  private static Vector devices = new Vector();
  
  
  static void loadConfig(String configFileName)
  {
		File configFile = new File(configPath, configFileName);
		
    DeviceEntry defaultDevice;
    if (configFileName.equals("config-swt.xml")) { 
			defaultDevice =
				  new DeviceEntry("Default device", null, "com.barteo.emulator.device.swt.SwtDevice", true, false);
    } else if (configFileName.equals("config-awt.xml")) {
			defaultDevice =
				  new DeviceEntry("Default device", null, "com.barteo.emulator.device.applet.AppletDevice", true, false);
  	} else {
			defaultDevice =
      	  new DeviceEntry("Default device", null, "com.barteo.emulator.device.j2se.J2SEDevice", true, false);
    }
    devices.add(defaultDevice);

    String xml = "";
    try {
      InputStream dis = new BufferedInputStream(new FileInputStream(configFile));
      while (dis.available() > 0) {
        byte[] b = new byte[dis.available()];
        dis.read(b);
        xml += new String(b);
      }
    } catch (FileNotFoundException ex) {
      loadDefaultConfig();
      return;
    } catch (IOException ex) {
      System.out.println(ex);
      loadDefaultConfig();
      return;
    }

    XMLElement configRoot = new XMLElement();
    try {
      configRoot.parseString(xml);
    } catch (XMLParseException ex) {
      System.err.println(ex);
      loadDefaultConfig();
      return;
    }
    
    for (Enumeration e = configRoot.enumerateChildren(); e.hasMoreElements(); ) {
      XMLElement tmp = (XMLElement) e.nextElement();
      if (tmp.getName().equals("devices")) {
        for (Enumeration e_device = tmp.enumerateChildren(); e_device.hasMoreElements(); ) {
          XMLElement tmp_device = (XMLElement) e_device.nextElement();
          if (tmp_device.getName().equals("device")) {            
            boolean devDefault = false;
            if (tmp_device.getStringAttribute("default") != null && tmp_device.getStringAttribute("default").equals("true")) {
              devDefault = true;
              defaultDevice.setDefaultDevice(false);
            }
            String devName = null;
            String devFile = null;
            String devClass = null;
            for (Enumeration e_cont = tmp_device.enumerateChildren(); e_cont.hasMoreElements(); ) {
              XMLElement tmp_cont = (XMLElement) e_cont.nextElement();
              if (tmp_cont.getName().equals("name")) {
                devName = tmp_cont.getContent();
              } else if (tmp_cont.getName().equals("filename")) {
                devFile = tmp_cont.getContent();
              } else if (tmp_cont.getName().equals("class")) {
                devClass = tmp_cont.getContent();
              }
            }
            devices.add(new DeviceEntry(devName, devFile, devClass, devDefault));
          }
        }
      }
    }
  }
  
  
  private static void loadDefaultConfig()
  {
  }
  
  
  public static void saveConfig(String configFileName)
  {
		File configFile = new File(configPath, configFileName);

    XMLElement xmlTmp;
    XMLElement xmlRoot = new XMLElement();
    xmlRoot.setName("config");
    XMLElement xmlDevices = new XMLElement();
    xmlDevices.setName("devices");
    xmlRoot.addChild(xmlDevices);
    
    for (Enumeration e = devices.elements(); e.hasMoreElements(); ) {
      DeviceEntry entry = (DeviceEntry) e.nextElement();
      if (!entry.canRemove()) {
        continue;
      }
      
      XMLElement xmlDevice = new XMLElement(false, false);
      xmlDevice.setName("device");
      xmlDevices.addChild(xmlDevice);
      if (entry.isDefaultDevice()) {
        xmlDevice.setAttribute("default", "true");
      }
      xmlTmp = new XMLElement();
      xmlTmp.setName("name");
      xmlTmp.setContent(entry.getName());
      xmlDevice.addChild(xmlTmp);
      xmlTmp = new XMLElement();
      xmlTmp.setName("filename");
      xmlTmp.setContent(entry.getFileName());
      xmlDevice.addChild(xmlTmp);
      xmlTmp = new XMLElement();
      xmlTmp.setName("class");
      xmlTmp.setContent(entry.getClassName());
      xmlDevice.addChild(xmlTmp);      
    }
    
    configPath.mkdirs();
    try {
      FileWriter fw = new FileWriter(configFile);
      xmlRoot.write(fw);
      fw.close();
    } catch (IOException ex) {
      System.out.println(ex);
    }
  }  
  
  
  public static File getConfigPath()
  {
    return configPath;
  }

  
  public static Vector getDevices()
  {
    return devices;
  }
  
}
