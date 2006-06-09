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

package org.microemu.app.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Hashtable;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import org.microemu.RecordStoreManager;
import org.microemu.app.Config;
import org.microemu.app.launcher.Launcher;
import org.microemu.util.RecordStoreImpl;



public class FileRecordStoreManager implements RecordStoreManager 
{
	private final static String RECORD_STORE_SUFFIX = ".rs";
	
	private Launcher launcher;
	
	private Hashtable testOpenRecordStores = new Hashtable();

	private FilenameFilter filter = new FilenameFilter()
	{

		public boolean accept(File dir, String name) 
		{
			if (name.endsWith(RECORD_STORE_SUFFIX)) {
				return true;
			} else {
				return false;
			}
		}
		
	};
	

	public FileRecordStoreManager(Launcher launcher) 
	{
		this.launcher = launcher;
	}


	public void deleteRecordStore(String recordStoreName) 
			throws RecordStoreNotFoundException, RecordStoreException 
	{
		File suiteFolder = new File(Config.getConfigPath(), "suite-" + launcher.getSuiteName());
		File storeFile = new File(suiteFolder, recordStoreName + RECORD_STORE_SUFFIX);

		RecordStoreImpl recordStoreImpl = (RecordStoreImpl) testOpenRecordStores.get(storeFile.getName());
		if (recordStoreImpl != null && recordStoreImpl.isOpen()) {
			throw new RecordStoreException();
		}
		
		try {
			recordStoreImpl = loadFromDisk(storeFile);
		} catch (FileNotFoundException ex) {
			throw new RecordStoreNotFoundException();
		}
		
		storeFile.delete();
	}


	public RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary) 
			throws RecordStoreNotFoundException 
	{
		File suiteFolder = new File(Config.getConfigPath(), "suite-" + launcher.getSuiteName());
		File storeFile = new File(suiteFolder, recordStoreName + RECORD_STORE_SUFFIX);
						
        RecordStoreImpl recordStoreImpl;
		try {
			recordStoreImpl = loadFromDisk(storeFile);
			recordStoreImpl.setRecordStoreManager(this);
		} catch (FileNotFoundException e) {
            if (!createIfNecessary) { 
                throw new RecordStoreNotFoundException(); 
            }
            recordStoreImpl = new RecordStoreImpl(this, recordStoreName);
            suiteFolder.mkdirs();
            saveToDisk(storeFile, recordStoreImpl);
		}
        recordStoreImpl.setOpen(true);

        testOpenRecordStores.put(storeFile.getName(), recordStoreImpl);
        
        return recordStoreImpl;
	}


	public String[] listRecordStores() 
	{
		File suiteFolder = new File(Config.getConfigPath(), "suite-" + launcher.getSuiteName());
		String[] result = suiteFolder.list(filter);
		
		if (result != null) {
			for (int i = 0; i < result.length; i++) {
				result[i] = result[i].substring(0, result[i].length() - RECORD_STORE_SUFFIX.length());
			}
		}
		
		return result;
	}


	public void saveChanges(RecordStoreImpl recordStoreImpl) 
			throws RecordStoreNotOpenException 
	{
		File suiteFolder = new File(Config.getConfigPath(), "suite-" + launcher.getSuiteName());
		File storeFile = new File(suiteFolder, recordStoreImpl.getName() + RECORD_STORE_SUFFIX);

		saveToDisk(storeFile, recordStoreImpl);
	}
	
	public void init()
	{
	}

    public void deleteStores()
    {
        String[] stores = listRecordStores();
        for (int i = 0; i < stores.length; i++)
        {
            String store = stores[i];
            try
            {
                deleteRecordStore(store);
            }
            catch (RecordStoreException e)
            {
                e.printStackTrace();
            }
        }
    }


    private RecordStoreImpl loadFromDisk(File recordStoreFile)
            throws FileNotFoundException
    {
        RecordStoreImpl store = null;

        try {
            DataInputStream dis = new DataInputStream(new FileInputStream(recordStoreFile));
            store = new RecordStoreImpl(dis);
            dis.close();
        } catch (FileNotFoundException ex) {
            throw ex;
        } catch (IOException ex) {
            System.out.println("RecordStore.loadFromDisk: ERROR reading " + recordStoreFile.getName());
            ex.printStackTrace();
        }

        return store;
    }
  
    
    private static void saveToDisk(File recordStoreFile, RecordStoreImpl recordStore) 
    {
        try {
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(recordStoreFile, false));
            recordStore.write(dos);
            dos.close();
        } catch (IOException ex) {
            System.out.println("RecordStore.saveToDisk: ERROR writting object to " + recordStoreFile.getName());
            ex.printStackTrace();
        }
    }

}
