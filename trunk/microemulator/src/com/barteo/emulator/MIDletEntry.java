/*
 * MicroEmulator
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.barteo.emulator;

import javax.microedition.midlet.MIDlet;

/**
 * MIDletEntry
 *      
 * @author <a href="mailto:barteo@it.pl">Bartek Teodorczyk</a>
 */
public class MIDletEntry 
{

  String name;
  MIDlet midlet;
  
  
  public MIDletEntry(String name, MIDlet midlet)
  {
    this.name = name;
    this.midlet = midlet;
  }
  
  
  public String getName()
  {
    return name;
  }
  
  
  public MIDlet getMIDlet()
  {
    return midlet;
  }

}
