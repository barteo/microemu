/*
 *  Nokia API for MicroEmulator
 *  Copyright (C) 2003 Markus Heberling <markus@heberling.net>
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
 
package com.nokia.mid.ui;

// only empty methods. the emulator cant vibrate or switch light on

public class DeviceControl 
{
   
    static void setLightOn(int val) 
    {
    }
    
    static int getLightOn() 
    {
        return 0;
    }
        
    public static void startVibra(int frequency, long duration) 
    {
    }
    
    public static void stopVibra() 
    {
    }
        
    public static void setLights(int num, int level) 
    {
    }
        
    public static void flashLights(long duration) 
    {
    }
}