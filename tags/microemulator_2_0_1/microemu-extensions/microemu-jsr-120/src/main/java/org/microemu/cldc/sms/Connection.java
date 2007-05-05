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

package org.microemu.cldc.sms;

import java.io.IOException;
import java.io.InterruptedIOException;

import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;

import org.microemu.cldc.ClosedConnection;

public class Connection implements MessageConnection, ClosedConnection {

	public javax.microedition.io.Connection open(String name) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public Message newMessage(String type) {
		// TODO Auto-generated method stub
		return null;
	}

	public Message newMessage(String type, String address) {
		// TODO Auto-generated method stub
		return null;
	}

	public int numberOfSegments(Message message) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Message receive() throws IOException, InterruptedIOException {
		// TODO Auto-generated method stub
		return null;
	}

	public void send(Message message) throws IOException,
			InterruptedIOException {
		// TODO Auto-generated method stub

	}

	public void setMessageListener(MessageListener listener) throws IOException {
		// TODO Auto-generated method stub

	}

	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

}
