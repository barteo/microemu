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


	private Image image;
	private int numGridX;
	private int numGridY;
	private int[] frameSequence;
	private int currentFrameIndex;
	private int currentTransform;
	
	
	public Sprite(Image image)
	{
		this(image, image.getWidth(), image.getHeight());
	}
	

	public Sprite(Image image, int frameWidth, int frameHeight)
	{
		if (frameWidth < 1 || frameHeight < 1) {
			throw new IllegalArgumentException();
		}
		if (image.getWidth() % frameWidth != 0 || image.getHeight() % frameHeight != 0) {
			throw new IllegalArgumentException();
		}
		
		this.image = image;
		this.width = frameWidth;
		this.height = frameHeight;
		
		numGridX = image.getWidth() / frameWidth;
		numGridY = image.getHeight() / frameHeight;
		setFrameSequence(null);
		setTransform(TRANS_NONE);
		
//		throw new RuntimeException("TODO");
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
		if (pixelLevel) {
			return false;
//			throw new RuntimeException("TODO");
		} else {
			throw new RuntimeException("TODO");
		}
	}
	

	public void defineCollisionRectangle(int x, int y, int width, int height)
	{
		throw new RuntimeException("TODO");
	}
	
	
	public void defineReferencePixel(int x, int y)		
	{
//		throw new RuntimeException("TODO");
	}


	public final int getFrame()
	{
		return currentFrameIndex;
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
		if (isVisible()) {
			int frameGridX = frameSequence[currentFrameIndex] % numGridX;
			int frameGridY = frameSequence[currentFrameIndex] / numGridX;
			g.drawRegion(image, 
					frameGridX * getWidth(), frameGridY * getHeight(), getWidth(), getHeight(), 
					currentTransform, getX(), getY(), Graphics.LEFT | Graphics.TOP);
		}
//		throw new RuntimeException("TODO");
	}


	public void prevFrame()
	{
		throw new RuntimeException("TODO");
	}


	public void setFrame(int sequenceIndex)
	{
		if (sequenceIndex < 0 || sequenceIndex >= frameSequence.length) {
			throw new ArrayIndexOutOfBoundsException();
		}
		
		currentFrameIndex = sequenceIndex;
	}


	public void setFrameSequence(int[] sequence)
	{
		if (sequence == null) {
			frameSequence = new int[numGridX * numGridY];
			for (int i = 0; i < frameSequence.length; i++) {
				frameSequence[i] = i;
			}
		} else {
			if (sequence.length < 1) {
				throw new IllegalArgumentException();
			}
			int numOfFrames = numGridX * numGridY;
			for (int i = 0; i < sequence.length; i++) {
				if (sequence[i] < 0 || sequence[i] >= numOfFrames) {
					throw new ArrayIndexOutOfBoundsException();
				}
			}
			
			frameSequence = new int[sequence.length];
			System.arraycopy(sequence, 0, frameSequence, 0, sequence.length);
		}
		currentFrameIndex = 0;
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
		if (transform < TRANS_NONE || transform > TRANS_MIRROR_ROT90) {
			throw new IllegalArgumentException();
		}
		
		if (currentTransform == TRANS_ROT90 || currentTransform == TRANS_ROT270 || 
				currentTransform == TRANS_MIRROR_ROT90 || currentTransform == TRANS_MIRROR_ROT270) {
			int tmp = width;
			width = height;
			height = tmp;
		}
		currentTransform = transform;
		if (transform == TRANS_ROT90 || transform == TRANS_ROT270 || 
				transform == TRANS_MIRROR_ROT90 || transform == TRANS_MIRROR_ROT270) {
			int tmp = width;
			width = height;
			height = tmp;
		}
		
//		throw new RuntimeException("TODO");
	}
	
}
