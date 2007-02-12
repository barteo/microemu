package org.microemu;

import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ChangeCallsMethodVisitor extends MethodAdapter implements Opcodes {

	static String NEW_SYSTEM_OUT_CLASS = codeName(OutputStreamRedirector.class);
	
	static String NEW_SYSTEM_PROPERTIES_CLASS = codeName(SystemProperties.class);
	
	static String NEW_RESOURCE_LOADER_CLASS = codeName(ResourceLoader.class);
	
	public ChangeCallsMethodVisitor(MethodVisitor mv) {
		super(mv);
	}

	private static String codeName(Class klass) {
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
				// "org/microemu/ResourceLoader", "getResourceAsStream", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/io/InputStream;");
				mv.visitMethodInsn(INVOKESTATIC, NEW_RESOURCE_LOADER_CLASS, name, "(Ljava/lang/Object;Ljava/lang/String;)Ljava/io/InputStream;");
				return;
			}
		}

		mv.visitMethodInsn(opcode, owner, name, desc);
	}

}
