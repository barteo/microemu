/*
 * MicroEmulator
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package com.barteo.emulator.util;

import java.util.Properties;

/**
 * JadProperties
 *      
 * @author <a href="mailto:barteo@it.pl">Bartek Teodorczyk</a>
 */
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
