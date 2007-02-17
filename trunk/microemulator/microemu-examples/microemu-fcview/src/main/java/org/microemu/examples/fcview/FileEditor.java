/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
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
