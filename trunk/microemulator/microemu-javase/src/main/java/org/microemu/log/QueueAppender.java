/**
 *  MicroEmulator
 *  Copyright (C) 2001-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
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
 *
 *  @version $Id$
 */
package org.microemu.log;

import java.util.LinkedList;
import java.util.List;

public class QueueAppender implements LoggerAppender {

	private int buferSize;

	private List queue = new LinkedList();

	public QueueAppender(int buferSize) {
		this.buferSize = buferSize;
	}

	public void append(LoggingEvent event) {
		queue.add(event);
		if (queue.size() > buferSize) {
			queue.remove(0);
		}
	}

	public LoggingEvent poll() {
		if (queue.size() == 0) {
			return null;
		}
		return (LoggingEvent) queue.remove(0);
	}

}
