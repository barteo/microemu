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

package org.microemu.cldc.datagram;

import java.io.IOException;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;


public class Connection implements DatagramConnection
{
  static final String NOT_IMPLEMENTED = "Not implemented";

      
	public void close()
			throws IOException
  {
    throw new IOException(NOT_IMPLEMENTED);
  }

  
  public int getMaximumLength()
			throws IOException
  {
    throw new IOException(NOT_IMPLEMENTED);
  }

  
	public int getNominalLength()
      throws IOException
  {
    throw new IOException(NOT_IMPLEMENTED);
  }
      
  
	public void send(Datagram dgram)
      throws IOException
  {
    throw new IOException(NOT_IMPLEMENTED);
  }

      
	public void receive(Datagram dgram)
      throws IOException
  {
    throw new IOException(NOT_IMPLEMENTED);
  }
  
  
	public Datagram newDatagram(int size)
      throws IOException
  {
    throw new IOException(NOT_IMPLEMENTED);
  }
      

	public Datagram newDatagram(int size, String addr)
      throws IOException
  {
    throw new IOException(NOT_IMPLEMENTED);
  }
      

	public Datagram newDatagram(byte[] buf, int size)
      throws IOException
  {
    throw new IOException(NOT_IMPLEMENTED);
  }
      
	
	public Datagram newDatagram(byte[] buf, int size, String addr)
      throws IOException
  {
    throw new IOException(NOT_IMPLEMENTED);
  }
        
}
