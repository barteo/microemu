/*
 *  MicroEmulator
 *  Copyright (C) 2001-2005 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.util;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.rms.RecordListener;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;

import org.microemu.MicroEmulator;
import org.microemu.RecordStoreManager;

public class MemoryRecordStoreManager implements RecordStoreManager {
	private Hashtable recordStores = new Hashtable();

	private RecordListener recordListener = null;

	public void init(MicroEmulator emulator) {
	}

	public String getName() {
		return "Memory record store";
	}

	public void deleteRecordStore(String recordStoreName) throws RecordStoreNotFoundException, RecordStoreException {
		RecordStoreImpl recordStoreImpl = (RecordStoreImpl) recordStores.get(recordStoreName);
		if (recordStoreImpl == null) {
			throw new RecordStoreNotFoundException(recordStoreName);
		}
		if (recordStoreImpl.isOpen()) {
			throw new RecordStoreException();
		}
		recordStores.remove(recordStoreName);

		fireRecordStoreListener(RecordListener.RECORDSTORE_DELETE, recordStoreName);
	}

	public RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary)
			throws RecordStoreNotFoundException {
		RecordStoreImpl recordStoreImpl = (RecordStoreImpl) recordStores.get(recordStoreName);
		if (recordStoreImpl == null) {
			if (!createIfNecessary) {
				throw new RecordStoreNotFoundException(recordStoreName);
			}
			recordStoreImpl = new RecordStoreImpl(this, recordStoreName);
			recordStores.put(recordStoreName, recordStoreImpl);
		}
		recordStoreImpl.setOpen(true);
		if (recordListener != null) {
			recordStoreImpl.addRecordListener(recordListener);
		}

		fireRecordStoreListener(RecordListener.RECORDSTORE_OPEN, recordStoreName);

		return recordStoreImpl;
	}

	public String[] listRecordStores() {
		String[] result = null;

		int i = 0;
		for (Enumeration e = recordStores.keys(); e.hasMoreElements();) {
			if (result == null) {
				result = new String[recordStores.size()];
			}
			result[i] = (String) e.nextElement();
			i++;
		}

		return result;
	}

	public void saveChanges(RecordStoreImpl recordStoreImpl) {
	}

	public void init() {
		deleteStores();
	}

	public void deleteStores() {
		if (recordStores != null)
			recordStores.clear();
	}

	public int getSizeAvailable(RecordStoreImpl recordStoreImpl) {
		// FIXME returns too much
		return (int) Runtime.getRuntime().freeMemory();
	}

	public void setRecordListener(RecordListener recordListener) {
		this.recordListener = recordListener;
	}

	public void fireRecordStoreListener(int type, String recordStoreName) {
		if (recordListener != null) {
			recordListener.recordStoreEvent(type, System.currentTimeMillis(), recordStoreName);
		}
	}
}
