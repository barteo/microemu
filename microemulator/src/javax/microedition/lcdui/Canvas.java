/*
 * @(#)Canvas.java  07/07/2001
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

import com.barteo.emulator.device.Device;


public abstract class Canvas extends Displayable
{

	public static final int UP = 1;
	public static final int DOWN = 6;
	public static final int LEFT = 2;
	public static final int RIGHT = 5;
	public static final int FIRE = 8;

	public static final int GAME_A = 9;
	public static final int GAME_B = 10;
	public static final int GAME_C = 11;
	public static final int GAME_D = 12;

  public static final int KEY_NUM0 = 48;
  public static final int KEY_NUM1 = 49;
  public static final int KEY_NUM2 = 50;
  public static final int KEY_NUM3 = 51;
  public static final int KEY_NUM4 = 52;
  public static final int KEY_NUM5 = 53;
  public static final int KEY_NUM6 = 54;
  public static final int KEY_NUM7 = 55;
  public static final int KEY_NUM8 = 56;
  public static final int KEY_NUM9 = 57;

  public static final int KEY_STAR = 42;
  public static final int KEY_POUND = 35;


	protected Canvas()
	{
	}


	public int getGameAction(int keyCode)
	{
		return Display.getGameAction(keyCode);
	}


  public int getKeyCode(int gameAction)
  {
    return Display.getKeyCode(gameAction);
  }


  public String getKeyName(int keyCode)
  {
    return Integer.toString(keyCode);
  }


  public boolean hasPointerEvents()
  {
    return false;
  }


  public boolean hasPointerMotionEvents()
  {
    return false;
  }


  public boolean hasRepeatEvents()
  {
    return false;
  }


	public int getWidth()
	{
		return Device.screenPaintable.width;
	}


	public int getHeight()
	{
		return  Device.screenPaintable.height;
	}


  protected void hideNotify()
  {
  }


  public boolean isDoubleBuffered()
  {
    return false;
  }


	protected void keyPressed(int keyCode)
	{
	}


	protected void keyRepeated(int keyCode)
	{
	}


	protected void keyReleased(int keyCode)
	{
	}


	protected abstract void paint(Graphics g);


  protected void pointerPressed(int x, int y)
  {
  }


  protected void pointerReleased(int x, int y)
  {
  }


  protected void pointerDragged(int x, int y)
  {
  }


	public final void repaint()
	{
		super.repaint();
	}


  public final void repaint(int x, int y, int width, int height)
  {
    repaint();
  }


  public final void serviceRepaints()
  {
  }

  
  protected void showNotify()
  {
  }
  
}
