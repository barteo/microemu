/*
 *  MicroEmulator
 *  Copyright (C) 2005 Andres Navarro
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

public abstract class CustomItem extends Item {
	protected static final int TRAVERSE_HORIZONTAL = 1;
	protected static final int TRAVERSE_VERTICAL = 2;
	protected static final int KEY_PRESS = 4;
	protected static final int KEY_RELEASE = 8;
	protected static final int KEY_REPEAT = 0x10;
	protected static final int POINTER_PRESS = 0x20;
	protected static final int POINTER_RELEASE = 0x40;
	protected static final int POINTER_DRAG = 0x80;
	protected static final int NONE = 0x00;

	protected CustomItem(String label) {
		super(label);
	}
	
	public int getGameAction(int keycode) {
		// TODO add support for keypress
		return 0;
	}
	
	protected final int getInteractionModes() {
		// TODO add support for aditional interaction modes
		return NONE;
	}
	
	protected abstract int getMinContentHeight();
	
	protected abstract int getMinContentWidth();
	
	protected abstract int getPrefContentHeight(int width);

	protected abstract int getPrefContentWidth(int height);

	protected void hideNotify() {
		// the default implementation of this method
		// does nothing
	}
	
	protected final void invalidate() {
		repaintOwner();
	}

	protected void keyPressed(int keyCode) {
		// the default implementation of this method
		// does nothing
	}
	
	protected void keyReleased(int keyCode) {
		// the default implementation of this method
		// does nothing
	}

	protected void keyRepeated(int keyCode) {
		// the default implementation of this method
		// does nothing
	}
	
	protected abstract void paint(Graphics g, int w, int h);

	protected void pointerDragged(int x, int y) {
		// the default implementation of this method
		// does nothing
	}

	protected void pointerPressed(int x, int y) {
		// the default implementation of this method
		// does nothing
	}
	
	protected void pointerReleased(int x, int y) {
		// the default implementation of this method
		// does nothing
	}
	
	protected final void repaint() {
		super.repaint();
	}

	protected final void repaint(int x, int y, int w, int h) {
		// TODO add support for partial repaint
		repaint();
	}
	
	protected void showNotify() {
		// the default implementation of this method
		// does nothing
	}

	protected void sizeChanged(int w, int h) {
		// the default implementation of this method
		// does nothing
	}
	
	protected boolean traverse(int dir, int viewportWidth,
            					int viewportHeight, int[] visRect_inout) {
		// the default implementation of this method
		// does nothing
		return false;
	}

	protected void traverseOut() {
		// the default implementation of this method
		// does nothing
	}

	
	// Item methods
	
	int paint(Graphics g) {
		// TODO paint content!!!
		super.paintContent(g);
		return super.getHeight();
	}
	
	// TODO write overrides for getMinimumWidth, etc
}
