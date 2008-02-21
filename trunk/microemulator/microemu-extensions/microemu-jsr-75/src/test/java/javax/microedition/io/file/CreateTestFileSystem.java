/**
 *  MicroEmulator
 *  Copyright (C) 2001-2007 Bartek Teodorczyk <barteo@barteo.net>
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
package javax.microedition.io.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.microemu.cldc.file.FileSystemFileConnection;

public class CreateTestFileSystem {

	public static void main(String[] args) {
		init();
	}

	public static void init() {
		File fsRoot = FileSystemFileConnection.getRoot(null);
		File d1 = mkdir(fsRoot, "test/dir1");
		mkfile(d1, "f1.txt", "text");
		mkfile(d1, "f2.txt", "text2");
		mkdir(fsRoot, "test/dir2");
	}

	private static File mkdir(File fsRoot, String name) {
		File dir = new File(fsRoot, name);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new RuntimeException("Can't create filesystem directory " + dir.getAbsolutePath());
			}
		}
		return dir;
	}

	private static File mkfile(File dir, String name, String context) {
		File file = new File(dir, name);
		if (!file.exists()) {
			try {
				FileWriter w = new FileWriter(file);
				w.write(context);
				w.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}
}
