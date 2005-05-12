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
 
package com.barteo.emulator.device.swt;

import javax.microedition.lcdui.Canvas;

import org.eclipse.swt.SWT;

import com.barteo.emulator.device.Device;


public class SwtDevice extends Device
{
  
  public int getGameAction(int keyCode) 
  {
		// TODO poprawic KeyEvent	
		switch (keyCode) {
			case SWT.ARROW_UP:
				return Canvas.UP;
        
			case SWT.ARROW_DOWN:
				return Canvas.DOWN;
        
			case SWT.ARROW_LEFT:
				return Canvas.LEFT;
      	
			case SWT.ARROW_RIGHT:
				return Canvas.RIGHT;
      	
			case SWT.CR:
				return Canvas.FIRE;
      	
/*			case KeyEvent.VK_1:
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
				return Canvas.GAME_D;*/
        
			default:
				return 0;
		}
  }


  public int getKeyCode(int gameAction) 
  {
		// TODO poprawic KeyEvent	
		switch (gameAction) {
			case Canvas.UP:
				return SWT.ARROW_UP;
        
			case Canvas.DOWN:
				return SWT.ARROW_DOWN;
        
			case Canvas.LEFT:
				return SWT.ARROW_LEFT;
        
			case Canvas.RIGHT:
				return SWT.ARROW_RIGHT;
        
			case Canvas.FIRE:
				return SWT.CR;
        
/*			case Canvas.GAME_A:
				return KeyEvent.VK_1;
        
			case Canvas.GAME_B:
				return KeyEvent.VK_3;
        
			case Canvas.GAME_C:
				return KeyEvent.VK_7;
        
			case Canvas.GAME_D:
				return KeyEvent.VK_9;*/

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
