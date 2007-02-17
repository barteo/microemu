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
 */

package org.microemu.app.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Hashtable;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import org.microemu.RecordStoreManager;
import org.microemu.app.Config;
import org.microemu.app.launcher.Launcher;
import org.microemu.log.Logger;
import org.microemu.util.RecordStoreImpl;

public class FileRecordStoreManager implements RecordStoreManager {
	
	private final static String RECORD_STORE_SUFFIX = ".rs";

	private Launcher launcher;

	private Hashtable testOpenRecordStores = new Hashtable();

	/* The context to be used when accessing files in Webstart */
    private AccessControlContext acc;
	
	private FilenameFilter filter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			if (name.endsWith(RECORD_STORE_SUFFIX)) {
				return true;
			} else {
				return false;
			}
		}
	};

	public FileRecordStoreManager(Launcher launcher) {
		this.launcher = launcher;
		this.acc = AccessController.getContext();
	}

	private File getSuiteFolder() {
		return new File(Config.getConfigPath(), "suite-" + launcher.getSuiteName());
	}
	
	public void deleteRecordStore(String recordStoreName) throws RecordStoreNotFoundException, RecordStoreException {
		final File storeFile = new File(getSuiteFolder(), recordStoreName + RECORD_STORE_SUFFIX);

		RecordStoreImpl recordStoreImpl = (RecordStoreImpl) testOpenRecordStores.get(storeFile.getName());
		if (recordStoreImpl != null && recordStoreImpl.isOpen()) {
			throw new RecordStoreException();
		}

		try {
			recordStoreImpl = loadFromDisk(storeFile);
		} catch (FileNotFoundException ex) {
			throw new RecordStoreNotFoundException();
		}

		try {
			AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws FileNotFoundException {
					storeFile.delete();
					return null;
				}
			}, acc);
		} catch (PrivilegedActionException e) {
			Logger.error("Unable remove file " + storeFile, e);
			throw new RecordStoreException();
		}
	}

	public RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary) throws RecordStoreException {
		File storeFile = new File(getSuiteFolder(), recordStoreName + RECORD_STORE_SUFFIX);

		RecordStoreImpl recordStoreImpl;
		try {
			recordStoreImpl = loadFromDisk(storeFile);
		} catch (FileNotFoundException e) {
			if (!createIfNecessary) {
				throw new RecordStoreNotFoundException();
			}
			recordStoreImpl = new RecordStoreImpl(this, recordStoreName);
			saveToDisk(storeFile, recordStoreImpl);
		}
		recordStoreImpl.setOpen(true);

		testOpenRecordStores.put(storeFile.getName(), recordStoreImpl);

		return recordStoreImpl;
	}

	public String[] listRecordStores() {
		String[] result;
		try {
			result = (String[])AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() {
					return getSuiteFolder().list(filter);
				}
			}, acc);
		} catch (PrivilegedActionException e) {
			Logger.error("Unable to acess storeFiles", e);
			return null;
		}
		if (result != null) {
			if (result.length == 0) {
				result = null;
			} else {
				for (int i = 0; i < result.length; i++) {
					result[i] = result[i].substring(0, result[i].length() - RECORD_STORE_SUFFIX.length());
				}
			}
		}
		return result;
	}

	public void saveChanges(RecordStoreImpl recordStoreImpl) throws RecordStoreNotOpenException, RecordStoreException {
		
		File storeFile = new File(getSuiteFolder(), recordStoreImpl.getName() + RECORD_STORE_SUFFIX);

		saveToDisk(storeFile, recordStoreImpl);
	}

	public void init() {
	}

	public void deleteStores() {
		String[] stores = listRecordStores();
		for (int i = 0; i < stores.length; i++) {
			String store = stores[i];
			try {
				deleteRecordStore(store);
			} catch (RecordStoreException e) {
				Logger.debug("deleteRecordStore", e);
			}
		}
	}

	private RecordStoreImpl loadFromDisk(final File recordStoreFile) throws FileNotFoundException {
		try {
			return (RecordStoreImpl) AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws FileNotFoundException {
					return loadFromDiskSecure(recordStoreFile);
				}
			}, acc);
		} catch (PrivilegedActionException e) {
			if (e.getCause() instanceof FileNotFoundException) {
				throw (FileNotFoundException)e.getCause(); 
			}
			Logger.error("Unable access file " + recordStoreFile, e);
			throw new FileNotFoundException();
		}
	}
	
	private RecordStoreImpl loadFromDiskSecure(File recordStoreFile) throws FileNotFoundException {
		RecordStoreImpl store = null;
		try {
			DataInputStream dis = new DataInputStream(new FileInputStream(recordStoreFile));
			store = new RecordStoreImpl(this, dis);
			dis.close();
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			Logger.error("RecordStore.loadFromDisk: ERROR reading " + recordStoreFile.getName(), e);
		}
		return store;
	}

	private void saveToDisk(final File recordStoreFile, final RecordStoreImpl recordStore) throws RecordStoreException {
		try {
			AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws RecordStoreException {
					saveToDiskSecure(recordStoreFile, recordStore);
					return null;
				}
			}, acc);
		} catch (PrivilegedActionException e) {
			if (e.getCause() instanceof RecordStoreException) {
				throw (RecordStoreException)e.getCause(); 
			}
			Logger.error("Unable access file " + recordStoreFile, e);
			throw new RecordStoreException();
		}
	}
	
	private void saveToDiskSecure(final  File recordStoreFile, final  RecordStoreImpl recordStore) throws RecordStoreException {
		if (!recordStoreFile.getParentFile().exists()) {
			if (!recordStoreFile.getParentFile().mkdirs()) {
				throw new RecordStoreException("Unable to create recordStore directory");
			}
		}
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(recordStoreFile));
			recordStore.write(dos);
			dos.close();
		} catch (IOException e) {
			Logger.error("RecordStore.saveToDisk: ERROR writting object to " + recordStoreFile.getName(), e);
			throw new RecordStoreException(e.getMessage());
		}
	}

	public int getSizeAvailable(RecordStoreImpl recordStoreImpl) {
		// FIXME should return free space on device
		return 1024 * 1024;
	}

}
