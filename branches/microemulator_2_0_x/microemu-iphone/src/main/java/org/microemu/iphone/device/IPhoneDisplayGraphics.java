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

import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

import joc.Pointer;
import joc.Scope;
import obc.CGColor;
import obc.CGColorSpace;
import obc.CGContext;
import obc.CGImage;
import obc.CGPoint;
import obc.CGRect;
import obc.NSString;
import obc.UIFont;

import org.microemu.device.DeviceFactory;
import org.microemu.device.impl.Rectangle;

import straptease.CGImageAlphaInfo;
import straptease.CGPathDrawingMode;
import straptease.CoreGraphics;
import straptease.CoreGraphicsConstants;

public class IPhoneDisplayGraphics extends javax.microedition.lcdui.Graphics {

	private Pointer<CGContext> context;

	private Rectangle clip;

	private Font font;

	private int color;

	private int height;

	private int currentTranslateX;
	private int currentTranslateY;

	private Queue<Renderable> renderQueue;

	protected Pointer<CGColor> currentColor;

	protected Font currentfont;

	public IPhoneDisplayGraphics(Pointer<CGContext> context, int width, int height, boolean offscreen) {
		this.context = context;
		this.height = height;
		this.clip = new Rectangle(0, 0, width, height);
		renderQueue = new LinkedList<Renderable>();
		// CoreGraphics.CGContextClipToRect(canvas, CoreGraphics.CGRectMake(0,
		// 0, width, height));
		// flip upside down
		CoreGraphics.CGContextTranslateCTM(context, 0, height);
		CoreGraphics.CGContextScaleCTM(context, 1.0f, -1.0f);
		CoreGraphics.CGContextSaveGState(context);

		setFont(Font.getDefaultFont());
	}

	public void clipRect(final int x, final int y, final int width, final int height) {
		// java.awt.Rectangle oldRect = new java.awt.Rectangle(clip.x, clip.y,
		// clip.width, clip.height);
		// java.awt.Rectangle intRect = oldRect.intersection(new
		// java.awt.Rectangle(x, y, width, height));
		// clip = new Rectangle(intRect.x, intRect.y, intRect.width,
		// intRect.height);
		// queue(new Renderable() {
		// public void render() {
		// CoreGraphics.CGContextClipToRect(context, CoreGraphics.CGRectMake(x,
		// y, width, height));
		// }
		// });
		setClip(x, y, width, height);
	}

	public void drawArc(final int x, final int y, final int width, final int height, final int startAngle,
			final int arcAngle) {
		queue(new Renderable() {
			public void render() {
				CoreGraphics.CGContextAddArc(context, x, y, 10, startAngle, arcAngle, 1);
				CoreGraphics.CGContextDrawPath(context, CGPathDrawingMode.kCGPathStroke);
			}
		});
	}

	public void drawImage(final Image img, final int x, final int y, final int anchor) {
		queue(new Renderable() {
			public void render() {
				int newx = x;
				int newy = y;
				int newanchor = anchor;

				if (newanchor == 0) {
					newanchor = javax.microedition.lcdui.Graphics.TOP | javax.microedition.lcdui.Graphics.LEFT;
				}

				if ((newanchor & javax.microedition.lcdui.Graphics.RIGHT) != 0) {
					newx -= img.getWidth();
				} else if ((newanchor & javax.microedition.lcdui.Graphics.HCENTER) != 0) {
					newx -= img.getWidth() / 2;
				}
				if ((newanchor & javax.microedition.lcdui.Graphics.BOTTOM) != 0) {
					newy -= img.getHeight();
				} else if ((newanchor & javax.microedition.lcdui.Graphics.VCENTER) != 0) {
					newy -= img.getHeight() / 2;
				}

				// Save the Context State because we want
				// to restore after we are done so we can draw normally again.
				CoreGraphics.CGContextSaveGState(context);

				// Setup the Context to Invert everything drawn on the Y Axis
				CoreGraphics.CGContextTranslateCTM(context, 0, height);
				CoreGraphics.CGContextScaleCTM(context, 1.0f, -1.0f);

				Pointer<CGRect> rect = CoreGraphics.CGRectMake(newx, newy, img.getWidth(), img.getHeight());
				Pointer<CGImage> bitmap = ((IPhoneImage) img).getBitmap();
				CoreGraphics.CGContextDrawImage(context, rect, bitmap);

				// Restore the Context State so drawing returns back to normal
				CoreGraphics.CGContextRestoreGState(context);
			}
		});
	}

	public void drawLine(final int x1, final int y1, final int x2, final int y2) {
		// if (x1 > x2) {
		// x1++;
		// } else {
		// x2++;
		// }
		// if (y1 > y2) {
		// y1++;
		// } else {
		// y2++;
		// }

		queue(new Renderable() {
			public void render() {
				CoreGraphics.CGContextMoveToPoint(context, x1, y1);
				CoreGraphics.CGContextAddLineToPoint(context, x2, y2);
				CoreGraphics.CGContextDrawPath(context, CGPathDrawingMode.kCGPathStroke);
			}
		});
	}

	public void drawRect(final int x, final int y, final int width, final int height) {
		queue(new Renderable() {
			public void render() {
				CoreGraphics.CGContextAddRect(context, CoreGraphics.CGRectMake(x, y, width, height));
				CoreGraphics.CGContextDrawPath(context, CGPathDrawingMode.kCGPathStroke);
			}
		});
	}

	public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		// TODO implement round rect
		drawRect(x, y, width, height);
	}

	public void drawString(final String str, final int x, final int y, final int anchor) {
		queue(new Renderable() {
			public void render() {
				int newx = x;
				int newy = y;
				int newanchor = anchor;

				UIFont uifont = ((IPhoneFontManager) DeviceFactory.getDevice().getFontManager()).getUIFont(currentfont);

				if (newanchor == 0) {
					newanchor = javax.microedition.lcdui.Graphics.TOP | javax.microedition.lcdui.Graphics.LEFT;
				}

				if ((newanchor & javax.microedition.lcdui.Graphics.VCENTER) != 0) {
					newy += font.getHeight() / 2;
				} else if ((newanchor & javax.microedition.lcdui.Graphics.BOTTOM) != 0) {
					newy += font.getHeight();
				}
				if ((newanchor & javax.microedition.lcdui.Graphics.HCENTER) != 0) {
					newx -= font.stringWidth(str) / 2;
				} else if ((newanchor & javax.microedition.lcdui.Graphics.RIGHT) != 0) {
					newx -= font.stringWidth(str);
				}

				// System.out.println(str + " " + newx + " " + newy + " " +
				// getTranslateX() + " " + getTranslateY() + " "
				// + clip);
				NSString string = new NSString().initWithString$(str);

				string.drawAtPoint$withFont$(new CGPoint(newx, newy), uifont);
				string.release();
			}
		});
	}

	public void fillArc(final int x, final int y, final int width, final int height, final int startAngle,
			final int arcAngle) {
		queue(new Renderable() {
			public void render() {
				CoreGraphics.CGContextAddArc(context, x, y, 10, startAngle, arcAngle, 1);
				CoreGraphics.CGContextDrawPath(context, CGPathDrawingMode.kCGPathFill);
			}
		});
	}

	public void fillRect(final int x, final int y, final int width, final int height) {
		queue(new Renderable() {
			public void render() {
				CoreGraphics.CGContextFillRect(context, CoreGraphics.CGRectMake(x, y, width, height));
			}
		});
	}

	public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		// TODO implement round rect
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

	public void setClip(final int x, final int y, final int width, final int height) {
		clip = new Rectangle(x, y, width, height);
		queue(new Renderable() {
			public void render() {
				// reset clip and all other state
				CoreGraphics.CGContextRestoreGState(context);
				CoreGraphics.CGContextSaveGState(context);
				CoreGraphics.CGContextTranslateCTM(context, currentTranslateX, currentTranslateY);
				 CoreGraphics.CGContextClipToRect(context, CoreGraphics.CGRectMake(x, y, width, height));
				if (currentColor != null) {
					CoreGraphics.CGContextSetFillColorWithColor(context, currentColor);
					CoreGraphics.CGContextSetStrokeColorWithColor(context, currentColor);
				}
			}
		});
	}

	public void setColor(final int RGB) {
		this.color = 0xff000000 | RGB;
		queue(new Renderable() {
			public void render() {
				int r = (RGB >> 16) & 0xFF;
				int g = (RGB >> 8) & 0xFF;
				int b = (RGB >> 0) & 0xFF;
				currentColor = CoreGraphics.CGColorCreateGenericRGB(((float) r) / 255f, ((float) g) / 255f,
						((float) b) / 255f, 1f);
				CoreGraphics.CGContextSetFillColorWithColor(context, currentColor);
				CoreGraphics.CGContextSetStrokeColorWithColor(context, currentColor);
			}
		});
	}

	public void setFont(final Font font) {
		this.font = font;
		queue(new Renderable() {
			public void render() {
				currentfont = font;
			}
		});
	}

	public void translate(final int x, final int y) {
		super.translate(x, y);
		clip.x -= x;
		clip.y -= y;

		queue(new Renderable() {
			public void render() {
				CoreGraphics.CGContextTranslateCTM(context, x, y);
				currentTranslateX += x;
				currentTranslateY += y;
			}
		});
	}

	public void drawRegion(final Image src, final int x_src, final int y_src, final int width, final int height,
			final int transform, final int x_dst, final int y_dst, final int anchor) {
		// may throw NullPointerException, this is ok
		System.out.println("IPhoneDisplayGraphics.drawRegion()");
		queue(new Renderable() {
			public void render() {
				Pointer<CGRect> clipRect = CoreGraphics.CGRectMake(x_dst, y_dst, width, height);

				Pointer<CGRect> drawRect = CoreGraphics.CGRectMake(x_dst - x_src, y_dst - y_src, src.getWidth(), src
						.getHeight());

				// Save the Context State because we want
				// to restore after we are done so we can draw normally again.
				CoreGraphics.CGContextSaveGState(context);

				// Setup the Context to Invert everything drawn on the Y Axis
				CoreGraphics.CGContextTranslateCTM(context, 0, height);
				CoreGraphics.CGContextScaleCTM(context, 1.0f, -1.0f);

				CoreGraphics.CGContextClipToRect(context, clipRect);
				CoreGraphics.CGContextDrawImage(context, drawRect, ((IPhoneImage) src).getBitmap());

				CoreGraphics.CGContextRestoreGState(context);
			}
		});

		// if (x_src + width > src.getWidth() || y_src + height >
		// src.getHeight() || width < 0 || height < 0 || x_src < 0
		// || y_src < 0)
		// throw new IllegalArgumentException("Area out of Image");
		//
		// // this cannot be done on the same image we are drawing
		// // check this if the implementation of getGraphics change so
		// // as to return different Graphic Objects on each call to
		// // getGraphics
		// if (src.isMutable() && src.getGraphics() == this)
		// throw new IllegalArgumentException("Image is source and target");
		//
		// Bitmap img;
		//
		// if (src.isMutable()) {
		// img = ((AndroidMutableImage) src).getBitmap();
		// } else {
		// img = ((AndroidImmutableImage) src).getBitmap();
		// }
		//
		// canvas.save(Canvas.MATRIX_SAVE_FLAG);
		//
		// int dW = width, dH = height;
		// switch (transform) {
		// case Sprite.TRANS_NONE: {
		// break;
		// }
		// case Sprite.TRANS_ROT90: {
		// canvas.translate(height, 0);
		// canvas.rotate(90);
		// dW = height;
		// dH = width;
		// break;
		// }
		// case Sprite.TRANS_ROT180: {
		// canvas.translate(width, height);
		// canvas.rotate(180);
		// break;
		// }
		// case Sprite.TRANS_ROT270: {
		// canvas.translate(0, width);
		// canvas.rotate(270);
		// dW = height;
		// dH = width;
		// break;
		// }
		// case Sprite.TRANS_MIRROR: {
		// canvas.translate(width, 0);
		// canvas.scale(-1, 1);
		// break;
		// }
		// case Sprite.TRANS_MIRROR_ROT90: {
		// canvas.translate(height, 0);
		// canvas.rotate(90);
		// canvas.translate(width, 0);
		// canvas.scale(-1, 1);
		// dW = height;
		// dH = width;
		// break;
		// }
		// case Sprite.TRANS_MIRROR_ROT180: {
		// canvas.translate(width, 0);
		// canvas.scale(-1, 1);
		// canvas.translate(width, height);
		// canvas.rotate(180);
		// break;
		// }
		// case Sprite.TRANS_MIRROR_ROT270: {
		// canvas.rotate(270);
		// canvas.scale(-1, 1);
		// dW = height;
		// dH = width;
		// break;
		// }
		// default:
		// throw new IllegalArgumentException("Bad transform");
		// }
		//
		// // process anchor and correct x and y _dest
		// // vertical
		// boolean badAnchor = false;
		//
		// if (anchor == 0) {
		// anchor = TOP | LEFT;
		// }
		//
		// if ((anchor & 0x7f) != anchor || (anchor & BASELINE) != 0)
		// badAnchor = true;
		//
		// if ((anchor & TOP) != 0) {
		// if ((anchor & (VCENTER | BOTTOM)) != 0)
		// badAnchor = true;
		// } else if ((anchor & BOTTOM) != 0) {
		// if ((anchor & VCENTER) != 0)
		// badAnchor = true;
		// else {
		// y_dst -= dH - 1;
		// }
		// } else if ((anchor & VCENTER) != 0) {
		// y_dst -= (dH - 1) >>> 1;
		// } else {
		// // no vertical anchor
		// badAnchor = true;
		// }
		//
		// // horizontal
		// if ((anchor & LEFT) != 0) {
		// if ((anchor & (HCENTER | RIGHT)) != 0)
		// badAnchor = true;
		// } else if ((anchor & RIGHT) != 0) {
		// if ((anchor & HCENTER) != 0)
		// badAnchor = true;
		// else {
		// x_dst -= dW - 1;
		// }
		// } else if ((anchor & HCENTER) != 0) {
		// x_dst -= (dW - 1) >>> 1;
		// } else {
		// // no horizontal anchor
		// badAnchor = true;
		// }
		//
		// if (badAnchor) {
		// canvas.restore();
		// throw new IllegalArgumentException("Bad Anchor");
		// }
		//            
		// Rect srcRect = new Rect(x_src, y_src, x_src + width, y_src + height);
		// Rect dstRect = new Rect(x_dst, y_dst, x_dst + width, y_dst + height);
		// canvas.drawBitmap(img, srcRect, dstRect, paint);
		//
		// canvas.restore();
	}

	public void drawRGB(int[] rgbData, int offset, int scanlength, final int x, final int y, final int width,
			final int height, boolean processAlpha) {
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

		final int data[] = new int[width * height];
		for (int yi = 0; yi < height; yi++) {
			System.arraycopy(rgbData, offset + scanlength * yi, data, (height - yi - 1) * width, width);
		}

		queue(new Renderable() {

			public void render() {
				Pointer<CGColorSpace> colorSpace = CoreGraphics.CGColorSpaceCreateDeviceRGB();

				Pointer<Integer> dataPointer = Pointer.box(data);
				Pointer<CGContext> imageContext = CoreGraphics.CGBitmapContextCreate(dataPointer, width, height, 8,
						width * 4, colorSpace, CoreGraphicsConstants.kCGBitmapByteOrder32Little
								| CGImageAlphaInfo.kCGImageAlphaNoneSkipFirst);

				Pointer<CGImage> bitmap = CoreGraphics.CGBitmapContextCreateImage(imageContext);
				//
				Pointer<CGRect> rect = CoreGraphics.CGRectMake(x, y, width, height);
				//
				// // flip upside down
				// CoreGraphics.CGContextTranslateCTM(uiContext, 0,
				// CoreGraphics.CGRectGetMaxY(rect));
				// CoreGraphics.CGContextScaleCTM(uiContext, 1.0f, -1.0f);
				//
				CoreGraphics.CGContextDrawImage(context, rect, bitmap);
				Pointer.free(dataPointer);
				CoreGraphics.CGContextRelease(imageContext);
				CoreGraphics.CGImageRelease(bitmap);
			}
		});

	}

	public void fillTriangle(final int x1, final int y1, final int x2, final int y2, final int x3, final int y3) {
		queue(new Renderable() {
			public void render() {
				CoreGraphics.CGContextMoveToPoint(context, x1, y1);
				CoreGraphics.CGContextAddLineToPoint(context, x2, y2);
				CoreGraphics.CGContextAddLineToPoint(context, x3, y3);
				CoreGraphics.CGContextClosePath(context);
				CoreGraphics.CGContextFillPath(context);
			}
		});
	}

	public void copyArea(int x_src, int y_src, int width, int height, int x_dest, int y_dest, int anchor) {
		throw new UnsupportedOperationException("Currently not supported on iPhone");
	}

	public int getDisplayColor(int color) {
		throw new UnsupportedOperationException("Currently not supported on iPhone");
	}

	final synchronized void queue(Renderable renderable) {
		renderQueue.offer(renderable);
		// flushRenderQueue();
	}

	final void flushRenderQueue() {
		System.out.println("IPhoneDisplayGraphics.flushRenderQueue() " + renderQueue.size());
		synchronized (renderQueue) {
			Scope scope = new Scope();
			try {
				CoreGraphics.UIGraphicsPushContext(context);

				while (!renderQueue.isEmpty()) {
					Renderable renderable = renderQueue.poll();
					renderable.render();
				}
			} finally {
				// //reset all
				// CoreGraphics.CGContextRestoreGState(context);
				// reset ui graphics
				CoreGraphics.UIGraphicsPopContext();
				scope.close();
			}
		}
	}

	interface Renderable {
		public abstract void render();

	}
}
