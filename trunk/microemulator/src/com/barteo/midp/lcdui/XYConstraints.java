/*
 * @(#)XYConstraints.java  07/07/2001
 *
 * Copyright (c) 2001 Bartek Teodorczyk <barteo@it.pl>. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package com.barteo.midp.lcdui;

import java.awt.Rectangle;


public class XYConstraints implements Cloneable
{
  int x;
  int y;
  int width;       // <= 0 means use the components's preferred size
  int height;      // <= 0 means use the components's preferred size

  public XYConstraints() {
    this(0, 0, 0, 0);
  }

  public XYConstraints(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }
  
  public XYConstraints(Rectangle rect) {
    this.x = rect.x;
    this.y = rect.y;
    this.width = rect.width;
    this.height = rect.height;
  }


  public int  getX() { return x; }
  public void setX(int x) { this.x = x; }
  public int  getY() { return y; }
  public void setY(int y) { this.y = y; }
  public int  getWidth() { return width; }
  public void setWidth(int width) { this.width = width; }
  public int  getHeight() { return height; }
  public void setHeight(int height) { this.height = height; }

  /**
   * Returns the hashcode for this XYConstraints.
   */
  public int hashCode() {
    return x ^ (y*37) ^ (width*43) ^ (height*47);
  }

  /**
   * Checks whether two XYConstraints are equal.
   */
  public boolean equals(Object that) {
    if (that instanceof XYConstraints) {
      XYConstraints other = (XYConstraints)that;
      return other.x == x && other.y == y && other.width == width && other.height == height;
    }
    return false;
  }

  public Object clone() {
    return new XYConstraints(x, y, width, height);
  }
  
  public String toString() {
    return "XYConstraints[" + x + "," + y + "," + width + "," + height + "]";  //NORES
  }
}
