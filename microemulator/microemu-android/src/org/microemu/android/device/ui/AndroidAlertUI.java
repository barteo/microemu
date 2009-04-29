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
 *  @version $Id$
 */

package org.microemu.android.device.ui;

import javax.microedition.lcdui.Alert;

import org.microemu.android.MicroEmulatorActivity;
import org.microemu.device.ui.AlertUI;

import android.content.res.TypedArray;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AndroidAlertUI extends AndroidDisplayableUI implements AlertUI {

	protected TextView alertTextView;
	
	public AndroidAlertUI(final MicroEmulatorActivity activity, Alert alert) {
		super(activity, alert, true);
		
		activity.post(new Runnable() {
			public void run() {
				alertTextView = new TextView(activity);
				alertTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				TypedArray a = alertTextView.getContext().obtainStyledAttributes(android.R.styleable.Theme);
				alertTextView.setTextAppearance(alertTextView.getContext(), a.getResourceId(android.R.styleable.Theme_textAppearanceLarge, -1));
				((LinearLayout) view).addView(alertTextView);				

				invalidate();
			}
		});		
	}

	//
	// AlertUI
	//		
	
	public void setString(final String str) {
		activity.post(new Runnable() {
			public void run() {
				alertTextView.setText(str);
			}
		});
	}
		
}
