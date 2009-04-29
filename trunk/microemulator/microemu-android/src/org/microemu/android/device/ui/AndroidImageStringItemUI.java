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
 *  @version $Id: AndroidImageStringItemUI.java 1931 2009-02-05 21:00:52Z barteo $
 */

package org.microemu.android.device.ui;

import javax.microedition.lcdui.Item;

import org.microemu.android.MicroEmulatorActivity;
import org.microemu.device.ui.ImageStringItemUI;

import android.content.res.TypedArray;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AndroidImageStringItemUI extends LinearLayout implements ImageStringItemUI {

	private MicroEmulatorActivity activity;
	
	private TextView labelView;
	
	private TextView textView;
	
	public AndroidImageStringItemUI(MicroEmulatorActivity activity, Item item) {
		super(activity);
		
		this.activity = activity;
		
		setOrientation(LinearLayout.VERTICAL);
		setFocusable(false);
		setFocusableInTouchMode(false);
//		setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
		
		textView = new TextView(activity);
		setFocusable(false);
		setFocusableInTouchMode(false);
		textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//		a = textView.getContext().obtainStyledAttributes(android.R.styleable.Theme);
//		textView.setTextAppearance(labelView.getContext(), a.getResourceId(android.R.styleable.Theme_textAppearanceLarge, -1));
		addView(textView);
		
		setLabel(item.getLabel());
	}

	public void setLabel(final String label) {
		activity.post(new Runnable() {
			public void run() {
				if (label == null) {
					if (labelView != null) {
						removeView(labelView);
						labelView = null;
					}
				} else {
					if (labelView == null) {
						labelView = new TextView(activity);
						labelView.setFocusable(false);
						labelView.setFocusableInTouchMode(false);
						labelView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
						TypedArray a = labelView.getContext().obtainStyledAttributes(android.R.styleable.Theme);
						labelView.setTextAppearance(labelView.getContext(), a.getResourceId(android.R.styleable.Theme_textAppearanceLarge, -1));
						addView(labelView, 0);
					}
					labelView.setText(label);
				}
			}
		});
	}

	public void setText(final String text) {
		activity.post(new Runnable() {
			public void run() {
				textView.setText(text);
			}
		});
	}

}
