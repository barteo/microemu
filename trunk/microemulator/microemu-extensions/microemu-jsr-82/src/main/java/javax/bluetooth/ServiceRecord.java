/*
 *  MicroEmulator
 *  Copyright (C) 2006 Bartek Teodorczyk <barteo@barteo.net>
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

package javax.bluetooth;

import java.io.IOException;

public interface ServiceRecord
{
    
    public static final int NOAUTHENTICATE_NOENCRYPT = 0x00;
    public static final int AUTHENTICATE_NOENCRYPT = 0x01;
    public static final int AUTHENTICATE_ENCRYPT = 0x02;
    
    
    public DataElement getAttributeValue(int attrID);

    public RemoteDevice getHostDevice();
    
    public int[] getAttributeIDs();
    
    public boolean populateRecord(int[] attrIDs) throws IOException;
    
    public String getConnectionURL(int requiredSecurity, boolean mustBeMaster);
    
    public void setDeviceServiceClasses(int classes);
    
    public boolean setAttributeValue(int attrID, DataElement attrValue);
    
}
