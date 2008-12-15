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
package org.microemu.app.ui;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.microemu.log.Logger;

/**
 * 
 * This class is used to Show error message to user
 * 
 * @author vlads
 *
 */
public class Message {

	public static final int ERROR = 0;

    public static final int INFO = 1;

    public static final int WARN = 2;
    
    private static List listeners = new Vector();
    
    static {
    	Logger.addLogOrigin(Message.class);
    }
    
    /**
     * Show Error message to user
     * 
     * @param title Dialog title
     * @param text  Message
     */
    public static void error(String title, String text) {
        Logger.error("Message: " + title + ": " + text);
        callListeners(ERROR, title, text, null);
    }

    /**
     * Show Error message to user
     * 
     * @param text  Message
     */
    public static void error(String text) {
        Logger.error("Message: Error: " + text);
        callListeners(ERROR, "Error", text, null);
    }
    
    /**
     * Show Error message to user
     * 
     * @param title Dialog title
     * @param text  Message
     */
    public static void error(String title, String text, Throwable throwable) {
        Logger.error("Message: " + title + ": " + text, throwable);
        callListeners(ERROR, title, text, throwable);
    }

    public static void error(String text, Throwable throwable) {
        Logger.error("Message: Error : " + text, throwable);
        callListeners(ERROR, "Error", text, throwable);
    }
    
    /**
     * Show info message to user
     * 
     * @param text  Message
     */
    public static void info(String text) {
        Logger.info("Message: info: " + text);
        callListeners(INFO, "Info", text, null);
    }

    /**
     * Show info message to user
     * 
     * @param text  Message
     */
    public static void warn(String text) {
        Logger.warn("Message: warn: " + text);
        callListeners(INFO, "Warning", text, null);
    }
    
    /**
     * Here we can butify error message text.
     * @param throwable
     * @return
     */
    public static String getCauseMessage(Throwable throwable) {
		if (throwable.getCause() == null) {
			return throwable.toString();
		} else {
			return getCauseMessage(throwable.getCause());
		}
    }
    
    private static void callListeners(int level, String title, String text, Throwable throwable) {
		for (Iterator iter = listeners.iterator(); iter.hasNext();) {
			MessageListener a = (MessageListener) iter.next();
			a.showMessage(level, title, text, throwable);
		};
	}
	
	public static void addListener(MessageListener newListener) {
		listeners.add(newListener);
	}
}
