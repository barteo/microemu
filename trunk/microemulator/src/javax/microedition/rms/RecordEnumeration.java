/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@it.pl>
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
 
package javax.microedition.rms;


public interface RecordEnumeration
{

  public int numRecords();
  
  public byte[] nextRecord()
      throws InvalidRecordIDException, RecordStoreNotOpenException, RecordStoreException;
  
  public int nextRecordId()
      throws InvalidRecordIDException;
  
  public byte[] previousRecord()
      throws InvalidRecordIDException, RecordStoreNotOpenException, RecordStoreException;
  
  public int previousRecordId()
      throws InvalidRecordIDException;
  
  public boolean hasNextElement();
  
  public boolean hasPreviousElement();
  
  public void reset();
  
  public void rebuild();
  
  public void keepUpdated(boolean keepUpdated);
  
  public boolean isKeptUpdated();
  
  public void destroy();

}

