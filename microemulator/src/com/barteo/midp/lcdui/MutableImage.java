/*
 *  @(#)MutableImage.java  07/07/2001
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

import java.awt.Frame;

import javax.microedition.lcdui.Graphics;

/**
 *  Description of the Class
 *
 *@author     barteo
 *@created    3 wrzesieñ 2001
 */
public class MutableImage extends ImageImpl {

    Frame f = new Frame();
    DisplayGraphics displayGraphics = null;


    /**
     *  Constructor for the MutableImage object
     *
     *@param  width   Description of Parameter
     *@param  height  Description of Parameter
     */
    public MutableImage(int width, int height) {
        f.addNotify();
        img = f.createImage(width, height);
    }


    /**
     *  Gets the graphics attribute of the MutableImage object
     *
     *@return    The graphics value
     */
    public javax.microedition.lcdui.Graphics getGraphics() {
        if (displayGraphics == null) {
            displayGraphics = new DisplayGraphics(img.getGraphics());
            displayGraphics.setGrayScale(255);
            displayGraphics.fillRect(0, 0, getWidth(), getHeight());
            displayGraphics.setGrayScale(0);
        }
        return displayGraphics;
    }


    /**
     *  Gets the mutable attribute of the MutableImage object
     *
     *@return    The mutable value
     */
    public boolean isMutable() {
        return true;
    }

}
