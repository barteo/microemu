/*
 *  @(#)DisplayGraphics.java  07/07/2001
 *
 *  Copyright (c) 2001 Bartek Teodorczyk <barteo@it.pl>. All Rights Reserved.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package com.barteo.midp.lcdui;

import java.awt.Color;
import java.awt.Graphics;

import javax.microedition.lcdui.*;
import com.barteo.emulator.GrayImageFilter;
import com.barteo.emulator.device.Device;

/**
 *  Description of the Class
 *
 *@author     barteo
 *@created    3 wrzesieñ 2001
 */
public class DisplayGraphics extends javax.microedition.lcdui.Graphics {

    java.awt.Graphics g;


    /**
     *  Constructor for the DisplayGraphics object
     *
     *@param  a_g  Description of Parameter
     */
    protected DisplayGraphics(java.awt.Graphics a_g) {
        g = a_g;
    }


    /**
     *  Sets the clip attribute of the DisplayGraphics object
     *
     *@param  x       The new clip value
     *@param  y       The new clip value
     *@param  width   The new clip value
     *@param  height  The new clip value
     */
    public void setClip(int x, int y, int width, int height) {
        g.setClip(x, y, width, height);
    }


    /**
     *  Sets the color attribute of the DisplayGraphics object
     *
     *@param  RGB  The new color value
     */
    public void setColor(int RGB) {
        GrayImageFilter filter = new GrayImageFilter();

        g.setColor(new Color(filter.filterRGB(0, 0, RGB)));
    }


    /**
     *  Sets the grayScale attribute of the DisplayGraphics object
     *
     *@param  grey  The new grayScale value
     */
    public void setGrayScale(int grey) {
        if (grey > 127) {
            g.setColor(Device.backgroundColor);
        } else {
            g.setColor(Device.foregroundColor);
        }
    }


    /**
     *  Description of the Method
     *
     *@param  x       Description of Parameter
     *@param  y       Description of Parameter
     *@param  width   Description of Parameter
     *@param  height  Description of Parameter
     */
    public void clipRect(int x, int y, int width, int height) {
        g.clipRect(x, y, width, height);
    }


    /**
     *  Description of the Method
     *
     *@param  x           Description of Parameter
     *@param  y           Description of Parameter
     *@param  width       Description of Parameter
     *@param  height      Description of Parameter
     *@param  startAngle  Description of Parameter
     *@param  arcAngle    Description of Parameter
     */
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        g.drawArc(x, y, width, height, startAngle, arcAngle);
    }


    /**
     *  Description of the Method
     *
     *@param  character  Description of Parameter
     *@param  x          Description of Parameter
     *@param  y          Description of Parameter
     *@param  anchor     Description of Parameter
     */
    public void drawChar(char character, int x, int y, int anchor) { }


    /**
     *  Description of the Method
     *
     *@param  data    Description of Parameter
     *@param  offset  Description of Parameter
     *@param  length  Description of Parameter
     *@param  x       Description of Parameter
     *@param  y       Description of Parameter
     *@param  anchor  Description of Parameter
     */
    public void drawChars(char[] data, int offset, int length, int x, int y, int anchor) { }


    /**
     *  Description of the Method
     *
     *@param  img     Description of Parameter
     *@param  x       Description of Parameter
     *@param  y       Description of Parameter
     *@param  anchor  Description of Parameter
     */
    public void drawImage(Image img, int x, int y, int anchor) {
        int newx = x;

        if (anchor == 0) {
            anchor = javax.microedition.lcdui.Graphics.TOP | javax.microedition.lcdui.Graphics.LEFT;
        }

        if ((anchor & javax.microedition.lcdui.Graphics.RIGHT) != 0) {
            newx -= img.getWidth();
        } else if ((anchor & javax.microedition.lcdui.Graphics.HCENTER) != 0) {
            newx -= img.getWidth() / 2;
        }

        g.drawImage(((ImageImpl) img).getImage(), newx, y, null);
    }


    /**
     *  Description of the Method
     *
     *@param  x1  Description of Parameter
     *@param  y1  Description of Parameter
     *@param  x2  Description of Parameter
     *@param  y2  Description of Parameter
     */
    public void drawLine(int x1, int y1, int x2, int y2) {
        g.drawLine(x1, y1, x2, y2);
    }


    /**
     *  Description of the Method
     *
     *@param  x       Description of Parameter
     *@param  y       Description of Parameter
     *@param  width   Description of Parameter
     *@param  height  Description of Parameter
     */
    public void drawRect(int x, int y, int width, int height) {
        drawLine(x, y, x + width, y);
        drawLine(x + width, y, x + width, y + height);
        drawLine(x + width, y + height, x, y + height);
        drawLine(x, y + height, x, y);
    }


    /**
     *  Description of the Method
     *
     *@param  x          Description of Parameter
     *@param  y          Description of Parameter
     *@param  width      Description of Parameter
     *@param  height     Description of Parameter
     *@param  arcWidth   Description of Parameter
     *@param  arcHeight  Description of Parameter
     */
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }


    /**
     *  Description of the Method
     *
     *@param  str     Description of Parameter
     *@param  x       Description of Parameter
     *@param  y       Description of Parameter
     *@param  anchor  Description of Parameter
     */
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
    }


    /**
     *  Description of the Method
     *
     *@param  x           Description of Parameter
     *@param  y           Description of Parameter
     *@param  width       Description of Parameter
     *@param  height      Description of Parameter
     *@param  startAngle  Description of Parameter
     *@param  arcAngle    Description of Parameter
     */
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        g.fillArc(x, y, width, height, startAngle, arcAngle);
    }


    /**
     *  Description of the Method
     *
     *@param  x       Description of Parameter
     *@param  y       Description of Parameter
     *@param  width   Description of Parameter
     *@param  height  Description of Parameter
     */
    public void fillRect(int x, int y, int width, int height) {
        g.fillRect(x, y, width, height);
    }


    /**
     *  Description of the Method
     *
     *@param  x          Description of Parameter
     *@param  y          Description of Parameter
     *@param  width      Description of Parameter
     *@param  height     Description of Parameter
     *@param  arcWidth   Description of Parameter
     *@param  arcHeight  Description of Parameter
     */
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }


    /**
     *  Description of the Method
     *
     *@param  x  Description of Parameter
     *@param  y  Description of Parameter
     */
    public void translate(int x, int y) {
        super.translate(x, y);
        g.translate(x, y);
    }

}
