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
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

import com.barteo.emulator.app.ui.DeviceEntry;
import nanoxml.XMLElement;
import nanoxml.XMLParseException;


public class Config 
{
  static String configPath;
  
  Vector devices = new Vector();
  
  
  public Config(String configPath)
  {
    this.configPath = configPath;
    loadConfig();
  }
  
  void loadConfig()
  {
    String xml = "";
    try {
      InputStream dis = new BufferedInputStream(new FileInputStream(configPath));
      while (dis.available() > 0) {
        byte[] b = new byte[dis.available()];
        dis.read(b);
        xml += new String(b);
      }
    } catch (Exception ex) {
      System.err.println(ex);
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
            if (tmp.getStringAttribute("default").equals("true")) {
              devDefault = true;
            }
            String devName = null;
            String devFile = null;
            String devClass = null;
            for (Enumeration e_cont = tmp_device.enumerateChildren(); e_cont.hasMoreElements(); ) {
              XMLElement tmp_cont = (XMLElement) e_cont.nextElement();
              if (tmp_cont.getName().equals("name")) {
                devName = tmp_cont.getContent();
              } else if (tmp_cont.getName().equals("file")) {
                devFile = tmp_cont.getContent();
              } else if (tmp_cont.getName().equals("class")) {
                devClass = tmp_cont.getContent();
              }
            }
            devices.add(new DeviceEntry(devName, devFile, null, devDefault));
          }
        }
      }
    }
  }
  
  
  void loadDefaultConfig()
  {
  }
  
  
  static void saveConfig()
  {
  }  
  
}
