/*
 *  MicroEmulator
 *  Copyright (C) 2002 Bartek Teodorczyk <barteo@barteo.net>
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

package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;


public class GameCanvas extends Canvas 
{
	public static final int DOWN_PRESSED = 1 << Canvas.DOWN;
	public static final int LEFT_PRESSED = 1 << Canvas.LEFT;
	public static final int RIGHT_PRESSED = 1 << Canvas.RIGHT;
	public static final int UP_PRESSED = 1 << Canvas.UP;
	public static final int FIRE_PRESSED = 1 << Canvas.FIRE;
	public static final int GAME_A_PRESSED = 1 << Canvas.GAME_A;
	public static final int GAME_B_PRESSED = 1 << Canvas.GAME_B;
	public static final int GAME_C_PRESSED = 1 << Canvas.GAME_C;
	public static final int GAME_D_PRESSED = 1 << Canvas.GAME_D;
	

	protected GameCanvas(boolean suppressKeyEvents)
	{
		throw new RuntimeException("TODO");
	}


	public void flushGraphics()
	{
		throw new RuntimeException("TODO");
	}
	
	
	public void flushGraphics(int x, int y, int width, int height)
	{
		throw new RuntimeException("TODO");
	}
	
	
	protected Graphics getGraphics()
	{
		throw new RuntimeException("TODO");
	}
	
	
	public int getKeyStates()
	{
		throw new RuntimeException("TODO");
	}

	
	public void paint(Graphics g) 
	{
		throw new RuntimeException("TODO");
	}

}
