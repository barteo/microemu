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
package com.siemens.mp.color_game;

import java.util.Vector;
import javax.microedition.lcdui.Graphics;

public class LayerManager {
    
    public LayerManager() {
        setViewWindow(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
        layers=new Vector();
    }
    
    public void append(Layer l) {
        layers.addElement(l);
    }
    
    public void insert(Layer l, int index) {
        layers.insertElementAt(l,index);
    }
    
    public Layer getLayerAt(int index) {
        return (Layer)layers.elementAt(index);
    }
    
    public int getSize() {
        return layers.size();
    }
    
    public void remove(Layer l) {
        layers.removeElement(l);
    }
    
    
    public void paint(Graphics g, int x, int y) {
        // save the original clip
        int clipX = g.getClipX();
        int clipY = g.getClipY();
        int clipW = g.getClipWidth();
        int clipH =  g.getClipHeight();
        
        
        // translate the LayerManager co-ordinates to Screen co-ordinates
        g.translate(x - viewX, y - viewY);
        // set the clip to view window
        g.setClip(viewX, viewY, viewWidth, viewHeight);
        // draw last to first
        
        for (int i = layers.size(); --i >= 0; ) {
            Layer comp = (Layer)layers.elementAt(i);
            if (comp.visible
            &&comp.x+comp.width>=viewX
            &&comp.x<=viewX+clipW
            &&comp.y+comp.height>=viewY
            &&comp.y<=viewY+clipH) {
                comp.paint(g);
            }
        }
        
        // restore Screen co-ordinates origin and clip
        
        g.translate(-x + viewX, -y + viewY);
        g.setClip(clipX, clipY, clipW, clipH);
    }
    
    
    public void setViewWindow(int x, int y, int width, int height) {
        
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException();
        }
        viewX = x;
        viewY = y;
        viewWidth = width;
        viewHeight = height;
    }
    
    private Vector layers;
    private int viewX, viewY, viewWidth, viewHeight; // = 0;
}
