/*
 *   
 *
 * Copyright  1990-2007 Sun Microsystems, Inc. All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included at /legal/license.txt).
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa
 * Clara, CA 95054 or visit www.sun.com if you need additional
 * information or have any questions.
 */

package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;

/**
 * A TiledLayer is a visual element composed of a grid of cells that
 * can be filled with a set of
 * tile images.  This class allows large virtual layers to be created
 * without the need for an
 * extremely large Image.  This technique is commonly used in 2D
 * gaming platforms to create
 * very large scrolling backgrounds,
 * <P>
 * <h3>Tiles</h3>
 * The tiles used to fill the TiledLayer's cells are provided in a
 * single Image object which
 * may be mutable or immutable.  The Image is broken up into a series
 * of equally-sized tiles;
 * the tile size is specified along with the Image.  As shown in the
 * figure below, the same
 * tile set can be stored in several different arrangements depending
 * on what is the most
 * convenient for the game developer.  
 * <br>
 * <center><img src="doc-files/tiles.gif" width=588 height=412
 *   ALT="Tiles"></center>
 * <br>
 * Each tile is assigned a unique index number.  The tile located in
 * the upper-left corner
 * of the Image is assigned an index of 1.  The remaining tiles are
 * then numbered consecutively
 * in row-major order (indices are assigned across the first row, then
 * the second row, and so on).
 * These tiles are regarded as <em>static tiles</em> because there is
 * a fixed link between
 * the tile and the image data associated with it.
 * <P> 
 * A static tile set is created when the TiledLayer is instantiated;
 * it can also be updated
 * at any time using the {@link #setStaticTileSet} method.
 * <P>
 * In addition to the static tile set, the developer can also define
 * several <em>animated tiles</em>.
 * An animated tile is a virtual tile that is dynamically associated
 * with a static tile; the appearance
 * of an animated tile will be that of the static tile that it is
 * currently associated with.
 * <P>
 * Animated tiles allow the developer to change the appearance of a
 * group of cells
 * very easily.  With the group of cells all filled with the animated
 * tile, the appearance
 * of the entire group can be changed by simply changing the static
 * tile associated with the
 * animated tile.  This technique is very useful for animating large
 * repeating areas without
 * having to explicitly change the contents of numerous cells.
 * <P>
 * Animated tiles are created using the {@link #createAnimatedTile}
 * method, which returns the
 * index to be used for the new animated tile.  The animated tile
 * indices are always negative
 * and consecutive, beginning with -1.  Once created, the static tile
 * associated with an
 * animated tile can be changed using the {@link #setAnimatedTile}
 * method.
 * <P>
 * <h3>Cells</h3>
 * The TiledLayer's grid is made up of equally sized cells; the number
 * of rows and
 * columns in the grid are specified in the constructor, and the
 * physical size of the cells
 * is defined by the size of the tiles.  
 * <P>
 * The contents of each cell is specified by means of a tile index; a
 * positive tile index refers
 * to a static tile, and a negative tile index refers to an animated
 * tile.  A tile index of 0
 * indicates that the cell is empty; an empty cell is fully
 * transparent and nothing is drawn
 * in that area by the TiledLayer.  By default, all cells contain tile
 * index 0.
 * <P>
 * The contents of cells may be changed using {@link #setCell} and
 * {@link #fillCells}.  Several
 * cells may contain the same tile; however, a single cell cannot
 * contain more than one tile.
 * The following example illustrates how a simple background can be
 * created using a TiledLayer.
 * <br>
 * <center><img src="doc-files/grid.gif" width=735 height=193
 * ALT="TiledLayer Grid"></center>
 * <br>
 * In this example, the area of water is filled with an animated tile
 * having an index of -1, which
 * is initially associated with static tile 5.  The entire area of
 * water may be animated by simply
 * changing the associated static tile using <code>setAnimatedTile(-1,
 * 7)</code>.
 * <br>
 * <center><img src="doc-files/grid2.gif" width=735 height=193
 * ALT="TiledLayer Grid 2"></center>
 * <br>
 * <P>
 * <h3>Rendering a TiledLayer</h3>
 * A TiledLayer can be rendered by manually calling its paint method;
 * it can also be rendered
 * automatically using a LayerManager object.
 * <P>
 * The paint method will attempt to render the entire TiledLayer
 * subject to the
 * clip region of the Graphics object; the upper left corner of the
 * TiledLayer is rendered at
 * its current (x,y) position relative to the Graphics object's
 * origin.  The rendered region
 * may be controlled by setting the clip region of the Graphics object
 * accordingly.
 * <P>
 */
public class TiledLayer extends Layer {

    /**
     * Creates a new TiledLayer.  <p>
     *
     * The TiledLayer's grid will be <code>rows</code> cells high and
     * <code>columns</code> cells wide.  All cells in the grid are initially
     * empty (i.e. they contain tile index 0).  The contents of the grid may
     * be modified through the use of {@link #setCell} and {@link #fillCells}.
     * <P>
     * The static tile set for the TiledLayer is created from the specified
     * Image with each tile having the dimensions of tileWidth x tileHeight.
     * The width of the source image must be an integer multiple of
     * the tile width, and the height of the source image must be an integer
     * multiple of the tile height; otherwise, an IllegalArgumentException
     * is thrown;<p>
     *
     * The entire static tile set can be changed using 
     * {@link  #setStaticTileSet(Image, int, int)}.
     * These methods should be used sparingly since they are both
     * memory and time consuming.
     * Where possible, animated tiles should be used instead to
     * animate tile appearance.<p>
     *
     * @param columns the width of the <code>TiledLayer</code>,
     * expressed as a number of cells
     * @param rows the height of the <code>TiledLayer</code>,
     * expressed as a number of cells
     * @param image the <code>Image</code> to use for creating
     *  the static tile set
     * @param tileWidth the width in pixels of a single tile
     * @param tileHeight the height in pixels of a single tile
     * @throws NullPointerException if <code>image</code> is <code>null</code>
     * @throws IllegalArgumentException if the number of <code>rows</code>
     *  or <code>columns</code> is less than <code>1</code>
     * @throws IllegalArgumentException if <code>tileHeight</code>
     *  or <code>tileWidth</code> is less than <code>1</code>
     * @throws IllegalArgumentException if the <code>image</code>
     *  width is not an integer multiple of the <code>tileWidth</code>
     * @throws IllegalArgumentException if the <code>image</code>
     * height is not an integer multiple of the <code>tileHeight</code>
     */
    public TiledLayer(int columns, int rows, Image image, int tileWidth, int tileHeight) {
        super(0, 0);
        throw new RuntimeException("STUB");
    }

    /**
     * Creates a new animated tile and returns the index that refers to the new
     * animated tile. It is initially associated with the specified tile index
     * (either a static tile or 0).
     * <P>
     * The indices for animated tiles are always negative. The first animated
     * tile shall have the index -1, the second, -2, etc.
     * 
     * @param staticTileIndex
     *            the index of the associated tile (must be <code>0</code> or
     *            a valid static tile index)
     * @return the index of newly created animated tile
     * @throws IndexOutOfBoundsException
     *             if the <code>staticTileIndex</code> is invalid
     */
    public int createAnimatedTile(int staticTileIndex) {
        throw new RuntimeException("STUB");
    }

    /**
     * Associates an animated tile with the specified static tile.  <p>
     *
     * @param animatedTileIndex the index of the animated tile
     * @param staticTileIndex the index of the associated tile
     * (must be <code>0</code> or a valid static tile index)
     * @throws IndexOutOfBoundsException if the 
     * <code>staticTileIndex</code> is invalid
     * @throws IndexOutOfBoundsException if the animated tile index
     * is invalid
     * @see #getAnimatedTile
     *
     */
    public void setAnimatedTile(int animatedTileIndex, int staticTileIndex) {
        throw new RuntimeException("STUB");

    }

    /**
     * Gets the tile referenced by an animated tile.  <p>
     *
     * Returns the tile index currently associated with the
     * animated tile.
     *
     * @param animatedTileIndex the index of the animated tile
     * @return the index of the tile reference by the animated tile
     * @throws IndexOutOfBoundsException if the animated tile index
     * is invalid
     * @see #setAnimatedTile
     */
    public int getAnimatedTile(int animatedTileIndex) {
        throw new RuntimeException("STUB");
    }

    /**
     * Sets the contents of a cell.  <P>
     *
     * The contents may be set to a static tile index, an animated
     * tile index, or it may be left empty (index 0)
     * @param col the column of cell to set
     * @param row the row of cell to set
     * @param tileIndex the index of tile to place in cell
     * @throws IndexOutOfBoundsException if there is no tile with index
     *         <code>tileIndex</code>
     * @throws IndexOutOfBoundsException if <code>row</code> or
     *         <code>col</code> is outside the bounds of the 
     *         <code>TiledLayer</code> grid
     * @see #getCell
     * @see #fillCells
     */
    public void setCell(int col, int row, int tileIndex) {
        throw new RuntimeException("STUB");
 
    }

    /**
     * Gets the contents of a cell.  <p>
     *
     * Gets the index of the static or animated tile currently displayed in
     * a cell.  The returned index will be 0 if the cell is empty.
     *
     * @param col the column of cell to check
     * @param row the row of cell to check
     * @return the index of tile in cell
     * @throws IndexOutOfBoundsException if <code>row</code> or
     *         <code>col</code> is outside the bounds of the 
     *         <code>TiledLayer</code> grid
     * @see #setCell
     * @see #fillCells
     */
    public int getCell(int col, int row) {
        throw new RuntimeException("STUB");
    }

    /**
     * Fills a region cells with the specific tile.  The cells may be filled
     * with a static tile index, an animated tile index, or they may be left 
     * empty (index <code>0</code>). 
     *
     * @param col the column of top-left cell in the region
     * @param row the row of top-left cell in the region
     * @param numCols the number of columns in the region
     * @param numRows the number of rows in the region
     * @param tileIndex the Index of the tile to place in all cells in the 
     * specified region
     * @throws IndexOutOfBoundsException if the rectangular region
     *         defined by the parameters extends beyond the bounds of the
     *         <code>TiledLayer</code> grid
     * @throws IllegalArgumentException if <code>numCols</code> is less
     * than zero
     * @throws IllegalArgumentException if <code>numRows</code> is less
     * than zero
     * @throws IndexOutOfBoundsException if there is no tile with
     *         index <code>tileIndex</code>
     * @see #setCell
     * @see #getCell
     */
    public void fillCells(int col, int row, int numCols, int numRows,
                          int tileIndex) {
        throw new RuntimeException("STUB");
    }


    /**
     * Gets the width of a single cell, in pixels.
     * @return the width in pixels of a single cell in the 
     * <code>TiledLayer</code> grid
     */
    public final int getCellWidth() {
        throw new RuntimeException("STUB");
    }

    /**
     * Gets the height of a single cell, in pixels.
     * @return the height in pixels of a single cell in the 
     * <code>TiledLayer</code> grid
     */
    public final int getCellHeight() {
        throw new RuntimeException("STUB");
    }

    /**
     * Gets the number of columns in the TiledLayer grid. 
     * The overall width of the TiledLayer, in pixels, 
     * may be obtained by calling {@link #getWidth}.
     * @return the width in columns of the 
     * <code>TiledLayer</code> grid
     */
    public final int getColumns() {
        throw new RuntimeException("STUB");
    }

    /**
     * Gets the number of rows in the TiledLayer grid.  The overall
     * height of the TiledLayer, in pixels, may be obtained by
     * calling {@link #getHeight}.
     * @return the height in rows of the 
     * <code>TiledLayer</code> grid
     */
    public final int getRows() {
        throw new RuntimeException("STUB");
    }

    /**
     * Change the static tile set.  <p>
     *
     * Replaces the current static tile set with a new static tile set.
     * See the constructor {@link #TiledLayer(int, int, Image, int, int)}
     * for information on how the tiles are created from the
     * image.<p>
     *
     * If the new static tile set has as many or more tiles than the
     * previous static tile set,
     * the the animated tiles and cell contents will be preserve.  If
     * not, the contents of
     * the grid will be cleared (all cells will contain index 0) and
     * all animated tiles
     * will be deleted.
     * <P>
     * @param image the <code>Image</code> to use for creating the
     * static tile set
     * @param tileWidth the width in pixels of a single tile
     * @param tileHeight the height in pixels of a single tile
     * @throws NullPointerException if <code>image</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>tileHeight</code>
     *  or <code>tileWidth</code> is less than <code>1</code>
     * @throws IllegalArgumentException if the <code>image</code>
     *  width is not an integer  multiple of the <code>tileWidth</code>
     * @throws IllegalArgumentException if the <code>image</code>
     *  height is not an integer  multiple of the <code>tileHeight</code>
     */
    public void setStaticTileSet(Image image, int tileWidth, int tileHeight) {
        throw new RuntimeException("STUB");	
    }

    /**
     * Draws the TiledLayer.  
     *
     * The entire TiledLayer is rendered subject to the clip region of
     * the Graphics object.
     * The TiledLayer's upper left corner is rendered at the
     * TiledLayer's current
     * position relative to the origin of the Graphics object.   The current
     * position of the TiledLayer's upper-left corner can be retrieved by 
     * calling {@link #getX()} and {@link #getY()}.
     * The appropriate use of a clip region and/or translation allows
     * an arbitrary region
     * of the TiledLayer to be rendered.
     * <p>
     * If the TiledLayer's Image is mutable, the TiledLayer is rendered 
     * using the current contents of the Image.
     * @param g the graphics object to draw the <code>TiledLayer</code>
     * @throws NullPointerException if <code>g</code> is <code>null</code>
     */
    public final void paint(Graphics g) {
        throw new RuntimeException("STUB");
    }
}
