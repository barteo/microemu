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
 *  @version $Id: AppletProducer.java 940 2007-02-16 20:59:37Z barteo $
 */

package org.microemu.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.microemu.app.classloader.ChangeCallsClassVisitor;
import org.microemu.app.util.IOUtils;
import org.microemu.device.Device;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public class AppletProducer {

	public static void createHtml(File htmlOutputFile, Device device, String className, File midletOutputFile,
			File appletPackageOutputFile, File deviceOutputFile, String deviceDescriptorLocation) throws IOException {
		int width = device.getNormalImage().getWidth();
		int height = device.getNormalImage().getHeight();
		
		FileWriter writer = null;
		try {
			writer = new FileWriter(htmlOutputFile);
			writer.write("");
			writer.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n\n");
			writer.write("<html>\n");
			writer.write("\t<head>\n");
			writer.write("\t\t<title>MicroEmulator</title>\n");
			writer.write("\t</head>\n");
			writer.write("\t<body>\n");
			writer.write("\t\t<applet code=\"org.microemu.applet.Main\"\n");
			writer.write("\t\t\t\twidth=\"" + width + "\" height=\"" + height + "\"\n");
			writer.write("\t\t\t\tarchive=\"" + appletPackageOutputFile.getName() + ",");
			if (deviceOutputFile != null) {
				writer.write(deviceOutputFile.getName() + ",");
			}
			writer.write(midletOutputFile.getName() + "\">\n");
			writer.write("\t\t\t<param name=\"midlet\" value=\"" + className + "\">\n");
			if (deviceDescriptorLocation != null) {
				writer.write("\t\t\t<param name=\"device\" value=\"" + deviceDescriptorLocation + "\">\n");
			}
			writer.write("\t\t</applet>\n");
			writer.write("\t</body>\n");
			writer.write("</html>\n");
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

	public static void createMidlet(String midletInput, File midletOutputFile) throws IOException {
		JarInputStream jis = null;
		JarOutputStream jos = null;
		try {
			jis = new JarInputStream(new URL(midletInput).openStream());
			Manifest manifest = jis.getManifest();
			if (manifest == null) {
				jos = new JarOutputStream(new FileOutputStream(midletOutputFile));
			} else {
				jos = new JarOutputStream(new FileOutputStream(midletOutputFile), manifest);
			}
		
			byte[] inputBuffer = new byte[1024];
			JarEntry jarEntry;
			while ((jarEntry = jis.getNextJarEntry()) != null) {
				if (jarEntry.isDirectory() == false) {
					String name = jarEntry.getName();
					int size = 0;
					int read;
					int length = inputBuffer.length;
					while ((read = jis.read(inputBuffer, size, length)) > 0) {
						size += read;
						
						length = 1024;
						if (size + length > inputBuffer.length) {
							byte[] newInputBuffer = new byte[size + length];
							System.arraycopy(inputBuffer, 0, newInputBuffer, 0, inputBuffer.length);
							inputBuffer = newInputBuffer;
						}
					}
					
					byte[] outputBuffer = inputBuffer;
					int outputSize = size;
					if (name.endsWith(".class")) {					
						ClassReader cr = new ClassReader(inputBuffer, 0, size);
				        ClassWriter cw = new ClassWriter(false);
				        ClassVisitor cv = new ChangeCallsClassVisitor(cw);
				        cr.accept(cv, false);
				        outputBuffer = cw.toByteArray();
				        outputSize = outputBuffer.length;
					}
					jos.putNextEntry(new JarEntry(name));
					jos.write(outputBuffer, 0, outputSize);
				}
			}
		} finally {
			IOUtils.closeQuietly(jis);
			IOUtils.closeQuietly(jos);
		}
	}
	
}
