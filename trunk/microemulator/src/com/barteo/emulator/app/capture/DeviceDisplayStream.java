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

import java.awt.Dimension;
import java.io.IOException;

import javax.media.Buffer;
import javax.media.Control;
import javax.media.Format;
import javax.media.format.RGBFormat;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferStream;

import com.barteo.emulator.app.ui.DisplayRepaintListener;
import com.barteo.emulator.device.MutableImage;

public class DeviceDisplayStream implements PushBufferStream, DisplayRepaintListener
{
	protected ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW);
	protected int maxDataLength;
	protected Dimension size;
	protected RGBFormat format;
	protected boolean started;
	protected int frameRate = 30;
	protected BufferTransferHandler transferHandler;
	protected Control[] controls = new Control[0];
	
	protected MutableImage image;


	public DeviceDisplayStream() 
	{
		size = new Dimension(320, 240);
		maxDataLength = size.width * size.height * 3;

		format =
			new RGBFormat(
				new Dimension(size.width, size.height),
				Format.NOT_SPECIFIED,
				Format.byteArray,
				(float) frameRate,
				32,
				0xff000000,
				0x00ff0000,
				0x0000ff00);

	}

	/***************************************************************************
	 * SourceStream
	 ***************************************************************************/

	public ContentDescriptor getContentDescriptor() {
		return cd;
	}

	public long getContentLength() {
		return LENGTH_UNKNOWN;
	}

	public boolean endOfStream() {
		System.out.println("LiveStream::endOfStream");
		return false;
	}

	/***************************************************************************
	 * PushBufferStream
	 ***************************************************************************/

	int seqNo = 0;
	double freq = 2.0;
	boolean stopped = false;
	long time = 0;

	public Format getFormat() 
	{
		return format;
	}


	public void read(Buffer buf) 
			throws IOException 
	{
		System.out.println("read");

		synchronized (this) {
			System.err.println("  - creating image: " + seqNo);

			int data[] = image.getData();
			buf.setData(data);

			System.err.println("    read " + data.length + " bytes.");

			buf.setOffset(0);
			buf.setLength(data.length);
			buf.setFormat(format);
			buf.setTimeStamp(time);
			long duration = (long) seqNo * (1000000 / frameRate) * 1000;
			buf.setDuration(duration);
			time += duration;
			buf.setFlags(buf.getFlags() | Buffer.FLAG_KEY_FRAME);

			seqNo++;
		}
	}

	public void setTransferHandler(BufferTransferHandler transferHandler) 
	{
		synchronized (this) {
			this.transferHandler = transferHandler;
			notifyAll();
		}
	}

	/***************************************************************************
	 * Runnable
	 ***************************************************************************/

	public void run() {
		while (started) {
			synchronized (this) {
				while (transferHandler == null && started) {
					try {
						wait(1000);
					} catch (InterruptedException ie) {
					}
				} // while
			}

			if (started && transferHandler != null) {
				transferHandler.transferData(this);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ise) {
				}
			}
		} // while (started)
	} // run

	// Controls

	public Object[] getControls() {
		return controls;
	}

	public Object getControl(String controlType) {
		try {
			Class cls = Class.forName(controlType);
			Object cs[] = getControls();
			for (int i = 0; i < cs.length; i++) {
				if (cls.isInstance(cs[i]))
					return cs[i];
			}
			return null;

		} catch (Exception e) { // no such controlType or such control
			return null;
		}
	}

	public void repaintInvoked(MutableImage image) 
	{
		this.image = image;
		transferHandler.transferData(this);
	}


	public DisplayRepaintListener getDisplayRepaintListener() 
	{
		return this;
	}
}