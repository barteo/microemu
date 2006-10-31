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

import javax.microedition.io.Connection;

public class RemoteDevice
{
    
    protected RemoteDevice(String address)
    {        
		try {
			throw new RuntimeException("Not implemented");
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
    }
    
    
    public boolean isTrustedDevice()
    {        
		try {
			throw new RuntimeException("Not implemented");
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
    }
    
    
    public String getFriendlyName(boolean alwaysAsk)
            throws IOException
    {        
		try {
			throw new RuntimeException("Not implemented");
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
    }
    
    
    public final String getBluetoothAddress()
    {
		try {
			throw new RuntimeException("Not implemented");
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
    }
    
    
    public boolean equals(Object obj)
    {
		try {
			throw new RuntimeException("Not implemented");
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
    }
    
    
    public int hashCode()
    {
		try {
			throw new RuntimeException("Not implemented");
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
    }
    
    
    public static RemoteDevice getRemoteDevice(Connection conn)
            throws IOException
    {
		try {
			throw new RuntimeException("Not implemented");
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
    }
    
    
    public boolean authenticate()
            throws IOException
    {
		try {
			throw new RuntimeException("Not implemented");
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
    }
    

    public boolean authorize(Connection conn)
            throws IOException
    {
		try {
			throw new RuntimeException("Not implemented");
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
    }
    
    
    public boolean encrypt(Connection conn, boolean on)
            throws IOException
    {
		try {
			throw new RuntimeException("Not implemented");
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
    }

    
    public boolean isAuthenticated()
    {
		try {
			throw new RuntimeException("Not implemented");
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
    }

    
    public boolean isAuthorized(Connection conn)
            throws IOException
    {
		try {
			throw new RuntimeException("Not implemented");
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
    }

    
    public boolean isEncrypted()
    {
		try {
			throw new RuntimeException("Not implemented");
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
    }

}
