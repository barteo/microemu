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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.WeakHashMap;

import org.microemu.MIDletBridge;
import org.microemu.MIDletContext;
import org.microemu.log.Logger;

/**
 * Terminate all timers on MIDlet exit. TODO Name all the timer Threads created
 * by MIDlet in Java 5
 * 
 * @author vlads
 */
public class MIDletTimer extends Timer implements Runnable {

	private static Map midlets = new WeakHashMap();

	private String name;

	private MIDletContext midletContext;
	
	// TODO use better data structure
	private List tasks;
	
	private boolean cancelled;

	private MIDletThread thread;
	
	public MIDletTimer() {
		super();
		StackTraceElement[] ste = new Throwable().getStackTrace();
		name = ste[1].getClassName() + "." + ste[1].getMethodName();
		tasks = new ArrayList();
		cancelled = false;
		thread = new MIDletThread(this);
		thread.start();
	}

	// TODO exceptions
	public void schedule(TimerTask task, Date time) {
		register(this);
		schedule(task, time.getTime(), -1, false);
	}

	// TODO exceptions
	public void schedule(TimerTask task, Date firstTime, long period) {
		register(this);
		schedule(task, firstTime.getTime(), period, false);
	}

	// TODO exceptions
	public void schedule(TimerTask task, long delay) {
		register(this);
		schedule(task, System.currentTimeMillis() + delay, -1, false);
	}

	// TODO exceptions
	public void schedule(TimerTask task, long delay, long period) {
		register(this);
		schedule(task, System.currentTimeMillis() + delay, period, false);
	}

	// TODO exceptions
	public void scheduleAtFixedRate(TimerTask task, Date firstTime, long period) {
		register(this);
		schedule(task, firstTime.getTime(), period, true);
	}

	// TODO exceptions
	public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
		register(this);
		schedule(task, System.currentTimeMillis() + delay, period, true);
	}

	public void cancel() {
		unregister(this);
		
		terminate();
	}
	
	public void run() {
		while (!cancelled) {
			MIDletTimerTask task = null;
			long nextTimeTask = Long.MAX_VALUE;
			synchronized (tasks) {
				Iterator it = tasks.iterator();
				while (it.hasNext()) {
					MIDletTimerTask candidate = (MIDletTimerTask) it.next();
					if (candidate.time > System.currentTimeMillis()) {
						if (candidate.time < nextTimeTask) {
							nextTimeTask = candidate.time;
						}
						continue;
					}
					if (task == null) {
						task = candidate;
					} else if (candidate.time < task.time) {
						if (task.time < nextTimeTask) {
							nextTimeTask = task.time;
						}
						task = candidate;
					} else if (candidate.time < nextTimeTask) {
						nextTimeTask = candidate.time;
					}
				}
				tasks.remove(task);
			}
			
			if (task != null) {
				try {
					task.task.run();
				} catch (Throwable t) {
					Logger.debug("MIDletTimerTask throws", t);
					task = null;
				}
				if (task != null) {
					synchronized (tasks) {
						// TODO implement scheduling for fixed rate tasks	
						if (task.period > 0) {
							task.time = System.currentTimeMillis() + task.period;
							tasks.add(task);
							if (task.time < nextTimeTask) {
								nextTimeTask = task.time;
							}
						}
					}
				}
			}
			
			synchronized (tasks) {
				try {
					if (nextTimeTask == Long.MAX_VALUE) {
						tasks.wait();
					} else {
						long timeout = nextTimeTask - System.currentTimeMillis();
						if (timeout > 0) {
							tasks.wait(timeout);
						}
					}
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private void terminate() {
		cancelled = true;
	}
	
	private void schedule(TimerTask task, long time, long period, boolean fixedRate) {
		synchronized (tasks) {
			tasks.add(new MIDletTimerTask(task, time, period, fixedRate));
			tasks.notify();
		}
	}

	private static void register(MIDletTimer timer) {
		if (timer.midletContext == null) {
			timer.midletContext = MIDletBridge.getMIDletContext();
		}
		if (timer.midletContext == null) {
			Logger.error("Creating Timer with no MIDlet context", new Throwable());
			return;
		}
		Map timers = (Map) midlets.get(timer.midletContext);
		if (timers == null) {
			// Can't use WeakHashMap Timers are disposed by JVM
			timers = new HashMap();
			midlets.put(timer.midletContext, timers);
		}
		// Logger.debug("Register timer created from [" + timer.name + "]");
		timers.put(timer, timer.midletContext);
	}

	private static void unregister(MIDletTimer timer) {
		if (timer.midletContext == null) {
			// Logger.error("Timer with no MIDlet context", new Throwable());
			return;
		}
		Map timers = (Map) midlets.get(timer.midletContext);
		if (timers == null) {
			return;
		}
		// Logger.debug("Unregister timer created from [" + timer.name + "]");
		timers.remove(timer);
	}

	/**
	 * Terminate all Threads created by MIDlet
	 */
	public static void contextDestroyed(MIDletContext midletContext) {
		if (midletContext == null) {
			return;
		}
		Map timers = (Map) midlets.get(midletContext);
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
				MIDletTimer tm = (MIDletTimer) o;
				Logger.warn("MIDlet timer created from [" + tm.name + "] still running");
				tm.terminate();
			} else {
				Logger.debug("unrecognized Object [" + o.getClass().getName() + "]");
			}
		}
	}

	private class MIDletTimerTask
	{
		private TimerTask task;
		
		private long time;
		
		private long period;
		
		private boolean fixedRate;

		public MIDletTimerTask(TimerTask task, long time, long period, boolean fixedRate) {
			this.task = task;
			this.time = time;
			this.period = period;
			this.fixedRate = fixedRate;
		}
		
	}
}
