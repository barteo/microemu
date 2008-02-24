/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
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
 *
 *  @version $Id$
 */
package org.microemu.microedition.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connection;

import org.microemu.microedition.Implementation;

/**
 * Delegate for javax.microedition.Connector
 * 
 * @author vlads
 */
public interface ConnectorDelegate extends Implementation {

	public Connection open(String name) throws IOException;
	
	public Connection open(String name, int mode) throws IOException;

	public Connection open(String name, int mode, boolean timeouts) throws IOException;

	public DataInputStream openDataInputStream(String name) throws IOException;

	public DataOutputStream openDataOutputStream(String name) throws IOException;

	public InputStream openInputStream(String name) throws IOException;
  
	public OutputStream openOutputStream(String name) throws IOException;
	
}
