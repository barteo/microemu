/*
 *  MicroEmulator
 *  Copyright (C) 2001-2005 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordComparator;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordFilter;
import javax.microedition.rms.RecordListener;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;


public class RecordEnumerationImpl implements RecordEnumeration
{
	private RecordStoreImpl recordStoreImpl;
	private RecordFilter filter;
	private RecordComparator comparator;
	private boolean keepUpdated;

	private Vector enumerationRecords = new Vector();
	private int currentRecord;    
    
	private RecordListener recordListener = new RecordListener() 
    {
		public void recordEvent(int type, long timestamp, RecordStore recordStore, int recordId) {
			if (type == RecordListener.RECORD_ADD || type == RecordListener.RECORD_CHANGE || type == RecordListener.RECORD_DELETE) {
				rebuild();
			}
		}

		public void recordStoreEvent(int type, long timestamp, String recordStoreName) {
		}
    };


    public RecordEnumerationImpl(RecordStoreImpl recordStoreImpl, RecordFilter filter, RecordComparator comparator, boolean keepUpdated)
    {
    	this.recordStoreImpl = recordStoreImpl;
        this.filter = filter;
        this.comparator = comparator;
        this.keepUpdated = keepUpdated;

        rebuild();

        if (keepUpdated) {
        	recordStoreImpl.addRecordListener(recordListener);
        }
    }


    public int numRecords()
    {
        return enumerationRecords.size();
    }


    public byte[] nextRecord() 
    		throws InvalidRecordIDException, RecordStoreNotOpenException, RecordStoreException
    {
        if (!recordStoreImpl.isOpen()) {
            throw new RecordStoreNotOpenException();
        }

        if (currentRecord >= numRecords()) {
            throw new InvalidRecordIDException();
        }

        byte[] result = ((EnumerationRecord) enumerationRecords
                .elementAt(currentRecord)).value;
        currentRecord++;

        return result;
    }


    public int nextRecordId() 
    		throws InvalidRecordIDException
    {
        if (currentRecord >= numRecords()) {
            throw new InvalidRecordIDException();
        }

        int result = ((EnumerationRecord) enumerationRecords
                .elementAt(currentRecord)).recordId;
        currentRecord++;

        return result;
    }


    public byte[] previousRecord() 
    		throws InvalidRecordIDException, RecordStoreNotOpenException, RecordStoreException
    {
        if (!recordStoreImpl.isOpen()) {
            throw new RecordStoreNotOpenException();
        }
        if (currentRecord < 0) {
            throw new InvalidRecordIDException();
        }

        byte[] result = ((EnumerationRecord) enumerationRecords
                .elementAt(currentRecord)).value;
        currentRecord--;

        return result;
    }


    public int previousRecordId() 
    		throws InvalidRecordIDException
    {
        if (currentRecord < 0) {
            throw new InvalidRecordIDException();
        }

        int result = ((EnumerationRecord) enumerationRecords
                .elementAt(currentRecord)).recordId;
        currentRecord--;

        return result;
    }


    public boolean hasNextElement()
    {
        if (currentRecord == numRecords()) {
            return false;
        } else {
            return true;
        }
    }


    public boolean hasPreviousElement()
    {
        if (currentRecord == 0) {
            return false;
        } else {
            return true;
        }
    }


    public void reset()
    {
        currentRecord = 0;
    }


    public void rebuild()
    {
        enumerationRecords.removeAllElements();

        //
        // filter
        //
        for (Enumeration e = recordStoreImpl.records.keys(); e.hasMoreElements();) {
            Object key = e.nextElement();
            byte[] data = (byte[]) recordStoreImpl.records.get(key);
            if (filter != null && !filter.matches(data)) {
                continue;
            }
            enumerationRecords.add(new EnumerationRecord(	((Integer) key).intValue(),
															data));
        }

        // 
        // sort
        //
		if (comparator != null) {
			Collections.sort(enumerationRecords, new Comparator() {

				public int compare(Object lhs, Object rhs) {

					int compare = comparator.compare(((EnumerationRecord) lhs).value,
					                                 ((EnumerationRecord) rhs).value);
					if (compare == RecordComparator.EQUIVALENT)
						return 0;
					else if (compare == RecordComparator.FOLLOWS)
						return 1;
					else
						return -1;

				}

			});
		}
    }


    public void keepUpdated(boolean keepUpdated)
    {
        if (keepUpdated) {
            if (!this.keepUpdated) {
                rebuild();
                recordStoreImpl.addRecordListener(recordListener);
            }
        } else {
        	recordStoreImpl.removeRecordListener(recordListener);
        }

        this.keepUpdated = keepUpdated;
    }


    public boolean isKeptUpdated()
    {
        return keepUpdated;
    }


    public void destroy()
    {
    }


    class EnumerationRecord
    {
        int recordId;

        byte[] value;

        
        EnumerationRecord(int recordId, byte[] value)
        {
            this.recordId = recordId;
            this.value = value;
        }
    }    

}
