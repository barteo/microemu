package org.microemu.android.device.ui;

import javax.microedition.lcdui.Graphics;

import org.microemu.CustomItemAccess;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.android.MicroEmulatorActivity;
import org.microemu.android.device.AndroidDisplayGraphics;
import org.microemu.device.ui.CustomItemUI;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AndroidCustomItemUI extends LinearLayout implements CustomItemUI {

	private MicroEmulatorActivity activity;
	
	private CustomItemAccess customItemAccess;
	
	private TextView labelView;
	
	private CanvasView view;
	
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
				TypedArray a = labelView.getContext().obtainStyledAttributes(android.R.styleable.Theme);
				labelView.setTextAppearance(labelView.getContext(), a.getResourceId(android.R.styleable.Theme_textAppearanceLarge, -1));
				addView(labelView);
				
				view = new CanvasView(activity);
				addView(view);
				
				setLabel(customItemAccess.getCustomItem().getLabel());
			}
		});
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
		
		public CanvasView(Context context) {
			super(context);
			
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
			Graphics g = new AndroidDisplayGraphics(androidCanvas);
			Rect r = androidCanvas.getClipBounds();
			g.clipRect(r.left, r.top, r.width(), r.height());
			int suggestedHeight = customItemAccess.getPrefContentHeight(-1);
			customItemAccess.paint(g, r.width(), suggestedHeight);
		}	
		
	}

}
