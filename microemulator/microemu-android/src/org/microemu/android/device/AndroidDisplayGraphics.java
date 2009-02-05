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

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import org.microemu.log.Logger;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class AndroidDisplayGraphics extends javax.microedition.lcdui.Graphics {
	
	private Canvas canvas;
	
	private Paint paint = new Paint();
	
	private Rect clip;
	
	private Font font;
	
	public AndroidDisplayGraphics(Canvas canvas) {
		this.canvas = canvas;
		this.canvas.save(Canvas.CLIP_SAVE_FLAG);
		this.clip = canvas.getClipBounds();
		setFont(Font.getDefaultFont());
	}
	
	public void clipRect(int x, int y, int width, int height) {
		canvas.clipRect(x, y, x + width, y + height);
		clip = canvas.getClipBounds();
	}

	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
	   	paint.setStyle(Paint.Style.STROKE);
	    RectF rect = new RectF(x, y, x + width, y + height);
	    canvas.drawArc(rect, startAngle, arcAngle, false, paint);
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
		if (x1 > x2) {
			x1++;
		} else {
			x2++;
		}
		if (y1 > y2) {
			y1++;
		} else {
			y2++;
		}

		canvas.drawLine(x1, y1, x2, y2, paint);
	}

	public void drawRect(int x, int y, int width, int height) {
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(x, y, x + width, y + height, paint);
	}

	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		Logger.debug("TODO drawRoundRect");
    }

	public void drawString(String str, int x, int y, int anchor) {
        int newx = x;
        int newy = y;

        if (anchor == 0) {
            anchor = javax.microedition.lcdui.Graphics.TOP | javax.microedition.lcdui.Graphics.LEFT;
        }
        
        AndroidFont androidFont = AndroidFontManager.getFont(font);

        if ((anchor & javax.microedition.lcdui.Graphics.TOP) != 0) {
            newy -= androidFont.paint.getFontMetricsInt().ascent;
        } else if ((anchor & javax.microedition.lcdui.Graphics.BOTTOM) != 0) {
            newy -= androidFont.paint.getFontMetricsInt().descent;
        }
        if ((anchor & javax.microedition.lcdui.Graphics.HCENTER) != 0) {
            newx -= androidFont.paint.measureText(str) / 2;
        } else if ((anchor & javax.microedition.lcdui.Graphics.RIGHT) != 0) {
            newx -= androidFont.paint.measureText(str);
        }

        androidFont.paint.setColor(paint.getColor());
        canvas.drawText(str, newx, newy, androidFont.paint);
	}

    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
    	paint.setStyle(Paint.Style.FILL);
	    RectF rect = new RectF(x, y, x + width, y + height);
	    canvas.drawArc(rect, startAngle, arcAngle, false, paint);
    }

	public void fillRect(int x, int y, int width, int height) {
		paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(x, y, x + width, y + height, paint);
	}

	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		Logger.debug("TODO fillRoundRect");
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
		return font;
	}

	public void setClip(int x, int y, int width, int height) {
		if (x == clip.left && x+ width == clip.right && y == clip.top && y + height == clip.bottom) {
			return;
		}
		if (x < clip.left || x + width > clip.right || y < clip.top || y + height > clip.bottom) {
			canvas.restore();
			canvas.save(Canvas.CLIP_SAVE_FLAG);
		}
        clip.left = x;
        clip.top = y;
        clip.right = x + width;
        clip.bottom = y + height;
		canvas.clipRect(clip);
	}

	public void setColor(int RGB) {
		paint.setColor(0xff000000 | RGB);
	}

	public void setFont(Font font) {
		this.font = font;
	}

    public void translate(int x, int y) {
        super.translate(x, y);
        canvas.translate(x, y);
        clip.left -= x;
        clip.right -= x;
        clip.top -= y;
        clip.bottom -= y;
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

        Matrix matrix = new Matrix();
        int dW = width, dH = height;
        switch (transform) {
        case Sprite.TRANS_NONE: {
            break;
        }
        case Sprite.TRANS_ROT90: {
        	matrix.preRotate(90);
        	img = Bitmap.createBitmap(img, x_src, y_src, width, height, matrix, true);
            dW = height;
            dH = width;
            break;
        }
        case Sprite.TRANS_ROT180: {
            matrix.preRotate(180);
        	img = Bitmap.createBitmap(img, x_src, y_src, width, height, matrix, true);
            break;
        }
        case Sprite.TRANS_ROT270: {
            matrix.preRotate(270);
            img = Bitmap.createBitmap(img, x_src, y_src, width, height, matrix, true);
            dW = height;
            dH = width;
            break;
        }
        case Sprite.TRANS_MIRROR: {
        	// TODO
            break;
        }
        case Sprite.TRANS_MIRROR_ROT90: {
        	// TODO
            dW = height;
            dH = width;
            break;
        }
        case Sprite.TRANS_MIRROR_ROT180: {
        	// TODO
            break;
        }
        case Sprite.TRANS_MIRROR_ROT270: {
        	// TODO
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

        if (badAnchor) {
            throw new IllegalArgumentException("Bad Anchor");
        }
            
        Rect srcRect = new Rect(x_src, y_src, x_src + width, y_src + height);
        Rect dstRect = new Rect(x_dst, y_dst, x_dst + width, y_dst + height);
        canvas.drawBitmap(img, srcRect, dstRect, paint);
	}

	public void drawRGB(int[] rgbData, int offset, int scanlength, int x,
			int y, int width, int height, boolean processAlpha) {
        if (rgbData == null)
            throw new NullPointerException();

        if (width == 0 || height == 0) {
            return;
        }

        int l = rgbData.length;
        if (width < 0 || height < 0 || offset < 0 || offset >= l || (scanlength < 0 && scanlength * (height - 1) < 0)
                || (scanlength >= 0 && scanlength * (height - 1) + width - 1 >= l)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        
        // FIXME MIDP allows almost any value of scanlength, drawBitmap is more strict with the stride
        if (scanlength == 0) {
        	scanlength = width;
        }
       	canvas.drawBitmap(rgbData, offset, scanlength, x, y, width, height, processAlpha, paint);
	}

	public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
		Logger.debug("TODO fillTriangle");
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
