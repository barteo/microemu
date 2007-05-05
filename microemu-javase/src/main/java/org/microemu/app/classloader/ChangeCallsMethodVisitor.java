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

import java.util.HashMap;

import org.microemu.Injected;
import org.microemu.app.util.MIDletThread;
import org.microemu.app.util.MIDletTimer;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author vlads
 *
 */
public class ChangeCallsMethodVisitor extends MethodAdapter implements Opcodes {

	private static final String INJECTED_CLASS = codeName(Injected.class);
	
	static String NEW_SYSTEM_OUT_CLASS = INJECTED_CLASS;
	
	static String NEW_SYSTEM_PROPERTIES_CLASS = INJECTED_CLASS;
	
	static String NEW_RESOURCE_LOADER_CLASS = INJECTED_CLASS;
	
	private HashMap catchInfo;
	
	private InstrumentationConfig config;
	
	private static class CatchInformation {
		
		Label label; 
		
		String type;

		public CatchInformation(String type) {
			this.label = new Label();
			this.type = type;
		}
	}
	
	public ChangeCallsMethodVisitor(MethodVisitor mv, InstrumentationConfig config) {
		super(mv);
		this.config = config;
	}

	public static String codeName(Class klass) {
		return klass.getName().replace('.', '/');
	}

    public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
		switch (opcode) {
		case GETSTATIC:
			if ((name.equals("out")) && (owner.equals("java/lang/System"))) {
				//System.out.println("owner " + owner + " name " + name + " desc " + desc);
				// GETSTATIC System.out : PrintStream
				mv.visitFieldInsn(opcode, NEW_SYSTEM_OUT_CLASS, name, desc);
				return;
			}
			if ((name.equals("err")) && (owner.equals("java/lang/System"))) {
				//System.out.println("owner " + owner + " name " + name + " desc " + desc);
				// GETSTATIC System.out : PrintStream
				mv.visitFieldInsn(opcode, NEW_SYSTEM_OUT_CLASS, name, desc);
				return;
			}
			break;

		}
		mv.visitFieldInsn(opcode, owner, name, desc);
	}
    
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
		switch (opcode) {
		case INVOKESTATIC:
			//System.out.println("Method owner " + owner + " name " + name + " desc " + desc);
			if ((name.equals("getProperty")) && (owner.equals("java/lang/System"))) {
				// INVOKESTATIC
                // java/lang/System.getProperty(Ljava/lang/String;)Ljava/lang/String;
				mv.visitMethodInsn(opcode, NEW_SYSTEM_PROPERTIES_CLASS, name, desc);
				return;
			}
			break;
		case INVOKEVIRTUAL:
			if ((name.equals("getResourceAsStream")) && (owner.equals("java/lang/Class"))) {
				// INVOKEVIRTUAL
		        // java/lang/Class.getResourceAsStream(Ljava/lang/String;)Ljava/io/InputStream;
				// "org/microemu/ResourceLoader", "getResourceAsStream", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/io/InputStream;");
				mv.visitMethodInsn(INVOKESTATIC, NEW_RESOURCE_LOADER_CLASS, name, "(Ljava/lang/Class;Ljava/lang/String;)Ljava/io/InputStream;");
				return;
			} else if ((name.equals("printStackTrace")) && (owner.equals("java/lang/Throwable"))) {
				// INVOKEVIRTUAL java/lang/Throwable.printStackTrace()V
				mv.visitMethodInsn(INVOKESTATIC, INJECTED_CLASS, name, "(Ljava/lang/Throwable;)V");
				return;
			}
			break;
		case INVOKESPECIAL:
			if  ((config.isEnhanceThreadCreation()) && (name.equals("<init>"))) {
				if (owner.equals("java/util/Timer")) {
					owner = codeName(MIDletTimer.class);
				} else if (owner.equals("java/lang/Thread")) {
					owner = codeName(MIDletThread.class);
				}
			}
			break;
		}

		mv.visitMethodInsn(opcode, owner, name, desc);
	}
	
    public void visitTypeInsn(final int opcode, String desc) {
    	if ((opcode == NEW) && (config.isEnhanceThreadCreation())) {
    		if ("java/util/Timer".equals(desc)) {
    			desc = codeName(MIDletTimer.class);
    		} else if ("java/lang/Thread".equals(desc)) {
    			desc = codeName(MIDletThread.class);
    		}
    	} 
    	mv.visitTypeInsn(opcode, desc);
    }
    
    public void visitTryCatchBlock(final Label start, final Label end, final Label handler, final String type) {
    	if (config.isEnhanceCatchBlock() && type != null) {
    		if (catchInfo == null) {
    			catchInfo = new HashMap(); 
    		}
    		CatchInformation newHandler = (CatchInformation)catchInfo.get(handler);
    		if (newHandler == null) {
    			newHandler = new CatchInformation(type);
    			catchInfo.put(handler, newHandler);
    		}
    		mv.visitTryCatchBlock(start, end, newHandler.label, type);
    	} else {
    		mv.visitTryCatchBlock(start, end, handler, type);
    	}
	}
    
    //TODO make this work for gMaps case
    public void visitLabel(Label label) {
    	if (config.isEnhanceCatchBlock() && catchInfo != null) {
    		CatchInformation newHandler = (CatchInformation)catchInfo.get(label);
    		if (newHandler != null) {
    			mv.visitLabel(newHandler.label);
    			// no push, just use current Throwable in stack
    			mv.visitMethodInsn(INVOKESTATIC, INJECTED_CLASS, "handleCatchThrowable", "(Ljava/lang/Throwable;)Ljava/lang/Throwable;");
    			// stack contains Throwable, just verify that it is right type for this handler
        		mv.visitTypeInsn(CHECKCAST, newHandler.type);
    		}	
    	}
    	mv.visitLabel(label);
    }
	
}