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


public class TiledLayer extends Layer 
{
	private int columns;
	private int rows;
	private Image image;
	private int tileWidth;
	private int tileHeight;
	

	public TiledLayer(int columns, int rows, Image image, int tileWidth, int tileHeight)
	{
		this.columns = columns;
		this.rows = rows;
		this.image = image;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		
//		throw new RuntimeException("TODO");
	}


	public int createAnimatedTile(int staticTileIndex)
	{
		throw new RuntimeException("TODO");
	}


	public void fillCells(int col, int row, int numCols, int numRows, int tileIndex)
	{
//		throw new RuntimeException("TODO");
	}


	public int getAnimatedTile(int animatedTileIndex)
	{
		throw new RuntimeException("TODO");
	}


	public int getCell(int col, int row)
	{
		throw new RuntimeException("TODO");
	}


	public final int getCellHeight()
	{
		throw new RuntimeException("TODO");
	}


	public final int getCellWidth()
	{
		throw new RuntimeException("TODO");
	}


	public final int getColumns()
	{
		throw new RuntimeException("TODO");
	}


	public final int getRows()
	{
		throw new RuntimeException("TODO");
	}


	public final void paint(Graphics g) 
	{
//		throw new RuntimeException("TODO");
	}


	public void setAnimatedTile(int animatedTileIndex, int staticTileIndex)
	{
		throw new RuntimeException("TODO");
	}


	public void setCell(int col, int row, int tileIndex)
	{
		throw new RuntimeException("TODO");
	}


	public void setStaticTileSet(Image image, int tileWidth, int tileHeight)
	{
		throw new RuntimeException("TODO");
	}

}
