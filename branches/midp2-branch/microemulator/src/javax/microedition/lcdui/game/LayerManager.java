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


public class LayerManager 
{
	private Layer layers[] = new Layer[4];
	private int numOfLayers = 0;
	private int x, y;
	private int width, height;
	

	public LayerManager()
	{
		setViewWindow(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
//		throw new RuntimeException("TODO");
	}


	public void append(Layer l)
	{
		remove(l);

		insert(l, numOfLayers);
	}


	public Layer getLayerAt(int index)		
	{
		throw new RuntimeException("TODO");
	}


	public int getSize()
	{
		throw new RuntimeException("TODO");
	}


	public void insert(Layer l, int index)
	{
		remove(l);

		if (index < 0 || index > numOfLayers) {
			throw new IndexOutOfBoundsException();
		}

		if (numOfLayers == layers.length) {
			Layer newLayers[] = new Layer[numOfLayers + 4];
			System.arraycopy(layers, 0, newLayers, 0, numOfLayers);
			layers = newLayers;
		}
		System.arraycopy(layers, index, layers, index + 1, numOfLayers - index);
		layers[index] = l;
		numOfLayers++;
	}


	public void paint(Graphics g, int x, int y)
	{
//		throw new RuntimeException("TODO");
		for (int i = numOfLayers - 1; i >= 0; i--) {
			layers[i].paint(g);
		} 
	}


	public void remove(Layer l)
	{
		for (int i = 0; i < numOfLayers; i++) {
			if (layers[i] == l) {
				System.arraycopy(layers, i + 1, layers, i, numOfLayers - i - 1);
				numOfLayers--;
				return;
			}
		}		
	}
	
	
	public void setViewWindow(int x, int y, int width, int height)	
	{
		if (width < 0 || height < 0) {
			throw new IllegalArgumentException();
		}
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
}
