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
 */

package org.microemu.app.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import org.microemu.MicroEmulator;
import org.microemu.RecordStoreManager;
import org.microemu.app.Config;
import org.microemu.log.Logger;
import org.microemu.util.ExtendedRecordListener;
import org.microemu.util.RecordStoreImpl;

public class FileRecordStoreManager implements RecordStoreManager {

	private final static String RECORD_STORE_SUFFIX = ".rms";

	private final static List replaceChars = new Vector();

	private MicroEmulator emulator;

	private Hashtable testOpenRecordStores = new Hashtable();

	private ExtendedRecordListener recordListener = null;

	/* The context to be used when accessing files in Webstart */
	private AccessControlContext acc;

	static {
		replaceChars.add(":");
		replaceChars.add("*");
		replaceChars.add("?");
		replaceChars.add("=");
		replaceChars.add("|");
		replaceChars.add("/");
		replaceChars.add("\\");
		replaceChars.add("\"");
	}

	private FilenameFilter filter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			if (name.endsWith(RECORD_STORE_SUFFIX)) {
				return true;
			} else {
				return false;
			}
		}
	};

	public void init(MicroEmulator emulator) {
		this.emulator = emulator;
		this.acc = AccessController.getContext();
	}

	public String getName() {
		return "File record store";
	}

	protected File getSuiteFolder() {
		return new File(Config.getConfigPath(), "suite-" + emulator.getLauncher().getSuiteName());
	}

	private static String escapeCharacter(String charcter) {
		return "_%%" + (int) (charcter.charAt(0)) + "%%_";
	}

	static String recordStoreName2FileName(String recordStoreName) {
		for (Iterator iterator = replaceChars.iterator(); iterator.hasNext();) {
			String c = (String) iterator.next();
			String newValue = escapeCharacter(c);
			if (c.equals("\\")) {
				c = "\\\\";
			}
			c = "[" + c + "]";
			recordStoreName = recordStoreName.replaceAll(c, newValue);
		}
		return recordStoreName + RECORD_STORE_SUFFIX;
	}

	static String fileName2RecordStoreName(String fileName) {
		for (Iterator iterator = replaceChars.iterator(); iterator.hasNext();) {
			String c = (String) iterator.next();
			String newValue = escapeCharacter(c);
			if (c.equals("\\")) {
				c = "\\\\";
			}
			fileName = fileName.replaceAll(newValue, c);
		}
		return fileName.substring(0, fileName.length() - RECORD_STORE_SUFFIX.length());
	}

	public void deleteRecordStore(final String recordStoreName) throws RecordStoreNotFoundException,
			RecordStoreException {
		final File storeFile = new File(getSuiteFolder(), recordStoreName2FileName(recordStoreName));

		RecordStoreImpl recordStoreImpl = (RecordStoreImpl) testOpenRecordStores.get(storeFile.getName());
		if (recordStoreImpl != null && recordStoreImpl.isOpen()) {
			throw new RecordStoreException();
		}

		try {
			recordStoreImpl = loadFromDisk(storeFile);
		} catch (FileNotFoundException ex) {
			throw new RecordStoreNotFoundException(recordStoreName);
		}

		try {
			AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() throws FileNotFoundException {
					storeFile.delete();
					fireRecordStoreListener(ExtendedRecordListener.RECORDSTORE_DELETE, recordStoreName);
					return null;
				}
			}, acc);
		} catch (PrivilegedActionException e) {
			Logger.error("Unable remove file " + storeFile, e);
			throw new RecordStoreException();
		}
	}

	public RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary) throws RecordStoreException {
		File storeFile = new File(getSuiteFolder(), recordStoreName2FileName(recordStoreName));

		RecordStoreImpl recordStoreImpl;
		try {
			recordStoreImpl = loadFromDisk(storeFile);
			recordStoreImpl.setOpen(true);
		} catch (FileNotFoundException e) {
			if (!createIfNecessary) {
				throw new RecordStoreNotFoundException(recordStoreName);
			}
			recordStoreImpl = new RecordStoreImpl(this, recordStoreName);
			recordStoreImpl.setOpen(true);
			saveToDisk(storeFile, recordStoreImpl);
		}
		if (recordListener != null) {
			recordStoreImpl.addRecordListener(recordListener);
		}

		testOpenRecordStores.put(storeFile.getName(), recordStoreImpl);

		fireRecordStoreListener(ExtendedRecordListener.RECORDSTORE_OPEN, recordStoreName);

		return recordStoreImpl;
	}

	public String[] listRecordStores() {
		String[] result;
		try {
			result = (String[]) AccessController.doPrivileged(new PrivilegedExceptionAction() {
				public Object run() {
					return getSuiteFolder().list(filter);
				}
			}, acc);
		} catch (PrivilegedActionException e) {
			Logger.error("Unable to access storeFiles", e);
			return null;
		}
		if (result != null) {
			if (result.length == 0) {
				result = null;
			} else {
				for (int i = 0; i < result.length; i++) {
					result[i] = fileName2RecordStoreName(result[i]);
				}
			}
		}
		return result;
	}

	public void deleteRecord(RecordStoreImpl recordStoreImpl, int recordId) throws RecordStoreNotOpenException, RecordStoreException 
	{
		saveRecord(recordStoreImpl, recordId);
	}
	
	public void loadRecord(RecordStoreImpl recordStoreImpl, int recordId)
			throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException 
	{
		// records are loaded when record store opens
	}

	public void saveRecord(RecordStoreImpl recordStoreImpl, int recordId) throws RecordStoreNotOpenException, RecordStoreException 
	{
		File storeFile = new File(getSuiteFolder(), recordStoreName2FileName(recordStoreImpl.getName()));

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
				throw (FileNotFoundException) e.getCause();
			}
			Logger.error("Unable access file " + recordStoreFile, e);
			throw new FileNotFoundException();
		}
	}

	private RecordStoreImpl loadFromDiskSecure(File recordStoreFile) throws FileNotFoundException {
		RecordStoreImpl store = null;
		try {
			store = new RecordStoreImpl(this);
			DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(recordStoreFile)));
			store.readHeader(dis);
			for (int i = 0; i < store.size; i++) {
				store.readRecord(dis);
			}
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
				throw (RecordStoreException) e.getCause();
			}
			Logger.error("Unable access file " + recordStoreFile, e);
			throw new RecordStoreException();
		}
	}

	private void saveToDiskSecure(final File recordStoreFile, final RecordStoreImpl recordStore)
			throws RecordStoreException {
		if (!recordStoreFile.getParentFile().exists()) {
			if (!recordStoreFile.getParentFile().mkdirs()) {
				throw new RecordStoreException("Unable to create recordStore directory");
			}
		}
		try {
			DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(recordStoreFile)));
			recordStore.writeHeader(dos);
			for (int i = 1; i <= recordStore.getNumRecords(); i++) {
				recordStore.writeRecord(dos, i);
			}
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

	public void setRecordListener(ExtendedRecordListener recordListener) {
		this.recordListener = recordListener;
	}

	public void fireRecordStoreListener(int type, String recordStoreName) {
		if (recordListener != null) {
			recordListener.recordStoreEvent(type, System.currentTimeMillis(), recordStoreName);
		}
	}
}
