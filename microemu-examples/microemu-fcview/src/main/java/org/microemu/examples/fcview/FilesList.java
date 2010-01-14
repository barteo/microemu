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

	static final Command infoCommand = new Command("Info", Command.ITEM, 6);

	private FileConnection currentDir;

	private Image dirIcon = null, fileIcon = null;

	private final static char DIR_SEP = '/';

	public FilesList() {
		super("", List.IMPLICIT);
		this.addCommand(exitCommand);
		this.addCommand(infoCommand);
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
					sep = String.valueOf(DIR_SEP);
				}
				dir = (FileConnection) Connector.open("file://localhost" + sep + location);
				if (!dir.isDirectory()) {
					FCViewMIDlet.setCurrentDisplayable(new FileEditor(dir, this));
					return;
				}
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

	private void showInfo(String name) {
		String p;
		if (currentDir == null) {
			p = DIR_SEP + name;
		} else if (name.equals("..")) {
			return;
		} else {
			p = currentDir.getPath() + currentDir.getName() + name;
		}
		try {
			FileConnection f = (FileConnection) Connector.open("file://localhost" + p);

			StringBuffer message = new StringBuffer();

			message.append("totalSize:").append(f.totalSize()).append('\n');
			message.append("availableSize:").append(f.availableSize()).append('\n');

			Alert alert = new Alert("Info", message.toString(), null, AlertType.INFO);
			alert.setTimeout(Alert.FOREVER);
			FCViewMIDlet.setCurrentDisplayable(alert);
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
						changeDir(newDir);
					}
				}).start();
			} else if (c == infoCommand) {
				final String newDir = this.getString(this.getSelectedIndex());
				new Thread(new Runnable() {
					public void run() {
						showInfo(newDir);
					}
				}).start();
			} else if (c == exitCommand) {
				if (currentDir != null) {
					try {
						currentDir.close();
					} catch (IOException ignore) {
					}
					currentDir = null;
				}
				FCViewMIDlet.exit();
			}
		}

	}
}
