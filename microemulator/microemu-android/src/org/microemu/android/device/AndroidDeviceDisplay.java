/**
 *  MicroEmulator
 *  Copyright (C) 2008 Bartek Teodorczyk <barteo@barteo.net>
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  @version $Id$
 */

package org.microemu.android.device;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.android.lcdui.Displayable;
import javax.microedition.android.lcdui.Graphics;
import javax.microedition.android.lcdui.Image;
import javax.microedition.android.lcdui.game.Sprite;

import org.microemu.DisplayAccess;
import org.microemu.EmulatorContext;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.MutableImage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class AndroidDeviceDisplay implements DeviceDisplay {
	
	private EmulatorContext context;
	
	// TODO change this
	public int displayRectangleWidth;
	
	// TODO change this
	public int displayRectangleHeight;
	
	public AndroidDeviceDisplay(EmulatorContext context) {
		this.context = context;
	}

	public Image createImage(String arg0) throws IOException {
		System.out.println("createImage: " + arg0);
		// TODO Auto-generated method stub
		return null;
	}

	public Image createImage(Image arg0) {
		System.out.println("createImage: " + arg0);
		// TODO Auto-generated method stub
		return null;
	}

	public Image createImage(InputStream arg0) throws IOException {
		System.out.println("createImage: " + arg0);
		// TODO Auto-generated method stub
		return null;
	}

	public Image createImage(int width, int height) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException();
		}

		return new AndroidMutableImage(width, height);
	}

	public Image createImage(byte[] imageData, int imageOffset, int imageLength) {
		return new AndroidImmutableImage(BitmapFactory.decodeByteArray(imageData, imageOffset, imageLength));
	}

	public Image createImage(Image image, int x, int y, int width, int height, int transform) {
		// TODO AndroidDisplayGraphics.drawRegion code is similar
		if (image == null)
			throw new NullPointerException();
		if (x + width > image.getWidth() || y + height > image.getHeight() || width <= 0 || height <= 0 || x < 0
				|| y < 0)
			throw new IllegalArgumentException("Area out of Image");

		int[] rgbData = new int[height * width];
		int[] rgbTransformedData = new int[height * width];
		if (image instanceof AndroidImmutableImage) {
			((AndroidImmutableImage) image).getRGB(rgbData, 0, width, x, y, width, height);
		} else {
			((AndroidMutableImage) image).getRGB(rgbData, 0, width, x, y, width, height);
		}

		int colIncr, rowIncr, offset;

		switch (transform) {
		case Sprite.TRANS_NONE: {
			offset = 0;
			colIncr = 1;
			rowIncr = 0;
			break;
		}
		case Sprite.TRANS_ROT90: {
			offset = (height - 1) * width;
			colIncr = -width;
			rowIncr = (height * width) + 1;
			int temp = width;
			width = height;
			height = temp;
			break;
		}
		case Sprite.TRANS_ROT180: {
			offset = (height * width) - 1;
			colIncr = -1;
			rowIncr = 0;
			break;
		}
		case Sprite.TRANS_ROT270: {
			offset = width - 1;
			colIncr = width;
			rowIncr = -(height * width) - 1;
			int temp = width;
			width = height;
			height = temp;
			break;
		}
		case Sprite.TRANS_MIRROR: {
			offset = width - 1;
			colIncr = -1;
			rowIncr = width << 1;
			break;
		}
		case Sprite.TRANS_MIRROR_ROT90: {
			offset = (height * width) - 1;
			colIncr = -width;
			rowIncr = (height * width) - 1;
			int temp = width;
			width = height;
			height = temp;
			break;
		}
		case Sprite.TRANS_MIRROR_ROT180: {
			offset = (height - 1) * width;
			colIncr = 1;
			rowIncr = -(width << 1);
			break;
		}
		case Sprite.TRANS_MIRROR_ROT270: {
			offset = 0;
			colIncr = width;
			rowIncr = -(height * width) + 1;
			int temp = width;
			width = height;
			height = temp;
			break;
		}
		default:
			throw new IllegalArgumentException("Bad transform");
		}

		// now the loops!
		for (int row = 0, i = 0; row < height; row++, offset += rowIncr) {
			for (int col = 0; col < width; col++, offset += colIncr, i++) {
/*			    int a = (rgbData[offset] & 0xFF000000);
			    int b = (rgbData[offset] & 0x00FF0000) >>> 16;
			    int g = (rgbData[offset] & 0x0000FF00) >>> 8;
			    int r = (rgbData[offset] & 0x000000FF);

			    rgbTransformedData[i] = a | (r << 16) | (g << 8) | b;*/
				rgbTransformedData[i] = rgbData[offset];
			}
		}
		// to aid gc
		rgbData = null;
		image = null;

		return createRGBImage(rgbTransformedData, width, height, true);
	}

	public Image createRGBImage(int[] rgb, int width, int height, boolean processAlpha) {
		if (rgb == null)
			throw new NullPointerException();
		if (width <= 0 || height <= 0)
			throw new IllegalArgumentException();
		
		return new AndroidImmutableImage(Bitmap.createBitmap(rgb, width, height, processAlpha));
	}

	public MutableImage getDisplayImage() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getFullHeight() {
		return displayRectangleHeight;
	}

	public int getFullWidth() {
		return displayRectangleWidth;
	}

	public int getHeight() {
		// TODO Auto-generated method stub
		return displayRectangleHeight;
	}

	public int getWidth() {
		// TODO Auto-generated method stub
		return displayRectangleWidth;
	}

	public boolean isColor() {
		return true;
	}

	public boolean isFullScreenMode() {
		// TODO Auto-generated method stub
		return false;
	}

	public int numAlphaLevels() {
		return 256;
	}

	public int numColors() {
		return 65536;
	}

	public void repaint(int x, int y, int width, int height) {
		context.getDisplayComponent().repaintRequest(x, y, width, height);
	}

	public void setScrollDown(boolean arg0) {
		// TODO Auto-generated method stub

	}

	public void setScrollUp(boolean arg0) {
		// TODO Auto-generated method stub

	}

	public void paintDisplayable(Graphics g, int x, int y, int width, int height) {
		MIDletAccess ma = MIDletBridge.getMIDletAccess();
		if (ma == null) {
			return;
		}
		DisplayAccess da = ma.getDisplayAccess();
		if (da == null) {
			return;
		}
		Displayable current = da.getCurrent();
		if (current == null) {
			return;
		}

		// TODO
		// g.save(android.graphics.Canvas.CLIP_SAVE_FLAG);
		// TODO
		// if (!(current instanceof Canvas) || ((Canvas) current).getWidth() != displayRectangle.width
		// 		|| ((Canvas) current).getHeight() != displayRectangle.height) {
		// 	g.translate(displayPaintable.x, displayPaintable.y);
		// }
		g.clipRect(x, y, x + width, y + height);
		// TODO
		// Font oldf = g.getFont();
		ma.getDisplayAccess().paint(g);
		// TODO
		// g.setFont(oldf);
		// TODO
		// if (!(current instanceof Canvas) || ((Canvas) current).getWidth() != displayRectangle.width
		//		|| ((Canvas) current).getHeight() != displayRectangle.height) {
		// 	g.translate(-displayPaintable.x, -displayPaintable.y);
		//}
		// TODO
		// g.restore();
	}
	
}
