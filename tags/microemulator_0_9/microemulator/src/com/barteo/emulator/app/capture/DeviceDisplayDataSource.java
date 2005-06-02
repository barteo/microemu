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

package com.barteo.emulator.app.capture;

import java.io.IOException;

import javax.media.Time;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;

import com.barteo.emulator.app.ui.DisplayRepaintListener;

public class DeviceDisplayDataSource extends PushBufferDataSource
{
	protected Object[] controls = new Object[0];
	protected boolean started = false;
	protected String contentType = "raw";
	protected boolean connected = false;
	protected DeviceDisplayStream[] streams = null;
	protected DeviceDisplayStream stream = null;


	public DeviceDisplayDataSource() 
	{
	}


	public String getContentType() 
	{
		if (!connected) {
			System.err.println("Error: DataSource not connected");
			return null;
		}
		return contentType;
	}


	public void connect() throws IOException 
	{
		if (connected)
			return;
		connected = true;
	}

	public void disconnect() 
	{
		try {
			if (started)
				stop();
		} catch (IOException e) {
		}
		connected = false;
	}


	public void start() 
			throws IOException 
	{
		System.out.println("start");
		// we need to throw error if connect() has not been called
		if (!connected)
			throw new java.lang.Error(
				"DataSource must be connected before it can be started");
		if (started)
			return;
		started = true;
	}


	public void stop() 
			throws IOException 
	{
		System.out.println("stop");
		if ((!connected) || (!started))
			return;
		started = false;
	}


	public Object[] getControls() 
	{
		return controls;
	}


	public Object getControl(String controlType) 
	{
		return null;
	}


	public Time getDuration() 
	{
		return DURATION_UNKNOWN;
	}


	public PushBufferStream[] getStreams() 
	{
		if (streams == null) {
			streams = new DeviceDisplayStream[1];
			stream = streams[0] = new DeviceDisplayStream();
		}
		return streams;
	}


	/**
	 * @return
	 */
	public DisplayRepaintListener getDisplayRepaintListener() 
	{
		return streams[0].getDisplayRepaintListener();
	}

}