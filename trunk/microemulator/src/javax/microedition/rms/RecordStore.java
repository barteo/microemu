/*
 * @(#)RecordStore.java  07/07/2001
 *
 * Copyright (c) 2001 Bartek Teodorczyk <barteo@it.pl>. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package javax.microedition.rms;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


public class RecordStore 
{

  private static Hashtable recordStores = new Hashtable();
  
  private String name;
  private boolean open = false;
  private Vector recordListeners = new Vector();
  private Hashtable records = new Hashtable();
  
	
  private RecordStore(String name)
  {
    this.name = name;
  }
  
  
  public static void deleteRecordStore(String recordStoreName)
      throws RecordStoreException, RecordStoreNotFoundException
	{
    RecordStore recordStore = (RecordStore) recordStores.get(recordStoreName);
    if (recordStore == null) {
      throw new RecordStoreNotFoundException();
    }
    if (recordStore.open) {
      throw new RecordStoreException();
    }
    
    recordStores.remove(recordStoreName);
	}
	
	
	public static RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary)
      throws RecordStoreException, RecordStoreFullException, RecordStoreNotFoundException
	{
    RecordStore recordStore = (RecordStore) recordStores.get(recordStoreName);
    if (recordStore == null) {
      if (!createIfNecessary) {
        throw new RecordStoreNotFoundException();
      }
      recordStore = new RecordStore(recordStoreName);
      recordStore.open = true;
      recordStores.put(recordStoreName, recordStore);
    }
    
		return recordStore;
	}
	
	
	public void closeRecordStore()
      throws RecordStoreNotOpenException, RecordStoreException
	{
    if (!open) {
      throw new RecordStoreNotOpenException();
    }
    
    recordListeners.removeAllElements();
    open = false;
	}
	
	
	public static String[] listRecordStores()
	{
    String[] result = new String[recordStores.size()];
    
    int i = 0;
    for (Enumeration e = recordStores.keys(); e.hasMoreElements(); ) {
      result[i] = (String) e.nextElement();
      i++;
    }
    
		return result;
	}
	
	
	public String getName()
	{
		return name;
	}
	
	
	public int getVersion()
	{
		return 0;
	}


	public int getNumRecords()
      throws RecordStoreNotOpenException
	{
    if (!open) {
      throw new RecordStoreNotOpenException();
    }
    
		return records.size();
	}

	
	public int getSizeAvailable()
	{
		return 0;
	}

	
	public long getLastModified()
	{
		return 0;
	}


	public void addRecordListener(RecordListener listener)
	{
    if (!recordListeners.contains(listener)) {
      recordListeners.add(listener);
    }
	}
	
	
	public void removeRecordListener(RecordListener listener)
	{
    recordListeners.remove(listener);
	}
	
	
	public int getNextRecordID()
	{
		return 0;
	}
	
	
	public int addRecord(byte[] data, int offset, int numBytes)
	{
		return 0;
	}
	
	
	public void deleteRecord(int recordId)
	{
	}
	
	
	public int getRecordSize(int recordId)
	{
		return 0;
	}
	
	
	public int getRecord(int recordId, byte[] buffer, int offset)
	{
		return 0;
	}
	
	
	public byte[] getRecord(int recordId)
	{
		return null;
	}
	
	
	public void setRecord(int recordId, byte[] newData, int offset, int numBytes)
			throws RecordStoreException
	{
	}
	
	
	public RecordEnumeration enumerateRecords(RecordFilter filter, RecordComparator comparator, boolean keepUpdated)
	{
		return null;
	}
	
}

