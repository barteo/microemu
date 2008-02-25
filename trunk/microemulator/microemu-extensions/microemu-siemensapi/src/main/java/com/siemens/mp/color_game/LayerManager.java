/*
 *  Siemens API for MicroEmulator
 *  Copyright (C) 2003 Markus Heberling <markus@heberling.net>
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
