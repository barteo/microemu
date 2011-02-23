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
 *  @version $Id: AndroidDateFieldUI.java 1918 2009-01-21 12:56:43Z barteo $
 */

package org.microemu.android.device.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.ItemStateListener;

import org.microemu.android.MicroEmulatorActivity;
import org.microemu.android.util.ValueObject;
import org.microemu.device.ui.DateFieldUI;

import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

public class AndroidDateFieldUI extends LinearLayout implements DateFieldUI {

	private MicroEmulatorActivity activity;
	
	private DateField dateField;
	
	private TextView labelView;
	
	private View datetimeView;
	
	private DatePicker datePicker;
	
	private TimePicker timePicker;
	
	private int mode;

	private Command defaultCommand;
	
	public AndroidDateFieldUI(final MicroEmulatorActivity activity, final DateField dateField) {
		super(activity);
		
		this.activity = activity;
		this.dateField = dateField;
		
		this.mode = -1;
		
		activity.post(new Runnable() {
			public void run() {
				setOrientation(LinearLayout.VERTICAL);
		//		setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
				
				labelView = new TextView(activity);
				labelView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				labelView.setTextAppearance(labelView.getContext(), android.R.style.TextAppearance_Large);
				addView(labelView);
				
				setLabel(dateField.getLabel());
			}
		});
	}

	public void setDefaultCommand(Command cmd) {
		this.defaultCommand = cmd;
	}

	public void setLabel(String label) {
		labelView.setText(label);
	}

	public void setInputMode(final int mode) {
		activity.post(new Runnable() {
			public void run() {
				if (AndroidDateFieldUI.this.mode != mode) {
					AndroidDateFieldUI.this.mode = mode;
					if (mode == DateField.TIME) {
						timePicker = createTimePicker();
						datetimeView = timePicker;
					} else if (mode == DateField.DATE) {
						datePicker = createDatePicker();
						datetimeView = datePicker;
					} else { // DateField.DATE_TIME
						datetimeView = new LinearLayout(activity);
						((LinearLayout) datetimeView).setOrientation(LinearLayout.VERTICAL);
						timePicker = createTimePicker();
						datePicker = createDatePicker();
						((LinearLayout) datetimeView).addView(timePicker);
						((LinearLayout) datetimeView).addView(datePicker);
					}
					datetimeView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
					addView(datetimeView);
				}
			}
		});
	}

	public void setDate(final Date date) {
		activity.post(new Runnable() {
			public void run() {
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(date);
				if ((mode & DateField.DATE) != 0) {
					datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
				}
				if ((mode & DateField.TIME) != 0) {
					timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
					timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));
				}
			}
		});
	}

	public Date getDate() {
		final ValueObject result = new ValueObject();
		result.value = null;
		
		activity.post(new Runnable() {
			public void run() {
				GregorianCalendar cal = new GregorianCalendar();
				if ((mode & DateField.DATE) != 0) {
					cal.set(Calendar.YEAR, datePicker.getYear());
					cal.set(Calendar.MONTH, datePicker.getMonth());
					cal.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
				}
				if ((mode & DateField.TIME) != 0) {
					cal.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
					cal.set(Calendar.MINUTE, timePicker.getCurrentMinute());
				}
				
				synchronized (AndroidDateFieldUI.this) {
					result.value = cal.getTime();
					AndroidDateFieldUI.this.notify();
				}
			}
		});
		
		synchronized (AndroidDateFieldUI.this) {
			if (result.value == null) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		return (Date) result.value;
	}
	
	private TimePicker createTimePicker() {
		TimePicker result = new TimePicker(activity);
		result.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				AndroidFormUI.AndroidListView formList = (AndroidFormUI.AndroidListView) getParent();
				if (formList != null) {
					ItemStateListener listener = formList.getUI().getItemStateListener();
					if (listener != null) {
						listener.itemStateChanged(dateField);
					}
				}
			}
			
		});
		
		return result;
	}
	
	private DatePicker createDatePicker() {
		DatePicker result = new DatePicker(activity);
		result.init(1970, 1, 1, new DatePicker.OnDateChangedListener() {

			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				AndroidFormUI.AndroidListView formList = (AndroidFormUI.AndroidListView) getParent();
				if (formList != null) {
					ItemStateListener listener = formList.getUI().getItemStateListener();
					if (listener != null) {
						listener.itemStateChanged(dateField);
					}
				}
			}
			
		});
		
		return result;
	}	

}
