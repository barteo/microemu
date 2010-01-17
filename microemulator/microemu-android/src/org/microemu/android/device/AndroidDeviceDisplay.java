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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.Sprite;

import org.microemu.app.ui.DisplayRepaintListener;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.EmulatorContext;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;

public class AndroidDeviceDisplay implements DeviceDisplay {
    
	private EmulatorContext context;
	
    public AndroidDisplayGraphics graphics;
    
	// TODO change this
	public int displayRectangleWidth;
	
	// TODO change this
	public int displayRectangleHeight;
	
    private ArrayList<DisplayRepaintListener> displayRepaintListeners = new ArrayList<DisplayRepaintListener>();
    	
	private Rect rectangle = new Rect();
	
	public AndroidDeviceDisplay(EmulatorContext context, int width, int height) {
		this.context = context;
        this.displayRectangleWidth = width;
        this.displayRectangleHeight = height;
		
	    graphics = new AndroidDisplayGraphics(Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565));
	}

	public Image createImage(String name) throws IOException {
		InputStream is = context.getResourceAsStream(name);
		if (is == null) {
			throw new IOException(name + " could not be found.");
		}

		return createImage(is);
	}

	public Image createImage(Image source) {
		if (source.isMutable()) {
			return new AndroidImmutableImage((AndroidMutableImage) source);
		} else {
			return source;
		}
	}

	public Image createImage(InputStream is) throws IOException {
		return new AndroidImmutableImage(BitmapFactory.decodeStream(is));
	}

	public Image createImage(int width, int height, boolean withAlpha, int fillColor) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException();
		}

		return new AndroidMutableImage(width, height, withAlpha, fillColor);
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

        Bitmap img;
        if (image.isMutable()) {
            img = ((AndroidMutableImage) image).getBitmap();
        } else {
            img = ((AndroidImmutableImage) image).getBitmap();
        }            

        Matrix matrix = new Matrix();
        switch (transform) {
        case Sprite.TRANS_NONE: {
            break;
        }
        case Sprite.TRANS_ROT90: {
        	matrix.preRotate(90);
            break;
        }
        case Sprite.TRANS_ROT180: {
            matrix.preRotate(180);
            break;
        }
        case Sprite.TRANS_ROT270: {
            matrix.preRotate(270);
            break;
        }
        case Sprite.TRANS_MIRROR: {
        	matrix.preScale(-1, 1);
            break;
        }
        case Sprite.TRANS_MIRROR_ROT90: {
        	matrix.preScale(-1, 1);
        	matrix.preRotate(-90);
            break;
        }
        case Sprite.TRANS_MIRROR_ROT180: {
        	matrix.preScale(-1, 1);
            matrix.preRotate(-180);
            break;
        }
        case Sprite.TRANS_MIRROR_ROT270: {
        	matrix.preScale(-1, 1);
            matrix.preRotate(-270);
            break;
        }
        default:
            throw new IllegalArgumentException("Bad transform");
        }

		return new AndroidImmutableImage(Bitmap.createBitmap(img, x, y, width, height, matrix, true));
	}

	public Image createRGBImage(int[] rgb, int width, int height, boolean processAlpha) {
		if (rgb == null)
			throw new NullPointerException();
		if (width <= 0 || height <= 0)
			throw new IllegalArgumentException();
		
		// TODO processAlpha is not handled natively, check whether we need to create copy of rgb
		int[] newrgb = rgb;
		if (!processAlpha) {
			newrgb = new int[rgb.length];
			for (int i = 0; i < rgb.length; i++) {
				newrgb[i] = (0x00ffffff & rgb[i]) | 0xff000000;
			}
		}
		return new AndroidImmutableImage(Bitmap.createBitmap(newrgb, width, height, Bitmap.Config.ARGB_8888));
	}

    public Graphics getGraphics(GameCanvas gameCanvas)
    {
        return graphics;
    }
    
    public void flushGraphics(int x, int y, int width, int height) {
        // TODO;
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
		paintDisplayable(x, y, width, height);
	}

	public void setScrollDown(boolean arg0) {
		// TODO Auto-generated method stub

	}

	public void setScrollUp(boolean arg0) {
		// TODO Auto-generated method stub

	}
	
	public void addDisplayRepaintListener(DisplayRepaintListener listener) {
	    displayRepaintListeners.add(listener);
	}

    public void removeDisplayRepaintListener(DisplayRepaintListener listener) {
        displayRepaintListeners.remove(listener);
    }

	public void paintDisplayable(int x, int y, int width, int height) {
        rectangle.left = x;
        rectangle.top = y;
        rectangle.right = x + width;
        rectangle.bottom = y + height;
        for (int i = 0; i < displayRepaintListeners.size(); i++) {
            displayRepaintListeners.get(i).repaintInvoked(rectangle);    
        }
	}
	
}
