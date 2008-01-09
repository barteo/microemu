package org.microemu.android.device;

import javax.microedition.android.lcdui.Graphics;

import org.microemu.device.MutableImage;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class AndroidMutableImage extends MutableImage {
	
	private Bitmap bitmap;
	
	public AndroidMutableImage(int width, int height) {
		bitmap = Bitmap.createBitmap(width, height, false);
	}

	@Override
	public int[] getData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graphics getGraphics() {
        Canvas canvas = new Canvas(bitmap);
        canvas.clipRect(0, 0, getWidth(), getHeight());
        AndroidDisplayGraphics displayGraphics = new AndroidDisplayGraphics(canvas, this);
		displayGraphics.setColor(0x00000000);
		displayGraphics.translate(-displayGraphics.getTranslateX(), -displayGraphics.getTranslateY());
		
		return displayGraphics;
	}

	@Override
	public boolean isMutable() {
		return true;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	@Override
	public int getWidth() {
		return bitmap.width();
	}
	
	@Override
	public int getHeight() {
		return bitmap.height();
	}

	@Override
    public void getRGB(int []argb, int offset, int scanlength,
            int x, int y, int width, int height) {

        if (width <= 0 || height <= 0)
            return;
        if (x < 0 || y < 0 || x + width > getWidth() || y + height > getHeight())
            throw new IllegalArgumentException("Specified area exceeds bounds of image");
        if ((scanlength < 0? -scanlength:scanlength) < width)
            throw new IllegalArgumentException("abs value of scanlength is less than width");
        if (argb == null)
            throw new NullPointerException("null rgbData");
        if (offset < 0 || offset + width > argb.length)
            throw new ArrayIndexOutOfBoundsException();
        if (scanlength < 0) {
            if (offset + scanlength*(height-1) < 0)
                throw new ArrayIndexOutOfBoundsException();
        } else {
            if (offset + scanlength*(height-1) + width > argb.length)
                throw new ArrayIndexOutOfBoundsException();
        }

        bitmap.getPixels(argb, offset, scanlength, x, y, width, height);

/*        for (int i = 0; i < argb.length; i++) {
		    int a = (argb[i] & 0xFF000000);
		    int b = (argb[i] & 0x00FF0000) >>> 16;
		    int g = (argb[i] & 0x0000FF00) >>> 8;
		    int r = (argb[i] & 0x000000FF);
	
		    argb[i] = a | (r << 16) | (g << 8) | b;
        }*/
	}
	
}
