/**
 *  MicroEmulator
 *  Copyright (C) 2006-2009 Bartek Teodorczyk <barteo@barteo.net>
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
 *  @version $Id: MIDletTimer.java 1970 2009-03-12 13:51:04Z barteo $
 */

package org.microemu.app.util;

import java.util.TimerTask;

public abstract class MIDletTimerTask extends TimerTask {
	
	MIDletTimer timer;

	long time = -1;
	
	long period;
	
	boolean oneTimeTaskExcecuted = false;
	
	public boolean cancel() {
		if (timer == null) {
			return false;
		}
		
		synchronized (timer.tasks) {
			// task was never scheduled
			if (time == -1) {
				return false;
			}		
			// task was scheduled for one-time execution and has already run
			if (oneTimeTaskExcecuted) {
				return false;
			}
			timer.tasks.remove(this);
		}

		return true;
	}

	public long scheduledExecutionTime() {
		return time;
	}

}
