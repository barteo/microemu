package org.microemu.app.util;

import org.microemu.Injected;
import org.microemu.log.Logger;
import org.microemu.log.LoggingEvent;

import junit.framework.TestCase;

public class MIDletOutputStreamRedirectorTest extends TestCase {

	EventCatureLoggerAppender capture;
	
	protected void setUp() throws Exception {
		super.setUp();
		capture = new EventCatureLoggerAppender();
		Logger.addAppender(capture);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		Logger.removeAppender(capture);
	}
	
	private void verify(LoggingEvent event, StackTraceElement b4call, final String message) {
		assertNotNull("got event", event);
		assertEquals(message, event.getMessage());
		StackTraceElement ste = event.getLocation();
		assertEquals("MethodName", b4call.getMethodName(), ste.getMethodName());
		assertEquals("ClassName", b4call.getClassName(), ste.getClassName());
		assertEquals("LineNumber", b4call.getLineNumber() + 1, ste.getLineNumber());
	}
	
	public void testRedirectStack() throws Exception {
		final String message = "Test text";
		capture.clearLastEvent();
		
		StackTraceElement b4call = new Throwable().getStackTrace()[0];
		Injected.out.println(message);
		
		verify(capture.getLastEvent(), b4call, message);
		
		b4call = new Throwable().getStackTrace()[0];
		Injected.out.print(message + "\n");
		
		verify(capture.getLastEvent(), b4call, message);
	}
	
	private class MIDletInternlaLogger {
		
		public void debug(String message) {
			Injected.out.println(message);
		}
		
		public void error(String message) {
			Injected.out.println(message);
		}
		
		public void log(String message) {
			Injected.out.println(message);	
		}
	}
	
	public void testMIDletInternlaLogger() throws Exception {
		final String message = "Test text";
		capture.clearLastEvent();
		MIDletInternlaLogger log = new MIDletInternlaLogger();
		
		StackTraceElement b4call = new Throwable().getStackTrace()[0];
		log.debug(message);
		
		verify(capture.getLastEvent(), b4call, message);
		
		b4call = new Throwable().getStackTrace()[0];
		log.error(message);
		
		verify(capture.getLastEvent(), b4call, message);
		
		b4call = new Throwable().getStackTrace()[0];
		log.log(message);
		
		verify(capture.getLastEvent(), b4call, message);
	}
}

