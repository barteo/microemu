/**
 *  MicroEmulator
 *  Copyright (C) 2008 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.android.asm;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AndroidClassVisitor extends ClassAdapter {
	
	boolean isMidlet;
	
	public class AndroidMethodVisitor extends MethodAdapter {

		public AndroidMethodVisitor(MethodVisitor mv) {
			super(mv);
		}
		
	    public void visitFieldInsn(final int opcode, String owner, final String name, String desc) {
			mv.visitFieldInsn(opcode, fixPackage(owner), name, fixPackage(desc));
		}	

		public void visitMethodInsn(int opcode, String owner, String name, String desc) {
			if (isMidlet && opcode == Opcodes.INVOKEVIRTUAL) {
				if ((name.equals("getResourceAsStream")) && (owner.equals("java/lang/Class"))) {							
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, fixPackage("org/microemu/MIDletBridge"), name, fixPackage("(Ljava/lang/Class;Ljava/lang/String;)Ljava/io/InputStream;"));
					return;
				}
			}
			
			mv.visitMethodInsn(opcode, fixPackage(owner), name, fixPackage(desc));
		}

		public void visitTypeInsn(final int opcode, String desc) {
			super.visitTypeInsn(opcode, fixPackage(desc));
		}
		
	}

	public AndroidClassVisitor(ClassVisitor cv, boolean isMidlet) {
		super(cv);
		
		this.isMidlet = isMidlet;
	}

    public void visit(final int version, final int access, String name, final String signature, String superName, final String[] interfaces) {
    	for (int i = 0; i < interfaces.length; i++) {
    		interfaces[i] = fixPackage(interfaces[i]);
    	}
    	super.visit(version, access, fixPackage(name), signature, fixPackage(superName), interfaces);
	}

	public FieldVisitor visitField(final int access, final String name, String desc, final String signature, final Object value) {
		return super.visitField(access, name, fixPackage(desc), signature, value);
	}

	public MethodVisitor visitMethod(final int access, final String name, String desc, final String signature, final String[] exceptions) {
		return new AndroidMethodVisitor(super.visitMethod(access, name, fixPackage(desc), signature, exceptions));
	}
	
	public static String fixPackage(String name) {
		int index = name.indexOf("javax/microedition/lcdui/");
		if (index != -1) {
			name = name.replaceAll("javax/microedition/lcdui/", "javax/microedition/android/lcdui/");
		}
		
		return name;
	}		
	
}
