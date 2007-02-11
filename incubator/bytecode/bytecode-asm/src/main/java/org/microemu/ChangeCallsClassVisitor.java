package org.microemu;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class ChangeCallsClassVisitor extends ClassAdapter {

	private boolean traceClassLoading;
	
	public ChangeCallsClassVisitor(ClassVisitor cv, boolean traceClassLoading) {
		super(cv);
		this.traceClassLoading = traceClassLoading;
	}

    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
    	if (this.traceClassLoading) {
    		//TODO Logger.info();
    		System.out.println("Processing class " + name);
    	}
    	super.visit(version, access, name, signature, superName, interfaces);
	}
    
	public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
		return  new ChangeCallsMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions));
	}

}
