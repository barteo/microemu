package org.microemu;

import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ChangeCallsMethodVisitor extends MethodAdapter implements Opcodes {

	public ChangeCallsMethodVisitor(MethodVisitor mv) {
		super(mv);
	}

	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
		switch (opcode) {
		case INVOKESTATIC:
			System.out.println("Method owner " + owner + " name " + name + " desc " + desc);
			if ((name.equals("getProperty")) && (owner.equals("java/lang/System"))) {
				// INVOKESTATIC
                // java/lang/System.getProperty(Ljava/lang/String;)Ljava/lang/String;
				mv.visitMethodInsn(opcode, "org/microemu/SystemProperties", name, desc);
				return;
			}
			break;
		case INVOKEVIRTUAL:
			if ((name.equals("getResourceAsStream")) && (owner.equals("java/lang/Class"))) {
				// INVOKEVIRTUAL
		        // java/lang/Class.getResourceAsStream(Ljava/lang/String;)Ljava/io/InputStream;
				// "org/microemu/ResourceLoader", "getResourceAsStream", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/io/InputStream;");
				mv.visitMethodInsn(INVOKESTATIC, "org/microemu/ResourceLoader", name, "(Ljava/lang/Object;Ljava/lang/String;)Ljava/io/InputStream;");
				return;
			}
		}

		mv.visitMethodInsn(opcode, owner, name, desc);
	}

}
