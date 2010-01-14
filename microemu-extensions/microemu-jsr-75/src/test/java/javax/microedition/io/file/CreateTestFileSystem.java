/**
 *  MicroEmulator
 *  Copyright (C) 2001-2007 Bartek Teodorczyk <barteo@barteo.net>
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
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
