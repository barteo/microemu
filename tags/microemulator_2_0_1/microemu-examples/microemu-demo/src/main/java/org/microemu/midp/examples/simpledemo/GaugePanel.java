/**
 *  MicroEmulator
 *  Copyright (C) 2001-2007 Bartek Teodorczyk <barteo@barteo.net>
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
