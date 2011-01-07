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

import org.microemu.DisplayAccess;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.android.MicroEmulatorActivity;
import org.microemu.android.device.AndroidDeviceDisplay;
import org.microemu.android.device.AndroidDisplayGraphics;
import org.microemu.android.device.AndroidInputMethod;
import org.microemu.android.util.AndroidRepaintListener;
import org.microemu.android.util.Overlay;
import org.microemu.app.ui.DisplayRepaintListener;
import org.microemu.device.Device;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.DeviceFactory;
import org.microemu.device.ui.CanvasUI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.view.View;

public class AndroidCanvasUI extends AndroidDisplayableUI implements CanvasUI {
    
    private AndroidDisplayGraphics graphics = null;
    
    private Bitmap bitmap = null;
    
    private android.graphics.Canvas bitmapCanvas;
    
    public AndroidCanvasUI(final MicroEmulatorActivity activity, Canvas canvas) {
        super(activity, canvas, false);
        
        DeviceDisplay display = activity.getEmulatorContext().getDeviceDisplay();
        initGraphics(display.getWidth(), display.getHeight());
        
        activity.post(new Runnable() {
            public void run() {
                view = new CanvasView(activity, AndroidCanvasUI.this);
            	((CanvasView) view).getHolder().addCallback(new Callback() {
					
					@Override
					public void surfaceDestroyed(SurfaceHolder holder) {
				        ((AndroidDeviceDisplay) activity.getEmulatorContext().getDeviceDisplay()).removeDisplayRepaintListener((DisplayRepaintListener) view);
					}
					
					@Override
					public void surfaceCreated(SurfaceHolder holder) {
		            	((AndroidDeviceDisplay) activity.getEmulatorContext().getDeviceDisplay()).addDisplayRepaintListener((DisplayRepaintListener) view);
		            	((Canvas) displayable).repaint(0, 0, bitmap.getWidth(), bitmap.getHeight());
					}
					
					@Override
					public void surfaceChanged(SurfaceHolder holder, int format, int width, int heigh) {
					}
					
				});
            }
        });
    }
    
    public void initGraphics(int width, int height) {
        if (graphics == null) {
            graphics = new AndroidDisplayGraphics();
        }
        if (bitmap == null || bitmap.getWidth() != width || bitmap.getHeight() != height) {
        	bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        	bitmapCanvas = new android.graphics.Canvas(bitmap);
        }
        graphics.reset(bitmapCanvas);
    }
    
    public View getView() {
        return view;
    }
    
	public AndroidDisplayGraphics getGraphics() {
		return graphics;
	}
    
    //
    // CanvasUI
    //
    
    public class CanvasView extends SurfaceView implements DisplayRepaintListener {
        
        private final static int FIRST_DRAG_SENSITIVITY_X = 5;
        
        private final static int FIRST_DRAG_SENSITIVITY_Y = 5;
        
        private AndroidCanvasUI ui;
        
        private int pressedX = -FIRST_DRAG_SENSITIVITY_X;
        
        private int pressedY = -FIRST_DRAG_SENSITIVITY_Y;
        
        private Overlay overlay = null;
        
        private Matrix scale = new Matrix();

        public CanvasView(Context context, AndroidCanvasUI ui) {
            super(context);
            
            this.ui = ui;
            
            setFocusable(true);
            setFocusableInTouchMode(true);
        }
        
        public AndroidCanvasUI getUI() {
            return ui;
        }
        
        public void flushGraphics(int x, int y, int width, int height) {
            // TODO handle x, y, width and height
            if (repaintListener == null) {
                SurfaceHolder holder = getHolder();
                android.graphics.Canvas canvas = holder.lockCanvas();
                if (canvas != null) {
                    canvas.drawBitmap(bitmap, scale, null);
                    if (overlay != null) {
                        overlay.onDraw(canvas);
                    }
                    holder.unlockCanvasAndPost(canvas);
                }        	
            } else {
                repaintListener.flushGraphics();
            }
        }

        public void setOverlay(Overlay overlay) {
            this.overlay = overlay;
        }
        
        public void setScale(float sx, float sy) {
            scale.reset();
            scale.postScale(sx, sy);
        }

        //
        // View
        //
        
        @Override
        public void onDraw(android.graphics.Canvas androidCanvas) {
            MIDletAccess ma = MIDletBridge.getMIDletAccess();
            if (ma == null) {
                return;
            }
            
            graphics.reset(androidCanvas);
            graphics.setClip(0, 0, view.getWidth(), view.getHeight());
            ma.getDisplayAccess().paint(graphics);
            if (overlay != null) {
                overlay.onDraw(androidCanvas);
            }
        }   
        
		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
			
			initGraphics(w, h);
			
			AndroidDeviceDisplay deviceDisplay = (AndroidDeviceDisplay) DeviceFactory.getDevice().getDeviceDisplay();
			deviceDisplay.displayRectangleWidth = w;
			deviceDisplay.displayRectangleHeight = h;
			MIDletAccess ma = MIDletBridge.getMIDletAccess();
			if (ma == null) {
				return;
			}
			DisplayAccess da = ma.getDisplayAccess();
			if (da != null) {
				da.sizeChanged();
			}
		}

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (overlay != null && overlay.onTouchEvent(event)) {
                return true;
            }
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

        //
        // DisplayRepaintListener
        //

        @Override
        public void repaintInvoked(Object repaintObject)
        {
            onDraw(bitmapCanvas);
            Rect r = (Rect) repaintObject;
            flushGraphics(r.left, r.top, r.width(), r.height());
        }
        
        private AndroidRepaintListener repaintListener;

        public void setAndroidRepaintListener(AndroidRepaintListener repaintListener)
        {
            this.repaintListener = repaintListener;
        }
        
    }

}
