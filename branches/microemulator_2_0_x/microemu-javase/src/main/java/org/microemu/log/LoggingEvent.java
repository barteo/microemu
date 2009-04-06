/**
 *  MicroEmulator
 *  Copyright (C) 2001-2007 Bartek Teodorczyk <barteo@barteo.net>
 *  Copyright (C) 2006-2007 Vlad Skarzhevskyy
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
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
