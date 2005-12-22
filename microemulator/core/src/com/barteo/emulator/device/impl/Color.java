/*
 *  MicroEmulator
 *  Copyright (C) 2005 Bartek Teodorczyk <barteo@berteo.net>
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

package com.barteo.emulator.device.impl;

public class Color
{
    private int value;
    
    
    public Color(int value)
    {
        this.value = value;
    }
    

    public int getRed()
    {
        return (value >> 16) & 0xff;
    }
    
    
    public int getGreen()
    {
        return (value >> 8) & 0xff;
    }

    
    public int getBlue()
    {
        return value & 0xff;
    }
    
    
    public int getRGB()
    {
        return value;
    }
    
}
