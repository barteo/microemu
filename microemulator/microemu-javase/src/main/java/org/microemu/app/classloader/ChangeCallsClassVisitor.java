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

import org.microemu.log.Logger;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * @author vlads
 *
 */
public class ChangeCallsClassVisitor extends ClassAdapter {

	private boolean traceClassLoading;
	
	public ChangeCallsClassVisitor(ClassVisitor cv, boolean traceClassLoading) {
		super(cv);
		this.traceClassLoading = traceClassLoading;
	}

    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
    	if (this.traceClassLoading) {
    		Logger.info("Loading MIDlet class", name);
    	}
    	super.visit(version, access, name, signature, superName, interfaces);
	}
    
	public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
		return  new ChangeCallsMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions));
	}

}
