/*
 *  MicroEmulator
 *  Copyright (C) 2001-2003 Bartek Teodorczyk <barteo@it.pl>
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

package com.barteo.cldc.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.microedition.io.StreamConnection;

import com.barteo.cldc.ClosedConnection;


public class Connection implements StreamConnection, ClosedConnection 
{
	private Socket socket;


	public javax.microedition.io.Connection open(String name)
		throws IOException
	{
		int portSepIndex = name.lastIndexOf(':');
		int port = Integer.parseInt(name.substring(portSepIndex + 1));		
		String host = name.substring("socket://".length(), portSepIndex);

		socket = new Socket(host, port);
    
	  return this;
	}


	public void close() 
			throws IOException 
	{
		if (socket == null) {
			return;
		}
		
		socket.close();
	}


	public InputStream openInputStream() 
			throws IOException 
	{
		if (socket == null) {
		  throw new IOException();
		}
		
		return socket.getInputStream();
	}


	public DataInputStream openDataInputStream() 
			throws IOException 
	{
		return new DataInputStream(openInputStream());
	}


	public OutputStream openOutputStream() 
			throws IOException 
	{
		if (socket == null) {
		  throw new IOException();
		}

		return socket.getOutputStream();
	}


	public DataOutputStream openDataOutputStream() 
			throws IOException 
	{
		return new DataOutputStream(openOutputStream());
	}

}
