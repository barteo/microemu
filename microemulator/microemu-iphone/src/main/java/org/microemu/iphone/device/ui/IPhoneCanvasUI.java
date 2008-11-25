package org.microemu.iphone.device.ui;

import static joc.Static.YES;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

import joc.Message;
import obc.CGPoint;
import obc.CGRect;
import obc.NSCFSet;
import obc.UIEvent;
import obc.UIToolbar;
import obc.UITouch;
import obc.UIView;

import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.device.ui.CanvasUI;
import org.microemu.iphone.MicroEmulator;
import org.microemu.iphone.device.IPhoneDisplayGraphics;

import straptease.CoreGraphics;

public class IPhoneCanvasUI extends AbstractUI implements CanvasUI {

	private final class CanvasView extends UIView {
		private final Canvas canvas;

		private CanvasView(Canvas canvas) {
			this.canvas = canvas;
			setUserInteractionEnabled$(YES);
		}

		@Override
		@Message
		public void drawRect$(CGRect arg0) {
			System.out.println("drawRect"+canvas+" "+this);
			MIDletAccess ma = MIDletBridge.getMIDletAccess();
			if (ma == null) {
				return;
			}
			Graphics g = new IPhoneDisplayGraphics(CoreGraphics.UIGraphicsGetCurrentContext(), canvas.getWidth(),
					canvas.getHeight());
			ma.getDisplayAccess().paint(g);
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
			UITouch touch = (UITouch) ((NSCFSet) event.allTouches()).anyObject();
			// System.out.println(touch);
			// System.out.println(touch.locationInView$(this));
			CGPoint point = touch.locationInView$(this);

			
			ma.getDisplayAccess().pointerPressed((int)point.x, canvas.getHeight()-(int)point.y);
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
			UITouch touch = (UITouch) ((NSCFSet) event.allTouches()).anyObject();
			// System.out.println(touch);
			// System.out.println(touch.locationInView$(this));
			CGPoint point = touch.locationInView$(this);

			
			ma.getDisplayAccess().pointerReleased((int)point.x, canvas.getHeight()-(int)point.y);
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
			UITouch touch = (UITouch) ((NSCFSet) event.allTouches()).anyObject();
			// System.out.println(touch);
			// System.out.println(touch.locationInView$(this));
			CGPoint point = touch.locationInView$(this);

			
			ma.getDisplayAccess().pointerDragged((int)point.x, canvas.getHeight()-(int)point.y);
		}
	}

	private UIView canvasView;
	
	private UIView view;

	public IPhoneCanvasUI(MicroEmulator microEmulator, final Canvas canvas) {
		super(microEmulator, canvas);
		canvasView = new CanvasView(canvas).initWithFrame$(microEmulator.getWindow().bounds());
	}

	public void hideNotify() {
		// TODO Auto-generated method stub

	}

	public void invalidate() {
		// TODO Auto-generated method stub

	}

	public void showNotify() {
		if (view==null) {
			view = new UIView().initWithFrame$(microEmulator.getWindow().bounds());
//			tableView = new UITableView().initWithFrame$style$(
//					new CGRect(0, 0, microEmulator.getWindow().bounds().size.width,
//							microEmulator.getWindow().bounds().size.height - 40), 0);
			view.addSubview$(canvasView);
			toolbar = (UIToolbar) new UIToolbar().initWithFrame$(new CGRect(0,
					microEmulator.getWindow().bounds().size.height - TOOLBAR_HEIGHT, microEmulator.getWindow().bounds().size.width,
					TOOLBAR_HEIGHT));
			view.addSubview$(toolbar);
			updateToolbar();
		}
		microEmulator.getWindow().addSubview$(view);
	}

	public UIView getCanvasView() {
		return canvasView;
	}

}
