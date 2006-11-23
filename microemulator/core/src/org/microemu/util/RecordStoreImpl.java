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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordComparator;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordFilter;
import javax.microedition.rms.RecordListener;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotOpenException;

import org.microemu.RecordStoreManager;



public class RecordStoreImpl extends RecordStore
{
	protected Hashtable records = new Hashtable();
	
	private String recordStoreName;
	private int version = 0;
	private long lastModified = 0;
	private int nextRecordID = 1;

	private transient boolean open;

	private transient RecordStoreManager recordStoreManager;

	private transient Vector recordListeners = new Vector();


	public RecordStoreImpl(RecordStoreManager recordStoreManager, String recordStoreName) 
	{
		this.recordStoreManager = recordStoreManager;
		if (recordStoreName.length() <= 32) {
			this.recordStoreName = recordStoreName;			
		} else {
			this.recordStoreName = recordStoreName.substring(0, 32);			
		}
		this.open = false;
	}
	
	
	public RecordStoreImpl(RecordStoreManager recordStoreManager, DataInputStream dis) 
			throws IOException
	{
		this.recordStoreManager = recordStoreManager;

		this.recordStoreName = dis.readUTF();
		this.version = dis.readInt();
		this.lastModified = dis.readLong();
		this.nextRecordID = dis.readInt();
		
		try {
			while (true) {
				int recordId = dis.readInt();
				byte[] data = new byte[dis.readInt()];
				dis.read(data, 0, data.length);
				this.records.put(new Integer(recordId), data);
			}
		} catch (EOFException ex) {			
		}
	}
	
	
	public void write(DataOutputStream dos) 
			throws IOException
	{
		dos.writeUTF(recordStoreName);
		dos.writeInt(version);
		dos.writeLong(lastModified);
		dos.writeInt(nextRecordID);
		
		Enumeration en = records.keys();
		while (en.hasMoreElements()) {
			Integer key = (Integer) en.nextElement();
			dos.writeInt(key.intValue());
			byte[] data = (byte[]) records.get(key);
			dos.writeInt(data.length);
			dos.write(data);			
		}
	}


	public boolean isOpen() 
	{
		return open;
	}


	public void setOpen(boolean open) 
	{
		this.open = open;
	}
	
	
    public void closeRecordStore() 
			throws RecordStoreNotOpenException, RecordStoreException
	{
		if (!open) {
		    throw new RecordStoreNotOpenException();
		}
		
		if (recordListeners != null) {
			recordListeners.removeAllElements();
		}	
		open = false;
	}

    
	public String getName()
			throws RecordStoreNotOpenException
	{
		if (!open) {
		    throw new RecordStoreNotOpenException();
		}

		return recordStoreName;
	}
    
	
    public int getVersion() 
			throws RecordStoreNotOpenException
	{
		if (!open) {
		    throw new RecordStoreNotOpenException();
		}
		
		synchronized (this) {
		    return version;
		}
	}


    public int getNumRecords() 
			throws RecordStoreNotOpenException
	{
		if (!open) {
		    throw new RecordStoreNotOpenException();
		}
		
		synchronized (this) {
		    return records.size();
		}
	}


    public int getSize()
			throws RecordStoreNotOpenException
	{
		if (!open) {
		    throw new RecordStoreNotOpenException();
		}
		
		// FIXME invalid size 
		return records.size();
	}


    public int getSizeAvailable()
			throws RecordStoreNotOpenException
	{
		if (!open) {
		    throw new RecordStoreNotOpenException();
		}
		
		return recordStoreManager.getSizeAvailable(this); 
	}


    public long getLastModified() 
			throws RecordStoreNotOpenException
	{
		if (!open) {
		    throw new RecordStoreNotOpenException();
		}
		
		synchronized (this) {
		    return lastModified;
		}
	}


    public void addRecordListener(RecordListener listener)
    {
        if (!recordListeners.contains(listener)) {
            recordListeners.addElement(listener);
        }
    }
	
	
    public void removeRecordListener(RecordListener listener)
    {
        recordListeners.removeElement(listener);
    }
	
	
    public int getNextRecordID() 
			throws RecordStoreNotOpenException, RecordStoreException
	{
		if (!open) {
		    throw new RecordStoreNotOpenException();
		}
		
		synchronized (this) {
		    return nextRecordID;
		}
	}


    public int addRecord(byte[] data, int offset, int numBytes)
    		throws RecordStoreNotOpenException, RecordStoreException, RecordStoreFullException
	{
		if (!open) {
		    throw new RecordStoreNotOpenException();
		}
		if (data == null && numBytes > 0) {
		    throw new NullPointerException();
		}
		if (numBytes > recordStoreManager.getSizeAvailable(this)) {
			throw new RecordStoreFullException();
		}		
		
		byte[] recordData = new byte[numBytes];
		if (data != null) {
		    System.arraycopy(data, offset, recordData, 0, numBytes);
		}
		
		int curRecordID;
		synchronized (this) {
		    records.put(new Integer(nextRecordID), recordData);
		    version++;
		    curRecordID = nextRecordID;
		    nextRecordID++;
		    lastModified = System.currentTimeMillis();
		}
		
        recordStoreManager.saveChanges(this);
		
		fireAddedRecordListener(curRecordID);
		
		return curRecordID;
	}


    public void deleteRecord(int recordId) 
			throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException
	{
		if (!open) {
		    throw new RecordStoreNotOpenException();
		}
		
		synchronized (this) {
		    if (records.remove(new Integer(recordId)) == null) {
		        throw new InvalidRecordIDException();
		    }
		    version++;
		    lastModified = System.currentTimeMillis();
		}
		
        recordStoreManager.saveChanges(this);
		
		fireDeletedRecordListener(recordId);
	}


    public int getRecordSize(int recordId) 
			throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException
	{
		if (!open) {
		    throw new RecordStoreNotOpenException();
		}
		
		synchronized (this) {
		    byte[] data = (byte[]) records.get(new Integer(recordId));
		    if (data == null) {
		        throw new InvalidRecordIDException();
		    }
		
		    return data.length;
		}
	}


    public int getRecord(int recordId, byte[] buffer, int offset)
    		throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException
	{
		int recordSize;
		synchronized (this) {
		    recordSize = getRecordSize(recordId);
		    System.arraycopy(records.get(new Integer(recordId)), 0, buffer,
		            offset, recordSize);
		}
		
		return recordSize;
	}


    public byte[] getRecord(int recordId) 
			throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException
	{
		byte[] data;
		
		synchronized (this) {
		    data = new byte[getRecordSize(recordId)];
		    getRecord(recordId, data, 0);
		}
		
		return data.length < 1 ? null : data;
	}


    public void setRecord(int recordId, byte[] newData, int offset, int numBytes)
    		throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException, RecordStoreFullException
	{
		if (!open) {
		    throw new RecordStoreNotOpenException();
		}
		
		// FIXME fixit
		if (numBytes > recordStoreManager.getSizeAvailable(this)) {
			throw new RecordStoreFullException();
		}		

		byte[] recordData = new byte[numBytes];
		System.arraycopy(newData, offset, recordData, 0, numBytes);
		
		synchronized (this) {
		    Integer id = new Integer(recordId);
		    if (records.remove(id) == null) {
		        throw new InvalidRecordIDException();
		    }
		    records.put(id, recordData);
		    version++;
		    lastModified = System.currentTimeMillis();
		}
		
        recordStoreManager.saveChanges(this);
		
		fireChangedRecordListener(recordId);
	}


    public RecordEnumeration enumerateRecords(RecordFilter filter, RecordComparator comparator, boolean keepUpdated)
    		throws RecordStoreNotOpenException
	{
		if (!open) {
		    throw new RecordStoreNotOpenException();
		}
		
		return new RecordEnumerationImpl(this, filter, comparator, keepUpdated);
	}
    
    
    public int getHeaderSize() 
    {
    	// TODO fixit
    	return recordStoreName.length() + 4 + 8 + 4;
    }
    
    
    public int getRecordHeaderSize()
    {
    	return 4 + 4;
    }


    private void fireAddedRecordListener(int recordId)
    {
    	if (recordListeners != null) { 
	        for (Enumeration e = recordListeners.elements(); e.hasMoreElements();) {
	            ((RecordListener) e.nextElement()).recordAdded(this, recordId);
	        }
    	}
    }

  
    private void fireChangedRecordListener(int recordId)
    {
    	if (recordListeners != null) {
	        for (Enumeration e = recordListeners.elements(); e.hasMoreElements();) {
	            ((RecordListener) e.nextElement()).recordChanged(this, recordId);
	        }
    	}
    }

  
    private void fireDeletedRecordListener(int recordId)
    {
    	if (recordListeners != null) {
	        for (Enumeration e = recordListeners.elements(); e.hasMoreElements();) {
	            ((RecordListener) e.nextElement()).recordDeleted(this, recordId);
	        }
    	}
    }
          
}
