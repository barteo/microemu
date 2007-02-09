package org.microemu;

import java.io.FileOutputStream;

import org.objectweb.asm.*;

public class Preporcessor {


	public static void main(String[] args) throws Exception {

		ClassReader cr = new ClassReader("org.TestMain");
        ClassWriter cw = new ClassWriter(false);
        ClassVisitor cv = new ChangeCallsClassAdapter(cw);
        cr.accept(cv, false);
        byte[] b = cw.toByteArray();

        // stores the adapted class on disk
        FileOutputStream fos = new FileOutputStream("Test.class");
        fos.write(b);
        fos.close();
		
	}

}
