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
		return null;
	}

	public RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary) throws RecordStoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public void saveChanges(RecordStoreImpl recordStoreImpl) throws RecordStoreNotOpenException, RecordStoreException {
		// TODO Auto-generated method stub

	}

	public void setRecordListener(ExtendedRecordListener recordListener) {
		// TODO Auto-generated method stub

	}

}
