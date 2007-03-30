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

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.log.Logger;

/**
 * MIDletAccess is used to hold keys to running Threads created by  MIDlet  
 * 
 * @author vlads
 */
public class MIDletThread extends Thread {

	private static final String THREAD_NAME_PREFIX = "MIDletThread-";
	
	private static Map midlets = new WeakHashMap();
	
    private static int threadInitNumber;
    
    private static synchronized int nextThreadNum() {
    	return threadInitNumber++;
    }
    
	public MIDletThread() {
		super(THREAD_NAME_PREFIX + nextThreadNum());
		register(this);
	}
	
	public MIDletThread(Runnable target) {
		super(target, THREAD_NAME_PREFIX + nextThreadNum());
		register(this);
	}
	
	public MIDletThread(Runnable target, String name) {
		super(target, THREAD_NAME_PREFIX + name);
		register(this);
	}
	
	public MIDletThread(String name) {
		super(THREAD_NAME_PREFIX + name);
		register(this);
	}
	
	private static void register(Thread thread) {
		MIDletAccess midletAccess = MIDletBridge.getMIDletAccess();
		if (midletAccess == null) {
			Logger.error("Creating thread with no MIDlet context", new Throwable());
			return;
		}
		Map threads = (Map)midlets.get(midletAccess);
		if (threads == null) {
			threads = new WeakHashMap();
			midlets.put(midletAccess, threads);
		}
		threads.put(thread, midletAccess);
	}
	
	/**
	 * Termnate all Threads created by MIDlet
	 * @param previousMidletAccess
	 */
	public static void notifyDestroyed(MIDletAccess midletAccess) {
		if (midletAccess == null) {
			return;
		}
		Map threads = (Map)midlets.get(midletAccess);
		if (threads != null) {
			terminateThreads(threads);
			midlets.remove(midletAccess);
		}
		MIDletTimer.notifyDestroyed(midletAccess);
	}
	
	private static void terminateThreads(Map threads) {
		for (Iterator iter = threads.keySet().iterator(); iter.hasNext();) {
			Object o = iter.next();
			if (o == null) {
				continue;
			}
			if (o instanceof MIDletThread) {
				MIDletThread t = (MIDletThread) o;
				if (t.isAlive()) {
					Logger.warn("MIDlet thread [" + t.getName() + "] still running");
					t.interrupt();
				}
			} else {
				Logger.debug("unrecognized Object [" + o.getClass().getName() + "]");
			}
		};
	}
}
