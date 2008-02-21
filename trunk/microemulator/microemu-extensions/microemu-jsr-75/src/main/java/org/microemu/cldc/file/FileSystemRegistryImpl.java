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
package org.microemu.cldc.file;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;

import javax.microedition.io.file.FileSystemListener;

import org.microemu.microedition.Implementation;

public class FileSystemRegistryImpl implements FileSystemRegistryDelegate, Implementation {

	/* The context to be used when acessing filesystem */
	private AccessControlContext acc;

	private String fsRoot;

	public FileSystemRegistryImpl(String fsRoot) {
		this.acc = AccessController.getContext();
		this.fsRoot = fsRoot;
	}

	public Enumeration listRoots() {
		switch (Connection.getConnectionType()) {
		case Connection.CONNECTIONTYPE_SYSTEM_FS:
			return (Enumeration) AccessController.doPrivileged(new PrivilegedAction() {
				public Object run() {
					return FileSystemFileConnection.listRoots(fsRoot);
				}
			}, acc);
		default:
			throw new RuntimeException("Invalid connectionType configuration");
		}
	}

	public boolean addFileSystemListener(FileSystemListener listener) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean removeFileSystemListener(FileSystemListener listener) {
		// TODO Auto-generated method stub
		return false;
	}

}
