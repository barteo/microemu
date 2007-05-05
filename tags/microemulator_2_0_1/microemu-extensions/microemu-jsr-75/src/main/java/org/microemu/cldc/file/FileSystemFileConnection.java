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
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.file.ConnectionClosedException;
import javax.microedition.io.file.FileConnection;

public class FileSystemFileConnection implements FileConnection {

	private static File fsRoot;
	
	private String host;
	
	private String fullPath;
	
	private File file;
	
	private boolean isRoot;
	
	private boolean isDirectory;
	
	private Throwable locationClosedFrom = null;
	
	private InputStream opendInputStream;
	
	private OutputStream opendOutputStream;
	
	private final static char  DIR_SEP = '/';
	
	private final static String  DIR_SEP_STR = "/";
	
	/* The context to be used when acessing filesystem */
    private AccessControlContext acc;
	
	FileSystemFileConnection(String name) throws IOException {
		// <host>/<path>
		int hostEnd = name.indexOf(DIR_SEP);
		if (hostEnd == -1) {
			throw new IOException("Invalid path " + name);
		}
		host = name.substring(0, hostEnd);
		fullPath = name.substring(hostEnd + 1);
		int rootEnd = fullPath.indexOf(DIR_SEP); 
		isRoot = ((rootEnd == -1) || (rootEnd == fullPath.length() - 1));
		if (fullPath.charAt(fullPath.length() - 1) == DIR_SEP) {
			fullPath = fullPath.substring(0, fullPath.length() -1);
		}
		acc = AccessController.getContext();
		AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				file = new File(getRoot(), fullPath);
				isDirectory = file.isDirectory();
				return null;
			}
		}, acc);
	}
	
	private Object doPrivilegedIO(PrivilegedExceptionAction action)	throws IOException {
		return FileSystemConnectorImpl.doPrivilegedIO(action, acc);
	}
	
	private abstract class PrivilegedBooleanAction implements PrivilegedAction {
		public Object run() {
			return new Boolean(getBoolean());
		}
		abstract boolean getBoolean();
	}
	
	private boolean doPrivilegedBoolean(PrivilegedBooleanAction action) {
		return ((Boolean)AccessController.doPrivileged(action)).booleanValue();
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
			System.out.println("Cannot access user.home " + e);
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
            	list.add(file.getName() + DIR_SEP);
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
		return doPrivilegedBoolean(new PrivilegedBooleanAction() {
			public boolean getBoolean() {
				return file.canRead();
			}
		});
	}

	public boolean canWrite() {
		throwClosed();
		return doPrivilegedBoolean(new PrivilegedBooleanAction() {
			public boolean getBoolean() {
				return file.canWrite();
			}
		});
	}

	public void create() throws IOException {
		throwClosed();
		doPrivilegedIO(new PrivilegedExceptionAction() {
			public Object run() throws IOException {
				if (!file.createNewFile()) {
					throw new IOException("File already exists  " + file.getAbsolutePath());
				}
				return null;
			}
		});
	}

	public void delete() throws IOException {
		throwClosed();
		doPrivilegedIO(new PrivilegedExceptionAction() {
			public Object run() throws IOException {
				if (!file.delete()) {
					throw new IOException("Unable to delete " + file.getAbsolutePath());
				}
				return null;
			}
		});
	}

	public long directorySize(final boolean includeSubDirs) throws IOException {
		throwClosed();
		return ((Long)doPrivilegedIO(new PrivilegedExceptionAction() {
			public Object run() throws IOException {
				if (!file.isDirectory()) {
					throw new IOException("Not a directory " +file.getAbsolutePath());
				}
				return new Long(directorySize(file, includeSubDirs));
			}
		})).longValue();
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
		return doPrivilegedBoolean(new PrivilegedBooleanAction() {
			public boolean getBoolean() {
				return file.exists();
			}
		});
	}

	public long fileSize() throws IOException {
		throwClosed();
		return ((Long)doPrivilegedIO(new PrivilegedExceptionAction() {
			public Object run() throws IOException {
				return new Long(file.length());
			}
		})).longValue();
	}

	public String getName() {
		// TODO test on real device. Not declared
		throwClosed();

		if (isRoot) {
			return "";
		}
		
		if (this.isDirectory) {
			return this.file.getName() + DIR_SEP;
		} else {
			return this.file.getName();
		}
	}

	public String getPath() {
		// TODO test on real device. Not declared
		throwClosed();
		
		//returns Parent directory
        //  /<root>/<directory>/
		if (isRoot) {
			return DIR_SEP + fullPath + DIR_SEP;
		}
		
		int pathEnd = fullPath.lastIndexOf(DIR_SEP);
		if (pathEnd == -1) {
			return DIR_SEP_STR;
		}
		return DIR_SEP + fullPath.substring(0, pathEnd + 1);
	}

	public String getURL() {
		// TODO test on real device. Not declared
		throwClosed();
		
		// file://<host>/<root>/<directory>/<filename.extension>
		//  or
		// file://<host>/<root>/<directory>/<directoryname>/
		return Connection.PROTOCOL + this.host + DIR_SEP + fullPath + ((this.isDirectory)?DIR_SEP_STR:"");
	}

	public boolean isDirectory() {
		throwClosed();
		return this.isDirectory;
	}

	public boolean isHidden() {
		throwClosed();
		return doPrivilegedBoolean(new PrivilegedBooleanAction() {
			public boolean getBoolean() {
				return file.isHidden();
			}
		});
	}

	public long lastModified() {
		throwClosed();
		return ((Long)AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return new Long(file.lastModified());
			}
		}, acc)).longValue();
	}

	public void mkdir() throws IOException {
		throwClosed();
		doPrivilegedIO(new PrivilegedExceptionAction() {
			public Object run() throws IOException {
				if (!file.mkdir()) {
					throw new IOException("Can't create directory " + file.getAbsolutePath());
				}
				return null;
			}
		});
	}
	
	public Enumeration list() throws IOException {
        return this.list(null, false);
	}

	public Enumeration list(final String filter, final boolean includeHidden) throws IOException {
		throwClosed();
		return (Enumeration)doPrivilegedIO(new PrivilegedExceptionAction() {
			public Object run() throws IOException {
				return listPrivileged(filter, includeHidden);
			}
		});
	}
	
	private Enumeration listPrivileged(String filter, boolean includeHidden) throws IOException {	
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
            	list.add(child.getName() + DIR_SEP);
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
		if (this.isDirectory) {
			throw new IOException("Unable to open Stream on directory");
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
		this.opendInputStream = (InputStream)doPrivilegedIO(new PrivilegedExceptionAction() {
			public Object run() throws IOException {
				return new FileInputStream(file) {
					public void close() throws IOException {
						FileSystemFileConnection.this.opendInputStream = null;
						super.close();
					}
				};
			}
		});
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
		this.opendOutputStream = (OutputStream)doPrivilegedIO(new PrivilegedExceptionAction() {
			public Object run() throws IOException {
				return new FileOutputStream(file) {
					public void close() throws IOException {
						FileSystemFileConnection.this.opendOutputStream = null;
						super.close();
					}
				};
			}
		}); 
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

	public void rename(final String newName) throws IOException {
		throwClosed();
		if (newName.indexOf(DIR_SEP) != -1) {
			throw new IllegalArgumentException("Name contains path specification " + newName);
		}
		doPrivilegedIO(new PrivilegedExceptionAction() {
			public Object run() throws IOException {
				File newFile = new File(file.getParentFile(), newName);
				if (!file.renameTo(newFile)) {
					throw new IOException("Unable to rename " + file.getAbsolutePath() + " to " + newFile.getAbsolutePath());
				}
				return null;
			}
		}); 
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
			doPrivilegedIO(new PrivilegedExceptionAction() {
				public Object run() throws IOException {
					file.setReadOnly();
					return null;
				}
			});
		}
		// TODO use Java6 function in reflection.
	}

	public void truncate(final long byteOffset) throws IOException {
		throwClosed();
		doPrivilegedIO(new PrivilegedExceptionAction() {
			public Object run() throws IOException {
				RandomAccessFile raf = new RandomAccessFile(file, "rw");
				try {
					raf.setLength(byteOffset);
				} finally {
					raf.close();
				}
				return null;
			}
		});
	}

	public long usedSize() {
		try {
			return fileSize();
		} catch (IOException e) {
			return -1;
		}
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
