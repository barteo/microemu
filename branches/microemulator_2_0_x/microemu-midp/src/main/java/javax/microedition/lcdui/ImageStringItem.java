/*
 *  MicroEmulator
 *  Copyright (C) 2001 Bartek Teodorczyk <barteo@barteo.net>
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
 
package javax.microedition.lcdui;


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
			stringComponent.setWidthDecreaser(img.getWidth() + 2);
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
