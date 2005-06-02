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

import java.util.Vector;
import javax.microedition.lcdui.*;

/**
 *
 * @author  markus
 * @version
 */
public class GraphicObjectManager  extends com.siemens.mp.misc.NativeMem{
    Vector v=new Vector();
    
    public void addObject(GraphicObject gobject) {
        //System.out.println("void addObject(GraphicObject "+gobject+")");
        v.addElement(gobject);
    }
    
    public static byte[] createTextureBits(int width, int height, byte[] texture) {
        System.out.println("static byte[] createTextureBits(int width, int height, byte[] texture)");
        return null;
    }
    
    
    public void deleteObject(GraphicObject gobject) {
        //System.out.println("void deleteObject(GraphicObject gobject)");
        v.removeElement(gobject);
    }
    
    
    public void deleteObject(int position) {
        //System.out.println("void deleteObject(int position)");
        v.removeElementAt(position);
    }
    
    
    public GraphicObject getObjectAt(int index) {
        //System.out.println("GraphicObject getObjectAt(int index)");
        Object o=v.elementAt(index);
        return (GraphicObject)o;
    }
    
    
    public int getObjectPosition(GraphicObject gobject) {
        //System.out.println("int getObjectPosition(GraphicObject gobject)");
        return v.indexOf(gobject);
    }
    
    
    public void insertObject(GraphicObject gobject, int position) {
        //System.out.println("void insertObject(GraphicObject gobject, int position)");
        v.insertElementAt(gobject, position);
    }
    
    
    public void paint(ExtendedImage eimage, int x, int y) {
        this.paint(eimage.getImage(),x,y);
    }
    
    
    public void  paint(Image image, int x, int y) {
        Graphics g=image.getGraphics();
        g.translate(x, y);

        for (int i=0;i<v.size();i++) {
            GraphicObject go=(GraphicObject)v.elementAt(i);
            go.paint(g);
        }
        g.translate(-x, -y);
        
    }
    
}
