/*
 *  Siemens API for MicroEmulator
 *  Copyright (C) 2003 Markus Heberling <markus@heberling.net>
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
package com.siemens.mp.color_game;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;

public class TiledLayer extends Layer {
    
    public TiledLayer(int columns, int rows, Image image, int tileWidth,
    int tileHeight) {
        // IllegalArgumentException will be thrown
        // in the Layer super-class constructor
        super(columns < 1 || tileWidth < 1 ? -1 : columns * tileWidth,
        rows < 1 || tileHeight < 1 ? -1 : rows * tileHeight);
        
        // if img is null img.getWidth() will throw NullPointerException
        if (((image.getWidth() % tileWidth) != 0) ||
        ((image.getHeight() % tileHeight) != 0)) {
            throw new IllegalArgumentException();
        }
        this.columns = columns;
        this.rows = rows;
        
        cellMatrix = new int[rows][columns];
        
        int noOfFrames =
        (image.getWidth() / tileWidth) * (image.getHeight() / tileHeight);
        // the zero th index is left empty for transparent tile
        // so it is passed in  createStaticSet as noOfFrames + 1
        // Also maintain static indices is true
        // all elements of cellMatrix[][]
        // are set to zero by new, so maintainIndices = true
        createStaticSet(image,  noOfFrames + 1, tileWidth, tileHeight, true);
    }
    
    
    public int createAnimatedTile(int staticTileIndex) {
        
        if (anim_to_static == null) {
            anim_to_static = new int[4];
            numOfAnimTiles = 1;
        } else if (numOfAnimTiles == anim_to_static.length) {
            // grow anim_to_static table if needed
            int new_anim_tbl[] = new int[anim_to_static.length * 2];
            System.arraycopy(anim_to_static, 0,
            new_anim_tbl, 0, anim_to_static.length);
            anim_to_static = new_anim_tbl;
        }
        anim_to_static[numOfAnimTiles] = staticTileIndex;
        numOfAnimTiles++;
        return (-(numOfAnimTiles - 1));
    }
    
    public void setAnimatedTile(int animatedTileIndex, int staticTileIndex) {
        animatedTileIndex = - animatedTileIndex;
        anim_to_static[animatedTileIndex] = staticTileIndex;
    }
    
    public int getAnimatedTile(int animatedTileIndex) {
        animatedTileIndex = - animatedTileIndex;
        return anim_to_static[animatedTileIndex];
    }
    
    public void setCell(int col, int row, int tileIndex) {
        cellMatrix[row][col] = tileIndex;
        
    }
    
    
    public int getCell(int col, int row) {
        return cellMatrix[row][col];
    }
    
    
    public void fillCells(int col, int row, int numCols, int numRows,
    int tileIndex) {
        for (int rowCount = row; rowCount < row + numRows; rowCount++) {
            for (int columnCount = col;
            columnCount < col + numCols; columnCount++) {
                cellMatrix[rowCount][columnCount] = tileIndex;
            }
        }
    }
    
    
    public final int getCellWidth() {
        return cellWidth;
    }
    
    public final int getCellHeight() {
        return cellHeight;
    }
    
    public final int getColumns() {
        return columns;
    }
    
    public final int getRows() {
        return rows;
    }
    
    public void setStaticTileSet(Image image, int tileWidth, int tileHeight) {
        // if img is null img.getWidth() will throw NullPointerException
        if (tileWidth < 1 || tileHeight < 1 ||
        ((image.getWidth() % tileWidth) != 0) ||
        ((image.getHeight() % tileHeight) != 0)) {
            throw new IllegalArgumentException();
        }
        width=(columns * tileWidth);
        height=(rows * tileHeight);
        
        int noOfFrames =
        (image.getWidth() / tileWidth) * (image.getHeight() / tileHeight);
        
        if (noOfFrames >= (numberOfTiles - 1)) {
            createStaticSet(image, noOfFrames + 1, tileWidth, tileHeight, true);
        } else {
            createStaticSet(image, noOfFrames + 1, tileWidth,
            tileHeight, false);
        }
    }
    
    public final void paint(Graphics g) {
        
        if (visible) {
            int tileIndex = 0;
            
            int clipX = g.getClipX();
            int clipY = g.getClipY();
            int clipW = g.getClipWidth();
            int clipH =  g.getClipHeight();
            
            
            
            // y-coordinate
            int ty = this.y;
            for (int row = 0;
            row < cellMatrix.length; row++, ty += cellHeight) {
                
                // reset the x-coordinate at the beginning of every row
                // x-coordinate to draw tile into
                int tx = this.x;
                int totalCols = cellMatrix[row].length;
                
                for (int column = 0; column < totalCols;
                column++, tx += cellWidth) {
                    
                    tileIndex = cellMatrix[row][column];
                    // check the indices
                    // if animated get the corresponding
                    // static index from anim_to_static table
                    if (tileIndex == 0) { // transparent tile
                        continue;
                    } else if (tileIndex < 0) {
                        tileIndex = getAnimatedTile(tileIndex);
                    }
                    if((tx+cellWidth>=clipX)&&(ty+cellHeight>=clipY)&&(tx<=clipX+clipW)&&(ty<=clipY+clipH)) {
                        g.setClip(tx, ty, this.cellWidth, this.cellHeight);
                        g.drawImage(sourceImage, tx-tileSetX[tileIndex], ty-tileSetY[tileIndex], Graphics.TOP | Graphics.LEFT);
                    }
                    
                }
            }
            g.setClip(clipX, clipY, clipW, clipH);
            
        }
    }
    
    // private implementations
    private void createStaticSet(Image image, int noOfFrames, int tileWidth,
    int tileHeight, boolean maintainIndices) {
        
        cellWidth = tileWidth;
        cellHeight = tileHeight;
        
        int imageW = image.getWidth();
        int imageH = image.getHeight();
        
        sourceImage = image;
        
        numberOfTiles = noOfFrames;
        tileSetX = new int[numberOfTiles];
        tileSetY = new int[numberOfTiles];
        
        if (!maintainIndices) {
            // populate cell matrix, all the indices are 0 to begin with
            for (rows = 0; rows < cellMatrix.length; rows++) {
                int totalCols = cellMatrix[rows].length;
                for (columns = 0; columns < totalCols; columns++) {
                    cellMatrix[rows][columns] = 0;
                }
            }
            // delete animated tiles
            anim_to_static = null;
        }
        
        int currentTile = 1;
        
        for (int y = 0; y < imageH; y += tileHeight) {
            for (int x = 0; x < imageW; x += tileWidth) {
                
                tileSetX[currentTile] = x;
                tileSetY[currentTile] = y;
                
                currentTile++;
            }
        }
    }
    
    int cellHeight; // = 0;
    int cellWidth; // = 0;
    int rows; // = 0;
    int columns; // = 0;
    int[][] cellMatrix; // = null;
    
    Image sourceImage; // = null;
    private int numberOfTiles; // = 0;
    int[] tileSetX;
    int[] tileSetY;
    
    private int[] anim_to_static; // = null;
    private int numOfAnimTiles; // = 0
}