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

import java.io.File;
import java.io.IOException;

import javax.media.DataSink;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Processor;
import javax.media.ProcessorModel;
import javax.media.format.RGBFormat;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;

import com.barteo.emulator.DisplayComponent;

public class Capturer 
{
	Processor processor = null;
	DeviceDisplayDataSource datasource = null;
	DataSink datasink = null;
	String outputType = "video.quicktime";


	public Capturer()
	{
		datasource = new DeviceDisplayDataSource();
		
		try {
			datasource.connect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		if (datasource != null) {
			// Set the preferred content type for the Processor's output
			FileTypeDescriptor ftd = new FileTypeDescriptor(outputType);

			Format[] formats = {
				new RGBFormat()
			};

			ProcessorModel pm = new ProcessorModel(datasource, formats, ftd);
			try {
				processor = Manager.createRealizedProcessor(pm);
			} catch (Exception me) {
				System.err.println(me);
				// Make sure the capture devices are released
				datasource.disconnect();
				return;
			}
		}		
	}


	public void startCapture(DisplayComponent display, String fileName) 
	{
		DataSource outputDS = processor.getDataOutput();
		
		display.addDisplayRepaintListener(datasource.getDisplayRepaintListener());
		
		try {
			MediaLocator ml = new MediaLocator(new File(fileName).toURL());
			datasink = Manager.createDataSink(outputDS, ml);
			datasink.open();
			datasink.start();
		} catch (Exception e) {
			System.err.println(e);
		}
		processor.start();
		System.out.println("Started saving...");
	}
	
	
	public void stopCapture(DisplayComponent display) 
	{
		// Stop the capture and the file writer (DataSink)
		processor.stop();
		processor.close();
		datasink.close();
		
		display.removeDisplayRepaintListener(datasource.getDisplayRepaintListener());
		
		System.out.println("Done saving.");
	}

}
