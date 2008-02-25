/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
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
package org.microemu.examples.fcview;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.file.FileConnection;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

/**
 * @author vlads
 *
 */
public class FileEditor extends TextBox  implements CommandListener {

	static final Command saveCommand = new Command("Save", Command.OK, 1);
	
	static final Command backCommand = new Command("Back", Command.BACK, 5);
	
	private Displayable back;
	
	FileConnection file;
	
	public FileEditor(FileConnection fc, Displayable back) {
		super("Edit " + fc.getName(), null, 128, TextField.ANY);
		this.back = back;
		addCommand(saveCommand);
		addCommand(backCommand);
		setCommandListener(this);
		
		file = fc;
		load();
	}

	private void load() {
		DataInputStream is = null; 
		try {
			is = file.openDataInputStream();
			this.setString(is.readUTF());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					((InputStream)is).close();
				}
			} catch (IOException ignore) {
			}
		}
	}

	private void save() {
		DataOutputStream os = null; 
		try {
			os = file.openDataOutputStream();
			os.writeUTF(this.getString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					((OutputStream)os).close();
				}
			} catch (IOException ignore) {
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command c, Displayable d) {
		if (c == backCommand) {
			try {
				file.close();
			} catch (IOException ignore) {
			}
			FCViewMIDlet.setCurrentDisplayable(back);
		} else if (c == saveCommand) {
			save();
		}
		
	}
}
