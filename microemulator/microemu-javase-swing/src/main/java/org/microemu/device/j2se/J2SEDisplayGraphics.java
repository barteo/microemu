/*
 * MicroEmulator 
 * Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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
 * Contributor(s): 
 *   3GLab
 *   Andres Navarro
 */

package org.microemu.device.j2se;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.HashMap;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.device.DisplayGraphics;
import org.microemu.device.MutableImage;

public class J2SEDisplayGraphics extends javax.microedition.lcdui.Graphics implements DisplayGraphics {
    // Andres Navarro
    private java.awt.Graphics2D g;

    // Andres Navarro

    private MutableImage image;

    private int color = 0;
    
    // TODO use IntHashMap
    private HashMap colorCache = new HashMap();
    
    // Access to the AWT clip is expensive in memory allocation 
    private Rectangle clip;

    private javax.microedition.lcdui.Font currentFont = javax.microedition.lcdui.Font.getDefaultFont();

    private java.awt.image.RGBImageFilter filter = null;

    // Andres Navarro
    public J2SEDisplayGraphics(java.awt.Graphics2D a_g, MutableImage a_image)
    // Andres Navarro
    {
        this.g = a_g;
        this.image = a_image;
        
        this.clip = a_g.getClipBounds();

        Device device = DeviceFactory.getDevice();
        J2SEFontManager fontManager = (J2SEFontManager) device.getFontManager();

        J2SEFont tmpFont = (J2SEFont) fontManager.getFont(currentFont);
        this.g.setFont(tmpFont.getFont());
        if (fontManager.getAntialiasing()) {
            this.g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else {
            this.g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }

        J2SEDeviceDisplay display = (J2SEDeviceDisplay) device.getDeviceDisplay();
        if (display.isColor()) {
            if (display.backgroundColor.getRed() != 255 || display.backgroundColor.getGreen() != 255 || display.backgroundColor.getBlue() != 255 ||
                    display.foregroundColor.getRed() != 0 || display.foregroundColor.getGreen() != 0 || display.foregroundColor.getBlue() != 0) {
                this.filter = new RGBImageFilter();
            }
        } else {
            if (display.numColors() == 2) {
                this.filter = new BWImageFilter();
            } else {
                this.filter = new GrayImageFilter();
            }
        }
    }

    public MutableImage getImage() {
        return image;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int RGB) {
        color = RGB;
        
        Color awtColor = (Color) colorCache.get(new Integer(RGB));
        if (awtColor == null) {
            if (filter != null) {
                awtColor = new Color(filter.filterRGB(0, 0, color));
            } else {
                awtColor = new Color(RGB);
            }
            colorCache.put(new Integer(RGB), awtColor);
        }
        g.setColor(awtColor);
    }

    public javax.microedition.lcdui.Font getFont() {
        return currentFont;
    }

    public void setFont(javax.microedition.lcdui.Font font) {
        currentFont = font;
        J2SEFont tmpFont = (J2SEFont) ((J2SEFontManager) DeviceFactory.getDevice().getFontManager())
                .getFont(currentFont);
        g.setFont(tmpFont.getFont());
    }

    public void clipRect(int x, int y, int width, int height) {
        g.clipRect(x, y, width, height);
        clip = g.getClipBounds();
    }

    public void setClip(int x, int y, int width, int height) {
        g.setClip(x, y, width, height);
        clip.x = x;
        clip.y = y;
        clip.width = width;
        clip.height = height;
    }

    public int getClipX() {
        return clip.x;
    }

    public int getClipY() {
        return clip.y;
    }

    public int getClipHeight() {
        return clip.height;
    }

    public int getClipWidth() {
        return clip.width;
    }

    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        g.drawArc(x, y, width, height, startAngle, arcAngle);
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
            g.drawImage(((J2SEMutableImage) img).getImage(), newx, newy, null);
        } else {
            g.drawImage(((J2SEImmutableImage) img).getImage(), newx, newy, null);
        }
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        g.drawLine(x1, y1, x2, y2);
    }

    public void drawRect(int x, int y, int width, int height) {
        drawLine(x, y, x + width, y);
        drawLine(x + width, y, x + width, y + height);
        drawLine(x + width, y + height, x, y + height);
        drawLine(x, y + height, x, y);
    }

    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    public void drawString(String str, int x, int y, int anchor) {
        int newx = x;
        int newy = y;

        if (anchor == 0) {
            anchor = javax.microedition.lcdui.Graphics.TOP | javax.microedition.lcdui.Graphics.LEFT;
        }

        if ((anchor & javax.microedition.lcdui.Graphics.TOP) != 0) {
            newy += g.getFontMetrics().getAscent();
        } else if ((anchor & javax.microedition.lcdui.Graphics.BOTTOM) != 0) {
            newy -= g.getFontMetrics().getDescent();
        }
        if ((anchor & javax.microedition.lcdui.Graphics.HCENTER) != 0) {
            newx -= g.getFontMetrics().stringWidth(str) / 2;
        } else if ((anchor & javax.microedition.lcdui.Graphics.RIGHT) != 0) {
            newx -= g.getFontMetrics().stringWidth(str);
        }

        g.drawString(str, newx, newy);

        if ((currentFont.getStyle() & javax.microedition.lcdui.Font.STYLE_UNDERLINED) != 0) {
            g.drawLine(newx, newy + 1, newx + g.getFontMetrics().stringWidth(str), newy + 1);
        }
    }

    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        g.fillArc(x, y, width, height, startAngle, arcAngle);
    }

    public void fillRect(int x, int y, int width, int height) {
        g.fillRect(x, y, width, height);
    }

    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    public void translate(int x, int y) {
        super.translate(x, y);
        g.translate(x, y);
    }

    // Andres Navarro
    public void drawRegion(Image src, int x_src, int y_src, int width, int height, int transform, int x_dst, int y_dst,
            int anchor) {

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

        java.awt.Image img;

        if (src.isMutable()) {
            img = ((J2SEMutableImage) src).getImage();
        } else {
            img = ((J2SEImmutableImage) src).getImage();
        }

        java.awt.geom.AffineTransform t = new java.awt.geom.AffineTransform();

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
        g.transform(t);

        g.drawImage(img, 0, 0, width, height, x_src, y_src, x_src + width, y_src + height, null);

        // return to saved
        g.setTransform(savedT);
    }

    public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height,
            boolean processAlpha) {
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
        int[] xPoints = new int[3];
        int[] yPoints = new int[3];
        xPoints[0] = x1;
        xPoints[1] = x2;
        xPoints[2] = x3;
        yPoints[0] = y1;
        yPoints[1] = y2;
        yPoints[2] = y3;

        g.fillPolygon(xPoints, yPoints, 3);
    }

    public void copyArea(int x_src, int y_src, int width, int height, int x_dest, int y_dest, int anchor) {

        // TODO check for Graphics Object size and
        // that this is not the Graphics representing the Screen
        if (width <= 0 || height <= 0)
            return;//?? is this ok or should i throw IllegalArgument?

        // process anchor and correct x and y _dest
        // vertical
        boolean badAnchor = false;
        if ((anchor & 0x7f) != anchor || (anchor & BASELINE) != 0)
            badAnchor = true;

        if ((anchor & TOP) != 0) {
            if ((anchor & (VCENTER | BOTTOM)) != 0)
                badAnchor = true;
        } else if ((anchor & BOTTOM) != 0) {
            if ((anchor & VCENTER) != 0)
                badAnchor = true;
            else {
                y_dest -= height - 1;
            }
        } else if ((anchor & VCENTER) != 0) {
            y_dest -= (height - 1) >>> 1;
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
                x_dest -= width;
            }
        } else if ((anchor & HCENTER) != 0) {
            x_dest -= (width - 1) >>> 1;
        } else {
            // no horizontal anchor
            badAnchor = true;
        }

        if (badAnchor)
            throw new IllegalArgumentException("Bad Anchor");

        g.copyArea(x_src, y_src, width, height, x_dest - x_src, y_dest - y_src);
    }

    public Graphics2D getGraphics() {
        return g;
    }

}
