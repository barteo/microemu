/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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
 
package javax.microedition.io;

import java.io.IOException;


public interface DatagramConnection extends Connection
{

	int getMaximumLength()
			throws IOException;

	int getNominalLength()
      throws IOException;
      
	void send(Datagram dgram)
      throws IOException;
      
	void receive(Datagram dgram)
      throws IOException;
  
	Datagram newDatagram(int size)
      throws IOException;

	Datagram newDatagram(int size, String addr)
      throws IOException;

	Datagram newDatagram(byte[] buf, int size)
      throws IOException;
	
	Datagram newDatagram(byte[] buf, int size, String addr)
      throws IOException;
      
}
