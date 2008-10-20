/**
 *  MicroEmulator
 *  Copyright (C) 2008 Bartek Teodorczyk <barteo@barteo.net>
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 *
 *  @version $Id$
 */

package org.microemu.android.asm;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public class AndroidProducer {

	private static byte[] instrument(final InputStream classInputStream, boolean isMidlet) throws IOException {
		ClassReader cr = new ClassReader(classInputStream);
		ClassWriter cw = new ClassWriter(0);
		ClassVisitor cv = new AndroidClassVisitor(cw, isMidlet);
		cr.accept(cv, 0);

		return cw.toByteArray();
    }

	public static void processJar(File jarInputFile, File jarOutputFile, boolean isMidlet) throws IOException {
		JarInputStream jis = null;
		JarOutputStream jos = null;
		try {
			jis = new JarInputStream(new FileInputStream(jarInputFile));
			Manifest manifest = jis.getManifest();
			if (manifest == null) {
				jos = new JarOutputStream(new FileOutputStream(jarOutputFile));
			} else {
				jos = new JarOutputStream(new FileOutputStream(jarOutputFile), manifest);
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
				        outputBuffer = instrument(new ByteArrayInputStream(inputBuffer, 0, size), isMidlet);
				        outputSize = outputBuffer.length;
					}
					jos.putNextEntry(new JarEntry(name));
					jos.write(outputBuffer, 0, outputSize);
				}
			}			
		} finally {
			if (jis != null) {
				jis.close();
			}
			if (jos != null) {
				jos.close();
			}
		}
	}
	
	public static void main(String args[]) {
		if (args.length < 2 || args.length > 3) {
			System.out.println("usage: AndroidProducer <infile> <outfile> [midlet]");
		} else {
			boolean isMidlet = false;
			if (args.length == 3 && args[2].toLowerCase().equals("midlet")) {
				isMidlet = true;
			}
			try {
				processJar(new File(args[0]), new File(args[1]), isMidlet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
