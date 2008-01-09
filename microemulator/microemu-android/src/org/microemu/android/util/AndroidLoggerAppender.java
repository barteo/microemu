package org.microemu.android.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.microemu.android.MicroEmulator;
import org.microemu.log.LoggerAppender;
import org.microemu.log.LoggingEvent;

import android.util.Log;

public class AndroidLoggerAppender implements LoggerAppender {

	public static String formatLocation(StackTraceElement ste) {
		if (ste == null) {
			return "";
		}
		// Make Line# clickable in eclipse
		return ste.getClassName() + "." + ste.getMethodName() + "(" + ste.getFileName() + ":" + ste.getLineNumber()
				+ ")";
	}

	public void append(LoggingEvent event) {
    	String data = "";
    	if (event.hasData()) {
    		data = " [" + event.getFormatedData() + "]";
    	}
		Log.v(MicroEmulator.LOG_TAG, event.getMessage() + data +  "\n\t  " + formatLocation(event.getLocation()));
    	if (event.getThrowable() != null) {
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		event.getThrowable().printStackTrace(new PrintStream(baos));
    		Log.v(MicroEmulator.LOG_TAG, baos.toString());
    	}

	}

}
