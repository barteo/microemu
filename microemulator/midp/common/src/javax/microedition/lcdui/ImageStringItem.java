/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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


class ImageStringItem extends Item
{

	Image img;
  StringComponent stringComponent;


  public ImageStringItem(String label, Image img, String text)
  {
    super(label);
		stringComponent = new StringComponent(text);
    setImage(img);
  }


	public Image getImage()
	{
    return img;
  }
    
    
	public void setImage(Image img)
	{
    this.img = img;
		if (this.img != null) {
			stringComponent.setWidth(
          DeviceFactory.getDevice().getDeviceDisplay().getWidth() - img.getWidth() - 2);
		}
	}


	public String getText()
	{
		return stringComponent.getText();
	}


	public void setText(String text)
	{
		stringComponent.setText(text);
	}


	int getHeight()
	{
		if (img != null && img.getHeight() > stringComponent.getHeight()) {
			return img.getHeight();
		} else {
			return stringComponent.getHeight();
		}
	}


  void invertPaint(boolean state)
  {
    stringComponent.invertPaint(state);
  }


  int paint(Graphics g)
  {
		if (stringComponent == null) {
			return 0;
		}

		if (img != null) {
			g.drawImage(img, 0, 0, Graphics.LEFT | Graphics.TOP);
			g.translate(img.getWidth() + 2, 0);
		}

		int y = stringComponent.paint(g);

		if (img != null) {
			g.translate(-img.getWidth() - 2, 0);
		}

		return y;
  }

}
