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
import javax.microedition.lcdui.Graphics;

import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.android.MicroEmulatorActivity;
import org.microemu.android.device.AndroidDisplayGraphics;
import org.microemu.android.device.AndroidInputMethod;
import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.device.ui.CanvasUI;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class AndroidCanvasUI extends AndroidDisplayableUI implements CanvasUI {

	public AndroidCanvasUI(final MicroEmulatorActivity activity, Canvas canvas) {
		super(activity, canvas);
		
		activity.post(new Runnable() {
			public void run() {
				view = new CanvasView(activity);
			}
		});
	}
	
	public CanvasView getView() {
		return (CanvasView) view;
	}
	
	//
	// CanvasUI
	//
	
	public class CanvasView extends View {
		
		private int FIRST_DRAG_SENSITIVITY_X = 5;
		private int FIRST_DRAG_SENSITIVITY_Y = 5;
		
		int pressedX = -FIRST_DRAG_SENSITIVITY_X;
		
		int pressedY = -FIRST_DRAG_SENSITIVITY_Y;
		
		public CanvasView(Context context) {
			super(context);
			
			setFocusable(true);
			setFocusableInTouchMode(true);
		}

		//
		// View
		//
		
		@Override
		protected void onDraw(android.graphics.Canvas androidCanvas) {
			MIDletAccess ma = MIDletBridge.getMIDletAccess();
			if (ma == null) {
				return;
			}
			Graphics g = new AndroidDisplayGraphics(androidCanvas);
			Rect r = androidCanvas.getClipBounds();
			g.clipRect(r.left, r.top, r.width(), r.height());
			ma.getDisplayAccess().paint(g);
		}	
		
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (MIDletBridge.getCurrentMIDlet() == null) {
				return false;
			}
			
			if (ignoreKey(keyCode)) {
                return false;    
            }
			
			Device device = DeviceFactory.getDevice();
			((AndroidInputMethod) device.getInputMethod()).buttonPressed(event);
			
			return true;
		}

		@Override
		public boolean onKeyUp(int keyCode, KeyEvent event) {
			if (MIDletBridge.getCurrentMIDlet() == null) {
				return false;
			}
            
            if (ignoreKey(keyCode)) {
                return false;    
            }

			Device device = DeviceFactory.getDevice();
			((AndroidInputMethod) device.getInputMethod()).buttonReleased(event);

			return true;
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
       
        private boolean ignoreKey(int keyCode) {
            switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_HEADSETHOOK: 
                return true;
            default:
                return false;
            }    
        }
	}

}
