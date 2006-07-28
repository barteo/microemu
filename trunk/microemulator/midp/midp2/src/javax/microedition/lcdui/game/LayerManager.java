/*
 *  MicroEmulator
 *  Copyright (C) 2005 Andres Navarro
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

package javax.microedition.lcdui.game;

import java.util.Vector;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author Andres Navarro
 */
/*
 * This class is deceptively simply, most of the
 * methods are calls to an underlying Vector
 */
public class LayerManager {
    private Vector layers;
    private int viewX, viewY, viewW, viewH;
    
    public LayerManager() {
        layers = new Vector();
        viewX = viewY = 0;
        viewW = viewH = Integer.MAX_VALUE;
    }
    
    public void append(Layer layer) {
    	synchronized (this) {
	        if (layer == null)
	            throw new NullPointerException();
	        layers.add(layer);
    	}
    }
            
    public Layer getLayerAt(int i) {
        // needs not be synchronized
        return (Layer) layers.get(i);
    }
    
    public int getSize() {
        // needs not be synchronized
        return layers.size();
    }
    
    public void insert(Layer layer, int i) {
    	synchronized (this) {
	        if (layer == null)
	            throw new NullPointerException();
	        layers.insertElementAt(layer, i);
    	}
    }
    
    public void remove(Layer layer) {
    	synchronized (this) {
	        if (layer == null)
	            throw new NullPointerException();
	        layers.remove(layer);
    	}
    }
    
    public void setViewWindow(int x, int y, int width, int height) {
    	synchronized (this) {
	        if (width < 0 || height < 0)
	            throw new IllegalArgumentException();
	        viewX = x;
	        viewY = y;
	        viewW = width;
	        viewH = height;
    	}
    }
    
    public void paint(Graphics g, int x, int y) {
    	synchronized (this) {
	        if (g == null)
	            throw new NullPointerException();
	        
	        int clipX = g.getClipX();
	        int clipY = g.getClipY();
	        int clipW = g.getClipWidth();
	        int clipH = g.getClipHeight();
	        
	        // save the clip rect
	        int sClipX = clipX;
	        int sClipY = clipY;
	        int sClipW = clipW;
	        int sClipH = clipH;
	        
	        // if the entire viewWindow is out of the clip area
	        // there is nothing to draw!
	        if ((x + viewX < clipX && x + viewX + viewW < clipX) ||
	                (x + viewX >= clipX + clipW) ||
	                (y + viewY < clipY && y + viewY + viewH < clipY) ||
	                (y + viewY >= clipY + clipH))
	            return;
	
	        if (x + viewX > clipX) {
	            clipX = x + viewX;
	            clipW -= x + viewX - clipX;
	        }
	        if (x + viewX + viewW < clipX + clipW)
	            clipW -= clipX + clipW - (x + viewX + viewW);
	        
	        if (y + viewY > clipY) {
	            clipY = y + viewY;
	            clipH -= y + viewY - clipY;
	        }
	        if (y + viewY + viewH < clipY + clipH)
	            clipH -= clipY + clipH - (y + viewY + viewH);
	        
	        // set new calculated clip area and 
	        // translation
	        g.clipRect(clipX, clipY, clipW, clipH);
	        g.translate(x, y);        
	
	        // draw all the tiles, from background
	        // to foreground
	        for (int i = getSize() - 1; i >= 0; i--) {
	            Layer layer = getLayerAt(i);
	            try {
	                if (layer.isVisible())
	                    layer.paint(g);
	            } catch (Throwable t) {
	                // it does nothing, but at least prevents
	                // the method from aborting
	            }
	        }
	
	        // restore old clip rect and translation
	        g.clipRect(sClipX, sClipY, sClipW, sClipH);
	        g.translate(-x, -y);
    	}
    }
}
