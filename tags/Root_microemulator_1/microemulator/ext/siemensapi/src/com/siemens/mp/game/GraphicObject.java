/*
 *  Siemens API for MicroEmulator
 *  Copyright (C) 2003 Markus Heberling <markus@heberling.net>
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
 */
package com.siemens.mp.game;

import javax.microedition.midlet.*;

/**
 *
 * @author  markus
 * @version
 */
public class GraphicObject extends com.siemens.mp.misc.NativeMem {
    /** Holds value of property visible. */
    private boolean visible;
    
    protected GraphicObject()
    {
        super();
    }
    
    /** Getter for property visible.
     * @return Value of property visible.
     *
     */
    public boolean getVisible() {
        return this.visible;
    }
    
    /** Setter for property visible.
     * @param visible New value of property visible.
     *
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    protected void paint(javax.microedition.lcdui.Graphics g)
    {
        
    }
}
