/**
 *  MicroEmulator
 *  Copyright (C) 2008 Markus Heberling <markus@heberling.net>
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
package org.microemu.iphone.device;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import org.microemu.MicroEmulator;
import org.microemu.RecordStoreManager;
import org.microemu.util.ExtendedRecordListener;
import org.microemu.util.RecordStoreImpl;

public class IPhoneRecordStoreManager implements RecordStoreManager {

	public IPhoneRecordStoreManager(org.microemu.iphone.MicroEmulator microEmulator) {
		// TODO Auto-generated constructor stub
	}

	public void deleteRecordStore(String recordStoreName) throws RecordStoreNotFoundException, RecordStoreException {
		// TODO Auto-generated method stub

	}

	public void deleteStores() {
		// TODO Auto-generated method stub

	}

	public void fireRecordStoreListener(int type, String recordStoreName) {
		// TODO Auto-generated method stub

	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getSizeAvailable(RecordStoreImpl recordStoreImpl) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void init(MicroEmulator emulator) {
		// TODO Auto-generated method stub

	}

	public String[] listRecordStores() {
		// TODO Auto-generated method stub
		return new String[]{};
	}

	public RecordStore openRecordStore(final String recordStoreName, boolean createIfNecessary) throws RecordStoreException {
		// TODO Auto-generated method stub
		RecordStoreImpl store=new RecordStoreImpl(this, recordStoreName);
		store.setOpen(true);
		return store;
	}

	public void saveChanges(RecordStoreImpl recordStoreImpl) throws RecordStoreNotOpenException, RecordStoreException {
		// TODO Auto-generated method stub

	}

	public void setRecordListener(ExtendedRecordListener recordListener) {
		// TODO Auto-generated method stub

	}

}
