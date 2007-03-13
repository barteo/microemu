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