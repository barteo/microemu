package org.microemu;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class ChangeCallsClassAdapter extends ClassAdapter {

	public ChangeCallsClassAdapter(ClassVisitor cv) {
		super(cv);
	}

	public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
		return  new ChangeCallsMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions));
	}

}
