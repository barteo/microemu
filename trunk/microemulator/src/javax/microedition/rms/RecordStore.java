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


public class RecordStore 
{

	
	public static void deleteRecordStore(String recordStoreName)
	{
	}
	
	
	public static RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary)
	{
		return null;
	}
	
	
	public void closeRecordStore()
	{
	}
	
	
	public static String[] listRecordStores()
	{
		return null;
	}
	
	
	public String getName()
	{
		return "this";
	}
	
	
	public int getVersion()
	{
		return 0;
	}


	public int getNumRecords()
	{
		return 0;
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
	}
	
	
	public void removeRecordListener(RecordListener listener)
	{
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

