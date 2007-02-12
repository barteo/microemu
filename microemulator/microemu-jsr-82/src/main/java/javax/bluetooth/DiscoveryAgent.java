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

public class DiscoveryAgent
{
    public static final int NOT_DISCOVERABLE = 0x00;
    
    public static final int GIAC = 0x9E8B33;
    public static final int LIAC = 0x9E8B00;
    
    public static final int CACHED = 0x00;
    public static final int PREKNOWN = 0x01;

    
    public RemoteDevice[] retrieveDevices(int option)
    {
		try {
			throw new RuntimeException("Not implemented");
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
    }
    
    
    public boolean startInquiry(int accessCode, DiscoveryListener listener)
            throws BluetoothStateException
    {
		try {
			throw new RuntimeException("Not implemented");
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
    }
    
    
    public boolean cancelInquiry(DiscoveryListener listener)
    {
		try {
			throw new RuntimeException("Not implemented");
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
    }
    
    
    public int searchServices(int[] attrSet, UUID[] uuidSet, RemoteDevice btDev, DiscoveryListener listener)
            throws BluetoothStateException
    {
		try {
			throw new RuntimeException("Not implemented");
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
    }
    
    
    public boolean cancelServiceSearch(int transID)
    {
		try {
			throw new RuntimeException("Not implemented");
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
    }
    
    
    public java.lang.String selectService(UUID uuid, int security, boolean master)
            throws BluetoothStateException
    {
		try {
			throw new RuntimeException("Not implemented");
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
    }
    
}
