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
	private int cells[][];
	private int columns;
	private int rows;
	private Image image;
	private int cellWidth;
	private int cellHeight;
	private int numGridX;
	private int numGridY;
	

	public TiledLayer(int columns, int rows, Image image, int tileWidth, int tileHeight)
	{		
		if (columns < 1 || rows < 1) {
			throw new IllegalArgumentException();
		}
		if (tileWidth < 1 || tileHeight < 1) {
			throw new IllegalArgumentException();
		}
		if (image.getWidth() % tileWidth != 0 || image.getHeight() % tileHeight != 0) {
			throw new IllegalArgumentException();
		}
		
		this.cells = new int[columns][rows];
		this.columns = columns;
		this.rows = rows;
		this.image = image;
		this.cellWidth = tileWidth;
		this.cellHeight = tileHeight;
		this.width = columns * tileWidth;
		this.height = rows * tileHeight;

		this.numGridX = image.getWidth() / tileWidth;
		this.numGridY = image.getHeight() / tileHeight;
	}


	public int createAnimatedTile(int staticTileIndex)
	{
		throw new RuntimeException("TODO");
	}


	public void fillCells(int col, int row, int numCols, int numRows, int tileIndex)
	{
		if (numCols < 0 || numRows < 0) {
			throw new IllegalArgumentException();
		}
		if (col + numCols > columns || row + numRows > rows) {
			throw new IndexOutOfBoundsException();
		}
		if (tileIndex < 0 || tileIndex > numGridX * numGridY) {
			throw new IndexOutOfBoundsException();
		}
		
		for (int c = col; c < col + numCols; c++) {
			for (int r = row; r < row + numRows; r++) {
				setCell(c, r, tileIndex);
			}
		}
	}


	public int getAnimatedTile(int animatedTileIndex)
	{
		throw new RuntimeException("TODO");
	}


	public int getCell(int col, int row)
	{
		return cells[col][row];
	}


	public final int getCellHeight()
	{
		return cellHeight;
	}


	public final int getCellWidth()
	{
		return cellWidth;
	}


	public final int getColumns()
	{
		return columns;
	}


	public final int getRows()
	{
		return rows;
	}


	public final void paint(Graphics g) 
	{
		for (int c = 0; c < columns; c++) {
			for (int r = 0; r < rows; r++) {
				if (cells[c][r] != 0) {
					int tileGridX = cells[c][r] % numGridX - 1;
					int tileGridY = cells[c][r] / numGridX;
					g.drawRegion(image, tileGridX * cellWidth, tileGridY * cellHeight, cellWidth, cellHeight, 
							Sprite.TRANS_NONE, getX() + c * cellWidth, getY() + r * cellHeight, Graphics.LEFT | Graphics.TOP);
				}
			}
		}
		
//		throw new RuntimeException("TODO");
	}


	public void setAnimatedTile(int animatedTileIndex, int staticTileIndex)
	{
		throw new RuntimeException("TODO");
	}


	public void setCell(int col, int row, int tileIndex)
	{
		if (tileIndex < 0 || tileIndex > numGridX * numGridY) {
			throw new IndexOutOfBoundsException();
		}
		
		cells[col][row] = tileIndex;
	}


	public void setStaticTileSet(Image image, int tileWidth, int tileHeight)
	{
		throw new RuntimeException("TODO");
	}

}
