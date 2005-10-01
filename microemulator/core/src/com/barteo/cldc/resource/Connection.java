/*
 *  MicroEmulator
 *  Copyright (C) 2001,2002 Bartek Teodorczyk <barteo@barteo.net>
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

package com.barteo.cldc.resource;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;

import javax.microedition.io.InputConnection;

import com.barteo.cldc.ClosedConnection;
import com.barteo.emulator.device.DeviceFactory;


public class Connection implements InputConnection, ClosedConnection {
    String resource;
    public void close() throws IOException {
    }
    
    public javax.microedition.io.Connection open(String name) throws IOException {
        resource=name.substring(9,name.length());//remove the resource: part of the string
        resource=resource.replace('\\','/');
        return this;
    }
    
    public DataInputStream openDataInputStream() throws IOException {
        return new DataInputStream(openInputStream());
    }
    
    public InputStream openInputStream() throws IOException {
        return DeviceFactory.getDevice().getEmulatorContext().getClassLoader().getResourceAsStream(resource);
    }
    
}
