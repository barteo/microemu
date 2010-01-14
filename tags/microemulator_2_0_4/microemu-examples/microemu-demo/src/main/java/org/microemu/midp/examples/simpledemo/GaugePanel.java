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
package org.microemu.midp.examples.simpledemo;

import javax.microedition.lcdui.Gauge;

public class GaugePanel extends BaseExamplesForm implements HasRunnable {

	private boolean cancel = false;

	private Gauge noninteractive = new Gauge("Noninteractive", false, 25, 0);

	private Runnable timerTask = new Runnable() {

		public void run() {
			while (!cancel) {
				if (isShown()) {
					int value = noninteractive.getValue();

					if (noninteractive.getValue() >= 25) {
						noninteractive.setValue(0);
					} else {
						noninteractive.setValue(++value);
					}
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException ex) {
					break;
				}
			}
		}
	};

	public GaugePanel() {
		super("Gauge");
		append(new Gauge("Interactive", true, 25, 0));
		append(noninteractive);
	}

	public void startRunnable() {
		cancel = false;
		Thread thread = new Thread(timerTask, "GaugePanelThread");
		thread.start();
	}

	public void stopRunnable() {
		cancel = true;
	}

}
