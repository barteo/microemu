/**
 *  MicroEmulator
 *  Copyright (C) 2008 Markus Heberling <markus@heberling.net>
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
package org.microemu.iphone.device.ui;

import static joc.Static.YES;

import java.awt.event.KeyEvent;

import javax.microedition.lcdui.Canvas;

import joc.Message;
import joc.Pointer;
import joc.Runtime;
import joc.Selector;
import joc.Static;
import obc.CGContext;
import obc.CGImage;
import obc.CGPoint;
import obc.CGRect;
import obc.NSArray;
import obc.NSCFSet;
import obc.NSConcreteNotification;
import obc.NSNotificationCenter;
import obc.UIColor;
import obc.UIEvent;
import obc.UITextField;
import obc.UIToolbar;
import obc.UITouch;
import obc.UIView;

import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.device.ui.CanvasUI;
import org.microemu.iphone.MicroEmulator;
import org.microemu.iphone.device.IPhoneMutableImage;

import straptease.CoreGraphics;

public class IPhoneCanvasUI extends AbstractUI<Canvas> implements CanvasUI {

	enum Touch {
		BEGIN, END, DRAG
	}

	private final class CanvasView extends UIView {
		private final Canvas canvas;
		private UITextField keyboardHandler;
		private String oldText = "";
		private boolean keybordVisible=false;
		private IPhoneMutableImage offscreen;

		private CanvasView(Canvas canvas) {
			this.canvas = canvas;
			setMultipleTouchEnabled$(YES);
			setUserInteractionEnabled$(YES);
			setClearsContextBeforeDrawing$(Static.NO);
//			setClearsContext$(Static.NO);
			setBackgroundColor$(UIColor.$clearColor());
			
			offscreen=new IPhoneMutableImage(canvas.getWidth(), canvas.getHeight());
			
			System.out.println(clearsContextBeforeDrawing());
		}
		
		@Override
		public UIView initWithFrame$(CGRect arg0) {
			UIView view = super.initWithFrame$(arg0);
			keyboardHandler=new UITextField().initWithFrame$(new CGRect(1000, 1000, 100, 100));
//			System.out.println("CanvasView.initWithFrame$(1)");
//			Runtime.msgSend(keyboardHandler, UITextInputTraits.class, "setKeyboardType:", 5);
//			System.out.println("CanvasView.initWithFrame$(2)");
			
			((NSNotificationCenter)NSNotificationCenter.$defaultCenter()).addObserver$selector$name$object$(this, new Selector("keyboardHandlerChanged"), "UITextFieldTextDidChangeNotification", keyboardHandler);
			addSubview$(keyboardHandler);
			return view;
		}

		@Message
		public void keyboardHandlerChanged(NSConcreteNotification object) {
			MIDletAccess ma = MIDletBridge.getMIDletAccess();
			if (ma == null) {
				return;
			}
			String text=keyboardHandler.text().toString();
			System.out.println(oldText+" -> "+text);
			int key;
			if(text.length()>oldText.length()){
				key=text.charAt(text.length()-1);
			} else {
				//removed one, so backspace
				key=KeyEvent.VK_BACK_SPACE;
			}
			ma.getDisplayAccess().keyPressed(key);
			ma.getDisplayAccess().keyReleased(key);
			oldText=text;
		}
		
		@Override
		@Message
		public void drawRect$(CGRect arg0) {
			System.out.println("drawRect: " + canvas + " " + this);
			MIDletAccess ma = MIDletBridge.getMIDletAccess();
			if (ma == null) {
				return;
			}
			Pointer<CGContext> context = CoreGraphics.UIGraphicsGetCurrentContext();
			CoreGraphics.CGContextTranslateCTM(context, 0, canvas.getHeight());
			CoreGraphics.CGContextScaleCTM(context, 1.0f, -1.0f);
//			CoreGraphics.CGContextSaveGState(context);
//			Graphics g = new IPhoneDisplayGraphics(context, canvas.getWidth(), canvas.getHeight(), false);
			ma.getDisplayAccess().paint(offscreen.getGraphics());
		
			Pointer<CGRect> rect=CoreGraphics.CGRectMake(0, 0, offscreen.getWidth(), offscreen.getHeight());
	        Pointer<CGImage> bitmap = offscreen.getBitmap();
			CoreGraphics.CGContextDrawImage(context, rect,  bitmap);
			CoreGraphics.CGImageRelease(bitmap);
			
//			g.drawString("XXX", 100, 100, 0);
//			CoreGraphics.CGContextRestoreGState(context);
		}

		@Override
		@Message
		public void touchesBegan$withEvent$(Object arg0, Object arg1) {
			MIDletAccess ma = MIDletBridge.getMIDletAccess();
			if (ma == null) {
				return;
			}

			UIEvent event = (UIEvent) arg1;
			// System.out.println("Event: "+event);
			// System.out.println("Touches: "+event.allTouches());
			NSCFSet touches = (NSCFSet) event.allTouches();

			if (touches.count() > 1) {
				handleMultiTouch(ma, touches, Touch.BEGIN);
			} else {
				UITouch touch = (UITouch) touches.anyObject();
				// System.out.println(touch);
				// System.out.println(touch.locationInView$(this));
				CGPoint point = touch.locationInView$(this);

				ma.getDisplayAccess().pointerPressed((int) point.x, (int) point.y);
			}
		}

		@Override
		@Message
		public void touchesEnded$withEvent$(Object arg0, Object arg1) {
			MIDletAccess ma = MIDletBridge.getMIDletAccess();
			if (ma == null) {
				return;
			}

			UIEvent event = (UIEvent) arg1;
			// System.out.println("Event: "+event);
			// System.out.println("Touches: "+event.allTouches());
			NSCFSet touches = (NSCFSet) event.allTouches();

			if (touches.count() > 1) {
				handleMultiTouch(ma, touches, Touch.END);
			} else {
				UITouch touch = (UITouch) touches.anyObject();
				// System.out.println(touch);
				// System.out.println(touch.locationInView$(this));
				CGPoint point = touch.locationInView$(this);

				ma.getDisplayAccess().pointerReleased((int) point.x, (int) point.y);
			}
		}

		@Override
		@Message
		public void touchesMoved$withEvent$(Object arg0, Object arg1) {
			MIDletAccess ma = MIDletBridge.getMIDletAccess();
			if (ma == null) {
				return;
			}

			UIEvent event = (UIEvent) arg1;

			// System.out.println("Event: "+event);
			// System.out.println("Touches: "+event.allTouches());
			NSCFSet touches = (NSCFSet) event.allTouches();

			if (touches.count() > 1) {
				handleMultiTouch(ma, touches, Touch.DRAG);
			} else {
				UITouch touch = (UITouch) touches.anyObject();
				// System.out.println(touch);
				// System.out.println(touch.locationInView$(this));
				CGPoint point = touch.locationInView$(this);
				
				ma.getDisplayAccess().pointerDragged((int) point.x, (int) point.y);
			}
		}

		private CGPoint moveFrom;

		private void handleMultiTouch(MIDletAccess ma, NSCFSet touches, Touch type) {
			System.out.println("CanvasView.handleMultiTouch(" + type + ")");
			if (touches.count() == 2) {
				UITouch touch1 = (UITouch) ((NSArray) touches.allObjects()).objectAtIndex$(0);
				UITouch touch2 = (UITouch) ((NSArray) touches.allObjects()).objectAtIndex$(1);
				CGPoint touchPoint = new CGPoint((touch1.locationInView$(view).x + touch2.locationInView$(view).x) / 2,
						(touch1.locationInView$(view).y + touch2.locationInView$(view).y) / 2);
				if (type == Touch.BEGIN) {
					moveFrom = touchPoint;
				} else if (type == Touch.END && moveFrom != null) {
					Integer key = null;
					CGPoint from = moveFrom;
					moveFrom = null;
					CGPoint to = touchPoint;
					System.out.println(from.x + "," + from.y + " -> " + to.x + "," + to.y);
					float diffX = to.x - from.x;
					float diffY = to.y - from.y;
					if (Math.abs(diffX) > Math.abs(diffY) + 10) {
						if (diffX > 0)
							key = Canvas.RIGHT;
						else if (diffX < 0)
							key = Canvas.LEFT;
					} else if (Math.abs(diffY) > Math.abs(diffX) + 10) {
						if (diffY > 0)
							key = Canvas.DOWN;
						else if (diffY < 0)
							key = Canvas.UP;
					} else {
						key = Canvas.FIRE;
					}

					if (key != null) {
						System.out.println("Pressing: " + key);
						ma.getDisplayAccess().keyPressed(key);
						ma.getDisplayAccess().keyReleased(key);
					}
				}
			} else if (touches.count() == 3 && type == Touch.END) {
				if (keybordVisible) {
					System.out.println("Hide Keyboard");
					Runtime.msgSend(keyboardHandler, UITextField.class, "resignFirstResponder");
					// view.resignFirstResponder();
				} else {
					System.out.println("Show Keyboard");
					Runtime.msgSend(keyboardHandler, UITextField.class, "becomeFirstResponder");
					// view.becomeFirstResponder();
				}
				keybordVisible=!keybordVisible;
			}
		}
	}

	private UIView canvasView;

	private UIView view;

	public IPhoneCanvasUI(MicroEmulator microEmulator, final Canvas canvas) {
		super(microEmulator, canvas);
	}

	public void hideNotify() {
		// TODO Auto-generated method stub

	}

	public void invalidate() {
		// TODO Auto-generated method stub

	}

	public void showNotify() {
		if (view == null) {
			canvasView = new CanvasView(displayable).initWithFrame$(microEmulator.getWindow().bounds());

			view = new UIView().initWithFrame$(microEmulator.getWindow().bounds());
			// tableView = new UITableView().initWithFrame$style$(
			// new CGRect(0, 0, microEmulator.getWindow().bounds().size.width,
			// microEmulator.getWindow().bounds().size.height - 40), 0);
			view.addSubview$(canvasView);
			toolbar = (UIToolbar) new UIToolbar().initWithFrame$(new CGRect(0,
					microEmulator.getWindow().bounds().size.height - TOOLBAR_HEIGHT,
					microEmulator.getWindow().bounds().size.width, TOOLBAR_HEIGHT));
			view.addSubview$(toolbar);
			updateToolbar();
		}
		microEmulator.getWindow().addSubview$(view);
	}

	public UIView getCanvasView() {
		return canvasView;
	}

}
