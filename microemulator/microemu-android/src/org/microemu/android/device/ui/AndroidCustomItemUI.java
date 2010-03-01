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
 *  @version $Id: AndroidCustomItemUI.java 1918 2009-01-21 12:56:43Z barteo $
 */

package org.microemu.android.device.ui;

import javax.microedition.lcdui.Command;

import org.microemu.CustomItemAccess;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.android.MicroEmulatorActivity;
import org.microemu.android.device.AndroidDisplayGraphics;
import org.microemu.device.ui.CustomItemUI;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AndroidCustomItemUI extends LinearLayout implements CustomItemUI {

	private MicroEmulatorActivity activity;
	
	private CustomItemAccess customItemAccess;
	
	private TextView labelView;
	
	public View view;
	
	public AndroidCustomItemUI(final MicroEmulatorActivity activity, final CustomItemAccess customItemAccess) {
		super(activity);
		
		this.activity = activity;
		this.customItemAccess = customItemAccess;

		activity.post(new Runnable() {
			public void run() {
				setOrientation(LinearLayout.VERTICAL);
		//		setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
				
				labelView = new TextView(activity);
				labelView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				labelView.setTextAppearance(labelView.getContext(), android.R.style.TextAppearance_Large);
				addView(labelView);
				
				view = new CanvasView(activity);
				addView(view);
				
				setLabel(customItemAccess.getCustomItem().getLabel());
			}
		});
	}

	public void setDefaultCommand(Command cmd) {
	}

	public void setLabel(final String label) {
		activity.post(new Runnable() {
			public void run() {
				if (labelView != null) {
					labelView.setText(label);
				}
			}
		});
	}
	
	public void repaint() {
		activity.post(new Runnable() {
			public void run() {
				view.invalidate();
			}
		});
	}
	
	private class CanvasView extends View {
		
		private int FIRST_DRAG_SENSITIVITY_X = 5;
		private int FIRST_DRAG_SENSITIVITY_Y = 5;
		
		int pressedX = -FIRST_DRAG_SENSITIVITY_X;
		
		int pressedY = -FIRST_DRAG_SENSITIVITY_Y;
		
        private AndroidDisplayGraphics graphics;
        
		public CanvasView(Context context) {
			super(context);
			
            this.graphics = new AndroidDisplayGraphics();

            setFocusable(true);
			setFocusableInTouchMode(true);
		}

		//
		// View
		//
		
		@Override
		public int getSuggestedMinimumWidth() {
			int suggestedWidth = customItemAccess.getPrefContentWidth(-1);
			return suggestedWidth;
		}
		
		@Override
		public int getSuggestedMinimumHeight() {
			int suggestedHeight = customItemAccess.getPrefContentHeight(-1);
			return suggestedHeight;
		}
		
		@Override
		protected void onDraw(android.graphics.Canvas androidCanvas) {
			MIDletAccess ma = MIDletBridge.getMIDletAccess();
			if (ma == null) {
				return;
			}
			graphics.reset(androidCanvas);
			graphics.setClip(0, 0, view.getWidth(), view.getHeight());
			int suggestedHeight = customItemAccess.getPrefContentHeight(-1);
			customItemAccess.paint(graphics, view.getWidth(), suggestedHeight);
		}	
		
	}

}
