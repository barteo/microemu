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
package org;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author vlads
 * 
 */
public class TimerTaskCancelRunner implements Runnable {

	static boolean timerTaskExecuted = false;

	public static void main(String[] args) {
		(new TimerTaskCancelRunner()).run();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		Timer timer = new Timer();
		try {
			TimerTask timerTask = new TimerTask() {
				public void run() {
					System.err.println("TimerTask executed");
					timerTaskExecuted = true;
				}
			};
			timer.schedule(timerTask, 1000);

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

			timerTask.cancel();

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

			if (timerTaskExecuted) {
				throw new RuntimeException("Timer should have been cancelled");
			} else {
				System.out.println("Timer was cancelled - OK");
			}
		} finally {
			timer.cancel();
		}
	}

}
