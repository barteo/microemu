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

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AndroidClassVisitor extends ClassAdapter {
	
	boolean isMidlet;
	
	public class AndroidMethodVisitor extends MethodAdapter {

		public AndroidMethodVisitor(MethodVisitor mv) {
			super(mv);
		}
		
		public void visitMethodInsn(int opcode, String owner, String name, String desc) {
			if (isMidlet && opcode == Opcodes.INVOKEVIRTUAL) {
				if ((name.equals("getResourceAsStream")) && (owner.equals("java/lang/Class"))) {							
					mv.visitMethodInsn(Opcodes.INVOKESTATIC, "org/microemu/MIDletBridge", name, "(Ljava/lang/Class;Ljava/lang/String;)Ljava/io/InputStream;");
					return;
				}
			}
			
			mv.visitMethodInsn(opcode, owner, name, desc);
		}
		
	}

	public AndroidClassVisitor(ClassVisitor cv, boolean isMidlet) {
		super(cv);
		
		this.isMidlet = isMidlet;
	}

	public MethodVisitor visitMethod(final int access, final String name, String desc, final String signature, final String[] exceptions) {
		return new AndroidMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions));
	}
	
}
