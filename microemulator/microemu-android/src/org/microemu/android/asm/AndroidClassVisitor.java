package org.microemu.android.asm;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;

public class AndroidClassVisitor extends ClassAdapter {
	
	public class AndroidMethodVisitor extends MethodAdapter {

		public AndroidMethodVisitor(MethodVisitor mv) {
			super(mv);
		}
		
	    public void visitFieldInsn(final int opcode, String owner, final String name, String desc) {
			mv.visitFieldInsn(opcode, fixPackage(owner), name, fixPackage(desc));
		}	

		public void visitMethodInsn(int opcode, String owner, String name, String desc) {
			mv.visitMethodInsn(opcode, fixPackage(owner), name, fixPackage(desc));
		}

		public void visitTypeInsn(final int opcode, String desc) {
			super.visitTypeInsn(opcode, fixPackage(desc));
		}
		
	}

	public AndroidClassVisitor(ClassVisitor cv) {
		super(cv);
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
