/**
 *  MicroEmulator
 *  Copyright (C) 2009 Bartek Teodorczyk <barteo@barteo.net>
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
 *  @version $Id: AndroidGaugeUI.java 1918 2009-01-21 12:56:43Z barteo $
 */

package org.microemu.android.device.ui;

import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.ItemStateListener;

import org.microemu.android.MicroEmulatorActivity;
import org.microemu.device.ui.GaugeUI;

import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class AndroidGaugeUI extends LinearLayout implements GaugeUI {

	private MicroEmulatorActivity activity;
	
	private TextView labelView;

	private SeekBar seekBar;
	
	public AndroidGaugeUI(final MicroEmulatorActivity activity, final Gauge gauge) {
		super(activity);
		
		this.activity = activity;

		activity.post(new Runnable() {
			public void run() {
				setOrientation(LinearLayout.VERTICAL);
		//		setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
				
				labelView = new TextView(activity);
				labelView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				labelView.setTextAppearance(labelView.getContext(), android.R.style.TextAppearance_Large);
				addView(labelView);
				
				seekBar = new SeekBar(activity);
				seekBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
				seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
						AndroidFormUI.AndroidListView formList = (AndroidFormUI.AndroidListView) getParent();
						if (formList != null) {
							ItemStateListener listener = formList.getUI().getItemStateListener();
							if (listener != null) {
								listener.itemStateChanged(gauge);
							}
						}
					}

					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					public void onStopTrackingTouch(SeekBar seekBar) {
					}
					
				});
				addView(seekBar);
				
				setLabel(gauge.getLabel());
			}
		});
	}

	public void setLabel(final String label) {
		activity.post(new Runnable() {
			public void run() {
				labelView.setText(label);
			}
		});
	}

	public void setValue(final int value) {
		activity.post(new Runnable() {
			public void run() {
				seekBar.setProgress(value);
			}
		});
	}
	
	private int getValueTransfer;

	public int getValue() {
		if (activity.isActivityThread()) {
			getValueTransfer = seekBar.getProgress();
		} else {
			getValueTransfer = Integer.MIN_VALUE;
			activity.post(new Runnable() {
				public void run() {
					synchronized (AndroidGaugeUI.this) {
						getValueTransfer = seekBar.getProgress();
						AndroidGaugeUI.this.notify();
					}
				}
			});

			synchronized (AndroidGaugeUI.this) {
				if (getValueTransfer == Integer.MIN_VALUE) {
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return getValueTransfer;
	}

	public void setMaxValue(final int maxValue) {
		activity.post(new Runnable() {
			public void run() {
				seekBar.setMax(maxValue);
			}
		});
	}

}
