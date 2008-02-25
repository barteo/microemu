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

package org.microemu.android.device;

import javax.microedition.android.lcdui.Font;
import javax.microedition.android.lcdui.Image;

import org.microemu.device.DeviceFactory;
import org.microemu.device.MutableImage;
import org.microemu.log.Logger;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class AndroidDisplayGraphics extends javax.microedition.android.lcdui.Graphics {
	
	private Canvas canvas;
	
	private Paint paint = new Paint();
	
	private Rect clip;
	
	public AndroidDisplayGraphics(Canvas canvas, MutableImage image) {
		this.canvas = canvas;
		this.canvas.save(Canvas.CLIP_SAVE_FLAG);
		this.clip = canvas.getClipBounds();
	}
	
	public void clipRect(int x, int y, int width, int height) {
		canvas.clipRect(x, y, x + width, y + height);
		clip = canvas.getClipBounds();
	}

	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		Logger.debug("drawArc");
    }

	public void drawImage(Image img, int x, int y, int anchor) {
        int newx = x;
        int newy = y;

        if (anchor == 0) {
            anchor = javax.microedition.lcdui.Graphics.TOP | javax.microedition.lcdui.Graphics.LEFT;
        }

        if ((anchor & javax.microedition.lcdui.Graphics.RIGHT) != 0) {
            newx -= img.getWidth();
        } else if ((anchor & javax.microedition.lcdui.Graphics.HCENTER) != 0) {
            newx -= img.getWidth() / 2;
        }
        if ((anchor & javax.microedition.lcdui.Graphics.BOTTOM) != 0) {
            newy -= img.getHeight();
        } else if ((anchor & javax.microedition.lcdui.Graphics.VCENTER) != 0) {
            newy -= img.getHeight() / 2;
        }

        if (img.isMutable()) {
            canvas.drawBitmap(((AndroidMutableImage) img).getBitmap(), newx, newy, paint);
        } else {
        	canvas.drawBitmap(((AndroidImmutableImage) img).getBitmap(), newx, newy, paint);
        }
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		canvas.drawLine(x1, y1, x2, y2, paint);
	}

	public void drawRect(int x, int y, int width, int height) {
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(x, y, x + width, y + height, paint);
	}

	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		Logger.debug("drawRoundRect");
    }

	public void drawString(String str, int x, int y, int anchor) {
        int newx = x;
        int newy = y;

        if (anchor == 0) {
            anchor = javax.microedition.android.lcdui.Graphics.TOP | javax.microedition.android.lcdui.Graphics.LEFT;
        }

        if ((anchor & javax.microedition.android.lcdui.Graphics.TOP) != 0) {
            newy -= paint.getFontMetricsInt().ascent;
        } else if ((anchor & javax.microedition.android.lcdui.Graphics.BOTTOM) != 0) {
            newy -= paint.getFontMetricsInt().descent;
        }
        if ((anchor & javax.microedition.android.lcdui.Graphics.HCENTER) != 0) {
            newx -= paint.measureText(str) / 2;
        } else if ((anchor & javax.microedition.android.lcdui.Graphics.RIGHT) != 0) {
            newx -= paint.measureText(str);
        }

        canvas.drawText(str, newx, newy, AndroidFont.paint);

        // TODO
        // if ((currentFont.getStyle() & javax.microedition.android.lcdui.Font.STYLE_UNDERLINED) != 0) {
        //     g.drawLine(newx, newy + 1, newx + g.getFontMetrics().stringWidth(str), newy + 1);
        // }
	}

    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		Logger.debug("fillArc");
    }

	public void fillRect(int x, int y, int width, int height) {
		paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(x, y, x + width, y + height, paint);
	}

	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		Logger.debug("fillRoundRect");
    }

	public int getClipHeight() {
		return clip.bottom - clip.top;
	}

	public int getClipWidth() {
		return clip.right - clip.left;
	}

	public int getClipX() {
		return clip.left;
	}

	public int getClipY() {
		return clip.top;
	}

	public int getColor() {
		return paint.getColor();
	}

	public Font getFont() {
		Logger.debug("getFont");

		return null;
	}

	public void setClip(int x, int y, int width, int height) {
		canvas.restore();
		canvas.save(Canvas.CLIP_SAVE_FLAG);
        clip.left = x;
        clip.top = y;
        clip.right = x + width;
        clip.bottom = y + height;
		canvas.clipRect(clip);
	}

	public void setColor(int RGB) {
		paint.setColor(0xff000000 | RGB);
		// TODO AndroidFont.paint cannot be static 
		AndroidFont.paint.setColor(0xff000000 | RGB);
	}

	public void setFont(Font font) {
		// TODO
		// Logger.debug("(todo) setFont");
	}

    public void translate(int x, int y) {
        super.translate(x, y);
        canvas.translate(x, y);
    }

	public void drawRegion(Image src, int x_src, int y_src, int width,
			int height, int transform, int x_dst, int y_dst, int anchor) {
        // may throw NullPointerException, this is ok
        if (x_src + width > src.getWidth() || y_src + height > src.getHeight() || width < 0 || height < 0 || x_src < 0
                || y_src < 0)
            throw new IllegalArgumentException("Area out of Image");

        // this cannot be done on the same image we are drawing
        // check this if the implementation of getGraphics change so
        // as to return different Graphic Objects on each call to
        // getGraphics
        if (src.isMutable() && src.getGraphics() == this)
            throw new IllegalArgumentException("Image is source and target");

        Bitmap img;

        if (src.isMutable()) {
            img = ((AndroidMutableImage) src).getBitmap();
        } else {
            img = ((AndroidImmutableImage) src).getBitmap();
        }            

// TODO  
        if (anchor != 0) {
//        	Logger.debug("(todo) drawRegion: " + anchor);
        }
/*        java.awt.geom.AffineTransform t = new java.awt.geom.AffineTransform();

        int dW = width, dH = height;
        switch (transform) {
        case Sprite.TRANS_NONE: {
            break;
        }
        case Sprite.TRANS_ROT90: {
            t.translate((double) height, 0);
            t.rotate(Math.PI / 2);
            dW = height;
            dH = width;
            break;
        }
        case Sprite.TRANS_ROT180: {
            t.translate(width, height);
            t.rotate(Math.PI);
            break;
        }
        case Sprite.TRANS_ROT270: {
            t.translate(0, width);
            t.rotate(Math.PI * 3 / 2);
            dW = height;
            dH = width;
            break;
        }
        case Sprite.TRANS_MIRROR: {
            t.translate(width, 0);
            t.scale(-1, 1);
            break;
        }
        case Sprite.TRANS_MIRROR_ROT90: {
            t.translate((double) height, 0);
            t.rotate(Math.PI / 2);
            t.translate((double) width, 0);
            t.scale(-1, 1);
            dW = height;
            dH = width;
            break;
        }
        case Sprite.TRANS_MIRROR_ROT180: {
            t.translate(width, 0);
            t.scale(-1, 1);
            t.translate(width, height);
            t.rotate(Math.PI);
            break;
        }
        case Sprite.TRANS_MIRROR_ROT270: {
            t.rotate(Math.PI * 3 / 2);
            t.scale(-1, 1);
            dW = height;
            dH = width;
            break;
        }
        default:
            throw new IllegalArgumentException("Bad transform");
        }

        // process anchor and correct x and y _dest
        // vertical
        boolean badAnchor = false;

        if (anchor == 0) {
            anchor = TOP | LEFT;
        }

        if ((anchor & 0x7f) != anchor || (anchor & BASELINE) != 0)
            badAnchor = true;

        if ((anchor & TOP) != 0) {
            if ((anchor & (VCENTER | BOTTOM)) != 0)
                badAnchor = true;
        } else if ((anchor & BOTTOM) != 0) {
            if ((anchor & VCENTER) != 0)
                badAnchor = true;
            else {
                y_dst -= dH - 1;
            }
        } else if ((anchor & VCENTER) != 0) {
            y_dst -= (dH - 1) >>> 1;
        } else {
            // no vertical anchor
            badAnchor = true;
        }

        // horizontal
        if ((anchor & LEFT) != 0) {
            if ((anchor & (HCENTER | RIGHT)) != 0)
                badAnchor = true;
        } else if ((anchor & RIGHT) != 0) {
            if ((anchor & HCENTER) != 0)
                badAnchor = true;
            else {
                x_dst -= dW - 1;
            }
        } else if ((anchor & HCENTER) != 0) {
            x_dst -= (dW - 1) >>> 1;
        } else {
            // no horizontal anchor
            badAnchor = true;
        }

        if (badAnchor)
            throw new IllegalArgumentException("Bad Anchor");

        java.awt.geom.AffineTransform savedT = g.getTransform();

        g.translate(x_dst, y_dst);
        g.transform(t);*/

        Rect srcRect = new Rect(x_src, y_src, x_src + width, y_src + height);
        Rect dstRect = new Rect(x_dst, y_dst, x_dst + width, y_dst + height);
        canvas.drawBitmap(img, srcRect, dstRect, paint);

        // return to saved
// TODO        
//        g.setTransform(savedT);
	}

	public void drawRGB(int[] rgbData, int offset, int scanlength, int x,
			int y, int width, int height, boolean processAlpha) {
        // this is less than ideal in terms of memory
        // but it's the easiest way

        if (rgbData == null)
            throw new NullPointerException();

        if (width == 0 || height == 0) {
            return;
        }

        int l = rgbData.length;

        if (width < 0 || height < 0 || offset < 0 || offset >= l || (scanlength < 0 && scanlength * (height - 1) < 0)
                || (scanlength >= 0 && scanlength * (height - 1) + width - 1 >= l))
            throw new ArrayIndexOutOfBoundsException();

        int[] rgb = new int[width * height];
        // this way we dont create yet another array in createImage
        int transparencyMask = processAlpha ? 0 : 0xff000000;
        for (int row = 0; row < height; row++) {
            for (int px = 0; px < width; px++)
                rgb[row * width + px] = rgbData[offset + px] | transparencyMask;
            offset += scanlength;
        }

        // help gc
        rgbData = null;
        Image img = DeviceFactory.getDevice().getDeviceDisplay().createRGBImage(rgb, width, height, true);
        drawImage(img, x, y, TOP | LEFT);
	}

	public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
		Logger.debug("fillTriangle");
	}

	public void copyArea(int x_src, int y_src, int width, int height,
			int x_dest, int y_dest, int anchor) {
		Logger.debug("copyArea");
	}

	public int getDisplayColor(int color) {
		Logger.debug("getDisplayColor");

		return -1;
	}
	
}
