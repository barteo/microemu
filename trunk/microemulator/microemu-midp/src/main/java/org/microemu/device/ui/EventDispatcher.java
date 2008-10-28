/**
 *  MicroEmulator
 *  Copyright (C) 2002 Bartek Teodorczyk <barteo@barteo.net>
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
 */

package org.microemu.device.ui;

import org.microemu.device.DeviceFactory;

public class EventDispatcher implements Runnable {
	
	public static final String EVENT_DISPATCHER_NAME = "event-thread";

	private volatile boolean cancelled = false;

	private Event head = null;

	private Event tail = null;

	private PaintEvent scheduledPaintEvent = null;

	private PointerEvent scheduledPointerDraggedEvent = null;

	private Object serviceRepaintsLock = new Object();

	public EventDispatcher() {
	}
	
	public void run() {

		while (!cancelled) {
			Event event = null;
			synchronized (this) {
				if (head != null) {
					event = head;
					head = event.next;
					if (head == null) {
						tail = null;
					}
					if (event instanceof PaintEvent) {
						scheduledPaintEvent = null;
					}
					if (event instanceof PointerEvent && ((PointerEvent) event).type == PointerEvent.POINTER_DRAGGED) {
						scheduledPointerDraggedEvent = null;
					}
				} else {
					try {
						wait();
					} catch (InterruptedException e) {
					}
				}
			}

			if (event != null) {
				post(event);
			}

			synchronized (serviceRepaintsLock) {
				if (event instanceof PaintEvent) {
					serviceRepaintsLock.notify();
				}
			}
		}
	}

	/**
	 * Do not service any more events
	 */
	public final void cancel() {
		cancelled = true;
		synchronized (this) {
			notifyAll();
		}
	}

	public void put(Event event) {
		synchronized (this) {
			if (event instanceof PaintEvent && scheduledPaintEvent != null) {
				scheduledPaintEvent.merge((PaintEvent) event);
			} else if (event instanceof PointerEvent && scheduledPointerDraggedEvent != null
					&& ((PointerEvent) event).type == PointerEvent.POINTER_DRAGGED) {
				scheduledPointerDraggedEvent.x = ((PointerEvent) event).x;
				scheduledPointerDraggedEvent.y = ((PointerEvent) event).y;
			} else {
				if (event instanceof PaintEvent) {
					scheduledPaintEvent = (PaintEvent) event;
				}
				if (event instanceof PointerEvent && ((PointerEvent) event).type == PointerEvent.POINTER_DRAGGED) {
					scheduledPointerDraggedEvent = (PointerEvent) event;
				}
				if (tail != null) {
					tail.next = event;
				}
				tail = event;
				if (head == null) {
					head = event;
				}
				notifyAll();
			}
		}
	}

	public void put(Runnable runnable) {
		put(new RunnableEvent(runnable));
	}

	public void serviceRepaints() {
		synchronized (serviceRepaintsLock) {
			synchronized (this) {
				if (scheduledPaintEvent == null) {
					return;
				}

				// TODO move scheduledPaintEvent to head
			}

			try {
				serviceRepaintsLock.wait();
			} catch (InterruptedException e) {
			}
		}
	}

	protected void post(Event event) {
		event.run();
	}

	public abstract class Event implements Runnable {

		Event next = null;

	}

	public final class PaintEvent extends Event {

		private int x = -1, y = -1, width = -1, height = -1;

		public PaintEvent(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		public void run() {
			DeviceFactory.getDevice().getDeviceDisplay().repaint(x, y, width, height);
		}

		/**
		 * Do a 2-D merge of the paint areas
		 * 
		 * @param event
		 */
		public final void merge(PaintEvent event) {
			int xMax = x + width;
			int yMax = y + height;

			this.x = Math.min(this.x, event.x);
			xMax = Math.max(xMax, event.x + event.width);

			this.y = Math.min(this.y, event.y);
			yMax = Math.max(yMax, event.y + event.height);

			this.width = xMax - x;
			this.height = yMax - y;
		}

	}

	public final class PointerEvent extends EventDispatcher.Event {

		public static final short POINTER_PRESSED = 0;

		public static final short POINTER_RELEASED = 1;

		public static final short POINTER_DRAGGED = 2;

		private Runnable runnable;

		private short type;

		private int x;

		private int y;

		public PointerEvent(Runnable runnable, short type, int x, int y) {
			this.runnable = runnable;
			this.type = type;
			this.x = x;
			this.y = y;
		}

		public void run() {
			runnable.run();
		}
	}
	
	public final class ShowHideNotifyEvent extends EventDispatcher.Event {

		public static final short SHOW_NOTIFY = 0;

		public static final short HIDE_NOTIFY = 1;

		private short type;

		private Runnable runnable;

		public ShowHideNotifyEvent(short type, Runnable runnable) {
			this.type = type;
			this.runnable = runnable;
		}

		public void run() {
			runnable.run();
		}
	}

	private class RunnableEvent extends Event {

		private Runnable runnable;

		public RunnableEvent(Runnable runnable) {
			this.runnable = runnable;
		}

		public void run() {
			runnable.run();
		}

	}

}
