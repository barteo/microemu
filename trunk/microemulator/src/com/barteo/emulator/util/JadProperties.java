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

import java.util.Properties;


public class JadProperties extends Properties
{

  public String getName()
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
    
}
