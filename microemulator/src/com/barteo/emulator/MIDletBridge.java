/*
 *  @(#)MIDletBridge.java  07/07/2001
 *
 *  Copyright (c) 2001 Bartek Teodorczyk <barteo@it.pl>. All Rights Reserved.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
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

  
  public static void notifyDestroyed()
  {
    emulator.notifyDestroyed();
  }
  
}
