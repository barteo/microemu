/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@it.pl>
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
 
package com.barteo.emulator.app.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.util.Hashtable;


public class XYLayout implements LayoutManager2
{
  private static final long serialVersionUID = 200L;

  int width;           // <= 0 means use the container's preferred size
  int height;          // <= 0 means use the container's preferred size


  public XYLayout() 
  {
  }


  public XYLayout(int width, int height) 
  {
    this.width  = width;
    this.height = height;
  }


  public int getWidth() 
  { 
  	return width; 
  }
  
  
  public void setWidth(int width) 
  { 
  	this.width = width; 
  }


  public int getHeight() 
  { 
  	return height; 
  }
  
  
  public void setHeight(int height) 
  { 
  	this.height = height; 
  }


  public String toString() 
  {
    return "XYLayout" + "[width=" + width + ",height=" + height + "]";
  }

  // LayoutManager interface

  public void addLayoutComponent(String name, Component component) 
  {
    //System.err.println("XYLayout.addLayoutComponent(" + name + "," + component + ")");
  }
  
  
  public void removeLayoutComponent(Component component) 
  {
    //System.err.println("XYLayout.removeLayoutComponent(" + component + ")");
    info.remove(component);
  }


  public Dimension preferredLayoutSize(Container target) 
  {
    return getLayoutSize(target, true);
  }


  public Dimension minimumLayoutSize(Container target) 
  {
    return getLayoutSize(target, false);
  }


  public void layoutContainer(Container target) 
  {
    Insets insets = target.getInsets();
    int count = target.getComponentCount();
    //System.err.println("XYLayout.layoutContainer(" + target + ") insets=" + insets + " count=" + count);
    for (int i = 0 ; i < count; i++) {
      Component component = target.getComponent(i);
      if (component.isVisible()) {
        Rectangle r = getComponentBounds(component, true);
        component.setBounds(insets.left + r.x, insets.top + r.y, r.width, r.height);
      }
    }
  }

  // LayoutManager2 interface

  public void addLayoutComponent(Component component, Object constraints) 
  {
    //System.err.println("XYLayout.addLayoutComponent(" + component + "," + constraints + ")");
    if (constraints instanceof XYConstraints)
      info.put(component, constraints);
  }

  
  public Dimension maximumLayoutSize(Container target) 
  {
    return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
  }


  public float getLayoutAlignmentX(Container target) 
  {
    return 0.5f;
  }


  public float getLayoutAlignmentY(Container target) 
  {
    return 0.5f;
  }


  public void invalidateLayout(Container target) 
  {
  }

  // internal

  Hashtable info = new Hashtable(); // leave this as non-transient
  static final XYConstraints defaultConstraints = new XYConstraints();

  Rectangle getComponentBounds(Component component, boolean doPreferred) 
  {
    XYConstraints constraints = (XYConstraints)info.get(component);
    //System.err.println("XYLayout.getComponentBounds(" + component + "," + doPreferred + ") constraints=" + constraints + " width=" + width + " height=" + height);
    if (constraints == null)
      constraints = defaultConstraints;
    Rectangle r = new Rectangle(constraints.x, constraints.y, constraints.width, constraints.height);
    if (r.width <= 0 || r.height <= 0) {
      Dimension d = doPreferred ? component.getPreferredSize() : component.getMinimumSize();
      if (r.width <= 0)
        r.width = d.width;
      if (r.height <= 0)
        r.height = d.height;
    }
    return r;
  }


  Dimension getLayoutSize(Container target, boolean doPreferred) 
  {
    Dimension dim = new Dimension(0, 0);

    //System.err.println("XYLayout.getLayoutSize(" + target + "," + doPreferred + ") width=" + width + " height=" + height);
    if (width <= 0 || height <= 0) {
      int count = target.getComponentCount();
      for (int i = 0; i < count; i++) {
        Component component = target.getComponent(i);
        if (component.isVisible()) {
          Rectangle r = getComponentBounds(component, doPreferred);
          //System.err.println(" bounds for " + component + " is " + r);
          dim.width  = Math.max(dim.width , r.x + r.width);
          dim.height = Math.max(dim.height, r.y + r.height);
        }
      }
    }
    if (width > 0)
      dim.width = width;
    if (height > 0)
      dim.height = height;
    Insets insets = target.getInsets();
    dim.width += insets.left + insets.right;
    dim.height += insets.top + insets.bottom;
    //System.err.println("  preferred container size is " + dim);
    return dim;
  }
}
