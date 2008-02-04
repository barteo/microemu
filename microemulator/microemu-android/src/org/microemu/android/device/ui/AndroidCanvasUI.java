/**
 *  MicroEmulator
 *  Copyright (C) 2008 Bartek Teodorczyk <barteo@barteo.net>
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

package org.microemu.android.device.ui;

import javax.microedition.android.lcdui.Image;

import org.microemu.MIDletBridge;
import org.microemu.android.MicroEmulatorActivity;
import org.microemu.android.device.AndroidInputMethod;
import org.microemu.android.device.AndroidMutableImage;
import org.microemu.device.Device;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.DeviceFactory;
import org.microemu.device.ui.CanvasUI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class AndroidCanvasUI extends AndroidDisplayableUI implements CanvasUI {

	private MicroEmulatorActivity activity;
	
	private CanvasView view;
	
	public AndroidCanvasUI(final MicroEmulatorActivity activity) {
		this.activity = activity;
		
		activity.getHandler().post(new Runnable() {
			public void run() {
				AndroidCanvasUI.this.view = new CanvasView(activity);
			}
		});
	}
	
	public View getView() {
		return view;
	}
	
	public Image getImage() {
		// TODO improve method that waits for for view being initialized
		while (view == null) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		return view.getImage();
	}

	//
	// DisplayableUI
	//

	public void hideNotify() {
	}

	public void showNotify() {
		System.out.println("AndroidCanvasUI::showNotify()");
		activity.getHandler().post(new Runnable() {
			public void run() {
				activity.setContentView(view);
			}
		});
	}
	
	private class CanvasView extends View {
		
		public CanvasView(Context context) {
			super(context);
			
			setFocusable(true);
		}

		private AndroidMutableImage displayImage = null;
		
		private Paint paint = new Paint();
		
		public Image getImage() {
			synchronized(this) {
				if (displayImage == null) {
					DeviceDisplay deviceDisplay = DeviceFactory.getDevice().getDeviceDisplay();
					displayImage = new AndroidMutableImage(deviceDisplay.getFullWidth(), deviceDisplay.getFullHeight());
				}
			}

			return displayImage;
		}

		//
		// View
		//
		
		@Override
		protected void onDraw(Canvas canvas) {
			if (displayImage != null) {
				synchronized (displayImage) {
					canvas.drawBitmap(displayImage.getBitmap(), 0, 0, paint);
				}
			}
		}	
		
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (MIDletBridge.getCurrentMIDlet() == null) {
				return false;
			}
			
			// KEYCODE_SOFT_LEFT == menu key 
			if (keyCode == KeyEvent.KEYCODE_SOFT_LEFT) {
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

			// KEYCODE_SOFT_LEFT == menu key 
			if (keyCode == KeyEvent.KEYCODE_SOFT_LEFT) {
				return false;
			}

			Device device = DeviceFactory.getDevice();
			((AndroidInputMethod) device.getInputMethod()).buttonReleased(event);

			return true;
		}

		@Override
		public boolean onMotionEvent(MotionEvent event) {
			Device device = DeviceFactory.getDevice();
			AndroidInputMethod inputMethod = (AndroidInputMethod) device.getInputMethod();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN :
				inputMethod.pointerPressed((int) event.getX(), (int) event.getY());
				break;
			case MotionEvent.ACTION_UP :
				inputMethod.pointerReleased((int) event.getX(), (int) event.getY());
				break;
			case MotionEvent.ACTION_MOVE :
				inputMethod.pointerDragged((int) event.getX(), (int) event.getY());
				break;
			default:
				return false;
			}
			
			return true;
		}

	}

}
