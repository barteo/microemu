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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.file.ConnectionClosedException;
import javax.microedition.io.file.FileConnection;

public class SystemFileConnection implements FileConnection {

	private static File fsRoot;
	
	private String host;
	
	private String fullPath;
	
	private File file;
	
	private Throwable locationClosedFrom = null;
	
	private InputStream opendInputStream;
	
	private OutputStream opendOutputStream;
	
	SystemFileConnection(String name) throws IOException {
		// <host>/<path>
		int hostEnd = name.indexOf('/');
		if (hostEnd == -1) {
			throw new IOException("Invalid path " + name);
		}
		host = name.substring(0, hostEnd);
		fullPath = name.substring(hostEnd + 1);
		this.file = new File(getRoot(), fullPath);
	}
	
	public static File getRoot() {
		try {
			if (fsRoot == null) {
				fsRoot = new File(System.getProperty("user.home") + "/.microemulator/filesystem");
				if (!fsRoot.exists()) {
					if (!fsRoot.mkdirs()) {
						throw new RuntimeException("Can't create filesystem root " + fsRoot.getAbsolutePath());
					}
				}
			}
			return fsRoot;
		} catch (SecurityException e) {
			System.out.println("Cannot access user.home in webstart: " + e);
			return null;
		}
	}
	
	static Enumeration listRoots() {
		File[] files = getRoot().listFiles();
        if (files == null) {  // null if security restricted
            return (new Vector()).elements();
        }
        Vector list = new Vector();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isHidden()) {
            	continue;
            }
            if (file.isDirectory()) {
            	list.add(file.getName() + "/");
            }
        }
        return list.elements();
	}
	
	public long availableSize() {
		throwClosed();
		if (getRoot() == null) {
			return -1;
		}
		// TODO 
		return 10000000;
	}
	
	public long totalSize() {
		throwClosed();
		if (getRoot() == null) {
			return -1;
		}
		// TODO 
		return 10000000;
	}

	public boolean canRead() {
		throwClosed();
		return this.file.canRead();
	}

	public boolean canWrite() {
		throwClosed();
		return this.file.canWrite();
	}

	public void create() throws IOException {
		throwClosed();
		if (!this.file.createNewFile()) {
			throw new IOException("File already exists  " + this.file.getAbsolutePath());
		}
	}

	public void delete() throws IOException {
		throwClosed();
		if (!this.file.delete()) {
			throw new IOException("Unable to delete " + this.file.getAbsolutePath());
		}
	}

	public long directorySize(boolean includeSubDirs) throws IOException {
		throwClosed();
		if (!this.file.isDirectory()) {
			throw new IOException("Not a directory " + this.file.getAbsolutePath());
		}
		return directorySize(this.file, includeSubDirs);
	}
	
	private static long directorySize(File dir, boolean includeSubDirs) throws IOException {
		long size = 0;
		
		File[] files = dir.listFiles();
        if (files == null) {  // null if security restricted
            return 0L;
        }
        for (int i = 0; i < files.length; i++) {
            File child = files[i];

            if (includeSubDirs && child.isDirectory()) {
                size += directorySize(child, true);
            } else {
                size += child.length();
            }
        }
        
		return size;
	}

	public boolean exists() {
		throwClosed();
		return this.file.exists();
	}

	public long fileSize() throws IOException {
		throwClosed();
		return this.file.length();
	}

	public String getName() {
		// TODO test on real device. Not declared
		throwClosed();

		if (this.file.isDirectory()) {
			return this.file.getName() + "/";
		} else {
			return this.file.getName();
		}
	}

	public String getPath() {
		// TODO test on real device. Not declared
		throwClosed();
		
        //  /<root>/<directory>/
		int pathEnd = fullPath.lastIndexOf('/');
		if (pathEnd == -1) {
			return "/";
		}
		return '/' + fullPath.substring(0, pathEnd + 1);
	}

	public String getURL() {
		// TODO test on real device. Not declared
		throwClosed();
		
		// file://<host>/<root>/<directory>/<filename.extension>
		//  or
		// file://<host>/<root>/<directory>/<directoryname>/
		return Connection.PROTOCOL + this.host + '/' + fullPath;
	}

	public boolean isDirectory() {
		throwClosed();
		return this.file.isDirectory();
	}

	public boolean isHidden() {
		throwClosed();
		return this.file.isHidden();
	}

	public long lastModified() {
		throwClosed();
		return this.file.lastModified();
	}

	public void mkdir() throws IOException {
		throwClosed();
		if (!this.file.mkdir()) {
			throw new RuntimeException("Can't create directory " + this.file.getAbsolutePath());
		}
	}
	
	public Enumeration list() throws IOException {
        return this.list(null, false);
	}

	public Enumeration list(String filter, boolean includeHidden) throws IOException {
		throwClosed();
		if (!this.file.isDirectory()) {
			throw new IOException("Not a directory " + this.file.getAbsolutePath());
		}
		// TODO
		FilenameFilter filenameFilter = null;
		
		File[] files = this.file.listFiles(filenameFilter);
        if (files == null) {  // null if security restricted
            return (new Vector()).elements();
        }
        Vector list = new Vector();
        for (int i = 0; i < files.length; i++) {
            File child = files[i];
            if ((!includeHidden) && (child.isHidden())) {
            	continue;
            }
            if (child.isDirectory()) {
            	list.add(child.getName() + "/");
            } else {
            	list.add(child.getName());
            }
        }
        return list.elements();
	}

	public boolean isOpen() {
		return (this.file != null);
	}
	
	private void throwOpenDirectory() throws IOException {
		if (this.file.isDirectory()) {
			throw new IOException("Unable to open Stream on directory " + this.file.getAbsolutePath());
		}
	}

	public InputStream openInputStream() throws IOException {
		throwClosed();
		throwOpenDirectory();
		
		if (this.opendInputStream != null) {
			throw new IOException("InputStream already opened");
		}
		/**
		 * Trying to open more than one InputStream or more than one OutputStream from a StreamConnection causes an IOException. 
		 */
		this.opendInputStream = new FileInputStream(this.file) {
			public void close() throws IOException {
				SystemFileConnection.this.opendInputStream = null;
				super.close();
			}
		};
		return this.opendInputStream;
	}
	
	public DataInputStream openDataInputStream() throws IOException {
		return new DataInputStream(openInputStream());
	}

	public OutputStream openOutputStream() throws IOException {
		return openOutputStream(false);
	}
	
	private OutputStream openOutputStream(boolean append) throws IOException {
		throwClosed();
		throwOpenDirectory();
		
		if (this.opendOutputStream != null) {
			throw new IOException("OutputStream already opened");
		}
		/**
		 * Trying to open more than one InputStream or more than one OutputStream from a StreamConnection causes an IOException. 
		 */
		this.opendOutputStream = new FileOutputStream(this.file) {
			public void close() throws IOException {
				SystemFileConnection.this.opendOutputStream = null;
				super.close();
			}
		};
		
		return this.opendOutputStream;
	}

	public DataOutputStream openDataOutputStream() throws IOException {
		return new DataOutputStream(openOutputStream());
	}
	
	public OutputStream openOutputStream(long byteOffset) throws IOException {
		throwClosed();
		throwOpenDirectory();
		if (this.opendOutputStream != null) {
			throw new IOException("OutputStream already opened");
		}
		truncate(byteOffset);
		return openOutputStream(true);
	}

	public void rename(String newName) throws IOException {
		throwClosed();
		if (newName.indexOf('/') != -1) {
			throw new IllegalArgumentException("Name contains path specification " + newName);
		}
		File newFile = new File(this.file.getParentFile(), newName);
		if (!this.file.renameTo(newFile)) {
			throw new IOException("Unable to rename " + this.file.getAbsolutePath() + " to " + newFile.getAbsolutePath());
		}
		this.fullPath = this.getPath() + newName;
	}

	public void setFileConnection(String s) throws IOException {
		throwClosed();
		// TODO Auto-generated method stub

	}

	public void setHidden(boolean hidden) throws IOException {
		throwClosed();
	}

	public void setReadable(boolean readable) throws IOException {
		throwClosed();
		// TODO use Java6 function in reflection.
	}

	public void setWritable(boolean writable) throws IOException {
		throwClosed();
		if (!writable) {
			this.file.setReadOnly();
		}
		// TODO use Java6 function in reflection.
	}

	public void truncate(long byteOffset) throws IOException {
		throwClosed();
		RandomAccessFile raf = new RandomAccessFile(this.file, "rw");
		try {
			raf.setLength(byteOffset);
		} finally {
			raf.close();
		}
	}

	public long usedSize() {
		throwClosed();
		return this.file.length();
	}

	public void close() throws IOException {
		if (this.file != null) {
			locationClosedFrom = new Throwable();
			locationClosedFrom.fillInStackTrace();
			this.file = null;
		}
	}

	private void throwClosed() throws ConnectionClosedException {
		if (this.file == null) {
			if (locationClosedFrom != null) {
				locationClosedFrom.printStackTrace();
			}
			throw new ConnectionClosedException("Connection already closed");
		}
	}
}
