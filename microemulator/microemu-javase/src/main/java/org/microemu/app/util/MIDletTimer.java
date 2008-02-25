/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
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
package org.microemu.app.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.WeakHashMap;

import org.microemu.MIDletBridge;
import org.microemu.MIDletContext;
import org.microemu.log.Logger;

/**
 * Terminate all timers on MIDlet exit.
 * TODO Name all the timer Threads created by MIDlet in Java 5  
 * @author vlads
 */
public class MIDletTimer extends Timer {

	private static Map midlets = new WeakHashMap();
	
	private class OneTimeTimerTaskWrapper extends TimerTask {

		TimerTask task;
		
		OneTimeTimerTaskWrapper(TimerTask task) {
			this.task = task;
		}
		
		public void run() {
			unregister(MIDletTimer.this);
			task.run();
		}
		
	}
	
	private String name;
	
	private MIDletContext midletContext;
	
	public MIDletTimer() {
		super();
		StackTraceElement[] ste = new Throwable().getStackTrace();
		name = ste[1].getClassName() + "." + ste[1].getMethodName(); 
	}
	
	public void schedule(TimerTask task, Date time) {
		register(this);
		super.schedule(new OneTimeTimerTaskWrapper(task), time);
	}
	
	public void schedule(TimerTask task, Date firstTime, long period) {
		register(this);
		super.schedule(task, firstTime, period);
	}
	
	public void schedule(TimerTask task, long delay) {
		register(this);
		super.schedule(new OneTimeTimerTaskWrapper(task), delay);
	}
	
	public void schedule(TimerTask task, long delay, long period) {
		register(this);
		super.schedule(task, delay, period);
	}
	
	public void scheduleAtFixedRate(TimerTask task, Date firstTime, long period) {
		register(this);
		super.schedule(task, firstTime, period);
	}
	
	public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
		register(this);
		super.scheduleAtFixedRate(task, delay, period);
	}
	
	public void cancel() {
    	unregister(this);
		super.cancel();
	}

	private void terminate() {
		super.cancel();
	}
	
	private static void register(MIDletTimer timer) {
		if (timer.midletContext == null) {
			timer.midletContext = MIDletBridge.getMIDletContext();
		}
		if (timer.midletContext == null) {
			Logger.error("Creating Timer with no MIDlet context", new Throwable());
			return;
		}
		Map timers = (Map)midlets.get(timer.midletContext);
		if (timers == null) {
			// Can't use WeakHashMap Timers are disposed by JVM
			timers = new HashMap();
			midlets.put(timer.midletContext, timers);
		}
		//Logger.debug("Register timer created from [" + timer.name + "]");
		timers.put(timer, timer.midletContext);
	}
	
	private static void unregister(MIDletTimer timer) {
		if (timer.midletContext == null) {
			Logger.error("Timer with no MIDlet context", new Throwable());
			return;
		}
		Map timers = (Map)midlets.get(timer.midletContext);
		if (timers == null) {
			return;
		}
		//Logger.debug("Unregister timer created from [" + timer.name + "]");
		timers.remove(timer);
	}
	
	/**
	 * Termnate all Threads created by MIDlet
	 */
	public static void contextDestroyed(MIDletContext midletContext) {
		if (midletContext == null) {
			return;
		}
		Map timers = (Map)midlets.get(midletContext);
		if (timers != null) {
			terminateTimers(timers);
			midlets.remove(midletContext);
		}
	}
	
	private static void terminateTimers(Map timers) {
		for (Iterator iter = timers.keySet().iterator(); iter.hasNext();) {
			Object o = iter.next();
			if (o == null) {
				continue;
			}
			if (o instanceof MIDletTimer) {
				MIDletTimer tm = (MIDletTimer)o;
				Logger.warn("MIDlet timer created from [" + tm.name + "] still running");
				tm.terminate();
			} else {
				Logger.debug("unrecognized Object [" + o.getClass().getName() + "]");
			}
		};
	}

}
