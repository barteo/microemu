/**
 *  MicroEmulator
 *  Copyright (C) 2008 Bartek Teodorczyk <barteo@barteo.net>
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

import javax.microedition.lcdui.Canvas;

import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.android.MicroEmulatorActivity;
import org.microemu.android.device.AndroidDisplayGraphics;
import org.microemu.android.device.AndroidInputMethod;
import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.device.ui.CanvasUI;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;

public class AndroidCanvasUI extends AndroidDisplayableUI implements CanvasUI {

	private CanvasView canvasView;

	public AndroidCanvasUI(final MicroEmulatorActivity activity, Canvas canvas) {
		super(activity, canvas, true);
		
		activity.post(new Runnable() {
			public void run() {
				canvasView = new CanvasView(activity);
				((LinearLayout) AndroidCanvasUI.this.view).addView(canvasView);

				invalidate();
			}
		});
	}
	
	public View getView() {
		return canvasView;
	}
	
	@Override
	public void invalidate() {
		activity.post(new Runnable() {
			public void run() {
				if (titleView != null) {
					((LinearLayout) AndroidCanvasUI.this.view).removeView(titleView);
					if (displayable.getTitle() != null) {
						titleView.setText(displayable.getTitle());
						((LinearLayout) AndroidCanvasUI.this.view).addView(titleView, 0);
					}
				}
			}
		});
	}

	//
	// CanvasUI
	//
	
	public class CanvasView extends SurfaceView {
		
		private final static int FIRST_DRAG_SENSITIVITY_X = 5;
		
		private final static int FIRST_DRAG_SENSITIVITY_Y = 5;
		
		private AndroidDisplayGraphics graphics = null;
		
		private int pressedX = -FIRST_DRAG_SENSITIVITY_X;
		
		private int pressedY = -FIRST_DRAG_SENSITIVITY_Y;
		
		public CanvasView(Context context) {
			super(context);
			
			setFocusable(true);
			setFocusableInTouchMode(true);
		}

		//
		// View
		//
		
		public void onDraw(Bitmap bitmap) {
			MIDletAccess ma = MIDletBridge.getMIDletAccess();
			if (ma == null) {
				return;
			}
			
			if (graphics == null) {
				graphics = new AndroidDisplayGraphics(bitmap);
			} else if (graphics.getBitmap() != bitmap) {
				graphics = new AndroidDisplayGraphics(bitmap);
			}
			graphics.reset();
			ma.getDisplayAccess().paint(graphics);
		}			

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			Device device = DeviceFactory.getDevice();
			AndroidInputMethod inputMethod = (AndroidInputMethod) device.getInputMethod();
			int x = (int) event.getX();
			int y = (int) event.getY();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN :
				inputMethod.pointerPressed(x, y);
				pressedX = x;
				pressedY = y;
				break;
			case MotionEvent.ACTION_UP :
				inputMethod.pointerReleased(x, y);
				break;
			case MotionEvent.ACTION_MOVE :
				if (x > (pressedX - FIRST_DRAG_SENSITIVITY_X) &&  x < (pressedX + FIRST_DRAG_SENSITIVITY_X)
						&& y > (pressedY - FIRST_DRAG_SENSITIVITY_Y) &&  y < (pressedY + FIRST_DRAG_SENSITIVITY_Y)) {
				} else {
					pressedX = -FIRST_DRAG_SENSITIVITY_X;
					pressedY = -FIRST_DRAG_SENSITIVITY_Y;
					inputMethod.pointerDragged(x, y);
				}
				break;
			default:
				return false;
			}
			
			return true;
		}

		@Override
		public Handler getHandler() {
			return super.getHandler();
		}		
       
	}

}
