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
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

public class AndroidDisplayGraphics extends javax.microedition.lcdui.Graphics {
	
    public Paint strokePaint = new Paint();
    
    public Paint fillPaint = new Paint();
    
    public AndroidFont androidFont;
    
	private static final DashPathEffect dashPathEffect = new DashPathEffect(new float[] { 5, 5 }, 0);
	
	private Canvas canvas;
	
    private GraphicsDelegate delegate;

	private Rect clip;
	
	private Font font;
	
	private int strokeStyle = SOLID;
	
	private Rect tmpRect = new Rect();
	
    private Rect tmpRectSecond = new Rect();
    
    private Matrix tmpMatrix = new Matrix();
    
	public AndroidDisplayGraphics() {
        this.delegate = null;
        
		strokePaint.setAntiAlias(true);
		strokePaint.setStyle(Paint.Style.STROKE);
		fillPaint.setAntiAlias(true);
		fillPaint.setStyle(Paint.Style.FILL);
	}
	
    public AndroidDisplayGraphics(Bitmap bitmap) {
        this.canvas = new Canvas(bitmap);
        this.canvas.clipRect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        this.delegate = null;
        
		strokePaint.setAntiAlias(true);
		strokePaint.setStyle(Paint.Style.STROKE);
		fillPaint.setAntiAlias(true);
		fillPaint.setStyle(Paint.Style.FILL);
        
        reset(this.canvas);
    }
	
	public void reset(Canvas canvas) {
	    this.canvas = canvas;
	    
		Rect tmp = this.canvas.getClipBounds();
		this.canvas.setMatrix(null);
		// setMatrix changes the clipping too
		this.canvas.clipRect(tmp, Region.Op.REPLACE);
		clip = this.canvas.getClipBounds();
		setFont(Font.getDefaultFont());
	}
	
	public Canvas getCanvas() {
		return canvas;
	}
	
    public void setDelegate(GraphicsDelegate delegate) {
        this.delegate = delegate;
    }

	public void clipRect(int x, int y, int width, int height) {
		canvas.clipRect(x, y, x + width, y + height);
		clip = canvas.getClipBounds();
		
        if (delegate != null) {
            delegate.setClip(clip.left, clip.top, clip.right - clip.left, clip.bottom - clip.top);
        }
	}

	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
	    RectF rect = new RectF(x, y, x + width, y + height);
	    canvas.drawArc(rect, -startAngle, -arcAngle, false, strokePaint);
    }

	public void drawImage(Image img, int x, int y, int anchor) {
        if (delegate != null) {
            delegate.drawImage(img, x, y, anchor);
        } else {
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
                canvas.drawBitmap(((AndroidMutableImage) img).getBitmap(), newx, newy, strokePaint);
            } else {
                canvas.drawBitmap(((AndroidImmutableImage) img).getBitmap(), newx, newy, strokePaint);
            }
        }
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
        if (delegate != null) {
            delegate.drawLine(x1, y1, x2, y2);
        } else {
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
    
    		canvas.drawLine(x1, y1, x2, y2, strokePaint);
        }
	}

	public void drawRect(int x, int y, int width, int height) {
	    if (delegate != null) {
	        delegate.drawRect(x, y, width, height);
	    } else {
	        canvas.drawRect(x, y, x + width, y + height, strokePaint);
	    }
	}

	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		canvas.drawRoundRect(new RectF(x, y, x + width, y + height), (float) arcWidth, (float) arcHeight, strokePaint);
   }

	public void drawString(String str, int x, int y, int anchor) {
		drawSubstring(str, 0, str.length(), x, y, anchor);
	}

	public void drawSubstring(String str, int offset, int len, int x, int y, int anchor) {
        int newx = x;
        int newy = y;

        if (anchor == 0) {
            anchor = javax.microedition.lcdui.Graphics.TOP | javax.microedition.lcdui.Graphics.LEFT;
        }
        
        if ((anchor & javax.microedition.lcdui.Graphics.TOP) != 0) {
            newy -= androidFont.metrics.ascent;
        } else if ((anchor & javax.microedition.lcdui.Graphics.BOTTOM) != 0) {
            newy -= androidFont.metrics.descent;
        }
        if ((anchor & javax.microedition.lcdui.Graphics.HCENTER) != 0) {
            newx -= androidFont.paint.measureText(str) / 2;
        } else if ((anchor & javax.microedition.lcdui.Graphics.RIGHT) != 0) {
            newx -= androidFont.paint.measureText(str);
        }

        androidFont.paint.setColor(strokePaint.getColor());

        if (delegate != null) {
	        delegate.drawSubstringDelegate(str, offset, len, newx, newy, anchor);
	    } else {    
            canvas.drawText(str, offset, len + offset, newx, newy, androidFont.paint);
	    }
	}

    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
	    RectF rect = new RectF(x, y, x + width, y + height);
	    canvas.drawArc(rect, -startAngle, -arcAngle, true, fillPaint);
    }

	public void fillRect(int x, int y, int width, int height) {
        if (delegate != null) {
            delegate.fillRect(x, y, width, height);
        } else {
            canvas.drawRect(x, y, x + width, y + height, fillPaint);
        }
	}

	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		canvas.drawRoundRect(new RectF(x, y, x + width, y + height), (float) arcWidth, (float) arcHeight, fillPaint);
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
		return strokePaint.getColor();
	}

	public Font getFont() {
		return font;
	}
	
	public int getStrokeStyle() {
		return strokeStyle;
	}

	public void setClip(int x, int y, int width, int height) {
		if (x == clip.left && x + width == clip.right && y == clip.top && y + height == clip.bottom) {
			return;
		}

        if (delegate != null) {
            delegate.setClip(x, y, width, height);
        }

        clip.left = x;
        clip.top = y;
        clip.right = x + width;
        clip.bottom = y + height;
		canvas.clipRect(clip, Region.Op.REPLACE);
	}

	public void setColor(int RGB) {
		strokePaint.setColor(0xff000000 | RGB);
		fillPaint.setColor(0xff000000 | RGB);
	}

	public void setFont(Font font) {
		this.font = font;
		
        androidFont = AndroidFontManager.getFont(font);

	}
	
	public void setStrokeStyle(int style) {
		if (style != SOLID && style != DOTTED) {
			throw new IllegalArgumentException();
		}
		
		this.strokeStyle = style;
		
		if (style == SOLID) {
			strokePaint.setPathEffect(null);
			fillPaint.setPathEffect(null);
		} else { // DOTTED
			strokePaint.setPathEffect(dashPathEffect);
			fillPaint.setPathEffect(dashPathEffect);
		}
	}

    public void translate(int x, int y) {
        if (delegate != null) {
            delegate.translate(x, y);
        } else {
            canvas.translate(x, y);
        }
            
        super.translate(x, y);
            
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

        tmpMatrix.reset();
        int dW = width, dH = height;
        switch (transform) {
        case Sprite.TRANS_NONE: {
            break;
        }
        case Sprite.TRANS_ROT90: {
            tmpMatrix.preRotate(90);
        	img = Bitmap.createBitmap(img, x_src, y_src, width, height, tmpMatrix, true);
            dW = height;
            dH = width;
            break;
        }
        case Sprite.TRANS_ROT180: {
            tmpMatrix.preRotate(180);
        	img = Bitmap.createBitmap(img, x_src, y_src, width, height, tmpMatrix, true);
            break;
        }
        case Sprite.TRANS_ROT270: {
            tmpMatrix.preRotate(270);
            img = Bitmap.createBitmap(img, x_src, y_src, width, height, tmpMatrix, true);
            dW = height;
            dH = width;
            break;
        }
        case Sprite.TRANS_MIRROR: {
            tmpMatrix.preScale(-1, 1);
        	img = Bitmap.createBitmap(img, x_src, y_src, width, height, tmpMatrix, true);
            break;
        }
        case Sprite.TRANS_MIRROR_ROT90: {
            tmpMatrix.preScale(-1, 1);
            tmpMatrix.preRotate(-90);
        	img = Bitmap.createBitmap(img, x_src, y_src, width, height, tmpMatrix, true);
            dW = height;
            dH = width;
            break;
        }
        case Sprite.TRANS_MIRROR_ROT180: {
            tmpMatrix.preScale(-1, 1);
            tmpMatrix.preRotate(-180);
        	img = Bitmap.createBitmap(img, x_src, y_src, width, height, tmpMatrix, true);
            break;
        }
        case Sprite.TRANS_MIRROR_ROT270: {
            tmpMatrix.preScale(-1, 1);
            tmpMatrix.preRotate(-270);
            img = Bitmap.createBitmap(img, x_src, y_src, width, height, tmpMatrix, true);
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
         
        tmpRect.left = x_src;
        tmpRect.top = y_src;
        tmpRect.right = x_src + width;
        tmpRect.bottom = y_src + height;
        tmpRectSecond.left = x_dst;
        tmpRectSecond.top = y_dst;
        tmpRectSecond.right = x_dst + width;
        tmpRectSecond.bottom = y_dst + height;
        
        if (delegate != null) {
            delegate.drawRegionDelegate(img, tmpRect, tmpRectSecond);
        } else {
            canvas.drawBitmap(img, tmpRect, tmpRectSecond, strokePaint);
        }
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
        
        // MIDP allows almost any value of scanlength, drawBitmap is more strict with the stride
        if (scanlength == 0) {
        	scanlength = width;
        }
        int rows = rgbData.length / scanlength;
        if (rows < height) {
        	height = rows;
        }
        
       	canvas.drawBitmap(rgbData, offset, scanlength, x, y, width, height, processAlpha, strokePaint);
	}

	public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
	    if (delegate != null) {
	        delegate.fillTriangle(x1, y1, x2, y2, x3, y3);
	    } else {
    		Path path = new Path();
    		path.moveTo(x1, y1);
    		path.lineTo(x2, y2);
    		path.lineTo(x3, y3);
    		path.lineTo(x1, y1);
    		canvas.drawPath(path, fillPaint);
	    }
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
