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

import java.io.IOException;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;

/**
 * @author vlads
 *
 */
public class FilesList extends List implements CommandListener {

	static final Command exitCommand = new Command("Exit", Command.EXIT, 5);

	private FileConnection currentDir;

	private Image dirIcon = null, fileIcon = null;

	private final static char  DIR_SEP = '/';
	
	public FilesList() {
		super("", List.IMPLICIT);
		this.addCommand(exitCommand);
		this.setCommandListener(this);
	}

	public void changeDir(String name) {
		if (currentDir == null) {
			setDir(name);
		} else if (name.equals("..")) {
			if (currentDir.getName().length() == 0) {
				setDir(null);
			} else {
				setDir(currentDir.getPath());
			}
		} else {
			setDir(currentDir.getPath() + currentDir.getName() + name);
		}
	}

	public void setDir(String location) {
		FileConnection dir = null;
		Enumeration fsEnum;
		try {
			if (location == null) {
				fsEnum = FileSystemRegistry.listRoots();
				this.setTitle("FS Roots");
			} else {
				System.out.println("cd " + location);
				String sep = "";
				if (location.charAt(0) != DIR_SEP) {
					sep  = String.valueOf(DIR_SEP);
				}
				dir = (FileConnection) Connector.open("file://localhost" + sep + location);
				this.setTitle(dir.getPath() + dir.getName());
				fsEnum = dir.list();
				System.out.println("new location " + dir.getURL());
			}

			this.deleteAll();
			if (location != null) {
				this.append("..", dirIcon);
			}
			while (fsEnum.hasMoreElements()) {
				String fileName = (String) fsEnum.nextElement();
				if (fileName.charAt(fileName.length() - 1) == DIR_SEP) {
					this.append(fileName, dirIcon);
				} else {
					this.append(fileName, fileIcon);
				}
			}
			if (currentDir != null) {
				currentDir.close();
			}
			currentDir = dir;
		} catch (IOException e) {
			showError(e.getMessage());
		}
	}

	private void showError(String message) {
		Alert alert = new Alert("Error", message, null, AlertType.ERROR);
		alert.setTimeout(Alert.FOREVER);
		FCViewMIDlet.setCurrentDisplayable(alert);
	}

	public void commandAction(Command c, Displayable d) {
		if (d == this) {
			if (c == List.SELECT_COMMAND) {
				final String newDir = this.getString(this.getSelectedIndex());
				 new Thread(new Runnable() {
		                public void run() {
		                	changeDir(newDir );
		                }
				 }).start();
			} else if (c == exitCommand) {
				FCViewMIDlet.exit();
			}
		}
		
	}
}
