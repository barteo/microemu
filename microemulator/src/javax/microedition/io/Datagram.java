/*
 * @(#)Datagram.java  11/11/2001
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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public interface Datagram extends DataInput, DataOutput
{

	public String getAddress();
	
	public byte[] getData();
	
	public int getLength();
	
	public int getOffset();
	
	public void setAddress(String addr)
			throws IOException;
	
	public void setAddress(Datagram reference);
	
	public void setLength(int len);
	
	public void setData(byte[] buffer, int offset, int len);
	
	public void reset();

}
