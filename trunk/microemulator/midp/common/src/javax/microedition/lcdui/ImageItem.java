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
 
package javax.microedition.lcdui;

import com.barteo.emulator.device.DeviceFactory;


public class ImageItem extends Item
{

	public static final int LAYOUT_DEFAULT = 0;
	public static final int LAYOUT_LEFT = 1;
	public static final int LAYOUT_RIGHT = 2;
	public static final int LAYOUT_CENTER = 3;
	public static final int LAYOUT_NEWLINE_BEFORE = 0x100;
	public static final int LAYOUT_NEWLINE_AFTER = 0x200;

	Image img;
	int layout;
  String altText;


	public ImageItem(String label, Image img, int layout, String altText)
	{
		super(label);
		
		if (img != null && img.isMutable()) {
			new IllegalArgumentException();
		}
		
		this.img = img;
		this.layout = layout;
    this.altText = altText;
	}


  public String getAltText()
  {
    return altText;
  }


  public Image getImage()
  {
    return img;
  }


  public int getLayout()
  {
    return layout;
  }


  public void setAltText(String text)
  {
    altText = text;
  }


  	public void setImage(Image img)
  	{
  		this.img = img;
  		repaint();
  	}


  public void setLabel(String label)
  {
    super.setLabel(label);
  }


  	public void setLayout(int layout)
  	{
 		this.layout = layout;
 		repaint();
 	}


	int getHeight()
	{
		if (img == null) {
			return super.getHeight();
		} else { 
			return super.getHeight() + img.getHeight();
		}
	}


  int paint(Graphics g)
  {
		super.paintContent(g);

		if (img != null) {
			g.translate(0, super.getHeight());
			if (layout == LAYOUT_DEFAULT || layout == LAYOUT_LEFT) {
				g.drawImage(img, 0, 0, Graphics.LEFT | Graphics.TOP);
			} else if (layout == LAYOUT_RIGHT) {
				g.drawImage(img, DeviceFactory.getDevice().getDeviceDisplay().getWidth(), 0, 
        	  Graphics.RIGHT | Graphics.TOP);
			} else if (layout == LAYOUT_CENTER) {
				g.drawImage(img, DeviceFactory.getDevice().getDeviceDisplay().getWidth() / 2, 0, 
        	  Graphics.HCENTER | Graphics.TOP);
			} else {
				g.drawImage(img, 0, 0, Graphics.LEFT | Graphics.TOP);
			}
			g.translate(0, -super.getHeight());
		}

		return getHeight();
	}


	int traverse(int gameKeyCode, int top, int bottom, boolean action)
	{
		Font f = Font.getDefaultFont();

		if (gameKeyCode == Canvas.UP) {
			if (top > 0) {
				if ((top % f.getHeight()) == 0) {
					return -f.getHeight();
				} else {
					return -(top % f.getHeight());
				}
			} else {
				return Item.OUTOFITEM;
			}
		}
		if (gameKeyCode == Canvas.DOWN) {
			if (bottom < getHeight()) {
				if (getHeight() - bottom < f.getHeight()) {
					return getHeight() - bottom;
				} else {
					return f.getHeight();
				}
			} else {
				return Item.OUTOFITEM;
			}
		}

		return 0;
	}

}
