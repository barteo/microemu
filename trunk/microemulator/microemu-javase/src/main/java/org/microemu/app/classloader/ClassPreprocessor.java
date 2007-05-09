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
package org.microemu.app.classloader;

import java.io.IOException;
import java.io.InputStream;

import org.microemu.log.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

/**
 * @author vlads
 *
 */
public class ClassPreprocessor {

	public static byte[] instrument(final InputStream classInputStream, InstrumentationConfig config) {
		try {
			ClassReader cr = new ClassReader(classInputStream);
			ClassWriter cw = new ClassWriter(0);
			ClassVisitor cv = new ChangeCallsClassVisitor(cw, config);
			cr.accept(cv, 0);
			return cw.toByteArray();
		} catch (IOException e) {
			Logger.error("Error loading MIDlet class", e);
			return null;
		} 
    }
	
}
