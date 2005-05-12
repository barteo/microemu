/*
 *  MicroEmulator
 *  Copyright (C) 2002 Bartek Teodorczyk <barteo@it.pl>
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
 
package com.barteo.emulator.device.j2se;

import java.awt.event.KeyEvent;

import javax.microedition.lcdui.Canvas;

import com.barteo.emulator.device.Device;


public class J2SEDevice extends Device
{

	public int getGameAction(int keyCode) 
  {
		switch (keyCode) {
			case KeyEvent.VK_UP:
				return Canvas.UP;
        
			case KeyEvent.VK_DOWN:
				return Canvas.DOWN;
        
			case KeyEvent.VK_LEFT:
				return Canvas.LEFT;
      	
			case KeyEvent.VK_RIGHT:
				return Canvas.RIGHT;
      	
			case KeyEvent.VK_ENTER:
				return Canvas.FIRE;
      	
			case KeyEvent.VK_1:
			case KeyEvent.VK_A:
				return Canvas.GAME_A;
        
			case KeyEvent.VK_3:
			case KeyEvent.VK_B:
				return Canvas.GAME_B;
        
			case KeyEvent.VK_7:
			case KeyEvent.VK_C:
				return Canvas.GAME_C;
        
			case KeyEvent.VK_9:
			case KeyEvent.VK_D:
				return Canvas.GAME_D;
        
			default:
				return 0;
		}
  }


  public int getKeyCode(int gameAction) 
  {
		switch (gameAction) {
			case Canvas.UP:
				return KeyEvent.VK_UP;
        
			case Canvas.DOWN:
				return KeyEvent.VK_DOWN;
        
			case Canvas.LEFT:
				return KeyEvent.VK_LEFT;
        
			case Canvas.RIGHT:
				return KeyEvent.VK_RIGHT;
        
			case Canvas.FIRE:
				return KeyEvent.VK_ENTER;
        
			case Canvas.GAME_A:
				return KeyEvent.VK_1;
        
			case Canvas.GAME_B:
				return KeyEvent.VK_3;
        
			case Canvas.GAME_C:
				return KeyEvent.VK_7;
        
			case Canvas.GAME_D:
				return KeyEvent.VK_9;

			default:
				throw new IllegalArgumentException();
		}
  }


  public boolean hasPointerMotionEvents()
  {
    return false;
  }
  
  
  public boolean hasPointerEvents()
  {
    return false;
  }
  
  
  public boolean hasRepeatEvents()
  {
    return false;
  }
    
}
