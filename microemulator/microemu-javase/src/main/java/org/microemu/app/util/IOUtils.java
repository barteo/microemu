/*
 *  MicroEmulator
 *  Copyright (C) 2002 Bartek Teodorczyk <barteo@barteo.net>
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
 */

package org.microemu.app.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

/**
 * General IO stream manipulation utilities.
 * Some functions are based on org.apache.commons.io
 * 
 * <p>
 * This class provides static utility methods for input/output operations.
 * <ul>
 * <li>closeQuietly - these methods close a stream ignoring nulls and exceptions
 * </ul>
 * <p>
 */

public class IOUtils {

	/**
	 * Solution for JVM bug 
	 *  http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6351751
	 */
	public static String getCanonicalFileURL(File file) {
		String path = file.getAbsoluteFile().getPath();
		if (File.separatorChar != '/') {
			path = path.replace(File.separatorChar, '/');
		}
		// Not network path
		if (!path.startsWith("//")) {
			if (path.startsWith("/")) {
				path = "//" + path;
			} else {
				path = "///" + path;
			}
		}
		return "file:" + path;
	}
	
	public static String getCanonicalFileClassLoaderURL(File file) {
		String url = getCanonicalFileURL(file);
		if ((file.isDirectory()) && (!url.endsWith("/"))) {
			url += "/";
		}
		return url;
	}
	
	public static void copyFile(File src, File dst) throws IOException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(src);
			copyToFile(fis, dst);
		} finally {
			closeQuietly(fis); 
		}
	}
	
	public static void copyToFile(InputStream is, File dst) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(dst);
			byte[] buf = new byte[1024]; 
			int i = 0;
			while ((i = is.read(buf)) != -1) { 
				fos.write(buf, 0, i);
			}
		} finally {
			closeQuietly(fos);	
		}
	}
	
    /**
     * Unconditionally close an <code>InputStream</code>.
     * <p>
     * Equivalent to {@link InputStream#close()}, except any exceptions will be ignored.
     * This is typically used in finally blocks.
     *
     * @param input  the InputStream to close, may be null or already closed
     */
    public static void closeQuietly(InputStream input) {
        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException ignore) {
            // ignore
        }
    }
    
    /**
     * Unconditionally close an <code>OutputStream</code>.
     * <p>
     * Equivalent to {@link OutputStream#close()}, except any exceptions will be ignored.
     * This is typically used in finally blocks.
     *
     * @param output  the OutputStream to close, may be null or already closed
     */
    public static void closeQuietly(OutputStream output) {
        try {
            if (output != null) {
                output.close();
            }
        } catch (IOException ignore) {
            // ignore
        }
    }
    
    /**
     * Unconditionally close a <code>Writer</code>.
     * <p>
     * Equivalent to {@link Writer#close()}, except any exceptions will be ignored.
     * This is typically used in finally blocks.
     *
     * @param output  the Writer to close, may be null or already closed
     */
    public static void closeQuietly(Writer output) {
        try {
            if (output != null) {
                output.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }
}
