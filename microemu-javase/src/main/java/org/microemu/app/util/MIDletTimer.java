/**
 *  MicroEmulator
 *  Copyright (C) 2006-2007 Bartek Teodorczyk <barteo@barteo.net>
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
