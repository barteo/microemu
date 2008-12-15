/**
 *  MicroEmulator
 *  Copyright (C) 2008 Bartek Teodorczyk <barteo@barteo.net>
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
package org.microemu.iphone.device;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

import joc.Pointer;
import obc.CGColor;
import obc.CGContext;
import obc.CGPoint;
import obc.CGRect;
import obc.NSString;

import org.microemu.device.DeviceFactory;
import org.microemu.device.impl.Rectangle;

import straptease.CGPathDrawingMode;
import straptease.CoreGraphics;

public class IPhoneDisplayGraphics extends javax.microedition.lcdui.Graphics {
	
	private Pointer<CGContext> context;
	
	private Rectangle clip;
	
	private Font font;
	
	private int color;

	private int height;
	
	public IPhoneDisplayGraphics(Pointer<CGContext> context, int width, int height) {
		this.context = context;
		this.height = height;
		this.clip = new Rectangle(0,0,width,height);
//		CoreGraphics.CGContextClipToRect(canvas, CoreGraphics.CGRectMake(0, 0, width, height));
		//flip upside down
		CoreGraphics.CGContextTranslateCTM(context, 0, height);
		CoreGraphics.CGContextScaleCTM(context, 1.0f, -1.0f);

		setFont(Font.getDefaultFont());
	}
	
	public void clipRect(int x, int y, int width, int height) {
		clip=new Rectangle(x,y,width,height);
		CoreGraphics.CGContextClipToRect(context, CoreGraphics.CGRectMake(0, 0, width, height));
	}

	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        throw new UnsupportedOperationException("Currently not supported on iPhone");
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

		Pointer<CGRect> rect=CoreGraphics.CGRectMake(newx, newy, img.getWidth(), img.getHeight());
        CoreGraphics.CGContextDrawImage(context, rect,  ((IPhoneImage)img).getBitmap());
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
	
		CoreGraphics.CGContextMoveToPoint(context, x1, y1);
		CoreGraphics.CGContextAddLineToPoint(context, x2, y2);
		CoreGraphics.CGContextDrawPath(context, CGPathDrawingMode.kCGPathStroke);
	}

	public void drawRect(int x, int y, int width, int height) {
		CoreGraphics.CGContextAddRect(context, CoreGraphics.CGRectMake(x, y, width, height));
		CoreGraphics.CGContextDrawPath(context, CGPathDrawingMode.kCGPathStroke);
	}

	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		//TODO implement round rect
		drawRect(x, y, width, height);
    }

	public void drawString(String str, int x, int y, int anchor) {
        int newx = x;
        int newy = y;

        CoreGraphics.UIGraphicsPushContext(context);
		CoreGraphics.CGContextScaleCTM(context, 1.0f, -1.0f);
		CoreGraphics.CGContextTranslateCTM(context, 0, -height);
       
        IPhoneFontManager fontManager = (IPhoneFontManager)DeviceFactory.getDevice().getFontManager();
        
        if (anchor == 0) {
            anchor = javax.microedition.lcdui.Graphics.TOP | javax.microedition.lcdui.Graphics.LEFT;
        }

        if ((anchor & javax.microedition.lcdui.Graphics.VCENTER) != 0) {
            newy += font.getHeight()/2;
        } else if ((anchor & javax.microedition.lcdui.Graphics.BOTTOM) != 0) {
            newy += font.getHeight();
        }
        if ((anchor & javax.microedition.lcdui.Graphics.HCENTER) != 0) {
            newx -= font.stringWidth(str) / 2;
        } else if ((anchor & javax.microedition.lcdui.Graphics.RIGHT) != 0) {
            newx -= font.stringWidth(str);
        }
        NSString string=new NSString().initWithString$(str);
// 		CoreGraphics.CGContextScaleCTM(context, 1.0f, -1.0f);
		CoreGraphics.CGContextSetTextMatrix(context, CoreGraphics.CGAffineTransformMakeScale(0.5f, -0.5f));

		string.drawAtPoint$withFont$(new CGPoint(newx,newy), fontManager.getUIFont(font));
//		CoreGraphics.CGContextScaleCTM(context, 1.0f, -1.0f);

//        CoreGraphics.CGContextSetTextDrawingMode(context, CGTextDrawingMode.kCGTextFill);
//		CoreGraphics.CGContextShowTextAtPoint(context, newx, newy, str, str.length());
		
		CoreGraphics.CGContextTranslateCTM(context, 0, height);
		CoreGraphics.CGContextScaleCTM(context, 1.0f, -1.0f);
 		CoreGraphics.UIGraphicsPopContext();
	}

    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        throw new UnsupportedOperationException("Currently not supported on iPhone");
    }

	public void fillRect(int x, int y, int width, int height) {
		CoreGraphics.CGContextFillRect(context, CoreGraphics.CGRectMake(x, y, width, height));
	}

	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		//TODO implement round rect
		fillRect(x, y, width, height);
    }

	public int getClipHeight() {
		return clip.height;
	}

	public int getClipWidth() {
		return clip.width;
	}

	public int getClipX() {
		return clip.x;
	}

	public int getClipY() {
		return clip.y;
	}

	public int getColor() {
		return color;
	}

	public Font getFont() {
		return font;
	}

	public void setClip(int x, int y, int width, int height) {
        throw new UnsupportedOperationException("Currently not supported on iPhone");
//		if (x == clip.x && x+ width == clip.right && y == clip.top && y + height == clip.bottom) {
//			return;
//		}
//		if (x < clip.left || x + width > clip.right || y < clip.top || y + height > clip.bottom) {
//			canvas.restore();
//			canvas.save(Canvas.CLIP_SAVE_FLAG);
//		}
//        clip.left = x;
//        clip.top = y;
//        clip.right = x + width;
//        clip.bottom = y + height;
//		canvas.clipRect(clip);
	}

	public void setColor(int RGB) {
		this.color=0xff000000 | RGB;
		Pointer<CGColor> cgcolor = CoreGraphics.CGColorCreateGenericRGB(((float)getRedComponent())/255f,((float)getGreenComponent())/255f,((float)getBlueComponent())/255f,1f);
		CoreGraphics.CGContextSetFillColorWithColor(context, cgcolor);
		CoreGraphics.CGContextSetStrokeColorWithColor(context, cgcolor);
	}

	public void setFont(Font font) {
		this.font = font;
	}

    public void translate(int x, int y) {
        super.translate(x, y);
        CoreGraphics.CGContextTranslateCTM(context, x, y);
        clip.x -= x;
        clip.y -= y;
    }

	public void drawRegion(Image src, int x_src, int y_src, int width,
			int height, int transform, int x_dst, int y_dst, int anchor) {
        // may throw NullPointerException, this is ok
        throw new UnsupportedOperationException("Currently not supported on iPhone");
//       if (x_src + width > src.getWidth() || y_src + height > src.getHeight() || width < 0 || height < 0 || x_src < 0
//                || y_src < 0)
//            throw new IllegalArgumentException("Area out of Image");
//
//        // this cannot be done on the same image we are drawing
//        // check this if the implementation of getGraphics change so
//        // as to return different Graphic Objects on each call to
//        // getGraphics
//        if (src.isMutable() && src.getGraphics() == this)
//            throw new IllegalArgumentException("Image is source and target");
//
//        Bitmap img;
//
//        if (src.isMutable()) {
//            img = ((AndroidMutableImage) src).getBitmap();
//        } else {
//            img = ((AndroidImmutableImage) src).getBitmap();
//        }            
//
//        canvas.save(Canvas.MATRIX_SAVE_FLAG);
//
//        int dW = width, dH = height;
//        switch (transform) {
//        case Sprite.TRANS_NONE: {
//            break;
//        }
//        case Sprite.TRANS_ROT90: {
//            canvas.translate(height, 0);
//            canvas.rotate(90);
//            dW = height;
//            dH = width;
//            break;
//        }
//        case Sprite.TRANS_ROT180: {
//            canvas.translate(width, height);
//            canvas.rotate(180);
//            break;
//        }
//        case Sprite.TRANS_ROT270: {
//            canvas.translate(0, width);
//            canvas.rotate(270);
//            dW = height;
//            dH = width;
//            break;
//        }
//        case Sprite.TRANS_MIRROR: {
//        	canvas.translate(width, 0);
//        	canvas.scale(-1, 1);
//            break;
//        }
//        case Sprite.TRANS_MIRROR_ROT90: {
//            canvas.translate(height, 0);
//            canvas.rotate(90);
//            canvas.translate(width, 0);
//            canvas.scale(-1, 1);
//            dW = height;
//            dH = width;
//            break;
//        }
//        case Sprite.TRANS_MIRROR_ROT180: {
//            canvas.translate(width, 0);
//            canvas.scale(-1, 1);
//            canvas.translate(width, height);
//            canvas.rotate(180);
//            break;
//        }
//        case Sprite.TRANS_MIRROR_ROT270: {
//            canvas.rotate(270);
//            canvas.scale(-1, 1);
//            dW = height;
//            dH = width;
//            break;
//        }
//        default:
//            throw new IllegalArgumentException("Bad transform");
//        }
//
//        // process anchor and correct x and y _dest
//        // vertical
//        boolean badAnchor = false;
//
//        if (anchor == 0) {
//            anchor = TOP | LEFT;
//        }
//
//        if ((anchor & 0x7f) != anchor || (anchor & BASELINE) != 0)
//            badAnchor = true;
//
//        if ((anchor & TOP) != 0) {
//            if ((anchor & (VCENTER | BOTTOM)) != 0)
//                badAnchor = true;
//        } else if ((anchor & BOTTOM) != 0) {
//            if ((anchor & VCENTER) != 0)
//                badAnchor = true;
//            else {
//                y_dst -= dH - 1;
//            }
//        } else if ((anchor & VCENTER) != 0) {
//            y_dst -= (dH - 1) >>> 1;
//        } else {
//            // no vertical anchor
//            badAnchor = true;
//        }
//
//        // horizontal
//        if ((anchor & LEFT) != 0) {
//            if ((anchor & (HCENTER | RIGHT)) != 0)
//                badAnchor = true;
//        } else if ((anchor & RIGHT) != 0) {
//            if ((anchor & HCENTER) != 0)
//                badAnchor = true;
//            else {
//                x_dst -= dW - 1;
//            }
//        } else if ((anchor & HCENTER) != 0) {
//            x_dst -= (dW - 1) >>> 1;
//        } else {
//            // no horizontal anchor
//            badAnchor = true;
//        }
//
//        if (badAnchor) {
//            canvas.restore();
//            throw new IllegalArgumentException("Bad Anchor");
//        }
//            
//        Rect srcRect = new Rect(x_src, y_src, x_src + width, y_src + height);
//        Rect dstRect = new Rect(x_dst, y_dst, x_dst + width, y_dst + height);
//        canvas.drawBitmap(img, srcRect, dstRect, paint);
//
//        canvas.restore();
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
        
        throw new UnsupportedOperationException("Currently not supported on iPhone");

//        canvas.drawBitmap(rgbData, offset, scanlength, x, y, width, height, processAlpha, paint);
	}

	public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
        throw new UnsupportedOperationException("Currently not supported on iPhone");
	}

	public void copyArea(int x_src, int y_src, int width, int height,
			int x_dest, int y_dest, int anchor) {
        throw new UnsupportedOperationException("Currently not supported on iPhone");
	}

	public int getDisplayColor(int color) {
        throw new UnsupportedOperationException("Currently not supported on iPhone");
	}
	
}
