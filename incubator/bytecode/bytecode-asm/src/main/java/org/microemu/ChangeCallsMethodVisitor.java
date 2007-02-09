package org.microemu;

import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ChangeCallsMethodVisitor extends MethodAdapter implements Opcodes {

	public ChangeCallsMethodVisitor(MethodVisitor mv) {
		super(mv);
	}

	public void visitMethodInsn(int opcode, String owner, String name, String desc) {
		if ((opcode & Opcodes.ACC_STATIC) != 0) {
			System.out.println("owner " + owner + " name " + name + " desc " + desc);
			if ((name.equals("getProperty")) && (owner.equals("java/lang/System"))) {
				mv.visitMethodInsn(opcode, "org/microemu/SystemProperties", name, desc);
				return;
			}
		}
		mv.visitMethodInsn(opcode, owner, name, desc);
	}

}
