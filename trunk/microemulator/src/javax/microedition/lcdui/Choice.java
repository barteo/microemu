/*
 * @(#)Choice.java  07/07/2001
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

package javax.microedition.lcdui;


public interface Choice {

  static final int EXCLUSIVE = 1;
  static final int MULTIPLE = 2;  
  static final int IMPLICIT = 3;
  

  public int append(String stringPart, Image imagePart);
    
  public void delete(int elementNum);
  
  public Image getImage(int elementNum);
    
  public int getSelectedFlags(boolean[] selectedArray_return);
  
  public int getSelectedIndex();
  
  public String getString(int elementNum);
    
  public void insert(int elementNum, String stringPart, Image imagePart);
  
  public boolean isSelected(int elementNum);
  
  public void set(int elementNum, String stringPart, Image imagePart);
  
  public void setSelectedFlags(boolean[] selectedArray);
  
  public void setSelectedIndex(int elementNum, boolean selected);
  
  public int size();
  
}

