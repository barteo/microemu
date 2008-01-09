package org.microemu.android.ui;

import javax.microedition.android.lcdui.Displayable;
import javax.microedition.android.lcdui.Graphics;

import org.microemu.DisplayAccess;
import org.microemu.DisplayComponent;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.android.MicroEmulator;
import org.microemu.android.device.AndroidDeviceDisplay;
import org.microemu.android.device.AndroidInputMethod;
import org.microemu.android.device.AndroidMutableImage;
import org.microemu.app.ui.DisplayRepaintListener;
import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.device.MutableImage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class AndroidDisplayComponent extends View implements DisplayComponent {

	private AndroidMutableImage displayImage = null;
	
	private final Paint paint = new Paint();
	
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
		
		Device device = DeviceFactory.getDevice();
		((AndroidInputMethod) device.getInputMethod()).buttonPressed(event);
		
		return true;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (MIDletBridge.getCurrentMIDlet() == null) {
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

	//
	// DisplayComponent
	//
	
	public AndroidDisplayComponent(Context context) {
		super(context);
		
		setFocusable(true);
	}

	public void addDisplayRepaintListener(DisplayRepaintListener arg0) {
		// TODO Auto-generated method stub
		
	}

	public MutableImage getDisplayImage() {
		return displayImage;
	}

	public void removeDisplayRepaintListener(DisplayRepaintListener arg0) {
		// TODO Auto-generated method stub
		
	}

	public void repaintRequest(int x, int y, int width, int height) {
		MIDletAccess ma = MIDletBridge.getMIDletAccess();
		if (ma == null) {
			return;
		}
		DisplayAccess da = ma.getDisplayAccess();
		if (da == null) {
			return;
		}
		Displayable current = da.getCurrent();
		if (current == null) {
			return;
		}

		Device device = DeviceFactory.getDevice();
		
		// TODO take region size into account
		if (device != null) {
			synchronized (this) {
				if (displayImage == null) {
					displayImage = new AndroidMutableImage(device.getDeviceDisplay().getFullWidth(), device
							.getDeviceDisplay().getFullHeight());
				}

				synchronized (displayImage) {
					Graphics canvas = displayImage.getGraphics();

					AndroidDeviceDisplay deviceDisplay = (AndroidDeviceDisplay) device.getDeviceDisplay();
					deviceDisplay.paintDisplayable(canvas, x, y, width, height);
					// TODO
					// if (!deviceDisplay.isFullScreenMode()) {
					// 	deviceDisplay.paintControls(canvas);
					// }
				}
			}
		}

		postInvalidate();
	}
	
}
