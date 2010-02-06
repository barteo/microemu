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

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import org.microemu.MIDletBridge;
import org.microemu.MIDletContext;
import org.microemu.log.Logger;
import org.microemu.util.ThreadUtils;

/**
 * MIDletContext is used to hold keys to running Threads created by  MIDlet  
 * 
 * @author vlads
 */
public class MIDletThread extends Thread {

	public static int graceTerminationPeriod = 5000;
	
	private static final String THREAD_NAME_PREFIX = "";
	
	private static boolean terminator = false;
	
	private static Map midlets = new WeakHashMap();
	
    private static int threadInitNumber;
    
    private String callLocation;
    
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
	
	private static void register(MIDletThread thread) {
		MIDletContext midletContext = MIDletBridge.getMIDletContext();
		if (midletContext == null) {
			Logger.error("Creating thread with no MIDlet context", new Throwable());
			return;
		}
		thread.callLocation  = ThreadUtils.getCallLocation(MIDletThread.class.getName());
		Map threads = (Map)midlets.get(midletContext);
		if (threads == null) {
			threads = new WeakHashMap();
			midlets.put(midletContext, threads);
		}
		threads.put(thread, midletContext);
	}
	
	//TODO overrite run() in user Threads using ASM
	public void run() {
		 try {
			super.run();
		} catch (Throwable e) {
			Logger.debug("MIDletThread throws", e);
		}
		//Logger.debug("thread ends, created from " + callLocation);	
	 }
	
	/**
	 * Terminate all Threads created by MIDlet
	 * @param previousMidletAccess
	 */
	public static void contextDestroyed(final MIDletContext midletContext) {
		if (midletContext == null) {
			return;
		}
		final Map threads = (Map)midlets.remove(midletContext);
		if ((threads != null) && (threads.size() != 0)) {
			terminator = true;
			Thread terminator = new Thread("MIDletThreadsTerminator") {
				public void run() {
					terminateThreads(threads);
				}
			};
			terminator.start();
		}
		MIDletTimer.contextDestroyed(midletContext);
	}
	
	public static boolean hasRunningThreads(MIDletContext midletContext) {
		//return (midlets.get(midletContext) != null);
		return terminator;
	}
	
	private static void terminateThreads(Map threads) {
		long endTime = System.currentTimeMillis() + graceTerminationPeriod;
		for (Iterator iter = threads.keySet().iterator(); iter.hasNext();) {
			Object o = iter.next();
			if (o == null) {
				continue;
			}
			if (o instanceof MIDletThread) {
				MIDletThread t = (MIDletThread) o;
				if (t.isAlive()) {
					Logger.info("wait thread [" + t.getName() + "] end");
					while ((endTime > System.currentTimeMillis()) && (t.isAlive())) {
						try {
							t.join(700);
						} catch (InterruptedException e) {
							break;
						}
					}
					if (t.isAlive()) {
						Logger.warn("MIDlet thread [" + t.getName() + "] still running" + ThreadUtils.getTreadStackTrace(t));
						if (t.callLocation != null) {
							Logger.info("this thread [" + t.getName() + "] was created from " + t.callLocation);
						}
						t.interrupt();
					}
				}
			} else {
				Logger.debug("unrecognized Object [" + o.getClass().getName() + "]");
			}
		};
		Logger.debug("all "+ threads.size() + " thread(s) finished");
		terminator = false;
	}

}
