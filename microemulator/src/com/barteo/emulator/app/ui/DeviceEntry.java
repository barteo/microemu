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

package com.barteo.emulator.app.ui;


public class DeviceEntry
{
  private String name;
  private String file;
  private Class deviceClass;
  private boolean defaultDevice;
  private boolean canRemove;
  
  
  public DeviceEntry(String name, String file, Class deviceClass, boolean defaultDevice)
  {
    this(name, file, deviceClass, defaultDevice, true);
  }
  
  
  DeviceEntry(String name, String file, Class deviceClass, boolean defaultDevice, boolean canRemove)
  {
    this.name = name;
    this.file = file;
    this.deviceClass = deviceClass;
    this.defaultDevice = defaultDevice;
    this.canRemove = canRemove;
  }
  
  
  public boolean canRemove()
  {
    return canRemove;
  }
  
  
  public Class getDeviceClass()
  {
    return deviceClass;
  }
  
  
  public boolean isDefaultDevice()
  {
    return defaultDevice;
  }
  
  
  public void setDefaultDevice(boolean b)
  {
    defaultDevice = b;
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