/*
 *  MicroEmulator
 *  Copyright (C) 2002 Bartek Teodorczyk <barteo@barteo.net>
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

package com.barteo.emulator.app.util;


public class DeviceEntry
{
  private String name;
  private String fileName;
  private String className;
  private boolean defaultDevice;
  private boolean canRemove;
  
  
  public DeviceEntry(String name, String fileName, String className, boolean defaultDevice)
  {
    this(name, fileName, className, defaultDevice, true);
  }
  
  
  public DeviceEntry(String name, String fileName, String className, boolean defaultDevice, boolean canRemove)
  {
    this.name = name;
    this.fileName = fileName;
    this.className = className;
    this.defaultDevice = defaultDevice;
    this.canRemove = canRemove;
  }
  
  
  public boolean canRemove()
  {
    return canRemove;
  }
  
  
  public String getClassName()
  {
    return className;
  }
  
  
  public String getFileName()
  {
    return fileName;
  }
  
  
  public String getName()
  {
    return name;
  }
  
  
  public boolean isDefaultDevice()
  {
    return defaultDevice;
  }
  
  
  public void setDefaultDevice(boolean b)
  {
    defaultDevice = b;
  }
  
  
  public boolean equals(DeviceEntry test)
  {
    if (test.getClassName().equals(getClassName())) {
      return true;
    }
    
    return false;
  }
  
  
  public String toString()
  {
    if (defaultDevice) {
      return name + " (default)";
    } else {
      return name;
    }
  }
  
}