/**
 *  MicroEmulator
 *  Copyright (C) 2001-2007 MicroEmulator Team.
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

/**
 * @author vlads
 *
 */
public class LoggingEvent {

	public final static int DEBUG = 1;

	public final static int INFO = 2;

	public final static int WARN = 3;

	public final static int ERROR = 4;
	
	protected int level;

	protected String message;
    
	protected StackTraceElement location; 

	protected boolean hasData = false;

	protected Object data;

	protected Throwable throwable;

	protected long eventTime;


    public LoggingEvent() {
    	this.eventTime = System.currentTimeMillis();
    }
    
	public LoggingEvent(int level, String message, StackTraceElement location, Throwable throwable) {
		this();
		this.level = level;
		this.message = message;
		this.location = location;
		this.throwable = throwable;
	}
	
	public LoggingEvent(int level, String message, StackTraceElement location, Throwable throwable, Object data) {
		this(level, message, location, throwable);
		setData(data);
	}

	public Object getData() {
		return this.data;
	}

	public void setData(Object data) {
		this.data = data;
		this.hasData = true;
	}

	public boolean hasData() {
		return this.hasData;
	}

	public String getFormatedData() {
		if (hasData()) {
			if (getData() == null) {
				return "{null}";
			} else {
				return getData().toString();
			}
		} else {
			return "";
		}
	}
	
	public long getEventTime() {
		return this.eventTime;
	}

	public int getLevel() {
		return this.level;
	}

	public StackTraceElement getLocation() {
		return this.location;
	}

	public String getMessage() {
		return this.message;
	}

	public Throwable getThrowable() {
		return this.throwable;
	}

}
