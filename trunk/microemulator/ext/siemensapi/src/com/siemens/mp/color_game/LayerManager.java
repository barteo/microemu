/*
 * LayerManager.java
 *
 * Created on 2. Juli 2003, 12:45
 */

package com.siemens.mp.color_game;

import java.util.Vector;

/**
 *
 * @author  markus
 * @version
 */
public class LayerManager {
    Vector vec;
    
    public LayerManager()
    {
        vec=new Vector();
    }
    
    public void append(Layer l)
    {
        vec.addElement(l);
    }
    
    public Layer getLayerAt(int index)
    {
        return (Layer)vec.elementAt(index);
    }
    
    public int getSize()
    {
        return vec.size();
    }
    
    public void insert(Layer l, int index)
    {
        vec.insertElementAt(l,index);
    }
    
    public void paint(javax.microedition.lcdui.Graphics g, int x, int y)
    {
    }
    
    public void remove(Layer l)
    {
        vec.removeElement(l);
    }
    
    public void removeLayer(com.siemens.mp.misc.NativeMem NativeMemoryTable, Layer l)
    {
        remove(l);
    }
    
    public void setViewWindow(int x, int y, int width, int height)
    {
    }
}
