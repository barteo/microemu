/*
 * MicroEmulator
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.barteo.emulator;

import java.util.Hashtable;

import javax.microedition.midlet.MIDlet;

/**
 * MIDletBridge
 *      
 * @author <a href="mailto:barteo@it.pl">Bartek Teodorczyk</a>
 */
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
