/*
 * @(#)DatagramConnection.java  11/11/2001
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

package javax.microedition.io;

import java.io.IOException;


public interface DatagramConnection extends Connection
{

	public int getMaximumLength()
			throws IOException;

	public int getNominalLength()
      throws IOException;
      
	public void send(Datagram dgram)
      throws IOException;
      
  public void receive(Datagram dgram)
      throws IOException;
  
	public Datagram newDatagram(int size)
      throws IOException;

	public Datagram newDatagram(int size, String addr)
      throws IOException;

	public Datagram newDatagram(byte[] buf, int size)
      throws IOException;
	
	public Datagram newDatagram(byte[] buf, int size, String addr)
      throws IOException;
      
}
