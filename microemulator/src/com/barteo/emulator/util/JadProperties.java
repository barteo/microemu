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
 
package com.barteo.emulator.util;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;


public class JadProperties extends Properties
{
  
  static String MIDLET_PREFIX = "MIDlet-";
  
  Vector midletEntries = null;
  

  public void clear()
  {
    super.clear();
    
    midletEntries = null;
  }

  
  public String getSuiteName()
  {
    return getProperty("MIDlet-Name");
  }
  
  
  public String getVersion()
  {
    return getProperty("MIDlet-Version");
  }

  
  public String getVendor()
  {
    return getProperty("MIDlet-Vendor");
  }
  

  public String getProfile()
  {
    return getProperty("MicroEdition-Profile");
  }

  
  public String getConfiguration()
  {
    return getProperty("MicroEdition-Configuration");
  }
  
  
  public String getJarURL()
  {
    return getProperty("MIDlet-Jar-URL");
  }
  
  
  public int getJarSize()
  {
    return Integer.parseInt(getProperty("MIDlet-Jar-Size"));
  }
  
  
  public Vector getMidletEntries()
  {
    String name, icon, className, test;
    int pos;
    
    if (midletEntries == null) {
      midletEntries = new Vector();
    
      for (Enumeration e = propertyNames(); e.hasMoreElements(); ) {
        test = (String) e.nextElement();
        if (test.startsWith(MIDLET_PREFIX)) {
          try {
            Integer.parseInt(test.substring(MIDLET_PREFIX.length()));
            test = getProperty(test);
            pos = test.indexOf(',');
            name = test.substring(0, pos).trim();
            icon = test.substring(pos + 1, test.indexOf(',', pos + 1)).trim();
            className = test.substring(test.indexOf(',', pos + 1) + 1).trim();
            midletEntries.addElement(new JadMidletEntry(name, icon, className));
          } catch (NumberFormatException ex) {}
        }
      }
    }
    
    return midletEntries;
  }
    
}
