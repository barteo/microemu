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


// TODO implement pointer events
public abstract class Screen extends Displayable
{
	
    Screen(String title)
    {
        super(title);
    }

    
    void scroll(int gameKeyCode) {
    	viewPortY += traverse(gameKeyCode, viewPortY, viewPortY + viewPortHeight);
    	repaint();
    }
    
	
	abstract int traverse(int gameKeyCode, int top, int bottom);

	
	void keyPressed(int keyCode) 
	{
		int gameKeyCode = Display.getGameAction(keyCode);

		if (gameKeyCode == Canvas.UP || gameKeyCode == Canvas.DOWN) {
			viewPortY += traverse(gameKeyCode, viewPortY, viewPortY + viewPortHeight);
			repaint();
		}
	}

	
	void hideNotify() 
	{
		super.hideNotify();
	}

	
	void keyRepeated(int keyCode) 
	{
		keyPressed(keyCode);
	}

	
	final void paint(Graphics g) 
	{
		int contentHeight = 0;
		int translatedY;

		if (viewPortY == 0) {
			currentDisplay.setScrollUp(false);
		} else {
			currentDisplay.setScrollUp(true);
		}

		g.setGrayScale(255);
		g.fillRect(0, 0, getWidth(), getHeight());

		g.setGrayScale(0);

        // TODO move to Displayable
		if (getTicker() != null) {
			contentHeight += getTicker().paintContent(g);
		}

		g.translate(0, contentHeight);
		translatedY = contentHeight;

        // TODO move to Displayable
        // TODO remove this StringComponent object when native UI is completed
        StringComponent title = new StringComponent(getTitle());
		contentHeight += title.paint(g);
		g.drawLine(0, title.getHeight(), getWidth(), title.getHeight());
		contentHeight += 1;

		g.translate(0, contentHeight - translatedY);
		translatedY = contentHeight;

		g.setClip(0, 0, getWidth(), getHeight() - contentHeight);
		g.translate(0, -viewPortY);
		contentHeight += paintContent(g);
		g.translate(0, viewPortY);

		if (contentHeight - viewPortY > getHeight()) {
			currentDisplay.setScrollDown(true);
		} else {
			currentDisplay.setScrollDown(false);
		}
		g.translate(0, -translatedY);
	}
	

	abstract int paintContent(Graphics g);

	
	void repaint() 
	{
		super.repaint();
	}

	
	void showNotify() 
	{
		viewPortY = 0;

		super.showNotify();
	}

}