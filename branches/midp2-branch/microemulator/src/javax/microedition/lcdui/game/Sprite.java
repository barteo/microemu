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

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;


public class Sprite extends Layer 
{
	public static final int TRANS_MIRROR = 2;
	public static final int TRANS_MIRROR_ROT180 = 1;
	public static final int TRANS_MIRROR_ROT270 = 4;
	public static final int TRANS_MIRROR_ROT90 = 7;
	public static final int TRANS_NONE = 0;
	public static final int TRANS_ROT180 = 3;
	public static final int TRANS_ROT270 = 6;
	public static final int TRANS_ROT90 = 5;


	public Sprite(Image image)
	{
		throw new RuntimeException("TODO");
	}
	

	public Sprite(Image image, int frameWidth, int frameHeight)
	{
		throw new RuntimeException("TODO");
	}
	

	public Sprite(Sprite s)
	{
		throw new RuntimeException("TODO");
	}
	

	public final boolean collidesWith(Image image, int x, int y, boolean pixelLevel)
	{
		throw new RuntimeException("TODO");
	}
	
	
	public final boolean collidesWith(Sprite s, boolean pixelLevel)
	{
		throw new RuntimeException("TODO");
	}

					
	public final boolean collidesWith(TiledLayer t, boolean pixelLevel)
	{
		throw new RuntimeException("TODO");
	}
	

	public void defineCollisionRectangle(int x, int y, int width, int height)
	{
		throw new RuntimeException("TODO");
	}
	
	
	public void defineReferencePixel(int x, int y)		
	{
		throw new RuntimeException("TODO");
	}


	public final int getFrame()
	{
		throw new RuntimeException("TODO");
	}

	public int getFrameSequenceLength()
	{
		throw new RuntimeException("TODO");
	}


	public int getRawFrameCount()
	{
		throw new RuntimeException("TODO");
	}


	public int getRefPixelX()
	{
		throw new RuntimeException("TODO");
	}


	public int getRefPixelY()
	{
		throw new RuntimeException("TODO");
	}


	public void nextFrame()
	{
		throw new RuntimeException("TODO");
	}

	
	public final void paint(Graphics g) 
	{
		throw new RuntimeException("TODO");
	}


	public void prevFrame()
	{
		throw new RuntimeException("TODO");
	}


	public void setFrame(int sequenceIndex)
	{
		throw new RuntimeException("TODO");
	}


	public void setFrameSequence(int[] sequence)
	{
		throw new RuntimeException("TODO");
	}


	public void setImage(Image img, int frameWidth, int frameHeight)
	{
		throw new RuntimeException("TODO");
	}


	public void setRefPixelPosition(int x, int y)
	{
		throw new RuntimeException("TODO");
	}


	public void setTransform(int transform)
	{
		throw new RuntimeException("TODO");
	}
	
}
