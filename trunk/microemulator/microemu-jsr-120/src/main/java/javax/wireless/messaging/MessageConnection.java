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

package javax.wireless.messaging;

import java.io.IOException;
import java.io.InterruptedIOException;

import javax.microedition.io.Connection;

public interface MessageConnection extends Connection {

	public static final java.lang.String BINARY_MESSAGE = "binary";
	public static final java.lang.String TEXT_MESSAGE = "text";
	
	public Message newMessage(String type);
	
	public Message newMessage(String type, String address);
	
	public int numberOfSegments(Message message);
	
	public Message receive() throws IOException, InterruptedIOException;
	
	public void send(Message message) throws IOException, InterruptedIOException;
	
	public void setMessageListener(MessageListener listener) throws IOException;
}
