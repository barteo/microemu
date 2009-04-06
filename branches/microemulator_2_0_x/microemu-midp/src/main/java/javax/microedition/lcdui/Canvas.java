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
 *
 *  Contributor(s):
 *    3GLab
 */
 
package javax.microedition.lcdui;

import javax.microedition.lcdui.game.GameCanvas;

import org.microemu.GameCanvasKeyAccess;
import org.microemu.MIDletBridge;
import org.microemu.device.DeviceFactory;


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
        super(null);
        super.setUI(DeviceFactory.getDevice().getUIFactory().createCanvasUI(this));
	}


	public int getGameAction(int keyCode)
	{
		return Display.getGameAction(keyCode);
	}


	public int getKeyCode(int gameAction)
    {
        return Display.getKeyCode(gameAction);
    }

    
    public String getKeyName(int keyCode) throws IllegalArgumentException 
    {
    	return Display.getKeyName(keyCode);
    }

    
    public boolean hasPointerEvents()
    {
        return device.hasPointerEvents();
    }

    
    public boolean hasPointerMotionEvents()
    {
        return device.hasPointerMotionEvents();
    }

    
    public boolean hasRepeatEvents()
    {
        return device.hasRepeatEvents();
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
        super.repaint(x, y, width, height);
    }

    
    public final void serviceRepaints()
    {
    	if (currentDisplay != null) {
    		currentDisplay.serviceRepaints();
    	}
    }
    
    
    public void setFullScreenMode(boolean mode) {
    	if (this.fullScreenMode != mode) {
    		this.fullScreenMode = mode;
    		
    		if (this instanceof GameCanvas) {
    			width = -1;
    			height = -1;
    		    GameCanvasKeyAccess access = MIDletBridge.getGameCanvasKeyAccess((GameCanvas) this);
    		    access.initBuffer();
    		}
    		
    		if (currentDisplay != null) {
    			sizeChanged(currentDisplay);
    		}
    	}
	}
    
    
    protected void sizeChanged(int w, int h)
    {
    }

    
    protected void showNotify()
    {
    }
  
}
