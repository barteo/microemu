package org.microemu.app.util;

import org.microemu.log.LoggerAppender;
import org.microemu.log.LoggingEvent;

public class EventCatureLoggerAppender implements LoggerAppender {

	private LoggingEvent lastEvent; 
	
	public void append(LoggingEvent event) {
		lastEvent = event;
	}

	public void clearLastEvent() {
		lastEvent = null;
	}
	
	public LoggingEvent getLastEvent() {
		LoggingEvent ev = lastEvent;
		lastEvent = null;
		return ev;
	}

}
