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
 
package com.barteo.emulator;

import java.util.Hashtable;

import javax.microedition.midlet.MIDlet;


public class MIDletBridge 
{

  static Hashtable midletAccess = new Hashtable();
  
  static MicroEmulator emulator = null;
  
  
  public static void setMicroEmulator(MicroEmulator a_emulator) 
  {
    emulator = a_emulator;
  }  

  
  public static void setAccess(MIDlet a_midlet, MIDletAccess a_midletAccess) 
  {
    midletAccess.put(a_midlet, a_midletAccess);
  }  

  
  public static MIDletAccess getAccess(MIDlet a_midlet) 
  {
    return (MIDletAccess) midletAccess.get(a_midlet);
  }
  
  
  public static String getAppProperty(String key)
  {
    return emulator.getAppProperty(key);
  }

    
  public static void notifyDestroyed()
  {
    emulator.notifyDestroyed();
  }
  
}
