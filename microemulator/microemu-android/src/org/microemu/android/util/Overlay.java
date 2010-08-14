package org.microemu.android.util;

import android.graphics.Canvas;
import android.view.MotionEvent;

public abstract class Overlay
{

    public abstract void onDraw(Canvas canvas);

    public abstract boolean onTouchEvent(MotionEvent event);
    
}
