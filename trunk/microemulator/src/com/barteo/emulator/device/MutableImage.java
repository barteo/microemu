/*
 * Created on 2003-07-08
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.barteo.emulator.device;

import javax.microedition.lcdui.Image;


public abstract class MutableImage extends Image 
{

	public abstract int getPixel(int x, int y);
	
	public abstract byte[] getData();

}
