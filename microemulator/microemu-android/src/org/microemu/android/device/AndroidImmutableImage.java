package org.microemu.android.device;

import android.graphics.Bitmap;

public class AndroidImmutableImage extends javax.microedition.android.lcdui.Image {

	private Bitmap bitmap;
	
	public AndroidImmutableImage(Bitmap bitmap) {
		this.bitmap = bitmap;
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
